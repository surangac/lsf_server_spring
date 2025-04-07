package com.dfn.lsf.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication request model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    
    private String messageType;
    private String subMessageType;
    private String username;
    private String password;
    private String ipAddress;
    private int channelId;
    private String securityKey;
    
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public int getChannelId() {
        return channelId;
    }
    
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
    
    public String getSecurityKey() {
        return securityKey;
    }
    
    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }
}