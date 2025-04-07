package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.AuthenticationRequest;
import com.dfn.lsf.model.AuthenticationResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Processor for authentication operations
 * This replaces the AKKA AuthenticationProcessor
 */
@Service
@Qualifier("10") // MESSAGE_TYPE_AUTHORIZATION_PROCESS
public class AuthenticationProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            AuthenticationRequest authRequest = gson.fromJson(request, AuthenticationRequest.class);
            String subMessageType = authRequest.getSubMessageType();
            
            logger.info("Processing authentication request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.LOGIN:
                    return login(authRequest);
                case LsfConstants.LOGOUT:
                    return logout(authRequest);
                case LsfConstants.VALIDATE_SESSION:
                    return validateSession(authRequest);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing authentication request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    private String login(AuthenticationRequest jsonRequest) {
        try {
            String sessionID = generateSessionID();
            String userID = jsonRequest.get("userID").getAsString();
            int channelID = jsonRequest.get("channelID").getAsInt();
            String ipAddress = jsonRequest.get("ipAddress").getAsString();
            
            String result = lsfRepository.addUpdateUserSession(
                sessionID,
                userID,
                channelID,
                LsfConstants.SESSION_ACTIVE,
                ipAddress,
                "", // No OMS session ID for login
                1 // Status 1 for active session
            );
            
            JsonObject response = new JsonObject();
            response.addProperty("status", LsfConstants.RESPONSE_SUCCESS);
            response.addProperty("sessionID", sessionID);
            response.addProperty("result", result);
            
            return response.toString();
        } catch (Exception e) {
            logger.error("Error during login", e);
            return createErrorResponse("Error during login: " + e.getMessage());
        }
    }
    
    /**
     * Handles logout requests
     */
    private String logout(AuthenticationRequest request) {
        logger.info("Processing logout request");
        
        AuthenticationResponse response = new AuthenticationResponse();
        
        try {
            // Validate required fields
            if (request.getSecurityKey() == null || request.getSecurityKey().isEmpty()) {
                response.setResponseCode(400);
                response.setErrorMessage("Security key is required");
                return gson.toJson(response);
            }
            
            // Logout user
            boolean success = lsfRepository.logoutUser(request.getSecurityKey());
            
            // Set response data
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Logout successful");
            } else {
                response.setResponseCode(400);
                response.setErrorMessage("Logout failed, session may not exist");
            }
            
        } catch (Exception e) {
            logger.error("Error during logout", e);
            response.setResponseCode(500);
            response.setErrorMessage("Error during logout: " + e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles session validation requests
     */
    private String validateSession(AuthenticationRequest request) {
        logger.info("Processing validate session request");
        
        AuthenticationResponse response = new AuthenticationResponse();
        
        try {
            // Validate required fields
            if (request.getSecurityKey() == null || request.getSecurityKey().isEmpty()) {
                response.setResponseCode(400);
                response.setErrorMessage("Security key is required");
                return gson.toJson(response);
            }
            
            // Validate session
            boolean isValid = lsfRepository.validateSession(request.getSecurityKey());
            
            // Set response data
            if (isValid) {
                // Get user details
                Map<String, Object> userInfo = lsfRepository.getUserBySession(request.getSecurityKey());
                
                response.setResponseCode(200);
                response.setResponseMessage("Session is valid");
                response.setSecurityKey(request.getSecurityKey());
                response.setUserId(userInfo.get("USER_ID").toString());
                response.setUsername(userInfo.get("USERNAME").toString());
                response.setUserRole(userInfo.get("ROLE").toString());
            } else {
                response.setResponseCode(401);
                response.setErrorMessage("Session is invalid or expired");
            }
            
        } catch (Exception e) {
            logger.error("Error during session validation", e);
            response.setResponseCode(500);
            response.setErrorMessage("Error during session validation: " + e.getMessage());
        }
        
        return gson.toJson(response);
    }

    private String generateSessionID() {
        return "LSF-" + System.currentTimeMillis() + "-" + Math.round(Math.random() * 1000);
    }
    
    private String createErrorResponse(String message) {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}