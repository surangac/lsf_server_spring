package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.TradingAcc;
import com.dfn.lsf.model.requestMsg.AccountCreationRequest;
import com.dfn.lsf.model.requestMsg.PendingActivityRequest;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Defined in InMessageHandlerAdminCbr
 * route : PENDING_ACTIVITY_ADMIN_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_PENDING_ACTIVITY_ADMIN = 25;
 */
@Service
@RequiredArgsConstructor
@Qualifier("25")
public class PendingActivityAdminProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OtpProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final NotificationManager notificationManager;
    private final Helper helper;
    private final LsfCoreService lsfCoreService;

    @Override
    public String process(String request) {
        PendingActivityRequest pendingActivityRequest = gson.fromJson((String) request, PendingActivityRequest.class);
        logger.debug("===========LSF : Pending Activity Request Received from Admin , Request :" + request);
        switch (pendingActivityRequest.getActivityID()) {
            case (LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED): {
                return resendInvestorAccountCreation(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED_OMS): {
                return resendInvestorAccountCreation(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION): {
                return resendExchangeAccountCreation(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATION_FAILED): {
                return resendExchangeAccountCreation(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_CASH_TRANSFER): {
                return resendAccountDeletion(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_SHARE_TRANSFER): {
                return resendAccountDeletion(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_DUE_TO_SHARE_TRANSFER_FAILURE_WITH_EXCHANGE): {
                return resendAccountDeletion(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_WITH_EXCHANGE): {
                return resendAccountDeletion(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_COLLATERAL_SHARE_TRANSFER_FAILED_FROM_EXCHANGE): {
                return resendCollateralTransfer(pendingActivityRequest);
            }
            case (LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS): {
                return resendCollateralTransfer(pendingActivityRequest);
            }
           /* case (LsfConstants.STATUS_BASKET_SHARE_TRANSFER_FAILED_FROM_EXCHANGE):{
                return resendBasketShareTransfer(pendingActivityRequest);
            }*/
            case (LsfConstants.STATUS_BASKET_SHARE_TRANSFER_REQUEST_FAILED_TO_SEND_OMS): {
                return resendBasketShareTransfer(pendingActivityRequest);
            }
            default: {
                CommonResponse commonResponse = new CommonResponse();
                commonResponse.setResponseCode(500);
                commonResponse.setResponseMessage("Invalid Pending Activity");
                commonResponse.setErrorMessage("Invalid Pending Activity");
                return null;
            }
        }
    }

    private String resendInvestorAccountCreation(PendingActivityRequest pendingActivity) {
        logger.debug("===========LSF :Resending Investor Account Creation : " + gson.toJson(pendingActivity));
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResponseCode(200);
        AccountCreationRequest createInvestorAccount = new AccountCreationRequest();
        createInvestorAccount.setReqType(LsfConstants.CREATE_INVESTOR_ACCOUNT);
        createInvestorAccount.setFromCashAccountNo(pendingActivity.getLsfTypeCashAccount());
        String omsResponseForInvestorAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createInvestorAccount));
        CommonResponse investorAccountResponse = helper.processOMSCommonResponseAccountCreation(
                omsResponseForInvestorAccountCreation);
        lsfRepository.updateActivity(
                pendingActivity.getApplicationID(),
                LsfConstants.STATUS_SENT_INVESTOR_ACCOUNT_CREATION);

        if (investorAccountResponse.getResponseCode() == -2) { /*---If Investor Account is already created---*/
            TradingAcc lsfTradingAcc = lsfCoreService.getLsfTypeTradinAccountForUser(
                    pendingActivity.getUserID(),
                    pendingActivity.getApplicationID());
            logger.debug("===========LSF :Investor Account Already Created  for Application ID :"
                         + pendingActivity.getApplicationID()
                         + " , Cash Account ID:"
                         + pendingActivity.getNonLSFCashAccount());
            AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
            createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
            createExchangeAccount.setTradingAccountId(lsfTradingAcc.getAccountId());
            createExchangeAccount.setExchange(lsfTradingAcc.getExchange());
            logger.debug("===========LSF : Creating Exchange Account for Trading Account :"
                         + createExchangeAccount.getTradingAccountId());
            String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(
                    createExchangeAccount));
            CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(
                    omsResponseForExchangeAccountCreation);
            if (exchangeAccountResponse.getResponseCode() == 1) {
                lsfRepository.updateActivity(
                        pendingActivity.getApplicationID(),
                        LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
            } else {
                lsfRepository.updateActivity(
                        pendingActivity.getApplicationID(),
                        LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
            }
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Investor Account Creation Succeed.");
        } else if (investorAccountResponse.getResponseCode() == -1) {
            lsfRepository.updateActivity(
                    pendingActivity.getApplicationID(),
                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED_OMS);
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Failed to create Investor Account, Please contact ABIC.");
            commonResponse.setErrorMessage("Failed to create Investor Account, Please contact ABIC.");
        }
        logger.debug("===========LSF :Resending Investor Account Creation , Response: " + gson.toJson(commonResponse));

        return gson.toJson(commonResponse);
    }

    private String resendExchangeAccountCreation(PendingActivityRequest pendingActivity) {
        logger.debug("===========LSF :Resending Exchange Account Creation : " + gson.toJson(pendingActivity));
        CommonResponse commonResponse = new CommonResponse();
        TradingAcc lsfTradingAccount = lsfCoreService.getLsfTypeTradinAccountForUser(
                pendingActivity.getUserID(),
                pendingActivity.getApplicationID());
        AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
        createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
        createExchangeAccount.setTradingAccountId(lsfTradingAccount.getAccountId());
        createExchangeAccount.setExchange(lsfTradingAccount.getExchange());
        String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createExchangeAccount));
        logger.debug("===========LSF : Creating Exchange Account for Trading Account :"
                     + createExchangeAccount.getTradingAccountId()
                     + " OMS Response  :"
                     + omsResponseForExchangeAccountCreation);
        CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(
                omsResponseForExchangeAccountCreation);
        if (exchangeAccountResponse.getResponseCode() == 1) {
            lsfRepository.updateActivity(
                    pendingActivity.getApplicationID(),
                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Exchange Account Creation Request Sent.");
        } else {
            lsfRepository.updateActivity(
                    pendingActivity.getApplicationID(),
                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Exchange Account Creation Request Failed to Send OMS.");
            commonResponse.setErrorMessage("Exchange Account Creation Request Failed to Send OMS.");
        }
        logger.debug("===========LSF :Resending Exchange Account Creation ,Response: " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    private String resendAccountDeletion(PendingActivityRequest pendingActivity) {
        logger.debug("===========LSF :Resending Account Deletion : " + gson.toJson(pendingActivity));
        AccountDeletionRequestState accountDeletionRequestState = lsfCoreService.closeLSFAccount(
                pendingActivity.getApplicationID(),
                pendingActivity.getLsfTypeTradingAccount(),
                pendingActivity.getNonLSFTradingAccount(),
                pendingActivity.getLsfTypeCashAccount(),
                pendingActivity.getNonLSFCashAccount());
        CommonResponse commonResponse = new CommonResponse();
        if (accountDeletionRequestState.isSent()) {
            //  lsfRepository.updateActivity(pendingActivity.getApplicationID(), LsfConstants
            //  .STATUS_ACCOUNT_DELETION_REQUEST_SENT_TO_OMS);
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Account Deletion Request Sent.");
        } else {
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage(accountDeletionRequestState.getFailureReason());
            commonResponse.setResponseMessage(accountDeletionRequestState.getFailureReason());
        }
        logger.debug("===========LSF :Resending Account Deletion ,Response: " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    private String resendCollateralTransfer(PendingActivityRequest pendingActivity) {
        logger.debug("===========LSF :Resending Holding Transfer for Contract : " + gson.toJson(pendingActivity));
        boolean isBasketTransferred = false;
        List<PurchaseOrder> orders = lsfRepository.getAllPurchaseOrder(pendingActivity.getApplicationID());
        if (orders != null) {
            PurchaseOrder purchaseOrder = orders.get(0);
            if (purchaseOrder.getBasketTransferState() == LsfConstants.BASKET_TRANSFER_SENT) {
                isBasketTransferred = true;
                logger.debug("=======LSF : Basket Holdings Already Transferred.");
            }
        }
        logger.debug("=======LSF : Transferring Collateral Holdings.");
        MApplicationCollaterals collaterals =
                lsfRepository.getApplicationCompleteCollateral(pendingActivity.getApplicationID());
        CommonResponse commonResponse = new CommonResponse();
        if (collaterals != null) {
            lsfRepository.updateActivity(
                    pendingActivity.getApplicationID(),
                    LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
            CommonResponse response = (CommonResponse) lsfCoreService.transferCollaterals(collaterals);
            if (response.getResponseCode() == 200) {
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage("Request Resent.");
            } else {
                commonResponse.setResponseCode(500);
                commonResponse.setResponseMessage("Collateral Transfer Resend Failed.");
                commonResponse.setErrorMessage("Collateral Transfer Resend Failed.");
            }
        } else {
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Collateral Transfer Resend Failed.");
            commonResponse.setErrorMessage("Collateral Transfer Resend Failed.");
            logger.debug("===========LSF :Resending Collateral Holding Transfer , Response " + gson.toJson(
                    commonResponse));
        }

        if (!isBasketTransferred) {
            logger.debug("=====LSF : Resending Basket Transfer.");
            commonResponse = (CommonResponse) lsfCoreService.transferToLsfAccount(
                    pendingActivity.getApplicationID(),
                    pendingActivity.getOrderID());/*---transferring po symbols to lsf account--*/
            if (commonResponse.getResponseCode() == 1) {
                lsfRepository.updateActivity(
                        pendingActivity.getApplicationID(),
                        LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage("Basket Share Transfer Request to OMS");
            } else {
                commonResponse.setResponseCode(500);
                commonResponse.setResponseMessage(commonResponse.getResponseMessage());
                commonResponse.setErrorMessage(commonResponse.getResponseMessage());
            }
            logger.debug("===========LSF :Resending Basket Share Transfer , Response " + gson.toJson(commonResponse));
        }
        logger.debug("===========LSF :Resending Collateral Transfer ,Response: " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }

    private String resendBasketShareTransfer(PendingActivityRequest pendingActivity) {
        logger.debug("===========LSF :Resending Basket Share Transfer : " + gson.toJson(pendingActivity));
        CommonResponse commonResponse = new CommonResponse();
        CommonResponse shareTransferResponse = null;
        shareTransferResponse = (CommonResponse) lsfCoreService.transferToLsfAccount(
                pendingActivity.getApplicationID(),
                pendingActivity.getOrderID());/*---transferring po symbols to lsf account--*/
        if (shareTransferResponse.getResponseCode() == 1) {
            lsfRepository.updateActivity(
                    pendingActivity.getApplicationID(),
                    LsfConstants.STATUS_BASKET_SHARE_TRANSFER_REQUEST_SENT_TO_OMS);
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Basket Share Transfer Request to OMS");
        } else {
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage(shareTransferResponse.getResponseMessage());
            commonResponse.setErrorMessage(shareTransferResponse.getResponseMessage());
        }
        logger.debug("===========LSF :Resending Basket Share Transfer , Response " + gson.toJson(commonResponse));
        return gson.toJson(commonResponse);
    }
}
