package com.dfn.lsf.model;

import java.util.Map;

/**
 * Report request model
 */
public class ReportRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String reportType;
    private String reportId;
    private Map<String, Object> parameters;
    
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
    
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}