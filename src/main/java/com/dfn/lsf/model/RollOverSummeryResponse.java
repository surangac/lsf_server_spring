package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RollOverSummeryResponse {
    private String originalAppId;
    private String appId;
    private String customerId;
    private String customerName;
    private String statusDescription;
    private int currentLevel;
    private int overallStatus;
    private List<TradingAccOmsResp> tradingAccounts;
    private List<CashAcc> cashAccounts;
    private double totalPfValue;
    private double totalCashBalance;
    private double totalCollateralValue;
    private String tenor;
    private double originalSettlementAmount;
    private double originalLoanAmount;
    private double originalProfitAmount;
    private double requiredAmount;
    private double adminFee;
    private double vatAmount;
    private double profitPercentage;
    private double newProfitAmount;
    private int productType;
    private String productName;
    private String marginabilityGroup;
    private String financeMethod;
    private double approvedLimit;
    private int rollOverSeqNumber;
    private String facilityType;
    private String date;
    private String proposalDate;
    private String cashAccountId;
    private String tradingAccountId;
    private Double initialRAPV;
    private String email;
    private String mobile;
    private List<TradingAcc> lsfTypeTradingAccounts;
    private List<CashAcc> lsfTypeCashAccounts;
}
