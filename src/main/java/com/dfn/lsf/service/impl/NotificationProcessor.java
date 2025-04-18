package com.dfn.lsf.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.util.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.notification.Message;
import com.dfn.lsf.model.notification.NotificationMsgConfiguration;
import com.dfn.lsf.model.notification.WebNotification;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.ErrorCodes;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_NOTIFICATION_PROCESSOR;

/**
 * Processor for notification operations
 * This replaces the AKKA NotificationProcessor
 */
@Service
@MessageType(MESSAGE_TYPE_NOTIFICATION_PROCESSOR)
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    
    private final Gson gson;

    private final NotificationManager notificationManager;
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing notification request with subMessageType: {}", subMessageType);
            
            switch (subMessageType) {
                case LsfConstants.SENT_NOTIFICATION_FOR_A_CUSTOMER:
                return sentNotificationToCustomer(requestMap); // admin terminal send customer ids, subject and body for send sms,email and web messages
                case LsfConstants.SENT_NOTIFICATION_FOR_GROUP:
                    return sentNotificationToGroup(requestMap); // admin terminal send current level and over role status according that find customers and send sms,email and web messages
                case LsfConstants.GET_CUSTOM_MESSAGE_HISTORY:
                    return getCustomMessageHistory();
                case LsfConstants.REQ_CLIENT_WEB_NOTIFICATIONS:
                    return sendClientWebNotifications(requestMap.get("id").toString()); // send web notification
                case LsfConstants.UPDATE_CLIENT_READ_NOTIFICATIONS:
                    return updateReadWebNotification(requestMap.get("id").toString(), requestMap.get("messageId").toString()); // update status for read web notification
                case LsfConstants.REQ_MSG_CONFIGURATION:
                    return getMessageConfiguration(requestMap); // get Automated messages configuration
                case LsfConstants.REQ_MSG_CONFIGURATION_LIST:
                    return getMessageConfigurationList(requestMap);
                case LsfConstants.SAVE_MSG_CONFIGURATION:
                    return saveMessageConfiguration(requestMap); //save Automated messages configuration
                case LsfConstants.UPDATE_MSG_CONFIGURATION:
                    return updateMessageConfiguration(requestMap); //update Automated web messages configuration only
                case LsfConstants.VIEW_MESSAGE_HISTORY: // view the message History - Admin
                    return viewCustomMessageHistory();
                case LsfConstants.GET_NOTIFICATION_HISTORY: //get notification history - Admin
                    return getNotificationHistory();
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error processing notification request", e);
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            return gson.toJson(cmr);
        }
    }
    
    private String updateReadWebNotification(String applicationId, String messageId) {
        CommonResponse cmr = new CommonResponse();
        try {
            lsfRepository.updateReadWebNotification(applicationId, messageId);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("updated");
        } catch (Exception e) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(e.getMessage());
        }
        return gson.toJson(cmr);
    }

    private String sendClientWebNotifications(String applicationId) {
        CommonResponse cmr = new CommonResponse();
        try {
            List<WebNotification> result = lsfRepository.getWebNotification(applicationId);
            cmr.setResponseCode(200);
            cmr.setResponseObject(result);
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        return gson.toJson(cmr);
    }

    private String sentNotificationToGroup(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        int statusCode = 200;
        String statusMessage = "Failed to send : ";
        Boolean result = false;
        String sentBy = "System";
        List<String> applicationList;
        boolean isSMS = false;
        boolean isEmail = false;
        boolean isWebNotification = false;
        String notificationType = "";
        if (map.containsKey("applications")) {
            applicationList = (List<String>) map.get("applications");
            if (map.containsKey("type")) {
                String type = map.get("type").toString();
                if (type.equalsIgnoreCase("1")) {
                    isSMS = true;
                    notificationType = "SMS";
                } else if (type.equalsIgnoreCase("2")) {
                    isEmail = true;
                    notificationType = "EMAIL";
                } else if (type.equalsIgnoreCase("3")) {
                    isSMS = true;
                    isEmail = true;
                    notificationType = "SMS & EMAIL";
                } else if (type.equalsIgnoreCase("4")) {
                    isWebNotification = true;
                    notificationType = "WEB";
                } else if (type.equalsIgnoreCase("5")) {
                    isSMS = true;
                    isWebNotification = true;
                    notificationType = "SMS & WEB";
                } else if (type.equalsIgnoreCase("6")) {
                    isEmail = true;
                    isWebNotification = true;
                    notificationType = "EMAIL & WEB";
                } else if (type.equalsIgnoreCase("7")) {
                    isSMS = true;
                    isEmail = true;
                    isWebNotification = true;
                    notificationType = "SMS & EMAIL & WEB";
                }
            }
            NotificationMsgConfiguration msgConfiguration = new NotificationMsgConfiguration();
            if (map.containsKey("subject")) {
                msgConfiguration.setEmailSubject((String) map.get("subject"));
                msgConfiguration.setWebSubject((String) map.get("subject"));
            }
            msgConfiguration.setSmsTemplate((String) map.get("body"));
            msgConfiguration.setEmailBody((String) map.get("body"));
            msgConfiguration.setWebBody((String) map.get("body"));
            msgConfiguration.setSms(isSMS);
            msgConfiguration.setMail(isEmail);
            msgConfiguration.setWeb((isWebNotification));
            if (map.containsKey("createdBy")) {
                sentBy = (String) map.get("createdBy");
            }
            for(String applicationID : applicationList){
                List<MurabahApplication> murabahApplications = null;
                murabahApplications =  lsfRepository.getMurabahAppicationApplicationID(applicationID);
                if(murabahApplications != null && murabahApplications.size() > 0){
                    if(notificationManager.sendNotification(msgConfiguration, murabahApplications.get(0), sentBy)){

                    }else{
                        statusMessage = statusMessage + "" + applicationID + ",";
                        log.error("===========LSF : Message Sender ,Failed to Send Message , application ID :" + applicationID);
                    }
                }else{
                    statusMessage = statusMessage + "" + applicationID + ",";
                    log.error("===========LSF : Message Sender , Can't find application, application ID : " + applicationID + "sending failed.");
                }
            }
        }else{
            statusCode = 500;
            statusMessage = "Invalid Request";
        }
        commonResponse.setResponseCode(statusCode);
        commonResponse.setResponseMessage(statusMessage);
        log.debug("===========LSF : Message Sender , Response:" + gson.toJson(commonResponse)) ;
        return gson.toJson(commonResponse);
    }

    private String sentNotificationToCustomer(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        Boolean results = false;
        List<String> customerIds = (List<String>) map.get("customerIds");
        if (customerIds.size() != 0) {
            for (String customerId : customerIds) {
                List<MurabahApplication> murabahApplications = lsfRepository.geMurabahAppicationUserID(customerId);
                if (murabahApplications != null && murabahApplications.size()!=0) {
                    String sentBy = null;
                    MurabahApplication murabahApplication = murabahApplications.get(0);
                    NotificationMsgConfiguration msgConfiguration = new NotificationMsgConfiguration();
                    msgConfiguration.setSmsTemplate((String) map.get("text"));
                    msgConfiguration.setEmailSubject((String) map.get("subject"));
                    msgConfiguration.setEmailBody((String) map.get("text"));
                    msgConfiguration.setWebSubject((String) map.get("subject"));
                    msgConfiguration.setWebBody((String) map.get("text"));
                    msgConfiguration.setSms((Boolean) map.get("isSms"));
                    msgConfiguration.setMail((Boolean) map.get("isMail"));
                    msgConfiguration.setWeb((Boolean) map.get("isWeb"));
                    if (map.containsKey("sentBy")){
                        sentBy = (String) map.get("sentBy");
                    }
                    results = notificationManager.sendNotification(msgConfiguration, murabahApplication, sentBy);
                }else {
                    results = false;
                }
            }
        }

        if (results) {
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Notification sent successfully");
            return gson.toJson(commonResponse);
        } else {
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Notification Error");
            return gson.toJson(commonResponse);
        }
    }

    private String getCustomMessageHistory() {
        List<Message> messageList = lsfRepository.getCustomMessageHistory();
        return gson.toJson(messageList);
    }

    private String getNotificationHistory(){
        List<Message> messageList = lsfRepository.getNotificationHistory();
        return gson.toJson(messageList);
    }

    private String getMessageConfiguration(Map<String, Object> map) {
        List<NotificationMsgConfiguration> msgConfigurations = lsfRepository.getNotificationMsgConfigurationForLevelStatus((String) map.get("currentLevel"), (String) map.get("overRoleStatus"));
        NotificationMsgConfiguration msgConfiguration = null;
        Map<String, Object> msgConStringMap = new HashMap<>();
        if (msgConfigurations != null && msgConfigurations.size() != 0) {
            msgConfiguration = msgConfigurations.get(0);
            msgConStringMap.put("id",msgConfiguration.getId());
            msgConStringMap.put("currentLevel",msgConfiguration.getCurrentLevel());
            msgConStringMap.put("overRoleStatus",msgConfiguration.getOverRoleStatus());
            msgConStringMap.put("subject",msgConfiguration.getWebSubject());
            msgConStringMap.put("text",msgConfiguration.getWebBody());
            msgConStringMap.put("isSms", msgConfiguration.isSms());
            msgConStringMap.put("isMail", msgConfiguration.isMail());
            msgConStringMap.put("isWeb", msgConfiguration.isWeb());
        }
        return gson.toJson(msgConStringMap);
    }

    private String getMessageConfigurationList(Map<String, Object> map) {
        List<NotificationMsgConfiguration> msgConfigurations = lsfRepository.getNotificationMsgConfiguration();
        List<Map<String, Object>> msgConList = new ArrayList<>();
        for(NotificationMsgConfiguration msgConfiguration : msgConfigurations){
            Map<String, Object> msgConMap = new HashMap<>();
            msgConMap.put("id",msgConfiguration.getId());
            msgConMap.put("currentLevel",msgConfiguration.getCurrentLevel());
            msgConMap.put("overRoleStatus",msgConfiguration.getOverRoleStatus());
            msgConMap.put("subject",msgConfiguration.getWebSubject());
            msgConMap.put("text",msgConfiguration.getWebBody());
            msgConMap.put("isSms",msgConfiguration.isSms());
            msgConMap.put("isMail",msgConfiguration.isMail());
            msgConMap.put("isWeb",msgConfiguration.isWeb());
            msgConList.add(msgConMap);
        }
        return gson.toJson(msgConList);
    }

    private String saveMessageConfiguration(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        NotificationMsgConfiguration msgConfiguration = new NotificationMsgConfiguration();
        msgConfiguration.setId(String.valueOf(System.currentTimeMillis()));
        msgConfiguration.setCurrentLevel((String) map.get("currentLevel"));
        msgConfiguration.setOverRoleStatus((String) map.get("overRoleStatus"));
        msgConfiguration.setSmsTemplate((String) map.get("text"));
        msgConfiguration.setWebSubject((String) map.get("subject"));
        msgConfiguration.setWebBody((String) map.get("text"));
        msgConfiguration.setEmailSubject((String) map.get("subject"));
        msgConfiguration.setEmailBody((String) map.get("text"));
        msgConfiguration.setSms((Boolean) map.get("isSms"));
        msgConfiguration.setMail((Boolean) map.get("isMail"));
        msgConfiguration.setWeb((Boolean) map.get("isWeb"));
        try {
            lsfRepository.updateNotificationMsgConfig(msgConfiguration);
            Map<String, Object> msgConStringMap = new HashMap<>();
            msgConStringMap.put("id",msgConfiguration.getId());
            msgConStringMap.put("currentLevel",msgConfiguration.getCurrentLevel());
            msgConStringMap.put("overRoleStatus",msgConfiguration.getOverRoleStatus());
            msgConStringMap.put("subject",msgConfiguration.getWebSubject());
            msgConStringMap.put("text",msgConfiguration.getWebBody());
            msgConStringMap.put("isSms", msgConfiguration.isSms());
            msgConStringMap.put("isMail", msgConfiguration.isMail());
            msgConStringMap.put("isWeb", msgConfiguration.isWeb());
            return gson.toJson(msgConStringMap);
        } catch (Exception e) {
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Database connection fail");
        }
        return gson.toJson(commonResponse);
    }

    //Use for save and update message configuration
    private String updateMessageConfiguration(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        NotificationMsgConfiguration msgConfiguration = new NotificationMsgConfiguration();
        msgConfiguration.setId((String) map.get("id"));
        msgConfiguration.setCurrentLevel((String) map.get("currentLevel"));
        msgConfiguration.setOverRoleStatus((String) map.get("overRoleStatus"));
        msgConfiguration.setSmsTemplate((String) map.get("text"));
        msgConfiguration.setWebSubject((String) map.get("subject"));
        msgConfiguration.setWebBody((String) map.get("text"));
        msgConfiguration.setEmailSubject((String) map.get("subject"));
        msgConfiguration.setEmailBody((String) map.get("text"));
        msgConfiguration.setSms((Boolean) map.get("isSms"));
        msgConfiguration.setMail((Boolean) map.get("isMail"));
        msgConfiguration.setWeb((Boolean) map.get("isWeb"));
        try {
            lsfRepository.updateNotificationMsgConfig(msgConfiguration);
            Map<String, Object> msgConStringMap = new HashMap<>();
            msgConStringMap.put("id",msgConfiguration.getId());
            msgConStringMap.put("currentLevel",msgConfiguration.getCurrentLevel());
            msgConStringMap.put("overRoleStatus",msgConfiguration.getOverRoleStatus());
            msgConStringMap.put("subject",msgConfiguration.getWebSubject());
            msgConStringMap.put("text",msgConfiguration.getWebBody());
            msgConStringMap.put("isSms", msgConfiguration.isSms());
            msgConStringMap.put("isMail", msgConfiguration.isMail());
            msgConStringMap.put("isWeb", msgConfiguration.isWeb());
            return gson.toJson(msgConStringMap);
        } catch (Exception e) {
            commonResponse.setResponseCode(500);
            commonResponse.setResponseMessage("Database connection fail");
        }
        return gson.toJson(commonResponse);
    }

    private String viewCustomMessageHistory(){
        List<Message> messageList = lsfRepository.getCustomMessageHistory();
        return gson.toJson(messageList);
    }
}