package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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
    private LocalDate createdDate;
}
