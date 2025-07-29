package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 2/12/2017.
 */
public class PendingActivity {
    private String applicationID;
    private int activityID;
    private String userID;
    private String activityDescription;
    private String orderID;
    private String nonLSFTradingAccount;
    private String nonLSFCashAccount;
    private String lsfTypeTradingAccount;
    private String lsfTypeCashAccount;
    private String displayApplicationID;

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
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

    public String getDisplayApplicationID() {
        return displayApplicationID;
    }

    public void setDisplayApplicationID(String displayApplicationID) {
        this.displayApplicationID = displayApplicationID;
    }
}
