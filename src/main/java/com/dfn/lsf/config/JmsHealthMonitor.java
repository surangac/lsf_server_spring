package com.dfn.lsf.config;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Health check for JMS connectivity
 * Verifies that we can establish a connection to the ActiveMQ broker
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class JmsHealthMonitor implements HealthIndicator {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public JmsHealthMonitor(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Health health() {
        Connection connection = null;
        try {
            log.debug("Testing ActiveMQ connection health...");
            connection = connectionFactory.createConnection();
            connection.start();
            connection.stop();
            log.info("ActiveMQ connection test successful");
            return Health.up()
                    .withDetail("status", "Connected")
                    .withDetail("type", "ActiveMQ JMS")
                    .build();
        } catch (JMSException e) {
            log.error("ActiveMQ connection test failed: {}", e.getMessage(), e);
            return Health.down()
                    .withDetail("status", "Connection failed")
                    .withDetail("type", "ActiveMQ JMS")
                    .withDetail("error", e.getMessage())
                    .build();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.warn("Error closing JMS connection: {}", e.getMessage());
                }
            }
        }
    }
}