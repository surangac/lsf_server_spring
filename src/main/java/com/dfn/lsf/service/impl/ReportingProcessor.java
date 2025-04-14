package com.dfn.lsf.service.impl;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.report.GridResponse;
import com.dfn.lsf.model.report.ReportConfiguration;
import com.dfn.lsf.model.report.ReportResponse;
import com.dfn.lsf.model.report.StreamingResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.reporting.ReportFactory;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Qualifier("21")
@Slf4j
public class ReportingProcessor implements MessageProcessor {

    private final LSFRepository lsfRepository;
    private final ReportFactory reportFactory;
    private final Gson gson;
    private final Helper helper;
    @Override
    public String process(String request) {
        log.info("Report processor received a request.");
            ReportResponse response = null;
            String rawMessage = (String) request;
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap = gson.fromJson(rawMessage, requestMap.getClass());

            String reportName = String.valueOf(requestMap.get("report_name"));
            String reportFormat = String.valueOf(requestMap.get("report_format"));
            String reportParams = String.valueOf(requestMap.get("report_summ"));
            if (!reportName.equals("null")) {
                ReportConfiguration reportConfig = lsfRepository.getReportConfiguration(reportName);
                reportConfig.setRequestMap(requestMap);

                if (reportFormat.equalsIgnoreCase("GRID")) {
                    List<?> reportDataList = (List) reportFactory.getReportData(requestMap, reportConfig);
                    Map<String, Object> parameterMap = reportFactory.getReportParameters(requestMap, reportConfig);
                    response = new GridResponse();
                    response.setData(reportDataList, parameterMap);
                } else if (reportFormat.equals("null")) {
                    log.info("Error in generating the report: invalid report format");
                } else {
                    reportConfig.setFormat(reportFormat);
                    String reportReturnType = String.valueOf(requestMap.get("return_type"));
                    if (reportReturnType.equalsIgnoreCase("REALTIME")) {
                        List<Map<String, ?>>reportDataList = (List<Map<String, ?>>) reportFactory.getReportData(requestMap, reportConfig);
                        Map<String, Object> parameterMap = reportFactory.getReportParameters(requestMap, reportConfig);
                        OutputStream outputStream = reportFactory.getReportAsStream(reportDataList, parameterMap, reportConfig);
                        response = new StreamingResponse();
                        response.setData(outputStream);
                    } else {
                        /*This is for generating report asynchronously. Synchronous send-receive will timeout for larger reports.
                        * Send only one_way = true requests this path*/
                        List<Map<String, ?>> reportDataList = (List<Map<String, ?>>) reportFactory.getReportData(requestMap, reportConfig);
                        Map<String, Object> parameterMap = reportFactory.getReportParameters(requestMap, reportConfig);

                        response = reportFactory.saveReport(reportDataList, parameterMap, reportConfig);

                        try {
                            helper.acknowledgeReportGeneration(gson.toJson(response), LsfConstants.HTTP_PRODUCER_LSF_ADMIN_REPORT_ACK);
                        } catch (Exception e) {
                            log.info("Error in report response: couldn't find producer!!!"); 
                        }
                    }
                }
            } else {
                log.info("Error in generating the report: empty report name");
            }

            if (response != null) {
                return gson.toJson(response.returnResponse());
            } else {
                log.info("There is nothing to respond!!!");
                return "No data";
            }
    }

}
