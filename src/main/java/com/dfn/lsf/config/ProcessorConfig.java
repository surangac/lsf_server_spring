package com.dfn.lsf.config;

import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for message processors
 * This registers all message processors by their message type
 */
@Configuration
public class ProcessorConfig {
    
    @Bean
    public Map<Integer, MessageProcessor> messageProcessors(
            @Qualifier("16") MessageProcessor commonInquiryProcessor,
            @Qualifier("3") MessageProcessor applicationMasterDataProcessor,
            @Qualifier("10") MessageProcessor authenticationProcessor,
            @Qualifier("11") MessageProcessor notificationProcessor,
            @Qualifier("1") MessageProcessor lsfCoreProcessor,
            @Qualifier("15") MessageProcessor settlementProcessor,
            @Qualifier("14") MessageProcessor reportGenerationProcessor) {
        
        Map<Integer, MessageProcessor> processors = new HashMap<>();
        
        // Register processors by message type
        processors.put(LsfConstants.MESSAGE_TYPE_COMMON_INQUIRY_PROCESS, commonInquiryProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_MASTER_DATA_PROCESS, applicationMasterDataProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_AUTHORIZATION_PROCESS, authenticationProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_NOTIFICATION_PROCESSOR, notificationProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_EXECUTE_CORE_PROCESS, lsfCoreProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_SETTLEMENT_PROCESS, settlementProcessor);
        processors.put(LsfConstants.MESSAGE_TYPE_REPORT_GENERATION_PROCESS, reportGenerationProcessor);
        
        // Additional processors can be added as needed
        // processors.put(LsfConstants.MESSAGE_TYPE_VALIDATE_BANK_ACC, validateBankAccProcessor);
        // processors.put(LsfConstants.MESSAGE_TYPE_APPLICATION_LIST_PROCESS, applicationListProcessor);
        // etc.
        
        return processors;
    }
}