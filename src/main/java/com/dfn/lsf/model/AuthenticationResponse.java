package com.dfn.lsf.model;

/**
 * Authentication response model
 */
public class AuthenticationResponse {
    
    private int responseCode;
    private String responseMessage;
    private String errorMessage;
    private String securityKey;
    private String userId;
    private String username;
    private String userRole;
    
    public int getResponseCode() {
        return responseCode;
    }
    
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    public String getResponseMessage() {
        return responseMessage;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getSecurityKey() {
        return securityKey;
    }
    
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}