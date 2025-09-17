package com.dfn.lsf.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dfn.lsf.model.*;
import com.dfn.lsf.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.requestMsg.AccountCreationRequest;
import com.dfn.lsf.model.requestMsg.AdminFeeRequest;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.requestMsg.OrderBasket;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.model.responseMsg.DashBoardData;
import com.dfn.lsf.model.responseMsg.ProfitResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_EXECUTE_CORE_PROCESS;

@Service
@MessageType(MESSAGE_TYPE_EXECUTE_CORE_PROCESS)
@Slf4j
@RequiredArgsConstructor
public class LsfCoreProcessor implements MessageProcessor {

    private final LSFRepository lsfRepository;
    
    private final Gson gson;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;
    private final SettlementProcessor settlementProcessor;
    private final AuditLogProcessor auditLogProcessor;

    @Value("${app.bypass.umessage:false}")
    private boolean bypassUmessage;
    
    @Override
    public String process(String request) {
        try {
            auditLogProcessor.process(request);
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing core operation request with subMessageType: {}", subMessageType);
            
            switch (subMessageType) {
                case LsfConstants.RUN_REVALUE_PROCESS:
                    if (requestMap.containsKey("id")) {
                        return lsfCore.reValuationProcess(requestMap.get("id").toString());
                    } else {
                        return lsfCore.reValuationProcess();
                    }
                case LsfConstants.RUN_INITIAL_VALUATION:
                    return lsfCore.initialValuation(requestMap.get("id").toString());
                case LsfConstants.CREATE_PURCHASE_ORDER: /*-------Create Purchase Order-----*/
                    return createPurchaseOrder(requestMap, request);
                case LsfConstants.REQ_PURCHASE_ORDER_LIST: /*-------Get  Purchase Order for Application-----*/
                    return getPurchaseOrderList(requestMap.get("id").toString());
                case LsfConstants.REQ_PURCHASE_ORDER_EXECUTION:/*-----------Get Order Execution Details for Order Reference-----------*/
                    return getExecutionDetails(requestMap.get("orderReference").toString());
                case LsfConstants.REQ_DASH_BOARD_DATA: /*---------Get Application Related Dash Board Data---------*/
                    return getDashBoardData(requestMap.get("id").toString());
                case LsfConstants.APPROVE_PURCHASE_ORDER:/*-----------Approve Purchase Order-----------*/
                    return approvePurchaseOrder(requestMap);
                case LsfConstants.CALCULATE_PROFIT:/*-----------Calculate Profit-------------*/
                    return calculateProfit(requestMap);
                case LsfConstants.REQ_APPROVE_ORDER_AGREEMENT:/*----------------Approve Purchase Order Agreement------------Client*/
                    return processOrderAgreement(requestMap);
                case LsfConstants.REQ_DASH_BOARD_PF_SUMMARY:/*-----------------Get Customer PF Summary ------*/
                    return getCustomerPFSummary(requestMap.get("id").toString());
                case LsfConstants.UPDATE_PO_BY_ADMIN:
                    return updatePurchaseOrdByAdmin(requestMap);
                case LsfConstants.APPROVE_COMMODITY_PO_BY_ADMIN_L1:
                    return approveL1CommodityPOBoughtAmnt(requestMap);
                case LsfConstants.APPROVE_COMMODITY_PO_BY_ADMIN_L2:
                    return finalApproveCommodityPoBoughtAmt(requestMap);
                case LsfConstants.CONFIRM_AUTH_ABIC_TO_SELL_BY_USER:
                    return updateAuthAbicToSell(requestMap);
                case LsfConstants.REVERT_TO_SELL_DELIVER_BY_ADMIN:
                    return revertPOToSellorDeliver(requestMap);
                case LsfConstants.COMMODITY_PO__UPDATE_SOLD_AMOUNT:
                    return updateSoldAmount(requestMap);
                case LsfConstants.APPROVE_PO_SOLD_AMOUNT_L1:
                    return approveCommunityPOSoldAmnt(requestMap);
                case LsfConstants.COMMODITY_PO_EXECUTE:
                    return commodityPOExecution(requestMap);
            }
        } catch (Exception e) {
            log.error("Error processing core operation request", e);
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            return gson.toJson(cmr);
        }
        return null; // Single return at the end of method
    }
    
    private String getDashBoardData(String applicationId) {
        log.debug("===========LSF : (reqDashBoardData)-REQUEST , applicationID:" + applicationId);
        DashBoardData boardData = new DashBoardData();
        try {
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
            if (collaterals != null) {
                boardData.setApplicationId(collaterals.getApplicationId());
                boardData.setCashCollateral(collaterals.getTotalCashColleteral());
                boardData.setPfCollateral(collaterals.getTotalPFColleteral());
                boardData.setTotalCollateral(collaterals.getNetTotalColleteral());
                boardData.setOutstandingBalance(collaterals.getOutstandingAmount());

                boardData.setRemainingApprovedLimit(collaterals.getApprovedLimitAmount() - collaterals.getOpperativeLimitAmount());
                boardData.setRemainingOperativeLimit(collaterals.getRemainingOperativeLimitAmount());
                boardData.setUtilizedLimit(collaterals.getUtilizedLimitAmount());
            }
            boardData.setDailyFtvList(lsfRepository.getFTVsummaryForDashBoard(applicationId));
            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
            boardData.setProductType(murabahApplication.getProductType());
            if (murabahApplication.getProductType() == 3) { // when Discount on Profit is available
                List<PurchaseOrder> purchaseOrderList = lsfRepository.getPurchaseOrderForApplication(applicationId);
                if (purchaseOrderList != null && purchaseOrderList.size() > 0 ) {
                    PurchaseOrder purchaseOrder = purchaseOrderList.get(0);
                    OrderProfit orderProfit = lsfCore.calculateConditionalProfit(collaterals, purchaseOrder, murabahApplication.getDiscountOnProfit());
                    boardData.setOrderProfit(orderProfit);
                }
            }

        } catch (Exception ex) {
            log.error("Error getting dash board data", ex);
        }
        log.debug("===========LSF : (reqDashBoardData)-LSF-SERVER RESPONSE  : " + gson.toJson(boardData));
        return gson.toJson(boardData);
    }

