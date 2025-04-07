package com.dfn.lsf.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.ReportRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

/**
 * Processor for report generation operations
 * This replaces the AKKA ReportGenerationProcessor
 */
@Service
@Qualifier("14") // MESSAGE_TYPE_REPORT_GENERATION_PROCESS
public class ReportGenerationProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing report generation request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.GENERATE_REPORT:
                    return generateReport(request);
                case LsfConstants.GET_REPORT_STATUS:
                    return getReportStatus(requestMap);
                case LsfConstants.GET_REPORT_LIST:
                    return getReportList(requestMap);
                case LsfConstants.DOWNLOAD_REPORT:
                    return downloadReport(requestMap);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing report generation request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Generates a report
     */
    private String generateReport(String request) {
        logger.info("Generating report");
        
        try {
            ReportRequest reportRequest = gson.fromJson(request, ReportRequest.class);
            
            // Validate required fields
            if (reportRequest.getReportType() == null || reportRequest.getReportType().isEmpty()) {
                return createErrorResponse("Report type is required");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(reportRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Generate report
            String reportId = lsfRepository.generateReport(
                    reportRequest.getReportType(),
                    reportRequest.getParameters(),
                    userId);
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (reportId != null) {
                response.setResponseCode(200);
                response.setResponseMessage("Report generation initiated");
                response.setResponseObject(reportId);
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to initiate report generation");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error generating report", e);
            return createErrorResponse("Error generating report: " + e.getMessage());
        }
    }
    
    /**
     * Gets report status
     */
    private String getReportStatus(Map<String, Object> requestMap) {
        logger.info("Getting report status");
        
        if (!requestMap.containsKey("reportId")) {
            return createErrorResponse("Report ID is required");
        }
        
        String reportId = requestMap.get("reportId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            Map<String, Object> reportStatus = lsfRepository.getReportStatus(reportId);
            
            if (reportStatus != null && !reportStatus.isEmpty()) {
                response.setResponseCode(200);
                response.setResponseObject(reportStatus);
            } else {
                response.setResponseCode(404);
                response.setErrorMessage("Report not found");
            }
        } catch (Exception e) {
            logger.error("Error getting report status", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Gets report list
     */
    private String getReportList(Map<String, Object> requestMap) {
        logger.info("Getting report list");
        
        // Extract user ID from session
        String securityKey = requestMap.get("securityKey").toString();
        Map<String, Object> userInfo = lsfRepository.getUserBySession(securityKey);
        if (userInfo == null || userInfo.isEmpty()) {
            return createErrorResponse("Invalid session");
        }
        
        String userId = userInfo.get("USER_ID").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> reportList = lsfRepository.getReportList(userId);
            response.setResponseCode(200);
            response.setResponseObject(reportList);
        } catch (Exception e) {
            logger.error("Error getting report list", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Downloads a report
     */
    private String downloadReport(Map<String, Object> requestMap) {
        logger.info("Downloading report");
        
        if (!requestMap.containsKey("reportId")) {
            return createErrorResponse("Report ID is required");
        }
        
        String reportId = requestMap.get("reportId").toString();
        
        // In a real implementation, this would return the report content
        // For now, we'll just return a placeholder response
        
        CommonResponse response = new CommonResponse();
        response.setResponseCode(200);
        response.setResponseMessage("Report download initiated");
        response.setResponseObject("Report content for " + reportId);
        
        return gson.toJson(response);
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}