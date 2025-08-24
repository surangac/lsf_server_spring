package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.responseMsg.SettlementBreakDownResponse;
import com.dfn.lsf.model.responseMsg.SettlementListResponse;
import com.dfn.lsf.model.responseMsg.SettlementSummaryResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_SETTLEMENT_INQUIRY_PROCESS;

/**
 * Defined in InMessageHandlerAdminCbr AND InMessageHandlerCbr
 * route : SETTLEMENT_INQUIRY_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_SETTLEMENT_INQUIRY_PROCESS = 13;
 */
@Service
@MessageType(MESSAGE_TYPE_SETTLEMENT_INQUIRY_PROCESS)
@RequiredArgsConstructor
public class SettlementInquiryProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SettlementInquiryProcessor.class);

    private final Gson gson;

    private final LSFRepository lsfRepository;

    private final LsfCoreService lsfCore;
    private final Helper helper;
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(final String request) {
        auditLogProcessor.process(request);
        String message = request;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(message, resultMap.getClass());
        String subMessageType = resultMap.get("subMessageType").toString();
        switch (subMessageType) {
            case LsfConstants.SETTLEMENT_SUMMARY_APPLICATION: { //settlement summary -client
                return gson.toJson(getSettlementSummaryForApplication(resultMap.get("id").toString(),
                                                                      resultMap.get("customerID").toString()));
            }
            case LsfConstants.SETTLEMENT_BREAKDOWN_APPLICATION: {// settlement breakdown-client
                return getSettlementBreakDownForApplication(resultMap.get("id").toString());
            }
            case LsfConstants.GET_SETTLEMENT_LIST: {//get settlement list-admin
                return getSettlementList(resultMap);
            }
            case LsfConstants.GET_LIST_FOR_MANUAL_SETTELEMENT: { // get the application list allowed for manual
                // settlement
                return getApplicationListForManualSettlement();
            }
            case LsfConstants.GET_SETTLEMENT_INSTALLMENT_LIST: {
                return getSettlementInstallmentList();
            }
            case LsfConstants.CONTRACT_ROLLOVER_PROCESS: {
                return createContractRollover(resultMap);
            }
        }
        return null;
    }

    public SettlementListResponse getSettlementSummaryForApplication(String applicationID, String userID) {
        SettlementListResponse settlementListResponse = new SettlementListResponse();
        logger.info("===========LSF : Settlement Summery Request Received , applicationID:" + applicationID);
        List<SettlementSummaryResponse> settlementSummaryResponseList = new ArrayList<>();
        List<PurchaseOrder> purchaseOrders = null;
        CashAcc cashAcc = null;
        OrderProfit orderProfit = null;

        purchaseOrders = lsfRepository.getAllPurchaseOrder(applicationID);
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
        int isInRollover = -1;
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            for (PurchaseOrder purchaseOrder : purchaseOrders) {
                if (purchaseOrder.getCustomerApproveStatus() == 1) {
                    SettlementSummaryResponse settlementSummary = new SettlementSummaryResponse();
                    settlementSummary.setApplicationID(applicationID);
                    settlementSummary.setCustomerID(userID);
                    settlementSummary.setOrderID(purchaseOrder.getId());
                    
                    settlementSummary.setSettlementDate(formatSettlementDate(String.valueOf(purchaseOrder.getSettlementDate())));
                    settlementSummary.setSettelmentStatus(purchaseOrder.getSettlementStatus());
                    settlementSummary.setLsfAccountDeletionState(murabahApplication.getLsfAccountDeletionState());
                    settlementSummary.setDiscountOnProfit(murabahApplication.getDiscountOnProfit());
                    settlementSummary.setProductType(murabahApplication.getProductType());
                    
                    
                    logger.info("===========LSF : (settlementSummaryApplication)-Product TYpe  : "
                                + murabahApplication.getProductType());

                    if (murabahApplication.getFinanceMethod().equals("1")) {

                        settlementSummary.setLoanAmount(purchaseOrder.getOrderCompletedValue());

                        cashAcc = lsfCore.getLsfTypeCashAccountForUser(userID, applicationID);
                        if (cashAcc != null) {
                            // remove pending settle from the available cash balance due to T+2 implementation
                            if (cashAcc.getNetReceivable() > 0) {
                                settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance()
                                                                            - cashAcc.getNetReceivable());
                            } else {
                                settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance());
                            }
                        }

                        if (murabahApplication.getProductType() != 3) {
                            orderProfit = lsfRepository.getSummationOfProfitUntilToday(applicationID, purchaseOrder.getId());
                            if (orderProfit != null) {
                                settlementSummary.setCumulativeProfit(orderProfit.getCumulativeProfitAmount());
                                settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                                // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount() +
                                // settlementSummary.getCumulativeProfit());
                            } else {
                                // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount());
                                settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount()); //change for DIB
                            }
                        } else {
                            OrderProfit profit = lsfCore.calculateConditionalProfit(collaterals,
                                                                                    purchaseOrder,
                                                                                    murabahApplication.getDiscountOnProfit());
                            settlementSummary.setOrderProfit(profit);
                            settlementSummary.setCumulativeProfit(profit.getChargeCommissionAmt());
                            logger.info("===========LSF : (settlementSummaryApplication)-Settlement Profit Target Comm : "
                                        + profit.getTargetCommission()
                                        + ", Traded Comm :"
                                        + profit.getTradedCommission());
                        }

                    } else {
                        settlementSummary.setLoanAmount(purchaseOrder.getOrderCompletedValue());

                        String appId = murabahApplication.isRollOverApp() ? murabahApplication.getRollOverAppId() : applicationID;
                        cashAcc = lsfCore.getLsfTypeCashAccountForUser(userID, appId);

                        if (cashAcc != null) {
                            if (cashAcc.getNetReceivable() > 0) {
                                settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance()
                                                                          - cashAcc.getNetReceivable());
                            } else {
                                settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance());
                            }
                        }
                     //   settlementSummary.setAvailableCashBalance(murabahApplication.getAvailableCashBalance());
                        settlementSummary.setCumulativeProfit(purchaseOrder.getProfitAmount());

                        if (murabahApplication.getProductType() != 3) {
                            orderProfit = lsfRepository.getSummationOfProfitUntilToday(applicationID, purchaseOrder.getId());
                            if (orderProfit != null) {
                                settlementSummary.setCumulativeProfit(orderProfit.getCumulativeProfitAmount());
                                settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                            } else {
                                settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                            }
                        } else {
                            OrderProfit profit = lsfCore.calculateConditionalProfit(collaterals,
                                                                                    purchaseOrder,
                                                                                    murabahApplication.getDiscountOnProfit());
                            settlementSummary.setOrderProfit(profit);
                            settlementSummary.setCumulativeProfit(profit.getChargeCommissionAmt());
                            settlementSummary.setLoanProfit(profit.getChargeCommissionAmt());
                        }

                        settlementSummary.setTotalSettlementAmount(purchaseOrder.getOrderCompletedValue() + settlementSummary.getCumulativeProfit());
                    }
                    lsfCore.calculateFTV(collaterals);

                    if (purchaseOrder.getCustomerApproveStatus() == 0) {
                        settlementSummary.setIsCustomerApproved(false);
                    } else if (purchaseOrder.getCustomerApproveStatus() == 1) {
                        settlementSummary.setIsCustomerApproved(true);
                    }
                    List<Installments> installmentsList =
                            lsfRepository.getPurchaseOrderInstallments(purchaseOrder.getId());
                    installmentsList.forEach(installments -> installments.setInstallmentDateString(settlementSummary.getSettlementDate()));
                    settlementSummary.setInstallmentsList(installmentsList);
                    if (murabahApplication.getFinanceMethod() != null && murabahApplication.getFinanceMethod()
                                                                                           .equalsIgnoreCase("2")) {
                        settlementSummary.setMaxRollOverPrd(GlobalParameters.getInstance().getMaxRolloverPeriod());
                        settlementSummary.setMinRollOverPrd(GlobalParameters.getInstance().getMinRolloverPeriod());
                        settlementSummary.setMinRollOverRatio(GlobalParameters.getInstance().getMinRolloverRatio());
                        if (isInRollover < 0) {
                            isInRollover = lsfCore.getIsInRollOverPeriod(purchaseOrder.getSettlementDate(),
                                                                         settlementSummary.getMinRollOverPrd(),
                                                                         settlementSummary.getMaxRollOverPrd());
                        }
                    }
                    settlementSummary.setIsInRollOverPrd(isInRollover);
                    if (murabahApplication.getRollOverAppId() == null || murabahApplication.getRollOverAppId()
                                                                                           .equalsIgnoreCase("null")) {
                        settlementSummary.setRollOverAppId("-1");
                    } else {
                        settlementSummary.setRollOverAppId(murabahApplication.getRollOverAppId());
                    }
                    settlementSummary.setFtv(collaterals.getFtv());
                    settlementSummaryResponseList.add(settlementSummary);
                }
            }
        }
        settlementListResponse.setSettlementSummaryResponseList(settlementSummaryResponseList);
        logger.info("===========LSF : (settlementSummaryApplication)-LSF-SERVER RESPONSE  : " + gson.toJson(
                settlementListResponse));
        return settlementListResponse;
    }

    public String getSettlementBreakDownForApplication(String applicationID) {
        SettlementBreakDownResponse settlementBreakDownResponse = new SettlementBreakDownResponse();
        PurchaseOrder purchaseOrder = null;
        List<PurchaseOrder> purchaseOrders = null;
        List<OrderProfit> orderProfitList = null;
        purchaseOrders = lsfRepository.getAllPurchaseOrder(applicationID);
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            purchaseOrder = purchaseOrders.get(0);
            orderProfitList = lsfRepository.getAllOrderProfitsForApplication(applicationID, purchaseOrder.getId());
        }
        settlementBreakDownResponse.setApplicationID(applicationID);
        settlementBreakDownResponse.setProfitList(orderProfitList);
        logger.info("===========LSF : (settlementBreakDownApplication)-LSF-SERVER RESPONSE  : " + gson.toJson(
                settlementBreakDownResponse));
        return gson.toJson(settlementBreakDownResponse);
    }

   /* public String performEarlySettlement(String applicationID, String userID, double settlementAmount) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;
        CashAcc lsfCashAccount = null;
        //buy pass for DIB
       *//* lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(userID);//get lsf type cash account details for user
        masterCashAccount = lsfCore.getMasterCashAccount();//getting master cash account
        if (lsfCore.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount,
        applicationID)) {
            responseMessage = "Successfully Transferred.";
            lsfCoreAbic.moveToCashTransferredClosedState(applicationID, "Early Settlement");//updating application

        } else {
            responseCode = 500;
            responseMessage = "Transfer Failed.";
        }*//*
        lsfCoreAbic.moveToCashTransferredClosedState(applicationID, "Early Settlement", o);
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);
        logger.info("===========LSF : (performEarlySettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }*/

    private String getSettlementList(Map<String, Object> resultMap) {

        int settlementStatus = -1;
        String fromDate = "01012017";
        String toDate = "01012050";
        if (resultMap.containsKey("settlementStatus")) {
            settlementStatus = Integer.valueOf(resultMap.get("settlementStatus").toString());
        }
        if (resultMap.containsKey("fromDate")) {
            fromDate = resultMap.get("fromDate").toString();
        }
        if (resultMap.containsKey("toDate")) {
            toDate = resultMap.get("toDate").toString();
        }

        List<SettlementListResponse> settlementSummaryResponseList = new ArrayList<>();
        List<SettlementSummaryResponse> settlementResponseList = lsfRepository.getSettlementListReport(settlementStatus,
                                                                                                       fromDate,
                                                                                                       toDate);
        SettlementListResponse settlementListResponse = new SettlementListResponse();
        settlementListResponse.setSettlementSummaryResponseList(settlementResponseList);
        settlementSummaryResponseList.add(settlementListResponse);
        return gson.toJson(settlementSummaryResponseList);
    }

    private String getSettlementInstallmentList() {
        List<MurabahApplication> murabahApplicationList = null;
        /*SettlementListResponse settlementListResponse = new SettlementListResponse();*/
        List<SettlementListResponse> settlementSummaryResponseList = new ArrayList<>();
        //   murabahApplicationList = lsfRepository.getOrderContractSingedApplications(); // instead of getting order
        //   contract signed app get all the apps change for DIB
        murabahApplicationList = lsfRepository.getAllMurabahApplications();
        if (murabahApplicationList != null && murabahApplicationList.size() > 0) {
            for (MurabahApplication murabahApplication : murabahApplicationList) {
                SettlementListResponse settlementSummaryResponse =
                        getSettlementSummaryForApplication(murabahApplication.getId(),
                                                                                                      murabahApplication.getCustomerId());
                if (settlementSummaryResponse != null
                    && settlementSummaryResponse.getSettlementSummaryResponseList().size() > 0) {
                    settlementSummaryResponseList.add(settlementSummaryResponse);
                }
            }
        }
        return gson.toJson(settlementSummaryResponseList);
    }

    private String formatSettlementDate(String settlementDate) {
        String formattedDate = "";
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sm = new SimpleDateFormat("dd/MM/yyyy");
        int difference = 0;
        try {
            Date settlement = df.parse(settlementDate);
            formattedDate = sm.format(settlement);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public String getApplicationListForManualSettlement() {
        SettlementListResponse settlementListResponse = new SettlementListResponse();
        logger.debug("===========LSF : (getListForManualSettlement)-REQUEST ");
        List<SettlementSummaryResponse> settlementSummaryResponseList = new ArrayList<>();
        List<PurchaseOrder> purchaseOrders = null;
        CashAcc cashAcc = null;
        OrderProfit orderProfit = null;

        purchaseOrders = lsfRepository.getApplicationForManualSettlement();
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            for (PurchaseOrder purchaseOrder : purchaseOrders) {
                SettlementSummaryResponse settlementSummary = new SettlementSummaryResponse();
                settlementSummary.setApplicationID(purchaseOrder.getApplicationId());
                settlementSummary.setCustomerName(purchaseOrder.getCustomerName());
                settlementSummary.setTradingAccNumber(purchaseOrder.getTradingAccNum());
                settlementSummary.setCustomerID(purchaseOrder.getCustomerId());
                // settlementSummary.setCustomerName();
                settlementSummary.setOrderID(purchaseOrder.getId());
                settlementSummary.setLoanAmount(purchaseOrder.getOrderCompletedValue());
                settlementSummary.setSettlementDate(formatSettlementDate(String.valueOf(purchaseOrder.getSettlementDate())));
                settlementSummary.setSettelmentStatus(purchaseOrder.getSettlementStatus());
                settlementSummary.setDisplayApplicationId(purchaseOrder.getDisplayApplicationId());
                cashAcc = lsfCore.getLsfTypeCashAccountForUser(purchaseOrder.getCustomerId(),
                                                               purchaseOrder.getApplicationId());
                if (cashAcc != null) {
                    settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance());
                    if (cashAcc.getNetReceivable() > 0) {
                        settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance()
                                                                  - cashAcc.getNetReceivable());
                    }
                }
                orderProfit = lsfRepository.getSummationOfProfitUntilToday(purchaseOrder.getApplicationId(),
                                                                           purchaseOrder.getId());
                if (orderProfit != null) {
                    settlementSummary.setCumulativeProfit(orderProfit.getCumulativeProfitAmount());
                    settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                } else {
                    settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount()); //change for DIB
                }
                settlementSummaryResponseList.add(settlementSummary);
            }
        }
        settlementListResponse.setSettlementSummaryResponseList(settlementSummaryResponseList);
        logger.info("===========LSF : (getListForManualSettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(
                settlementListResponse));
        return gson.toJson(settlementListResponse);
    }

    public String createContractRollover(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String appID = "";
        if (map.containsKey("appId")) {
            appID = map.get("appId").toString();
        }
        double rollOverFinanceRequiredAmt = Double.parseDouble(map.get("financeRequiredAmt").toString());
        double rollOverProfit = Double.parseDouble(map.get("profitAmount").toString());
        String rollOverTenure = map.get("tenor").toString();

        MurabahApplication oldApplication = lsfRepository.getMurabahApplication(appID);
        List<PurchaseOrder> purchaseOrders = lsfRepository.getPurchaseOrderForApplication(appID);
        if (oldApplication.getFinanceMethod().equalsIgnoreCase("2")
            || oldApplication.getAgreementList().get(0).getFinanceMethod() == 2) {
            Date crntDate;
            long remainDays = 0;
            Date settlementDate;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
            MApplicationCollaterals response = lsfCore.reValuationProcess(oldApplication, false);
            var po = purchaseOrders.getFirst();

            if(po.getIsPhysicalDelivery() == 1) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Rollover is not allowed for Physical Delivery");
                cmr.setErrorCode(LsfConstants.ERROR_NOT_IN_ROLLOVER_PERIOD);
                logger.info("===========LSF : Rollover is not allowed for Physical Delivery , Application ID :" + map.get(
                        "appId"));
                return gson.toJson(cmr);
            }

            try {
                crntDate = new Date();
                settlementDate = sdf2.parse(po.getSettlementDate());

                remainDays = TimeUnit.MILLISECONDS.toDays(settlementDate.getTime() - crntDate.getTime());
                logger.info("Remain days for Rollover : " + remainDays);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (remainDays > GlobalParameters.getInstance().getMaxRolloverPeriod()
                || remainDays < GlobalParameters.getInstance().getMinRolloverPeriod()) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Not in Roll Over Period");
                cmr.setErrorCode(LsfConstants.ERROR_NOT_IN_ROLLOVER_PERIOD);
                logger.info("===========LSF : Rollover period validation failed , Application ID :" + map.get("appId"));
                return gson.toJson(cmr);
            }

            if (GlobalParameters.getInstance().getMinRolloverRatio() > response.getFtv()) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Minimum Rollover Ratio Validation failed, FTV {} " + response.getFtv());
                cmr.setErrorCode(LsfConstants.ERROR_NOT_IN_ROLLOVER_RATIO);
                logger.info("===========LSF : Minimum Rollover Ratio Validation failed, Application ID :" + map.get(
                        "appId"));
                return gson.toJson(cmr);
            }
            java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            MurabahApplication newApplication = oldApplication;
            newApplication.setId("-1");
            // always make sure that RollOverAppId is original application ID
            if (oldApplication.isRollOverApp()) {
                newApplication.setRollOverAppId(oldApplication.getRollOverAppId());
            } else {
                newApplication.setRollOverAppId(oldApplication.getId());
            }
            Status status = new Status();
            status.setLevelId(1);
            status.setStatusId(OverrallApprovalStatus.PENDING.statusCode());
            status.setStatusDescription(OverrallApprovalStatus.PENDING.statusDescription());
            status.setStatusChangedDate(dateFormat.format(date));
            newApplication.setDate(dateFormat.format(date));
            newApplication.setProposalDate(dateFormat.format(date));
            newApplication.addNewStatus(status);
            newApplication.setOverallStatus(Integer.toString(OverrallApprovalStatus.PENDING.statusCode()));
            newApplication.setCurrentLevel(1);
            newApplication.setAdminFeeCharged(0.0);
            newApplication.setTenor(rollOverTenure);

            double newAmnt = rollOverFinanceRequiredAmt
                             + rollOverProfit;
            newApplication.setFinanceRequiredAmt(newAmnt);
            newApplication.setRollOverAppId(appID);
            newApplication.setRollOverSeqNumber(oldApplication.getRollOverSeqNumber() + 1);

            // set Original Application LSF type Accounts as rollover account main accounts.
            CashAcc lsfCashAccount = helper.getLsfTypeCashAccounts(oldApplication.getCustomerId(), appID).getFirst();
            TradingAccOmsResp lsfTradingAccount = helper.getLsfTypeTradingAccounts(oldApplication.getCustomerId(), appID, null).getFirst();
            newApplication.setCashAccount(lsfCashAccount.getAccountId());
            newApplication.setTradingAcc(lsfTradingAccount.getAccountId());
            newApplication.setDibAcc(lsfCashAccount.getAccountId());
            newApplication.setAvailableCashBalance(lsfTradingAccount.getAvailableCash());

            String id = lsfRepository.updateMurabahApplication(newApplication);
            logger.info("New application ID : " + id);
            String l32ID = lsfRepository.initialAgreementStatus(Integer.parseInt(id),
                                                                2,
                                                                oldApplication.getProductType(),
                                                                2);
            newApplication.setId(id);
            logger.info("===========LSF : New Murabah Application Created to rollover, Application ID :"
                        + id
                        + " , User ID:"
                        + newApplication.getCustomerId()
                        + " ,l32ID : "
                        + l32ID);
            String RspMsg = id + "|" + 1 + "|" + OverrallApprovalStatus.PENDING.statusCode();
            List<Symbol> symbols = lsfRepository.getInitialAppPortfolio(oldApplication.getId());
            List<Symbol> symbolList = new ArrayList<>();
            newApplication.setPflist(symbols);
            if (symbols != null) {
                if (symbols.size() > 0) {
                    for (int i = 0; i < symbols.size(); i++) {
                        Map<String, Object> params = (Map<String, Object>) symbols.get(i);
                        Symbol symbol = new Symbol();
                        symbol.setSymbolCode(params.get("symbolCode").toString());
                        symbol.setExchange(params.get("exchange").toString());
                        symbol.setPreviousClosed(Double.parseDouble(params.get("previousClosed").toString()));
                        symbol.setAvailableQty((int) Double.parseDouble(params.get("availableQty").toString()));
                        symbolList.add(symbol);
                    }
                    for (Symbol symbol : symbolList) {
                        lsfRepository.updateInitailAppPortfolio(symbol, id);
                    }
                }
            }
            cmr.setResponseCode(200);
            cmr.setResponseMessage(RspMsg);
            logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
            return gson.toJson(cmr);
        } else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Rollover is only allowed for Commodity finance Margin");
            cmr.setErrorCode(LsfConstants.ERROR_ABNORMAL_ACTIVITY);
            logger.info("===========LSF : Rollover is only allowed for Commodity finance Margin , Application ID :"
                        + map.get("id"));
            return gson.toJson(cmr);
        }
    }
}