    private String getExecutionDetails(String purchaseOrderRef) {
        log.debug("===========LSF : (reqPurchaseOrderExecution)-REQUEST ");
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(purchaseOrderRef);
        MurabahApplication application = lsfRepository.getMurabahApplication(po.getApplicationId());
        if (application.getFinanceMethod().equalsIgnoreCase("1")) {
            inqueryMessage.setReqType(LsfConstants.GET_EXECUTION_DETAILS);
            inqueryMessage.setBasketReference(purchaseOrderRef);
            String response = helper.orderRelatedOMS(gson.toJson(inqueryMessage));
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
            log.debug("===========LSF : (reqPurchaseOrderExecution)-LSF-SERVER RESPONSE  : " + gson.toJson(responseList));
            return gson.toJson(responseList);
        }else {
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
                    map.put("boughtAmnt", commodity.getBoughtAmnt());

                    responseList.add(map);
                }
            }
            log.debug("===========LSF : (reqPurchaseOrderExecution)-LSF-SERVER RESPONSE  : " + gson.toJson(responseList));
            return gson.toJson(responseList);
        }

    }

    private String getPurchaseOrderList(String applicationId) {
        log.debug("===========LSF : (reqPurchaseOrderList)-REQUEST , applicationID" + applicationId);

        PurchaseOrderListResponse purchaseOrderListResponse = new PurchaseOrderListResponse();
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
        if (murabahApplication != null) {
            purchaseOrderListResponse.setCustomerAddress(murabahApplication.getAddress());
        }
        try {
            var orderList = lsfRepository.getAllPurchaseOrder(applicationId);
            if (!orderList.isEmpty()) {
                for (PurchaseOrder po : orderList) {
                    MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCollateral(po.getApplicationId());
                    po.setTotalOutStandingBalance(mApplicationCollaterals.getOutstandingAmount());
                }
                purchaseOrderListResponse.setPurchaseOrderList(orderList);

                double administrationFee = GlobalParameters.getInstance().getComodityAdminFee();// + GlobalParameters.getInstance().getComodityFixedFee();

                if (murabahApplication.getFinanceMethod().equals("1")) {
                    administrationFee = GlobalParameters.getInstance().getShareAdminFee();// + GlobalParameters.getInstance().getShareFixedFee();
                }
                double vatAmount = lsfCore.calculateVatAmt(administrationFee);
                purchaseOrderListResponse.setAdministrationFee(administrationFee + vatAmount);

                purchaseOrderListResponse.setDailyFtvList(lsfRepository.getFTVsummaryForDashBoard(applicationId));
            }
        } catch (Exception ex) {
            log.error("Error getting purchase order list", ex);
        }
        log.debug("===========LSF : (reqPurchaseOrderList)-LSF-SERVER RESPONSE  : " + gson.toJson(purchaseOrderListResponse));
        return gson.toJson(purchaseOrderListResponse);
    }

    protected String createPurchaseOrder(Map<String, Object> map, String msgString) {

        CommonResponse commonResponse = new CommonResponse();
        PurchaseOrder po = parsePo(map, msgString);
        if (map.containsKey("customerName")) {
            po.setCustomerName(map.get("customerName").toString());
        }
        int financeMethod = 1;
        if (map.containsKey("financeMethod")) {
            financeMethod = Integer.parseInt(map.get("financeMethod").toString());
        }

        if (LSFUtils.isPurchaseOrderCreationAllowed(bypassUmessage)) { /*----PO Submission should be allowed until 1h 15min before market close time---*/
            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(po.getApplicationId());
            if ( murabahApplication != null && murabahApplication.isRollOverApp() ) {
                return createPurchaseOrderRollOver(po, murabahApplication);
            }
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(po.getApplicationId());
            if (map.containsKey("ipAddress")) {
                collaterals.setIpAddress(map.get("ipAddress").toString());
            }
            if (Integer.parseInt(murabahApplication.getOverallStatus()) < 0 || murabahApplication.getCurrentLevel() == GlobalParameters.getInstance().getGetAppCloseLevel()) {
                commonResponse.setResponseCode(500);
                commonResponse.setErrorMessage("Invalid Details");
                commonResponse.setErrorCode(LsfConstants.ERROR_INVALID_DETAILS);
                log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
                return gson.toJson(commonResponse);
            }
            boolean lsfTrdAccCreated = murabahApplication.isRollOverApp() || sendAccountCreationRequestToOMS(
                    murabahApplication);
            boolean isCommodity = murabahApplication.getFinanceMethod().equals("2");

            if (lsfTrdAccCreated || isCommodity) {
                if(murabahApplication.getProfitPercentage()>0){
                    // use the appliaction level profit %
                    po.setProfitPercentage(murabahApplication.getProfitPercentage());
                }else {
                    CommissionStructure applyingCommissionStructure = lsfCore.getCommissionStructureBasedOnOrderValue(po.getOrderValue());
                    if (applyingCommissionStructure != null) {
                        po.setProfitPercentage(applyingCommissionStructure.getPercentageAmount());
                    }
                }
                /*---update Outstanding Amount---*/
                collaterals.setOutstandingAmount(collaterals.getOutstandingAmount() + po.getOrderSettlementAmount());

                /*---update Utilization Amount---*/
                collaterals.setUtilizedLimitAmount(collaterals.getUtilizedLimitAmount() + po.getOrderSettlementAmount());

                /*---update FTV---*/
                lsfCore.calculateFTV(collaterals);

                lsfCore.calculateRemainingOperativeLimit(collaterals);
                if (!isCommodity) {
                    if (!validatePo(po, commonResponse, collaterals, murabahApplication.getStockConcentrationGroup())) { /*---Vlidate PO ---*/
                        return gson.toJson(commonResponse);
                    }
                }

                String respMessage = Integer.toString(murabahApplication.getCurrentLevel()) + "|" + murabahApplication.getOverallStatus();

                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage(respMessage);
                log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));

                /*----Sending Automatic Order Approval---*/

                log.debug("===========LSF : Sending Automatic Order Approval ,poId :" + po.getId() + ", Application ID:" + po.getApplicationId());
                approvePurchaseOrderABIC(po, collaterals, commonResponse, financeMethod);

                log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
                return gson.toJson(commonResponse);
            } else {
                commonResponse.setResponseCode(500);
                commonResponse.setErrorMessage("Account Creation Request Failed.");
                commonResponse.setErrorCode(LsfConstants.ERROR_ACCOUNT_CREATION_FAILED);
                log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
                return gson.toJson(commonResponse);
            }

        } else {
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage("Purchase orders cannot be submitted after 1.15PM. Please retry tomorrow, before said time.");
            commonResponse.setErrorCode(LsfConstants.ERROR_PURCHASE_ORDERS_CANNOT_BE_SUBMITTED_AFTER_PLEASE_TRY_TOMORROW);
            log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
            return gson.toJson(commonResponse);
        }
    }

    protected String createPurchaseOrderRollOver(PurchaseOrder po, MurabahApplication murabahApplication) {
        CommonResponse commonResponse = new CommonResponse();
        String approvedbyId = "SYSTEM";
        String approvedbyName = "SYSTEM";
        String ipAddress = murabahApplication.getIpAddress();
        int approvalStatus = 1;

        if (Integer.parseInt(murabahApplication.getOverallStatus()) < 0 || murabahApplication.getCurrentLevel() == GlobalParameters.getInstance().getGetAppCloseLevel()) {
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage("Invalid Details");
            commonResponse.setErrorCode(LsfConstants.ERROR_INVALID_DETAILS);
            log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
            return gson.toJson(commonResponse);
        }

        if(murabahApplication.getProfitPercentage()>0){
            po.setProfitPercentage(murabahApplication.getProfitPercentage());
        }else {
            CommissionStructure applyingCommissionStructure = lsfCore.getCommissionStructureBasedOnOrderValue(po.getOrderValue());
            if (applyingCommissionStructure != null) {
                po.setProfitPercentage(applyingCommissionStructure.getPercentageAmount());
            }
        }
        try {
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCollateral(po.getApplicationId());

            collaterals.setOutstandingAmount(collaterals.getOutstandingAmount() + po.getOrderSettlementAmount());
            collaterals.setUtilizedLimitAmount(collaterals.getUtilizedLimitAmount() + po.getOrderSettlementAmount());
            lsfCore.calculateFTV(collaterals);
            lsfCore.calculateRemainingOperativeLimit(collaterals);
            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_PO_CREATED_WAITING_TO_ORDER_FILL);
            lsfRepository.addPurchaseOrder(po, ipAddress);
            lsfRepository.addEditCollaterals(collaterals, approvedbyName, Integer.parseInt(murabahApplication.getCustomerId()));
            po.setApprovalStatus(approvalStatus);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);
            lsfRepository.approveRejectOrder(po, ipAddress);

            notificationManager.sendNotification(murabahApplication);
            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_SENT_INVESTOR_ACCOUNT_CREATION);

            String respMessage = murabahApplication.getCurrentLevel() + "|" + murabahApplication.getOverallStatus();
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage(respMessage);

        } catch (Exception ex) {
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage(ex.getMessage());
        }
        return gson.toJson(commonResponse);
    }

    public String approvePurchaseOrderABIC(PurchaseOrder purchaseOrder, MApplicationCollaterals completeCollateral, CommonResponse cmr, int financeMethod) {
        String applicationId = "";
        String ipAddress = "";
        boolean isLsfTypeCashAccExist = false;
        boolean isLsfTypeTradingAccExist = false;
        try {
            String poId = purchaseOrder.getId();
            applicationId = purchaseOrder.getApplicationId();
            ipAddress = completeCollateral.getIpAddress();

            int approvedbyId = 0;  //"SYSTEM";
            String approvedbyName = "SYSTEM";
            int approvalStatus = 1;
            log.info("===========LSF : (approvePurchaseOrder)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName + " ,approvalStatus:" + approvalStatus);
            OrderBasket orderBasket = createPOInstruction(purchaseOrder);

            cmr.setResponseCode(200);
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationId);
            cmr.setResponseMessage(application.getCurrentLevel() + "|" + application.getOverallStatus());
            notificationManager.sendNotification(application);/*---Send Notification---*/
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATED_WAITING_TO_ORDER_FILL);

            //MApplicationCollaterals completeCollateral = lsfRepository.getApplicationCompleteCollateral(applicationId);
            String originalAppId = application.isRollOverApp() ? application.getRollOverAppId() : application.getId();
            var lsfTradingAccList =  helper.getLsfTypeTradingAccounts(application.getCustomerId(), originalAppId, application.getMarginabilityGroup());
            if (lsfTradingAccList != null && !lsfTradingAccList.isEmpty()) {
                TradingAccOmsResp omsTrdAcc = lsfTradingAccList.getFirst();
                TradingAcc tradingAcc =  completeCollateral.isTradingAccountLSFTypeExist(omsTrdAcc.getAccountId());
                tradingAcc.setApplicationId(applicationId);
                tradingAcc.setLsfType(true);
                tradingAcc.setExchange(omsTrdAcc.getExchange());
                isLsfTypeTradingAccExist = true;
            }

            //           CommonInqueryMessage request = new CommonInqueryMessage();
//            Map<String, Object> resultMap = new HashMap<>();

//            String result = "";
//
//            request.setCustomerId(application.getCustomerId());
//            request.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
//            request.setContractId(applicationId);
//            result = (String) helper.sendMessageToOms(gson.toJson(request));
//            resultMap.clear();
//            resultMap = gson.fromJson((String) result, resultMap.getClass());
//            ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
//            String tradingAccId;
//            for (Map<String, Object> trd : lsfTrd) {
//                Map<String, Object> mpTRadingAcc = (Map<String, Object>) trd.get("tradingAccount");
//                tradingAccId = mpTRadingAcc.get("accountId").toString();
//                t = completeCollateral.isTradingAccountLSFTypeExist(tradingAccId);
//                t.setExchange(mpTRadingAcc.get("exchange").toString());
//                t.setApplicationId(applicationId);
//                t.setLsfType(true);
//                isLsfTypeTradingAccExist = true;
//            }

            var lsfTypeCashAccounts = helper.getLsfTypeCashAccounts(application.getCustomerId(), originalAppId);
            if (lsfTypeCashAccounts != null && !lsfTypeCashAccounts.isEmpty()) {
                CashAcc cashAccOms = lsfTypeCashAccounts.getFirst();
                CashAcc c = completeCollateral.isCashAccLSFTypeExist(cashAccOms.getAccountId());
                //c.mapFromOms(cashAccOms);
                c.setCashBalance(cashAccOms.getCashBalance());
                c.setApplicationId(applicationId);
                c.setLsfType(true);
                isLsfTypeCashAccExist = true;
            }
//
//            result = "";
//            String cashAccid = null;
//            request.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
//            request.setContractId(applicationId);
//            result = (String) helper.sendMessageToOms(gson.toJson(request));
//            resultMap.clear();
//            resultMap = gson.fromJson((String) result, resultMap.getClass());
//            ArrayList<Map<String, Object>> lsfcash = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
//            for (Map<String, Object> cash : lsfcash) {
//                cashAccid = cash.get("accountNo").toString();
//                CashAcc c = completeCollateral.isCashAccLSFTypeExist(cashAccid);
//                c.setCashBalance(Double.parseDouble(cash.get("balance").toString()));
//                c.setApplicationId(applicationId);
//                c.setLsfType(true);
//                isLsfTypeCashAccExist = true;
//            }

            lsfRepository.addEditCompleteCollateral(completeCollateral, approvedbyName, approvedbyId); /*----Update LSF Type Cash & Trading Accounts------*/

            /*----- creating investor account for LSF type cash account----*/
            if (isLsfTypeCashAccExist && isLsfTypeTradingAccExist) {
                var lsfTradingAcc = completeCollateral.getLsfTypeTradingAccounts().getFirst();
                var lsfCashAcc = completeCollateral.getCashAccForColleterals().getFirst();

                boolean isInvestorAccountCreated = application.isRollOverApp()
                                                   || createInvestorAccount(lsfCashAcc.getAccountId(), applicationId);
                boolean isExchangeAccountCreated = application.isRollOverApp()
                                                   || createExchangeAccount(lsfTradingAcc.getAccountId(),
                                                                            lsfTradingAcc.getExchange(),
                                                                            applicationId);
                // send order to OMS if Exchagne account created successfully
                if (isInvestorAccountCreated && isExchangeAccountCreated) {
                    lsfRepository.addPurchaseOrder(purchaseOrder, ipAddress);
                    lsfRepository.addEditCollaterals(completeCollateral, approvedbyName, approvedbyId);
                    purchaseOrder.setApprovalStatus(approvalStatus);
                    purchaseOrder.setApprovedById("SYSTEM");
                    purchaseOrder.setApprovedByName(approvedbyName);
                    lsfRepository.approveRejectOrder(purchaseOrder, ipAddress);
                    if (financeMethod == 1) {
                        setPOInstructionToOMS(orderBasket, applicationId);
                    }
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Order Creation failed, Exchange and Investor account creation failed");
                    lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATION_FAILED);
                }
            }
            
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Order Approve failed");
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATION_FAILED);
        }
        log.debug("===========LSF : (approvePurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    protected boolean sendAccountCreationRequestToOMS(MurabahApplication murabahApplication) {
        CommonInqueryMessage accountCreationRequest = new CommonInqueryMessage();
        /*---Creating  LSF Accounts for User-----*/
        accountCreationRequest.setReqType(LsfConstants.CREATE_ACCOUNT);
        accountCreationRequest.setCustomerId(murabahApplication.getCustomerId());
        accountCreationRequest.setContractId(murabahApplication.getId());

        if (GlobalParameters.getInstance().getBaseCurrency() != null) {
            accountCreationRequest.setCurrency(GlobalParameters.getInstance().getBaseCurrency());
        } else {
            accountCreationRequest.setCurrency("SAR");
        }
        accountCreationRequest.setExchange(murabahApplication.getTradingAccExchange());
        accountCreationRequest.setTradingAccountId(murabahApplication.getTradingAcc());
        log.debug("===========LSF : Creating Accounts in OMS fro Application ID :" + murabahApplication.getId());
        return lsfCore.createAccounts(accountCreationRequest);
    }

    protected boolean createInvestorAccount(String cashAccId, String applicationId) {
        log.debug("===========LSF : Creating Investor Account for Application ID :" + applicationId + " , Cash Account ID:" + cashAccId);
        AccountCreationRequest createInvestorAccount = new AccountCreationRequest();
        createInvestorAccount.setReqType(LsfConstants.CREATE_INVESTOR_ACCOUNT);
        createInvestorAccount.setFromCashAccountNo(cashAccId);
        String omsResponseForInvestorAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createInvestorAccount));
        CommonResponse investorAccountResponse = helper.processOMSCommonResponseAccountCreation(omsResponseForInvestorAccountCreation);
        lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_SENT_INVESTOR_ACCOUNT_CREATION);
        if (investorAccountResponse.getResponseCode() == -2) { 
            log.debug("===========LSF :Investor Account Already Created  for Application ID :" + applicationId + " , Cash Account ID:" + cashAccId);
            return true;
        } else if (investorAccountResponse.getResponseCode() == -1) {
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED_OMS);
            return false;
        }
        return true;
    }

    protected boolean createExchangeAccount(String tradingAccId, String exchange, String applicationId) {
        AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
        createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
        createExchangeAccount.setTradingAccountId(tradingAccId);
        createExchangeAccount.setExchange(exchange);
        String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createExchangeAccount));
        CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(omsResponseForExchangeAccountCreation);
        if (exchangeAccountResponse.getResponseCode() == 1) {
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
            return true;
        } else {
            if (bypassUmessage) {
                return true;
            }
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
            return false;
        }
    }

    protected String approveCommunityPOSoldAmnt(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String applicationId = "";
        try {
            String poId = map.get("id").toString();
            applicationId = map.get("applicationId").toString();
            String approvedbyId = map.get("approvedById").toString();
            String approvedbyName = map.get("approvedByName").toString();
            String statusChangedIP = map.get("ipAddress").toString();
            log.info("===========LSF : (approvePurchaseOrder)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName );
            int approvalStatus = map.get("approvalStatus") != null ? (int) Double.parseDouble(map.get("approvalStatus").toString()) : 0;

            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);

            List<Map<String, Object>> commodityListMap = (List<Map<String, Object>>) map.get("commodityList");
            List<Commodity> commodities = getCommodityList(commodityListMap);

            updatePoCommodityList(po, commodities);

            //po.setApprovalStatus(LsfConstants.PO_APPROVAL_L1_SOLD_AMT);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);

            if (approvalStatus == 1) {
                po.setApprovalStatus(LsfConstants.PO_APPROVAL_L1_SOLD_AMT);
                lsfRepository.updatePurchaseOrderByAdmin(po);
                String response = lsfRepository.approveRejectPOCommodity(po);
                if(response.equalsIgnoreCase("1")) {
                    lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_SOLD_AMT_L1_APPROVED);
                    MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
                    String statusMessage = "Sold Amount Level 1 Approved";
                    lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Commodity Order Approved");
                }
            } else {
                po.setApprovalStatus(LsfConstants.PO_REJECT_L1_BOUGHT_AMT);
                String response = lsfRepository.approveRejectPOCommodity(po);
                if(response.equalsIgnoreCase("1")) {
                    // update to the previous activity status
                    lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Commodity Order Rejected");
                }
            }

        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Order Approve failed");
            log.error("Order Approve failed", ex);
        }
        log.debug("===========LSF : (approvePurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    protected String approvePurchaseOrder(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String applicationId = "";
        try {
            String poId = map.get("id").toString();
            applicationId = map.get("applicationId").toString();
            String approvedbyId = map.get("approvedById").toString();
            String approvedbyName = map.get("approvedByName").toString();
            String ipAddress = map.get("ipAddress").toString();
            int approvalStatus = Integer.parseInt(map.get("approvalStatus").toString());
            log.info("===========LSF : (approvePurchaseOrder)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName + " ,approvalStatus:" + approvalStatus);

            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
            po.setApprovalStatus(approvalStatus);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);

            if (approvalStatus > 0) {
                OrderBasket orderBasket = createPOInstruction(po);
                if (setPOInstructionToOMS(orderBasket, po.getApplicationId())) {
                    lsfRepository.approveRejectOrder(po, ipAddress);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Order Approved and send to OMS");
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Order send to OMS Failed");
                    cmr.setErrorCode(LsfConstants.ERROR_ORDER_SEND_TO_OMS_FAILED);
                }
            } else {
                if (lsfRepository.approveRejectOrder(po, ipAddress).equalsIgnoreCase("1")) {
                    MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
                    CommonResponse commonResponse = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                    if (commonResponse.getResponseCode() == 200) {
                        cmr.setResponseCode(200);
                    } else {
                        cmr.setResponseCode(200);
                        cmr.setErrorMessage("Collatral Release failed from OMS");
                        cmr.setErrorCode(LsfConstants.ERROR_COLLATRAL_RELEASE_FAILED_FROM_OMS);
                    }
                } else {
                    cmr.setResponseCode(200);
                    cmr.setErrorMessage("Order Rejection failed");
                }
            }
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Order Approve failed");
            log.error("Order Approve failed", ex);
        }
        log.debug("===========LSF : (approvePurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    protected boolean setPOInstructionToOMS(OrderBasket basket, String applicationId) {
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        inqueryMessage.setReqType(LsfConstants.SEND_PO_INSTRUCTIONS);
        inqueryMessage.setLsfBasket(basket);
        inqueryMessage.setContractId(applicationId);
        String response = helper.orderRelatedOMS(gson.toJson(inqueryMessage));
        Map<String, Object> resMap = new HashMap<>();
        resMap = gson.fromJson(response.toString(), resMap.getClass());
        if (resMap.containsKey("responseObject")) {
            String s = resMap.get("responseObject").toString();
            log.info("Order Basket Creation Response , ID :" + basket.getBasketReference() + " ,Response:" + response);
            String delimitter = "\\|\\|";
            String[] resultMap = s.split(delimitter);
            log.info("ResultMap >>>>>>" + resultMap.toString());
            return resultMap[0].equals("1");
        } else {
            log.info("Order Basket Creation Response , ID :" + basket.getBasketReference() + " ,Response:" + response);

            return false;
        }
    }

    protected OrderBasket createPOInstruction(PurchaseOrder purchaseOrder) {
        OrderBasket orderBasket = new OrderBasket();

        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, 1);
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        orderBasket.setBasketReference(purchaseOrder.getId());
        orderBasket.setTradingAccountId(purchaseOrder.getTradingAccount());
        orderBasket.setCustomerId(purchaseOrder.getCustomerId());
        orderBasket.setExpiryDate(dateFormat.format(date.getTime()));
        orderBasket.setLoanAmount(purchaseOrder.getOrderValue());
        orderBasket.setSymbolList(purchaseOrder.getSymbolList());

        return orderBasket;
    }

    protected boolean validatePo(PurchaseOrder purchaseOrder, CommonResponse cmr, MApplicationCollaterals collaterals, String concenTrationGrp) {
        if (purchaseOrder.getOrderSettlementAmount() <= 0) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Invalid Purchase Order value");
            return false;
        }
        if (purchaseOrder.getTradingAccount().equals("")) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Invalid Trading Account");
            return false;
        }

        /*--- validation for Stock Concentration---*/
