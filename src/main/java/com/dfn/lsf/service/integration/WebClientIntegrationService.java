package com.dfn.lsf.service.integration;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.util.IntegrationConstants;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import reactor.core.publisher.Mono;

/**
 * Implementation of IntegrationService using WebClient
 * Replaces the original Helper class with modern Spring Boot approach
 */
@Service
public class WebClientIntegrationService implements IntegrationService {

    private static final Logger log = LoggerFactory.getLogger(WebClientIntegrationService.class);
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
    
    private final WebClient webClient;
    private final Gson gson;
    private final Executor virtualThreadExecutor;
    private final LSFRepository lsfRepository;
    
    @Value("${integration.timeout:60000}")
    private long defaultTimeoutMillis;
    
    @Value("${integration.oms.base-url}")
    private String omsBaseUrl;
    
    @Value("${integration.notification.base-url}")
    private String notificationBaseUrl;
    
    @Value("${integration.iflex.base-url}")
    private String iflexBaseUrl;
    
    @Autowired
    public WebClientIntegrationService(WebClient webClient, Executor virtualThreadExecutor, 
                                      LSFRepository lsfRepository) {
        this.webClient = webClient;
        this.gson = new Gson();
        this.virtualThreadExecutor = virtualThreadExecutor;
        this.lsfRepository = lsfRepository;
    }
    
