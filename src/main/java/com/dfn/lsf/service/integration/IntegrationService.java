package com.dfn.lsf.service.integration;

import com.dfn.lsf.model.CommonResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.dfn.lsf.model.QueMsgDto;

/**
 * Integration service for making HTTP requests to external systems
 * Replaces the original Helper class with modern Spring Boot approach
 */
public interface IntegrationService {

    /**
     * Get customer related data from OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String getCustomerRelatedOmsData(String requestBody);

    /**
     * Get cash account related data from OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String getCashAccountRelatedOmsData(String requestBody);

    /**
     * Get portfolio related data from OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String getPortfolioRelatedOmsData(String requestBody);

    /**
     * Get symbol related data from OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String getSymbolRelatedOmsData(String requestBody);

    /**
     * Validate SSO token
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String validateSso(String requestBody);

    /**
     * Send order related request to OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendOrderRelatedOmsRequest(String requestBody);

    /**
     * Send B2B related request to OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendB2bRelatedOmsRequest(String requestBody);

    /**
     * Send common requests to OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendOmsCommonRequest(String requestBody);

    /**
     * Send validation related request to OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendOmsValidationRequest(String requestBody);

    /**
     * Send SMS notification
     *
     * @param queMsgDto QueMsgDto object
     * @return Success status
     */
    boolean sendSmsNotification(QueMsgDto queMsgDto);

    /**
     * Send email notification
     *
     * @param queMsgDto QueMsgDto object
     * @return Success status
     */
    boolean sendEmailNotification(QueMsgDto queMsgDto);

    /**
     * Send message to iFlex system
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendMessageToIflex(String requestBody);

    /**
     * Send message to OMS
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendMessageToOms(String requestBody);

    /**
     * Send message to OMS to get trading account
     *
     * @param requestBody Request body as JSON string
     * @return Response as JSON string
     */
    String sendMessageToOmsForTradingAccount(String requestBody);

    /**
     * Send settlement related request to OMS
     *
     * @param requestBody Request body as JSON string
     * @param producerName Producer name/endpoint identifier
     * @return Response as JSON string
     */
    String sendSettlementRelatedOmsRequest(String requestBody, String producerName);

    /**
     * Process OMS common response
     *
     * @param response OMS response as JSON string
     * @return Processed common response
     */
    CommonResponse processOmsCommonResponse(String response);

    /**
     * Process OMS common response for admin fee
     *
     * @param response OMS response as JSON string
     * @return Processed common response
     */
    CommonResponse processOmsCommonResponseAdminFee(String response);

    /**
     * Process OMS common response for account creation
     *
     * @param response OMS response as JSON string
     * @return Processed common response
     */
    CommonResponse processOmsCommonResponseAccountCreation(String response);

    /**
     * Process OMS common response for account deletion request
     *
     * @param response OMS response as JSON string
     * @return Processed common response
     */
    CommonResponse processOmsCommonResponseAccountDeletion(String response);

    /**
     * Handle OMS request with specified producer
     *
     * @param requestBody Request body as JSON string
     * @param producerName Producer name/endpoint identifier
     * @return Response as JSON string
     */
    String handleOmsRequest(String requestBody, String producerName);

    /**
     * Acknowledge report generation
     *
     * @param requestBody Request body as JSON string
     * @param producerName Producer name/endpoint identifier
     * @return Response as JSON string
     */
    String acknowledgeReportGeneration(String requestBody, String producerName);

    /**
     * Send B2B request to specified producer
     *
     * @param requestBody Request body as JSON string
     * @param producerName Producer name/endpoint identifier
     * @return Success status
     */
    boolean sendB2bRequest(String requestBody, String producerName);

    /**
     * Cancel margin loan pending baskets
     *
     * @param requestBody Request body as JSON string
     * @return Common response
     */
    CommonResponse cancelMlPendingBaskets(String requestBody);
    
    /**
     * Send a request to any OMS endpoint asynchronously
     *
     * @param requestBody Request body as JSON string
     * @param producerName Producer name/endpoint identifier
     * @return CompletableFuture with response
     */
    CompletableFuture<String> sendRequestAsync(String requestBody, String producerName);
}