package com.dfn.lsf.service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.repository.GlobalParametersRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalParametersService {

    private final GlobalParametersRepository globalParametersRepository;

    @PostConstruct
    public void init() {
        log.info("Initializing GlobalParametersService - loading parameters at startup");
        refreshGlobalParameters();
    }

    @Scheduled(fixedRateString = "${scheduler.global.parameters.refresh.rate:900000}") // Default 15 minutes (in milliseconds)
    @Transactional(readOnly = true)
    public void refreshGlobalParameters() {
        try {
            log.debug("Refreshing global parameters from repository");
            
            // Get parameters from repository
            GlobalParameters params = globalParametersRepository.getGlobalParameters();
            
            if (params != null) {
                // Reset the singleton instance with fresh data
                GlobalParameters.reset(params);
                log.info("Global parameters refreshed successfully. Last loaded at: {}, {}", GlobalParameters.getLastLoadTime(), params.getDefaultExchange());
            } else {
                log.error("No global parameters found in repository. This is a critical error as global parameters are required for system operation.");
                throw new IllegalStateException("No global parameters found in repository");
            }
        } catch (Exception e) {
            log.error("Error refreshing global parameters: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to refresh global parameters", e);
        }
    }
    
    // Method to manually trigger refresh
    public void manualRefresh() {
        refreshGlobalParameters();
    }

    // Method to get current parameters
    public GlobalParameters getCurrentParameters() {
        return GlobalParameters.getInstance();
    }

    // Method to update parameters
    @Transactional
    public String updateParameters(GlobalParameters parameters) {
        try {
            log.info("Updating global parameters");
            String result = globalParametersRepository.updateGlobalParameters(parameters);
            if (result != null && result.contains("Success")) {
                GlobalParameters.reset(parameters);
                log.info("Global parameters updated successfully");
            } else {
                log.error("Failed to update global parameters: {}", result);
                throw new RuntimeException("Failed to update global parameters: " + result);
            }
            return result;
        } catch (Exception e) {
            log.error("Error updating global parameters: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update global parameters", e);
        }
    }
} 