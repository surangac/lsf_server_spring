package com.dfn.lsf.model.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
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
}
