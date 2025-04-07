package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.NotificationRequest;
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
 * Processor for notification operations
 * This replaces the AKKA NotificationProcessor
 */
@Service
@Qualifier("11") // MESSAGE_TYPE_NOTIFICATION_PROCESSOR
public class NotificationProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing notification request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.SENT_NOTIFICATION_FOR_A_CUSTOMER:
                    return sendNotificationToCustomer(request);
                case LsfConstants.REQ_CLIENT_WEB_NOTIFICATIONS:
                    return getClientWebNotifications(requestMap);
                case LsfConstants.UPDATE_CLIENT_READ_NOTIFICATIONS:
                    return updateReadNotification(requestMap);
                case LsfConstants.REQ_MSG_CONFIGURATION:
                    return getMessageConfiguration();
                case LsfConstants.GET_NOTIFICATION_HISTORY:
                    return getNotificationHistory(requestMap);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing notification request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handles sending notification to a customer
     */
    private String sendNotificationToCustomer(String request) {
        logger.info("Sending notification to customer");
        
        try {
            NotificationRequest notificationRequest = gson.fromJson(request, NotificationRequest.class);
            
            // Validate required fields
            if (notificationRequest.getCustomerId() == null || notificationRequest.getCustomerId().isEmpty()) {
                return createErrorResponse("Customer ID is required");
            }
            
            if (notificationRequest.getMessageContent() == null || notificationRequest.getMessageContent().isEmpty()) {
                return createErrorResponse("Message content is required");
            }
            
            if (notificationRequest.getNotificationType() == null || notificationRequest.getNotificationType().isEmpty()) {
                return createErrorResponse("Notification type is required");
            }
            
            // Send notification
            boolean success = lsfRepository.sendNotification(
                    notificationRequest.getCustomerId(),
                    notificationRequest.getSubMessageType(),
                    notificationRequest.getMessageContent(),
                    notificationRequest.getSubject(),
                    notificationRequest.getNotificationType());
            
            CommonResponse response = new CommonResponse();
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Notification sent successfully");
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to send notification");
            }
            
            return gson.toJson(response);
        } catch (Exception e) {
            logger.error("Error sending notification", e);
            return createErrorResponse("Error sending notification: " + e.getMessage());
        }
    }
    
    /**
     * Handles getting web notifications for a client
     */
    private String getClientWebNotifications(Map<String, Object> requestMap) {
        logger.info("Getting web notifications for client");
        
        if (!requestMap.containsKey("customerId")) {
            return createErrorResponse("Customer ID is required");
        }
        
        String customerId = requestMap.get("customerId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> notifications = lsfRepository.getCustomerNotifications(customerId);
            response.setResponseCode(200);
            response.setResponseObject(notifications);
        } catch (Exception e) {
            logger.error("Error getting web notifications", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles updating notification read status
     */
    private String updateReadNotification(Map<String, Object> requestMap) {
        logger.info("Updating notification read status");
        
        if (!requestMap.containsKey("notificationId")) {
            return createErrorResponse("Notification ID is required");
        }
        
        String notificationId = requestMap.get("notificationId").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            boolean success = lsfRepository.markNotificationAsRead(notificationId);
            
            if (success) {
                response.setResponseCode(200);
                response.setResponseMessage("Notification marked as read");
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to mark notification as read");
            }
        } catch (Exception e) {
            logger.error("Error updating notification read status", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles getting message configuration
     */
    private String getMessageConfiguration() {
        logger.info("Getting message configuration");
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> configurations = lsfRepository.getNotificationConfigurations();
            response.setResponseCode(200);
            response.setResponseObject(configurations);
        } catch (Exception e) {
            logger.error("Error getting message configuration", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles getting notification history
     */
    private String getNotificationHistory(Map<String, Object> requestMap) {
        logger.info("Getting notification history");
        
        if (!requestMap.containsKey("customerId")) {
            return createErrorResponse("Customer ID is required");
        }
        
        String customerId = requestMap.get("customerId").toString();
        String fromDate = (String) requestMap.getOrDefault("fromDate", "");
        String toDate = (String) requestMap.getOrDefault("toDate", "");
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> history = lsfRepository.getNotificationHistory(customerId, fromDate, toDate);
            response.setResponseCode(200);
            response.setResponseObject(history);
        } catch (Exception e) {
            logger.error("Error getting notification history", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }
}