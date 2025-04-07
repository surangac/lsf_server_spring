package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 2/15/2016.
 */
public class FTVInfo {

    private String tradingAcc;
    private double amountGranted;
    private double amountUtilized;
    private double commission;
    private double commissionPreviousDay;
    private double pfMarketValue;
    private double marginablePfMarketValue;
    private double ftvPreviousDay;
    private String startDate;
    private int tenor;
    private String expireDate;
    private int dayesLeft;
    private double cumProfit;

    private String customerID;
    private String customerName;
    private String applicationID;
    private double operativeLimit;
    private double limitUtilized;
    private double remainingOperativeLimit;
    private double outstandingAmount;
    private double ftv;
    private double adminFeeCharged;
    private double maximumWithdrawAmount;
    private double availableCashBalance;


    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public double getOperativeLimit() {
        return operativeLimit;
    }

    public void setOperativeLimit(double operativeLimit) {
        this.operativeLimit = operativeLimit;
    }

    public double getLimitUtilized() {
        return limitUtilized;
    }

    public void setLimitUtilized(double limitUtilized) {
        this.limitUtilized = limitUtilized;
    }

    public double getRemainingOperativeLimit() {
        return remainingOperativeLimit;
    }

    public void setRemainingOperativeLimit(double remainingOperativeLimit) {
        this.remainingOperativeLimit = remainingOperativeLimit;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public double getFtv() {
        return ftv;
    }

    public void setFtv(double ftv) {
        this.ftv = ftv;
    }

    public double getAdminFeeCharged() {
        return adminFeeCharged;
    }

    public void setAdminFeeCharged(double adminFeeCharged) {
        this.adminFeeCharged = adminFeeCharged;
    }

    public double getMaximumWithdrawAmount() {
        return maximumWithdrawAmount;
    }

    public void setMaximumWithdrawAmount(double maximumWithdrawAmount) {
        this.maximumWithdrawAmount = maximumWithdrawAmount;
    }

    public double getAvailableCashBalance() {
        return availableCashBalance;
    }

    public void setAvailableCashBalance(double availableCashBalance) {
        this.availableCashBalance = availableCashBalance;
    }

    public String getTradingAcc() {
        return tradingAcc;
    }

    public void setTradingAcc(String tradingAcc) {
        this.tradingAcc = tradingAcc;
    }

    public double getAmountGranted() {
        return amountGranted;
    }

    public void setAmountGranted(double amountGranted) {
        this.amountGranted = amountGranted;
    }

    public double getAmountUtilized() {
        return amountUtilized;
    }

    public void setAmountUtilized(double amountUtilized) {
        this.amountUtilized = amountUtilized;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getCommissionPreviousDay() {
        return commissionPreviousDay;
    }

    public void setCommissionPreviousDay(double commissionPreviousDay) {
        this.commissionPreviousDay = commissionPreviousDay;
    }

    public double getPfMarketValue() {
        return pfMarketValue;
    }

    public void setPfMarketValue(double pfMarketValue) {
        this.pfMarketValue = pfMarketValue;
    }

    public double getMarginablePfMarketValue() {
        return marginablePfMarketValue;
    }

    public void setMarginablePfMarketValue(double marginablePfMarketValue) {
        this.marginablePfMarketValue = marginablePfMarketValue;
    }

    public double getFtvPreviousDay() {
        return ftvPreviousDay;
    }

    public void setFtvPreviousDay(double ftvPreviousDay) {
        this.ftvPreviousDay = ftvPreviousDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getDayesLeft() {
        return dayesLeft;
    }

    public void setDayesLeft(int dayesLeft) {
        this.dayesLeft = dayesLeft;
    }

    public double getCumProfit() {
        return cumProfit;
    }

    public void setCumProfit(double cumProfit) {
        this.cumProfit = cumProfit;
    }
}
