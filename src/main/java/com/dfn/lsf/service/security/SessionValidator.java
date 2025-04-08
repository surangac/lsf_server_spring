package com.dfn.lsf.service.security;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.dfn.lsf.model.UserSession;
import com.dfn.lsf.repository.LSFRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor    
public class SessionValidator {
    
    private final LSFRepository lsfRepository;

    public String createSession(String userName, int channelID, String ipAddress, String omsSessionID){
        String sessionID = UUID.randomUUID().toString().substring(4, 23).replace("-", "");
        String pureSecurityKey = userName + "|" + sessionID;
        String encryptedSecurityKey = CustomEncryption.getEncrypted(pureSecurityKey);
        String result = lsfRepository.addUpdateUserSession(sessionID, userName, channelID, 1, ipAddress, omsSessionID, 1);
        log.info("===========LSF : New Client Session Created, Session ID :" + sessionID + " ,User ID :" + userName + " ,Security Key :" + encryptedSecurityKey + ", Client IP:" + ipAddress + " , result :" + result);

        return encryptedSecurityKey;
    }
    public SessionValidationInternalResponse validateSession(String encryptedSecurityKey) {
        SessionValidationInternalResponse response = new SessionValidationInternalResponse();
        String[] spllitedKeys = decryptSecurityKey(encryptedSecurityKey);
        String username = spllitedKeys[0];
        String sessionID = spllitedKeys[1];
        UserSession userSession = getsession(username);
        log.info("===========LSF : Validating User Session , User ID :" + username + " Session ID : " + sessionID);
        if(userSession == null){
            response.setIsValidate(false);
            response.setRejectReason(LSFSecurityConstants.SUSPICIOUS_USER_ACTIVITY);
            log.error("===========LSF : Suspicious User Activity . User ID :" + username + "Session ID : " + sessionID);
        }else{
            if ((sessionID.equalsIgnoreCase(userSession.getSerssionId()))) {
                if(userSession.getStatus() != 1){
                    response.setIsValidate(false);
                    response.setRejectReason(LSFSecurityConstants.INVALID_SESSION);
                    return response;
                }else{
                    if (LSFSecurityConstants.SESSION_TIME_OUT_ENABLED) {
                        log.info("===========LSF : Session Timeout is enabled.");
                        if (((new Date()).getTime() - userSession.getLastActiveTime().getTime()) < LSFSecurityConstants.SESSION_TIME_OUT_PERIOD) {
                            response.setIsValidate(true);
                        } else {
                            response.setIsValidate(false);
                            response.setRejectReason(LSFSecurityConstants.SESSION_TIME_OUT);
                        }
                    }else{
                        response.setIsValidate(true);
                    }
                }

            } else {
                response.setIsValidate(false);
                response.setRejectReason(LSFSecurityConstants.INVALID_SESSION);
            }
            log.info("===========LSF : Is Valid Session :" + response.isValidate());
            //todo need to remove
            response.setIsValidate(true);
        }
        return response;
    }
    private String[] decryptSecurityKey(String encryptedSecurityKey){
        String decrypted = CustomEncryption.getDecrypted(encryptedSecurityKey);
        String[] response = decrypted.split("\\|");
        return response;
    }

    private UserSession getsession(String userId){
        UserSession respone =  null;
        try {
            List<UserSession> userSessionList = lsfRepository.getUserSession(userId);
            if(userSessionList != null){
                if(!userSessionList.isEmpty()) {
                    respone = userSessionList.get(0);
                }
            } else {
                log.info("===========LSF : Suspicious User Activity , User ID :" + userId);
            }
        } catch (Exception e) {
            respone = null;
        }
        return  respone;
    }

}
