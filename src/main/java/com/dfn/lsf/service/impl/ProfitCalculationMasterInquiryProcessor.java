package com.dfn.lsf.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.util.ProfitCalculationNew;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.OrderProfit;
import com.dfn.lsf.model.ProfitCalMurabahaApplication;
import com.dfn.lsf.model.ProfitCalculationMasterEntry;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.ProfitCalculationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Qualifier("999") // find the correct qualifier
@RequiredArgsConstructor
public class ProfitCalculationMasterInquiryProcessor implements MessageProcessor {

    private final Gson gson;
    private final LSFRepository lsfRepository;
    //private final ProfitCalculationUtils profitCalculationUtils;
    private final ProfitCalculationNew profitCalculationUtils;
    private final LsfCoreService lsfCore;
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(String request) {
        auditLogProcessor.process(request);
        log.info("Processing profit calculation master inquiry request: {}", request);
        Map<String, Object> requestMap = gson.fromJson(request, new TypeToken<Map<String, Object>>() {}.getType());
        String requestType = (String) requestMap.get("requestType");            // todo
        String response = null;

        switch (requestType) {
            case LsfConstants.REQ_PROFIT_CAL_RELATED_MASTER_DATA: {//application summary status
                return getProfitCalRelatedMasterData();
            }
            case LsfConstants.REQ_CALCULATE_MISSING_ENTRIES: {//calculate missing entries for the application
                return runManualCalculation(requestMap);
            }
        }
        return null;
    }

    private String getProfitCalRelatedMasterData() {
        ProfitCalculationMasterEntry entry = lsfRepository.getLastProfitCalculationEntry();
        List<ProfitCalMurabahaApplication> applicationList = lsfRepository.getProfitCalculationEligibleApplications();
        entry.setMurabahaApplicationList(applicationList);
        log.info("===========LSF : (reqProfitCalRelatedMasterData)-LSF-SERVER RESPONSE  : " + gson.toJson(entry));
        return gson.toJson(entry);
    }

    private String runManualCalculation(Map<String, Object> reqMap) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(200);
        String applicationID = (String) reqMap.get("id");
        String dateStr = (String) reqMap.get("issueDate");
        String customerID = (String) reqMap.get("customerId");
        Date startDate = LSFUtils.formatStringToDate(dateStr);
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        String masterCashAccount = lsfCore.getMasterCashAccount();
        log.info("runManualCalculation id : "+applicationID+ " issueDate : "+dateStr+ " customerID : "+customerID);

        if (startDate.equals(new Date())) { // if the issueDate is equal to the current day
            if (LSFUtils.isABeforeSettlementJob()) { // if manual run before the job reject request
                response.setResponseCode(500);
                response.setErrorCode(LsfConstants.ERROR_NOT_ALLOW_TO_RUN_MANUAL_PROFIT_CYCLE_FOR_FUTURE_DATE);
            } else {
                List<OrderProfit> profits = lsfRepository.getProfitEntryForApplicationAndDate(applicationID, LSFUtils.formatDateToString(new Date()));
                if (profits != null && profits.size() > 0) { // if the profit cycle is already run
                    response.setResponseCode(500);
                    response.setErrorCode(LsfConstants.ERROR_NOT_ALLOW_TO_RUN_MANUAL_PROFIT_CYCLE_FOR_FUTURE_DATE);
                } else { // run the calculation for today
                    profitCalculationUtils.runCalculationForTheCurrentDay(murabahApplication, masterCashAccount);
                }
            }
        } else if (startDate.compareTo(new Date()) == 1) { // if the issueDate after current day --> reject the request
            response.setResponseCode(500);
            response.setErrorCode(LsfConstants.ERROR_NOT_ALLOW_TO_RUN_MANUAL_PROFIT_CYCLE_FOR_FUTURE_DATE);
        } else if (startDate.compareTo(new Date()) == -1) { // if the issueDate before the current day
            int daysUntilToday = LSFUtils.getDateDiff(LSFUtils.formatDateToString(new Date()), LSFUtils.formatDateToString(startDate));
            for (int i = 0; i <= daysUntilToday; i++) {
                Date processingDate = LSFUtils.dateAdd(startDate, 0, 0, i);

                if (i == daysUntilToday) { // currant Date
                    if (!LSFUtils.isABeforeSettlementJob()) {
                        lsfRepository.correctProfitEntry(applicationID, customerID, LSFUtils.formatDateToString(processingDate));
                    }
                } else {
                    lsfRepository.correctProfitEntry(applicationID, customerID, LSFUtils.formatDateToString(processingDate));
                }
            }
        }
        return gson.toJson(response);
    }
}
