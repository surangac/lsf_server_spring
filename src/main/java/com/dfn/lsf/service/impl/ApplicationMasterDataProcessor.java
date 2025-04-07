package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.ApplicationFlowRequest;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor for application master data operations
 * This replaces the AKKA ApplicationMasterDataProcessor
 */
@Service
@Qualifier("3") // MESSAGE_TYPE_MASTER_DATA_PROCESS
public class ApplicationMasterDataProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMasterDataProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing application master data request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.REQ_APP_FLOW:
                    return getApplicationFlow();
                case LsfConstants.GET_APPLICATION_HISTORY:
                    return getApplicationHistory(requestMap);
                case LsfConstants.GET_APPLICATION_HISTORY_DETAILS:
                    return getApplicationHistoryDetails(requestMap);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing application master data request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handles reqAppFlow requests
     */
    private String getApplicationFlow() {
        logger.info("Getting application flow");
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> applicationFlow = lsfRepository.getApplicationFlow();
            response.setResponseCode(200);
            response.setResponseObject(applicationFlow);
        } catch (Exception e) {
            logger.error("Error getting application flow", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles applicationHistory requests
     */
    private String getApplicationHistory(Map<String, Object> requestMap) {
        logger.info("Getting application history");
        
        if (!requestMap.containsKey("applicationId")) {
            return createErrorResponse("Missing applicationId parameter");
        }
        
        String applicationId = requestMap.get("applicationId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> applicationHistory = lsfRepository.getApplicationStatus(applicationId);
            response.setResponseCode(200);
            response.setResponseObject(applicationHistory);
        } catch (Exception e) {
            logger.error("Error getting application history", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles applicationHistoryDetails requests
     */
    private String getApplicationHistoryDetails(Map<String, Object> requestMap) {
        logger.info("Getting application history details");
        
        if (!requestMap.containsKey("applicationId")) {
            return createErrorResponse("Missing applicationId parameter");
        }
        
        String applicationId = requestMap.get("applicationId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> applicationHistoryDetails = lsfRepository.getApplicationFlowHistory(applicationId);
            response.setResponseCode(200);
            response.setResponseObject(applicationHistoryDetails);
        } catch (Exception e) {
            logger.error("Error getting application history details", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Processes application flow status update
     */
    public String updateApplicationFlowStatus(String request) {
        logger.info("Updating application flow status");
        
        try {
            ApplicationFlowRequest flowRequest = gson.fromJson(request, ApplicationFlowRequest.class);
            
            if (flowRequest.getApplicationId() == null || flowRequest.getApplicationId().isEmpty()) {
                return createErrorResponse("Missing application ID");
            }
            
            if (flowRequest.getUserId() == null || flowRequest.getUserId().isEmpty()) {
                return createErrorResponse("Missing user ID");
            }
            
            if (flowRequest.getMessageType() == null || flowRequest.getMessageType().isEmpty()) {
                return createErrorResponse("Missing message type");
            }
            
            int status = Integer.parseInt(flowRequest.getSubMessageType());
            
            boolean success = lsfRepository.updateApplicationFlowStatus(
                    flowRequest.getApplicationId(),
                    flowRequest.getUserId(),
                    status,
                    flowRequest.getIpAddress(),
                    flowRequest.getComments());
            
            CommonResponse response = new CommonResponse();
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Application flow status updated successfully");
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to update application flow status");
            }
            
            return gson.toJson(response);
        } catch (Exception e) {
            logger.error("Error updating application flow status", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}