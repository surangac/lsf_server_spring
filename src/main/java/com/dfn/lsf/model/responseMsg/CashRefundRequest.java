package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 10/13/2016.
 */
public class CashRefundRequest {
    private int reqType;
    private String fromCashAccountId;
    private String fromCashAccountNo;
    private double amount;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getFromCashAccountId() {
        return fromCashAccountId;
    }

    public void setFromCashAccountId(String fromCashAccountId) {
        this.fromCashAccountId = fromCashAccountId;
    }

    public String getFromCashAccountNo() {
        return fromCashAccountNo;
    }

    public void setFromCashAccountNo(String fromCashAccountNo) {
        this.fromCashAccountNo = fromCashAccountNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
