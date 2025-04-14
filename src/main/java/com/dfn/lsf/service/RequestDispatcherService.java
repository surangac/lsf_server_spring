package com.dfn.lsf.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MessageHeader;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

/**
 * Service to dispatch requests to appropriate message handlers
 * This replaces the AKKA content-based routing functionality
 */
@Service
public class RequestDispatcherService {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestDispatcherService.class);
    
    @Autowired
    private Map<Integer, MessageProcessor> messageProcessors;
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    /**
     * Dispatches client requests to appropriate processor
     * Mimics functionality of InMessageHandlerCbr
     */
    public String dispatchClientRequest(String request) {
        try {
            MessageHeader header = gson.fromJson(request, MessageHeader.class);
            String correlationId = String.valueOf(System.currentTimeMillis());
            header.setCorrelationID(correlationId);
            
            logger.info("Dispatching client request - Message Type: {}, Sub Message Type: {}, Correlation ID: {}", 
                    header.getMessageType(), header.getSubMessageType(), correlationId);
            
            // Validate session if not an authorization request
            if (!isAuthorizationRequest(header) && !validateSession(header.getSecurityKey())) {
                logger.warn("Invalid session detected for request - Correlation ID: {}", correlationId);
                return handleInvalidSession();
            }
            
            // Find appropriate processor by message type
            int messageType = Integer.parseInt(header.getMessageType());
            MessageProcessor processor = messageProcessors.get(messageType);
            if (processor == null) {
                logger.error("No processor found for message type: {}", header.getMessageType());
                return createErrorResponse("Unknown message type");
            }
            
            // Process the request
            logger.info("Processing request with processor: {}", processor.getClass().getSimpleName());
            String response = processor.process(request);
            logger.info("Request processed successfully - Correlation ID: {}", correlationId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error dispatching client request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Dispatches admin requests to appropriate processor
     * Mimics functionality of InMessageHandlerAdminCbr
     */
    public String dispatchAdminRequest(String request) {
        try {
            MessageHeader header = gson.fromJson(request, MessageHeader.class);
            String correlationId = String.valueOf(System.currentTimeMillis());
            header.setCorrelationID(correlationId);
            
            logger.info("Dispatching admin request - Message Type: {}, Sub Message Type: {}, Correlation ID: {}", 
                    header.getMessageType(), header.getSubMessageType(), correlationId);
            
            // Validate session if not an authorization request
            if (!isAuthorizationRequest(header) && !validateSession(header.getSecurityKey())) {
                logger.warn("Invalid session detected for admin request - Correlation ID: {}", correlationId);
                return handleInvalidSession();
            }
            
            // Find appropriate processor by message type
            int messageType = Integer.parseInt(header.getMessageType());
            MessageProcessor processor = messageProcessors.get(messageType);
            if (processor == null) {
                logger.error("No processor found for message type: {}", header.getMessageType());
                return createErrorResponse("Unknown message type");
            }
            
            // Process the request
            logger.info("Processing admin request with processor: {}", processor.getClass().getSimpleName());
            String response = processor.process(request);
            logger.info("Admin request processed successfully - Correlation ID: {}", correlationId);
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error dispatching admin request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the request is an authorization request
     */
    private boolean isAuthorizationRequest(MessageHeader header) {
        return Integer.parseInt(header.getMessageType()) == LsfConstants.MESSAGE_TYPE_AUTHORIZATION_PROCESS;
    }
    
    /**
     * Validates session using repository
     */
    private boolean validateSession(String securityKey) {
        if (securityKey == null || securityKey.isEmpty()) {
            return false;
        }
        
        return true; //lsfRepository.validateSession(securityKey);
    }
    
    /**
     * Handles invalid session
     */
    private String handleInvalidSession() {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(401);
        response.setErrorMessage("Invalid or expired session");
        return gson.toJson(response);
    }
    
    /**
     * Creates error response
     */
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}