package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.MessageHeader;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.security.CustomEncryption;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * every time this processor is at the end of the Routh path
 * As example:
 * "ps:otphandling -> consumer:originator", "ps:auditLogProcessor"
 * "ps:pendingActivityInquiryProcessor -> consumer:originator", "ps:auditLogProcessor"
 * "ps:adminCommonRejectProcessor -> consumer:originator", "ps:auditLogProcessor"
 * Seems we don't have used  this processor  as other processors
 */
@Service
@RequiredArgsConstructor
public class AuditLogProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;

    @Override
    public String process(String request) {
        Long id = 0L;
        String rawMassege = "";
        String messageType = "";
        String subMessageType = "";
        String ip = "";
        String channelID = "";
        String userName = "";
        String corellationID = "";
        MessageHeader header = gson.fromJson((String) request, MessageHeader.class);
        id = System.nanoTime();
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(request, map.getClass());
        messageType = header.getMessageType();
        if (map.containsKey("subMessageType")) {
            subMessageType = map.get("subMessageType").toString();
        }
        if (map.containsKey("ipAddress")) {
            ip = map.get("ipAddress").toString();
        }
        if (map.containsKey("channelId")) {
            channelID = map.get("channelId").toString();
            if (channelID.equalsIgnoreCase("1")) { //if client
                if (map.containsKey("securityKey")) {
                    String[] response = CustomEncryption.getDecrypted(map.get("securityKey").toString()).split("\\|");
                    userName = response[0];
                }
            } else if (channelID.equalsIgnoreCase("2")) {//if admin
                if (map.containsKey("userName")) {
                    userName = map.get("userName").toString();
                }
            }
        }
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        rawMassege = map.toString();
        lsfRepository.addAuditDetails(id, request, messageType, subMessageType, ip, channelID, userName, corellationID);
        return null;
    }
}
