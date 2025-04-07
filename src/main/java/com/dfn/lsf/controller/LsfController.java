package com.dfn.lsf.controller;

import com.dfn.lsf.service.RequestDispatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main controller to handle LSF client requests
 * This replaces the AKKA consumer for client messages
 */
@RestController
public class LsfController {
    
    private static final Logger logger = LoggerFactory.getLogger(LsfController.class);
    
    @Autowired
    private RequestDispatcherService dispatcherService;
    
    /**
     * Handles client requests on the same endpoint as the original AKKA consumer
     * Maps to consumer "httpClient" in ABIC_InitComponents
     */
    @PostMapping("/lsf")
    public ResponseEntity<String> handleClientRequest(@RequestBody String request) {
        logger.info("Received client request: {}", request);
        String response = dispatcherService.dispatchClientRequest(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handles report-specific requests
     * Maps to consumer "httpClientReport" in ABIC_InitComponents
     */
    @PostMapping("/lsf/report")
    public ResponseEntity<String> handleClientReportRequest(@RequestBody String request) {
        logger.info("Received client report request: {}", request);
        String response = dispatcherService.dispatchClientRequest(request);
        return ResponseEntity.ok(response);
    }
}