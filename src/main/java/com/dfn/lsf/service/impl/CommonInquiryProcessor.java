package com.dfn.lsf.service.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

/**
 * Processor for common inquiry requests
 * This replaces the AKKA CommonInquiryProcessor
 */
@Service
@Qualifier("16") // MESSAGE_TYPE_COMMON_INQUIRY_PROCESS
public class CommonInquiryProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonInquiryProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing common inquiry request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.GET_APPLICATIONS_FTV:
                    return getFTVList();
                case LsfConstants.REQ_DETAILED_FTV_LIST:
                    return getDetailedFTVList(requestMap);
                case LsfConstants.REQ_APPROVED_PURCHASE_ORDERS:
                    return getApprovedPurchaseOrders(requestMap);
                case LsfConstants.REQ_GET_BLACKLISTED_APPLICATION:
                    return getBlackListedApplications();
                case LsfConstants.REQ_CONVERT_NUMBER_TO_STRING:
                    return convertNumberToArabic(requestMap);
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing common inquiry request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handles applicationsFTV requests
     */
    private String getFTVList() {
        logger.info("Getting FTV list");
        
        CommonResponse response = new CommonResponse();
        try {
            List<Map<String, Object>> list = lsfRepository.getApplicationCollateralFtvList();
            DecimalFormat formatter = new DecimalFormat("#0.00");
            
            StringBuilder ftvList = new StringBuilder();
            if (list != null && !list.isEmpty()) {
                for (Map<String, Object> col : list) {
                    Object ftv = col.get("FTV");
                    if (ftv != null) {
                        if (ftvList.length() > 0) {
                            ftvList.append(",");
                        }
                        ftvList.append(formatter.format(Double.parseDouble(ftv.toString())));
                    }
                }
            }
            
            response.setResponseCode(200);
            response.setResponseObject(ftvList.toString());
        } catch (Exception e) {
            logger.error("Error getting FTV list", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Handles reqDetailedFTVList requests
     */
    private String getDetailedFTVList(Map<String, Object> requestMap) {
        String fromDate = "01012017";
        String toDate = "31122050";
        int settleStatus = 1; // -1=all, 1=unsettled, 0=settled
        
        if (requestMap.containsKey("fromDate")) {
            fromDate = requestMap.get("fromDate").toString();
        }
        if (requestMap.containsKey("toDate")) {
            toDate = requestMap.get("toDate").toString();
        }
        if (requestMap.containsKey("settlementStatus")) {
            settleStatus = Integer.parseInt(requestMap.get("settlementStatus").toString());
        }
        
        logger.info("Getting detailed FTV list with params: fromDate={}, toDate={}, settleStatus={}", 
                fromDate, toDate, settleStatus);
        
        List<Map<String, Object>> commissionDetails = lsfRepository.getCommissionDetails(toDate);
        List<Map<String, Object>> ftvInfoList = lsfRepository.getDetailedFTVList(fromDate, toDate, settleStatus);
        
        // Calculate additional fields and enrich data
        if (ftvInfoList != null && !ftvInfoList.isEmpty()) {
            for (Map<String, Object> ftvInfo : ftvInfoList) {
                // Add calculated fields
                calculateWithdrawAmount(ftvInfo);
                
                // Add commission details
                for (Map<String, Object> commission : commissionDetails) {
                    if (commission.get("TRADING_ACC_ID").equals(ftvInfo.get("TRADING_ACC"))) {
                        ftvInfo.put("COMMISSION", Double.parseDouble(commission.get("COMMISSION").toString()));
                        ftvInfo.put("COMMISSION_PREVIOUS_DAY", 
                                Double.parseDouble(commission.get("PREVIOUS_DAY_COMMISSION").toString()));
                    }
                }
            }
        }
        
        return gson.toJson(ftvInfoList);
    }
    
    /**
     * Helper method to calculate withdrawal amount
     */
    private void calculateWithdrawAmount(Map<String, Object> ftvInfo) {
        // In a real implementation, this would calculate the maximum withdraw amount
        // based on collateral value, outstanding amount, and margin levels
        
        // For now, we'll just set placeholder values
        ftvInfo.put("MAXIMUM_WITHDRAW_AMOUNT", 1000.0);
        ftvInfo.put("AVAILABLE_CASH_BALANCE", 500.0);
    }
    
    /**
     * Handles reqApprovedPurchaseOrders requests
     */
    private String getApprovedPurchaseOrders(Map<String, Object> requestMap) {
        logger.info("Getting approved purchase orders");
        
        try {
            String fromDate = null;
            String toDate = null;
            
            if (requestMap.containsKey("fromDate")) {
                fromDate = requestMap.get("fromDate").toString();
            }
            if (requestMap.containsKey("toDate")) {
                toDate = requestMap.get("toDate").toString();
            }
            
            java.util.Date fromDateObj = null;
            java.util.Date toDateObj = null;
            
            if (fromDate != null) {
                fromDateObj = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(fromDate);
            }
            if (toDate != null) {
                toDateObj = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(toDate);
            }
            
            List<Map<String, Object>> applications = lsfRepository.getApprovedPurchaseOrderApplicationList(
                    fromDateObj, toDateObj);
            
            // Enrich with status information
            for (Map<String, Object> application : applications) {
                String appId = application.get("ID").toString();
                List<Map<String, Object>> statusList = lsfRepository.getApplicationStatus(appId);
                application.put("APP_STATUS", statusList);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("applicationList", applications);
            
            return gson.toJson(response);
        } catch (ParseException e) {
            logger.error("Error parsing date", e);
            return createErrorResponse("Error parsing date: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error getting approved purchase orders", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handles reqGetBlackListedApplication requests
     */
    private String getBlackListedApplications() {
        logger.info("Getting blacklisted applications");
        
        try {
            List<Map<String, Object>> applications = lsfRepository.getBlackListedApplications();
            
            // Enrich with status information
            for (Map<String, Object> application : applications) {
                String appId = application.get("ID").toString();
                List<Map<String, Object>> statusList = lsfRepository.getApplicationStatus(appId);
                application.put("APP_STATUS", statusList);
            }
            
            return gson.toJson(applications);
        } catch (Exception e) {
            logger.error("Error getting blacklisted applications", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handles reqConvertNumberToString requests
     */
    private String convertNumberToArabic(Map<String, Object> requestMap) {
        logger.info("Converting number to string");
        
        if (!requestMap.containsKey("number")) {
            return createErrorResponse("Missing number parameter");
        }
        
        try {
            String number = requestMap.get("number").toString();
            String language = requestMap.getOrDefault("lan", "EN").toString();
            
            // In a real implementation, this would use a proper number-to-words converter
            // For now, we'll just return a placeholder
            String result;
            if ("AR".equalsIgnoreCase(language)) {
                result = "ألف ريال سعودي"; // "One thousand Saudi Riyals" in Arabic
            } else {
                result = "One thousand Saudi Riyals";
            }
            
            return result;
        } catch (Exception e) {
            logger.error("Error converting number to string", e);
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