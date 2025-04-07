package com.dfn.lsf.util;

/**
 * Constants for integration with external systems
 * Replaces the HTTP producer constants from LsfConstants
 */
public final class IntegrationConstants {
    
    private IntegrationConstants() {
        // Private constructor to prevent instantiation
    }
    
    // Base URLs
    public static final String OMS_BASE_URL = "${integration.oms.base-url:http://localhost:8080/oms}";
    public static final String NOTIFICATION_BASE_URL = "${integration.notification.base-url:http://localhost:8080/notification}";
    public static final String IFLEX_BASE_URL = "${integration.iflex.base-url:http://localhost:8080/iflex}";
    
    // OMS endpoints (mapped from original HTTP producer names)
    public static final String OMS_CUSTOMER_INFO_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_CASH_ACCOUNT_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_PORTFOLIO_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_SYMBOL_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_VALIDATE_SSO_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_PURCHASE_ORDER_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_B2B_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_COMMON_REQUEST_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_VALIDATION_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_TRADING_ACCOUNT_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    public static final String OMS_SETTLEMENT_ENDPOINT = "/MubasherRESTAPI/api/RestSOA/processLsfRequest";
    
    // Notification endpoints
    public static final String SMS_ENDPOINT = "/sms";
    public static final String EMAIL_ENDPOINT = "/email";
    
    // Producer name to endpoint mapping
    public static final String PRODUCER_IFLEX = "http_producer_to_Iflex";
    public static final String PRODUCER_OMS = "http_producer_to_OMS";
    public static final String PRODUCER_OMS_TRADING = "http_producer_to_OMS_for_trading";
    
    // Timeouts
    public static final int DEFAULT_TIMEOUT = 60000; // 60 seconds
    
    // Response delimiters
    public static final String RESPONSE_DELIMITER = "\\|\\|";
    
    // Response status codes
    public static final String STATUS_SUCCESS = "1";
    public static final String STATUS_ERROR = "-1";
    public static final String STATUS_GENERAL_FAILURE = "-2";
    
    // Response object key
    public static final String RESPONSE_OBJECT_KEY = "responseObject";
    public static final String STATUS_KEY = "status";
    public static final String STATUS_OK = "OK";
}