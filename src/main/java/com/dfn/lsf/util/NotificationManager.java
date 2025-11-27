package com.dfn.lsf.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.model.notification.AdminUser;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.MurabahaProduct;
import com.dfn.lsf.model.OrderProfit;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.notification.Message;
import com.dfn.lsf.model.notification.NotificationMsgConfiguration;
import com.dfn.lsf.model.notification.WebNotification;
import com.dfn.lsf.repository.LSFRepository;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationManager {

 private final LSFRepository lsfRepository;
 private final Gson gson;
 private final Helper helper;
 private  List<AdminUser> adminUsers;

 public void sendNotification(MurabahApplication murabahApplication) {

    log.debug("===========LSF: Sending Notifications , applicationID:" + murabahApplication.getId() + ", Current Level :" + murabahApplication.getCurrentLevel() + " , Status :" + murabahApplication.getOverallStatus());

    int tempNotificationType = 0;
    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = lsfRepository.getNotificationMsgConfigurationForApplication(murabahApplication.getId());
    if (msgConfigurations != null & msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();

        log.debug("===========LSF: Notification, msgConfiguration:" + gson.toJson(msgConfiguration));
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();

        Map<String, String> paramsMap = getApplicationParameterMap(murabahApplication);

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }

        }
        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        message.setSentBy("SYSTEM");
        // String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIL :" + thirdPartyEmail);
            message.setThirdPartyEmail(thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("===========LSF :Can't find the message configurations");

    }
}

public boolean sendNotification(NotificationMsgConfiguration msgConfiguration, MurabahApplication murabahApplication, String sentBy) {
    Boolean results = false;
    int tempNotificationType = 0;
    if (msgConfiguration != null) {
        String subject = msgConfiguration.getWebSubject();
        String body = msgConfiguration.getWebBody();
        Message message = fillMessage(murabahApplication);
        message.setMessage(msgConfiguration.getSmsTemplate());
        message.setCustom(true);
        message.setSentBy(sentBy);
        String stringMessage = gson.toJson(message);

        if (msgConfiguration.isSms()) {
            helper.sendToSMSProducer(stringMessage);
            tempNotificationType += 2;
            results = true;
        }
        if (msgConfiguration.isMail()) {
            helper.sendToEmailProducer(stringMessage);
            tempNotificationType += 3;
            results = true;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(subject);
            msgConfiguration.setWebBody(body);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);
    }
    return results;
}

public void sendSettlementNotification(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder, OrderProfit orderProfit, int dateDifference) {
    try {

            NotificationMsgConfiguration msgConfiguration = null;
            List<NotificationMsgConfiguration> msgConfigurations = null;
            int tempNotificationType = 0;

            if (dateDifference == 0) {
                msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.SETTLEMENT_REMINDER_EXPIRY_DATE);
            } else {
                msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.SETTLEMENT_REMINDER_5_DAYS_REMAIN);
            }

            if (msgConfigurations != null && msgConfigurations.size() > 0) {
                msgConfiguration = msgConfigurations.get(0);
                log.debug("===========LSF; Sending Settlement Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
                String webSubject = msgConfiguration.getWebSubject();
                String webBody = msgConfiguration.getWebBody();
                String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
                String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
                Map<String, String> paramsMap = getParameterMapForSettlementNotification(murabahApplication, purchaseOrder, orderProfit);
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                    if (webSubject != null && webSubject.contains(entry.getKey())) {
                        webSubject = webSubject.replace(entry.getKey(), entry.getValue());
                    }
                    if (webBody != null && webBody.contains(entry.getKey())) {
                        webBody = webBody.replace(entry.getKey(), entry.getValue());
                    }
                    if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                        thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
                    }
                    if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                        thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
                    }
                }

                Message message = fillMessage(murabahApplication);
                message.setSubject(webSubject);
                message.setMessage(webBody);
                message.setCustom(false);
                String stringMessage = gson.toJson(message);
                if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
                    log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
                    message.setThirdPartySMS(thirdPartySMS);
                    log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
                    tempNotificationType += 2;
                }
                if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
                    helper.sendToEmailProducer(thirdPartyEmail);
                    message.setThirdPartyEmail(thirdPartyEmail);
                    tempNotificationType += 3;
                }
                if (msgConfiguration.isWeb()) {
                    msgConfiguration.setWebSubject(webSubject);
                    msgConfiguration.setWebBody(webBody);
                    sendWebNotification(msgConfiguration, murabahApplication);
                    tempNotificationType += 4;
                }
                message.setNotificationType(decideMessageType(tempNotificationType));
                lsfRepository.addMessageOut(message);

            } else {
                log.debug("No configuration found for Settlement Reminder");
            }
    } catch (Exception e) {
        log.info("===========LSF(sendSettlementNotification) error "+ e.getMessage());
    }
}

