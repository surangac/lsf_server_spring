package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.SymbolListResponse;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used in end of the route.
 * consumer:loadExchangeSymbolTimer -> trans:loadExchangeSymbolsTransformer->processor:exchangeSymbolLoader
 * Seems we don't have used  this processor  as other processors
 */
@Service
@RequiredArgsConstructor
public class ExchangeSymbolLoaderProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeSymbolLoaderProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = new CommonResponse();
        map = gson.fromJson(request, map.getClass());
        String actionType = map.get("subMessageType").toString();

/*
        List<String> exchangeList = new ArrayList<>();
*/
        String[] exchangeArray = null;
        String exchanges = LSFUtils.getConfiguration("exchanges");
        if (exchanges.contains(",")) {
            exchangeArray = exchanges.split(",");
        } else {
            exchangeArray = new String[1];
            exchangeArray[0] = exchanges;
        }
        //String[] exchangeList = new String[]{ "ADSM", "DFM", "TDWL"};

      /*  String defaultExchange = GlobalParameters.getInstance().getDefaultExchange();
        exchangeList.add(defaultExchange);*/
        switch (actionType) {
            case LsfConstants.LOAD_INIT_DATA: /*-----------Sync All the Exchange Symbols-----------*/
                for (int j = 0; j < exchangeArray.length; j++) {
                    syncSymbols(exchangeArray[j]);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Symbols Loaded");
                }
                return gson.toJson(cmr);
        }
        return null;
    }

    private void syncSymbols(String exchangeCode) {
        try {
            CommonInqueryMessage slistReq = new CommonInqueryMessage();
            slistReq.setExchange(exchangeCode);
            if (GlobalParameters.getInstance()
                                .getShariaSymbolsAsCollateral()) { // only sharia symbols allow for collateral
                slistReq.setReqType(LsfConstants.SYNC_SYMBOLS);
            } else {
                // all symbols allow for collateral
                slistReq.setReqType(LsfConstants.SERVICE_TYPE_GET_EXCHANGE_ALL_SYMBOLS);
            }
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
}
