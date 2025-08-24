package com.dfn.lsf.service.scheduler;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.LiquidatePortfolioRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.ProfitCalculationNew;
import com.dfn.lsf.util.ProfitCalculationUtils;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class SettlementCalculationProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SettlementCalculationProcessor.class);

    private final Gson gson;

    private final LSFRepository lsfRepository;

    private final LsfCoreService lsfCore;

    private final Helper helper;

    //private final ProfitCalculationUtils profitCalculationUtils;
    private final ProfitCalculationNew profitCalculationUtils;

    @Override
    public String process(final String request) {
        String rawMessage = request;
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(rawMessage, map.getClass());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                if (subMessageType.equalsIgnoreCase("runSettlementCalculation")) {
                    runSettlementCalculation(); /* run profit calculation process*/
                } else {

                }
            }
            if (map.containsKey("correlationId")) {
                /*new implementation orderID should received from oMS */
                cashTransferAfterLiquidation(map.get("correlationId").toString()); /* cash transfer after liquidation
                 success response
                                                form OMS queue path*/
            }
        } catch (Exception ex) {
            logger.error("===========LSF : Error Occurred in Side SettlementCalculationProcessor.");
        }
        return gson.toJson(cmr);
    }
// schedule to run every day at 8:00 AM and 4:00 PM 
    @Scheduled(cron = "0 30 15 * * *")
    public void runSettlementCalculation() {
        String masterCashAccount = null;
        logger.info("===========LSF : Settlement Calculation Request Received  :");
        List<MurabahApplication> murabahApplicationList = lsfRepository.getOrderContractSingedApplications();
        // getting the
        // application eligible for profit calculation
        logger.debug("===========LSF : Profit Calculation Eligible Application Count  :"
                     + murabahApplicationList.size());

        masterCashAccount = lsfCore.getMasterCashAccount();
        if (!murabahApplicationList.isEmpty()) {
            /*--ALBILADSUP-389--*/
            lsfRepository.insertProfitCalculationMasterEntry(murabahApplicationList.size());
            for (MurabahApplication murabahApplication : murabahApplicationList) {
                profitCalculationUtils.runCalculationForTheCurrentDay(murabahApplication, masterCashAccount);
            }
        } else {
            logger.info("===========LSF : No matched Application Found.");
        }
    }

    private void cashTransferAfterLiquidation(String correlationId) {
        logger.info("===========LSF : Liquidation Success Response Received, Liquidation Reference  :"
                    + correlationId
                    + " , Performing Cash Transfer.");
        LiquidationLog liquidationLog = lsfRepository.getLiquidationLog(Integer.parseInt(correlationId)).get(0);
        if (lsfCore.cashTransferToMasterAccount(liquidationLog.getFromAccount(),
                                                liquidationLog.getFromAccount(),
                                                liquidationLog.getToAccount(),
                                                liquidationLog.getAmountToBeSettled(),
                                                liquidationLog.getApplicationID(),
                                                "1")) { // if cash transfer is succeed.
            logger.info("===========LSF : Cash Transfer Success ,From Account :"
                        + liquidationLog.getFromAccount()
                        + " , To Account :"
                        + liquidationLog.getToAccount()
                        + " , Transfer Amount :"
                        + liquidationLog.getAmountToBeSettled()
                        + " ,ApplicationID : "
                        + liquidationLog.getApplicationID()
                        + " , Liquidation Reference :"
                        + correlationId);
            lsfRepository.updateLiquidationLogState(Integer.parseInt(correlationId),
                                                    LsfConstants.LIQUIDATION_SUCCESS_RESPONSE_RECEIVED_FAILED_CASH_TRANSFER_SUCCESSFUL);
            lsfCore.moveToCashTransferredClosedState(liquidationLog.getApplicationID(),
                                                     "Cash Transferred Due to Unsettled",
                                                     liquidationLog.getOrderID());//updating application
            /*Close Account Close After Cash Transfer*/
            logger.error("===========LSF : Closing Application, ID : " + liquidationLog.getApplicationID());

            MurabahApplication application = lsfRepository.getMurabahApplication(liquidationLog.getApplicationID());
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(application.getId());
            CashAcc lsfCashAccount = null;
            List<CashAcc> lsfTypeCashAccLlist = collaterals.getLsfTypeCashAccounts();
            if (lsfTypeCashAccLlist.size() > 0) {
                lsfCashAccount = lsfTypeCashAccLlist.get(0);
            }
            TradingAcc lsfTradingAccount = null;
            List<TradingAcc> lsfTypeTradingList = collaterals.getLsfTypeTradingAccounts();
            if (lsfTypeTradingList.size() > 0) {
                lsfTradingAccount = lsfTypeTradingList.get(0);
            }
            //lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(application.getCustomerId());
            //lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(application.getCustomerId());
            lsfCore.closeLSFAccount(application.getId(),
                                    lsfTradingAccount.getAccountId(),
                                    application.getTradingAcc(),
                                    lsfCashAccount.getAccountId(),
                                    application.getCashAccount());
            /*-------------*/
        } else {
            logger.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:"
                         + liquidationLog.getFromAccount()
                         + " ,To Account :"
                         + liquidationLog.getToAccount()
                         + " , ApplicationID :"
                         + liquidationLog.getApplicationID()
                         + " , Liquidation Reference :"
                         + correlationId);
            lsfRepository.updateLiquidationLogState(Integer.parseInt(correlationId),
                                                    LsfConstants.LIQUIDATION_SUCCESS_RESPONSE_RECEIVED_FAILED_CASH_TRANSFER);
        }
    }


    /*private void decideSettlementAction(String settlementDate, MurabahApplication murabahApplication, PurchaseOrder
     purchaseOrder, OrderProfit orderProfit, CashAcc lsfTypeCashAccount, TradingAcc lsfTradinAccount, String 
     masterCashAccount) {
        int dateDifference = 0;
        int notificationPeriod = GlobalParameters.getInstance().getNoOfDaysPriorRemindingThePayment();
        dateDifference = LSFUtils.getDaysToSettlement(settlementDate); // calculate remaining days to settlement from
         today
        if (notificationPeriod == dateDifference) {//start settlement notification
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);
            try {
                NotificationManager.sendSettlementNotification(murabahApplication, purchaseOrder, orderProfit, 
                dateDifference);
            } catch (ComponentLookUpException e) {
                e.printStackTrace();
            }
        } else if ((notificationPeriod > dateDifference) && (dateDifference > 0)) {//in between settlement 
        notification start & settlement date
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);
            try {
                NotificationManager.sendSettlementNotification(murabahApplication, purchaseOrder, orderProfit, 
                dateDifference);
            } catch (ComponentLookUpException e) {
                e.printStackTrace();
            }
        } else if ((dateDifference <= 0) && murabahApplication.getAutomaticSettlementAllow()==1) {// in the 
        settlement date or after and automatic settlement is allowed
            double amountToBeSettled = purchaseOrder.getOrderCompletedValue() + orderProfit.getCumulativeProfitAmount();
            murabahApplication.setCurrentLevel(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_LEVEL);// need to change
            murabahApplication.setOverallStatus(LsfConstants.SETTLEMENT_NOTIFICATION_MARGIN_STATUS);// need to change
            // String masterCashAccount = getMasterCashAccount();//getting master cash account

            try {
                NotificationManager.sendSettlementNotification(murabahApplication, purchaseOrder, orderProfit, 
                dateDifference);
                if (lsfCore.checkPendingOrdersForLSFTradingAccount(lsfTradinAccount.getAccountId(), 
                lsfTypeCashAccount.getAccountId()) == 0
                        && ((lsfTypeCashAccount.getCashBalance()-lsfTypeCashAccount.getNetReceivable()) >= 
                        amountToBeSettled)
                        ) {
                    if (masterCashAccount != null) { // if master cash account is recieved from OMS

                        TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication
                        .getCustomerId(),murabahApplication.getId());
                        if (lsfCore.cashTransferToMasterAccount(lsfTypeCashAccount.getAccountId(), masterCashAccount,
                         amountToBeSettled, murabahApplication.getId())) { // if cash transfer is succeed.
                            logger.info("===========LSF : Cash Transfer Success ,From Account :" + lsfTypeCashAccount
                             + " , To Account :" + masterCashAccount + " , Transfer Amount :" + purchaseOrder
                             .getOrderSettlementAmount() + orderProfit.getCumulativeProfitAmount()
                                    + " ,ApplicationID : " + murabahApplication.getId());
                            logger.info("Updating PO " + purchaseOrder.getId() + " to settled state");
                            lsfRepository.updatePOToSettledState(Integer.parseInt(purchaseOrder.getId()));
                            AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount
                            (murabahApplication.getId(), lsfTradingAcc.getAccountId(), murabahApplication
                            .getTradingAcc(), lsfTypeCashAccount.getAccountId(), murabahApplication.getCashAccount());
                            if (accountDeletionRequestState.isSent()) {
                                logger.info("===========LSF :- Moving Application to close state, Application ID :" +
                                 murabahApplication.getId());
                                // lsfCore.moveToCashTransferredClosedState(murabahApplication.getId(), "Early 
                                Settlement", purchaseOrder.getId());//updating application
                            }else{
                                logger.info("===========LSF :(performAutoSettlement)- Account Deletion Request 
                                Rejected from OMS, Application ID :" + murabahApplication.getId() + ", Reason :" +  
                                accountDeletionRequestState.getFailureReason());

                            }
                            //     lsfRepository.updateAccountDeletionState(murabahApplication.getId(), LsfConstants
                            .REQUEST_SENT_TO_OMS);

                        } else {
                            logger.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:" + 
                            lsfTypeCashAccount + " ,To Account :" + masterCashAccount + " , ApplicationID :" + 
                            murabahApplication.getId());
                        }
                    } else {
                        logger.error("===========LSF : Cash Transfer Failure(Master Account Didn't received from OMS)
                         , Loan ID :" + murabahApplication.getId());
                    }

                } else{*//*---No need to auto liquidation---*//*
                    logger.info("Application id " + murabahApplication.getId() +" has pending orders in the LSF 
                    Trading Account :" + lsfTradinAccount.getAccountId() +" LSF Type Cash Account " 
                    +lsfTypeCashAccount +" Can't perform settlement");
                    logger.info("===========LSF : Application Liquidation No Auto Liquidatation Application id 
                    "+murabahApplication.getId());
                }
            } catch (ComponentLookUpException e) {
               logger.info("===========LSF :(decideSettlementAction) App Id"+ murabahApplication.getId()+ " Date Diff
                "+ dateDifference +" error" +  e.getMessage());
            }
        }
    }
*/

   /* private CashAcc getLsfTypeCashAccountForUser(String userID) {
        CashAcc cashAcc = new CashAcc();
        Map<String, Object> resultMap = new HashMap<>();
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        inqueryMessage.setCustomerId(userID);
        inqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
        Object result = helper.sendSettlementRelatedOMSRequest(gson.toJson(inqueryMessage), LsfConstants
        .HTTP_PRODUCER_OMS_GET_LSF_CASH_ACCOUNT_USERID);
        if(result == null){
            return null;
        }else{
            resultMap = gson.fromJson((String) result, resultMap.getClass());
            ArrayList<Map<String, Object>> cashAccList = (ArrayList<Map<String, Object>>) resultMap.get
            ("responseObject");
            cashAcc.setCashBalance(Double.parseDouble(cashAccList.get(0).get("balance").toString()));
            cashAcc.setAccountId(cashAccList.get(0).get("accountNo").toString());
            return cashAcc;
        }
    }*/

 /*   private double calculateProfit(double utilization,double profitPercentage) {
        double profit = 0.0;
        // since calculate dailyl rate date is send as 1
        profit = lsfCore.calculateProfitOnStructureSimple(utilization, 1,profitPercentage).getProfitAmount();
        return profit;
    }

    private OrderProfit getLastEntry(String applicationID, String orderID) {
        List<OrderProfit> orderProfitList = lsfRepository.getLastEntryForApplication(applicationID, orderID);
        if (orderProfitList != null && orderProfitList.size() > 0) {
            return orderProfitList.get(0);
        } else {
            return null;
        }
    }

    private boolean updateProfit(OrderProfit orderProfit, double lsfCashBalance) {
        String response = lsfRepository.updateProfit(orderProfit, lsfCashBalance);
        return response.equalsIgnoreCase("1");
    }*/

   /* public String getMasterCashAccount(){
        String cashAccount = null;
        String institutionTradingAccount =  null;
        String exchange = null;
        institutionTradingAccount = GlobalParameters.getInstance().getInstitutionTradingAcc();
        exchange = GlobalParameters.getInstance().getDefaultExchange();
        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
        if(exchange != null && institutionTradingAccount != null){
            commonInqueryMessage.setReqType(LsfConstants.GET_ACCOUNT_INFO_BY_TRADING_ACCOUNT);
            commonInqueryMessage.setTradingAccountId(institutionTradingAccount);
            commonInqueryMessage.setExchange(exchange);
            String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage), 
            LsfConstants.HTTP_PRODUCER_OMS_GET_MASTER_CASH_ACCOUNT);
            if(result != null){
                Map<String, Object> resMap = new HashMap<>();
                resMap = gson.fromJson(result, resMap.getClass());
                String responseString = resMap.get("responseObject").toString();
                Map<String, Object> finalMap = gson.fromJson(responseString, resMap.getClass());
                cashAccount = finalMap.get("relCashAccNo").toString();
            }
        }
        return cashAccount;
    }*/

