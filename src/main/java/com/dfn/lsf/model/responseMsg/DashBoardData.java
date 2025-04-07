package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.OrderProfit;

/**
 * Created by surangac on 6/22/2015.
 */
public class DashBoardData {
    private String applicationId;
    private double remainingOperativeLimit;
    private double remainingApprovedLimit;
    private double utilizedLimit;
    private double cashCollateral;
    private double pfCollateral;
    private double totalCollateral;
    private List<FtvSummary> dailyFtvList;
    private double outstandingBalance;
    private OrderProfit orderProfit;
    private int productType;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public double getRemainingOperativeLimit() {
        return remainingOperativeLimit;
    }

    public void setRemainingOperativeLimit(double remainingOperativeLimit) {
        this.remainingOperativeLimit = remainingOperativeLimit;
    }

    public double getRemainingApprovedLimit() {
        return remainingApprovedLimit;
    }

    public void setRemainingApprovedLimit(double remainingApprovedLimit) {
        this.remainingApprovedLimit = remainingApprovedLimit;
    }

    public double getUtilizedLimit() {
        return utilizedLimit;
    }

    public void setUtilizedLimit(double utilizedLimit) {
        this.utilizedLimit = utilizedLimit;
    }

    public double getCashCollateral() {
        return cashCollateral;
    }

    public void setCashCollateral(double cashCollateral) {
        this.cashCollateral = cashCollateral;
    }

    public double getPfCollateral() {
        return pfCollateral;
    }

    public void setPfCollateral(double pfCollateral) {
        this.pfCollateral = pfCollateral;
    }

    public double getTotalCollateral() {
        return totalCollateral;
    }

    public void setTotalCollateral(double totalCollateral) {
        this.totalCollateral = totalCollateral;
    }

    public List<FtvSummary> getDailyFtvList() {
        return dailyFtvList;
    }

    public void setDailyFtvList(List<FtvSummary> dailyFtvList) {
        this.dailyFtvList = dailyFtvList;
    }

    public double getOutstandingBalance() {
        return outstandingBalance;
    }

    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }

    public OrderProfit getOrderProfit() {
        return orderProfit;
    }

    public void setOrderProfit(OrderProfit orderProfit) {
        this.orderProfit = orderProfit;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }
}
