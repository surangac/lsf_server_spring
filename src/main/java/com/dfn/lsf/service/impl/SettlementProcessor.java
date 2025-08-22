package com.dfn.lsf.service.impl;

import java.util.Map;

import com.dfn.lsf.model.*;
import com.dfn.lsf.util.MessageType;
import org.springframework.stereotype.Service;

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
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(String request) {
        try {
            auditLogProcessor.process(request);
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
        MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);

        if(!application.getFinanceMethod().equals("1")) {
            return performEarlySettlementCommodity(applicationID, userID, settlementAmount, orderID);
        }
        if(application.getCurrentLevel() >= 17) {
            responseCode = 500;
            responseMessage = "Application is already in settlement process";
            commonResponse.setResponseCode(responseCode);
            commonResponse.setResponseMessage(responseMessage);
            return gson.toJson(commonResponse);
        }
        log.info("===========LSF : (performEarlySettlement)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);
        TradingAcc lsfTradingAcc = lsfCoreService.getLsfTypeTradinAccountForUser(userID,applicationID);
        CashAcc lsfCashAccount = lsfCoreService.getLsfTypeCashAccountForUser(userID,applicationID);//get lsf type cash account details for user
        PurchaseOrder purchaseOrder= lsfRepository.getSinglePurchaseOrder(orderID);        
        
        if((lsfCoreService.checkPendingOrdersForLSFTradingAccount(lsfTradingAcc.getAccountId(), lsfCashAccount.getAccountId())== 0)
                &&
                ((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount)
                &&
                purchaseOrder.getSettlementStatus()!=1
                ){
            masterCashAccount = lsfCoreService.getMasterCashAccount();//getting ABIC master cash account
            application.setCurrentLevel(17);
            application.setOverallStatus(String.valueOf(16));
            if (lsfCoreService.cashTransferToMasterAccount(lsfCashAccount.getAccountId(),lsfTradingAcc.getAccountId(), masterCashAccount, settlementAmount, applicationID, "1")) {
                lsfRepository.updatePOToSettledState(Integer.parseInt(orderID));
                log.info("===========LSF :(performEarlySettlement)- Cash Transfer Succeed , From(Customer Cash Account):" + lsfCashAccount.getAccountId() + ", To:(Master Cash Account)" + masterCashAccount + ", Amount :" + settlementAmount);
                responseMessage = "Successfully Deducted Outstanding Amount.";
                //Todo -- After Sending the Account Closure Request to OMS LSF is not waiting to the OMS Response
                AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                accountDeletionRequestState = lsfCoreService.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), application.getTradingAcc(), lsfCashAccount.getAccountId(), application.getCashAccount());
                if (accountDeletionRequestState.isSent()) {
                    application.setCurrentLevel(18);
                    application.setOverallStatus(String.valueOf(17));
                    log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);

                    try {
                        notificationManager.sendEarlySettlementNotification(application);
                    } catch (Exception e) {
                        commonResponse.setResponseCode(responseCode);
                        commonResponse.setResponseMessage(responseMessage + " , failed to send the notification.");
                        e.printStackTrace();
                    }
                } else {
                    log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
                    responseCode = 200; // since cash transfer is success response code is set to 200
                    commonResponse.setErrorCode(accountDeletionRequestState.getErrorCode()); // "-1" error code is set since cash transfer is success and account closure is failed
                    responseMessage = accountDeletionRequestState.getFailureReason();
                }
            } else {
                responseCode = 500;
                responseMessage = "Failed to deduct outstanding due to Cash Transfer failure.";
            }
        } else {
            responseCode = 500;
            responseMessage = "Already settled or,You have pending orders in the LSF Trading Account :" + lsfTradingAcc.getAccountId() + ", Can't perform settlement";
        }
        lsfRepository.updateMurabahApplication(application);
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);

        log.info("===========LSF : (performEarlySettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    public String performEarlySettlementCommodity(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String toCashAccount = null;
        String toTradingAccount = null;

        log.info("===========LSF : (performEarlySettlementCommodity)-REQUEST  : , ApplicationID:" + applicationID + ",Order ID:" + orderID + ", Amount:" + settlementAmount);

        MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);

        // check if the is Rollover application
        if (application.isRollOverApp()) {
            MurabahApplication originalApplication = lsfRepository.getMurabahApplication(application.getRollOverAppId());
            // check whether the original application is in settlement process
            if (originalApplication.getCurrentLevel() != 18) {
                responseCode = 500;
                responseMessage = "The original application must be settled before settling the rollover.";
                commonResponse.setErrorCode(LsfConstants.APPLICATION_MUST_BE_SETTLED);
                commonResponse.setResponseCode(responseCode);
                commonResponse.setResponseMessage(responseMessage);
                return gson.toJson(commonResponse);
            }
            toCashAccount = originalApplication.getCashAccount();
            toTradingAccount = originalApplication.getTradingAcc();
        } else {
            toCashAccount = application.getCashAccount();
            toTradingAccount = application.getTradingAcc();
        }

        String masterCashAccount = GlobalParameters.getInstance().getInstitutionInvestAccount();

        String originalApplicationID = application.isRollOverApp() ? application.getRollOverAppId() : applicationID;

        CashAcc lsfCashAccount = lsfCoreService.getLsfTypeCashAccountForUser(userID,originalApplicationID);
        TradingAcc lsfTradingAcc = lsfCoreService.getLsfTypeTradinAccountForUser(userID,originalApplicationID);
        PurchaseOrder purchaseOrder= lsfRepository.getSinglePurchaseOrder(orderID);
