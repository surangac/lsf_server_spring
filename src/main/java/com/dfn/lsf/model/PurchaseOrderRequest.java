package com.dfn.lsf.model;

/**
 * Purchase order request model
 */
public class PurchaseOrderRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String applicationId;
    private String tradingAccount;
    private String cashAccount;
    private String symbolCode;
    private int quantity;
    private double orderPrice;
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getSubMessageType() {
        return subMessageType;
    }
    
    public void setSubMessageType(String subMessageType) {
        this.subMessageType = subMessageType;
    }
    
    public String getSecurityKey() {
        return securityKey;
    }
    
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getTradingAccount() {
        return tradingAccount;
    }
    
    public void setTradingAccount(String tradingAccount) {
        this.tradingAccount = tradingAccount;
    }
    
    public String getCashAccount() {
        return cashAccount;
    }
    
    public void setCashAccount(String cashAccount) {
        this.cashAccount = cashAccount;
    }
    
    public String getSymbolCode() {
        return symbolCode;
    }
    
    public void setSymbolCode(String symbolCode) {
        this.symbolCode = symbolCode;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getOrderPrice() {
        return orderPrice;
    }
    
    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }
}