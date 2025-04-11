package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.responseMsg.PendingActivity;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Defined in InMessageHandlerAdminCbr
 * route : PENDING_ACTIVITY_INQUIRY_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_PENDING_ACTIVITY_INQUIRY = 24;
 */
@Service
@RequiredArgsConstructor
@Qualifier("24")
public class PendingActivityInquiryProcessor implements MessageProcessor {

    private final LSFRepository lsfRepository;
    private final Gson gson;

    @Override
    public String process(String request) {
        List<PendingActivity> pendingActivityList = new ArrayList<>();
        pendingActivityList = lsfRepository.getPendingActivityList();
        return gson.toJson(pendingActivityList);
    }
}
