package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 4/15/2016.
 */
public class CloseLSFAccountRequest {
    private int reqType;
    private String fromTradingAccountId;
    private String toTradingAccountId;
    private String fromCashAccountId;
    private String toCashAccountId;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getFromTradingAccountId() {
        return fromTradingAccountId;
    }

    public void setFromTradingAccountId(String fromTradingAccountId) {
        this.fromTradingAccountId = fromTradingAccountId;
    }

    public String getToTradingAccountId() {
        return toTradingAccountId;
    }

    public void setToTradingAccountId(String toTradingAccountId) {
        this.toTradingAccountId = toTradingAccountId;
    }

    public String getFromCashAccountId() {
        return fromCashAccountId;
    }

    public void setFromCashAccountId(String fromCashAccountId) {
        this.fromCashAccountId = fromCashAccountId;
    }

    public String getToCashAccountId() {
        return toCashAccountId;
    }

    public void setToCashAccountId(String toCashAccountId) {
        this.toCashAccountId = toCashAccountId;
    }
}
