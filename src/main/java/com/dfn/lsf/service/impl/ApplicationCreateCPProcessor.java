package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.ErrorCodes;
import com.dfn.lsf.util.NotificationManager;
import com.dfn.lsf.util.OverrallApprovalStatus;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Defined in InMessageHandlerAdminCbr,InMessageHandlerCbr
 * Handling Message types :
 * - MESSAGE_TYPE_CREDIT_PROPOSAL_PROCESS = 9;
 */
@Service
@RequiredArgsConstructor
@Qualifier("9")
public class ApplicationCreateCPProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationCreateCPProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final NotificationManager notificationManager;

    @Override
    public String process(String request) {
        String rawMessage = (String) request;
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = new CommonResponse();
        map = gson.fromJson(rawMessage, map.getClass());
        int appStatus = 1;
        try {
            String statusChangedUserid = "";
            String statusChangedUserName = "";
            String statusMessage = "";
            String statusChangedIP = "127.0.0.1";
          /*  EsbPersistence bRequest = esbPersistence.createEsbQuery().where("id")
                    .is(map.get("id").toString())
                    .and("overallStatus").is(Integer.toString(OverrallApprovalStatus.PENDING.statusCode()));*/
            if (map.containsKey("userid")) {
                statusChangedUserid = map.get("userid").toString();
            }
            if (map.containsKey("username")) {
                statusChangedUserName = map.get("username").toString();
            }
            if (map.containsKey("statusMessage")) {
                statusMessage = map.get("statusMessage").toString();
            }
            if (map.containsKey("approvalStatus")) {
                appStatus = Integer.parseInt(map.get("approvalStatus").toString());
            }
            if (map.containsKey("ipAddress")) {
                statusChangedIP = map.get("ipAddress").toString();
            }
            logger.debug("===========LSF : (createCP)-REQUEST RECEIVED , statusChangedUserid"
                         + statusChangedUserid
                         + " , statusChangedUserName:"
                         + statusChangedUserName);

            MurabahApplication fromDB = lsfRepository.getMurabahApplication(map.get("id").toString());
            if (fromDB != null) {
                fromDB.setOverallStatus(Integer.toString(OverrallApprovalStatus.READYFORAPPROVAL.statusCode()));
                int currentLevel = fromDB.getCurrentLevel() + 1;
                fromDB.setCurrentLevel(currentLevel);
                fromDB.setTypeofFacility(map.get("typeofFacility").toString());
                fromDB.setFacilityType(map.get("facilityType").toString());
                fromDB.setProposedLimit(Double.parseDouble(map.get("proposedLimit").toString()));
                lsfRepository.updateMurabahApplication(fromDB);
                lsfRepository.approveApplication(
                        appStatus,
                        fromDB.getId(),
                        statusMessage,
                        statusChangedUserid,
                        statusChangedUserName,
                        statusChangedIP);
                notificationManager.sendNotification(fromDB);
                cmr.setResponseCode(200);
                cmr.setResponseMessage("Credit Proporsal has been Successfully Created");
            } else {
                cmr.setResponseCode(500);
                cmr.setErrorMessage(ErrorCodes.ERROR_CREATING_CP.errorDescription());
            }
        } catch (Exception e) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            e.printStackTrace();
        }
        logger.debug("===========LSF : (createCP)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
}
