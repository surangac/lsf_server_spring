package com.dfn.lsf.model;

/**
 * Settlement request model
 */
public class SettlementRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String settlementId;
    private String applicationId;
    
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
    
    public String getSettlementId() {
        return settlementId;
    }
    
    public void setSettlementId(String settlementId) {
        this.settlementId = settlementId;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}