//        if (!lsfCore.validateSymbolAmountAgainstConcentration(concenTrationGrp, purchaseOrder.getSymbolList())) {
//            cmr.setResponseCode(500);
//            cmr.setErrorMessage("Stock Concentration validation failed");
//            return false;
//        }
        return true;
    }

    protected PurchaseOrder parsePo(Map<String, Object> map, String msgString) {
        PurchaseOrder po = gson.fromJson(msgString, PurchaseOrder.class);
        // Settlement date calculation moved to server
        processSettlementDate(po, Integer.parseInt(map.get("settlementDurationInMonths").toString()));
        // set ID
        po.setId(lsfRepository.getNextAvailablePOID());
        // set settlemet date
        Calendar date = Calendar.getInstance();
        //set Created Date
        java.text.DateFormat dateFormatCrDate = new SimpleDateFormat("dd/MM/yyyy");
        po.setCreatedDate(dateFormatCrDate.format(date.getTime()));
        // set Settlement date with Duration
        java.text.DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        date.add(Calendar.MONTH, po.getSettlementDurationInMonths());
        // po.setSettlementDate(Integer.parseInt(dateFormat.format(date.getTime())));
        //  po.setSettlementDate();
        // set Institution Trading account as order Trading account according to the configuration
        if (GlobalParameters.getInstance().getOrderAccPriority() == 1) {
            po.setTradingAccount(GlobalParameters.getInstance().getInstitutionTradingAcc());
        }

        if (!po.isOneTimeSettlement()) {
            // create settlement frequency
            if (po.getSettlementDurationInMonths() >= po.getInstallmentFrequency()) {
                int mod = po.getSettlementDurationInMonths() % po.getInstallmentFrequency();
                int noOfInstallments = (po.getSettlementDurationInMonths() - mod) / po.getInstallmentFrequency();
                int totalInsatallments = noOfInstallments;
                if (mod > 0)
                    totalInsatallments += 1;
                double installmentAmount = po.getOrderSettlementAmount() / totalInsatallments;
                Calendar insDates = Calendar.getInstance();
                List<Installments> installments = new ArrayList<>();
                for (int i = 1; i <= noOfInstallments; i++) {
                    insDates.add(Calendar.MONTH, po.getInstallmentFrequency());
                    Installments installment = new Installments();
                    installment.setInstalmentNumber(i);
                    installment.setInstallmentAmount(installmentAmount);
                    installment.setInstallmentStatus(LsfConstants.SETTLEMENT_PENDING);
                    installment.setInstalmentDate(Integer.parseInt(dateFormat.format(insDates.getTime())));
                    installments.add(installment);
                }
                // add the last Installment in case of duration is not equal with final settlement
                if (mod > 0) {
                    Installments installment = new Installments();
                    installment.setInstalmentNumber(installments.size() + 1);
                    installment.setInstallmentAmount(installmentAmount);
                    installment.setInstallmentStatus(LsfConstants.SETTLEMENT_PENDING);
                    installment.setInstalmentDate(Integer.parseInt(po.getSettlementDate()));
                    installments.add(installment);
                }
                po.setInstallments(installments);
            }
        } else {
            // one time payment also taken as 1 install settlement
            List<Installments> installments = new ArrayList<>();
            Installments installment = new Installments();
            installment.setInstalmentNumber(1);
            installment.setInstallmentAmount(po.getOrderSettlementAmount());
            installment.setInstallmentStatus(LsfConstants.SETTLEMENT_PENDING);
            installment.setInstalmentDate(Integer.parseInt(po.getSettlementDate()));
            installments.add(installment);
            po.setInstallments(installments);
        }
        return po;
    }

    private void processSettlementDate(PurchaseOrder po, int durationInMonths) {
        int totalDates = 30 * durationInMonths - 1;
        String stlDate = LSFUtils.formatDateToString(LSFUtils.dateAdd(new Date(), 0, 0, totalDates));
        log.info("===========LSF : (processSettlementDate) Application Id :" + po.getApplicationId() + " ,Added No Of Days:" + totalDates + " ,Settlement Date:" + stlDate);
        po.setSettlementDate(stlDate);
    }

    private String calculateProfit(Map<String, Object> map) {
        log.info("===========LSF : (calculateProfit)-REQUEST, Parameters:" + gson.toJson(map));
        CommonResponse cmr = new CommonResponse();
        try {
            ProfitResponse profitResponse = new ProfitResponse();
            double profitPercent = getApplicationProfitPercentage(map);
            boolean isRollOver = false;
            if(map.containsKey("isRollOver")) {
                isRollOver = map.get("isRollOver").toString().equalsIgnoreCase("1");
            }
            log.info("===========LSF : (calculateProfit)-Profit Persentate:" + profitPercent);
            if (GlobalParameters.getInstance().getProfitCalculateMethode() == LsfConstants.PROFIT_CALC_TENOR_BASED) {
                if (!map.get("tenorId").toString().equals("-1")) {
                    profitResponse = lsfCore.calculateProfitOnTenor(Integer.parseInt(map.get("tenorId").toString()), Double.parseDouble(map.get("ordervalue").toString()), profitPercent);
                }
            } else {
                int tenor = Integer.parseInt(map.get("tenorId").toString());
                int loanPeriodInDays = 30 * tenor;// days per month is taken as 30
                profitResponse = lsfCore.calculateProfitOnStructure(Double.parseDouble(map.get("ordervalue").toString()), loanPeriodInDays, profitPercent, isRollOver);
            }
            cmr.setResponseCode(200);
            cmr.setResponseObject(profitResponse);
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error on calculating Profit");
        }
        log.info("===========LSF : (calculateProfit)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private double getApplicationProfitPercentage(Map<String, Object> map) {
        try {
            if (map.containsKey("applicationId")) {
                log.info("===========LSF : (getApplicationProfitPercentage) retreaving profit persentage of the applicaiton");
                MurabahApplication application = lsfRepository.getMurabahApplication(map.get("applicationId").toString());
                if (application != null) {
                    log.info("===========LSF : (getApplicationProfitPercentage) retreaving profit persentage of the applicaiton :" + application.getProfitPercentage());
                    return application.getProfitPercentage();
                }
                return 0;
            }
        } catch (Exception ex) {
            log.error("Error getting application profit percentage", ex);
            return 0;
        }
        return 0;
    }

    protected String processOrderAgreement(Map<String, Object> map) { /*---Approve Order Contract---*/
        CommonResponse response = new CommonResponse();
        String applicationID = null;
        String approvedBy = map.get("userName").toString();
        int approvedById = Integer.parseInt(map.get("customerId").toString());
        if (map.containsKey("id")) {
            applicationID = map.get("id").toString();
        }
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
        boolean isCommodityApplication = murabahApplication.getFinanceMethod().equalsIgnoreCase("2");
        if (isCommodityApplication && murabahApplication.getCurrentLevel() > 15) {
            response.setResponseCode(500);
            response.setErrorMessage("Order Agreement cannot be signed for Commodity Application at this stage.");
            log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
            return gson.toJson(response);
        }

        if (murabahApplication.getCurrentLevel() != 18 && !(murabahApplication.getAdminFeeCharged() > 0)) { /*---If order is already liquidate---*/
            int responseCode = 200;
            String responseMessage = "Successful";
            String orderId = null;
            String approveStatus = null;
            String statusComment = null;
            String statusChangedIP = "";

            if (map.containsKey("orderID")) {
                orderId = map.get("orderID").toString();
            }
            if (map.containsKey("approveStatus")) {
                approveStatus = map.get("approveStatus").toString();
            }
            if (map.containsKey("comment")) {
                statusComment = map.get("comment").toString();
            }
            if (map.containsKey("ipAddress")) {
                statusChangedIP = map.get("ipAddress").toString();
            }
            log.debug("===========LSF : (reqApproveOrderAgreement)-Request  : " + gson.toJson(map) + " , Application ID: " + applicationID);
            //   lsfCore.addContractDetailsToMap(applicationID, map);/*---Add Order Contract singed customer data to Map---*/
            if (approveStatus !=null && approveStatus.equalsIgnoreCase("1")) { /*---- If Client accept the agreement----*/
                if(!collaterals.isExchangeAccountCreated() && !murabahApplication.isRollOverApp()) {
                    response.setResponseCode(500);
                    response.setErrorMessage("Exchange Account not yet created.");
                    response.setErrorCode(LsfConstants.ERROR_EXCHNAGE_ACCOUNT_NOT_YET_CREATED);
                    log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                    return gson.toJson(response);
                }

                List<PurchaseOrder> purchaseOrders = lsfRepository.getPurchaseOrderForApplication(applicationID);
                if (purchaseOrders != null && !purchaseOrders.isEmpty()) {
                    PurchaseOrder purchaseOrder = purchaseOrders.getFirst();
                    //if (murabahApplication.getFinanceMethod().equalsIgnoreCase("1") || (purchaseOrder.getIsPhysicalDelivery() == 1 && !murabahApplication.isRollOverApp())) {
                   // if (!murabahApplication.isRollOverApp()) {
                    response = (CommonResponse) lsfCore.releaseCollaterals(collaterals); /*-----Releasing Collaterals----*/
                    log.debug("===========LSF : Released Collateral from OMS :" + purchaseOrder.getId() + " , Application ID :" + applicationID + "Status :" + response.getResponseCode());
              //  }

                    double adminFee = isCommodityApplication ? GlobalParameters.getInstance().getComodityAdminFee() : GlobalParameters.getInstance().getShareAdminFee();
                    AdminFeeRequest adminChargeRequest = new AdminFeeRequest();
                    adminChargeRequest.setReqType(LsfConstants.ADMIN_FEE_REQUEST);
                    // to Do in Roll Over Application, from Account should be the Main LSF cash account
                    adminChargeRequest.setFromCashAccountNo(helper.getCashTransferFromAccount(murabahApplication));

                    double vatAmount=LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(adminFee));
                    adminChargeRequest.setAmount(adminFee + vatAmount);
                    adminChargeRequest.setBrokerVat(vatAmount);
                    //adminChargeRequest.setExchangeVat(vatAmount);

                    response = helper.processOMSCommonResponseAdminFee(helper.sendMessageToOms(gson.toJson(adminChargeRequest)).toString());

                    if (response.getResponseCode() == 200) {
                        murabahApplication.setAdminFeeCharged(adminChargeRequest.getAmount());
                        lsfRepository.updateMurabahApplication(murabahApplication);
                        log.info("===========LSF : Admin Fee Successfully updated Application " + murabahApplication.getCustomerId() + " Customer id " + murabahApplication.getCustomerId() + " Cash Account " + murabahApplication.getCashAccount());
                        lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARGED);
                        log.info("===========LSF : Admin Fee Successfully Charged , CustomerID" + murabahApplication.getCustomerId() + " , Application ID :" + murabahApplication.getId() + " , Account ID:" + murabahApplication.getCashAccount() + " , Total Amount:" + (adminFee +vatAmount) + " ,Vat Amount:" + vatAmount);
                        //update admin fee in PO level,just to keep the record
                        lsfRepository.updateAdminFee(GlobalParameters.getInstance().getSimaCharges(),GlobalParameters.getInstance().getTransferCharges(),vatAmount,purchaseOrder.getId());
                        // at this stage cash collaterals and shares will be transferred only for shares type or physical delivery for commodity type
                        //if (murabahApplication.getFinanceMethod().equalsIgnoreCase("1") || (purchaseOrder.getIsPhysicalDelivery() == 1 && !murabahApplication.isRollOverApp())) {
                        if (!murabahApplication.isRollOverApp()) {
                            transferCollateralsToLSFAccount(murabahApplication, purchaseOrder, collaterals);
                            transferPOToLSFAccount(murabahApplication, purchaseOrder);
                        } else {
                            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
                        }

                    } else {
                        lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARG_FAILED);
                        log.info("===========LSF : Admin Fee Request Failed, ApplicationID :" + murabahApplication.getId() + ", Account ID :" + murabahApplication.getCashAccount());
                    }
                    lsfRepository.updateCustomerOrderStatus(purchaseOrder.getId(), "1", statusComment, statusChangedIP, approvedBy, approvedById);
                    log.debug("===========LSF : Order and Application  Status Changed Successfully, Order ID :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId());
                }

            } else if (approveStatus !=null && approveStatus.equalsIgnoreCase("-1")) { /*---If Client Reject the Agreement---*/
                log.debug("===========LSF :Order Contract Rejected, Order Contract Rejected, Release Collateral from OMS :" + orderId + " , Application ID :" + applicationID + "Status :" + response.getResponseCode());
                response = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                lsfRepository.updateCustomerOrderStatus(orderId, "-1", statusComment, statusChangedIP, approvedBy, approvedById);
                liquidateOrder(orderId, murabahApplication, approveStatus, statusComment, statusChangedIP, collaterals, approvedBy, approvedById);
            }
            response.setResponseCode(responseCode);
            response.setResponseMessage(responseMessage);
            response.setErrorMessage(responseMessage);
            log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
            return gson.toJson(response);
        } else {
            response.setResponseCode(500);
            response.setErrorMessage("Order has been liquidated due to purchase order not acceptance or Already Approved");
            response.setErrorCode(LsfConstants.ERROR_ORDER_HAS_BEEN_lIQUIDATED_DUE_TO_NOT_ACCEPTANCE);
            log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
            return gson.toJson(response);
        }
    }

    private void liquidateOrder(String orderId, MurabahApplication murabahApplication, String approveStatus, String statusComment, String statusChangedIP, MApplicationCollaterals collaterals, String approvedBy, int approvedById) {
        String applicationID = murabahApplication.getId();
        if (!murabahApplication.getFinanceMethod().equalsIgnoreCase("1")) {
            //if(collaterals.isExchangeAccountCreated()) {
            TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());
            CashAcc  lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());

            AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), murabahApplication.getTradingAcc(), lsfCashAccount.getAccountId(), murabahApplication.getCashAccount());

            if (accountDeletionRequestState.isSent()) {
                log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);
            }
