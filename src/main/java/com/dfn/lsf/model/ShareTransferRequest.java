package com.dfn.lsf.model;

/**
 * Share transfer request model
 */
public class ShareTransferRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String sourceAccount;
    private String destinationAccount;
    private String symbolCode;
    private int quantity;
    private String applicationId;
    private String description;
    
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
    
    public String getSourceAccount() {
        return sourceAccount;
    }
    
    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }
    
    public String getDestinationAccount() {
        return destinationAccount;
    }
    
    public void setDestinationAccount(String destinationAccount) {
        this.destinationAccount = destinationAccount;
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
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}