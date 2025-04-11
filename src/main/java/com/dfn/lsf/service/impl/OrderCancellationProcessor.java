package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Not defined route found only processor created named orderCancellationProcessor
 */
@Service
@RequiredArgsConstructor
public class OrderCancellationProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MurabahApplicationPersistProcessor.class);

    private final Gson gson;
    private final Helper helper;


    @Override
    public String process(String request) {
        CommonInqueryMessage basketCancellationRequest = new CommonInqueryMessage();
        basketCancellationRequest.setReqType(LsfConstants.ORDER_BASKET_CANCELLATION);
        String response = helper.handleOMSRequest(
                helper.sendMessageToOms(gson.toJson(basketCancellationRequest))
                      .toString(),
                LsfConstants.HTTP_PRODUCER_OMS_REQ_HANDLER);
        logger.debug("===========LSF : Order Basket Cancellation Request received & Sent to OMS , OMS Response :"
                     + response);
        return null;
    }
}
