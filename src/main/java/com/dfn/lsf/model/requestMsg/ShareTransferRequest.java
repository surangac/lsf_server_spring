package com.dfn.lsf.model.requestMsg;

/**
 * Created by surangac on 6/1/2015.
 */
public class ShareTransferRequest {
    private int reqType;
    private String id;
    private String applicationid;
    private String fromTradingAccountId;
    private String toTradingAccountId;
    private String fromExchange;
    private String toExchange;
    private String symbol;
    private String exchange;
    private int quantity;
    private int sendToExchange;
    private String params;
    private String basketReference;

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
    public String getFromExchange() {
        return fromExchange;
    }

    public void setFromExchange(String fromExchange) {
        this.fromExchange = fromExchange;
    }

    public String getToExchange() {
        return toExchange;
    }

    public void setToExchange(String toExchange) {
        this.toExchange = toExchange;
    }

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSendToExchange() {
        return sendToExchange;
    }

    public void setSendToExchange(int sendToExchange) {
        this.sendToExchange = sendToExchange;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getBasketReference() {
        return basketReference;
    }

    public void setBasketReference(String basketReference) {
        this.basketReference = basketReference;
    }
}
