package com.dfn.lsf.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.repository.LSFRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalParametersService {

    private final LSFRepository lsfRepository;

    @Scheduled(fixedRateString = "${global.parameters.refresh.rate:900000}") // Default 15 minutes (in milliseconds)
    public void refreshGlobalParameters() {
        try {
            log.debug("Refreshing global parameters from repository");
            
            // Get parameters from repository
            java.util.List<GlobalParameters> paramsList = lsfRepository.getGlobalParameters();
            
            if (paramsList != null && !paramsList.isEmpty()) {
                // Reset the singleton instance with fresh data
                GlobalParameters.reset(paramsList.get(0));
                log.debug("Global parameters refreshed successfully");
            } else {
                log.warn("No global parameters found in repository");
            }
        } catch (Exception e) {
            log.error("Error refreshing global parameters: {}", e.getMessage(), e);
        }
    }
    
    // Method to manually trigger refresh
    public void manualRefresh() {
        refreshGlobalParameters();
    }
    
    // Call this method during application startup
    public void initialize() {
        refreshGlobalParameters();
    }
} 