package com.dfn.lsf.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.integration.IntegrationService;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class Helper {
    private static final transient Logger logger = LoggerFactory.getLogger(Helper.class);
    private static final Gson gson = new Gson();

    private final LSFRepository lsfRepository;
    private final IntegrationService integrationService;

    @Value("${integration.notification.third.party.sms.queue}")
    private String thirdPartySmsQueue;

    @Value("${integration.notification.third.party.email.queue}")
    private String thirdPartyEmailQueue;

    public String getCustomerRelatedOMSData(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.getCustomerRelatedOmsData(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String cashAccountRelatedOMS(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.getCashAccountRelatedOmsData(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String portfolioRelatedOMS(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.getPortfolioRelatedOmsData(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String symbolRelatedOMS(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.getSymbolRelatedOmsData(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String validateSSO(String request) {
        String response = null;
        try {
            logger.info("===========LSF : OMS REQUEST" + request);
            Object result = integrationService.validateSso(request);
            response = result.toString();
        } catch (Exception ex) {
            logger.error("Error validating SSO", ex);
        }
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String orderRelatedOMS(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.sendOrderRelatedOmsRequest(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String b2bRelatedOMS(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.sendB2bRelatedOmsRequest(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String omsCommonRequests(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.sendOmsCommonRequest(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public String OMSValidationResponseRelated(String message) {
        logger.info("===========LSF : OMS REQUEST" + message);
        String response = integrationService.sendOmsValidationRequest(message);
        logger.info("===========LSF : OMS RESPONSE" + response);
        return response;
    }

    public boolean sendToSMSProducer(String message) {
        try {
            QueMsgDto queMsgDto = new QueMsgDto();
            queMsgDto.setQueueName(thirdPartySmsQueue);
            queMsgDto.setMessage(message);
            integrationService.sendSmsNotification(queMsgDto);
            return true;
        } catch (Exception e) {
            logger.error("Error sending SMS notification", e);
            return false;
        }
    }

    public boolean sendToEmailProducer(String message) {
        try {
            QueMsgDto queMsgDto = new QueMsgDto();
            queMsgDto.setQueueName(thirdPartyEmailQueue);
            queMsgDto.setMessage(message);
            integrationService.sendEmailNotification(queMsgDto);
            return true;
        } catch (Exception e) {
            logger.error("Error sending email notification", e);
            return false;
        }
    }


    public Object sendMessageToIflex(String message) {
        try {
            return integrationService.sendMessageToIflex(message);
        } catch (Exception e) {
            logger.error("Error sending message to iFlex", e);
            return "";
        }
    }

    public Object sendMessageToOms(String message) {
        try {
            logger.info("===========LSF : OMS REQUEST" + message);
            String response  = integrationService.sendMessageToOms(message);
            logger.info("===========LSF : OMS RESPONSE" + response);
            return  response;
        } catch (Exception ex) {
            logger.error("Error sending message to OMS", ex);
            return "";
        }
    }



    public Object sendMessageToOmsToGetTradingAcc(String message) {
        try {
            return integrationService.sendMessageToOmsForTradingAccount(message);
        } catch (Exception ex) {
            logger.error("Error sending message to OMS to get trading account", ex);
            return ex;
        }
    }

    public Object sendSettlementRelatedOMSRequest(String message, String producerName) {
        logger.info("===========LSF : OMS REQUEST" + message);
        try {
            return integrationService.sendSettlementRelatedOmsRequest(message, producerName);

        } catch (Exception ex) {
            logger.error("Error sending settlement related OMS request", ex);
            return ex;
        }
    }

    public CommonResponse processOMSCommonResponse(String request) {
        logger.info("===========LSF : OMS REQUEST" + request);
        CommonResponse cmr = new CommonResponse();
        if (request != null && !request.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(request, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            logger.info("===========LSF : OMS RESPONSE" + s);
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray.length > 0) {
                if (resultArray[0].equals("1")) {
                    cmr.setResponseCode(200);
                    if (resultArray.length > 1) {
                        cmr.setResponseMessage(resultArray[1]);
                    }
                } else {
                    cmr.setResponseCode(500);
                    if (resultArray.length > 1) {
                        cmr.setErrorMessage(resultArray[1]);

                    }
                }
            }

            return cmr;
        } else {
            cmr.setResponseCode(500);
            return cmr;
        }

    }


    public CommonResponse processOMSCommonResponseAdminFee(String request) {
        logger.info("===========LSF : OMS REQUEST" + request);
        CommonResponse cmr = new CommonResponse();
        if (request != null && !request.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(request, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            logger.info("===========LSF : OMS RESPONSE" + s);
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray[0].equals("1")) {
                cmr.setResponseCode(200);
                cmr.setResponseMessage(resultArray[1]);
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage(resultArray[1]);
            }
            return cmr;
        } else {
            cmr.setResponseCode(500);
            return cmr;
        }

    }

    public CommonResponse processOMSCommonResponseAccountCreation(String request) {
        logger.info("===========LSF : OMS Response" + request);
        CommonResponse cmr = new CommonResponse();
        if (request != null && !request.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(request, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray.length > 0) {
                cmr.setResponseCode(Integer.parseInt(resultArray[0]));
            }

            return cmr;
        } else {
            cmr.setResponseCode(500);
            return cmr;
        }

    }

    public CommonResponse processOMSCommonResponseAccountDeletionRequest(String request) {
        logger.info("===========LSF : OMS Response" + request);
        CommonResponse cmr = new CommonResponse();
        if (request != null && !request.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(request, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray.length > 0) {
                cmr.setResponseCode(Integer.parseInt(resultArray[0]));
            }
            return cmr;
        } else {
            cmr.setResponseCode(-3);
            return cmr;
        }

    }


    public LiquidityType existingSymbolLiqudityType(String symbolCode, String exchange) {
        LiquidityType defLiq = new LiquidityType();
        defLiq.setLiquidId(1);
        defLiq.setLiquidName("Liquid");
        defLiq.setMarginabilityPercent(100.0);
        try {
            Symbol symbol = new Symbol();
            symbol.setSymbolCode(symbolCode);
            symbol.setExchange(exchange);
            return lsfRepository.getSymbolLiquidityType(symbol);
        } catch (Exception ex) {
            logger.error("Error getting symbol liquidity type", ex);
            return defLiq;
        }
    }

    public double getSymbolMarginabilityPerc(String symbolCode, String exchange, String appId) {
        try {
            return lsfRepository.getSymbolMarginabilityPerc(symbolCode, exchange, appId);
        } catch (Exception e) {
            logger.error("Error getting symbol marginability percentage", e);
            return 0;
        }
    }

    public MarginabilityGroup getMarginabilityGroup(String mgroupId) {
        try {

            MarginabilityGroup marginabilityGroup = lsfRepository.getMarginabilityGroup(mgroupId);
            if (marginabilityGroup != null) {
                marginabilityGroup.setMarginabilityList(lsfRepository.getMarginabilityGroupLiquidTypes(marginabilityGroup.getId()));
                marginabilityGroup.setMarginableSymbols(lsfRepository.getMarginabilityPercByGroup(marginabilityGroup.getId()));
            }
            return marginabilityGroup;

        } catch (Exception ex) {
            logger.error("Error getting marginability group", ex);
            return null;
        }
    }


    public String handleOMSRequest(String request, String producerName) {
        return integrationService.sendMessageToOms(request);
    }

    public String acknowledgeReportGeneration(String request, String producerName) {
        try {
            return integrationService.acknowledgeReportGeneration(request, producerName);
        } catch (Exception ex) {
            logger.error("Error acknowledging report generation", ex);
            return null;
        }        
    }

    public boolean sendB2BRequest(String request, String producerName) {
        try {
            return integrationService.sendB2bRequest(request, producerName);
        } catch (Exception ex) {
            logger.error("Error sending B2B request", ex);
            return false;
        }
    }

    public CommonResponse cancelMLPendingBaskets(String request) {
        logger.info("===========LSF : OMS REQUEST" + request);
        CommonResponse pendingBasketCancelResponse = new CommonResponse();
        pendingBasketCancelResponse.setResponseCode(500);
        try {
            return integrationService.cancelMlPendingBaskets(request);
        } catch (Exception ex) {
            logger.error("Error canceling ML pending baskets", ex);
            return pendingBasketCancelResponse;
        }
    }
    @Cacheable(value = "commonCacheOneMinute", key = "#customerId", unless = "#result == null")
    public List<CashAcc> getNonLsfTypeCashAccounts(String customerId) {
        List<CashAcc> cashAccounts = new ArrayList<>();
        try {
            CommonInqueryMessage request = new CommonInqueryMessage();
            request.setReqType(LsfConstants.GET_NON_LSF_CASH_ACCOUNT_DETAILS);
            request.setCustomerId(customerId);

            String result = sendMessageToOms(gson.toJson(request)).toString();
            Map<String, Object> resultMap = gson.fromJson(result, HashMap.class);

            processCashAccountOMSResponse(resultMap, cashAccounts);
        } catch (Exception e) {
            logger.error("Error getting LSF type cash account for customer id : {}", customerId, e);
        }
        return cashAccounts;
    }

    @Cacheable(value = "commonCacheOneMinute", key = "#customerId + '_' + #applicationId", unless = "#result == null")
    public List<CashAcc> getLsfTypeCashAccounts(String customerId, String applicationId) {
        List<CashAcc> cashAccounts = new ArrayList<>();
        try {
            CommonInqueryMessage request = new CommonInqueryMessage();
            request.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
            request.setCustomerId(customerId);
            request.setContractId(applicationId);

            String result = sendMessageToOms(gson.toJson(request)).toString();
            Map<String, Object> resultMap = gson.fromJson(result, HashMap.class);

            processCashAccountOMSResponse(resultMap, cashAccounts);
        } catch (Exception e) {
            logger.error("Error getting LSF type cash account for customer id : {}, Application id : {}", customerId, applicationId, e);
        }
        return cashAccounts;
    }

    private static void processCashAccountOMSResponse(final Map<String, Object> resultMap, final List<CashAcc> cashAccounts) {
        List<Map<String, Object>> lsfcash = (List<Map<String, Object>>) resultMap.get("responseObject");
        if (lsfcash != null) {
            for (Map<String, Object> cash : lsfcash) {
                CashAcc cashAcc = CashAcc.builder()
                                         .accountId(cash.get("accountNo").toString())
                                         .cashBalance(Double.parseDouble(cash.get("balance").toString()))
                                         .blockedAmount(Double.parseDouble(cash.get("blockedAmount").toString()))
                                         .pendingSettle(Double.parseDouble(cash.get("pendingSettle").toString()))
                                         .netReceivable(Double.parseDouble(cash.get("netReceivable").toString()))
                                         .isLsfType(Boolean.parseBoolean(cash.get("isLsf").toString()))
                                         .build();
                if(cash.get("investorAccountNo") != null) {
                    cashAcc.setInvestmentAccountNumber(cash.get("investorAccountNo").toString());
                }
                cashAccounts.add(cashAcc);
            }
        }
    }

    @Cacheable(value = "commonCacheOneMinute", key = "#customerId", unless = "#result == null")
    public List<TradingAccOmsResp> getNonLsfTypeTradingAccounts(String customerId) {
        CommonInqueryMessage trReq = new CommonInqueryMessage();
        trReq.setReqType(LsfConstants.GET_TRADING_ACCOUNT_LIST);
        trReq.setCustomerId(customerId);
        logger.info("===========LSF(reqTradingAccList): REQUEST , customerID" + trReq.getCustomerId());

        String resMap = portfolioRelatedOMS(gson.toJson(trReq));
        Type listType = new TypeToken<List<TradingAccOmsResp>>() {}.getType();
        Map<String, Object> resultMap = gson.fromJson(resMap, HashMap.class);
        List<TradingAccOmsResp> tradingAccList = gson.fromJson(gson.toJson(resultMap.get("responseObject")), listType);
        logger.info("===========LSF (reqTradingAccList): LSF-SERVER RESPONSE  :"
                    + gson.toJson(tradingAccList)
                    + ", customerID :"
                    + trReq.getCustomerId());
        return tradingAccList;
    }

    @Cacheable(value = "commonCacheOneMinute", key = "#customerId + '_' + #applicationId + '_' + #marginalabilityGroupId", unless = "#result == null")
    public List<TradingAccOmsResp> getLsfTypeTradingAccounts(String customerId, String applicationId, String marginalabilityGroupId) {
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        inqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
        inqueryMessage.setCustomerId(customerId);
        inqueryMessage.setContractId(applicationId);
        Object result = sendMessageToOms(gson.toJson(inqueryMessage));
        Map<String, Object> resultMap = gson.fromJson((String) result, HashMap.class);

        List<Map<String, Object>> accList = (List<Map<String, Object>>) resultMap.get("responseObject");
        List<TradingAccOmsResp> respList = new ArrayList<>();
        if (accList != null) {
            for (Map<String, Object> resMap : accList) {
                TradingAccOmsResp tradingAcc = extractTradingAccount(resMap);
                if (tradingAcc != null && resMap.containsKey("shariaSymbols")) {
                    List<Map<String, Object>> symbolsList = (List<Map<String, Object>>) resMap.get("shariaSymbols");
                    processSymbols(tradingAcc, symbolsList, marginalabilityGroupId, true);
                }
                respList.add(tradingAcc);
            }
        }
        return respList;
    }

    public List<TradingAccOmsResp> getPFDetailsNonLSF(String customerId, String marginalabilityGroupId) {
        CommonInqueryMessage req = new CommonInqueryMessage();
        req.setReqType(GlobalParameters.getInstance().getShariaSymbolsAsCollateral()
                       ? LsfConstants.GET_PF_SYMBOLS_FOR_COLLETRALS
                       : LsfConstants.GET_NON_SHARIA_PF_SYMBOLS_FOR_COLLETRALS);
        req.setCustomerId(customerId);

        try {
            String result = (String) sendMessageToOms(gson.toJson(req));
            Map<String, Object> resultMap = gson.fromJson(result, HashMap.class);
            List<Map<String, Object>> accList = (List<Map<String, Object>>) resultMap.get("responseObject");
            List<TradingAccOmsResp> respList = new ArrayList<>();
            if (accList != null) {
                for (Map<String, Object> resMap : accList) {
                    TradingAccOmsResp tradingAcc = extractTradingAccount(resMap);
                    if (tradingAcc != null && resMap.containsKey("shariaSymbols")) {
                        List<Map<String, Object>> symbolsList = (List<Map<String, Object>>) resMap.get("shariaSymbols");
                        processSymbols(tradingAcc, symbolsList, marginalabilityGroupId, false);
                    }
                    respList.add(tradingAcc);
                }
            }
            return respList;
        } catch (Exception e) {
            logger.error("Error processing PF details for customerId: {}", customerId, e);
        }
        return new ArrayList<>();
    }

    private TradingAccOmsResp extractTradingAccount(Map<String, Object> resMap) {
        try {
            Map<String, Object> tradingAccMap = (Map<String, Object>) resMap.get("tradingAccount");
            TradingAccOmsResp tradingAcc = new TradingAccOmsResp();
            tradingAcc.setAccountId(tradingAccMap.get("accountId").toString());
            tradingAcc.setExchange(tradingAccMap.get("exchange").toString());
            tradingAcc.setLsf(Boolean.parseBoolean(tradingAccMap.get("isLsf").toString()));
            tradingAcc.setSymbolList(new ArrayList<>());
            return tradingAcc;
        } catch (Exception e) {
            logger.error("Error extracting trading account", e);
            return null;
        }
    }

    private void processSymbols(TradingAccOmsResp tradingAcc, List<Map<String, Object>> symbolsList, String marginabilityGroupId, boolean isLsfType) {
        MarginabilityGroup marginabilityGroup = getMarginabilityGroup(marginabilityGroupId);
        List<LiquidityType> attachedLiqGoupList = null;
        List<SymbolMarginabilityPercentage> symbolMarginabilityPercentages = null;
        if (marginabilityGroup != null) {
            attachedLiqGoupList = marginabilityGroup.getMarginabilityList();
            symbolMarginabilityPercentages = marginabilityGroup.getMarginableSymbols();
        }

        if (symbolsList != null) {
            for (Map<String, Object> symbolObj : symbolsList) {
                Symbol symbol = new Symbol();
                symbol.setSymbolCode(symbolObj.get("symbolCode").toString());
                symbol.setExchange(symbolObj.get("exchange").toString());
                symbol.setShortDescription((String) symbolObj.getOrDefault("shortDescription", ""));
                symbol.setPreviousClosed(Double.parseDouble(symbolObj.get("previousClosed").toString()));
                symbol.setLastTradePrice(Double.parseDouble(symbolObj.get("lastTradePrice").toString()));

                if(symbolObj.containsKey("openBuyQty")){
                    symbol.setOpenBuyQty(Integer.parseInt(symbolObj.get("openBuyQty").toString().split("\\.")[0]));
                }
                if(symbolObj.containsKey("openSellQty")){
                    symbol.setOpenBuyQty(Integer.parseInt(symbolObj.get("openSellQty").toString().split("\\.")[0]));
                }


                int pendingSettle = Math.round(Float.parseFloat(symbolObj.getOrDefault("pendingSettle", "0").toString()));
                int sellPending = Math.round(Float.parseFloat(symbolObj.getOrDefault("sellPending", "0").toString()));
                symbol.setAvailableQty(Math.round(Float.parseFloat(symbolObj.get("availableQty").toString())) - pendingSettle + sellPending);
                symbol.setMarketValue(symbol.getAvailableQty() * Math.max(symbol.getLastTradePrice(), symbol.getPreviousClosed()));

                LiquidityType attachedToSymbolLiq = existingSymbolLiqudityType(symbol.getSymbolCode(), symbol.getExchange());
                symbol.setLiquidityType(attachedToSymbolLiq);
                if (isLsfType) {
                    symbol.setTransferedQty(symbol.getAvailableQty());
                }

                if (attachedLiqGoupList != null) {
                    attachedLiqGoupList.stream().filter(liquidityType -> liquidityType.getLiquidId() == attachedToSymbolLiq.getLiquidId())
                        .findFirst()
                        .ifPresent(symbol::setLiquidityType);
                }

                if (marginabilityGroup != null) {
                    symbol.setMarginabilityPercentage(marginabilityGroup.getGlobalMarginablePercentage());
                }

                if(symbolMarginabilityPercentages != null) {
                    for(SymbolMarginabilityPercentage smp :symbolMarginabilityPercentages) {
                        if(smp.getSymbolCode().equals(symbol.getSymbolCode()) && smp.getExchange().equals(symbol.getExchange())){
                            symbol.setMarginabilityPercentage(smp.getMarginabilityPercentage());
                        }
                    }
                }

                tradingAcc.getSymbolList().add(symbol);
            }
        }
    }

    public TradingAccOmsResp getTradingAccount(String customerId, String tradingAccId) {
        List<TradingAccOmsResp> tradingAccList = getNonLsfTypeTradingAccounts(customerId);
        if (tradingAccList != null) {
            return tradingAccList.stream()
                .filter(tradingAcc -> tradingAcc.getAccountId().equals(tradingAccId))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    public String getCashTransferFromAccount(MurabahApplication application) {
        if (application.isRollOverApp()) {
            // load lsf cash account from Original application
            var cashAccounts = getLsfTypeCashAccounts(application.getRollOverAppId(), application.getId());
            if (cashAccounts != null && !cashAccounts.isEmpty()) {
                return cashAccounts.getFirst().getAccountId();
            }
        }
        return application.getCashAccount();
    }
}