//            } else {
//                AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), murabahApplication.getTradingAcc(), lsfCashAccount.getAccountId(), murabahApplication.getCashAccount());
//                log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request not Sent as Exchange Account has not been created.");
//            }
            return;
        }
        
        CommonResponse liquidateResponse = (CommonResponse) lsfCore.liquidate(orderId);
        if (liquidateResponse.getResponseCode() == 1) {
            log.debug("===========LSF : Symbol Liquidation Successful , Order ID :" + orderId + " , Application ID :" + applicationID);
            String orderStatusChangeResponse = lsfRepository.updateCustomerOrderStatus(orderId, approveStatus, statusComment, statusChangedIP, approvedBy, approvedById);
            if (orderStatusChangeResponse.equalsIgnoreCase("1")) {
                log.debug("===========LSF : Order and Application  Status Changed Successfully, Order ID :" + orderId + " , Application ID :" + applicationID + " Approve State :" + approveStatus);
                lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ORDER_CONTRACT_REJECTED);
                notificationManager.sendNotification(murabahApplication);
            }

            if(collaterals.isExchangeAccountCreated()) {
                TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());
                CashAcc  lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());

                AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), murabahApplication.getTradingAcc(), lsfCashAccount.getAccountId(), murabahApplication.getCashAccount());

                if (accountDeletionRequestState.isSent()) {
                    log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);
                }
            } else {
                log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request not Sent as Exchange Account has not been created.");
            }
        }
    }

    private void transferCommodityValuetoLsfCashAccount(MurabahApplication murabahApplication, String poId, double soldAmount) {
        if (!murabahApplication.getFinanceMethod().equalsIgnoreCase("2")) {
            log.debug("===========LSF : Transfer Commodity Value to LSF Cash Account is not allowed for this application.");
            return;
        }
        String appId = murabahApplication.isRollOverApp() ? murabahApplication.getRollOverAppId() : murabahApplication.getId();
        log.info("===========LSF : (performtransferCommodityValuetoLsfCashAccount)-REQUEST  : , ApplicationID:" + appId + ",Order ID:" + poId + ", Amount:" + soldAmount);
        CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(), appId);
        var lsfTypeTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(), appId);
        String masterCashAccount =  GlobalParameters.getInstance().getInstitutionInvestAccount();//.  lsfCore.getMasterCashAccount();
        log.info("===========LSF : (performtransferCommodityValuetoLsfCashAccount) RESPONSE : collaterals transferred to LSF accounts :" + appId + " ,total sold amount :" + soldAmount);
        var isTransferred = lsfCore.cashTransfer(masterCashAccount, lsfCashAccount.getAccountId(), soldAmount, murabahApplication.getId(), lsfTypeTradingAccount.getAccountId());
        log.info("===========LSF : (performtransferCommodityValuetoLsfCashAccount) RESPONSE : collaterals transferred to LSF accounts :" + appId + " ,total sold amount :" + soldAmount);
        if (isTransferred) {
            lsfRepository.updateFacilityTransferStatus(murabahApplication.getId(), LsfConstants.STATUS_COMMODITY_VALUE_TRANSFERRED_TO_LSF_CASH_ACCOUNT);
        } else {
            lsfRepository.updateFacilityTransferStatus(murabahApplication.getId(), LsfConstants.STATUS_COMMODITY_VALUE_TRANSFERRED_TO_LSF_CASH_ACCOUNT_FAILED);
            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.CASH_TRANSFER_FAILED_FROM_OMS);
        }
    }

    private void transferCollateralsToLSFAccount(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder, MApplicationCollaterals collaterals) {
        log.debug("===========LSF : Transferring Collaterals  to LSF type Account :" + purchaseOrder.getId());
        lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
        CommonResponse response = lsfCore.transferCollaterals(collaterals);

        if (response.getResponseCode() == 200) {
            log.debug("===========LSF : Collateral transfer  to LSF type Account Request Sent to OMS :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId() + "Status :" + response);

        } else {
            log.debug("===========LSF: Failed To Send Collateral Transfer to OMS");
            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS);
        }
    }

    private void transferPOToLSFAccount(MurabahApplication murabahApplication, PurchaseOrder purchaseOrder) {
        if (!murabahApplication.getFinanceMethod().equalsIgnoreCase("1")) {
            log.debug("===========LSF : Transfer PO to LSF Account is not allowed for this application.");
            return;
        }

        CommonResponse shareTransferResponse = (CommonResponse) lsfCore.transferToLsfAccount(murabahApplication.getId(), purchaseOrder.getId());
        if (shareTransferResponse.getResponseCode() == 1) {
            log.debug("===========LSF : Transferred PO symbols  :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId() + "Status :" + shareTransferResponse);
            try {
                notificationManager.sendNotification(murabahApplication);
            } catch (Exception e) {  
                log.error("Error sending notification", e);
            }
        } else {
            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS);
            log.debug("===========LSF: Collateral & PO Symbol Transfer Failed.");
        }
        
    }

    private String getCustomerPFSummary(String applicationID) {
        MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);
        List<PurchaseOrder> purchaseOrders = lsfRepository.getAllPurchaseOrder(applicationID);
        if (purchaseOrders.size() == 0) {
            return null;
        } else {
            PurchaseOrder po = purchaseOrders.get(0);
            if (po.getCustomerApproveStatus() == 1 && !(application.getLsfAccountDeletionState() == LsfConstants.REQUEST_SENT_TO_OMS || application.getLsfAccountDeletionState() == LsfConstants.ACCOUNT_DELETION_SUCCESS || application.getLsfAccountDeletionState() == LsfConstants.EXCHANGE_ACCOUNT_DELETION_FAILED_FROM_EXCHANGE || application.getLsfAccountDeletionState() == LsfConstants.SHARE_TRANSFER_FAILED_WITH_EXCHANGE)) {
                if (application != null) {
                    MApplicationCollaterals response = application.isRollOverApp()
                                                       ? lsfCore.reValuationProcess_RollOverApp(application, false,true)
                                                       : lsfCore.reValuationProcess(application,false, true);
                    log.info("===========LSF : (reqDashBoardPFSummary)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                    getBPDetails(response, application);
                    return gson.toJson(response);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private MApplicationCollaterals getBPDetails(final MApplicationCollaterals collaterals, final MurabahApplication application) { // calculating buyingPower for each marginability
        var customerMgtGrp = helper.getMarginabilityGroup(application.getMarginabilityGroup());
        Set<Integer> uniquePercentages = new HashSet<>();
        if (customerMgtGrp != null) {
            log.info("Found Marginability Group for Application: " + application.getId() + ", Group ID: " + customerMgtGrp.getId());
            uniquePercentages.add((int)customerMgtGrp.getGlobalMarginablePercentage());
            if(customerMgtGrp.getMarginableSymbols() != null) {
                for (SymbolMarginabilityPercentage marginabilityPercentage: customerMgtGrp.getMarginableSymbols()) {
                    uniquePercentages.add((int)marginabilityPercentage.getMarginabilityPercentage());
                }
            }
        }

        List<BPSummary> bpList = new ArrayList<>();
        for(Integer marginabilityGroup : uniquePercentages) {
            var liquidityType = new LiquidityType();
            liquidityType.setLiquidName(marginabilityGroup.toString());
            liquidityType.setMarginabilityPercent(Double.valueOf(marginabilityGroup));
            liquidityType.setStockConcentrationPercent(Double.valueOf(marginabilityGroup));
            double calculatedPfForBP = calculatePfForBP(collaterals);
            BPSummary bfSummary = calculateBP(liquidityType, collaterals.getTotalCashColleteral(), calculatedPfForBP, collaterals.getTotalOutstandingForFtv());
            bpList.add(bfSummary);
        }

//        if (marginabilityGroup != null) {
//            attachedLiqGoupList = marginabilityGroup.getMarginabilityList();
//            for (LiquidityType liquidityType : attachedLiqGoupList) {
//                BPSummary bfSummary = calculateBP(liquidityType, collaterals.getTotalCashColleteral(), collaterals.getTotalPFColleteral(), collaterals.getOutstandingAmount());
//                bpList.add(bfSummary);
//            }
//        }
        if (bpList.size() > 0) {
            collaterals.setBuyingPowerSummary(bpList);
        }
        return collaterals;
    }

    private static BPSummary calculateBP(
            LiquidityType liquidityType,
            double totalCash,
            double totalPF,
            double totalOutstanding) {

        BPSummary bpSummary = new BPSummary();
        bpSummary.setMarginabilityType(liquidityType.getLiquidName());

        double marginabilityPercent = liquidityType.getMarginabilityPercent();
        double firstMarginCallRatio = GlobalParameters.getInstance().getFirstMarginCall() / 100.0; // e.g., 160% = 1.6

        // If fully marginable: BP = cash
        if (marginabilityPercent >= 100.0) {
            bpSummary.setBuyingPower(BigDecimal.valueOf(totalCash));
        } else {
            double availableFunds = totalCash + totalPF;
            double requiredEquity = firstMarginCallRatio * totalOutstanding;

            // Funds that can actually be used (after FMC limit)
            double usableFunds = availableFunds - requiredEquity;
            double mfactor = 1 - (liquidityType.getMarginabilityPercent() / 100);

            // Apply marginability factor
            double rawBP = mfactor<= 0 ? totalCash : usableFunds / mfactor;

            // BP can't exceed totalCash or be less than zero
            double finalBP = Math.max(0, Math.min(rawBP, totalCash));

            bpSummary.setBuyingPower(BigDecimal.valueOf(finalBP));
        }

        log.info(String.format(
                "LSF: Calculating Buying Power, Cash: %.2f, PF: %.2f, Outstanding: %.2f, Margin Type: %s, Margin%%: %.2f, BP: %.2f",
                totalCash, totalPF, totalOutstanding, liquidityType.getLiquidName(), marginabilityPercent, bpSummary.getBuyingPower().doubleValue()
        ));

        return bpSummary;
    }

    private double calculatePfForBP(MApplicationCollaterals collaterals) {
        var pfSecurityList = collaterals.getSecurityList();
        double pfForBP = 0.0;
        if ( pfSecurityList == null) {
            return collaterals.getTotalPFColleteral();
        }
        if (!pfSecurityList.isEmpty()) {
            for (Symbol symbol: pfSecurityList) {
                int qtyForPf = symbol.getAvailableQty() + symbol.getSellPending() + symbol.getOpenBuyQty();
                double symbolPf = ((qtyForPf * (symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed())) / 100) * symbol.getMarginabilityPercentage();
                log.info("===========LSF : Calculating PF for BP, Symbol :" + symbol.getSymbolCode() + " , Qty for PF :" + qtyForPf + " , LTP :" + symbol.getLastTradePrice() + " , Marginability% :" + symbol.getMarginabilityPercentage() + " , PF for BP :" + symbolPf);
                pfForBP += symbolPf;
            }
        } else {
            pfForBP = collaterals.getTotalPFColleteral();
        }
        return pfForBP;
    }



//    private static BPSummary calculateBP(LiquidityType liquidityType, double totalCash, double totalPF, double totalOutstanding) {
//        BPSummary BPSummary = new BPSummary();
//        BPSummary.setMarginabilityType(liquidityType.getLiquidName());
//        double marginabilityDifference = 1 - (liquidityType.getMarginabilityPercent() / 100);
//        //double marginabilityDifference = (liquidityType.getMarginabilityPercent() / 100);
//        //marginabilityDifference = marginabilityDifference == 0 ? 1 : marginabilityDifference; // to avoid division by zero
//        if (marginabilityDifference == 1.0) {
//            double bf = (totalPF + totalCash) - (GlobalParameters.getInstance().getFirstMarginCall() / 100) * totalOutstanding;
//            if (bf > totalCash) {
//                BPSummary.setBuyingPower(BigDecimal.valueOf(totalCash));
//            } else {
//                BPSummary.setBuyingPower(bf > 0 ? BigDecimal.valueOf(bf) : BigDecimal.ZERO);
//            }
//
//        } else {
//            double sum = totalCash + totalPF;
//            double buyingPower = (sum - (GlobalParameters.getInstance().getFirstMarginCall() / 100) * totalOutstanding) / marginabilityDifference;
//            if (buyingPower > 0) {
//                if (buyingPower > totalCash) {
//                    BPSummary.setBuyingPower(BigDecimal.valueOf(totalCash));
//
//                } else {
//                    BPSummary.setBuyingPower(buyingPower > 0 ? BigDecimal.valueOf(buyingPower) : BigDecimal.ZERO);
//                }
//            } else {
//                BPSummary.setBuyingPower(BigDecimal.ZERO);
//            }
//        }
//        log.info("===========LSF : Calculating Buying Power, Total Cash :" + totalCash + " , Total PF :" + totalCash + " , OutStanding :" + totalOutstanding + ", Margin Type :" + liquidityType.getLiquidName() + " , percentage :" + liquidityType.getMarginabilityPercent() + " BP:" + BPSummary.getBuyingPower());
//
//        return BPSummary;
//    }

    private String updateSoldAmount(Map<String, Object> map) {
        String poId = map.get("id").toString();
        log.info("updatePurchaseOrdByAdmin PO Id: " + poId);
        String approvedbyId = map.get("approvedById").toString();
        String approvedbyName = map.get("approvedByName").toString();
        String statusChangedIP = map.get("ipAddress").toString();
        CommonResponse cmr = new CommonResponse();
        try {
            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
            if (po == null) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("PO not found");
                return gson.toJson(cmr);
            }
            MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
            if (fromDB != null && fromDB.getCurrentLevel() != 16 && po.getApprovalStatus() != 1) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Purchase order is already Submitted");
                return gson.toJson(cmr);
            }
            po.setCertificatePath(map.get("certificatePath").toString());

            List<Map<String, Object>> commodityListMap = (List<Map<String, Object>>) map.get("commodityList");
            List<Commodity> commodities = getCommodityList(commodityListMap);

            updatePoCommodityList(po, commodities);
      //      lsfRepository.updateCommodityPOExecution(po);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);
            po.setApprovalStatus(1);
            String response = lsfRepository.approveRejectPOCommodity(po);
            if(response.equalsIgnoreCase("1")) {
                lsfRepository.updatePurchaseOrderByAdmin(po);
                lsfRepository.updateActivity(fromDB.getId(), LsfConstants.STATUS_COMMODITY_PO_SOLD_AMT_UPDATED);
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Commodity Order Approved");
            }


//            po.setApprovalStatus(2);// pending approval by admin
//            String approvedbyId = "SYSTEM";
//            String approvedbyName = "SYSTEM";
//            po.setApprovedById(approvedbyId);
//            po.setApprovedByName(approvedbyName);
//            lsfRepository.approveRejectOrder(po);

            String statusMessage = "Sold Amount Added";
            lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);

            cmr.setResponseCode(200);
            cmr.setResponseMessage("Successfully Updated the Purchase order by admin");
            log.info("Successfully Updated the Sold Amount order by admin,pending Approval");
        }catch (Exception e){
            cmr.setErrorCode(500);
            cmr.setErrorMessage("PO Update Failed");
            log.error("PO Update Failed", e);
        }
        return gson.toJson(cmr);
    }

    private String updatePurchaseOrdByAdmin(Map<String, Object> map){
        String statusChangedIP = map.get("ipAddress").toString();
        String poId = map.get("id").toString();
        log.info("updatePurchaseOrdByAdmin PO Id: " + poId);
        String approvedbyId = map.get("approvedById").toString();
        String approvedbyName = map.get("approvedByName").toString();
        CommonResponse cmr = new CommonResponse();
        try {
            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
            if (po == null) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("PO not found");
                return gson.toJson(cmr);
            }
            MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
             if (fromDB != null && fromDB.getCurrentLevel() > 14) {
                 cmr.setResponseCode(500);
                 cmr.setErrorMessage("Purchase order is already Submitted");
                 return gson.toJson(cmr);
             }
            updateCommodityPODetails(po, map, Integer.parseInt(approvedbyId), approvedbyName);

// down below moved to PO update final method
//            ProfitResponse profitResponse = lsfCore.calculateProfit(
//                    Integer.parseInt(fromDB.getTenor()),
//                    totalSoldAmount,
//                    fromDB.getProfitPercentage());
//
//            log.debug("===========LSF : (updatePurchaseOrdByAdmin)-REQUEST , orderID :"
//                         + po.getId()
//                         + " , order completed value:"
//                         + totalSoldAmount
//                         + " , new Profit:"
//                         + profitResponse.getProfitAmount());
//
//            String statusMessage = "Purchase order submited by ADMIN";
//            String statusChangedUserid = "Admin";
//            String statusChangedUserName = "Admin";
//
//            String responseMessage = lsfRepository.approveApplication(1, fromDB.getId(), statusMessage, statusChangedUserid, statusChangedUserName, statusChangedIP);
//            //lsfRepository.updateActivity(fromDB.getId(), -1);
//
//            String response = lsfRepository.upadateOrderStatus(
//                    po.getId(),
//                    1,
//                    totalSoldAmount,
//                    profitResponse.getProfitAmount(),
//                    profitResponse.getProfitPercent(),
//                    0); // vat amount is 0 for now
//
//            if (response.equalsIgnoreCase("1")) {
//                lsfRepository.updateActivity(
//                        fromDB.getId(),
//                        LsfConstants.STATUS_PO_FILLED_WAITING_FOR_ACCEPTANCE);
//                notificationManager.sendPOAcceptanceReminders(
//                        fromDB,
//                        po,
//                        1,
//                        false);
//            }
//            notificationManager.sendNotification(fromDB);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);
            po.setApprovalStatus(1);
            String response = lsfRepository.approveRejectPOCommodity(po);
            if(response.equalsIgnoreCase("1")) {
                lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_UPDATED_BY_ADMIN);
                String statusMessage = "PO Value Added by Admin";
                lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Commodity Order Approved");
            }
            log.info("Successfully Updated the Purchase order by admin");
        }catch (Exception e){
            cmr.setErrorCode(500);
            cmr.setErrorMessage("PO Update Failed");
            log.error("PO Update Failed", e);
        }
        return gson.toJson(cmr);
    }

    private void updateCommodityPODetails(PurchaseOrder po, Map<String, Object> map, int approvedbyId, String approvedbyName) {
        if(map.containsKey("sellButNotSettle")) {
            po.setSellButNotSettle((int) Double.parseDouble(map.get("sellButNotSettle").toString()));
        }
        if(map.containsKey("isPhysicalDelivery")) {
            po.setIsPhysicalDelivery((int) Double.parseDouble(map.get("isPhysicalDelivery").toString()));
        }
        if(map.containsKey("certificatePath")) {
            po.setCertificatePath(map.get("certificatePath").toString());
        }
        if(map.containsKey("certificateNumber")) {
            po.setCertificateNumber(map.get("certificateNumber").toString());
        }
        List<Commodity> commodityList = (List<Commodity>) map.get("commodityList");
        List<Commodity> commodities = new ArrayList<>();
        double totalSoldAmount = 0.0;
        if (commodityList != null) {
            if (commodityList.size() > 0) {
                for (int i = 0; i < commodityList.size(); i++) {
                    Map<String, Object> params = (Map<String, Object>) commodityList.get(i);
                    Commodity commodity = new Commodity();
                    commodity.setSymbolCode(params.get("symbolCode").toString());
                    commodity.setSymbolName(params.get("symbolName").toString());
                    commodity.setShortDescription(params.get("shortDescription").toString());
                    commodity.setExchange(params.get("exchange").toString());
                    commodity.setBroker(params.get("broker").toString());
                    commodity.setPrice(Double.parseDouble(params.get("price").toString()));
                    commodity.setUnitOfMeasure(params.get("unitOfMeasure").toString());
                    commodity.setStatus((int) Double.parseDouble(params.get("status").toString()));
                    commodity.setPercentage(Double.parseDouble(params.get("percentage").toString()));
                    commodity.setSoldAmnt((int) Double.parseDouble(params.get("soldAmnt").toString()));
                    commodity.setBoughtAmnt(Double.parseDouble(params.get("boughtAmnt").toString()));
                    totalSoldAmount += commodity.getBoughtAmnt();
                    commodities.add(commodity);

                }
            }
        }
        po.setCommodityList(commodities);
        po.setOrderCompletedValue(totalSoldAmount);

        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(po.getApplicationId());

        collaterals.setOutstandingAmount(LSFUtils.ceilTwoDecimals(totalSoldAmount));
        lsfRepository.addEditCollaterals(collaterals, approvedbyName, approvedbyId);
        lsfRepository.updatePurchaseOrderByAdmin(po);
    }

    protected String approveL1CommodityPOBoughtAmnt(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String applicationId = "";
        try {
            String poId = map.get("id").toString();
            applicationId = map.get("applicationId").toString();
            String approvedbyId = map.get("approvedById").toString();
            String approvedbyName = map.get("approvedByName").toString();
            String statusChangedIP = map.get("ipAddress").toString();
            int approvalStatus = map.get("approvalStatus") != null ? (int) Double.parseDouble(map.get("approvalStatus").toString()) : 0;
            log.info("===========LSF : (approveL1CommodityPOBoughtAmnt)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName);

            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
//            if(po.getApprovalStatus() != 0) {
//                cmr.setResponseCode(500);
//                cmr.setErrorMessage("Purchase order is already Approved for L1");
//                return gson.toJson(cmr);
//            }
            updateCommodityPODetails(po, map, Integer.parseInt(approvedbyId), approvedbyName);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);
            if (approvalStatus == 1) {
                po.setApprovalStatus(LsfConstants.PO_APPROVAL_L1_BOUGHT_AMT);
                String response = lsfRepository.approveRejectPOCommodity(po);
                if(response.equalsIgnoreCase("1")) {
                    lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_UPDATED_BY_ADMIN_L1_APPROVED);
                    MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
                    String statusMessage = "PO Level 1 Approved";
                    lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Commodity Order Approved");
                }
            } else {
                po.setApprovalStatus(LsfConstants.PO_REJECT_L1_BOUGHT_AMT);
                String response = lsfRepository.approveRejectPOCommodity(po);
                if(response.equalsIgnoreCase("1")) {
                    // update to the previous activity status
                    lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_SENT_INVESTOR_ACCOUNT_CREATION);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Commodity Order Rejected");
                }
            }
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Order Approve failed");
            log.error("Order Approve failed", ex);
        }
        log.debug("===========LSF : (approvePurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String finalApproveCommodityPoBoughtAmt(Map<String, Object> map) {
        String statusChangedIP = map.get("ipAddress").toString();
        String poId = map.get("id").toString();
        String approvedbyId = map.get("approvedById").toString();
        String approvedbyName = map.get("approvedByName").toString();
        int approvalStatus = map.get("approvalStatus") != null ? (int) Double.parseDouble(map.get("approvalStatus").toString()) : 0;
        CommonResponse cmr = new CommonResponse();

        PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
        if (po == null) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("PO not found");
            return gson.toJson(cmr);
        }

        if(approvalStatus == -1) {
            po.setApprovalStatus(LsfConstants.PO_REJECT_L1_SOLD_AMT);
            String response = lsfRepository.approveRejectPOCommodity(po);
            if(response.equalsIgnoreCase("1")) {
                lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_UPDATED_BY_ADMIN);
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Commodity Order Rejected");
            }
            return gson.toJson(cmr);
        }


        MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
        if (fromDB != null && fromDB.getCurrentLevel() > 14) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Purchase order is already Submitted");
            return gson.toJson(cmr);
        }

        updateCommodityPODetails(po, map, Integer.parseInt(approvedbyId), approvedbyName);
        double totalBoutAmount = po.getCommodityList().stream()
                .mapToDouble(Commodity::getBoughtAmnt)
                .sum();


        ProfitResponse profitResponse = lsfCore.calculateProfit(
                Integer.parseInt(fromDB.getTenor()),
                totalBoutAmount,
                fromDB.getProfitPercentage());

        log.debug("===========LSF : (finalApproveCommodityPoBoughtAmt)-REQUEST , orderID :"
                  + po.getId()
                  + " , order completed value:"
                  + totalBoutAmount
                  + " , new Profit:"
                  + profitResponse.getProfitAmount());

        String statusMessage = "PO Bought Amount Approved by Admin";

        String responseMessage = ""; // lsfRepository.approveApplication(1, fromDB.getId(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);

        String response = lsfRepository.upadateOrderStatus(
                po.getId(),
                2, // 2 filled and ready for acceptance
                totalBoutAmount,
                profitResponse.getProfitAmount(),
                profitResponse.getProfitPercent(),
                0, statusChangedIP);

        if (response.equalsIgnoreCase("1")) {
            lsfRepository.updateActivity(
                    fromDB.getId(),
                    LsfConstants.STATUS_PO_FILLED_WAITING_FOR_ACCEPTANCE);
            notificationManager.sendPOAcceptanceReminders(
                    fromDB,
                    po,
                    1,
                    false);
        }
        notificationManager.sendNotification(fromDB);
        String message = "PO Level 2 Approved";
        lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), message, approvedbyId, approvedbyName, statusChangedIP);
        cmr.setResponseCode(200);
        cmr.setResponseMessage(responseMessage + "|" + "Successfully Approved the Purchase order by admin");
        log.info("PO {} Successfully Approved by admin", poId);
        return gson.toJson(cmr);
    }

    private String commodityPOExecution(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        try {
            String poId = map.get("id").toString();
            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);

            if (po.getIsPhysicalDelivery() == 1) {
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Order is already in Physical Delivery, cannot be executed");
                return gson.toJson(cmr);
            }
            MurabahApplication application = lsfRepository.getMurabahApplication(po.getApplicationId());
            if (application == null) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Application not found for the given PO ID");
                return gson.toJson(cmr);
            }
            int currentLevel= 16;
            if (currentLevel !=application.getCurrentLevel() && po.getApprovalStatus() != 3) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Application is already approved");
                log.info("===========LSF : (commodityPOExecute) RESPONSE : application level update failed :" + application.getId() + " ,current Level :" + currentLevel + " Already approved.");
                return gson.toJson(cmr);
            }
            String statusChangedIP = map.get("ipAddress").toString();
            if (statusChangedIP == null || statusChangedIP.isEmpty()) {
                cmr.setResponseCode(500);
                cmr.setResponseMessage("IP Address is not detected");
                cmr.setErrorMessage("IP Address is not detected");
                cmr.setErrorCode(LsfConstants.ERROR_IP_ADDRESS_IS_NOT_DETECTED);
                log.info("===========LSF : (commodityPOExecute) PO Execution Response  :" + gson.toJson(cmr));
                return gson.toJson(cmr);
            }

            if (!LSFUtils.validateAdminApproveAction(statusChangedIP)) {
                cmr.setResponseCode(500);
                cmr.setResponseMessage("Abnormal Activity");
                cmr.setErrorMessage("Abnormal Activity");
                cmr.setErrorCode(LsfConstants.ERROR_ABNORMAL_ACTIVITY);
                log.info("===========LSF : (commodityPOExecute)LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
                return gson.toJson(cmr);
            }

            int approvalStatus = map.get("approvalStatus") != null ? (int) Double.parseDouble(map.get("approvalStatus").toString()) : 0;
            if (approvalStatus == -1) {

                po.setApprovalStatus(LsfConstants.PO_REJECT_L1_SOLD_AMT);
                String response = lsfRepository.approveRejectPOCommodity(po);
                if(response.equalsIgnoreCase("1")) {
                    lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_SOLD_AMT_UPDATED);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Commodity Order Rejected");
                }
                return gson.toJson(cmr);
            }
            List<Map<String, Object>> commodityListMap = (List<Map<String, Object>>) map.get("commodityList");
            List<Commodity> commodities = getCommodityList(commodityListMap);

            updatePoCommodityList(po, commodities);
            double totalSoldAmount = getTotalSoldAmount(po);

            updatePoCommodityList(po, commodities);
            lsfRepository.updatePurchaseCommodityList(po.getId(), po.getCommodityList());
            lsfRepository.updateActivity(po.getApplicationId(), LsfConstants.STATUS_COMMODITY_PO_EXECUTED);
            transferCommodityValuetoLsfCashAccount(application, po.getId(), totalSoldAmount);

            // If the Application is RollOver, then need to settle the Previouse Loan
            // if PO.sellButNotSettle is 0
            if (po.getSellButNotSettle() == 0 && application.isRollOverApp()) {
                log.info("Po Sell But Not Settle is set to : {}", po.getSellButNotSettle());
                performSettlementPreviousCommodity(application.getRollOverAppId(),  statusChangedIP);
            } else {
                log.info("===========LSF : (commodityPOExecution) PO Sell But Not Settle is set to 1, so not settling the Murabah Application");
            }

            MurabahApplication fromDB = lsfRepository.getMurabahApplication(po.getApplicationId());
            String statusMessage = "Sold Amount Level 2 Approved";
            String approvedbyId = map.get("approvedById").toString();
            String approvedbyName = map.get("approvedByName").toString();
            lsfRepository.commodityAppStatus(po.getApplicationId(), fromDB.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);

            cmr.setResponseCode(200);
            cmr.setResponseMessage("Successfully Updated the Purchase order by admin");
        } catch (Exception e) {
            cmr.setErrorCode(500);
            cmr.setErrorMessage("PO Execution Failed");
            log.error("PO Execution Failed", e);
        }

        return gson.toJson(cmr);
    }

    private void performSettlementPreviousCommodity(String originalApplicationId, String statusChangedIP) {
        var allPo = lsfRepository.getAllPurchaseOrder(originalApplicationId);
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(originalApplicationId);
        if ( collaterals == null) {
            log.info("===========LSF : (commodityPOExecution) No Collateral found for the Application ID : {}", originalApplicationId);
            return;
        }
        double settlementAmount = collaterals.getOutstandingAmount();

         // check if there is any outstanding amount in the collateral, if yes then do not settle the application
        if (allPo != null && !allPo.isEmpty()) {
            PurchaseOrder po = allPo.getFirst();
            log.info("===========LSF : (commodityPOExecution) PO Sell But Not Settle is set to 0, so settling the Murabah Application: {}, Settlement Amount : {}", originalApplicationId, settlementAmount);
            settlementProcessor.performEarlySettlementCommodity(originalApplicationId, po.getCustomerId(), settlementAmount, po.getId());
        }
    }

    private double getTotalSoldAmount(PurchaseOrder po){
        return po.getCommodityList().stream().mapToDouble(Commodity::getSoldAmnt).sum();
    }

    private void updatePoCommodityList(PurchaseOrder po, List<Commodity> commodities){
        commodities.forEach(commodity -> {
            po.getCommodityList().stream().filter(c -> c.getSymbolCode().equals(commodity.getSymbolCode())).findFirst().ifPresent(c -> {
                c.setSoldAmnt(commodity.getSoldAmnt());
            });
        });
    }

    private List<Commodity> getCommodityList(List<Map<String, Object>> commodityListMap){

        List<Commodity> commodities = new ArrayList<>();
        if (commodityListMap != null) {    
            if (commodityListMap.size() > 0) {
                for (int i = 0; i < commodityListMap.size(); i++) {
                    Map<String, Object> params = (Map<String, Object>) commodityListMap.get(i);
                    Commodity commodity = new Commodity();
                    commodity.setSymbolCode(params.get("symbolCode").toString());
                    commodity.setSymbolName(params.get("symbolName").toString());
                    commodity.setShortDescription(params.get("shortDescription").toString());
                    commodity.setExchange(params.get("exchange").toString());
                    commodity.setBroker(params.get("broker").toString());
                    commodity.setPrice(Double.parseDouble(params.get("price").toString()));
                    commodity.setUnitOfMeasure(params.get("unitOfMeasure").toString());
                    commodity.setStatus((int) Double.parseDouble(params.get("status").toString()));
                    commodity.setPercentage(Double.parseDouble(params.get("percentage").toString()));
                    commodity.setSoldAmnt(Double.parseDouble(params.get("soldAmnt").toString()));
                    commodities.add(commodity);

                }
            }
        }
        return commodities;
    }


    private String updateAuthAbicToSell(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        PurchaseOrder po = new PurchaseOrder();

        String authAbicToSell = map.get("authAbicToSell").toString();
        
        po.setId(map.get("poid").toString());
        po.setAuthAbicToSell(authAbicToSell);
        if (Objects.equals(authAbicToSell, "0") || authAbicToSell.equalsIgnoreCase("0")){
            po.setIsPhysicalDelivery(1);
        }else {
            po.setIsPhysicalDelivery(0);
        }
        String key = lsfRepository.addAuthAbicToSellStatus(po);
        if (key.equalsIgnoreCase("1")){
            String approvedbyId = map.get("customerId").toString();
            String approvedbyName = map.get("userName").toString();
            String statusChangedIP = map.get("ipAddress").toString();
            String appId = map.get("appId").toString();
            int currentLevel = 16;
            String statusMessage = "Authorized ABIC to Sell";
            lsfRepository.commodityAppStatus(appId, currentLevel, statusMessage, approvedbyId, approvedbyName, statusChangedIP);

            cmr.setResponseCode(200);
            cmr.setResponseMessage(key+"|Confirmed");
        }else {
            cmr.setErrorCode(500);
            cmr.setErrorMessage("Authentication Abic to Sell Failed : "+po.getId() +" status : "+po.getAuthAbicToSell());
        }

        return gson.toJson(cmr);
    }
    private String revertPOToSellorDeliver(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        PurchaseOrder po = new PurchaseOrder();
        po.setId(map.get("poid").toString());
        po.setAuthAbicToSell(map.get("authAbicToSell").toString());
        if (Objects.equals(po.getAuthAbicToSell(), "0")) {
            po.setIsPhysicalDelivery(1);
        }else {
            po.setIsPhysicalDelivery(0);
        }
        String key = lsfRepository.addAuthAbicToSellStatus(po);
        if (key.equalsIgnoreCase("1")){
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key+"|Confirmed");
        }else {
            cmr.setErrorCode(500);
            cmr.setErrorMessage("Authentication Abic to Sell Failed : "+po.getId() +" status : "+po.getAuthAbicToSell());
        }

        return gson.toJson(cmr);
    }
}