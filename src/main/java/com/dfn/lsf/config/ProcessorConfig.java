package com.dfn.lsf.config;

import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for message processors
 * This registers all message processors by their message type
 */
@Configuration
public class ProcessorConfig {

    @Bean
    public Map<Integer, MessageProcessor> messageProcessors(List<MessageProcessor> processors) {

        Map<Integer, MessageProcessor> registry = new HashMap<>();

        for (MessageProcessor processor : processors) {
            MessageType annotation = processor.getClass().getAnnotation(MessageType.class);
            if (annotation != null) {
                registry.put(annotation.value(), processor);
            }
        }

        return registry;
    }
}