package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OMSQueueRequest {
    private int messageType;
    private int correlationId;
    private int status;
    private double filledValue;
    private String customerId;
    private String pendingId;
    private double amount;
    private String cashAccNo;
    private String investorAccount;
    private String tradingAccount;
    private String exchangeAccount;
    private String mubasherNo;
    private String exchange;
    private String symbol;
    private int quantity;
    private double price;
    private double commission;
    private String sessionID;
    private int isLsf;
    private String basketReference;
    private double vat;
    private String contractId;
    private String openOrderValues;


    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getFilledValue() {
        return filledValue;
    }

    public void setFilledValue(double filledValue) {
        this.filledValue = filledValue;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPendingId() {
        return pendingId;
    }

    public void setPendingId(String pendingId) {
        this.pendingId = pendingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCashAccNo() {
        return cashAccNo;
    }

    public void setCashAccNo(String cashAccNo) {
        this.cashAccNo = cashAccNo;
    }

    public String getInvestorAccount() {
        return investorAccount;
    }

    public void setInvestorAccount(String investorAccount) {
        this.investorAccount = investorAccount;
    }

    public String getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(String tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public String getExchangeAccount() {
        return exchangeAccount;
    }

    public void setExchangeAccount(String exchangeAccount) {
        this.exchangeAccount = exchangeAccount;
    }

    public String getMubasherNo() {
        return mubasherNo;
    }

    public void setMubasherNo(String mubasherNo) {
        this.mubasherNo = mubasherNo;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getIsLsf() {
        return isLsf;
    }

    public void setIsLsf(int isLsf) {
        this.isLsf = isLsf;
    }

    public String getBasketReference() {
        return basketReference;
    }

    public void setBasketReference(String basketReference) {
        this.basketReference = basketReference;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getOpenOrderValues() {
        return openOrderValues;
    }

    public void setOpenOrderValues(String openOrderValues) {
        this.openOrderValues = openOrderValues;
    }
}
