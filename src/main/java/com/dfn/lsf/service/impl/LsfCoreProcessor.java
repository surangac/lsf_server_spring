package com.dfn.lsf.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.util.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.BPSummary;
import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.CommissionStructure;
import com.dfn.lsf.model.Commodity;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.Installments;
import com.dfn.lsf.model.LiquidityType;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MarginabilityGroup;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.OrderProfit;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.PurchaseOrderListResponse;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.TradingAcc;
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
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing core operation request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
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
                case LsfConstants.COMMODITY_PO_EXECUTE:
                    return commodityPOExecution(requestMap);
                case LsfConstants.CONFIRM_AUTH_ABIC_TO_SELL_BY_USER:
                    return updateAuthAbicToSell(requestMap);
                case LsfConstants.REVERT_TO_SELL_DELIVER_BY_ADMIN:
                    return revertPOToSellorDeliver(requestMap);
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
        List<PurchaseOrder> orderList = null;
        try {
            orderList = lsfRepository.getAllPurchaseOrder(applicationId);
            if (orderList.size() > 0) {
                for (PurchaseOrder po : orderList) {
                    MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCollateral(po.getApplicationId());
                    po.setTotalOutStandingBalance(mApplicationCollaterals.getOutstandingAmount());
                }
                purchaseOrderListResponse.setPurchaseOrderList(orderList);

                double administrationFee = GlobalParameters.getInstance().getComodityAdminFee() + GlobalParameters.getInstance().getComodityFixedFee();
                if (murabahApplication.getFinanceMethod().equals("1")) {
                    administrationFee = GlobalParameters.getInstance().getShareAdminFee() + GlobalParameters.getInstance().getShareFixedFee();  
                }
                purchaseOrderListResponse.setAdministrationFee(administrationFee);

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
        int financeMethod = 1;
        if (map.containsKey("financeMethod")) {
            financeMethod = Integer.parseInt(map.get("financeMethod").toString());
        }

        MApplicationCollaterals collaterals = lsfRepository.getApplicationCollateral(po.getApplicationId());

        if (LSFUtils.isPurchaseOrderCreationAllowed()) { /*----PO Submission should be allowed until 1h 15min before market close time---*/

            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(po.getApplicationId());
            if (Integer.parseInt(murabahApplication.getOverallStatus()) < 0 || murabahApplication.getCurrentLevel() == GlobalParameters.getInstance().getGetAppCloseLevel()) {
                commonResponse.setResponseCode(500);
                commonResponse.setErrorMessage("Invalid Details");
                commonResponse.setErrorCode(LsfConstants.ERROR_INVALID_DETAILS);
                log.debug("===========LSF : (createPurchaseOrder)-LSF-SERVER RESPONSE  : " + gson.toJson(commonResponse));
                return gson.toJson(commonResponse);
            }


            /*---------------------------------------*/
            boolean lsfTrdAccCreated = false;
            boolean isCommodity = false;
            if (financeMethod == 1){
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
                lsfTrdAccCreated = lsfCore.createAccounts(accountCreationRequest);
                isCommodity = false;
            }else {
                lsfTrdAccCreated = false;
                isCommodity = true;
            }

            if (isCommodity || lsfTrdAccCreated) {

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
                log.debug("===========LSF : Automatic Order Approval ,status :" + approvePurchaseOrderABIC(po, collaterals, commonResponse, financeMethod));

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

    protected String approvePurchaseOrderABIC(PurchaseOrder purchaseOrder, MApplicationCollaterals collaterals, CommonResponse cmr, int financeMethod) {

        String applicationId = "";
        try {
            String poId = purchaseOrder.getId();
            applicationId = purchaseOrder.getApplicationId();

            String approvedbyId = "SYSTEM";
            String approvedbyName = "SYSTEM";
            int approvalStatus = 1;
            log.info("===========LSF : (approvePurchaseOrder)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName + " ,approvalStatus:" + approvalStatus);

            if (approvalStatus > 0) { /*---Will run only this block---*/
                OrderBasket orderBasket = createPOInstruction(purchaseOrder);
                boolean isOrderCreated = false;
                if (financeMethod == 1) {
                    isOrderCreated = setPOInstructionToOMS(orderBasket, purchaseOrder.getApplicationId());
                }
                if (isOrderCreated || financeMethod != 1) {
                    lsfRepository.addPurchaseOrder(purchaseOrder);
                    lsfRepository.addEditCollaterals(collaterals);
                    purchaseOrder.setApprovalStatus(approvalStatus);
                    purchaseOrder.setApprovedById(approvedbyId);
                    purchaseOrder.setApprovedByName(approvedbyName);
                    lsfRepository.approveRejectOrder(purchaseOrder);
                    cmr.setResponseCode(200);
                    MurabahApplication application = lsfRepository.getMurabahApplication(applicationId);
                    cmr.setResponseMessage(Integer.toString(application.getCurrentLevel()) + "|" + application.getOverallStatus());
                    notificationManager.sendNotification(application);/*---Send Notification---*/
                    lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATED_WAITING_TO_ORDER_FILL);

                    MApplicationCollaterals completeCollateral = lsfRepository.getApplicationCompleteCollateral(applicationId);

                    //MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
                    CommonInqueryMessage request = new CommonInqueryMessage();
                    Map<String, Object> resultMap = new HashMap<>();

                    String result = "";
                    TradingAcc t = null;

                    if (financeMethod == 1) {
                        /*---- request for LSF Type Trading Accounts----*/
                        request.setCustomerId(application.getCustomerId());
                        request.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
//                        request.setReqType(47);
                        request.setContractId(applicationId);
                        result = (String) helper.sendMessageToOms(gson.toJson(request));
                        resultMap.clear();
                        resultMap = gson.fromJson((String) result, resultMap.getClass());
                        ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
                        String tradingAccId;
                        for (Map<String, Object> trd : lsfTrd) {
                            Map<String, Object> mpTRadingAcc = (Map<String, Object>) trd.get("tradingAccount");
                            tradingAccId = mpTRadingAcc.get("accountId").toString();
                            t = completeCollateral.isTradingAccountLSFTypeExist(tradingAccId);
                            t.setExchange(mpTRadingAcc.get("exchange").toString());
                            t.setApplicationId(applicationId);
                            t.setLsfType(true);
                        }
                    }
                /*----request for LSF type Cash Acc-----*/
                    result = "";
                    String cashAccid = null;
                    request.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
                    request.setContractId(applicationId);
                    result = (String) helper.sendMessageToOms(gson.toJson(request));
                    resultMap.clear();
                    resultMap = gson.fromJson((String) result, resultMap.getClass());
                    ArrayList<Map<String, Object>> lsfcash = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
                    for (Map<String, Object> cash : lsfcash) {
                        cashAccid = cash.get("accountNo").toString();
                        CashAcc c = completeCollateral.isCashAccLSFTypeExist(cashAccid);
                        c.setCashBalance(Double.parseDouble(cash.get("balance").toString()));
                        c.setApplicationId(applicationId);
                        c.setLsfType(true);
                    }

                    lsfRepository.addEditCompleteCollateral(completeCollateral); /*----Update LSF Type Cash & Trading Accounts------*/

                    /*----- creating investor account for LSF type cash account----*/
                    if (completeCollateral.isLSFCashAccExist() && completeCollateral.isLSFTradingAccExist()) {
                        log.debug("===========LSF : Creating Investor Account for Application ID :" + application.getId() + " , Cash Account ID:" + cashAccid);
                        AccountCreationRequest createInvestorAccount = new AccountCreationRequest();
                        createInvestorAccount.setReqType(LsfConstants.CREATE_INVESTOR_ACCOUNT);
                        createInvestorAccount.setFromCashAccountNo(cashAccid);
                        String omsResponseForInvestorAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createInvestorAccount));
                        CommonResponse investorAccountResponse = helper.processOMSCommonResponseAccountCreation(omsResponseForInvestorAccountCreation);
                        lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_SENT_INVESTOR_ACCOUNT_CREATION);

                        if (investorAccountResponse.getResponseCode() == -2) { /*---If Investor Account is already created---*/
                            log.debug("===========LSF :Investor Account Already Created  for Application ID :" + application.getId() + " , Cash Account ID:" + cashAccid);
                            AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
                            createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
                            createExchangeAccount.setTradingAccountId(t!=null? t.getAccountId(): "");
                            createExchangeAccount.setExchange(t !=null ?t.getExchange():"");
                            log.debug("===========LSF : Creating Exchange Account for Trading Account :" + createExchangeAccount.getTradingAccountId());
                            String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(createExchangeAccount));
                            CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(omsResponseForExchangeAccountCreation);
                            if (exchangeAccountResponse.getResponseCode() == 1) {
                                lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
                            } else {
                                lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
                            }

                        } else if (investorAccountResponse.getResponseCode() == -1) {
                            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED_OMS);
                        }

                    }


                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Order send to OMS Failed");
                    cmr.setErrorCode(LsfConstants.ERROR_ORDER_SEND_TO_OMS_FAILED);
                    lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATION_FAILED);
                }
            } else {
                if (lsfRepository.approveRejectOrder(purchaseOrder).equalsIgnoreCase("1")) {
                    MApplicationCollaterals blockedColaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
                    CommonResponse commonResponse = (CommonResponse) lsfCore.releaseCollaterals(blockedColaterals);
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
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_PO_CREATION_FAILED);
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
            int approvalStatus = Integer.parseInt(map.get("approvalStatus").toString());
            log.info("===========LSF : (approvePurchaseOrder)-REQUEST, poID :" + poId + " , applicationID:" + applicationId + " ,approvedById: " + approvedbyId + ", approvedbyName" + approvedbyName + " ,approvalStatus:" + approvalStatus);

            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(poId);
            po.setApprovalStatus(approvalStatus);
            po.setApprovedById(approvedbyId);
            po.setApprovedByName(approvedbyName);

            if (approvalStatus > 0) {
                OrderBasket orderBasket = createPOInstruction(po);
                if (setPOInstructionToOMS(orderBasket, po.getApplicationId())) {
                    lsfRepository.approveRejectOrder(po);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Order Approved and send to OMS");
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Order send to OMS Failed");
                    cmr.setErrorCode(LsfConstants.ERROR_ORDER_SEND_TO_OMS_FAILED);
                }
            } else {
                if (lsfRepository.approveRejectOrder(po).equalsIgnoreCase("1")) {
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
        if (!lsfCore.validateSymbolAmountAgainstConcentration(concenTrationGrp, purchaseOrder.getSymbolList())) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Stock Concentration validation failed");
            return false;
        }
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
            log.info("===========LSF : (calculateProfit)-Profit Persentate:" + profitPercent);
            if (GlobalParameters.getInstance().getProfitCalculateMethode() == LsfConstants.PROFIT_CALC_TENOR_BASED) {
                if (!map.get("tenorId").toString().equals("-1")) {
                    profitResponse = lsfCore.calculateProfitOnTenor(Integer.parseInt(map.get("tenorId").toString()), Double.parseDouble(map.get("ordervalue").toString()), profitPercent);
                }
            } else {
                int tenor = Integer.parseInt(map.get("tenorId").toString());
                int loanPeriodInDays = 30 * tenor;// days per month is taken as 30
                profitResponse = lsfCore.calculateProfitOnStructure(Double.parseDouble(map.get("ordervalue").toString()), loanPeriodInDays, profitPercent);
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
        if (map.containsKey("id")) {
            applicationID = map.get("id").toString();
        }
        MurabahApplication murabahApplication = null;
        murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);

        if (murabahApplication.getCurrentLevel() != 18 && (murabahApplication !=null && !(murabahApplication.getAdminFeeCharged() > 0))) { /*---If order is already liquidate---*/
            if (LSFUtils.isMarketOpened()) {//*---If market is closed stop signing order contract---*/
                int responseCode = 200;
                String responseMessage = "Successful";
                String orderId = null;
                String approveStatus = null;
                String statusComment = null;
                String statusChangedIP = "";
                CommonResponse liquidateResponse;
                String orderStatusChangeResponse = "";


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
                    if(murabahApplication.getFinanceMethod().equalsIgnoreCase("1") && !collaterals.isExchangeAccountCreated()){
                        response.setResponseCode(500);
                        response.setErrorMessage("Exchange Account not yet created.");
                        response.setErrorCode(LsfConstants.ERROR_EXCHNAGE_ACCOUNT_NOT_YET_CREATED);
                        log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                        return gson.toJson(response);
                    }

                    List<PurchaseOrder> purchaseOrders = lsfRepository.getPurchaseOrderForApplication(applicationID);
                    if (purchaseOrders != null && purchaseOrders.size() > 0) {
                        PurchaseOrder purchaseOrder = purchaseOrders.get(0);
                        // CommonResponse response = new CommonResponse();
                        response = (CommonResponse) lsfCore.releaseCollaterals(collaterals); /*-----Releasing Collaterals----*/
                        log.debug("===========LSF : Released Collateral from OMS :" + purchaseOrder.getId() + " , Application ID :" + applicationID + "Status :" + response.getResponseCode());

                        AdminFeeRequest adminChargeRequest = new AdminFeeRequest(); /*---Sending Admin Fee Request----*/
                        adminChargeRequest.setReqType(LsfConstants.ADMIN_FEE_REQUEST);
                        adminChargeRequest.setFromCashAccountNo(murabahApplication.getCashAccount());
                        double adminFee = GlobalParameters.getInstance().getSimaCharges() + GlobalParameters.getInstance().getTransferCharges();
                        double vatAmount=LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(adminFee));
                        adminChargeRequest.setAmount(adminFee+vatAmount);
                        adminChargeRequest.setBrokerVat(vatAmount);
                        adminChargeRequest.setExchangeVat(0.0);
                        response = helper.processOMSCommonResponseAdminFee(helper.sendMessageToOms(gson.toJson(adminChargeRequest)).toString());

                        if (response.getResponseCode() == 200) {
                            murabahApplication.setAdminFeeCharged(adminChargeRequest.getAmount());
                            lsfRepository.updateMurabahApplication(murabahApplication);
                            log.info("===========LSF : Admin Fee Successfully updated Application " + murabahApplication.getCustomerId() + " Customer id " + murabahApplication.getCustomerId() + " Cash Account " + murabahApplication.getCashAccount());
                            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARGED);
                            log.info("===========LSF : Admin Fee Successfully Charged , CustomerID" + murabahApplication.getCustomerId() + " , Application ID :" + murabahApplication.getId() + " , Account ID:" + murabahApplication.getCashAccount() + " , Total Amount:" + (adminFee +vatAmount) + " ,Vat Amount:" + vatAmount);
                            //update admin fee in PO level,just to keep the record
                            lsfRepository.updateAdminFee(GlobalParameters.getInstance().getSimaCharges(),GlobalParameters.getInstance().getTransferCharges(),vatAmount,purchaseOrder.getId());
                            CommonResponse shareTransferResponse = null;
                            shareTransferResponse = (CommonResponse) lsfCore.transferToLsfAccount(murabahApplication.getId(), purchaseOrder.getId());/*---transferring po symbols to lsf account--*/
                            if (shareTransferResponse.getResponseCode() == 1) {
                                log.debug("===========LSF : Transferred PO symbols  :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId() + "Status :" + shareTransferResponse);
                                log.debug("===========LSF : Transferring Collaterals  to LSF type Account :" + purchaseOrder.getId());
                                lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT);
                                response = (CommonResponse) lsfCore.transferCollaterals(collaterals); /*----Transferring Application Collaterals to LSF Account-----*/

                                if (response.getResponseCode() == 200) {
                                    log.debug("===========LSF : Collateral transfer  to LSF type Account Request Sent to OMS :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId() + "Status :" + response);

                                } else {
                                    log.debug("===========LSF: Failed To Send Collateral Transfer to OMS");
                                    lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS);
                                }

                                try {
                                    notificationManager.sendNotification(murabahApplication);
                                } catch (Exception e) {  
                                    log.error("Error sending notification", e);
                                }

                            } else {
                                lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS);
                                log.debug("===========LSF: Collateral & PO Symbol Transfer Failed.");
                            }


                        } else {
                            lsfRepository.updateActivity(murabahApplication.getId(), LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARG_FAILED);
                            log.info("===========LSF : Admin Fee Request Failed, ApplicationID :" + murabahApplication.getId() + ", Account ID :" + murabahApplication.getCashAccount());
                        }
                        // this is moved to end as order application should be moved to next stage after all above is completed
                        lsfRepository.updateCustomerOrderStatus(purchaseOrder.getId(), "1", statusComment, statusChangedIP);
                        log.debug("===========LSF : Order and Application  Status Changed Successfully, Order ID :" + purchaseOrder.getId() + " , Application ID :" + murabahApplication.getId());
                    }

                } else if (approveStatus !=null && approveStatus.equalsIgnoreCase("-1")) { /*---If Client Reject the Agreement---*/
                    log.debug("===========LSF :Order Contract Rejected, Order Contract Rejected, Release Collateral from OMS :" + orderId + " , Application ID :" + applicationID + "Status :" + response.getResponseCode());
                    response = (CommonResponse) lsfCore.releaseCollaterals(collaterals); /*-----Release Blocked Collaterals------*/
                    liquidateResponse = (CommonResponse) lsfCore.liquidate(orderId);/*---liquidating customer symbols---*/
                    if (liquidateResponse.getResponseCode() == 1) {
                        log.debug("===========LSF : Symbol Liquidation Successful , Order ID :" + orderId + " , Application ID :" + applicationID);
                        orderStatusChangeResponse = lsfRepository.updateCustomerOrderStatus(orderId, approveStatus, statusComment, statusChangedIP);//updating user & order status
                        if (orderStatusChangeResponse.equalsIgnoreCase("1")) {
                            log.debug("===========LSF : Order and Application  Status Changed Successfully, Order ID :" + orderId + " , Application ID :" + applicationID + " Approve State :" + approveStatus);
                            lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ORDER_CONTRACT_REJECTED);
                        }

                        if(collaterals.isExchangeAccountCreated()){
                            TradingAcc lsfTradingAcc = lsfCore.getLsfTypeTradinAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());
                            CashAcc  lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(murabahApplication.getCustomerId(),murabahApplication.getId());

                            AccountDeletionRequestState accountDeletionRequestState = lsfCore.closeLSFAccount(applicationID, lsfTradingAcc.getAccountId(), murabahApplication.getTradingAcc(), lsfCashAccount.getAccountId(), murabahApplication.getCashAccount());

                            if (accountDeletionRequestState.isSent()) {
                                log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request Sent to OMS, Application ID :" + applicationID);
                            }
                        }else{
                            log.info("===========LSF :(customerRejectOrderContract)- Account Deletion Request not Sent as Exchange Account has not been created.");
                        }

                    } else {
                        log.error("===========LSF : Liquidation Failed , Order ID :" + orderId + " , Application ID :" + applicationID);
                        responseCode = 500;
                        responseMessage = "Failure while liquidating symbols. ";
                    }
                    /*----- sending the Notification----*/
                    try {
                        if (orderStatusChangeResponse.equalsIgnoreCase("1")) {
                            MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);
                            notificationManager.sendNotification(application);
                        }
                    } catch (Exception ex) {
                        log.error("Error sending notification", ex);
                    }
                }

                response.setResponseCode(responseCode);
                response.setResponseMessage(responseMessage);
                response.setErrorMessage(responseMessage);
                log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                return gson.toJson(response);
            } else {
                response.setResponseCode(500);
                response.setErrorMessage("Signing/Rejecting Order Agreement is not allowed at the moment due to market unavailability");
                response.setErrorCode(LsfConstants.ERROR_SIGNING_ORDER_AGREEMENT_IS_NOT_ALLOWED_MARCCKET_CLOSED);
                log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                return gson.toJson(response);
            }

        } else {
            response.setResponseCode(500);
            response.setErrorMessage("Order has been liquidated due to purchase order not acceptance or Already Approved");
            response.setErrorCode(LsfConstants.ERROR_ORDER_HAS_BEEN_lIQUIDATED_DUE_TO_NOT_ACCEPTANCE);
            log.debug("===========LSF : (reqApproveOrderAgreement)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
            return gson.toJson(response);
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
                    MApplicationCollaterals response = lsfCore.reValuationProcess(application,true);
                    log.info("===========LSF : (reqDashBoardPFSummary)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
                    getBPDetails(response, applicationID);
                    return gson.toJson(response);
                } else {
                    return null;
                }
            } else {
                return null;
            }

        }

    }

    private MApplicationCollaterals getBPDetails(MApplicationCollaterals collaterals, String applicationID) { // calculating buyingPower for each marginability
        MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);
        MarginabilityGroup marginabilityGroup = helper.getMarginabilityGroup(application.getMarginabilityGroup());
        List<BPSummary> bpList = new ArrayList<>();
        List<LiquidityType> attachedLiqGoupList = null;
        if (marginabilityGroup != null) {
            attachedLiqGoupList = marginabilityGroup.getMarginabilityList();
            for (LiquidityType liquidityType : attachedLiqGoupList) {
                BPSummary bfSummary = calculateBP(liquidityType, collaterals.getTotalCashColleteral(), collaterals.getTotalPFColleteral(), collaterals.getOutstandingAmount());
                bpList.add(bfSummary);
            }
        }
        if (bpList.size() > 0) {
            collaterals.setBuyingPowerSummary(bpList);
        }
        return collaterals;
    }

    private static BPSummary calculateBP(LiquidityType liquidityType, double totalCash, double totalPF, double totalOutstanding) {
        BPSummary BPSummary = new BPSummary();
        BPSummary.setMarginabilityType(liquidityType.getLiquidName());
        double marginabilityDifference = 1 - (liquidityType.getMarginabilityPercent() / 100);
        if (marginabilityDifference == 1.0) {
            double bf = (totalPF + totalCash) - (GlobalParameters.getInstance().getFirstMarginCall() / 100) * totalOutstanding;
            if (bf > totalCash) {
                BPSummary.setBuyingPower(BigDecimal.valueOf(totalCash));
            } else {
                BPSummary.setBuyingPower(bf > 0 ? BigDecimal.valueOf(bf) : BigDecimal.ZERO);
            }

        } else {
            double sum = totalCash + totalPF;
            double buyingPower = (sum - (GlobalParameters.getInstance().getFirstMarginCall() / 100) * totalOutstanding) / marginabilityDifference;
            if (buyingPower > 0) {
                if (buyingPower > totalCash) {
                    BPSummary.setBuyingPower(BigDecimal.valueOf(totalCash));

                } else {
                    BPSummary.setBuyingPower(buyingPower > 0 ? BigDecimal.valueOf(buyingPower) : BigDecimal.ZERO);
                }
            } else {
                BPSummary.setBuyingPower(BigDecimal.ZERO);
            }

        }
        log.info("===========LSF : Calculating Buying Power, Total Cash :" + totalCash + " , Total PF :" + totalCash + " , OutStanding :" + totalOutstanding + ", Margin Type :" + liquidityType.getLiquidName() + " , percentage :" + liquidityType.getMarginabilityPercent() + " BP:" + BPSummary.getBuyingPower());

        return BPSummary;
    }
    private String updatePurchaseOrdByAdmin(Map<String, Object> map){
        log.info("updatePurchaseOrdByAdmin :" +map.get("id").toString());
        CommonResponse cmr = new CommonResponse();
        try {
            PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(map.get("id").toString());
            po.setSellButNotSettle((int) Double.parseDouble(map.get("sellButNotSettle").toString()));
            po.setIsPhysicalDelivery((int) Double.parseDouble(map.get("isPhysicalDelivery").toString()));
//            List<Commodity> commodityList = gson.fromJson(map.get("commodityList"),)
            List<Commodity> commodityList = (List<Commodity>) map.get("commodityList");
            List<Commodity> commodities = new ArrayList<>();
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
                        commodities.add(commodity);

                    }
                }
            }
            po.setCommodityList(commodities);
            lsfRepository.updatePurchaseOrderByAdmin(po);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Successfully Updated the Purchase order by admin");
            log.info("Successfully Updated the Purchase order by admin");
        }catch (Exception e){
            cmr.setErrorCode(500);
            cmr.setErrorMessage("PO Update Failed");
            log.error("PO Update Failed", e);
        }

        return gson.toJson(cmr);
    }
    private String commodityPOExecution(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        try {
            PurchaseOrder po = new PurchaseOrder();
            po.setId(map.get("id").toString());
            po.setOrderStatus((int) Double.parseDouble(map.get("status").toString()));// filled
            po.setCertificatePath(map.get("certificatePath").toString());
            String key = lsfRepository.updateCommodityPOExecution(po);

            po = lsfRepository.getSinglePurchaseOrder(po.getId());


            if(map.containsKey("ipAddress")){
                String statusChangedIP = map.get("ipAddress").toString();
                    if(LSFUtils.validateAdminApproveAction(statusChangedIP)){ /*---Check the time interval of Admin approvals for the particular ip---*/
                        List<MurabahApplication> applicationList = null;
                        try {
                            applicationList = lsfRepository.getMurabahAppicationApplicationID(po.getApplicationId());
                            if (applicationList.size() > 0) {
                                MurabahApplication fromDB = applicationList.get(0);
                                if (fromDB != null) {
                                    int appStatus = 1;
                                    int currentLevel= 14;
                                    if(currentLevel!=fromDB.getCurrentLevel()){
                                        cmr.setResponseCode(500);
                                        cmr.setErrorMessage("Application is already approved");
                                        log.info("===========LSF : (commodityPOExecute) RESPONSE : application level update failed :" + fromDB.getId() + " ,current Level :" + currentLevel + " Already approved.");
                                        return gson.toJson(cmr);
                                    }
                                    String statusMessage = "Purchase order submited by ADMIN";
                                    String statusChangedUserid = "Admin";
                                    String statusChangedUserName = "Admin";
                                    String responseMessage = "";

                                    responseMessage = lsfRepository.approveApplication(appStatus, fromDB.getId(), statusMessage, statusChangedUserid, statusChangedUserName, statusChangedIP);
                                    if (appStatus < 0 && appStatus != -1) { /*---if the application is permanent reject---*/
                                        CommonInqueryMessage blackListRequest = new CommonInqueryMessage();
                                        blackListRequest.setReqType(LsfConstants.BLACK_LIST_CUSTOMER);
                                        blackListRequest.setCustomerId(fromDB.getCustomerId());
                                        blackListRequest.setChangeParameter(1);
                                        blackListRequest.setValue("1");
                                        blackListRequest.setParams("Customer need to be Black Listed");
                                        log.info("===========LSF : Sending Black List Request to OMS:" + gson.toJson(blackListRequest));
                                        String omsResponse = helper.omsCommonRequests(gson.toJson(blackListRequest));
                                        log.info("===========LSF : OMS Response to  Black List Request :" + omsResponse);

                                    }

                                    notificationManager.sendNotification(fromDB);/*---Sending Notification---*/
                                    cmr.setResponseCode(200);
                                    cmr.setResponseMessage(responseMessage); /*---currentLevel|approveState---*/
                                }
                            } else {
                                cmr.setResponseCode(500);
                                cmr.setErrorMessage(ErrorCodes.ERROR_AUTHORIZATION_CP.errorDescription());
                            }
                        } catch (Exception ex) {
                            cmr.setResponseCode(500);
                            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
                            log.error("Error sending notification", ex);
                        }
                        log.info("===========LSF : (commodityPOExecute)LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
                        return gson.toJson(cmr);
                    }else{
                        cmr.setResponseCode(500);
                        cmr.setResponseMessage("Abnormal Activity");
                        cmr.setErrorMessage("Abnormal Activity");
                        cmr.setErrorCode(LsfConstants.ERROR_ABNORMAL_ACTIVITY);
                        log.info("===========LSF : (commodityPOExecute)LSF-SERVER RESPONSE  :" + gson.toJson(cmr) );

                        return gson.toJson(cmr);
                    }


                }else{
                    cmr.setResponseCode(500);
                    cmr.setResponseMessage("IP Address is not detected");
                    cmr.setErrorMessage("IP Address is not detected");
                    cmr.setErrorCode(LsfConstants.ERROR_IP_ADDRESS_IS_NOT_DETECTED);
                    log.info("===========LSF : (commodityPOExecute) PO Execution Response  :" + gson.toJson(cmr));
                    return gson.toJson(cmr);
                }

            } catch (Exception e){
            cmr.setErrorCode(500);
            cmr.setErrorMessage("PO Execution Failed");
            log.error("PO Execution Failed", e);
        }

        return gson.toJson(cmr);
    }
    private String updateAuthAbicToSell(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        PurchaseOrder po = new PurchaseOrder();
        po.setId(map.get("poid").toString());
        po.setAuthAbicToSell((int) Double.parseDouble(map.get("authAbicToSell").toString()));
        if (po.getAuthAbicToSell() == 0){
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
    private String revertPOToSellorDeliver(Map<String, Object> map){
        CommonResponse cmr = new CommonResponse();
        PurchaseOrder po = new PurchaseOrder();
        po.setId(map.get("poid").toString());
        po.setAuthAbicToSell((int) Double.parseDouble(map.get("authAbicToSell").toString()));
        if (po.getAuthAbicToSell() == 0){
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