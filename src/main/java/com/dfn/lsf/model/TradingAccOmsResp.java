package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TradingAccOmsResp {
    private String accountId;
    private String exchange;
    private boolean isLsf;
    private String relCashAccNo;
    private int u06Id;
    private int u05CustomerId;
    private int branchId;
    private double availableCash;
    private int customerId;
    private int accCategory;
    private int accType;
    private int accStatus;
    private String investorAccountNo;
    private int accountClassification;
    private int securityAccountId;
    private double pendingSettle;
    private double netReceivable;
    private double blockedAmount;
    private List<Symbol> symbolList;
}
