package com.dfn.lsf.model.report;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarginInformation {
    private String fromDate;
    private String toDate;
    private String numberOfCustomers;
    private String marginCommitment;
    private String outStandingBalance;
    private String totalPFMarketValue;
    private String weightedTotalPFMarketValue;
    private String clientContributionToMarketValue;
    private String weightedClientContributionToMarketValue;
    private String marginRatio;
    private String weightedRatio;
    private String numberOfMarginCalls;
    private String numberOfLiquidationInstructions;
    private String liquidatedAmount;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(String numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }

    public String getMarginCommitment() {
        return marginCommitment;
    }

    public void setMarginCommitment(String marginCommitment) {
        this.marginCommitment = marginCommitment;
    }

    public String getOutStandingBalance() {
        return outStandingBalance;
    }

    public void setOutStandingBalance(String outStandingBalance) {
        this.outStandingBalance = outStandingBalance;
    }

    public String getTotalPFMarketValue() {
        return totalPFMarketValue;
    }

    public void setTotalPFMarketValue(String totalPFMarketValue) {
        this.totalPFMarketValue = totalPFMarketValue;
    }

    public String getWeightedTotalPFMarketValue() {
        return weightedTotalPFMarketValue;
    }

    public void setWeightedTotalPFMarketValue(String weightedTotalPFMarketValue) {
        this.weightedTotalPFMarketValue = weightedTotalPFMarketValue;
    }

    public String getClientContributionToMarketValue() {
        return clientContributionToMarketValue;
    }

    public void setClientContributionToMarketValue(String clientContributionToMarketValue) {
        this.clientContributionToMarketValue = clientContributionToMarketValue;
    }

    public String getWeightedClientContributionToMarketValue() {
        return weightedClientContributionToMarketValue;
    }

    public void setWeightedClientContributionToMarketValue(String weightedClientContributionToMarketValue) {
        this.weightedClientContributionToMarketValue = weightedClientContributionToMarketValue;
    }

    public String getMarginRatio() {
        return marginRatio;
    }

    public void setMarginRatio(String marginRatio) {
        this.marginRatio = marginRatio;
    }

    public String getWeightedRatio() {
        return weightedRatio;
    }

    public void setWeightedRatio(String weightedRatio) {
        this.weightedRatio = weightedRatio;
    }

    public String getNumberOfMarginCalls() {
        return numberOfMarginCalls;
    }

    public void setNumberOfMarginCalls(String numberOfMarginCalls) {
        this.numberOfMarginCalls = numberOfMarginCalls;
    }

    public String getNumberOfLiquidationInstructions() {
        return numberOfLiquidationInstructions;
    }

    public void setNumberOfLiquidationInstructions(String numberOfLiquidationInstructions) {
        this.numberOfLiquidationInstructions = numberOfLiquidationInstructions;
    }

    public String getLiquidatedAmount() {
        return liquidatedAmount;
    }

    public void setLiquidatedAmount(String liquidatedAmount) {
        this.liquidatedAmount = liquidatedAmount;
    }
}
