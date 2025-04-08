package com.dfn.lsf.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.dfn.lsf.service.GlobalParametersService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ApplicationConfig {

    private final GlobalParametersService globalParametersService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // Initialize global parameters when application starts
        globalParametersService.initialize();
    }
} 