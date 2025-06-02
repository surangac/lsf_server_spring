package com.dfn.lsf.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/25/2015.
 */
@Builder
public class CashAcc {
    private String accountId;
    private String currencyCode;
    private double cashBalance;
    private double amountAsColletarals;
    private double amountTransfered;
    private boolean isLsfType;
    private String collateralId;
    private String applicationId;
    private String blockedReference;
    private int transStatus;
    private String investmentAccountNumber;
    private double pendingSettle;
    private double netReceivable;
    private double blockedAmount;

    public void mapFromOms(CashAcc cashAcc) {
        this.cashBalance = cashAcc.getCashBalance();
        this.blockedAmount = cashAcc.getBlockedAmount();
        this.pendingSettle = cashAcc.getPendingSettle();
        this.netReceivable = cashAcc.getNetReceivable();
        this.investmentAccountNumber = cashAcc.getInvestmentAccountNumber();
        this.isLsfType = cashAcc.isLsfType();
    }
}
