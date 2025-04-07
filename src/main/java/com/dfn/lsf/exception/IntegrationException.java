package com.dfn.lsf.exception;

/**
 * Exception thrown when there is an issue with external service integration
 */
public class IntegrationException extends RuntimeException {
    
    private final String serviceId;
    private final int statusCode;
    
    /**
     * Create a new integration exception
     *
     * @param message Error message
     * @param serviceId ID of the service that failed
     * @param statusCode HTTP status code if applicable
     * @param cause Original exception
     */
    public IntegrationException(String message, String serviceId, int statusCode, Throwable cause) {
        super(message, cause);
        this.serviceId = serviceId;
        this.statusCode = statusCode;
    }
    
    /**
     * Create a new integration exception without status code
     *
     * @param message Error message
     * @param serviceId ID of the service that failed
     * @param cause Original exception
     */
    public IntegrationException(String message, String serviceId, Throwable cause) {
        this(message, serviceId, 0, cause);
    }
    
    /**
     * Create a new integration exception without cause
     *
     * @param message Error message
     * @param serviceId ID of the service that failed
     * @param statusCode HTTP status code if applicable
     */
    public IntegrationException(String message, String serviceId, int statusCode) {
        this(message, serviceId, statusCode, null);
    }
    
    /**
     * Create a new integration exception with message only
     *
     * @param message Error message
     * @param serviceId ID of the service that failed
     */
    public IntegrationException(String message, String serviceId) {
        this(message, serviceId, 0, null);
    }
    
    /**
     * Get the service ID
     *
     * @return Service ID
     */
    public String getServiceId() {
        return serviceId;
    }
    
    /**
     * Get the HTTP status code
     *
     * @return Status code
     */
    public int getStatusCode() {
        return statusCode;
    }
}