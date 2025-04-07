package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 2/12/2017.
 */
public class PendingActivityRequest {
    private String messageType;
    private String applicationID;
    private String userID;
    private int activityID;
    private String orderID;
    private String nonLSFTradingAccount;
    private String nonLSFCashAccount;
    private String lsfTypeTradingAccount;
    private String lsfTypeCashAccount;
    private String ipAddress;
    private String adminUserID;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getNonLSFTradingAccount() {
        return nonLSFTradingAccount;
    }

    public void setNonLSFTradingAccount(String nonLSFTradingAccount) {
        this.nonLSFTradingAccount = nonLSFTradingAccount;
    }

    public String getNonLSFCashAccount() {
        return nonLSFCashAccount;
    }

    public void setNonLSFCashAccount(String nonLSFCashAccount) {
        this.nonLSFCashAccount = nonLSFCashAccount;
    }

    public String getLsfTypeTradingAccount() {
        return lsfTypeTradingAccount;
    }

    public void setLsfTypeTradingAccount(String lsfTypeTradingAccount) {
        this.lsfTypeTradingAccount = lsfTypeTradingAccount;
    }

    public String getLsfTypeCashAccount() {
        return lsfTypeCashAccount;
    }

    public void setLsfTypeCashAccount(String lsfTypeCashAccount) {
        this.lsfTypeCashAccount = lsfTypeCashAccount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAdminUserID() {
        return adminUserID;
    }

    public void setAdminUserID(String adminUserID) {
        this.adminUserID = adminUserID;
    }
}
