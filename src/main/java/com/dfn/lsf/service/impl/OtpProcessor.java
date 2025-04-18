package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_OTP_PROCESS;

/**
 * Defined in InMessageHandlerCbr
 * route : HANDLE_OTP
 * Handling Message types :
 * - MESSAGE_TYPE_OTP_PROCESS = 22;
 */
@Service
@MessageType(MESSAGE_TYPE_OTP_PROCESS)
@RequiredArgsConstructor
public class OtpProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OtpProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final NotificationManager notificationManager;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(request, map.getClass());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                switch (subMessageType) {
                    case "reqNewOTP": // done
                        return generateOTP(map);
                    case "validateOTP": // done
                        return validateOTP(map);
                }
            }
        } catch (Exception ex) {
            cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            ex.printStackTrace();
        }
        return gson.toJson(cmr);
    }

    private String generateOTP(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String applicationId = map.get("id").toString();
        try {
            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
            String otp = getID();
            long otpGeneratedMill = new java.util.Date().getTime();
            murabahApplication.setOtp(otp);
            murabahApplication.setOtpGeneratedTime(otpGeneratedMill);
            // update Database
            logger.debug("===========LSF : Generate OTP : Application ID " + applicationId + " OTP - " + otp);
            lsfRepository.updateApplicationOtp(murabahApplication);
            notificationManager.sendOTP(murabahApplication, otp);
            // send SMS
            cmr.setResponseCode(200);
            cmr.setResponseMessage(murabahApplication.getMobileNo());
            logger.debug("===========LSF : Generate OTP Sending Response " + applicationId + " OTP - " + otp);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("OTP generation failed");
            cmr.setErrorCode(LsfConstants.ERROR_OTP_GENERATION_FAILED);
            logger.debug("===========LSF : Generate OTP error " + ex.getMessage());
        }

        return gson.toJson(cmr);
    }

    private String validateOTP(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String applicationId = map.get("id").toString();
        String otpResp = map.get("otp").toString();
        long otpValidityInMinutes = 5;
        try {
            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
            // first validate the expire time
            logger.debug("===========LSF : Validate OTP : Application ID " + applicationId + " OTP - " + otpResp);
            long currentTimeInmillis = new java.util.Date().getTime();
            long otpGeneratedTimeInMills = murabahApplication.getOtpGeneratedTime();
            long timeDiff = currentTimeInmillis - otpGeneratedTimeInMills;
            long diffMinutes = timeDiff / (60 * 1000);
            if (diffMinutes > otpValidityInMinutes) {
                // OTP expired and response with error message
                cmr.setResponseCode(500);
                cmr.setResponseMessage("OTP Expired");
            } else {
                if (murabahApplication.getOtp().equals(otpResp)) {
                    // entered OTP is valid
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("OTP Valid");
                } else {
                    cmr.setResponseCode(400);
                    cmr.setResponseMessage("OTP Mismatch");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("OTP validation failed");
            cmr.setErrorCode(LsfConstants.ERROR_OTP_VALIDATION_FAILED);
            logger.debug("===========LSF : Validate OTP error " + ex.getMessage());
        }
        logger.debug("===========LSF : Validate OTP : sending Response " + applicationId + " OTP - " + otpResp);
        return gson.toJson(cmr);
    }

    private String getID() {
        String dCase = "abcdefghijklmnopqrstuvwxyz";
        String uCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String sChar = "!@#$%^*";
        String intChar = "0123456789";
        Random r = new Random();
        String pass = "";
        while (pass.length() != 6) {
            int rPick = r.nextInt(4);
           /* if (rPick == 0){
                int spot = r.nextInt(25);
                pass += dCase.charAt(spot);
            }*//* else if (rPick == 1) {
                int spot = r.nextInt (25);
                pass += uCase.charAt(spot);
            } *//*else */
            if (rPick == 3) {
                int spot = r.nextInt(9);
                pass += intChar.charAt(spot);
            } /*else if (rPick == 2) {
                int spot = r.nextInt (7);
                pass += sChar.charAt(spot);
            }*/
        }
        return pass;
    }
}