public void sendEarlySettlementNotification(MurabahApplication murabahApplication) {
    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.EARLY_SETTLEMENT_NOTIFICATION);
    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        log.debug("===========LSF; Sending Early Settlement Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
        Map<String, String> paramsMap = getParameterMapForEarlySettlementNotification(murabahApplication);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (webSubject != null && webSubject.contains(entry.getKey())) {
                webSubject = webSubject.replace(entry.getKey(), entry.getValue());
            }
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }
        }

        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIl :" + thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            message.setThirdPartyEmail(thirdPartyEmail);
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("No configuration found for Settlement Reminder");
    }

}

public void sendNotificationCommodity(MurabahApplication murabahApplication, String notificationType) {
    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(notificationType);
    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        log.debug("===========LSF; Sending Early Settlement Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
        Map<String, String> paramsMap = getApplicationParameterMap(murabahApplication);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (webSubject != null && webSubject.contains(entry.getKey())) {
                webSubject = webSubject.replace(entry.getKey(), entry.getValue());
            }
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }
        }

        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIl :" + thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            message.setThirdPartyEmail(thirdPartyEmail);
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("No configuration found for Settlement Reminder");
    }
}

private Map<String, String> getParameterMapForEarlySettlementNotification(MurabahApplication murabahApplication) {

    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("$customerName", murabahApplication.getFullName());
    paramMap.put("$applicationId", murabahApplication.getDisplayApplicationId());
    paramMap.put("$cifNumber", murabahApplication.getCustomerReferenceNumber());
    paramMap.put("$prefLanguage",murabahApplication.getPreferedLanguage()!=null?murabahApplication.getPreferedLanguage():"E");
    paramMap.put("$lsfTypeTradingAccount", murabahApplication.getTradingAcc());
    paramMap.put("$tradingAccount", murabahApplication.getTradingAcc());
    paramMap.put("$mobileNumber", murabahApplication.getMobileNo());
    return paramMap;
}

private Map<String, String> getParameterMapForSettlementNotification(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder, OrderProfit orderProfit) {
    MApplicationCollaterals completeCollaterals = lsfRepository.getApplicationCompleteCollateral(murabahApplication.getId());
    Map<String, String> paramMap = new HashMap<>();
    String lsfAccount = murabahApplication.getTradingAcc();
    paramMap.put("$customerName", murabahApplication.getFullName());
    paramMap.put("$applicationId", murabahApplication.getDisplayApplicationId());
    paramMap.put("$orderID", purchaseOrder.getId());
    paramMap.put("$settlementDate", String.valueOf(purchaseOrder.getSettlementDate()));
    paramMap.put("$settlementAmount", String.valueOf(purchaseOrder.getOrderCompletedValue() + orderProfit.getCumulativeProfitAmount()));
    paramMap.put("$cifNumber", murabahApplication.getCustomerReferenceNumber());
    paramMap.put("$mobileNumber", murabahApplication.getMobileNo());
    paramMap.put("$prefLanguage",murabahApplication.getPreferedLanguage()!=null?murabahApplication.getPreferedLanguage():"E");
    if (completeCollaterals != null) {
        if (completeCollaterals.getLsfTypeTradingAccounts() != null && completeCollaterals.getLsfTypeTradingAccounts().size() > 0) {
            lsfAccount = completeCollaterals.getLsfTypeTradingAccounts().get(0).getAccountId();
        }

    }
    paramMap.put("$lsfTypeTradingAccount", lsfAccount);
    // paramMap.put("$tradingAccount", murabahApplication.getTradingAcc());

    return paramMap;
}

