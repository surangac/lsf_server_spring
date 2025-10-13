package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by manodyas on 10/16/2015.
 */
@Getter
@Setter
public class AdminFeeRequest {
    private int reqType;
    private String fromCashAccountId;
    private String fromCashAccountNo;
    private double amount;
    private double brokerVat;
    private double exchangeVat;
    private int txnCode;
    private String appId;
}
