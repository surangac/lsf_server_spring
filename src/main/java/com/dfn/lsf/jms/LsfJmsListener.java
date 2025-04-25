package com.dfn.lsf.jms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.service.impl.ExchangeAccountProcessor;
import com.dfn.lsf.service.impl.DepositResponseHandlingProcessor;
import com.dfn.lsf.service.impl.LsfOmsValidatorAbicProcessor;
import com.dfn.lsf.service.impl.SettlementCalculationProcessor;
import com.dfn.lsf.service.impl.UpdateOrderStatusProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * JMS Listener for receiving and processing messages from TO_LSF_QUEUE
 * Replaces the AKKA-based implementation in the original system
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "lsf.jms.enabled", havingValue = "true")
public class LsfJmsListener {
    
    private final Gson gson;
    private final ExchangeAccountProcessor exchangeAccountProcessor;
    private final LsfOmsValidatorAbicProcessor lsfOmsValidatorProcessor;
    private final UpdateOrderStatusProcessor updateOrderStatusProcessor;
    private final SettlementCalculationProcessor settlementCalculationProcessor;
    private final DepositResponseHandlingProcessor depositWithdrawProcessor;
    
    public LsfJmsListener(
            Gson gson,
            ExchangeAccountProcessor exchangeAccountProcessor,
            LsfOmsValidatorAbicProcessor lsfOmsValidatorProcessor,
            UpdateOrderStatusProcessor updateOrderStatusProcessor,
            SettlementCalculationProcessor settlementCalculationProcessor,
            DepositResponseHandlingProcessor depositWithdrawProcessor) {
        this.gson = gson;
        this.exchangeAccountProcessor = exchangeAccountProcessor;
        this.lsfOmsValidatorProcessor = lsfOmsValidatorProcessor;
        this.updateOrderStatusProcessor = updateOrderStatusProcessor;
        this.settlementCalculationProcessor = settlementCalculationProcessor;
        this.depositWithdrawProcessor = depositWithdrawProcessor;
    }
    
    /**
     * Handles messages from the ActiveMQ TO_LSF_QUEUE
     * @param message Text message from JMS queue
     */
    @JmsListener(destination = "${lsf.jms.queue.to-lsf:TO_LSF_QUEUE}", containerFactory = "jmsListenerContainerFactory")
    public void handleJmsMessage(String message) {
        log.info("Received message from JMS queue: {}", message);
        
        try {
            // Parse the message
            OMSQueueRequest omsRequest = gson.fromJson(message, OMSQueueRequest.class);
            
            // Route based on message type
            switch (omsRequest.getMessageType()) {
                case LsfConstants.UPDATE_ORDER_STATUS_PROCESS:
                    // Core processor handles order status updates, liquidations, deposits, withdrawals
                    processOrderStatusUpdate(message, "updateOrderStatus");
                    break;
                    
                case LsfConstants.APPROVE_ORDER_FOR_FTV:
                case LsfConstants.APPROVE_WITHDRAW_FOR_FTV:
                case LsfConstants.RIA_LOGOUT_RESPONSE: 
                case LsfConstants.TRADE_HOLDING_UPDATE_RESPONSE:
                    // OMS validator handles validation requests
                    processWithValidator(message, "validateRequest");
                    break;
                    
                case LsfConstants.EXCHANGE_ACCOUNT_DELETION_RESPONSE:
                case LsfConstants.INVESTOR_ACCOUNT_CREATION_RESPONSE:
                    // Account creation responses handled by core processor
                    accountUpdateProcessor(message, "accountCreationResponse");
                    break;
                    
                case LsfConstants.LIQUIDATION_SUCCESS_RESPONSE:
                    processWithSettlementCalculation(message, "liquidationResponse");
                    break;
                    
                case LsfConstants.DEPOSIT_SUCCESS_RESPONSE:
                case LsfConstants.WITHDRAW_SUCCESS_RESPONSE:
                    processWithDepositWithdraw(message, "depositWithdrawResponse");
                    break;
                    
                default:
                    log.warn("Unhandled message type: {}", omsRequest.getMessageType());
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Process message using the ExchangeAccountProcessor
     */
    private void accountUpdateProcessor(String message, String subMessageType) {
        log.info("Processing with account processor: {}", subMessageType);
        exchangeAccountProcessor.process(message);
    }
    
    /**
     * Process message using the LsfOmsValidatorAbicProcessor
     */
    private void processWithValidator(String message, String subMessageType) {
        log.info("Processing with validator: {}", subMessageType);
        lsfOmsValidatorProcessor.process(message);
    }

    /**
     * Process message using the UpdateOrderStatusProcessor
     */
    private void processOrderStatusUpdate(String message, String subMessageType) {
        log.info("Processing order status update: {}", subMessageType);
        updateOrderStatusProcessor.process(message);
    }

    /**
     * Process message using the SettlementCalculationProcessor
     */
    private void processWithSettlementCalculation(String message, String subMessageType) {
        log.info("Processing with settlement calculation: {}", subMessageType);
        settlementCalculationProcessor.process(message);
    }

    /**
     * Process message using the DepositResponseHandlingProcessor
     */
    private void processWithDepositWithdraw(String message, String subMessageType) {
        log.info("Processing with deposit/withdraw: {}", subMessageType);
        depositWithdrawProcessor.process(message);
    }
}