/*----Send PO Acceptance Reminder--*/
public boolean sendPOAcceptanceReminders(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder, int reminderCounter, boolean isFinal) {
    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;

    int tempNotificationType = 0;
    if (isFinal) {
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.PURCHASE_ORDER_EXPIRED);
    } else {
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.PURCHASE_ORDER_ACCEPTANCE);

    }
    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        log.debug("===========LSF; Sending Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
        Map<String, String> paramsMap = getApplicationParameterMap(murabahApplication);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) { // filling web message , third party sms/email with parameters
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
                // log.debug("====THird Party SMS template: " + thirdPartySMS);
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }

        }
        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        message.setSentBy("SYSTEM");
        // String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIL :" + thirdPartySMS);
            message.setThirdPartyEmail(thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);
        return true;
    } else {
        return false;
    }

}

/*----Send Margin Notification--*/
public boolean sendMarginNotification(int marginLevel, MApplicationCollaterals applicationCollaterals, MurabahApplication application) {

    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    if (marginLevel == 1) {
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.MARGIN_NOTIFICATION_LEVEL1);

    }  else if (marginLevel == 2) {
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.MARGIN_NOTIFICATION_LEVEL2);
    }
     else if (marginLevel == 3) {
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.MARGIN_NOTIFICATION_LEVEL3);
    }
    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        for(NotificationMsgConfiguration msgConfiguration :msgConfigurations){

            log.debug("===========LSF; Sending Margin Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
            String webSubject = msgConfiguration.getWebSubject();
            String webBody = msgConfiguration.getWebBody();
            String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
            String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
            Map<String, String> paramsMap = getParameterMapForMargin(marginLevel, applicationCollaterals, application);
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (webSubject != null && webSubject.contains(entry.getKey())) {
                    webSubject = webSubject.replace(entry.getKey(), entry.getValue());
                }
                if (webBody != null && webBody.contains(entry.getKey())) {
                    webBody = webBody.replace(entry.getKey(), entry.getValue());
                }
                if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                    thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
                }
                if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                    thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
                }
            }
            Message message = fillMessage(application);
            message.setSubject(webSubject);
            message.setMessage(webBody);
            message.setCustom(false);
            String stringMessage = gson.toJson(message);
            if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
                log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
                message.setThirdPartySMS(thirdPartySMS);
                log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
                tempNotificationType += 2;
            }
            if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
                log.debug("===========LSF; Sending Email :" + thirdPartyEmail);
                helper.sendToEmailProducer(thirdPartyEmail);
                message.setThirdPartyEmail(thirdPartyEmail);
                tempNotificationType += 3;
            }

            message.setNotificationType(decideMessageType(tempNotificationType));
            lsfRepository.addMessageOut(message);
        }
        return  true;
    } else {
        return false;
    }

}

private Map<String, String> getParameterMapForMargin(int marginLevel, MApplicationCollaterals applicationCollaterals, MurabahApplication application) {
    MApplicationCollaterals completeCollaterals = lsfRepository.getApplicationCompleteCollateral(application.getId());
    Map<String, String> paramMap = new HashMap<>();
    String lsfAccount = application.getTradingAcc();
    paramMap.put("$ftv", String.valueOf(applicationCollaterals.getFtv()));
    paramMap.put("$outstandingBalance", String.valueOf(applicationCollaterals.getOutstandingAmount()));
    paramMap.put("$netCollateral", String.valueOf(applicationCollaterals.getNetTotalColleteral()));
    paramMap.put("$marginLevel", String.valueOf(marginLevel));
    paramMap.put("$customerName", application.getFullName());
    paramMap.put("$tradingAccount", application.getTradingAcc());
    paramMap.put("$email", application.getEmail() != null ? application.getEmail() : "");
    paramMap.put("$firstMarginLevel", String.valueOf(GlobalParameters.getInstance().getFirstMarginCall()));
    paramMap.put("$secondMarginLevel", String.valueOf(GlobalParameters.getInstance().getSecondMarginCall()));
    paramMap.put("$liquidationLevel", String.valueOf(GlobalParameters.getInstance().getLiquidationCall()));
    paramMap.put("$cifNumber", application.getCustomerReferenceNumber() != null ? application.getCustomerReferenceNumber() : "");
    paramMap.put("$mobileNumber", application.getMobileNo() != null ? application.getMobileNo() : "");
    paramMap.put("$prefLanguage",application.getPreferedLanguage()!=null?application.getPreferedLanguage():"E");
    if (completeCollaterals != null) {
        if (completeCollaterals.getLsfTypeTradingAccounts() != null && completeCollaterals.getLsfTypeTradingAccounts().size() > 0) {
            lsfAccount = completeCollaterals.getLsfTypeTradingAccounts().get(0).getAccountId();
        }

    }
    paramMap.put("$lsfTypeTradingAccount", lsfAccount);

    return paramMap;
}

