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

    @Cacheable(value = "commonCacheOneMinute", key = "#customerId + '_' + #applicationId", unless = "#result == null")
   public List<CashAcc> getLsfTypeCashAccountForApp(String customerId, String applicationId) {
        List<CashAcc> cashAccounts = new ArrayList<>();
        try {
            CommonInqueryMessage request = new CommonInqueryMessage();
            request.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
            request.setCustomerId(customerId);
            request.setContractId(applicationId);

            String result = sendMessageToOms(gson.toJson(request)).toString();
            Map<String, Object> resultMap = gson.fromJson(result, HashMap.class);

            List<Map<String, Object>> lsfcash = (List<Map<String, Object>>) resultMap.get("responseObject");
            if (lsfcash != null) {
                for (Map<String, Object> cash : lsfcash) {
                    CashAcc cashAcc = CashAcc.builder()
                            .accountId(cash.get("accountNo").toString())
                            .cashBalance(Double.parseDouble(cash.get("balance").toString()))
                            .blockedAmount(Double.parseDouble(cash.get("blockedAmount").toString()))
                            .pendingSettle(Double.parseDouble(cash.get("pendingSettle").toString()))
                            .netReceivable(Double.parseDouble(cash.get("netReceivable").toString()))
                            .isLsfType(true)
                            .build();
                    cashAccounts.add(cashAcc);
                }
            }
        } catch (Exception e) {
            logger.error("Error getting LSF type cash account for customer id : {}, Application id : {}", customerId, applicationId, e);
        }
        return cashAccounts;
    }

    @Cacheable(value = "commonCacheOneMinute", key = "#customerId", unless = "#result == null")
    public List<TradingAccOmsResp> getTradingAccountList(String customerId) {
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

    public TradingAccOmsResp getTradingAccount(String customerId, String tradingAccId) {
        List<TradingAccOmsResp> tradingAccList = getTradingAccountList(customerId);
        if (tradingAccList != null) {
            return tradingAccList.stream()
                .filter(tradingAcc -> tradingAcc.getAccountId().equals(tradingAccId))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

}
