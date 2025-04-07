package com.dfn.lsf.model.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinanceBrokerageInfo {
    private String customerName;
    private String portfolioNumber;
    private String outstandingLoan;
    private String customerExposure;
    private String accumulatedProfit;
    private String contractProfit;
    private String totalFees;
    private String totalCommission;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public String getOutstandingLoan() {
        return outstandingLoan;
    }

    public void setOutstandingLoan(String outstandingLoan) {
        this.outstandingLoan = outstandingLoan;
    }

    public String getCustomerExposure() {
        return customerExposure;
    }

    public void setCustomerExposure(String customerExposure) {
        this.customerExposure = customerExposure;
    }

    public String getAccumulatedProfit() {
        return accumulatedProfit;
    }

    public void setAccumulatedProfit(String accumulatedProfit) {
        this.accumulatedProfit = accumulatedProfit;
    }

    public String getContractProfit() {
        return contractProfit;
    }

    public void setContractProfit(String contractProfit) {
        this.contractProfit = contractProfit;
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
