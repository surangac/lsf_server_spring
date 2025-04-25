package com.dfn.lsf.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.DeliveryMode;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class JmsProducerConfig {

    @Value("${lsf.jms.queue.from-lsf:FROM_LSF_QUEUE}")
    private String fromLsfQueue;

    /**
     * Create a dedicated JMS template for sending messages to FROM_LSF_QUEUE
     */
    @Bean(name = "omsQueueProducer")
    public JmsTemplate omsQueueProducer(ConnectionFactory connectionFactory) {
        log.info("Creating OMS Queue Producer for queue: {}", fromLsfQueue);
        
        JmsTemplate producer = new JmsTemplate(connectionFactory);
        producer.setDefaultDestinationName(fromLsfQueue);
        
        // Configure delivery settings
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setPriority(4);
        producer.setTimeToLive(0); // No expiration
        producer.setExplicitQosEnabled(true);
        
        return producer;
    }
}