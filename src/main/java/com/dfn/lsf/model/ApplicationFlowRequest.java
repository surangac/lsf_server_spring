package com.dfn.lsf.model;

/**
 * Request model for application flow operations
 */
public class ApplicationFlowRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String applicationId;
    private String userId;
    private String ipAddress;
    private String comments;
    
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
}