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
 * Controller to handle LSF admin requests
 * This replaces the AKKA consumer for admin messages
 */
@RestController
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private RequestDispatcherService dispatcherService;
    
    /**
     * Handles admin requests on the same endpoint as the original AKKA consumer
     * Maps to consumer "httpAdmin" in ABIC_InitComponents
     */
    @PostMapping("/admin/lsf")
    public ResponseEntity<String> handleAdminRequest(@RequestBody String request) {
        logger.info("Received admin request: {}", request);
        String response = dispatcherService.dispatchAdminRequest(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handles admin report requests
     * Maps to consumer "httpAdminReport" in ABIC_InitComponents
     */
    @PostMapping("/admin/lsf/report")
    public ResponseEntity<String> handleAdminReportRequest(@RequestBody String request) {
        logger.info("Received admin report request: {}", request);
        String response = dispatcherService.dispatchAdminRequest(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Handles report download requests
     * Maps to consumer "httpReportDownload" in ABIC_InitComponents
     */
    @PostMapping("/admin/lsf/report/download")
    public ResponseEntity<String> handleReportDownloadRequest(@RequestBody String request) {
        logger.info("Received report download request: {}", request);
        String response = dispatcherService.dispatchAdminRequest(request);
        return ResponseEntity.ok(response);
    }
}