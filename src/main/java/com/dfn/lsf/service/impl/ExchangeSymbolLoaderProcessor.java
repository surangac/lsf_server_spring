package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.SymbolListResponse;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Used in end of the route.
 * consumer:loadExchangeSymbolTimer -> trans:loadExchangeSymbolsTransformer->processor:exchangeSymbolLoader
 * Seems we don't have used  this processor  as other processors
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeSymbolLoaderProcessor implements MessageProcessor {

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(String request) {
        syncSymbolsProcessor();
        return null;
    }

    @Scheduled(fixedRateString = "${scheduler.exchange.symbol.loader.rate:600000}")
    public void syncSymbolsProcessor() {
        String exchange = GlobalParameters.getInstance().getDefaultExchange();
        syncSymbols(exchange);
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
//                var filteredSymbols = returnMap.stream()
//                                               .filter(symbol -> !symbol.getSecurityType().equals("OPT") && !symbol.getSecurityType().equals("FUT"))
//                                               .toList();

                var filteredSymbols = returnMap.stream()
                                               .filter(symbol -> !symbol.getSecurityType().equals("OPT")
                                                                 && !symbol.getSecurityType().equals("FUT")
                                                                 && !symbol.getSymbolCode().endsWith(".C")
                                                                 && !symbol.getSymbolCode().endsWith(".B")
                                                                 && !symbol.getSymbolCode().endsWith(".O")
                                                                 && !symbol.getSymbolCode().endsWith("`B")
                                                                 && !symbol.getSymbolCode().endsWith("`O")
                                                                 && !symbol.getSymbolCode().endsWith("`C")
                                                                 && symbol.getInstrumentType() != 68)
                                               .toList();

                for (Symbol map : filteredSymbols) {
                    lsfRepository.updateSymbol(map);// Updating L08_SYMBOL
                }
            }
        } catch (Exception e) {
            log.error("Error in syncSymbols: {}", e.getMessage());
        }
    }
}
