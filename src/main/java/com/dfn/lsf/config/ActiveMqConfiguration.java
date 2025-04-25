package com.dfn.lsf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;

import lombok.extern.slf4j.Slf4j;

/**
 * JMS Configuration using ActiveMQ
 */
@Configuration
@EnableJms
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class ActiveMqConfiguration {

    @Value("${lsf.jms.queue.to-lsf:TO_LSF_QUEUE}")
    private String toLsfQueueName;
    
    @Value("${lsf.jms.queue.from-lsf:FROM_LSF_QUEUE}")
    private String fromLsfQueueName;

    @Value("${lsf.jms.concurrency:1}")
    private int concurrency;

    /**
     * Configure JMS listener container factory
     */
    @Bean(name = "jmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        log.info("Configuring JMS listener container for queue: {}", toLsfQueueName);
        
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1-" + concurrency);
        factory.setSessionTransacted(false);
        factory.setPubSubDomain(false); // Queue, not Topic
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        
        // Set error handling
        factory.setErrorHandler(t -> log.error("Error in JMS processing", t));
        
        return factory;
    }

    /**
     * Create a JMS template for default messaging
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        log.info("Creating JMS template for queue: {}", toLsfQueueName);
        
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName(toLsfQueueName);
        template.setReceiveTimeout(2000); // 2 seconds timeout
        
        return template;
    }
    
    /**
     * Create a JMS template for sending messages to FROM_LSF_QUEUE
     */
    @Bean(name = "omsQueueProducer") 
    public JmsTemplate omsQueueProducer(ConnectionFactory connectionFactory) {
        log.info("Creating JMS template for FROM_LSF_QUEUE: {}", fromLsfQueueName);
        
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setDefaultDestinationName(fromLsfQueueName);
        template.setExplicitQosEnabled(true);
        template.setDeliveryPersistent(false);
        
        return template;
    }
} 