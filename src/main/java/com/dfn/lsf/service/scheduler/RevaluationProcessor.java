package com.dfn.lsf.service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dfn.lsf.service.LsfCoreService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RevaluationProcessor {

private final LsfCoreService lsfCore;
    
    @Scheduled(fixedRateString = "${scheduler.revaluation.processor.rate:900000}") // Default 15 minutes (in milliseconds)
    public void revaluationProcessor() {
        log.info("Revaluation processor started");
        lsfCore.reValuationProcess();
    }
    
}
