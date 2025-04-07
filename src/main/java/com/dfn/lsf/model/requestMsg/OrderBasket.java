package com.dfn.lsf.model.requestMsg;

import java.util.List;

import com.dfn.lsf.model.Symbol;

/**
 * Created by surangac on 6/15/2015.
 */
public class OrderBasket {
    private String customerId;
    private double loanAmount;
    private List<Symbol> symbolList;
    private String expiryDate;
    private String basketReference;
    private String tradingAccountId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public List<Symbol> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<Symbol> symbolList) {
        this.symbolList = symbolList;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBasketReference() {
        return basketReference;
    }

    public void setBasketReference(String basketReference) {
        this.basketReference = basketReference;
    }

    public String getTradingAccountId() {
        return tradingAccountId;
    }

    public void setTradingAccountId(String tradingAccountId) {
        this.tradingAccountId = tradingAccountId;
    }
}
