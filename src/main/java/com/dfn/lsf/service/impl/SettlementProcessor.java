package com.dfn.lsf.service.impl;

import java.util.Map;

import com.dfn.lsf.util.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.TradingAcc;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.ErrorCodes;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_SETTLEMENT_PROCESS;

@Service
@MessageType(MESSAGE_TYPE_SETTLEMENT_PROCESS)
@Slf4j
@RequiredArgsConstructor
public class SettlementProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    
    private final Gson gson;
    private final LsfCoreService lsfCoreService;
    private final NotificationManager notificationManager;
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing settlement request with subMessageType: {}", subMessageType);
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
                default: {
                    CommonResponse cmr = new CommonResponse();
                    cmr.setResponseCode(400);
                    cmr.setErrorMessage("Invalid subMessageType: " + subMessageType);
                    return gson.toJson(cmr);
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing settlement request", e);
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            return gson.toJson(cmr);
        }
    }

    public String performEarlySettlement(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;

        log.info("===========LSF : (performEarlySettlement)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);

        //buy pass for DIB
 /*       if(LSFUtils.isMarketOpened()){*/ // remove market open status during settlement after Sulthan's comment
            TradingAcc lsfTradingAcc = lsfCoreService.getLsfTypeTradinAccountForUser(userID,applicationID);
            CashAcc lsfCashAccount = lsfCoreService.getLsfTypeCashAccountForUser(userID,applicationID);//get lsf type cash account details for user
            PurchaseOrder purchaseOrder= lsfRepository.getSinglePurchaseOrder(orderID);
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);
            if((lsfCoreService.checkPendingOrdersForLSFTradingAccount(lsfTradingAcc.getAccountId(), lsfCashAccount.getAccountId())== 0)
                    &&
                    ((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount)
                    &&
                    purchaseOrder.getSettlementStatus()!=1
                    ){
                masterCashAccount = lsfCoreService.getMasterCashAccount();//getting ABIC master cash account
                application.setCurrentLevel(17);
                application.setOverallStatus(String.valueOf(16));
                if (lsfCoreService.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount, applicationID)) {
                    lsfRepository.updatePOToSettledState(Integer.parseInt(orderID));
                    log.info("===========LSF :(performEarlySettlement)- Cash Transfer Succeed , From(Customer Cash Account):" + lsfCashAccount.getAccountId() + ", To:(Master Cash Account)" + masterCashAccount + ", Amount :" + settlementAmount);
                    responseMessage = "Successfully Deducted Outstanding Amount.";
                    //Todo -- After Sending the Account Closure Request to OMS LSF is not waiting to the OMS Response
                    application.setCurrentLevel(18);
                    application.setOverallStatus(String.valueOf(17));
                    AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                    accountDeletionRequestState = lsfCoreService.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), application.getTradingAcc(), lsfCashAccount.getAccountId(), application.getCashAccount());
                    if (accountDeletionRequestState.isSent()) {
                   
                        log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);

                        try {
                            notificationManager.sendEarlySettlementNotification(application);
                        } catch (Exception e) {
                            commonResponse.setResponseCode(responseCode);
                            commonResponse.setResponseMessage(responseMessage + " , failed to send the notification.");
                            e.printStackTrace();
                        }
                    }else{
                        log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
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
        lsfRepository.updateMurabahApplication(application);
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);

        log.info("===========LSF : (performEarlySettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    public String performManualSettlement(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;
        String collateralTradingAccount = null;
        String collateralCashAccount = null;
        log.info("===========LSF : (performManualSettlement)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);

        TradingAcc lsfTradingAcc = lsfCoreService.getLsfTypeTradinAccountForUser(userID,applicationID);
        CashAcc  lsfCashAccount = lsfCoreService.getLsfTypeCashAccountForUser(userID,applicationID);//get lsf type cash account details for user
        masterCashAccount = lsfCoreService.getMasterCashAccount();//getting master cash account
        PurchaseOrder purchaseOrder= lsfRepository.getSinglePurchaseOrder(orderID);
        if((lsfCoreService.checkPendingOrdersForLSFTradingAccount(lsfTradingAcc.getAccountId(), lsfCashAccount.getAccountId()) == 0)
                &&
                ((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount)
                &&
                purchaseOrder.getSettlementStatus()!=1
                ){
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);

            if (lsfCoreService.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), masterCashAccount, settlementAmount, applicationID)) {
                log.info("===========LSF :(performManualSettlement)- Cash Transfer Succeed , From:" + lsfCashAccount.getAccountId() + ", To:" + masterCashAccount + ", Amount :" + settlementAmount);
                responseMessage = "Successfully Deducted Outstanding Amount.";
                lsfRepository.updatePOToSettledState(Integer.parseInt(orderID));
                AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                accountDeletionRequestState = lsfCoreService.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), application.getTradingAcc(), lsfCashAccount.getAccountId(), application.getCashAccount());

                if (accountDeletionRequestState.isSent()) {
                     log.info("===========LSF :(performManualSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);
                }else{
                    log.info("===========LSF :(performManualSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
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
        log.info("===========LSF : (performManualSettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
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

        MurabahApplication newApplication = lsfRepository.getMurabahApplication(appID);
        oldAppId = newApplication.getRollOverAppId();
        PurchaseOrder oldPO = lsfRepository.getSinglePurchaseOrder(oldAppId);
        PurchaseOrder newPO = lsfRepository.getSinglePurchaseOrder(newApplication.getId());
        performEarlySettlement(oldAppId,customerId,oldPO.getOrderSettlementAmount(),oldPO.getId());
        newPO.setAuthAbicToSell(1);
        newPO.setIsPhysicalDelivery(0);
        lsfRepository.addAuthAbicToSellStatus(newPO);
        responseMessage = "Confirmed Rollover Successfully";
        try {
            //todo : Need to add seperate notification templates
            notificationManager.sendAuthAbicToSellNotification(newApplication, true);/*---Send Notification---*/
        } catch (Exception e) {
            cmr.setResponseCode(responseCode);
            cmr.setResponseMessage(responseMessage + " , failed to send the notification.");
        }
        log.debug("===========LSF : LSF-SERVER RESPONSE (confirmRolloverByUser) :" + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
}