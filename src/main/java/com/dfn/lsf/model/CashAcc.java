package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/25/2015.
 */
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
}
