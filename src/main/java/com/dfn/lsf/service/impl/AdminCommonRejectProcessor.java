package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.MessageType;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_ADMIN_COMMON_REJECT;

/**
 * Defined in InMessageHandlerAdminCbr
 * Handling Message types :
 * - MESSAGE_TYPE_ADMIN_COMMON_REJECT = 26
 */
@Service
@MessageType(MESSAGE_TYPE_ADMIN_COMMON_REJECT)
@RequiredArgsConstructor
public class AdminCommonRejectProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AdminCommonRejectProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;

    @Override
    public String process(String request) {
        String rawMessage = (String) request;
        logger.debug("====LSF : AdminCommonRejectProcessor , Received Message:" + rawMessage);
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(rawMessage, map.getClass());
        if (map.containsKey("subMessageType")) {
            String subMessageType = map.get("subMessageType").toString();
            switch (subMessageType) {
                case "getApplicationListForAdminReject":
                    return getApplicationListForAdminReject();/*---Get Application For Admin Reject---*/
                case "rejectAppAdmin":
                    return rejectApplication(map);
            }
        }
        return null;
    }

    private String getApplicationListForAdminReject() {
        String response = "No Applications";
        List<MurabahApplication> applications = lsfRepository.getApplicationListForAdminCommonReject();
        if (applications != null) {
            response = gson.toJson(applications);
        }
        logger.debug("===========LSF : (getApplicationListForAdminReject)-LSF-SERVER RESPONSE  : " + response);
        return response;
    }

    private String rejectApplication(Map<String, Object> map) {

        CommonResponse response = new CommonResponse();

        String applicationID = null;
        String purchaseOrderId = null;
        String statusMessage = "Admin Reject";
        String statusChangedUserid = "Admin";
        String statusChangedIP = "127.0.0.1";
        String statusChangedUserName = "Admin";
        MurabahApplication murabahApplication = null;
        boolean deleteAccounts = false;

        if (map.containsKey("id")) {
            applicationID = map.get("id").toString();
        }

        if (map.containsKey("purchaseOrderId")) {
            purchaseOrderId = map.get("purchaseOrderId").toString();
        }

        if (map.containsKey("statusMessage")) {
            statusMessage = map.get("statusMessage").toString();
        }

        if (map.containsKey("userid")) {
            statusChangedUserid = map.get("userid").toString();
        }

        if (map.containsKey("username")) {
            statusChangedUserName = map.get("username").toString();
        }

        if (map.containsKey("ipAddress")) {
            statusChangedIP = map.get("ipAddress").toString();
        }

        if (applicationID != null) {

            if (purchaseOrderId != null
                && !purchaseOrderId.equalsIgnoreCase("")) { /*-If the user has a purchaseOrder-*/
                if (purchaseOrderId.contains(".0")) {
                    purchaseOrderId = purchaseOrderId.replace(".0", "");
                }
                CommonInqueryMessage omsInqueryMessage = new CommonInqueryMessage();
                omsInqueryMessage.setReqType(LsfConstants.CANCEL_PENDING_ML_BASKETS);
                omsInqueryMessage.setBasketReference(purchaseOrderId);
                CommonResponse basketDeletionOMSResponse = (CommonResponse) helper.cancelMLPendingBaskets(gson.toJson(
                        omsInqueryMessage));
                if (basketDeletionOMSResponse.getResponseCode()
                    == 200) { //*--If Basket is successfully deleted in OMS--*//*
                    MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
                    CommonResponse collateralReleaseResponse = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                    deleteAccounts = collaterals.isLSFCashAccExist() || collaterals.isLSFTradingAccExist();
                    if (collateralReleaseResponse.getResponseCode() == 200) {
                        String responseMessage = lsfRepository.approveApplication(
                                -1,
                                applicationID,
                                statusMessage,
                                statusChangedUserid,
                                statusChangedUserName,
                                statusChangedIP);
                        response.setResponseCode(200);
                        response.setResponseMessage(responseMessage);
                    } else {
                        response.setResponseCode(500);
                        response.setErrorMessage("Failed during Collateral Release.");
                        response.setResponseMessage("Failed during Collateral Release.");
                        response.setErrorCode(LsfConstants.ERROR_FAILED_DURING_COLLATERAL_RELEASE);
                    }
                } else {//*--If basket deletion is failed in OMS---*//*
                    response.setResponseCode(500);
                    response.setErrorMessage(
                            "You cannot cancel the request with open orders. Please cancel the open orders to cancel "
                            + "the ML request");//basketDeletionOMSResponse.getErrorMessage());
                    response.setErrorCode(LsfConstants.ERROR_YOU_CANNOT_CANCEL_THE_REQUEST_WITH_OPEN_ORDERS);
                    //basketDeletionOMSResponse.getErrorMessage());
                }
//                response.setResponseCode(500);
//                response.setErrorMessage("Not Yet Implemented");
//                response.setResponseMessage("Not Yet Implemented.");

            } else {
                MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
                CommonResponse collateralReleaseResponse = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                deleteAccounts = collaterals.isLSFCashAccExist() || collaterals.isLSFTradingAccExist();
                if (collateralReleaseResponse.getResponseCode() == 200) {
                    String responseMessage = lsfRepository.approveApplication(
                            -1,
                            applicationID,
                            statusMessage,
                            statusChangedUserid,
                            statusChangedUserName,
                            statusChangedIP);
                    response.setResponseCode(200);
                    response.setResponseMessage(responseMessage);
                } else {
                    response.setResponseCode(500);
                    response.setErrorMessage("Failed during Collateral Release.");
                    response.setErrorCode(LsfConstants.ERROR_FAILED_DURING_COLLATERAL_RELEASE);
                    response.setResponseMessage("Failed during Collateral Release.");
                }
            }
            if (response.getResponseCode() == 200) {
                murabahApplication = lsfRepository.getMurabahApplication(applicationID);
                if (deleteAccounts) {
                    // sending account deletetion request
                    deleteLSFAccount(murabahApplication);
                }
                if (murabahApplication != null) {
                    notificationManager.sendCommonRejectNotifications(murabahApplication);
                }
            }
        } else {
            response.setResponseCode(500);
            response.setErrorMessage("Error while processing the request.");
            response.setErrorCode(LsfConstants.ERROR_ERROR_WHILE_PROCESSING_THE_REQUEST);
        }
        logger.debug("===========LSF : (rejectAppAdmin)-LSF-SERVER RESPONSE  : " + gson.toJson(response));

        return gson.toJson(response);
    }

    private void deleteLSFAccount(MurabahApplication murabahApplication) {
        TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(
                murabahApplication.getCustomerId(),
                murabahApplication.getId());
        CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(
                murabahApplication.getCustomerId(),
                murabahApplication.getId());
        try {
            if (lsfCashAccount != null && lsfTradingAcc != null) {
                AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
                accountDeletionRequestState = lsfCore.closeLSFAccount(
                        murabahApplication.getId(),
                        lsfTradingAcc.getAccountId(),
                        murabahApplication.getTradingAcc(),
                        lsfCashAccount.getAccountId(),
                        murabahApplication.getCashAccount());
            }
        } catch (Exception ex) {

        }
    }
}
