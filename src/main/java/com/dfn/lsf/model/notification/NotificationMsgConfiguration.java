package com.dfn.lsf.model.notification;

/**
 * Created by Atchuthan on 7/28/2015.
 */
public class NotificationMsgConfiguration {
    private String id; //unique identifier
    private String currentLevel; //muraba Application current level
    private String overRoleStatus; //muraba Application over role status
    private boolean isSms;
    private boolean isMail;
    private boolean isWeb;
    private String smsTemplate; // sms template
    private String emailSubject; //email subject
    private String emailBody; //email body
    private String webSubject;
    private String webBody;
    private String notificationCode;
    private String thirdPartySMSTemplate;
    private String thirdPartyEmailTemplate;
    private String thirdPartyReference;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getOverRoleStatus() {
        return overRoleStatus;
    }

    public void setOverRoleStatus(String overRoleStatus) {
        this.overRoleStatus = overRoleStatus;
    }

    public boolean isSms() {
        return isSms;
    }

    public void setSms(boolean isSms) {
        this.isSms = isSms;
    }

    public boolean isMail() {
        return isMail;
    }

    public void setMail(boolean isEmail) {
        this.isMail = isEmail;
    }

    public boolean isWeb() {
        return isWeb;
    }

    public void setWeb(boolean isWeb) {
        this.isWeb = isWeb;
    }

    public String getWebSubject() {
        return webSubject;
    }

    public void setWebSubject(String webSubject) {
        this.webSubject = webSubject;
    }

    public String getWebBody() {
        return webBody;
    }

    public void setWebBody(String webBody) {
        this.webBody = webBody;
    }

    public String getSmsTemplate() {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    public String getThirdPartySMSTemplate() {
        return thirdPartySMSTemplate;
    }

    public void setThirdPartySMSTemplate(String thirdPartySMSTemplate) {
        this.thirdPartySMSTemplate = thirdPartySMSTemplate;
    }

    public String getThirdPartyEmailTemplate() {
        return thirdPartyEmailTemplate;
    }

    public void setThirdPartyEmailTemplate(String thirdPartyEmailTemplate) {
        this.thirdPartyEmailTemplate = thirdPartyEmailTemplate;
    }

    public String getThirdPartyReference() {
        return thirdPartyReference;
    }

    public void setThirdPartyReference(String thirdPartyReference) {
        this.thirdPartyReference = thirdPartyReference;
    }
}