private Map<String, String> getApplicationParameterMap(MurabahApplication murabahApplication) {
    MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(murabahApplication.getId());
    List<PurchaseOrder> purchaseOrderList = lsfRepository.getAllPurchaseOrder(murabahApplication.getId());
    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("$customerName", murabahApplication.getFullName());
    paramMap.put("$applicationId", murabahApplication.getDisplayApplicationId());
    paramMap.put("$mobileNumber", murabahApplication.getMobileNo() != null ? murabahApplication.getMobileNo() : "");
    paramMap.put("$tradingAccount", murabahApplication.getTradingAcc() != null ? murabahApplication.getTradingAcc() : "");
    paramMap.put("$cifNumber", murabahApplication.getCustomerReferenceNumber() != null ? murabahApplication.getCustomerReferenceNumber() : "");
    paramMap.put("$prefLanguage",murabahApplication.getPreferedLanguage()!=null?murabahApplication.getPreferedLanguage():"E");


    if (collaterals != null) {
        paramMap.put("$totalGrossCollateralValue", String.valueOf(collaterals.getTotalPFColleteral()));
        paramMap.put("$totalNetRiskAdjustedCollateralValue", String.valueOf(collaterals.getNetTotalColleteral()));
        paramMap.put("$operativeLimit", String.valueOf(collaterals.getOpperativeLimitAmount()));
        paramMap.put("$operativeLimit", String.valueOf(collaterals.getOpperativeLimitAmount()));
        paramMap.put("$netTotalCollateralValue", String.valueOf(collaterals.getNetTotalColleteral()));
        String lsfAccount = "";
        if (collaterals.getLsfTypeTradingAccounts() != null && collaterals.getLsfTypeTradingAccounts().size() > 0) {
            lsfAccount = collaterals.getLsfTypeTradingAccounts().get(0).getAccountId();
        }
        paramMap.put("$lsfTypeTradingAccount", lsfAccount);
    }
    if (purchaseOrderList != null && purchaseOrderList.size() != 0) {
        var purchaseOrder = purchaseOrderList.getFirst();
        paramMap.put("$poValue", String.valueOf(purchaseOrder.getOrderValue()));
        paramMap.put("$BanksProfit", String.valueOf(purchaseOrder.getProfitAmount()));
        paramMap.put("$totalFeeCharge", String.valueOf(purchaseOrder.getProfitAmount())); //todo
        paramMap.put("$settlementAmount", String.valueOf(purchaseOrder.getOrderSettlementAmount()));
        paramMap.put("$orderId", String.valueOf(purchaseOrder.getId()));
        paramMap.put("$orderCompletedValue", String.valueOf(purchaseOrder.getOrderCompletedValue()));
        paramMap.put("$contractsnnumber", purchaseOrder.getCertificateNumber());
    }
    return paramMap;
}

private boolean sendWebNotification(NotificationMsgConfiguration msgConfiguration, MurabahApplication murabahApplication) {
    WebNotification webNotification = new WebNotification();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    webNotification.setMessageId(Long.toString(System.currentTimeMillis()));
    webNotification.setApplicationId(murabahApplication.getId());
    webNotification.setSubject(msgConfiguration.getWebSubject());
    webNotification.setBody(msgConfiguration.getWebBody());
    webNotification.setDate(dateFormat.format(date));
    webNotification.setStatus(0);
    log.debug("===========LSF :Web Notification Message : " + gson.toJson(webNotification));
    lsfRepository.addWebNotification(webNotification);
    log.debug("===========LSF :Web notification successfully save to database");
    return true;
}

