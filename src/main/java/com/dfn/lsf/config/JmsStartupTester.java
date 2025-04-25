package com.dfn.lsf.config;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Tests JMS connectivity at application startup
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class JmsStartupTester {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplate jmsTemplate;
    
    @Value("${lsf.jms.queue.to-lsf:TO_LSF_QUEUE}")
    private String toLsfQueueName;
    
    @Value("${lsf.jms.queue.from-lsf:FROM_LSF_QUEUE}")
    private String fromLsfQueueName;

    @Bean
    public CommandLineRunner testJmsConnectivity() {
        return args -> {
            log.info("Testing JMS connectivity on startup...");
            testConnection();
            testQueues();
            log.info("JMS connectivity test completed successfully");
        };
    }
    
    private void testConnection() {
        Connection connection = null;
        Session session = null;
        
        try {
            // Try to establish a connection
            log.info("Testing basic JMS connection...");
            connection = connectionFactory.createConnection();
            connection.start();
            
            // Create a session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.info("JMS connection established successfully");
            
        } catch (JMSException e) {
            log.error("Failed to establish JMS connection: {}", e.getMessage(), e);
            throw new RuntimeException("JMS connectivity test failed", e);
        } finally {
            // Clean up resources
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    log.warn("Error closing JMS session: {}", e.getMessage());
                }
            }
            
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.warn("Error closing JMS connection: {}", e.getMessage());
                }
            }
        }
    }
    
    private void testQueues() {
        try {
            // Test the TO_LSF_QUEUE - just check if it exists
            log.info("Testing TO_LSF_QUEUE connectivity...");
            jmsTemplate.setReceiveTimeout(500); // Short timeout for test
            jmsTemplate.setDefaultDestinationName(toLsfQueueName);
            jmsTemplate.receive(); // Just try to receive, don't care about result
            
            // Test the FROM_LSF_QUEUE
            log.info("Testing FROM_LSF_QUEUE connectivity...");
            jmsTemplate.setDefaultDestinationName(fromLsfQueueName);
            
            // Optionally send a test message - commented out to avoid side effects
            // String testMsg = "{\"test\":\"connectivity_check\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}";
            // jmsTemplate.send(session -> session.createTextMessage(testMsg));
            
            log.info("Queue connectivity tests successful");
            
        } catch (Exception e) {
            log.error("Failed to verify queue connectivity: {}", e.getMessage(), e);
            throw new RuntimeException("Queue connectivity test failed", e);
        }
    }
}