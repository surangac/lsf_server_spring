package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by manodyas on 9/16/2015.
 */
public class OrderProfit {
    private String applicationID;
    private String orderID;
    private String date;
    private double profitAmount;
    private double cumulativeProfitAmount;
    private double tradedCommission;
    private double targetCommission;
    private boolean isChargeCommission;
    private double chargeCommissionAmt;

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public double getCumulativeProfitAmount() {
        return cumulativeProfitAmount;
    }

    public void setCumulativeProfitAmount(double cumulativeProfitAmount) {
        this.cumulativeProfitAmount = cumulativeProfitAmount;
    }

    public double getTradedCommission() {
        return tradedCommission;
    }

    public void setTradedCommission(double tradedCommission) {
        this.tradedCommission = tradedCommission;
    }

    public double getTargetCommission() {
        return targetCommission;
    }

    public void setTargetCommission(double targetCommission) {
        this.targetCommission = targetCommission;
    }

    public boolean isChargeCommission() {
        return isChargeCommission;
    }

    public void setChargeCommission(boolean chargeCommission) {
        isChargeCommission = chargeCommission;
    }

    public double getChargeCommissionAmt() {
        return chargeCommissionAmt;
    }

    public void setChargeCommissionAmt(double chargeCommissionAmt) {
        this.chargeCommissionAmt = chargeCommissionAmt;
    }
}