private Message fillMessage(MurabahApplication murabahApplication) { //Fill message header params
    Message message = new Message();
    List<String> mobileNumbers = new ArrayList<>();
    List<String> toAddress = new ArrayList<>();
    List<String> ccAddress = new ArrayList<>();
    List<String> bccAddress = new ArrayList<>();
    mobileNumbers.add(murabahApplication.getMobileNo());
    toAddress.add(murabahApplication.getEmail());
    message.setUid(String.valueOf(System.nanoTime()));
    message.setUserId(murabahApplication.getCustomerId());
    message.setLanguage("EN");
    message.setMobileNumbers(mobileNumbers);
    message.setToAddresses(toAddress);
    message.setCcAddresses(ccAddress);
    message.setBccAddresses(bccAddress);
    message.setFromAddress("from.b@c");
    message.setStatus(1);

    // Add admin emails/phones to cc/mobile lists if needed
    try {
        List<?> adminUsers = lsfRepository.getAdminUsers();
        if (adminUsers != null && !adminUsers.isEmpty()) {
            for (Object adminUserObj : adminUsers) {
                // Handle different possible return types from repository
                if (adminUserObj instanceof Map) {
                    Map<?, ?> adminUser = (Map<?, ?>) adminUserObj;
                    if (adminUser.get("email") != null)
                        ccAddress.add(adminUser.get("email").toString());
                    if (adminUser.get("mobile") != null)
                        mobileNumbers.add(adminUser.get("mobile").toString());
                } else {
                    // If it's some other type, try to use reflection
                    try {
                        Object email = adminUserObj.getClass().getMethod("getEmail").invoke(adminUserObj);
                        Object mobile = adminUserObj.getClass().getMethod("getMobile").invoke(adminUserObj);
                        if (email != null)
                            ccAddress.add(email.toString());
                        if (mobile != null)
                            mobileNumbers.add(mobile.toString());
                    } catch (Exception e) {
                        log.error("Error getting admin user properties", e);
                    }
                }
            }
        }
    } catch (Exception e) {
        log.error("Error getting admin users", e);
    }

    return message;
}

private String decideMessageType(int count) {
    if (count == 2) {
        return NotificationConstants.NOTIFICATION_TYPE_SMS;
    } else if (count == 3) {
        return NotificationConstants.NOTIFICATION_TYPE_EMAIL;
    } else if (count == 4) {
        return NotificationConstants.NOTIFICATION_TYPE_WEB;
    } else if (count == 5) {
        return NotificationConstants.NOTIFICATION_TYPE_SMS_EMAIL;
    } else if (count == 6) {
        return NotificationConstants.NOTIFICATION_TYPE_SMS_WEB;
    } else if (count == 7) {
        return NotificationConstants.NOTIFICATION_TYPE_SMS_EMAIL;
    } else if (count == 9) {
        return NotificationConstants.NOTIFICATION_TYPE_SMS_EMAIL_WEB;
    }
    return null;
}

public void sendOTP(MurabahApplication murabahApplication, String otp) {
    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.OTP);
    Map<String, String> paramsMap = new HashMap<>();
    paramsMap.put("$mobileNumber", murabahApplication.getMobileNo());
    paramsMap.put("$otp", otp);
    paramsMap.put("$cifNumber", murabahApplication.getCustomerReferenceNumber());
    paramsMap.put("$prefLanguage",murabahApplication.getPreferedLanguage()!=null?murabahApplication.getPreferedLanguage():"E");
    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        log.debug("===========LSF; Sending Settlement Notifications , msgConfiguration:" + gson.toJson(msgConfiguration));
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (webSubject != null && webSubject.contains(entry.getKey())) {
                webSubject = webSubject.replace(entry.getKey(), entry.getValue());
            }
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }
        }

        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            helper.sendToEmailProducer(thirdPartyEmail);
            message.setThirdPartyEmail(thirdPartyEmail);
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("No configuration found for Settlement Reminder");
    }
}

public void sendCommonRejectNotifications(MurabahApplication murabahApplication){

    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.ADMIN_COMMON_REJECT);

    if (msgConfigurations != null & msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();

        log.debug("===========LSF: Notification, msgConfiguration:" + gson.toJson(msgConfiguration));
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();

        Map<String, String> paramsMap = getApplicationParameterMap(murabahApplication);

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) { // filling web message , third party sms/email with parameters
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }

        }
        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        message.setSentBy("SYSTEM");
        // String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF: Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIL :" + thirdPartyEmail);
            message.setThirdPartyEmail(thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("===========LSF :Can't find the message configurations");

    }
}