//    private TradingAcc getLSFTypeTradingAccount(String customerID) {
//        TradingAcc tradingAcc = new TradingAcc();
//        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
//        commonInqueryMessage.setCustomerId(customerID);
//        commonInqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
//        String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage), 
//        LsfConstants.HTTP_PRODUCER_OMS_REQ_GET_LSF_TYPE_TRADING_ACCOUNT);
//        Map<String, Object> resMap = new HashMap<>();
//        resMap = gson.fromJson(result, resMap.getClass());
//        ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resMap.get("responseObject");
//        Map<String, Object> lsfTrdAccnt = (Map<String, Object>) lsfTrd.get(0).get("tradingAccount");
//        tradingAcc.setExchange(lsfTrdAccnt.get("exchange").toString());
//        tradingAcc.setAccountId(lsfTrdAccnt.get("accountId").toString());
//        return tradingAcc;
//    }

    private boolean liquidatePortfolio(String customerID,
                                       String applicationID,
                                       String masterCashAccount,
                                       String customerCashAccount,
                                       double amountToBeSettled,
                                       String poID) {
        boolean isLiquidate = false;
        String liquidationReference = String.valueOf(System.currentTimeMillis()).substring(5, 13);
        TradingAcc tradingAccInfo = lsfCore.getLsfTypeTradinAccountForUser(customerID, applicationID);
        LiquidatePortfolioRequest liquidatePortfolioRequest = new LiquidatePortfolioRequest();
        liquidatePortfolioRequest.setReqType(LsfConstants.LIQUIDATE_PORTFOLIO);
        liquidatePortfolioRequest.setTradingAccountId(tradingAccInfo.getAccountId());
        liquidatePortfolioRequest.setExchange(tradingAccInfo.getExchange());
        liquidatePortfolioRequest.setParams(liquidationReference);
        String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(liquidatePortfolioRequest),
                                                                        LsfConstants.HTTP_PRODUCER_OMS_LIQUIDATE_PORTFOLIO);
        if (result != null && !result.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(result, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray[0].equals("1")) {
                isLiquidate = true;
                logger.info("===========LSF : Liquidation Success , Trading Account: "
                            + tradingAccInfo.getAccountId()
                            + " , application ID:"
                            + applicationID
                            + " , liquidation reference :"
                            + liquidationReference
                            + "PoID"
                            + poID);
                lsfRepository.addLiquidationLog(Integer.parseInt(liquidationReference),
                                                customerCashAccount,
                                                masterCashAccount,
                                                amountToBeSettled,
                                                applicationID,
                                                LsfConstants.LIQUIDATION_REQUEST_SENT_TO_OMS,
                                                poID);
            } else {
                isLiquidate = false;
                logger.error("===========LSF : Liquidation Failed , Trading Account: "
                             + tradingAccInfo.getAccountId()
                             + " , Failure Reason :"
                             + resultArray[1]);
            }
        } else {
            isLiquidate = false;
        }
        return isLiquidate;
    }
}
