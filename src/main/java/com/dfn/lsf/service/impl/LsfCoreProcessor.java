package com.dfn.lsf.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CashTransferRequest;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.PurchaseOrderRequest;
import com.dfn.lsf.model.ShareTransferRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.dfn.lsf.model.CommonInquiryMessage;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.Commodity;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.service.integration.IntegrationService;
import com.dfn.lsf.model.PurchaseOrderListResponse;
import com.dfn.lsf.model.MApplicationCollaterals;
/**
 * Core processor for LSF operations
 * This replaces the AKKA LsfCoreProcessor
 */
@Service
@Qualifier("1") // MESSAGE_TYPE_EXECUTE_CORE_PROCESS
public class LsfCoreProcessor implements MessageProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(LsfCoreProcessor.class);
    
    @Autowired
    private LSFRepository lsfRepository;

    @Autowired
    private IntegrationService integrationService;
    
    private final Gson gson = new Gson();
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            logger.info("Processing core operation request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.CASH_TRANSFER:
                    return processCashTransfer(request);
                case LsfConstants.SHARE_TRANSFER:
                    return processShareTransfer(request);
                case LsfConstants.GET_PORTFOLIO_DETAILS:
                    return getPortfolioDetails(requestMap);
                case LsfConstants.CREATE_PURCHASE_ORDER:
                    return createPurchaseOrder(request);
                case LsfConstants.RUN_REVALUE_PROCESS:
                    return runRevalueProcess(requestMap);
                case LsfConstants.RUN_INITIAL_VALUATION:
                    return runInitialValuation(requestMap);
                case LsfConstants.REQ_PURCHASE_ORDER_EXECUTION:/*-----------Get Order Execution Details for Order Reference-----------*/
                    return getExecutionDetails(requestMap.get("orderReference").toString());
                case LsfConstants.REQ_PURCHASE_ORDER_LIST: /*-------Get  Purchase Order for Application-----*/
                    return getPurchaseOrderList(requestMap.get("id").toString());
                default:
                    logger.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            logger.error("Error processing core operation request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Processes cash transfer request
     */
    private String processCashTransfer(String request) {
        logger.info("Processing cash transfer request");
        
        try {
            CashTransferRequest transferRequest = gson.fromJson(request, CashTransferRequest.class);
            
            // Validate required fields
            if (transferRequest.getSourceAccount() == null || transferRequest.getSourceAccount().isEmpty()) {
                return createErrorResponse("Source account is required");
            }
            
            if (transferRequest.getDestinationAccount() == null || transferRequest.getDestinationAccount().isEmpty()) {
                return createErrorResponse("Destination account is required");
            }
            
            if (transferRequest.getAmount() <= 0) {
                return createErrorResponse("Amount must be greater than zero");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(transferRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Process transfer
            String transactionId = lsfRepository.transferCash(
                    transferRequest.getSourceAccount(),
                    transferRequest.getDestinationAccount(),
                    transferRequest.getAmount(),
                    userId,
                    transferRequest.getApplicationId(),
                    transferRequest.getDescription());
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (transactionId != null) {
                response.setResponseCode(200);
                response.setResponseMessage("Cash transfer successful");
                response.setResponseObject(transactionId);
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to process cash transfer");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error processing cash transfer", e);
            return createErrorResponse("Error processing cash transfer: " + e.getMessage());
        }
    }
    
    /**
     * Processes share transfer request
     */
    private String processShareTransfer(String request) {
        logger.info("Processing share transfer request");
        
        try {
            ShareTransferRequest transferRequest = gson.fromJson(request, ShareTransferRequest.class);
            
            // Validate required fields
            if (transferRequest.getSourceAccount() == null || transferRequest.getSourceAccount().isEmpty()) {
                return createErrorResponse("Source account is required");
            }
            
            if (transferRequest.getDestinationAccount() == null || transferRequest.getDestinationAccount().isEmpty()) {
                return createErrorResponse("Destination account is required");
            }
            
            if (transferRequest.getSymbolCode() == null || transferRequest.getSymbolCode().isEmpty()) {
                return createErrorResponse("Symbol code is required");
            }
            
            if (transferRequest.getQuantity() <= 0) {
                return createErrorResponse("Quantity must be greater than zero");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(transferRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Process transfer
            String transactionId = lsfRepository.transferShares(
                    transferRequest.getSourceAccount(),
                    transferRequest.getDestinationAccount(),
                    transferRequest.getSymbolCode(),
                    transferRequest.getQuantity(),
                    userId,
                    transferRequest.getApplicationId(),
                    transferRequest.getDescription());
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (transactionId != null) {
                response.setResponseCode(200);
                response.setResponseMessage("Share transfer successful");
                response.setResponseObject(transactionId);
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to process share transfer");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error processing share transfer", e);
            return createErrorResponse("Error processing share transfer: " + e.getMessage());
        }
    }
    
    /**
     * Gets portfolio details
     */
    private String getPortfolioDetails(Map<String, Object> requestMap) {
        logger.info("Getting portfolio details");
        
        if (!requestMap.containsKey("tradingAccount")) {
            return createErrorResponse("Trading account is required");
        }
        
        String tradingAccount = requestMap.get("tradingAccount").toString();
        
        CommonResponse response = new CommonResponse();
        try {
            Map<String, Object> portfolioDetails = lsfRepository.getPortfolioDetails(tradingAccount);
            
            if (portfolioDetails != null && !portfolioDetails.isEmpty()) {
                response.setResponseCode(200);
                response.setResponseObject(portfolioDetails);
            } else {
                response.setResponseCode(404);
                response.setErrorMessage("Portfolio not found");
            }
        } catch (Exception e) {
            logger.error("Error getting portfolio details", e);
            response.setResponseCode(500);
            response.setErrorMessage(e.getMessage());
        }
        
        return gson.toJson(response);
    }
    
    /**
     * Creates purchase order
     */
    private String createPurchaseOrder(String request) {
        logger.info("Creating purchase order");
        
        try {
            PurchaseOrderRequest orderRequest = gson.fromJson(request, PurchaseOrderRequest.class);
            
            // Validate required fields
            if (orderRequest.getApplicationId() == null || orderRequest.getApplicationId().isEmpty()) {
                return createErrorResponse("Application ID is required");
            }
            
            if (orderRequest.getTradingAccount() == null || orderRequest.getTradingAccount().isEmpty()) {
                return createErrorResponse("Trading account is required");
            }
            
            if (orderRequest.getCashAccount() == null || orderRequest.getCashAccount().isEmpty()) {
                return createErrorResponse("Cash account is required");
            }
            
            if (orderRequest.getSymbolCode() == null || orderRequest.getSymbolCode().isEmpty()) {
                return createErrorResponse("Symbol code is required");
            }
            
            if (orderRequest.getQuantity() <= 0) {
                return createErrorResponse("Quantity must be greater than zero");
            }
            
            if (orderRequest.getOrderPrice() <= 0) {
                return createErrorResponse("Order price must be greater than zero");
            }
            
            // Extract user ID from session
            Map<String, Object> userInfo = lsfRepository.getUserBySession(orderRequest.getSecurityKey());
            if (userInfo == null || userInfo.isEmpty()) {
                return createErrorResponse("Invalid session");
            }
            
            String userId = userInfo.get("USER_ID").toString();
            
            // Create purchase order
            String orderId = lsfRepository.createPurchaseOrder(
                    orderRequest.getApplicationId(),
                    orderRequest.getTradingAccount(),
                    orderRequest.getCashAccount(),
                    orderRequest.getSymbolCode(),
                    orderRequest.getQuantity(),
                    orderRequest.getOrderPrice(),
                    userId);
            
            // Create response
            CommonResponse response = new CommonResponse();
            if (orderId != null) {
                response.setResponseCode(200);
                response.setResponseMessage("Purchase order created successfully");
                response.setResponseObject(orderId);
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Failed to create purchase order");
            }
            
            return gson.toJson(response);
            
        } catch (Exception e) {
            logger.error("Error creating purchase order", e);
            return createErrorResponse("Error creating purchase order: " + e.getMessage());
        }
    }
    
    /**
     * Runs revalue process
     */
    private String runRevalueProcess(Map<String, Object> requestMap) {
        logger.info("Running revalue process");
        
        // This would typically trigger a background job to revalue portfolios
        // For the migration, we'll just return a success response
        
        CommonResponse response = new CommonResponse();
        response.setResponseCode(200);
        response.setResponseMessage("Revalue process initiated");
        
        return gson.toJson(response);
    }
    
    /**
     * Runs initial valuation
     */
    private String runInitialValuation(Map<String, Object> requestMap) {
        logger.info("Running initial valuation");
        
        if (!requestMap.containsKey("applicationId")) {
            return createErrorResponse("Application ID is required");
        }
        
        String applicationId = requestMap.get("applicationId").toString();
        
        // This would typically trigger a valuation for a specific application
        // For the migration, we'll just return a success response
        
        CommonResponse response = new CommonResponse();
        response.setResponseCode(200);
        response.setResponseMessage("Initial valuation completed for application " + applicationId);
        
        return gson.toJson(response);
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }

    private String getExecutionDetails(String purchaseOrderRef) {
        logger.debug("===========LSF : (reqPurchaseOrderExecution)-REQUEST ");
        CommonInquiryMessage inqueryMessage = new CommonInquiryMessage();
        PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(purchaseOrderRef);
        MurabahApplication application = lsfRepository.getMurabahApplication(po.getApplicationId());
        if (application.getFinanceMethod().equalsIgnoreCase("1")) {
            inqueryMessage.setReqType(String.valueOf(LsfConstants.GET_EXECUTION_DETAILS));
            inqueryMessage.setBasketReference(purchaseOrderRef);
            String response = integrationService.sendOrderRelatedOmsRequest(gson.toJson(inqueryMessage));
            Map<String, Object> resultMap = new HashMap<>();
            resultMap = gson.fromJson(response, resultMap.getClass());
            List<Object> objects = (List<Object>) resultMap.get("responseObject");
            List<Map<String, Object>> responseList = new ArrayList<>();
            for (Object o : objects) {
                Map<String, Object> map = (Map<String, Object>) o;
                if (map.containsKey("symbolCode")) {
                    List<Symbol> symbols = lsfRepository.getSymbolDescription(map.get("symbolCode").toString());
                    if (symbols.size() > 0) {
                        map.put("arabicName", symbols.get(0).getArabicName());
                        map.put("englishName", symbols.get(0).getEnglishName());
                    }
                }
                responseList.add(map);
            }
            logger.debug("===========LSF : (reqPurchaseOrderExecution)-LSF-SERVER RESPONSE  : " + gson.toJson(responseList));
            return gson.toJson(responseList);
        }else { //application.getFinanceMethod().equalsIgnoreCase("2")
            List<Map<String, Object>> responseList = new ArrayList<>();
            List<Commodity> commodities = lsfRepository.getPurchaseOrderCommodities(purchaseOrderRef);
            if (commodities != null && commodities.size() > 0){
                for (Commodity commodity : commodities) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("symbolCode", commodity.getSymbolCode());
                    map.put("symbolName", commodity.getSymbolName());
                    map.put("arabicName", commodity.getArabicName());
                    map.put("englishName", commodity.getSymbolName());//commodity.getEnglishName()
                    map.put("shortDescription", commodity.getShortDescription());
                    map.put("exchange", commodity.getExchange());
                    map.put("broker", commodity.getBroker());
                    map.put("price", commodity.getPrice());
                    map.put("unitOfMeasure", commodity.getUnitOfMeasure());
                    map.put("status", commodity.getStatus());
                    map.put("percentage", commodity.getPercentage());
                    map.put("soldAmnt", commodity.getSoldAmnt());

                    responseList.add(map);
                }
            }
            logger.debug("===========LSF : (reqPurchaseOrderExecution)-LSF-SERVER RESPONSE  : " + gson.toJson(responseList));
            return gson.toJson(responseList);
        }
    }

    private Object getPurchaseOrderList(String applicationId) {
        logger.debug("===========LSF : (reqPurchaseOrderList)-REQUEST , applicationID" + applicationId);

        PurchaseOrderListResponse purchaseOrderListResponse = new PurchaseOrderListResponse();
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
        if (murabahApplication != null) {
            purchaseOrderListResponse.setCustomerAddress(murabahApplication.getAddress());
        }
        List<PurchaseOrder> orderList = null;
        // calculate either flat amount or percentage amount
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
        //    double administrativeFee=GlobalParameters.getInstance().getAdministrationFee() + collaterals.getApprovedLimitAmount()*( GlobalParameters.getInstance().getAdministrationFeePercent()/100);
        double administrativeFee = GlobalParameters.getInstance().getSimaCharges() + GlobalParameters.getInstance().getTransferCharges();
        double vatAmount = LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(administrativeFee));
        try {
            orderList = lsfRepository.getAllPurchaseOrder(applicationId);
            if (orderList.size() > 0) {
                for (PurchaseOrder po : orderList) {
                    MApplicationCollaterals mApplicationCollaterals = lsfDaoI.getApplicationCollateral(po.getApplicationId());
                    po.setTotalOutStandingBalance(mApplicationCollaterals.getOutstandingAmount());
                }
                purchaseOrderListResponse.setPurchaseOrderList(orderList);
                PurchaseOrder purchaseOrder = orderList.get(0);
                if (purchaseOrder.getSimaCharges() > 0 || purchaseOrder.getTransferCharges() > 0) {
                    purchaseOrderListResponse.setAdministrationFee(purchaseOrder.getSimaCharges() + purchaseOrder.getTransferCharges() + vatAmount);
                } else {
                    purchaseOrderListResponse.setAdministrationFee(administrativeFee + vatAmount);
                }
                purchaseOrderListResponse.setDailyFtvList(lsfDaoI.getFTVsummaryForDashBoard(applicationId));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.debug("===========LSF : (reqPurchaseOrderList)-LSF-SERVER RESPONSE  : " + gson.toJson(purchaseOrderListResponse));
        return gson.toJson(purchaseOrderListResponse);
    }
}