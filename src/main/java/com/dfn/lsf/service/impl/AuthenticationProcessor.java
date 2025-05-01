package com.dfn.lsf.service.impl;

import java.util.List;
import java.util.Map;

import com.dfn.lsf.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.responseMsg.AuthResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.security.SessionValidator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_AUTHORIZATION_PROCESS;

/**
 * Processor for authentication operations
 * This replaces the AKKA AuthenticationProcessor
 */
@Service
@MessageType(MESSAGE_TYPE_AUTHORIZATION_PROCESS)
@RequiredArgsConstructor
public class AuthenticationProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProcessor.class);
    
    private final LSFRepository lsfRepository;
    
    private final Gson gson;

    private final Helper helper;

    private final SessionValidator sessionValidator;

    private final NotificationManager notificationManager;

    private final LsfCoreService lsfCore;

    @Override
    public String process(String request) {
        String rawMessage = (String) request;
        Map<String, Object> map = gson.fromJson(rawMessage, new TypeToken<Map<String, Object>>() {}.getType());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                return switch (subMessageType) {
                    case "initialLoginReq" -> initializeCustomerLogin(map);
                    case "initialLoginReqAdmin" -> initializeCustomerLoginAdmin(map);
                    case "commonApprove" -> gson.toJson(commonAuthorizeProcess(map));
                    default -> {
                        CommonResponse cmr = new CommonResponse();
                        cmr.setResponseCode(500);
                        cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
                        yield gson.toJson(cmr);
                    }
                };
            }
            CommonResponse response = new CommonResponse();
            response.setResponseCode(401);
            response.setErrorMessage("Invalid or expired session");
            return gson.toJson(response);
        } catch (Exception ex) {
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            return gson.toJson(cmr);
        }
    }

    private String initializeCustomerLogin(Map<String, Object> map) {
        List<MurabahApplication> applicationList = null;
        int statusCode = 200;
        String securityKey = "";
        String corellationID = "";
        String ipAddress = "";
        String ssoToken = null;
        String customerId = "";
        String omsSessionID = "";
        int channelId = 0;
        CommonInqueryMessage ssoValidationRequest = new CommonInqueryMessage();
        CommonResponse cmr = new CommonResponse();

        if (map.containsKey("ipAddress")) {
            ipAddress = map.get("ipAddress").toString();
        }
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        if (map.containsKey("channelId")) {
            channelId = Integer.parseInt(map.get("channelId").toString());
        }
        if (map.containsKey("customerId")) {
            customerId = map.get("customerId").toString();
        }
        /*----------SSO----------------*/
        if (map.containsKey("ssoToken")) {
            ssoToken = map.get("ssoToken").toString();
            logger.info("===========LSF : (initialLoginReqSSO)-REQUEST RECEIVED , Customer ID " + customerId + " , SSO Token:" + ssoToken);

        }
        ssoValidationRequest.setReqType(LsfConstants.VALIDATE_SSO_TOKEN);
        ssoValidationRequest.setParams(ssoToken);
        String ssoValidationResponse = helper.validateSSO(gson.toJson(ssoValidationRequest));
        if (ssoValidationResponse == null) {
            cmr.setResponseCode(-777);
            cmr.setErrorMessage("Error While Validating the SSO Token");
            cmr.setErrorCode(LsfConstants.ERROR_ERROR_WHILE_VALIDATING_THE_SSO_TOKEN);
        } else {
            String delimitter = "\\|\\|";
            String[] resultArray = ssoValidationResponse.split(delimitter);
            if (resultArray[0].equalsIgnoreCase("1")) {
                customerId = resultArray[1];
                omsSessionID = resultArray[2];
                try {
                    AuthResponse response = new AuthResponse();
                    applicationList = lsfRepository.geMurabahAppicationUserIDFilteredByClosedApplication(customerId);
                    if (applicationList.isEmpty()) {  /*----If first Login Create a new Session---*/
                        response.setCustomerId(customerId);
                        response.setLastLoginTime("");
                        response.setUserName("Guest");
                        response.setCurrentLevel(1);
                        response.setOverallStatus(0);
                        if (channelId == LsfConstants.APP_LSF_CLIENT) {
                            securityKey = sessionValidator.createSession(customerId,LsfConstants.APP_LSF_CLIENT, ipAddress, omsSessionID);
                            response.setSecurityKey(securityKey);
                        }
                        response.setIsFirstTime(1);
                    } else {
                        MurabahApplication prApplicaiton=isPermenentRejectAppAvailable(applicationList);
                        if (prApplicaiton!=null) { /*---If User is Black Listed Permanently---*/
                            List<Status> statusList = null;
                            statusList = lsfRepository.getApplicationPermanentlyRejectedReason(prApplicaiton.getId());
                            if (statusList != null && statusList.size() > 0) {
                                cmr.setErrorMessage(statusList.get(0).getStatusMessage());
                            }
                            statusCode = LsfConstants.PREVIOUS_APP_PERMANTLY_REJECTED;
                            response.setPreviousApplicationStatus(LsfConstants.PREVIOUS_APP_PERMANTLY_REJECTED);
                        }
                        else { /*---If Application is normal---*/
                            response.setApplicationList(applicationList);
                            response.setCustomerId(customerId);
                            response.setLastLoginTime("");
                            response.setUserName(applicationList.get(0).getFullName());
                            if (channelId == LsfConstants.APP_LSF_CLIENT) {
                                securityKey = sessionValidator.createSession(customerId,LsfConstants.APP_LSF_CLIENT, ipAddress, omsSessionID);
                                response.setSecurityKey(securityKey);
                            }
                            response.setIsFirstTime(0);
                        }
                    }
                    setCurrentStatusToAuthResponse(response,customerId);
                    response.setDefaultCurrency(GlobalParameters.getInstance().getBaseCurrency());//setting the default currency
                    response.setNumberOfDecimalPlaces(GlobalParameters.getInstance().getNumberOfDecimalPlaces());
                    response.setIsMultipleOrderAllowed(GlobalParameters.getInstance().isMultipleOrderAllowed());
                    response.setMinimumOrderValue(GlobalParameters.getInstance().getMinimumOrderValue());
                    response.setMinLoanLimit(GlobalParameters.getInstance().getMinGuidanceLimit());
                    response.setMaxLoanLimit(GlobalParameters.getInstance().getMaxGuidanceLimit());
                    response.setQuestionConfigList(lsfRepository.getAllQustionSettings()); // setting risk wavier question config
                    response.setIsOTPEnabled(GlobalParameters.getInstance().getMurabahaOTP());

                    /*---Conditional CR Changes Send the product list*/
                    response.setProductsList(lsfRepository.getMurabahaProducts());

                    cmr.setResponseCode(statusCode);
                    cmr.setResponseObject(response);
                } catch (Exception ex) {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage(ex.getMessage());
                }
            } else if (resultArray[0].equalsIgnoreCase("-1")) {
                cmr.setResponseCode(-777);
                cmr.setErrorMessage("Invalid SSO Token");
                cmr.setErrorCode(LsfConstants.ERROR_INVALID_SSO_TOKEN);
            }
        }
        /*----------SSO----------------*/
        logger.info("===========LSF : (initialLoginReq)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr) + " , CorrelationID:" + corellationID);
        return gson.toJson(cmr);
    }

    private String initializeCustomerLoginAdmin(Map<String, Object> map) {
        List<MurabahApplication> applicationList = null;
        int statusCode = 200;
        String securityKey = "";
        String corellationID = "";
        String ipAddress = "";
        String ssoToken = null;
        String customerId = "";
        String omsSessionID = "11111111";
        int channelId = 0;
        CommonResponse cmr = new CommonResponse();

        if (map.containsKey("ipAddress")) {
            ipAddress = map.get("ipAddress").toString();
        }
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        if (map.containsKey("channelId")) {
            channelId = Integer.parseInt(map.get("channelId").toString());
        }
        if (map.containsKey("customerId")) {
            customerId = map.get("customerId").toString();
        }
        logger.info("===========LSF : (initialLoginReqAdmin)-REQUEST, customerID " + customerId + " , CorrelationID:" + corellationID);

        /*----------No SSO----------------*/
        try {
            AuthResponse response = new AuthResponse();
            applicationList = lsfRepository.geMurabahAppicationUserIDFilteredByClosedApplication(map.get("customerId").toString());
            if (applicationList.isEmpty()) {
                response.setCustomerId(customerId);
                response.setLastLoginTime("");
                response.setUserName("Guest");
                response.setCurrentLevel(1);
                response.setOverallStatus(0);
                if (channelId == LsfConstants.APP_LSF_CLIENT) {
                    securityKey = sessionValidator.createSession(customerId, LsfConstants.APP_LSF_ADMIN,ipAddress, omsSessionID);
                    response.setSecurityKey(securityKey);
                }
                response.setIsFirstTime(1);
            } else {
                MurabahApplication prApplicaiton=isPermenentRejectAppAvailable(applicationList);
                if (prApplicaiton!=null) { /*---If User is Black Listed Permanently---*///  application.getOverallStatus().equalsIgnoreCase("-999")
                    List<Status> statusList = null;
                    statusList = lsfRepository.getApplicationPermanentlyRejectedReason(prApplicaiton.getId());
                    if (statusList != null && statusList.size() > 0) {
                        cmr.setErrorMessage(statusList.get(0).getStatusMessage());
                    }
                    statusCode = LsfConstants.PREVIOUS_APP_PERMANTLY_REJECTED;
                    response.setPreviousApplicationStatus(LsfConstants.PREVIOUS_APP_PERMANTLY_REJECTED);
              }
                else { /*---If Application is normal send the list of application ids---*/
                    response.setApplicationList(applicationList);
                    response.setCustomerId(customerId);
                    response.setLastLoginTime("");
                    response.setUserName(applicationList.get(0).getFullName());
                    if (channelId == LsfConstants.APP_LSF_CLIENT) {
                        securityKey = sessionValidator.createSession(customerId,LsfConstants.APP_LSF_ADMIN, ipAddress, omsSessionID);
                        response.setSecurityKey(securityKey);
                    }
                    response.setIsFirstTime(0);
                }
            }
            setCurrentStatusToAuthResponse(response,customerId);
            response.setDefaultCurrency(GlobalParameters.getInstance().getBaseCurrency());//setting the default currency
            response.setNumberOfDecimalPlaces(GlobalParameters.getInstance().getNumberOfDecimalPlaces());
            response.setIsMultipleOrderAllowed(GlobalParameters.getInstance().isMultipleOrderAllowed());
            response.setMinimumOrderValue(GlobalParameters.getInstance().getMinimumOrderValue());
            response.setMinLoanLimit(GlobalParameters.getInstance().getMinGuidanceLimit());
            response.setMaxLoanLimit(GlobalParameters.getInstance().getMaxGuidanceLimit());
            response.setQuestionConfigList(lsfRepository.getAllQustionSettings()); // setting risk wavier question config
            response.setIsOTPEnabled(false);/*--Regardless the OTP is enabled Global or not disable OTP for Admin channel--*/
            response.setMaximumRetryCount(GlobalParameters.getInstance().getMaximumRetryCount());

            /*---Conditional CR Changes Send the product list*/
            response.setProductsList(lsfRepository.getMurabahaProducts());


            cmr.setResponseCode(statusCode);
            cmr.setResponseObject(response);
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        logger.info("===========LSF : (initialLoginReqAdmin)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr) + " , CorrelationID:" + corellationID);
        return gson.toJson(cmr);
    }

    private MurabahApplication isPermenentRejectAppAvailable(List<MurabahApplication> applicationsList){
        for(MurabahApplication application:applicationsList){
            return application.getOverallStatus().equalsIgnoreCase("-999")? application:null;
        }
        return null;
    }

    private CommonResponse commonAuthorizeProcess(Map<String, Object> objMap) {

        CommonResponse cmr = new CommonResponse();
        String corellationID = "";

        if (objMap.containsKey("corellationID")) {
            corellationID = objMap.get("corellationID").toString();
        }

        if(objMap.containsKey("ipAddress")){
            String statusChangedIP = objMap.get("ipAddress").toString();
            if(LSFUtils.validateAdminApproveAction(statusChangedIP)) {
                List<MurabahApplication> applicationList = null;
                try {
                    applicationList = lsfRepository.getMurabahAppicationApplicationID(objMap.get("id").toString());
                    if (!applicationList.isEmpty()) {
                        MurabahApplication fromDB = applicationList.get(0);
                        if (fromDB != null) {
                            int appStatus = Integer.parseInt(objMap.get("approvalStatus").toString());
                            int currentLevel=Integer.parseInt(objMap.get("currentLevel").toString());
                            if(currentLevel!=fromDB.getCurrentLevel()){
                                cmr.setResponseCode(500);
                                cmr.setErrorMessage("Application is already approved");
                                logger.info("===========LSF : (commonAuthorization)application  :" + objMap.get("id").toString() + " ,current Level :" + currentLevel + " Already approved.");
                                return cmr;
                            }
                            String statusMessage = "";
                            String statusChangedUserid = "";
                            String statusChangedUserName = "";
                            String responseMessage = "";

                            if (objMap.containsKey("statusMessage"))
                                statusMessage = objMap.get("statusMessage").toString();
                            if (objMap.containsKey("userid")) {
                                statusChangedUserid = objMap.get("userid").toString();
                            }
                            if (objMap.containsKey("username")) {
                                statusChangedUserName = objMap.get("username").toString();
                            }
                            logger.info("===========LSF : (commonAuthorization)REQUEST, UserID" + statusChangedUserid + " , StatusMessage:" + statusMessage + ",  IPAddress:" + statusChangedIP);
                            responseMessage = lsfRepository.approveApplication(appStatus, fromDB.getId(), statusMessage, statusChangedUserid, statusChangedUserName, statusChangedIP);
                            if (appStatus < 0 && appStatus != -1) {
                                CommonInqueryMessage blackListRequest = new CommonInqueryMessage();
                                blackListRequest.setReqType(LsfConstants.BLACK_LIST_CUSTOMER);
                                blackListRequest.setCustomerId(fromDB.getCustomerId());
                                blackListRequest.setChangeParameter(1);
                                blackListRequest.setValue("1");
                                blackListRequest.setParams("Customer need to be Black Listed");
                                logger.info("===========LSF : Sending Black List Request to OMS:" + gson.toJson(blackListRequest));
                                String omsResponse = helper.omsCommonRequests(gson.toJson(blackListRequest));
                                logger.info("===========LSF : OMS Response to  Black List Request :" + omsResponse);

                            }

                            notificationManager.sendNotification(fromDB);
                            cmr.setResponseCode(200);
                            cmr.setResponseMessage(responseMessage);
                        }
                    } else {
                        cmr.setResponseCode(500);
                        cmr.setErrorMessage(ErrorCodes.ERROR_AUTHORIZATION_CP.errorDescription());
                    }
                } catch (Exception ex) {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
                }
                logger.info("===========LSF : (commonAuthorization)LSF-SERVER RESPONSE  :" + gson.toJson(cmr) + " , CorrelationID:" + corellationID);
                return cmr;
            }else{
                cmr.setResponseCode(500);
                cmr.setResponseMessage("Abnormal Activity");
                cmr.setErrorMessage("Abnormal Activity");
                cmr.setErrorCode(LsfConstants.ERROR_ABNORMAL_ACTIVITY);
                logger.info("===========LSF : (commonAuthorization)LSF-SERVER RESPONSE  :" + gson.toJson(cmr) + " , CorrelationID:" + corellationID);
                return cmr;
            }


        }else{
            cmr.setResponseCode(500);
            cmr.setResponseMessage("IP Address is not detected");
            cmr.setErrorMessage("IP Address is not detected");
            cmr.setErrorCode(LsfConstants.ERROR_IP_ADDRESS_IS_NOT_DETECTED);
            logger.info("===========LSF : (commonAuthorization)LSF-SERVER RESPONSE  :" + gson.toJson(cmr) + " , CorrelationID:" + corellationID);
            return cmr;
        }

    }

    private void setCurrentStatusToAuthResponse(AuthResponse authResponse,String cutomerId){
        int unclosedApps=lsfCore.getNoOfOpenMurabahContracts(cutomerId);
        int penidingApps=lsfCore.getnoOfPendingMurabahContrats(cutomerId);
        int activeApps=unclosedApps>0?unclosedApps-penidingApps:0;
        authResponse.setNoOfGrantedContracts(activeApps);
        authResponse.setNoOfPendingContracts(penidingApps);
        authResponse.setMaxNoOfGrantedContracts(GlobalParameters.getInstance().getMaxNumberOfActiveContracts());
    }
}