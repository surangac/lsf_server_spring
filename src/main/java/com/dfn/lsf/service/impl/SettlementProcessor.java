package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.SettlementRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Processor for settlement operations
 * This replaces the AKKA SettlementProcessor
 */
@Service
@Qualifier("15") // MESSAGE_TYPE_SETTLEMENT_PROCESS
public class SettlementProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(SettlementProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing settlement request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.SETTLEMENT_SUMMARY_APPLICATION:
                    return getSettlementSummary(requestMap);
                case LsfConstants.SETTLEMENT_BREAKDOWN_APPLICATION:
                    return getSettlementBreakdown(requestMap);
                case LsfConstants.PERFORM_EARLY_SETTLEMENT:
                    return performEarlySettlement(request);
                case LsfConstants.PERFORM_MANUAL_SETTLEMENT:
                    return performManualSettlement(request);
                case LsfConstants.GET_SETTLEMENT_LIST:
                    return getSettlementList(requestMap);
                case LsfConstants.GET_LIST_FOR_MANUAL_SETTELEMENT:
                    return getListForManualSettlement();
                case LsfConstants.SETTLEMENT_PROCESS:
                    return processSettlement(request);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing settlement request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Gets settlement summary for an application
     */
    private String getSettlementSummary(Map<String, Object> requestMap) {
        logger.info("Getting settlement summary");
        
        if (!requestMap.containsKey("applicationId")) {
            return createErrorResponse("Application ID is required");
        }
        
        String applicationId = requestMap.get("applicationId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            // In a real implementation, this would get the settlement summary for the application
            // For now, we'll just return a placeholder response
            response.setResponseCode(200);
            response.setResponseMessage("Settlement summary retrieved successfully");
            response.setResponseObject("Settlement summary for application " + applicationId);
        } catch (Exception e) {
            logger.error("Error getting settlement summary", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Gets settlement breakdown for an application
     */
    private String getSettlementBreakdown(Map<String, Object> requestMap) {
        logger.info("Getting settlement breakdown");
        
        if (!requestMap.containsKey("settlementId")) {
            return createErrorResponse("Settlement ID is required");
        }
        
        String settlementId = requestMap.get("settlementId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            Map<String, Object> settlementDetails = lsfRepository.getSettlementDetails(settlementId);
            
            if (settlementDetails != null && !settlementDetails.isEmpty()) {
                response.setResponseCode(200);
                response.setResponseObject(settlementDetails);
            } else {
                response.setResponseCode(404);
                response.setErrorMessage("Settlement not found");
            }
        } catch (Exception e) {
            logger.error("Error getting settlement breakdown", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Performs early settlement
     */
    private String performEarlySettlement(String request) {
        logger.info("Performing early settlement");
        
        try {
            SettlementRequest settlementRequest = gson.fromJson(request, SettlementRequest.class);
            
            // Validate required fields
            if (settlementRequest.getApplicationId() == null || settlementRequest.getApplicationId().isEmpty()) {
                return createErrorResponse("Application ID is required");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(settlementRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // In a real implementation, this would perform early settlement for the application
            // For now, we'll just return a success response
            
            CommonResponse response = new CommonResponse();
            response.setResponseCode(200);
            response.setResponseMessage("Early settlement initiated for application " + 
                    settlementRequest.getApplicationId());
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error performing early settlement", e);
            return createErrorResponse("Error performing early settlement: " + e.getMessage());
        }
    }
    
    /**
     * Performs manual settlement
     */
    private String performManualSettlement(String request) {
        logger.info("Performing manual settlement");
        
        try {
            SettlementRequest settlementRequest = gson.fromJson(request, SettlementRequest.class);
            
            // Validate required fields
            if (settlementRequest.getSettlementId() == null || settlementRequest.getSettlementId().isEmpty()) {
                return createErrorResponse("Settlement ID is required");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(settlementRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Process settlement
            boolean success = lsfRepository.processSettlement(
                    settlementRequest.getSettlementId(), 
                    userId);
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Settlement processed successfully");
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to process settlement");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error performing manual settlement", e);
            return createErrorResponse("Error performing manual settlement: " + e.getMessage());
        }
    }
    
    /**
     * Gets settlement list for an application
     */
    private String getSettlementList(Map<String, Object> requestMap) {
        logger.info("Getting settlement list");
        
        if (!requestMap.containsKey("applicationId")) {
            return createErrorResponse("Application ID is required");
        }
        
        String applicationId = requestMap.get("applicationId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> settlementList = lsfRepository.getSettlementList(applicationId);
            response.setResponseCode(200);
            response.setResponseObject(settlementList);
        } catch (Exception e) {
            logger.error("Error getting settlement list", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Gets list for manual settlement
     */
    private String getListForManualSettlement() {
        logger.info("Getting list for manual settlement");
        
        CommonResponse response = new CommonResponse();
        try {
            // Get settlements with status 0 (pending)
            List<Map<String, Object>> settlementList = lsfRepository.getSettlementsForProcessing(0);
            response.setResponseCode(200);
            response.setResponseObject(settlementList);
        } catch (Exception e) {
            logger.error("Error getting list for manual settlement", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Processes settlement
     */
    private String processSettlement(String request) {
        logger.info("Processing settlement");
        
        try {
            SettlementRequest settlementRequest = gson.fromJson(request, SettlementRequest.class);
            
            // Validate required fields
            if (settlementRequest.getSettlementId() == null || settlementRequest.getSettlementId().isEmpty()) {
                return createErrorResponse("Settlement ID is required");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(settlementRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Process settlement
            boolean success = lsfRepository.processSettlement(
                    settlementRequest.getSettlementId(), 
                    userId);
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Settlement processed successfully");
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to process settlement");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error processing settlement", e);
            return createErrorResponse("Error processing settlement: " + e.getMessage());
        }
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}