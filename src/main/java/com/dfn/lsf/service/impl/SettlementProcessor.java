package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.SettlementRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import org.slf4j.log;
import org.slf4j.logFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Qualifier("15") // MESSAGE_TYPE_SETTLEMENT_PROCESS
// contrains both LsfSettlementProcessorAbic and SettlementInquiryProcessor
@Slf4j
@RequiredArgsConstructor
public class SettlementProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    
    private final Gson gson;
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing settlement request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.PERFORM_EARLY_SETTLEMENT: {//early settlement-client
                    return performEarlySettlement(requestMap.get("id").toString(), requestMap.get("customerID").toString(), Double.valueOf(requestMap.get("settlementAmount").toString()), requestMap.get("orderID").toString());
                }
                case LsfConstants.PERFORM_MANUAL_SETTLEMENT: {//manual  settlement-admin
                    return performManualSettlement(requestMap.get("id").toString(), requestMap.get("customerID").toString(), Double.valueOf(requestMap.get("settlementAmount").toString()), requestMap.get("orderID").toString());
                }
                case LsfConstants.CONFIRM_ROLLOVER_BY_USER: {
                    return confirmRolloverByUser(requestMap);
                }
                case LsfConstants.SETTLEMENT_SUMMARY_APPLICATION: { //settlement summary -client
                    return gson.toJson(getSettlementSummaryForApplication(requestMap.get("id").toString(), requestMap.get("customerID").toString()));
                }
                case LsfConstants.SETTLEMENT_BREAKDOWN_APPLICATION: {// settlement breakdown-client
                    return getSettlementBreakDownForApplication(requestMap.get("id").toString());
                }
                case LsfConstants.GET_SETTLEMENT_LIST: {//get settlement list-admin
                    return getSettlementList(requestMap);
                }
                case LsfConstants.GET_LIST_FOR_MANUAL_SETTELEMENT:{ // get the application list allowed for manual settlement
                    return getApplicationListForManualSettlement();
                }
                case LsfConstants.GET_SETTLEMENT_INSTALLMENT_LIST:{
                    return getSettlementInstallmentList();
                }
                case LsfConstants.CONTRACT_ROLLOVER_PROCESS:{
                    return createContractRollover(requestMap);
                }
                default:
                    log.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            log.error("Error processing settlement request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }

    public String performEarlySettlement(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;

        logger.info("===========LSF : (performEarlySettlement)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);

        //buy pass for DIB
 /*       if(LSFUtils.isMarketOpened()){*/ // remove market open status during settlement after Sulthan's comment
            TradingAcc lsfTradingAcc = lsfCoreAbic.getLsfTypeTradinAccountForUser(userID,applicationID);
            CashAcc lsfCashAccount = lsfCoreAbic.getLsfTypeCashAccountForUser(userID,applicationID);//get lsf type cash account details for user
            PurchaseOrder purchaseOrder= lsfDaoI.getSinglePurchaseOrder(orderID);
            MurabahApplication application = lsfDaoI.getMurabahApplication(applicationID);
            if((lsfCoreAbic.checkPendingOrdersForLSFTradingAccount(lsfTradingAcc.getAccountId(), lsfCashAccount.getAccountId())== 0)
                    &&
                    ((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount)
                    &&
                    purchaseOrder.getSettlementStatus()!=1
                    ){
                masterCashAccount = lsfCoreAbic.getMasterCashAccount();//getting ABIC master cash account
                application.setCurrentLevel(17);
                application.setOverallStatus(String.valueOf(16));
                if (lsfCoreAbic.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount, applicationID)) {
                    lsfDaoI.updatePOToSettledState(Integer.parseInt(orderID));
                    logger.info("===========LSF :(performEarlySettlement)- Cash Transfer Succeed , From(Customer Cash Account):" + lsfCashAccount.getAccountId() + ", To:(Master Cash Account)" + masterCashAccount + ", Amount :" + settlementAmount);
                    responseMessage = "Successfully Deducted Outstanding Amount.";
                    //Todo -- After Sending the Account Closure Request to OMS LSF is not waiting to the OMS Response
                    application.setCurrentLevel(18);
                    application.setOverallStatus(String.valueOf(17));
                    AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                    accountDeletionRequestState = lsfCoreAbic.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), application.getTradingAcc(), lsfCashAccount.getAccountId(), application.getCashAccount());
                    if (accountDeletionRequestState.isSent()) {
                   /* logger.info("===========LSF :(performEarlySettlement)- Moving Application to close state, Application ID :" + applicationID);
                    lsfCoreAbic.moveToCashTransferredClosedState(applicationID, "Early Settlement", orderID);//updating application*/

                        logger.info("===========LSF :(performAutoSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);

                        try {
                            NotificationManager.sendEarlySettlementNotification(application);
                        } catch (ComponentLookUpException e) {
                            commonResponse.setResponseCode(responseCode);
                            commonResponse.setResponseMessage(responseMessage + " , failed to send the notification.");
                            e.printStackTrace();
                        }
                    }else{
                        logger.info("===========LSF :(performAutoSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
                        responseCode = 200; // since cash transfer is success response code is set to 200
                        commonResponse.setErrorCode(-1); // error code is set since cash transfer is success and account closure is failed
                        responseMessage = accountDeletionRequestState.getFailureReason();
                    }
                } else {
                    responseCode = 500;
                    responseMessage = "Failed to deduct outstanding due to Cash Transfer failure.";
                }
            }else{
                responseCode = 500;
                responseMessage = "Already settled or,You have pending orders in the LSF Trading Account :" + lsfTradingAcc.getAccountId() + ", Can't perform settlement";
            }
        lsfDaoI.updateMurabahApplication(application);
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);

        logger.info("===========LSF : (performEarlySettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    public String performManualSettlement(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;
        String collateralTradingAccount = null;
        String collateralCashAccount = null;
        logger.info("===========LSF : (performManualSettlement)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);

        TradingAcc lsfTradingAcc = lsfCoreAbic.getLsfTypeTradinAccountForUser(userID,applicationID);
        CashAcc  lsfCashAccount = lsfCoreAbic.getLsfTypeCashAccountForUser(userID,applicationID);//get lsf type cash account details for user
        masterCashAccount = lsfCoreAbic.getMasterCashAccount();//getting master cash account
        PurchaseOrder purchaseOrder= lsfDaoI.getSinglePurchaseOrder(orderID);
        if((lsfCoreAbic.checkPendingOrdersForLSFTradingAccount(lsfTradingAcc.getAccountId(), lsfCashAccount.getAccountId()) == 0)
                &&
                ((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount)
                &&
                purchaseOrder.getSettlementStatus()!=1
                ){
            MurabahApplication application = lsfDaoI.getMurabahApplication(applicationID);

            if (lsfCoreAbic.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount, applicationID)) {
                logger.info("===========LSF :(performManualSettlement)- Cash Transfer Succeed , From:" + lsfCashAccount.getAccountId() + ", To:" + masterCashAccount + ", Amount :" + settlementAmount);
                responseMessage = "Successfully Deducted Outstanding Amount.";
                lsfDaoI.updatePOToSettledState(Integer.parseInt(orderID));
                AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                accountDeletionRequestState = lsfCoreAbic.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), application.getTradingAcc(), lsfCashAccount.getAccountId(), application.getCashAccount());

                if (accountDeletionRequestState.isSent()) {
                   /* logger.info("===========LSF :(performManualSettlement)- Moving Application to close state, Application ID :" + applicationID);
                    lsfCoreAbic.moveToCashTransferredClosedState(applicationID, "Manual Settlement", orderID);//updating application
*/
                    logger.info("===========LSF :(performManualSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);
                }else{
                    logger.info("===========LSF :(performManualSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
                    responseCode = 200;
                    commonResponse.setErrorCode(-1);
                    responseMessage = accountDeletionRequestState.getFailureReason();
                }
            } else {
                responseCode = 500;
                responseMessage = "Failed to deduct outstanding due to Cash Transfer failure.";
            }
        }else{
            responseCode = 500;
            responseMessage = "Already settled or,You have pending orders in the LSF Trading Account :" + lsfTradingAcc.getAccountId() + ", Can't perform settlement";
        }


        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);
        logger.info("===========LSF : (performManualSettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }
    public String confirmRolloverByUser(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        String appID = map.get("appId").toString();
        String customerId = map.get("customerId").toString();
        String oldAppId = "";
        String ip;
        int responseCode = 200;
        String responseMessage = "";
        if (map.containsKey("ipAddress")){
            ip = map.get("ipAddress").toString();
        }

        MurabahApplication newApplication = lsfDaoI.getMurabahApplication(appID);
        oldAppId = newApplication.getRollOverAppId();
        PurchaseOrder oldPO = lsfDaoI.getSinglePurchaseOrder(oldAppId);
        PurchaseOrder newPO = lsfDaoI.getSinglePurchaseOrder(newApplication.getId());
        performEarlySettlement(oldAppId,customerId,oldPO.getOrderSettlementAmount(),oldPO.getId());
        newPO.setAuthAbicToSell(1);
        newPO.setIsPhysicalDelivery(0);
        lsfDaoI.addAuthAbicToSellStatus(newPO);
        responseMessage = "Confirmed Rollover Successfully";
        try {
            //todo : Need to add seperate notification templates
            NotificationManager.sendAuthAbicToSellNotification(newApplication, true);/*---Send Notification---*/
        } catch (ComponentLookUpException e) {
            cmr.setResponseCode(responseCode);
            cmr.setResponseMessage(responseMessage + " , failed to send the notification.");
            e.printStackTrace();
        }
        logger.debug("===========LSF : LSF-SERVER RESPONSE (confirmRolloverByUser) :" + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
    public SettlementListResponse getSettlementSummaryForApplication(String applicationID, String userID) {
        SettlementListResponse settlementListResponse = new SettlementListResponse();
        logger.info("===========LSF : Settlement Summery Request Received , applicationID:" + applicationID);
        List<SettlementSummaryResponse> settlementSummaryResponseList = new ArrayList<>();
        List<PurchaseOrder> purchaseOrders = null;
        CashAcc cashAcc = null;
        OrderProfit orderProfit = null;

        purchaseOrders = lsfDaoI.getAllPurchaseOrder(applicationID);
        MurabahApplication murabahApplication = lsfDaoI.getMurabahApplication(applicationID);
        MApplicationCollaterals collaterals = lsfDaoI.getApplicationCompleteCollateral(applicationID);
        int isInRollover = -1;
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            for(PurchaseOrder purchaseOrder : purchaseOrders) {
                if(purchaseOrder.getCustomerApproveStatus() == 1){
                    SettlementSummaryResponse settlementSummary = new SettlementSummaryResponse();
                    settlementSummary.setApplicationID(applicationID);
                    settlementSummary.setCustomerID(userID);
                    settlementSummary.setOrderID(purchaseOrder.getId());
                    settlementSummary.setLoanAmount(purchaseOrder.getOrderCompletedValue());
                    settlementSummary.setSettlementDate(formatSettlementDate(String.valueOf(purchaseOrder.getSettlementDate())));
                    settlementSummary.setSettelmentStatus(purchaseOrder.getSettlementStatus());
                    settlementSummary.setLsfAccountDeletionState(murabahApplication.getLsfAccountDeletionState());
                    settlementSummary.setDiscountOnProfit(murabahApplication.getDiscountOnProfit());
                    settlementSummary.setProductType(murabahApplication.getProductType());
                    cashAcc = lsfCore.getLsfTypeCashAccountForUser(userID,applicationID);
                    if (cashAcc != null) {
                        // remove pending settle from the available cash balance due to T+2 implementation
                        if(cashAcc.getNetReceivable()>0){
                            settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance()-cashAcc.getNetReceivable());
                        }else {
                            settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance());
                    }
                    }
                    logger.info("===========LSF : (settlementSummaryApplication)-Product TYpe  : " + murabahApplication.getProductType());
                    if(murabahApplication.getProductType()!=3){
                        orderProfit = lsfDaoI.getSummationOfProfitUntilToday(applicationID, purchaseOrder.getId());
                        if (orderProfit != null) {
                            settlementSummary.setCumulativeProfit(orderProfit.getCumulativeProfitAmount());
                            settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                            // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount() + settlementSummary.getCumulativeProfit());
                        } else {
                            // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount());
                            settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount()); //change for DIB
                        }
                    }else {
                        OrderProfit profit = lsfCore.calculateConditionalProfit(collaterals,purchaseOrder,murabahApplication.getDiscountOnProfit());
                        settlementSummary.setOrderProfit(profit);
                        settlementSummary.setCumulativeProfit(profit.getChargeCommissionAmt());
                        logger.info("===========LSF : (settlementSummaryApplication)-Settlement Profit Target Comm : " + profit.getTargetCommission() + ", Traded Comm :" + profit.getTradedCommission());
                    }

                    if(purchaseOrder.getCustomerApproveStatus() == 0){
                        settlementSummary.setIsCustomerApproved(false);
                    }else if(purchaseOrder.getCustomerApproveStatus() == 1){
                        settlementSummary.setIsCustomerApproved(true);
                    }
                    List<Installments> installmentsList = lsfDaoI.getPurchaseOrderInstallments(purchaseOrder.getId());
                        settlementSummary.setInstallmentsList(installmentsList);
                    if (murabahApplication.getFinanceMethod() != null && murabahApplication.getFinanceMethod().equalsIgnoreCase("2")) {
                        settlementSummary.setMaxRollOverPrd(GlobalParameters.getInstance().getMaxRolloverPeriod());
                        settlementSummary.setMinRollOverPrd(GlobalParameters.getInstance().getMinRolloverPeriod());
                        settlementSummary.setMinRollOverRatio(GlobalParameters.getInstance().getMinRolloverRatio());
                        if (isInRollover < 0) {
                            isInRollover = lsfCore.getIsInRollOverPeriod(
                                    purchaseOrder.getSettlementDate(),
                                    settlementSummary.getMinRollOverPrd(),
                                    settlementSummary.getMaxRollOverPrd());
                        }
                    }
                    settlementSummary.setIsInRollOverPrd(isInRollover);
                    if (murabahApplication.getRollOverAppId() == null || murabahApplication.getRollOverAppId().equalsIgnoreCase("null")){
                        settlementSummary.setRollOverAppId("-1");
                    } else {
                        settlementSummary.setRollOverAppId(murabahApplication.getRollOverAppId());
                    }
                    lsfCore.calculateFTV(collaterals);
                    settlementSummary.setFtv(collaterals.getFtv());
                    settlementSummaryResponseList.add(settlementSummary);
                }

            }
        }
        settlementListResponse.setSettlementSummaryResponseList(settlementSummaryResponseList);
        logger.info("===========LSF : (settlementSummaryApplication)-LSF-SERVER RESPONSE  : " + gson.toJson(settlementListResponse));
        return settlementListResponse;
    }

    public String getSettlementBreakDownForApplication(String applicationID) {
        SettlementBreakDownResponse settlementBreakDownResponse = new SettlementBreakDownResponse();
        PurchaseOrder purchaseOrder = null;
        List<PurchaseOrder> purchaseOrders = null;
        List<OrderProfit> orderProfitList = null;
        purchaseOrders = lsfDaoI.getAllPurchaseOrder(applicationID);
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            purchaseOrder = purchaseOrders.get(0);
            orderProfitList = lsfDaoI.getAllOrderProfitsForApplication(applicationID, purchaseOrder.getId());
        }
        settlementBreakDownResponse.setApplicationID(applicationID);
        settlementBreakDownResponse.setProfitList(orderProfitList);
        logger.info("===========LSF : (settlementBreakDownApplication)-LSF-SERVER RESPONSE  : " + gson.toJson(settlementBreakDownResponse));
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
        if (lsfCore.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount, applicationID)) {
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

    private String getSettlementList(Map<String, Object> requestMap){

        int settlementStatus=-1;
        String fromDate="01012017";
        String toDate="01012050";
        if(requestMap.containsKey("settlementStatus"))
            settlementStatus=Integer.valueOf(requestMap.get("settlementStatus").toString());
        if(requestMap.containsKey("fromDate"))
            fromDate=requestMap.get("fromDate").toString();
        if(requestMap.containsKey("toDate"))
            toDate=requestMap.get("toDate").toString();

        List<SettlementListResponse> settlementSummaryResponseList = new ArrayList<>();
        List<SettlementSummaryResponse> settlementResponseList= lsfDaoI.getSettlementListReport(settlementStatus,fromDate,toDate);
        SettlementListResponse settlementListResponse=new SettlementListResponse();
        settlementListResponse.setSettlementSummaryResponseList(settlementResponseList);
        settlementSummaryResponseList.add(settlementListResponse);
        return gson.toJson(settlementSummaryResponseList);
    }

    private String getSettlementInstallmentList(){
        List<MurabahApplication> murabahApplicationList = null;
        /*SettlementListResponse settlementListResponse = new SettlementListResponse();*/
        List<SettlementListResponse> settlementSummaryResponseList = new ArrayList<>();
     //   murabahApplicationList = lsfDaoI.getOrderContractSingedApplications(); // instead of getting order contract signed app get all the apps change for DIB
        murabahApplicationList = lsfDaoI.getAllMurabahApplications();
        if(murabahApplicationList != null && murabahApplicationList.size() > 0){
            for(MurabahApplication murabahApplication : murabahApplicationList){
                SettlementListResponse settlementSummaryResponse = getSettlementSummaryForApplication(murabahApplication.getId(), murabahApplication.getCustomerId());
                if(settlementSummaryResponse != null && settlementSummaryResponse.getSettlementSummaryResponseList().size() > 0){
                    settlementSummaryResponseList.add(settlementSummaryResponse);
                }

              /* if(murabahApplication.getCurrentLevel() >= 17){
                   settlementSummaryResponse.setSettelmentStatus(1);
               }else{
                   settlementSummaryResponse.setSettelmentStatus(0);
               }*/
            }
        }
/*
        settlementListResponse.setSettlementSummaryResponseList(settlementSummaryResponseList);
*/
        return gson.toJson(settlementSummaryResponseList);
    }


    private String formatSettlementDate(String settlementDate) {
        String formattedDate = "";
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
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

        purchaseOrders = lsfDaoI.getApplicationForManualSettlement();
        if (purchaseOrders != null && purchaseOrders.size() > 0) {
            for (PurchaseOrder purchaseOrder : purchaseOrders) {
                SettlementSummaryResponse settlementSummary = new SettlementSummaryResponse();
             //   MurabahApplication application = lsfDaoI.getMurabahApplication(purchaseOrder.getApplicationId());
                settlementSummary.setApplicationID(purchaseOrder.getApplicationId());
                settlementSummary.setCustomerName(purchaseOrder.getCustomerName());
                settlementSummary.setTradingAccNumber(purchaseOrder.getTradingAccNum());
                settlementSummary.setCustomerID(purchaseOrder.getCustomerId());
               // settlementSummary.setCustomerName();
                settlementSummary.setOrderID(purchaseOrder.getId());
                settlementSummary.setLoanAmount(purchaseOrder.getOrderCompletedValue());
                settlementSummary.setSettlementDate(formatSettlementDate(String.valueOf(purchaseOrder.getSettlementDate())));
                settlementSummary.setSettelmentStatus(purchaseOrder.getSettlementStatus());
                cashAcc = lsfCore.getLsfTypeCashAccountForUser(purchaseOrder.getCustomerId(),purchaseOrder.getApplicationId());
                if (cashAcc != null) {
                    settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance());
                    if(cashAcc.getNetReceivable()>0){
                        settlementSummary.setAvailableCashBalance(cashAcc.getCashBalance()-cashAcc.getNetReceivable());
                    }
                }
                orderProfit = lsfDaoI.getSummationOfProfitUntilToday(purchaseOrder.getApplicationId(), purchaseOrder.getId());
                if (orderProfit != null) {
                    settlementSummary.setCumulativeProfit(orderProfit.getCumulativeProfitAmount());
                    settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount());
                    // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount() + settlementSummary.getCumulativeProfit());
                } else {
                    // settlementSummary.setTotalSettlementAmount(settlementSummary.getLoanAmount());
                    settlementSummary.setLoanProfit(purchaseOrder.getProfitAmount()); //change for DIB
                }
                settlementSummaryResponseList.add(settlementSummary);
            }
        }
        settlementListResponse.setSettlementSummaryResponseList(settlementSummaryResponseList);
        logger.info("===========LSF : (getListForManualSettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(settlementListResponse));
        return gson.toJson(settlementListResponse);
    }

    public String createContractRollover(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String appID = "";
        String userId = "";
        if (map.containsKey("appId")) {
            appID = map.get("appId").toString();
        }
        if (map.containsKey("customerId")){
            userId = map.get("customerId").toString();
        }
        MurabahApplication oldApplication = lsfDaoI.getMurabahApplication(appID);
        List<PurchaseOrder> purchaseOrders = lsfDaoI.getPurchaseOrderForApplication(appID);
        if (oldApplication.getFinanceMethod().equalsIgnoreCase("2") || oldApplication.getAgreementList().get(0).getFinanceMethod() == 2) {
            Date startDate;
            Date crntDate;
            long remainDays = 0;
            Date settlementDate;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
            Object response = lsfCore.reValuationProcess(oldApplication,true);
            try {
                crntDate = new Date();
                settlementDate = sdf2.parse(purchaseOrders.get(0).getSettlementDate());

                remainDays = TimeUnit.MILLISECONDS.toDays(settlementDate.getTime() - crntDate.getTime());
                logger.info("Remain days for Rollover : "+remainDays);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (remainDays > GlobalParameters.getInstance().getMaxRolloverPeriod() || remainDays < GlobalParameters.getInstance().getMinRolloverPeriod()) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Not in Roll Over Period");
                cmr.setErrorCode(LsfConstants.ERROR_NOT_IN_ROLLOVER_PERIOD);
                logger.info("===========LSF : Rollover period validation failed , Application ID :" + map.get("appId"));
                return gson.toJson(cmr);
            } else if (response != null && GlobalParameters.getInstance().getMinRolloverRatio() > lsfDaoI.getFTVforToday(appID).getFtv()){
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Minimum Rollover Ratio Validation failed");
                cmr.setErrorCode(LsfConstants.ERROR_NOT_IN_ROLLOVER_RATIO);
                logger.info("===========LSF : Minimum Rollover Ratio Validation failed, Application ID :" + map.get("appId"));
                return gson.toJson(cmr);
            }else {
                java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                MurabahApplication newApplication = oldApplication;
                newApplication.setId("-1");
                newApplication.setRollOverAppId(oldApplication.getId());
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

                SettlementListResponse settlementListResponse = getSettlementSummaryForApplication(appID,userId);
                double newAmnt = oldApplication.getFinanceRequiredAmt() + settlementListResponse.getSettlementSummaryResponseList().get(0).getLoanProfit() +
                        GlobalParameters.getInstance().getComodityAdminFee()+GlobalParameters.getInstance().getComodityFixedFee();
                newApplication.setFinanceRequiredAmt(newAmnt);
                newApplication.setRollOverAppId(appID);

                String id = lsfDaoI.updateMurabahApplication(newApplication);
                logger.info("New application ID : " + id);
                String l32ID = lsfDaoI.initialAgreementStatus(Integer.parseInt(id), 2, oldApplication.getProductType(),2);
                newApplication.setId(id);
//                try {
//                    NotificationManager.sendNotification(newApplication);
//                } catch (ComponentLookUpException e) {
//                    logger.info(e);
//                }
                logger.info("===========LSF : New Murabah Application Created to rollover, Application ID :" + id + " , User ID:" + newApplication.getCustomerId() + " ,l32ID : "+l32ID);
                String RspMsg = id + "|" + 1 + "|" + OverrallApprovalStatus.PENDING.statusCode();
                List<Symbol> symbols = lsfDaoI.getInitialAppPortfolio(oldApplication.getId());
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
                            lsfDaoI.updateInitailAppPortfolio(symbol, id);
                        }
                    }
                }
                cmr.setResponseCode(200);
                cmr.setResponseMessage(RspMsg);
                logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
                return gson.toJson(cmr);
            }
        }else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Rollover is only allowed for Commodity finance Margin");
            cmr.setErrorCode(LsfConstants.ERROR_ABNORMAL_ACTIVITY);
            logger.info("===========LSF : Rollover is only allowed for Commodity finance Margin , Application ID :" + map.get("id"));
            return gson.toJson(cmr);
        }
    }
}