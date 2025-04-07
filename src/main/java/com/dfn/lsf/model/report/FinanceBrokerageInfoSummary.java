package com.dfn.lsf.model.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceBrokerageInfoSummary {
    private String masterCashAccountBalance;
    private String outstandingSum;
    private String totalExposure;
    private String accumulatedProfit;
    private String contractProfitSum;
    private String totalFees;
    private String totalCommission;

    public String getMasterCashAccountBalance() {
        return masterCashAccountBalance;
    }

    public void setMasterCashAccountBalance(String masterCashAccountBalance) {
        this.masterCashAccountBalance = masterCashAccountBalance;
    }

    public String getOutstandingSum() {
        return outstandingSum;
    }

    public void setOutstandingSum(String outstandingSum) {
        this.outstandingSum = outstandingSum;
    }

    public String getTotalExposure() {
        return totalExposure;
    }

    public void setTotalExposure(String totalExposure) {
        this.totalExposure = totalExposure;
    }

    public String getAccumulatedProfit() {
        return accumulatedProfit;
    }

    public void setAccumulatedProfit(String accumulatedProfit) {
        this.accumulatedProfit = accumulatedProfit;
    }

    public String getContractProfitSum() {
        return contractProfitSum;
    }

    public void setContractProfitSum(String contractProfitSum) {
        this.contractProfitSum = contractProfitSum;
    }

    public String getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(String totalFees) {
        this.totalFees = totalFees;
    }

    public String getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(String totalCommission) {
        this.totalCommission = totalCommission;
    }
}
