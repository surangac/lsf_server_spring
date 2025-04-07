package com.dfn.lsf.model.requestMsg;

/**
 * Created by surangac on 5/25/2015.
 */
public class CommonInqueryMessage {
    private int reqType;
    private String customerId;
    private String tradingAccountId;
    private String exchange;
    private OrderBasket lsfBasket;
    private String basketReference;
    private String currency;
    private  String params;
    private int changeParameter;
    private String value;
    private String contractId;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public OrderBasket getLsfBasket() {
        return lsfBasket;
    }

    public void setLsfBasket(OrderBasket lsfBasket) {
        this.lsfBasket = lsfBasket;
    }

    public String getBasketReference() {
        return basketReference;
    }

    public void setBasketReference(String basketReference) {
        this.basketReference = basketReference;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public int getChangeParameter() {
        return changeParameter;
    }

    public void setChangeParameter(int changeParameter) {
        this.changeParameter = changeParameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
