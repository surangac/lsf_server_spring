package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 5/10/2016.
 */
public class AccountCreationRequest {
    private int reqType;
    private String referenceNo;
    private String fromCashAccountNo;
    private String tradingAccountId;
    private String exchange;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getFromCashAccountNo() {
        return fromCashAccountNo;
    }

    public void setFromCashAccountNo(String fromCashAccountNo) {
        this.fromCashAccountNo = fromCashAccountNo;
    }

    public String getTradingAccountId() {
        return tradingAccountId;
    }

    public void setTradingAccountId(String tradingAccountId) {
        this.tradingAccountId = tradingAccountId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
