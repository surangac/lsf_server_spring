package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cash transfer request model
 */
@Getter
@Setter
@NoArgsConstructor
public class CashTransferRequest {
    private int reqType;
    private String id;
    private String applicationid;
    private double amount;
    private String fromCashAccountId;
    private String toCashAccountId;
    private String params;
}
