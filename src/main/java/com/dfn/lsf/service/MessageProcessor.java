package com.dfn.lsf.service;

/**
 * Interface for message processors
 * This replaces the AKKA processor functionality
 */
public interface MessageProcessor {
    
    /**
     * Process a message and return a response
     * 
     * @param request The JSON request string
     * @return The JSON response string
     */
    String process(String request);
}