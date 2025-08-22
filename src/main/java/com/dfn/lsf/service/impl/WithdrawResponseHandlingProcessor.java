package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.MessageType;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.dfn.lsf.util.LsfConstants.WITHDRAW_SUCCESS_RESPONSE;

@Service
@MessageType(WITHDRAW_SUCCESS_RESPONSE)
@RequiredArgsConstructor
public class WithdrawResponseHandlingProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawResponseHandlingProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(final String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        OMSQueueRequest omsRequest = gson.fromJson((String) request, OMSQueueRequest.class);
        map = gson.fromJson(request, map.getClass());
        int messageType = omsRequest.getMessageType();
        switch (messageType) {
            case LsfConstants.WITHDRAW_SUCCESS_RESPONSE:
                return processB2BWithdrawResponse(map);/*----------Withdraw Success Response-------*/
            default:
                return null;
        }
    }

    private String processB2BWithdrawResponse(Map<String, Object> map) {
        String referenceNo = map.get("referenceNo").toString();
        //if(referenceNo.contains("|")){
        double status = Double.parseDouble(map.get("status").toString()/*.replace(".0","")*/);
        //String narration = map.get("narration").toString();
        logger.info("===========LSF :Withdraw Response Receive from , referenceNo: "
                    + referenceNo
                    + " , status :"
                    + status /*+ " , narration:" + narration */);
        if (status > 0) {
            lsfRepository.updateDepositStatus(
                    referenceNo,
                    LsfConstants.RESPONSE_RECEIVED_B2B_SUCCESS,
                    LsfConstants.WITHDRAW);
        } else {
            lsfRepository.updateDepositStatus(
                    referenceNo,
                    LsfConstants.RESPONSE_RECEIVED_B2B_FAILED,
                    LsfConstants.WITHDRAW);
        }
        //}

        return null;
    }
}