public void sendAuthAbicToSellNotification(MurabahApplication murabahApplication, boolean isAutomatic, PurchaseOrder purchaseOrder) {

    NotificationMsgConfiguration msgConfiguration = null;
    List<NotificationMsgConfiguration> msgConfigurations = null;
    int tempNotificationType = 0;
    if (isAutomatic){
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.AUTH_ABIC_TO_SELL_AUTO);
    }else{
        msgConfigurations = lsfRepository.getNotificationMsgConfigurationForNotificationType(NotificationConstants.AUTH_ABIC_TO_SELL);
    }

    if (msgConfigurations != null && msgConfigurations.size() > 0) {
        msgConfiguration = msgConfigurations.get(0);
        log.debug("===========LSF; Sending sendAuthAbicToSellNotification , msgConfiguration:" + gson.toJson(msgConfiguration));
        String webSubject = msgConfiguration.getWebSubject();
        String webBody = msgConfiguration.getWebBody();
        String thirdPartySMS = msgConfiguration.getThirdPartySMSTemplate();
        String thirdPartyEmail = msgConfiguration.getThirdPartyEmailTemplate();
        Map<String, String> paramsMap = getParameterMapForAuthAbicToSellNotification(murabahApplication, purchaseOrder);
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            if (webSubject != null && webSubject.contains(entry.getKey())) {
                webSubject = webSubject.replace(entry.getKey(), entry.getValue());
            }
            if (webBody != null && webBody.contains(entry.getKey())) {
                webBody = webBody.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartySMS != null && thirdPartySMS.contains(entry.getKey())) {
                thirdPartySMS = thirdPartySMS.replace(entry.getKey(), entry.getValue());
            }
            if (thirdPartyEmail != null && thirdPartyEmail.contains(entry.getKey())) {
                thirdPartyEmail = thirdPartyEmail.replace(entry.getKey(), entry.getValue());
            }
        }

        Message message = fillMessage(murabahApplication);
        message.setSubject(webSubject);
        message.setMessage(webBody);
        message.setCustom(false);
        String stringMessage = gson.toJson(message);
        if (thirdPartySMS != null && thirdPartySMS.length() > 0) {
            log.debug("===========LSF; Sending SMS :" + thirdPartySMS);
            message.setThirdPartySMS(thirdPartySMS);
            log.info("===========LSF; Sending SMS , Producer Response" + helper.sendToSMSProducer(thirdPartySMS));
            tempNotificationType += 2;
        }
        if (thirdPartyEmail != null && thirdPartyEmail.length() > 0) {
            log.debug("===========LSF; Sending EMAIl :" + thirdPartyEmail);
            log.info("===========LSF; Sending EMAIL , Producer Response" + helper.sendToEmailProducer(thirdPartyEmail));
            message.setThirdPartyEmail(thirdPartyEmail);
            tempNotificationType += 3;
        }
        if (msgConfiguration.isWeb()) {
            msgConfiguration.setWebSubject(webSubject);
            msgConfiguration.setWebBody(webBody);
            sendWebNotification(msgConfiguration, murabahApplication);
            tempNotificationType += 4;
        }
        message.setNotificationType(decideMessageType(tempNotificationType));
        lsfRepository.addMessageOut(message);

    } else {
        log.debug("No configuration found for Auth Abic to Sell");
    }

}

private Map<String, String> getParameterMapForAuthAbicToSellNotification(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder) {

    Map<String, String> paramMap = new HashMap<>();
    paramMap.put("$customerName", murabahApplication.getFullName());
    paramMap.put("$applicationId", murabahApplication.getDisplayApplicationId());
    paramMap.put("$cifNumber", murabahApplication.getCustomerReferenceNumber());
    paramMap.put("$prefLanguage",murabahApplication.getPreferedLanguage()!=null?murabahApplication.getPreferedLanguage():"E");
    paramMap.put("$lsfTypeTradingAccount", murabahApplication.getTradingAcc());
    paramMap.put("$tradingAccount", murabahApplication.getTradingAcc());
    MurabahaProduct product = lsfRepository.getMurabahaProduct(murabahApplication.getProductType());
    paramMap.put("$productName",product.getProductName());
    paramMap.put("$mobileNumber", murabahApplication.getMobileNo() != null ? murabahApplication.getMobileNo() : "");
    paramMap.put("$contractsnnumber", purchaseOrder != null ? purchaseOrder.getCertificateNumber() : murabahApplication.getId());
    return paramMap;
}

    public void reloadAdminUsers() {
        adminUsers = lsfRepository.getAdminUsers();
    }

}