    @Override
    public String getCustomerRelatedOmsData(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_CUSTOMER_INFO_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String getCashAccountRelatedOmsData(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_CASH_ACCOUNT_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String getPortfolioRelatedOmsData(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_PORTFOLIO_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String getSymbolRelatedOmsData(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_SYMBOL_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String validateSso(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        try {
            String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_VALIDATE_SSO_ENDPOINT);
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            if (responseMap != null && responseMap.containsKey(IntegrationConstants.RESPONSE_OBJECT_KEY)) {
                return responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
            }
            return null;
        } catch (Exception e) {
            log.error("Error validating SSO", e);
            return null;
        }
    }
    
    @Override
    public String sendOrderRelatedOmsRequest(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_PURCHASE_ORDER_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String sendB2bRelatedOmsRequest(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_B2B_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String sendOmsCommonRequest(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_COMMON_REQUEST_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public String sendOmsValidationRequest(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_VALIDATION_ENDPOINT);
        log.info("OMS RESPONSE: {}", response);
        return response;
    }
    
    @Override
    public boolean sendSmsNotification(String requestBody) {
        try {
            sendRequest(requestBody, notificationBaseUrl + IntegrationConstants.SMS_ENDPOINT);
            return true;
        } catch (Exception e) {
            log.error("Error sending SMS notification", e);
            return false;
        }
    }
    
    @Override
    public boolean sendEmailNotification(String requestBody) {
        try {
            sendRequest(requestBody, notificationBaseUrl + IntegrationConstants.EMAIL_ENDPOINT);
            return true;
        } catch (Exception e) {
            log.error("Error sending email notification", e);
            return false;
        }
    }
    
    @Override
    public String sendMessageToIflex(String requestBody) {
        try {
            log.info("iFlex REQUEST: {}", requestBody);
            String response = sendRequest(requestBody, iflexBaseUrl);
            log.info("iFlex RESPONSE: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error sending message to iFlex", e);
            return "";
        }
    }
    
    @Override
    public String sendMessageToOms(String requestBody) {
        try {
            log.info("OMS REQUEST: {}", requestBody);
            String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_CUSTOMER_INFO_ENDPOINT);
            log.info("OMS RESPONSE: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error sending message to OMS", e);
            return "";
        }
    }
    
    @Override
    public String sendMessageToOmsForTradingAccount(String requestBody) {
        try {
            log.info("OMS REQUEST: {}", requestBody);
            String response = sendRequest(requestBody, omsBaseUrl + IntegrationConstants.OMS_TRADING_ACCOUNT_ENDPOINT);
            log.info("OMS RESPONSE: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error getting trading account from OMS", e);
            return "";
        }
    }
    
    @Override
    public String sendSettlementRelatedOmsRequest(String requestBody, String producerName) {
        log.info("OMS REQUEST: {}", requestBody);
        try {
            // Map producer name to URL or use default settlement endpoint
            String url = mapProducerNameToUrl(producerName, 
                    omsBaseUrl + IntegrationConstants.OMS_SETTLEMENT_ENDPOINT);
            
            String response = sendRequest(requestBody, url);
            log.info("OMS RESPONSE: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error sending settlement request to OMS", e);
            return null;
        }
    }
    
    @Override
    public CommonResponse processOmsCommonResponse(String response) {
        log.info("Processing OMS RESPONSE: {}", response);
        CommonResponse commonResponse = new CommonResponse();
        
        if (response == null || response.isEmpty()) {
            commonResponse.setResponseCode(500);
            return commonResponse;
        }
        
        try {
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            String responseObject = responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
            
            String[] resultArray = responseObject.split(IntegrationConstants.RESPONSE_DELIMITER);
            if (resultArray.length > 0) {
                if (IntegrationConstants.STATUS_SUCCESS.equals(resultArray[0])) {
                    commonResponse.setResponseCode(200);
                    if (resultArray.length > 1) {
                        commonResponse.setResponseMessage(resultArray[1]);
                    }
                } else {
                    commonResponse.setResponseCode(500);
                    if (resultArray.length > 1) {
                        commonResponse.setErrorMessage(resultArray[1]);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing OMS response", e);
            commonResponse.setResponseCode(500);
        }
        
        return commonResponse;
    }
    
    @Override
    public CommonResponse processOmsCommonResponseAdminFee(String response) {
        log.info("Processing OMS RESPONSE: {}", response);
        CommonResponse commonResponse = new CommonResponse();
        
        if (response == null || response.isEmpty()) {
            commonResponse.setResponseCode(500);
            return commonResponse;
        }
        
        try {
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            String responseObject = responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
            
            String[] resultArray = responseObject.split(IntegrationConstants.RESPONSE_DELIMITER);
            if (IntegrationConstants.STATUS_SUCCESS.equals(resultArray[0])) {
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage(resultArray[1]);
            } else {
                commonResponse.setResponseCode(500);
                commonResponse.setErrorMessage(resultArray[1]);
            }
        } catch (Exception e) {
            log.error("Error processing OMS admin fee response", e);
            commonResponse.setResponseCode(500);
        }
        
        return commonResponse;
    }
    
    @Override
    public CommonResponse processOmsCommonResponseAccountCreation(String response) {
        log.info("Processing OMS RESPONSE: {}", response);
        CommonResponse commonResponse = new CommonResponse();
        
        if (response == null || response.isEmpty()) {
            commonResponse.setResponseCode(500);
            return commonResponse;
        }
        
        try {
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            String responseObject = responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
            
            String[] resultArray = responseObject.split(IntegrationConstants.RESPONSE_DELIMITER);
            if (resultArray.length > 0) {
                commonResponse.setResponseCode(Integer.parseInt(resultArray[0]));
            }
        } catch (Exception e) {
            log.error("Error processing OMS account creation response", e);
            commonResponse.setResponseCode(500);
        }
        
        return commonResponse;
    }
    
    @Override
    public CommonResponse processOmsCommonResponseAccountDeletion(String response) {
        log.info("Processing OMS RESPONSE: {}", response);
        CommonResponse commonResponse = new CommonResponse();
        
        if (response == null || response.isEmpty()) {
            commonResponse.setResponseCode(-3);
            return commonResponse;
        }
        
        try {
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            String responseObject = responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
            
            String[] resultArray = responseObject.split(IntegrationConstants.RESPONSE_DELIMITER);
            if (resultArray.length > 0) {
                commonResponse.setResponseCode(Integer.parseInt(resultArray[0]));
            }
        } catch (Exception e) {
            log.error("Error processing OMS account deletion response", e);
            commonResponse.setResponseCode(-3);
        }
        
        return commonResponse;
    }
    
    @Override
    public String handleOmsRequest(String requestBody, String producerName) {
        log.info("OMS REQUEST: {}", requestBody);
        try {
            String url = mapProducerNameToUrl(producerName, omsBaseUrl);
            String stringResponse = sendRequest(requestBody, url);
            log.info("OMS RESPONSE: {}", stringResponse);
            
            Map<String, Object> responseMap = gson.fromJson(stringResponse, MAP_TYPE);
            return responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
        } catch (Exception e) {
            log.error("Error handling OMS request", e);
            return null;
        }
    }
    
    @Override
    public String acknowledgeReportGeneration(String requestBody, String producerName) {
        try {
            String url = mapProducerNameToUrl(producerName, omsBaseUrl);
            sendRequest(requestBody, url);
            return null; // Original method doesn't actually use the response
        } catch (Exception e) {
            log.error("Error acknowledging report generation", e);
            return null;
        }
    }
    
    @Override
    public boolean sendB2bRequest(String requestBody, String producerName) {
        try {
            String url = mapProducerNameToUrl(producerName, omsBaseUrl + IntegrationConstants.OMS_B2B_ENDPOINT);
            String response = sendRequest(requestBody, url);
            
            Map<String, Object> responseMap = gson.fromJson(response, MAP_TYPE);
            String status = responseMap.get(IntegrationConstants.STATUS_KEY).toString();
            
            return IntegrationConstants.STATUS_OK.equalsIgnoreCase(status);
        } catch (Exception e) {
            log.error("Error sending B2B request", e);
            return false;
        }
    }
    
    @Override
    public CommonResponse cancelMlPendingBaskets(String requestBody) {
        log.info("OMS REQUEST: {}", requestBody);
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        
        try {
            String omsResponse = sendRequest(requestBody, 
                    omsBaseUrl + IntegrationConstants.OMS_COMMON_REQUEST_ENDPOINT);
            log.info("OMS RESPONSE: {}", omsResponse);
            
            if (omsResponse != null && !omsResponse.isEmpty()) {
                Map<String, Object> responseMap = gson.fromJson(omsResponse, MAP_TYPE);
                String responseObject = responseMap.get(IntegrationConstants.RESPONSE_OBJECT_KEY).toString();
                
                String[] resultArray = responseObject.split(IntegrationConstants.RESPONSE_DELIMITER);
                if (resultArray.length > 0) {
                    if ("1".equalsIgnoreCase(resultArray[0])) {
                        response.setResponseCode(200);
                    } else if ("-1".equalsIgnoreCase(resultArray[0])) {
                        response.setResponseCode(500);
                        response.setErrorMessage("There are pending Orders.");
                    } else if ("-2".equalsIgnoreCase(resultArray[0])) {
                        response.setResponseCode(500);
                        response.setErrorMessage("General Failure.");
                    } else {
                        response.setResponseCode(500);
                        response.setErrorMessage("Failure.");
                    }
                }
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failure");
            }
        } catch (Exception e) {
            log.error("Error canceling ML pending baskets", e);
            response.setResponseCode(500);
            response.setErrorMessage("Error While Canceling the Order");
        }
        
        return response;
    }
    
    @Override
    public CompletableFuture<String> sendRequestAsync(String requestBody, String producerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = mapProducerNameToUrl(producerName, omsBaseUrl);
                return sendRequest(requestBody, url);
            } catch (Exception e) {
                log.error("Error in async request", e);
                throw new RuntimeException("Error in async request", e);
            }
        }, virtualThreadExecutor);
    }
    
    /**
     * Send HTTP request using WebClient
     *
     * @param requestBody JSON request body
     * @param url Target URL
     * @return Response as JSON string
     */
    private String sendRequest(String requestBody, String url) {
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    log.error("Error response: {} {}", response.statusCode(), response.toString());
                    return Mono.error(new WebClientResponseException(
                            response.statusCode().value(),
                            "Error calling external service",
                            null, null, null));
                })
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(defaultTimeoutMillis))
                .onErrorResume(e -> {
                    log.error("Error in HTTP request", e);
                    throw new RuntimeException("Error in HTTP request", e);
                })
                .block();
    }
    
    /**
     * Map producer name to URL
     *
     * @param producerName Producer name
     * @param defaultUrl Default URL if no mapping found
     * @return URL
     */
    private String mapProducerNameToUrl(String producerName, String defaultUrl) {
        // Map producer names to URLs
        Map<String, String> producerUrlMap = new HashMap<>();
        producerUrlMap.put(IntegrationConstants.PRODUCER_IFLEX, iflexBaseUrl);
        producerUrlMap.put(IntegrationConstants.PRODUCER_OMS, omsBaseUrl);
        producerUrlMap.put(IntegrationConstants.PRODUCER_OMS_TRADING, omsBaseUrl + IntegrationConstants.OMS_TRADING_ACCOUNT_ENDPOINT);
        producerUrlMap.put("http_producer_to_OMS_for_trading", omsBaseUrl + IntegrationConstants.OMS_TRADING_ACCOUNT_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_CUSTOMER_INFO, omsBaseUrl + IntegrationConstants.OMS_CUSTOMER_INFO_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_CASH_ACCOUNT_RELATED, omsBaseUrl + IntegrationConstants.OMS_CASH_ACCOUNT_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_PORTFOLIO_RELATED, omsBaseUrl + IntegrationConstants.OMS_PORTFOLIO_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_SYMBOL_RELATED, omsBaseUrl + IntegrationConstants.OMS_SYMBOL_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_REQ_VALIDATE_SSO, omsBaseUrl + IntegrationConstants.OMS_VALIDATE_SSO_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_PURCHASE_ORDER, omsBaseUrl + IntegrationConstants.OMS_PURCHASE_ORDER_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_B2B_RELATED, omsBaseUrl + IntegrationConstants.OMS_B2B_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_COMMON_REQUEST_RELATED, omsBaseUrl + IntegrationConstants.OMS_COMMON_REQUEST_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_OMS_VALIDATION_RELATED, omsBaseUrl + IntegrationConstants.OMS_VALIDATION_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_SMS_RELATED, notificationBaseUrl + IntegrationConstants.SMS_ENDPOINT);
        producerUrlMap.put(LsfConstants.HTTP_PRODUCER_EMAIL_RELATED, notificationBaseUrl + IntegrationConstants.EMAIL_ENDPOINT);
        
        return producerUrlMap.getOrDefault(producerName, defaultUrl);
    }
}