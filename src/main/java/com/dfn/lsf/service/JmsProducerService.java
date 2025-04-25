package com.dfn.lsf.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending messages to the FROM_LSF_QUEUE
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class JmsProducerService {

    private final JmsTemplate omsQueueProducer;
    private final Gson gson;

    public JmsProducerService(@Qualifier("omsQueueProducer") JmsTemplate omsQueueProducer, Gson gson) {
        this.omsQueueProducer = omsQueueProducer;
        this.gson = gson;
    }

    /**
     * Send a message to the FROM_LSF_QUEUE
     * @param message Object to be converted to JSON and sent
     */
    public void sendMessage(Object message) {
        try {
            // Convert the message to JSON string
            String jsonMessage = gson.toJson(message);
            
            log.debug("Sending message to FROM_LSF_QUEUE: {}", jsonMessage);
            
            // Send the message as a text message
            omsQueueProducer.send(session -> session.createTextMessage(jsonMessage));
            
            log.info("Message sent to FROM_LSF_QUEUE successfully");
        } catch (Exception e) {
            log.error("Error sending message to FROM_LSF_QUEUE: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message to FROM_LSF_QUEUE", e);
        }
    }
    
    /**
     * Send a raw string message to the FROM_LSF_QUEUE
     * @param jsonMessage JSON string to be sent
     */
    public void sendRawMessage(String jsonMessage) {
        try {
            log.debug("Sending raw message to FROM_LSF_QUEUE: {}", jsonMessage);
            
            // Send the message as a text message
            omsQueueProducer.send(session -> session.createTextMessage(jsonMessage));
            
            log.info("Raw message sent to FROM_LSF_QUEUE successfully");
        } catch (Exception e) {
            log.error("Error sending raw message to FROM_LSF_QUEUE: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send raw message to FROM_LSF_QUEUE", e);
        }
    }
}