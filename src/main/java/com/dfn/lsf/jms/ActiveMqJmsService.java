package com.dfn.lsf.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MapMessage;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * JMS Service for sending messages to ActiveMQ queues
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class ActiveMqJmsService {

    private final ConnectionFactory connectionFactory;
    private final JmsTemplate jmsTemplate;
    private final JmsTemplate omsQueueProducer;
    
    @Autowired
    public ActiveMqJmsService(
            ConnectionFactory connectionFactory, 
            JmsTemplate jmsTemplate,
            @Qualifier("omsQueueProducer") JmsTemplate omsQueueProducer) {
        this.connectionFactory = connectionFactory;
        this.jmsTemplate = jmsTemplate;
        this.omsQueueProducer = omsQueueProducer;
    }

    /**
     * Send a message to the specified queue
     * @param eventType The event type 
     * @param sessionID The session ID
     * @param msgData The message data (usually JSON or XML)
     * @param queueName The queue name
     * @return true if successful
     */
    public boolean sendMessage(String eventType, String sessionID, String msgData, String queueName) {
        log.info("Sending message to queue: {}", queueName);
        
        Connection connection = null;
        
        try {
            // Create the connection
            connection = connectionFactory.createConnection();
            
            // Create the session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Create the queue
            Queue queue = session.createQueue(queueName);
            
            // Create the producer
            MessageProducer producer = session.createProducer(queue);
            
            // Create a map message
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("EventType", eventType);
            mapMessage.setString("SessionID", sessionID);
            mapMessage.setString("EventData", msgData);
            
            // Start the connection
            connection.start();
            
            // Send the message
            producer.send(mapMessage);
            
            log.info("Message sent successfully to queue: {}", queueName);
            return true;
        } catch (Exception e) {
            log.error("Error sending message to queue: {}", queueName, e);
            return false;
        } finally {
            closeConnection(connection);
        }
    }
    
    /**
     * Send a text message to the specified queue
     * @param text The text message
     * @param queueName The queue name
     * @return true if successful
     */
    public boolean sendTextMessage(String text, String queueName) {
        try {
            log.info("Sending text message to queue: {}", queueName);
            
            jmsTemplate.send(queueName, session -> {
                TextMessage message = session.createTextMessage(text);
                return message;
            });
            
            log.info("Text message sent successfully to queue: {}", queueName);
            return true;
        } catch (Exception e) {
            log.error("Error sending text message to queue: {}", queueName, e);
            return false;
        }
    }
    
    /**
     * Send a message to FROM_LSF_QUEUE
     * @param text The text message
     * @return true if successful
     */
    public boolean sendToOmsQueue(String text) {
        try {
            log.info("Sending message to FROM_LSF_QUEUE");
            
            omsQueueProducer.send(session -> {
                TextMessage message = session.createTextMessage(text);
                return message;
            });
            
            log.info("Message sent successfully to FROM_LSF_QUEUE");
            return true;
        } catch (Exception e) {
            log.error("Error sending message to FROM_LSF_QUEUE", e);
            return false;
        }
    }
    
    /**
     * Close a JMS connection
     * @param connection The connection to close
     */
    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException jmse) {
                log.warn("Could not close connection: {} exception was {}", connection, jmse.getMessage());
            }
        }
    }
} 