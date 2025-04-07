package com.dfn.lsf.model.responseMsg;

/**
 * Created by surangac on 8/11/2015.
 */
public class ProfitResponse {
    private double profitAmount;
    private double profitPercent;
    private double sibourAmount;
    private double LibourAmount;
    private double totalProfit;

    public double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public double getProfitPercent() {
        return profitPercent;
    }

    public void setProfitPercent(double profitPercent) {
        this.profitPercent = profitPercent;
    }

    public double getSibourAmount() {
        return sibourAmount;
    }

    public void setSibourAmount(double sibourAmount) {
        this.sibourAmount = sibourAmount;
    }

    public double getLibourAmount() {
        return LibourAmount;
    }

    public void setLibourAmount(double libourAmount) {
        LibourAmount = libourAmount;
    }

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }
}
