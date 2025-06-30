package com.dfn.lsf.util;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProfitCalculationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProfitCalculationUtils.class);

    private final NotificationManager notificationManager;

    private final LSFRepository lsfRepository;

    private final LsfCoreService lsfCore;

    public void runCalculationForTheCurrentDay(MurabahApplication murabahApplication, String masterCashAccount) {
        try {
            double lsfTypeCashAccountBalance = 0.0;
            double purchaseOrderValue = 0.0;
            double utilization = 0.0;
            double profit = 0.0;
            String orderID;
            String applicationID = murabahApplication.getId();
            String settlementDate = "";
            OrderProfit lastEntry = null;
            CashAcc lsfCashAccount = null;
            TradingAcc lsfTradingAccount = null;
            PurchaseOrder purchaseOrder = null;
            List<PurchaseOrder> orderList = lsfRepository.getAllPurchaseOrder(murabahApplication.getId());

            logger.debug("===========LSF : Received Order List for ApplicationID :"
                         + murabahApplication.getId()
                         + " , Order Count :"
                         + orderList.size()
                         + " , Order ID :"
                         + orderList.getFirst().getId());
            if (!orderList.isEmpty()) {
                OrderProfit newOrderProfit = null;
                purchaseOrder = orderList.getFirst();
                settlementDate = String.valueOf(purchaseOrder.getSettlementDate());

                lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(),
                                                                      murabahApplication.getId());

                if (murabahApplication.getProductType() != 3) {

                    purchaseOrderValue = getPoValue(purchaseOrder, murabahApplication.getFinanceMethod());
                    orderID = purchaseOrder.getId();
                    //lsfTypeCashAccountBalance = lsfCashAccount.getCashBalance();
                    // remove pending settle for T+2
                    lsfTypeCashAccountBalance = lsfCashAccount.getCashBalance();
                    utilization = purchaseOrderValue;
                    if (murabahApplication.getProductType() == 1) { //calculate utilization based on discounted (Discounted Profit)
                        utilization = purchaseOrderValue - lsfTypeCashAccountBalance;
                    }
                    if (utilization > 0 && (LSFUtils.getDaysToSettlement(settlementDate) >= 0)) { // calculate profit if the utilization is > 0 & number of days
                        // to settlement is > =0
                        //profit = LSFUtils.ceilTwoDecimals(ProfitCalculationUtils.calculateProfit(utilization,
                        // purchaseOrder.getProfitPercentage(), lsfCore)); // profit calculation
                        profit = ProfitCalculationUtils.calculateProfit(utilization,
                                                                        purchaseOrder.getProfitPercentage(),
                                                                        lsfCore);
                    }
                    logger.debug("===========LSF : Calculated Profit  for ApplicationID :"
                                 + applicationID
                                 + ", Purchase Order Value :"
                                 + purchaseOrderValue
                                 + " , Lsf Account Balance :"
                                 + lsfTypeCashAccountBalance
                                 + ", Profit Amount :"
                                 + profit);
                    lastEntry = ProfitCalculationUtils.getLastEntry(applicationID,
                                                                    orderID,
                                                                    lsfRepository); //retrieving last profit entry for
                    // application
                    newOrderProfit = new OrderProfit();
                    MApplicationCollaterals applicationCollaterals = lsfRepository.getApplicationCollateral(applicationID);
                    if (lastEntry == null) { // if first entry
                        newOrderProfit.setApplicationID(applicationID);
                        newOrderProfit.setOrderID(orderID);
                        newOrderProfit.setProfitAmount(profit);
                        newOrderProfit.setCumulativeProfitAmount(profit);
                        ProfitCalculationUtils.updateProfit(newOrderProfit, lsfTypeCashAccountBalance, lsfRepository);
                    } else {
                        newOrderProfit.setApplicationID(applicationID);
                        newOrderProfit.setOrderID(orderID);
                        newOrderProfit.setProfitAmount(profit);
                        /*--ALBILADSUP-389--*/
                        //If one profit entry is missed for a past date cumulative profit will be 0 to avoid this
                        // comProfit taken from outstanding amount
                        // newOrderProfit.setCumulativeProfitAmount(profit + lastEntry.getCumulativeProfitAmount());
                        // 2021-05-20 changed due to l23_cum profit amout few decimal places diff
                        double cumProfitTillLastDate = lastEntry.getCumulativeProfitAmount();
                        //applicationCollaterals.getOutstandingAmount() - purchaseOrderValue;
                        newOrderProfit.setCumulativeProfitAmount(cumProfitTillLastDate + profit);
                        logger.info("===========LSF :  Application :"
                                    + applicationID
                                    + " Profit Amt = "
                                    + profit
                                    + " , Cum Profit up to now :"
                                    + cumProfitTillLastDate
                                    + " new Cum Profit ="
                                    + newOrderProfit.getCumulativeProfitAmount());
                        ProfitCalculationUtils.updateProfit(newOrderProfit, lsfTypeCashAccountBalance, lsfRepository);
                    }
                    /*---Updating the Outstanding Amount---*/

                    logger.info("===========LSF :  Application :"
                                + applicationID
                                + " , Current Outstanding :"
                                + applicationCollaterals.getOutstandingAmount());
                    applicationCollaterals.setOutstandingAmount(purchaseOrderValue
                                                                + newOrderProfit.getCumulativeProfitAmount());
                    lsfRepository.addEditCollaterals(applicationCollaterals);
                    logger.info("===========LSF :  Application :"
                                + applicationID
                                + " , New Outstanding :"
                                + applicationCollaterals.getOutstandingAmount());

                    /*-----*/
                    /*--ALBILADSUP-389--*/
                    // Update Last Profit Cycle Date in the Application level
                    lsfRepository.updateLastProfitCycleDate(applicationID);
                } else {
                    MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
                    newOrderProfit = lsfCore.calculateConditionalProfit(collaterals,
                                                                        purchaseOrder,
                                                                        murabahApplication.getDiscountOnProfit());
                }

                lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(),
                                                                           murabahApplication.getId());
                decideSettlementAction(settlementDate,
                                       murabahApplication,
                                       purchaseOrder,
                                       newOrderProfit,
                                       lsfCashAccount,
                                       lsfTradingAccount,
                                       masterCashAccount);// analyze information and perform actions
            } else {
                logger.info("===========LSF : Order Found for Application :" + applicationID);
            }
        } catch (Exception ex) {
            logger.error("===========LSF :SettlementCalculation failed Customer:"
                         + murabahApplication.getCustomerId()
                         + " application id:"
                         + murabahApplication.getId()
                         + " error: "
                         + ex.getMessage());
        }
    }

    private double getPoValue(PurchaseOrder purchaseOrder, String financeType) {
        if (financeType.equals("1")) {
            return purchaseOrder.getOrderCompletedValue();
        } else if (financeType.equals("2")) {
            return purchaseOrder.getCommodityList().stream().mapToDouble(Commodity::getBoughtAmnt).sum();
        }
        return 0.0;
    }

    public static double calculateProfit(double utilization, double profitPercentage, LsfCoreService lsfCore) {
        double profit = 0.0;
        // since calculate dailyl rate date is send as 1
        profit = lsfCore.calculateProfitOnStructureSimple(utilization, 1, profitPercentage).getProfitAmount();
        return profit;
    }

    public static OrderProfit getLastEntry(String applicationID, String orderID, LSFRepository lsfDaoI) {
        List<OrderProfit> orderProfitList = lsfDaoI.getLastEntryForApplication(applicationID, orderID);
        if (orderProfitList != null && orderProfitList.size() > 0) {
            return orderProfitList.get(0);
        } else {
            return null;
        }
    }

    public static boolean updateProfit(OrderProfit orderProfit, double lsfCashBalance, LSFRepository lsfDaoI) {
        String response = lsfDaoI.updateProfit(orderProfit, lsfCashBalance);
        return response.equalsIgnoreCase("1");
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

                        TradingAcc lsfTradingAcc =
                                lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(),
                                                                                          murabahApplication.getId());
                        boolean cashTransferredToMasterAcc = lsfCore.cashTransferToMasterAccount(lsfTypeCashAccount.getAccountId(),
                                                                                                 masterCashAccount,
                                                                                                 amountToBeSettled,
                                                                                                 murabahApplication.getId());
                        if (cashTransferredToMasterAcc) { // if cash transfer is
                            // succeed.
                            logger.info("===========LSF : Cash Transfer Success ,From Account :"
                                        + lsfTypeCashAccount
                                        + " , To Account :"
                                        + masterCashAccount
                                        + " , Transfer Amount :"
                                        + purchaseOrder.getOrderSettlementAmount()
                                        + orderProfit.getCumulativeProfitAmount()
                                        + " ,ApplicationID : "
                                        + murabahApplication.getId());
                            logger.info("Updating PO " + purchaseOrder.getId() + " to settled state");
                            lsfRepository.updatePOToSettledState(Integer.parseInt(purchaseOrder.getId()));
                            // TODO: need to check whether Rollover applications are available for this application
                            // if not proceed with account deletion request
                            AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(
                                    murabahApplication.getId(),
                                    lsfTradingAcc.getAccountId(),
                                    murabahApplication.getTradingAcc(),
                                    lsfTypeCashAccount.getAccountId(),
                                    murabahApplication.getCashAccount());
                            if (accountDeletionRequestState.isSent()) {
                                logger.info("===========LSF :- Moving Application to close state, Application ID :"
                                            + murabahApplication.getId());
                                // lsfCore.moveToCashTransferredClosedState(murabahApplication.getId(), "Early
                                // Settlement", purchaseOrder.getId());//updating application
                            } else {
                                logger.info(
                                        "===========LSF :(performAutoSettlement)- Account Deletion Request Rejected "
                                        + "from OMS, Application ID :"
                                        + murabahApplication.getId()
                                        + ", Reason :"
                                        + accountDeletionRequestState.getFailureReason());
                            }
                            //     lsfDaoI.updateAccountDeletionState(murabahApplication.getId(), LsfConstants
                            //     .REQUEST_SENT_TO_OMS);

                        } else {
                            logger.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:"
                                         + lsfTypeCashAccount
                                         + " ,To Account :"
                                         + masterCashAccount
                                         + " , ApplicationID :"
                                         + murabahApplication.getId());
                        }
                    } else {
                        logger.error(
                                "===========LSF : Cash Transfer Failure(Master Account Didn't received from OMS) , "
                                + "Loan ID :"
                                + murabahApplication.getId());
                    }
                } else {/*---No need to auto liquidation---*/
                    logger.info("Application id "
                                + murabahApplication.getId()
                                + " has pending orders in the LSF Trading Account :"
                                + lsfTradinAccount.getAccountId()
                                + " LSF Type Cash Account "
                                + lsfTypeCashAccount
                                + " Can't perform settlement");
                    logger.info("===========LSF : Application Liquidation No Auto Liquidatation Application id "
                                + murabahApplication.getId());
                }
            } catch (Exception e) {
                logger.info("===========LSF :(decideSettlementAction) App Id"
                            + murabahApplication.getId()
                            + " Date Diff "
                            + dateDifference
                            + " error"
                            + e.getMessage());
            }
        }
    }
}
