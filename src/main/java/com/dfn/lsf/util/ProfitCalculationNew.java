package com.dfn.lsf.util;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfitCalculationNew {
    private final NotificationManager notificationManager;

    private final LSFRepository lsfRepository;

    private final LsfCoreService lsfCore;

    public void runCalculationForTheCurrentDay(MurabahApplication murabahApplication, String masterCashAccount) {
        try {
            String applicationID = murabahApplication.getId();
            String originalAppId = murabahApplication.isRollOverApp() ? murabahApplication.getRollOverAppId() : applicationID;

            List<PurchaseOrder> orderList = lsfRepository.getAllPurchaseOrder(murabahApplication.getId());

            log.debug("===========LSF : Received Order List for ApplicationID :"
                         + murabahApplication.getId()
                         + " , Order Count :"
                         + orderList.size());

            if (!orderList.isEmpty()) {
                PurchaseOrder purchaseOrder = orderList.getFirst();
                String orderID = purchaseOrder.getId();

                // Calculate and create profit entries for all missing dates
                calculateAndCreateMissingProfitEntries(murabahApplication, purchaseOrder, originalAppId, masterCashAccount);

            } else {
                log.info("===========LSF : No Orders Found for Application :" + applicationID);
            }
        } catch (Exception ex) {
            log.error("===========LSF :SettlementCalculation failed Customer:"
                         + murabahApplication.getCustomerId()
                         + " application id:"
                         + murabahApplication.getId()
                         + " error: "
                         + ex.getMessage());
        }
    }

    private void calculateAndCreateMissingProfitEntries(MurabahApplication murabahApplication,
                                                        PurchaseOrder purchaseOrder,
                                                        String originalAppId,
                                                        String masterCashAccount) {
        String applicationID = murabahApplication.getId();
        String orderID = purchaseOrder.getId();

        // Get the last profit entry from DB
        OrderProfit lastEntry = getLastEntryFromDB(applicationID, orderID);

        // Determine the start date for calculations
        LocalDate startDate = getCalculationStartDate(purchaseOrder, lastEntry);
        LocalDate currentDate = LocalDate.now();
        // Handle settlement date - convert string to LocalDate
        String settlementDateStr = purchaseOrder.getSettlementDate();
        LocalDate settlementDate;
        if (settlementDateStr == null || settlementDateStr.isEmpty()) {
            settlementDate = LocalDate.now().plusYears(10); // Default to far future if null/empty
        } else {
            try {
                settlementDate = LocalDate.parse(settlementDateStr);
            } catch (Exception e) {
                log.warn("Invalid settlement date format: {}, using default", settlementDateStr);
                settlementDate = LocalDate.now().plusYears(10);
            }
        }

        log.info("===========LSF : Processing profit calculation from " + startDate + " to " + currentDate
                    + " for ApplicationID: " + applicationID);

        // Get account information
        CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(), originalAppId);
        TradingAcc lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(), originalAppId);

        // Calculate profits for each missing date
        double cumulativeProfit = (lastEntry != null) ? lastEntry.getCumulativeProfitAmount() : 0.0;

        for (LocalDate date = startDate; !date.isAfter(currentDate); date = date.plusDays(1)) {
            // Skip profit calculation if date is after settlement date
            if (date.isAfter(settlementDate)) {
                continue;
            }
            if (!isProfitEntryExists(applicationID, orderID, date)) {
                OrderProfit dailyProfit = calculateDailyProfit(murabahApplication, purchaseOrder,
                                                               lsfCashAccount, date, cumulativeProfit);

                if (dailyProfit != null && dailyProfit.getOrderID() != null) {
                    cumulativeProfit += dailyProfit.getProfitAmount();
                    dailyProfit.setCumulativeProfitAmount(cumulativeProfit);

                    // Save the profit entry with specific date
                    saveProfitEntryWithDate(dailyProfit, date, lsfCashAccount.getCashBalance());

                    // Update outstanding amount
                    updateOutstandingAmount(applicationID, purchaseOrder, dailyProfit, murabahApplication.getFinanceMethod());

                    log.debug("===========LSF : Created profit entry for date: " + date
                                 + ", ApplicationID: " + applicationID
                                 + ", Daily Profit: " + dailyProfit.getProfitAmount()
                                 + ", Cumulative Profit: " + cumulativeProfit);
                }
            }
        }

        // Update Last Profit Cycle Date
        lsfRepository.updateLastProfitCycleDate(applicationID);
        OrderProfit todaysProfit = getProfitEntryForDate(applicationID, orderID, currentDate);

        decideSettlementAction(settlementDateStr, murabahApplication, purchaseOrder, todaysProfit,
                               lsfCashAccount, lsfTradingAccount, masterCashAccount);
    }

    private OrderProfit calculateDailyProfit(MurabahApplication murabahApplication,
                                             PurchaseOrder purchaseOrder,
                                             CashAcc lsfCashAccount,
                                             LocalDate calculationDate,
                                             double currentCumulativeProfit) {

        String applicationID = murabahApplication.getId();
        String orderID = purchaseOrder.getId();
        String settlementDate = String.valueOf(purchaseOrder.getSettlementDate());

        double purchaseOrderValue = getPoValue(purchaseOrder, murabahApplication.getFinanceMethod());
        double lsfTypeCashAccountBalance = lsfCashAccount.getCashBalance();
        double utilization = purchaseOrderValue;
        double profit = 0.0;

        // Calculate utilization based on product type
        if (murabahApplication.getProductType() == 1) { // Discounted Profit
            utilization = purchaseOrderValue - lsfTypeCashAccountBalance;
        }

        // Calculate profit if utilization > 0 and within settlement period
        if (utilization > 0 && LSFUtils.getDaysToSettlement(settlementDate) >= 0) {
            if (murabahApplication.getProductType() != 3) {
                profit = calculateProfit(utilization, purchaseOrder.getProfitPercentage(),lsfCore);
            } else {
                // Handle conditional profit calculation for product type 3
                MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
                OrderProfit conditionalProfit = lsfCore.calculateConditionalProfit(collaterals,
                                                                                   purchaseOrder,
                                                                                   murabahApplication.getDiscountOnProfit());
                return conditionalProfit;
            }
        }

        // Create OrderProfit object
        OrderProfit orderProfit = new OrderProfit();
        orderProfit.setApplicationID(applicationID);
        orderProfit.setOrderID(orderID);
        orderProfit.setProfitAmount(profit);
        // Note: Cumulative amount will be set by caller

        log.debug("===========LSF : Calculated Daily Profit for " + calculationDate
                     + ", ApplicationID: " + applicationID
                     + ", Purchase Order Value: " + purchaseOrderValue
                     + ", Lsf Account Balance: " + lsfTypeCashAccountBalance
                     + ", Profit Amount: " + profit);

        return orderProfit;
    }

    public double calculateProfit(double utilization, double profitPercentage, LsfCoreService lsfCore) {
        double profit = 0.0;
        // since calculate dailyl rate date is send as 1
        profit = lsfCore.calculateProfitOnStructureSimple(utilization, 1, profitPercentage).getProfitAmount();
        return profit;
    }

    private LocalDate getCalculationStartDate(PurchaseOrder purchaseOrder, OrderProfit lastEntry) {
        LocalDate acceptedDate = LocalDate.parse(
                purchaseOrder.getAcceptedDate().substring(0, 10),
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
        );

        if (lastEntry == null) {
            return acceptedDate;
        } else {
            // Get the date of the last entry and start from the next day
            LocalDate lastEntryDate = getDateFromOrderProfit(lastEntry);
            return lastEntryDate.plusDays(1);
        }
    }

    private OrderProfit getLastEntryFromDB(String applicationID, String orderID) {
        List<OrderProfit> orderProfitList = lsfRepository.getAllOrderProfitsForApplication(applicationID, orderID);

        if (orderProfitList != null && !orderProfitList.isEmpty()) {
            // Sort by date descending and get the most recent entry
            return orderProfitList.stream()
                                  .max(Comparator.comparing(this::getDateFromOrderProfit))
                                  .orElse(null);
        }
        return null;
    }

    private boolean isProfitEntryExists(String applicationID, String orderID, LocalDate date) {
        // Check if profit entry already exists for this specific date
        return lsfRepository.isProfitEntryExistsForDate(applicationID, orderID, date);
    }

    private OrderProfit getProfitEntryForDate(String applicationID, String orderID, LocalDate date) {
        return lsfRepository.getProfitEntryForDate(applicationID, orderID, date);
    }

    private void saveProfitEntryWithDate(OrderProfit orderProfit, LocalDate date, double lsfCashBalance) {
        // Set the creation date to the specific date we're calculating for
        orderProfit.setCreatedDate(date);
        lsfRepository.updateProfit_withDate(orderProfit, lsfCashBalance);
    }

    private void updateOutstandingAmount(String applicationID, PurchaseOrder purchaseOrder, OrderProfit orderProfit, String financeType) {
        try {
            MApplicationCollaterals applicationCollaterals = lsfRepository.getApplicationCollateral(applicationID);
            double purchaseOrderValue = getPoValue(purchaseOrder, financeType); // You may need to pass the finance method
            String approvedBy = "";
            int approvedById = 0;

            log.info("===========LSF : Application: " + applicationID
                        + ", Current Outstanding: " + applicationCollaterals.getOutstandingAmount());

            applicationCollaterals.setOutstandingAmount(purchaseOrderValue + orderProfit.getCumulativeProfitAmount());
            lsfRepository.addEditCollaterals(applicationCollaterals, approvedBy, approvedById);

            log.info("===========LSF : Application: " + applicationID
                        + ", New Outstanding: " + applicationCollaterals.getOutstandingAmount());
        } catch (Exception e) {
            log.error("===========LSF : Failed to update outstanding amount for ApplicationID: "
                         + applicationID + ", Error: " + e.getMessage());
        }
    }

    private LocalDate getDateFromOrderProfit(OrderProfit orderProfit) {
        // This method should extract the date from OrderProfit
        // You may need to adjust this based on how dates are stored in your OrderProfit class
        if (orderProfit.getCreatedDate() != null) {
            return orderProfit.getCreatedDate();
        }
        // Fallback to current date if no date is available
        return LocalDate.now();
    }

    // Utility method to calculate profit with specific date context
    private static int getDaysToSettlement(String settlementDate, LocalDate calculationDate) {
        try {
            LocalDate settlement = LocalDate.parse(settlementDate);
            return (int) ChronoUnit.DAYS.between(calculationDate, settlement);
        } catch (Exception e) {
            return -1;
        }
    }

    // Updated getLastEntry method - now simplified since we handle missing dates properly
    public static OrderProfit getLastEntry(String applicationID, String orderID, LSFRepository lsfRepository, String acceptedDate) {
        List<OrderProfit> orderProfitList = lsfRepository.getAllOrderProfitsForApplication(applicationID, orderID);

        if (orderProfitList != null && !orderProfitList.isEmpty()) {
            // Return the most recent entry (assuming the list is sorted by date)
            return orderProfitList.stream()
                                  .max(Comparator.comparing(profit -> profit.getCreatedDate()))
                                  .orElse(null);
        }
        return null;
    }

    private double getPoValue(PurchaseOrder purchaseOrder, String financeType) {
        if (financeType.equals("1")) {
            return purchaseOrder.getOrderCompletedValue();
        } else if (financeType.equals("2")) {
            return purchaseOrder.getCommodityList().stream().mapToDouble(Commodity::getBoughtAmnt).sum();
        }
        return 0.0;
    }

    public void decideSettlementAction(String settlementDate,
                                       MurabahApplication murabahApplication,
                                       PurchaseOrder purchaseOrder,
                                       OrderProfit orderProfit,
                                       CashAcc lsfTypeCashAccount,
                                       TradingAcc lsfTradinAccount,
                                       String masterCashAccount) {
        int dateDifference = 0;
        int notificationPeriod = GlobalParameters.getInstance().getNoOfDaysPriorRemindingThePayment();
        dateDifference = LSFUtils.getDaysToSettlement(settlementDate); // calculate remaining days to settlement from
        // today
        if (notificationPeriod == dateDifference) {//start settlement notification
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);
            try {
                notificationManager.sendSettlementNotification(murabahApplication,
                                                               purchaseOrder,
                                                               orderProfit,
                                                               dateDifference);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ((notificationPeriod > dateDifference) && (dateDifference> 0)) {//in between settlement notification start &
            // settlement date
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);
            try {
                notificationManager.sendSettlementNotification(murabahApplication,
                                                               purchaseOrder,
                                                               orderProfit,
                                                               dateDifference);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ((dateDifference <= 0) && murabahApplication.getAutomaticSettlementAllow() == 1) {// in the settlement date or after and automatic settlement is allowed
            double amountToBeSettled = 0;

            amountToBeSettled = getPoValue(purchaseOrder, murabahApplication.getFinanceMethod()) + orderProfit.getCumulativeProfitAmount();

         /*   if(murabahApplication.getProductType() != 3){
            }else{
                amountToBeSettled =
            }*/
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);// need to change
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);// need to change
            // String masterCashAccount = getMasterCashAccount();//getting master cash account

            try {
                notificationManager.sendSettlementNotification(murabahApplication,
                                                               purchaseOrder,
                                                               orderProfit,
                                                               dateDifference);
                if (lsfCore.checkPendingOrdersForLSFTradingAccount(lsfTradinAccount.getAccountId(),
                                                                   lsfTypeCashAccount.getAccountId()) == 0
                    && ((lsfTypeCashAccount.getCashBalance() - lsfTypeCashAccount.getNetReceivable())
                        >= amountToBeSettled)) {
                    if (masterCashAccount != null) { // if master cash account is recieved from OMS
                        String isTransferType = "1";
                        if (murabahApplication.getFinanceMethod().equals("2")) {
                            masterCashAccount = GlobalParameters.getInstance().getInstitutionInvestAccount();
                            isTransferType = "0";
                        }
                        boolean isCommodityApplication = murabahApplication.getFinanceMethod().equalsIgnoreCase("2");
                        LsfConstants.ProductType productType = LsfConstants.ProductType.SHARE;
                        if (isCommodityApplication) {
                            productType = LsfConstants.ProductType.COMMODITY;
                        }
                        if (murabahApplication.isRollOverApp()) {
                            productType = LsfConstants.ProductType.ROLLOVER;
                        }

                        boolean cashTransferredToMasterAcc = lsfCore.cashTransferToMasterAccount(lsfTypeCashAccount.getAccountId(),
                                                                                                 lsfTypeCashAccount.getAccountId(),
                                                                                                 masterCashAccount,
                                                                                                 amountToBeSettled,
                                                                                                 murabahApplication.getId(),
                                                                                                 isTransferType,
                                                                                                 productType);
                        if (cashTransferredToMasterAcc) { // if cash transfer is
                            // succeed.
                            log.info("===========LSF : Cash Transfer Success ,From Account :"
                                        + lsfTypeCashAccount.getAccountId()
                                        + " , To Account :"
                                        + masterCashAccount
                                        + " , Transfer Amount :"
                                        + purchaseOrder.getOrderSettlementAmount()
                                        + orderProfit.getCumulativeProfitAmount()
                                        + " ,ApplicationID : "
                                        + murabahApplication.getId()
                            + " , Trading Account :" + lsfTradinAccount.getAccountId());
                            // Update PO to settled state
                            log.info("Updating PO " + purchaseOrder.getId() + " to settled state");
                            lsfRepository.updatePOToSettledState(Integer.parseInt(purchaseOrder.getId()));
                            if (murabahApplication.isRollOverApp() || !hasRolloverApplication(murabahApplication)) {
                                String toCashAccount = null;
                                String toTradingAccount = null;
                                if (murabahApplication.isRollOverApp()) {
                                    MurabahApplication originalApplication = lsfRepository.getMurabahApplication(murabahApplication.getRollOverAppId());
                                    toCashAccount = originalApplication.getCashAccount();
                                    toTradingAccount = originalApplication.getTradingAcc();
                                } else {
                                    toCashAccount = murabahApplication.getCashAccount();
                                    toTradingAccount = murabahApplication.getTradingAcc();
                                }

                                log.info("===========LSF :- Deleting the Application, Application ID :"
                                         + murabahApplication.getId());
                                String originalApplicationID = murabahApplication.isRollOverApp() ? murabahApplication.getRollOverAppId() : murabahApplication.getId();
                                CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(), originalApplicationID);
                                TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(), originalApplicationID);
                                AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(murabahApplication.getId(), lsfTradingAcc.getAccountId(), toTradingAccount, lsfCashAccount.getAccountId(), toCashAccount);
//                                AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(
//                                        murabahApplication.getId(),
//                                        lsfTradinAccount.getAccountId(),
//                                        murabahApplication.getTradingAcc(),
//                                        lsfTypeCashAccount.getAccountId(),
//                                        murabahApplication.getCashAccount());
                                if (accountDeletionRequestState.isSent()) {
                                    log.info("===========LSF :- Moving Application to close state, Application ID :"
                                                + murabahApplication.getId());
                                    lsfCore.moveToCashTransferredClosedState(murabahApplication.getId(), "Early Settlement", purchaseOrder.getId());
                                } else {
                                    log.info(
                                            "===========LSF :(performAutoSettlement)- Account Deletion Request Rejected "
                                            + "from OMS, Application ID :"
                                            + murabahApplication.getId()
                                            + ", Reason :"
                                            + accountDeletionRequestState.getFailureReason());
                                }
                            } else {
                                log.info("===========LSF :- Moving Application to close state, Application ID :"
                                         + murabahApplication.getId());
                                lsfCore.moveToCashTransferredClosedState(murabahApplication.getId(), "Early Settlement", purchaseOrder.getId());
                            }
                        } else {
                            log.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:"
                                         + lsfTypeCashAccount
                                         + " ,To Account :"
                                         + masterCashAccount
                                         + " , ApplicationID :"
                                         + murabahApplication.getId());
                        }
                    } else {
                        log.error(
                                "===========LSF : Cash Transfer Failure(Master Account Didn't received from OMS) , "
                                + "Loan ID :"
                                + murabahApplication.getId());
                    }
                } else {/*---No need to auto liquidation---*/
                    log.info("Application id "
                                + murabahApplication.getId()
                                + " has pending orders in the LSF Trading Account :"
                                + lsfTradinAccount.getAccountId()
                                + " LSF Type Cash Account "
                                + lsfTypeCashAccount
                                + " Can't perform settlement");
                    log.info("===========LSF : Application Liquidation No Auto Liquidatation Application id "
                                + murabahApplication.getId());
                }
            } catch (Exception e) {
                log.info("===========LSF :(decideSettlementAction) App Id"
                            + murabahApplication.getId()
                            + " Date Diff "
                            + dateDifference
                            + " error"
                            + e.getMessage());
            }
        }
    }

    private boolean hasRolloverApplication(MurabahApplication application) {
        int rollOverCount = lsfRepository.hasRollOver(application.getId());
        return rollOverCount >0 ? true : false;
    }
}
