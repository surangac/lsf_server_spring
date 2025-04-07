package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 10/16/2015.
 */
public class AdminFeeRequest {
    private int reqType;
    private String fromCashAccountId;
    private String fromCashAccountNo;
    private double amount;
    private double brokerVat;
    private double exchangeVat;

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFromCashAccountNo() {
        return fromCashAccountNo;
    }

    public void setFromCashAccountNo(String fromCashAccountNo) {
        this.fromCashAccountNo = fromCashAccountNo;
    }

    public double getBrokerVat() {
        return brokerVat;
    }

    public void setBrokerVat(double brokerVat) {
        this.brokerVat = brokerVat;
    }

    public double getExchangeVat() {
        return exchangeVat;
    }

    public void setExchangeVat(double exchangeVat) {
        this.exchangeVat = exchangeVat;
    }
}
