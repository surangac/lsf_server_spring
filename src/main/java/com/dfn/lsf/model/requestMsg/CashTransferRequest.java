package com.dfn.lsf.model.requestMsg;

/**
 * Created by surangac on 6/4/2015.
 */
public class CashTransferRequest {
    private int reqType;
    private String id;
    private String applicationid;
    private double amount;
    private String fromCashAccountId;
    private String toCashAccountId;
    private String params;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationid() {
        return applicationid;
    }

    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
