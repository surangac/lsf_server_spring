package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defined in InMessageHandlerAdminCbr, InMessageHandlerCbr
 * route : EXCHANGE_SYMBOLS
 * Handling Message types :
 * - MESSAGE_TYPE_EXCHANGE_SYMBOL_PROCESS = 6;
 */
@Service
@RequiredArgsConstructor
@Qualifier("6")
public class ExchangeSymbolProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeSymbolProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = new CommonResponse();
        map = gson.fromJson(request, map.getClass());
        try {
            String actionType = map.get("subMessageType").toString();
            //String[] exchangeList = new String[]{ "ADSM", "DFM", "TDWL"};
            List<String> exchangeList = new ArrayList<>();
            String defaultExchange = GlobalParameters.getInstance().getDefaultExchange();
            exchangeList.add(defaultExchange);
            switch (actionType) {
                case LsfConstants.LOAD_INIT_DATA: /*-----------Sync All the Exchange Symbols-----------*/
                    for (String exchange : exchangeList) {
                        syncSymbols(exchange);
                        cmr.setResponseCode(200);
                        cmr.setResponseMessage("Symbols Loaded");
                    }
                    return gson.toJson(cmr);
                case LsfConstants.UPDATE_LIQUIDITY_TYPE:/*-----------Update Liquidity type of Symbols-----------*/
                    return updateLiquidityType(request);
                case LsfConstants.SEARCH_SYMBOL:/*-----------Search Symbols-----------*/
                    return loadSymbols(map);
                case LsfConstants.GET_ALL_SYMBOLS:
                    return getAllSymbols();
                case LsfConstants.GET_SYMBOL_MARGINABILITY_GROUPS:
                    return getSymbolMarginabilityGroups(map);
                case LsfConstants.ADD_TO_WISH_LIST:/*---Add Symbols for Investment Offer---*/
                    return addToWishList(request);
                case LsfConstants.LOAD_SYMBOL_WISH_LIST:/*--Load Symbol Wish list--*/
                    return loadSymbolWishList(map);
                case LsfConstants.UPDATE_SYMBOL_CLASSIFY_LOG_STATUS:/*--Load Symbol Wish list--*/
                    return updateSymbolClassifyLogStatus(map);
                case LsfConstants.GET_SYMBOL_CLASSIFY_LOG:/*--Load Symbol Wish list--*/
                    return getSymbolClassifyLog(map);
                case LsfConstants.GET_STOCK_CLASSIFICATION_DATA:/*---------Load Stock Classification Data----------*/
                    return getSymbolClassificationData(map);
                case LsfConstants.GET_SYMBOL_MARGINABILITY_DATA:
                    return getSymbolMarginabilityData(map);
                case LsfConstants.GET_SYMBOL_INSTRUMENTS_LIST:/*---------Load Stock Classification Data----------*/
                    return getSymbolInstrumentList();
            }
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        return gson.toJson(cmr);
    }

    private void syncSymbols(String exchangeCode) {
        try {
            CommonInqueryMessage slistReq = new CommonInqueryMessage();
            slistReq.setExchange(exchangeCode);
            slistReq.setReqType(LsfConstants.SYNC_SYMBOLS);
            String result = helper.symbolRelatedOMS(gson.toJson(slistReq));
            if (result != null) {

                SymbolListResponse symbolListResponse = new SymbolListResponse();
                List<Symbol> returnMap = null;
                result = result.toString().replace("responseObject", "symbolsList");
                symbolListResponse = gson.fromJson(result, SymbolListResponse.class);
                returnMap = symbolListResponse.getSymbolsList();
                for (Symbol map : returnMap) {
                    lsfRepository.updateSymbol(map);// Updating L08_SYMBOL
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String updateLiquidityType(String respString) {
        logger.debug("===========LSF : (updateLiquidityType)-REQUEST, requestMessage" + respString);
        CommonResponse cmr = new CommonResponse();
        SymbolListResponse response = gson.fromJson(respString, SymbolListResponse.class);
        List<Symbol> updateList = response.getSymbolsList();
        for (Symbol smb : updateList) {
            if (smb.getLiquidityType() != null) {
                lsfRepository.updateLiquidType(smb);
            }
        }
        cmr.setResponseCode(200);
        cmr.setResponseMessage("updated");
        logger.debug("===========LSF : (updateLiquidityType)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String loadSymbols(Map objMap) {
        SymbolListResponse sResponse = new SymbolListResponse();
        try {
            String exchangeCode = objMap.get("exchange").toString();
            String symbolCode = "1";
            if (objMap.containsKey("symbolCode")) {
                symbolCode = objMap.get("symbolCode").toString();
            }
            if (symbolCode.equalsIgnoreCase("") || symbolCode == null) {
                symbolCode = "1";
            }
            logger.debug("===========LSF : (searchSymbol)-REQUEST, exchange"
                         + exchangeCode
                         + ", symbolCode"
                         + symbolCode);
            List<Symbol> searchSymbol = lsfRepository.loadSymbols(exchangeCode, symbolCode);
            sResponse.setResponseCode(200);
            sResponse.setSymbolsList(searchSymbol);
        } catch (Exception ex) {
            ex.printStackTrace();
            sResponse.setResponseCode(500);
            sResponse.setErrorMessage(ex.getMessage());
        }
        logger.debug("===========LSF : (searchSymbol)-LSF-SERVER RESPONSE  : " + gson.toJson(sResponse));

        return gson.toJson(sResponse);
    }

    private String getAllSymbols() {
        SymbolListResponse sResponse = new SymbolListResponse();
        try {
            logger.debug("===========LSF : (getAllSymbols)-REQUEST");
            List<Symbol> allSymbol = lsfRepository.loadAllSymbols();
            sResponse.setResponseCode(200);
            sResponse.setSymbolsList(allSymbol);
        } catch (Exception ex) {
            ex.printStackTrace();
            sResponse.setResponseCode(500);
            sResponse.setErrorMessage(ex.getMessage());
        }
        logger.debug("===========LSF : (getAllSymbols)-LSF-SERVER RESPONSE  : " + gson.toJson(sResponse));

        return gson.toJson(sResponse);
    }

    private String getSymbolMarginabilityGroups(Map objMap) {
        CommonResponse cmr = new CommonResponse();
        logger.debug("+++++++++++++++++++++++++++++++ " + objMap);
        try {
            String exchange = objMap.get("exchange").toString();
            String symbolCode = objMap.get("symbolCode").toString();

            logger.debug("===========LSF : (getSymbolMarginabilityGroups)-REQUEST, exchange"
                         + exchange
                         + ", symbolCode"
                         + symbolCode);

            List<SymbolMarginabilityPercentage> marginabilityPercentages =
                    lsfRepository.getSymbolMarginabilityGroups(symbolCode,
                                                                                                                      exchange);

            cmr.setResponseCode(200);
            cmr.setResponseObject(marginabilityPercentages);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        logger.debug("===========LSF : (getSymbolMarginabilityGroups)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));

        return gson.toJson(cmr);
    }

    private String addToWishList(String rowMessage) {
        logger.debug("===========LSF : (addToWishList)-REQUEST, request" + rowMessage);
        CommonResponse cmr = new SymbolListResponse();
        try {
            MApplicationSymbolWishList application = gson.fromJson(
                    rowMessage,
                    MApplicationSymbolWishList.class);  //(MApplicationSymbolWishList)dataService.findOne(bRequest);
            if (lsfRepository.updateWishListSymbols(application) == "1") {
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Successfully Added Wish List");
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage("Add to Wish List Failed");
            }
        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ex.getMessage());
        }
        logger.debug("===========LSF : (addToWishList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String loadSymbolWishList(Map<String, Object> objMap) {
        String corellationID = "";
        if (objMap.containsKey("corellationID")) {
            corellationID = objMap.get("corellationID").toString();
        }
        SymbolListResponse sResponse = new SymbolListResponse();
        try {
            String mId = objMap.get("id").toString();
            String exchange = null;
            if (objMap.containsKey("exchange")) {
                exchange = objMap.get("exchange").toString();
            }
            logger.debug("===========LSF : (loadSymbolWishList)-REQUEST, id :" + mId + " , exchange:" + exchange);
            List<Symbol> exchangeSymbols = lsfRepository.getWishListSymbols(mId, exchange);
            MApplicationSymbolWishList fromDB = new MApplicationSymbolWishList();
            MurabahApplication application = lsfRepository.getMurabahApplication(mId);
            fromDB.setWishListSymbols(exchangeSymbols);
            if (fromDB != null) {
                sResponse.setSymbolsList(fromDB.getWishListSymbols());
            }
            if (application != null) {
                // send Consentration group for display purpose to the customer
                StockConcentrationGroup group = new StockConcentrationGroup();
                List<LiquidityType> gropLiqTypes =
                        lsfRepository.getStockConcentrationGroupLiquidTypes(application.getStockConcentrationGroup());
                group.setConcentrationList(gropLiqTypes);
                sResponse.setConcentrationGroup(group);
            }
            sResponse.setResponseCode(200);
        } catch (Exception ex) {
            ex.printStackTrace();
            sResponse.setResponseCode(500);
            sResponse.setErrorMessage(ex.getMessage());
        }
        logger.info("===========LSF : (loadSymbolWishList)LSF-SERVER RESPONSE  :"
                    + gson.toJson(sResponse).toString()
                    + " , CorrelationID:"
                    + corellationID);
        return gson.toJson(sResponse);
    }

    private String updateSymbolClassifyLogStatus(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            lsfRepository.updateSymbolClassifyLogStatus(Long.valueOf(String.valueOf(map.get("customerId"))));

            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Done");
            logger.info("Symbol classify log request, format error!!!");
        } catch (NumberFormatException e) {
            commonResponse.setResponseCode(400);
            commonResponse.setResponseMessage("Error");
            logger.info("Symbol classify log updated!!!");
        }

        return gson.toJson(commonResponse);
    }

    private String getSymbolClassifyLog(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<SymbolClassifyLog> symbolClassifyLogs = lsfRepository.getSymbolClassifyLog(Long.valueOf(String.valueOf(
                    map.get("customerId"))));

            commonResponse.setResponseCode(200);
            commonResponse.setResponseObject(symbolClassifyLogs);
            logger.info("Symbol classify log request, format error!!!");
        } catch (NumberFormatException e) {
            commonResponse.setResponseCode(400);
            commonResponse.setResponseMessage("Error");
            logger.info("Symbol classify log request received!!!");
        }

        return gson.toJson(commonResponse);
    }

    private String getSymbolClassificationData(Map<String, Object> map) {
        SymbolListResponse sResponse = new SymbolListResponse();

        List<Symbol> symbols = lsfRepository.loadSymbolsForClassification();
        sResponse.setSymbolsList(symbols);
        if (map.containsKey("id") && !map.get("id").toString().equalsIgnoreCase("-1")) {
            String applicationID = map.get("id").toString();
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationID);
            MarginabilityGroup marginabilityGroup = helper.getMarginabilityGroup(application.getMarginabilityGroup());
            sResponse.setMarginabilityGroup(marginabilityGroup);
        } else {
            List<MarginabilityGroup> marginabilityGroups = lsfRepository.getMarginabilityGroups();
            if (marginabilityGroups != null) {
                for (MarginabilityGroup marginabilityGroup : marginabilityGroups) {
                    if (marginabilityGroup.getIsDefault() == 1) {
                        marginabilityGroup.setMarginabilityList(lsfRepository.getMarginabilityGroupLiquidTypes(
                                marginabilityGroup.getId()));
                        sResponse.setMarginabilityGroup(marginabilityGroup);
                        break;
                    }
                }
            }
        }
        logger.info("===========LSF : (getStockClassificationData)LSF-SERVER RESPONSE  :" + gson.toJson(sResponse)
                                                                                                .toString());

        return gson.toJson(sResponse);
    }

    private String getSymbolInstrumentList() {
        //syncSymbols(GlobalParameters.getInstance().getDefaultExchange());
        InstrumentListResponse sResponse = new InstrumentListResponse();

        List<InstumentType> instrumentList = lsfRepository.loadSymbolInstrumentTypes();
        sResponse.setInstumentTypeList(instrumentList);

        logger.info("===========LSF : (getSymbolInstrumentList)LSF-SERVER RESPONSE  :" + gson.toJson(sResponse)
                                                                                             .toString());

        return gson.toJson(sResponse);
    }

    private String getSymbolMarginabilityData(Map<String, Object> map) {
        logger.info("============ MAP" + gson.toJson(map));
        CommonResponse commonResponse = new CommonResponse();
        try {
            List<SymbolMarginabilityPercentage> percentages = lsfRepository.getSymbolMarginabilityPercentage(map.get(
                    "id").toString());

            commonResponse.setResponseCode(200);
            commonResponse.setResponseObject(percentages);
            logger.info("Symbol marginability percentage request, request received");
        } catch (Exception e) {
            commonResponse.setResponseCode(400);
            commonResponse.setResponseMessage("Error");
            logger.info("Symbol marginability percentage request, Error" + e);
        }

        return gson.toJson(commonResponse);
    }
}
