package com.dfn.lsf.jms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.service.impl.ExchangeAccountProcessor;
import com.dfn.lsf.service.impl.DepositResponseHandlingProcessor;
import com.dfn.lsf.service.impl.LsfOmsValidatorAbicProcessor;
import com.dfn.lsf.service.scheduler.SettlementCalculationProcessor;
import com.dfn.lsf.service.impl.UpdateOrderStatusProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import com.dfn.lsf.model.responseMsg.OMSQueueResponse;

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
    public OMSQueueResponse handleJmsMessage(String message) {
        log.info("Received message from JMS queue: {}", message);
        
        try {
            // Parse the message
            OMSQueueRequest omsRequest = gson.fromJson(message, OMSQueueRequest.class);
            
            // Route based on message type
            switch (omsRequest.getMessageType()) {
                case LsfConstants.UPDATE_ORDER_STATUS_PROCESS:
                    // Core processor handles order status updates, liquidations, deposits, withdrawals
                    return processOrderStatusUpdate(omsRequest, "updateOrderStatus");
                    
                case LsfConstants.APPROVE_ORDER_FOR_FTV:
                case LsfConstants.APPROVE_WITHDRAW_FOR_FTV:
                case LsfConstants.RIA_LOGOUT_RESPONSE: 
                case LsfConstants.TRADE_HOLDING_UPDATE_RESPONSE:
                    // OMS validator handles validation requests
                    return processWithValidator(omsRequest, "validateRequest");
                    
                case LsfConstants.EXCHANGE_ACCOUNT_DELETION_RESPONSE:
                case LsfConstants.INVESTOR_ACCOUNT_CREATION_RESPONSE:
                case LsfConstants.EXCHANGE_ACCOUNT_CREATION_RESPONSE:
                    // Account creation responses handled by core processor
                    return accountUpdateProcessor(omsRequest, "accountCreationResponse");
                    
                case LsfConstants.LIQUIDATION_SUCCESS_RESPONSE:
                    return processWithSettlementCalculation(message, "liquidationResponse");
                
                case LsfConstants.DEPOSIT_SUCCESS_RESPONSE:
                case LsfConstants.WITHDRAW_SUCCESS_RESPONSE:
                    return processWithDepositWithdraw(omsRequest, "depositWithdrawResponse");
            }
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Process message using the ExchangeAccountProcessor
     */
    private OMSQueueResponse accountUpdateProcessor(OMSQueueRequest message, String subMessageType) {
        log.info("Processing with account processor: {}", subMessageType);
        exchangeAccountProcessor.process(message);
        OMSQueueResponse response = new OMSQueueResponse();
        response.setParams("Done");
        return response;
    }
    
    /**
     * Process message using the LsfOmsValidatorAbicProcessor
     */
    private OMSQueueResponse processWithValidator(OMSQueueRequest message, String subMessageType) {
        log.info("Processing with validator: {}", subMessageType);
        return lsfOmsValidatorProcessor.process(message);
    }

    /**
     * Process message using the UpdateOrderStatusProcessor
     */
    private OMSQueueResponse processOrderStatusUpdate(OMSQueueRequest message, String subMessageType) {
        log.info("Processing order status update: {}", subMessageType);
        String responseString = updateOrderStatusProcessor.process(message);
        OMSQueueResponse response = new OMSQueueResponse();
        response.setParams(responseString);
        return response;
    }

    /**
     * Process message using the SettlementCalculationProcessor
     */
    private OMSQueueResponse processWithSettlementCalculation(String message, String subMessageType) {
        log.info("Processing with settlement calculation: {}", subMessageType);
        settlementCalculationProcessor.process(message);
        OMSQueueResponse response = new OMSQueueResponse();
        response.setParams("Done");
        return response;
    }

    /**
     * Process message using the DepositResponseHandlingProcessor
     */
    private OMSQueueResponse processWithDepositWithdraw(OMSQueueRequest message, String subMessageType) {
        log.info("Processing with deposit/withdraw: {}", subMessageType);
        depositWithdrawProcessor.process(message);
        OMSQueueResponse response = new OMSQueueResponse();
        response.setParams("Done");
        return response;
    }
}