//        if (purchaseOrder.getIsPhysicalDelivery() == 1 || purchaseOrder.getSellButNotSettle() ==1) {
//            responseCode = 500;
//            responseMessage = "Cannot perform early settlement for physical delivery orders or orders in sell but not settle state.";
//            commonResponse.setResponseCode(responseCode);
//            commonResponse.setResponseMessage(responseMessage);
//            return gson.toJson(commonResponse);
//        }

        if (((lsfCashAccount.getCashBalance()-lsfCashAccount.getNetReceivable())>settlementAmount) && purchaseOrder.getSettlementStatus()!=1) {
            application.setCurrentLevel(17);
            application.setOverallStatus(String.valueOf(16));

            if (lsfCoreService.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), lsfTradingAcc.getAccountId(), masterCashAccount, settlementAmount, applicationID, "0")) {
                lsfRepository.updatePOToSettledState(Integer.parseInt(orderID));
                log.info("===========LSF :(performEarlySettlementCommodity)- Cash Transfer Succeed , From(Customer Cash Account):" + lsfCashAccount.getAccountId() + ", To:(Master Cash Account)" + masterCashAccount + ", Amount :" + settlementAmount);

                if(application.isRollOverApp() || !hasRolloverApplication(application)) {
                    AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                    accountDeletionRequestState = lsfCoreService.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), toTradingAccount, lsfCashAccount.getAccountId(), toCashAccount);
                    if (accountDeletionRequestState.isSent()) {
                        application.setCurrentLevel(18);
                        application.setOverallStatus(String.valueOf(17));
                        log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);

                        try {
                            notificationManager.sendEarlySettlementNotification(application);
                        } catch (Exception e) {
                            commonResponse.setResponseCode(responseCode);
                            commonResponse.setResponseMessage(responseMessage + " , failed to send the notification.");
                        }
                    } else {
                        log.info("===========LSF :(performAutoSettlement)- Account Deletion Request Rejected from OMS, Application ID :" + applicationID + ", Reason :" +  accountDeletionRequestState.getFailureReason());
                        responseCode = 200; // since cash transfer is success response code is set to 200
                        commonResponse.setErrorCode(accountDeletionRequestState.getErrorCode()); // error code is set since cash transfer is success and account closure is failed
                        responseMessage = accountDeletionRequestState.getFailureReason();
                    }
                } else {
                    application.setCurrentLevel(18);
                    application.setOverallStatus(String.valueOf(17));
                }
                try {
                    notificationManager.sendEarlySettlementNotification(application);
                } catch (Exception e) {
                    commonResponse.setResponseCode(responseCode);
                    commonResponse.setResponseMessage(responseMessage + " , failed to send the notification.");
                }
            } else {
                responseCode = 500;
                responseMessage = "Failed to deduct outstanding due to Cash Transfer failure.";
            }
        } else {
            responseCode = 500;
            responseMessage = "Already settled or,You have pending orders in the LSF Cash Account :" + lsfCashAccount.getAccountId() + ", Can't perform settlement";
        }
        log.info("===========LSF :(performEarlySettlementCommodity)- Updating Customer:" + applicationID + ", OverRall Status:" + application.getOverallStatus() + ", Current Level:" + application.getCurrentLevel());
        lsfRepository.updateMurabahApplication(application);
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);

        log.info("===========LSF : (performEarlySettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    private boolean hasRolloverApplication(MurabahApplication application) {
        int rollOverCount = lsfRepository.hasRollOver(application.getId());
        return rollOverCount >0 ? true : false;
    }


    public String performManualSettlement(String applicationID, String userID, double settlementAmount, String orderID) {
        CommonResponse commonResponse = new CommonResponse();
        int responseCode = 200;
        String responseMessage = "";
        String masterCashAccount = null;
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
                ) {
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);

            if (lsfCoreService.cashTransferToMasterAccount(lsfCashAccount.getAccountId(), lsfTradingAcc.getAccountId(), masterCashAccount, settlementAmount, applicationID, "1")) {
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
                    commonResponse.setErrorCode(accountDeletionRequestState.getErrorCode());
                    responseMessage = accountDeletionRequestState.getFailureReason();
                }
            } else {
                responseCode = 500;
                responseMessage = "Failed to deduct outstanding due to Cash Transfer failure.";
            }
        } else {
            responseCode = 500;
            responseMessage = "Already settled or,You have pending orders in the LSF Trading Account :" + lsfTradingAcc.getAccountId() + ", Can't perform settlement";
        }
        commonResponse.setResponseCode(responseCode);
        commonResponse.setResponseMessage(responseMessage);
        log.info("===========LSF : (performManualSettlement)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    public String confirmRolloverByUser(Map<String, Object> map) {
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
        newPO.setAuthAbicToSell("1");
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