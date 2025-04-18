package com.dfn.lsf.service.impl;

import java.io.OutputStream;
import java.util.Map;

import com.dfn.lsf.util.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.ReportCompletedResponse;
import com.dfn.lsf.model.ReportConfigObject;
import com.dfn.lsf.model.StockConcentrationRptData;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.reporting.ReportComposer;
import com.dfn.lsf.service.reporting.ReportFactory;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_REPORT_GENERATION_PROCESS;

@Service
@MessageType(MESSAGE_TYPE_REPORT_GENERATION_PROCESS)
@Slf4j
@RequiredArgsConstructor
public class ReportGenerationProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    private final Helper helper;
    
    private final Gson gson;

    private final ReportFactory reportFactory;
    private final ReportComposer reportComposer;
    
    @Override
    public String process(String request) {
        try {
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing report generation request with subMessageType: {}", subMessageType);
            
            // Handle different sub-message types
            switch (subMessageType) {
                case "generic":
                    return createGenericReport(requestMap);/*-----------creating generic report-----------*/
                case "stockConcentrationReport":
                    return generateStockConcentrationReport(requestMap);
                default:
                    log.warn("Unknown sub-message type: {}", subMessageType);
                    return createErrorResponse("Unknown sub-message type: " + subMessageType);
            }
        } catch (Exception e) {
            log.error("Error processing report generation request", e);
            return createErrorResponse("Error processing request: " + e.getMessage());
        }
    }
    
    private String createErrorResponse(String message) {
        CommonResponse response = new CommonResponse();
        response.setResponseCode(500);
        response.setErrorMessage(message);
        return gson.toJson(response);
    }

    private String createGenericReport(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        int reportID = 0;
        String reportType = null;
        String fromDate = null;
        String toDate = null;
        String customerID = null;
        String adminUserID = null;
        String fileFormat = null;
        String applicationID = null;
        ReportConfigObject reportConfigObject = null;


        if (map.containsKey("reportID")) {
            reportID = Integer.parseInt(map.get("reportID").toString());
        }
        log.info("===========LSF :Report Generation Request Received :" + reportID);
        reportConfigObject = lsfRepository.getReportConfigObject(reportID);

        if (map.containsKey("fromDate")) {
            fromDate = map.get("fromDate").toString();
            reportConfigObject.setFromDate(fromDate);
        }
        if (map.containsKey("toDate")) {
            toDate = map.get("toDate").toString();
            reportConfigObject.setToDate(toDate);
        }
        if (map.containsKey("customerID")) {
            customerID = map.get("customerID").toString();
        }
        if (map.containsKey("adminUserID")) {
            adminUserID = map.get("adminUserID").toString();
        }
        if (map.containsKey("fileFormat")) {
            fileFormat = map.get("fileFormat").toString();
        }
        if(map.containsKey("applicationID")){
            applicationID = map.get("applicationID").toString();
        }

        reportConfigObject.setCustomerID(customerID);
        reportConfigObject.setAdminUserID(adminUserID);
        reportConfigObject.setFormat(fileFormat);
        reportConfigObject.setApplicationID(applicationID);
        ReportCompletedResponse response = (ReportCompletedResponse) reportComposer.generateReport(reportConfigObject);
        if(reportID != LsfConstants.INVESTMENT_OFFER_LETTER_RPOERT){
            helper.acknowledgeReportGeneration(gson.toJson(response), LsfConstants.HTTP_PRODUCER_LSF_ADMIN_REPORT_ACK);
            return null;

        }else{
            if(response.getCompleteState()==1){
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage(response.getFileName());
            }else{
                commonResponse.setResponseCode(500);
            }
            return gson.toJson(commonResponse);
        }
    }


    private String generateStockConcentrationReport(Map<String, Object> map){
        int reportID = 0;
        String fileFormat = null;
        CommonResponse commonResponse = new CommonResponse();
        StockConcentrationRptData stockConcentrationRptData;
        stockConcentrationRptData = gson.fromJson(gson.toJson(map), StockConcentrationRptData.class);

        ReportConfigObject reportConfigObject = null;
        if (map.containsKey("reportID")) {
            reportID = Integer.parseInt(map.get("reportID").toString());
        }

        log.info("===========LSF :Report Generation Request Received For Stock Concentration Report");

        reportConfigObject = lsfRepository.getReportConfigObject(reportID);

        if (map.containsKey("fileFormat")) {
            fileFormat = map.get("fileFormat").toString();
        }

        reportConfigObject.setFormat(fileFormat);
        reportConfigObject.setReportName("Stock Concentration Report");
        OutputStream response = (OutputStream) reportComposer.generateReport(reportConfigObject,stockConcentrationRptData);
        return response.toString();
    }
}