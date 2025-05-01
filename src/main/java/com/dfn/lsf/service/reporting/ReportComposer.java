package com.dfn.lsf.service.reporting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dfn.lsf.model.ReportConfigObject;
import com.dfn.lsf.model.ReportCompletedResponse;
import com.dfn.lsf.model.StockConcentrationRptData;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

// Add JasperReports imports
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.report.MarginInformation;
import com.dfn.lsf.model.report.FinanceBrokerageInfo;
import com.dfn.lsf.model.report.FinanceBrokerageInfoSummary;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.GlobalParameters;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ReportComposer {

    private final LSFRepository lsfRepository;
    private final Gson gson;
    private final Helper helper;

    // load this from config file, default value is ../reports/lsf.png
    @Value("${lsf.img.path:../reports/lsf.png}")
    private String LSF_IMG_PATH;

    public Object generateReport(ReportConfigObject reportConfigObject) {
        try {
            Map<String, Object> parameterMap = getParameters(reportConfigObject);
            Collection<Map<String, ?>> reportDataList = getData(reportConfigObject);
            return getReportAsStream(reportDataList, parameterMap, reportConfigObject);
        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage(), e);
            return new ReportCompletedResponse(-1, null, reportConfigObject.getReportDestination(), 
                reportConfigObject.getAdminUserID(), null, null);
        }
    }

    public Object generateReport(ReportConfigObject reportConfigObject, StockConcentrationRptData stockConcentrationRptData) {
        try {
            Map<String, Object> parameterMap = getParameters(reportConfigObject, stockConcentrationRptData);
            Collection<Map<String, ?>> reportDataList = getData(reportConfigObject, stockConcentrationRptData);
            return getReportAsStream(reportDataList, parameterMap, reportConfigObject);
        } catch (Exception e) {
            log.error("Error generating stock concentration report: {}", e.getMessage(), e);
            return null;
        }
    }

    private Object getReportAsStream(Collection<Map<String, ?>> reportDataList, Map<String, Object> parameterMap, 
            ReportConfigObject reportConfigObject) throws IOException, JRException {
        
        try (InputStream inputStream = new FileInputStream(reportConfigObject.getTemplatePath())) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, dataSource);
            
            String tempFileName = generateTempFileName(reportConfigObject);
            String fileFormat = reportConfigObject.getFormat();
            String filePath = reportConfigObject.getReportDestination() + "/" + tempFileName + "." + fileFormat;
            
            JRExporter exporter = createExporter(fileFormat);
            configureExporter(exporter, jasperPrint, filePath, reportConfigObject);
            exporter.exportReport();

            if (reportConfigObject.getReportID() != LsfConstants.STOCK_CONCENTRATION_REPORT) {
                log.info("Report Generated: {}", filePath);
                return new ReportCompletedResponse(1, filePath, reportConfigObject.getReportDestination(), 
                    reportConfigObject.getAdminUserID(), reportConfigObject.getFormat(), tempFileName);
            } else {
                log.info("Report outputStreamed for report: {}", reportConfigObject.getReportName());
                return new ByteArrayOutputStream();
            }
        } catch (Exception e) {
            log.error("Error in report generation: {}", e.getMessage(), e);
            return new ReportCompletedResponse(-1, null, reportConfigObject.getReportDestination(), 
                reportConfigObject.getAdminUserID(), null, null);
        }
    }

    private String generateTempFileName(ReportConfigObject reportConfigObject) {
        if (reportConfigObject.getReportID() == LsfConstants.INVESTMENT_OFFER_LETTER_RPOERT) {
            return reportConfigObject.getReportName() + "_" + reportConfigObject.getApplicationID();
        }
        return reportConfigObject.getReportName() + "." + System.nanoTime();
    }

    private JRExporter createExporter(String fileFormat) {
        return switch (fileFormat.toLowerCase()) {
            case "pdf" -> new JRPdfExporter();
            case "xlsx" -> new JRXlsxExporter();
            case "html" -> new JRHtmlExporter();
            default -> new JRPdfExporter();
        };
    }

    private void configureExporter(JRExporter exporter, JasperPrint jasperPrint, String filePath, 
            ReportConfigObject reportConfigObject) {
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        if (reportConfigObject.getReportID() != LsfConstants.STOCK_CONCENTRATION_REPORT) {
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, filePath);
        } else {
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new ByteArrayOutputStream());
        }
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
    }

    public Map getParameters(ReportConfigObject reportConfigObject) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> map = new HashMap<String, Object>();
        ArrayList<String> parameterList = reportConfigObject.getParameters();
        String jsonString = "";
        if (reportConfigObject.getReportID() == 1) {
            List<MurabahApplication> murabahApplications = lsfRepository.geMurabahAppicationUserID(reportConfigObject.getCustomerID());
            MurabahApplication murabahApplication = murabahApplications.get(0);
            jsonString = gson.toJson(murabahApplication);
            JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
            map = gson.fromJson(jsonObject, map.getClass());
        } else if (reportConfigObject.getReportID() == LsfConstants.MARGIN_INFORMATION_REPORT) {
            MarginInformation marginInformation = null;
            marginInformation = lsfRepository.getMarginInformation(reportConfigObject.getFromDate(), reportConfigObject.getToDate());
            marginInformation.setFromDate(reportConfigObject.getFromDate());
            marginInformation.setToDate(reportConfigObject.getToDate());
            jsonString = gson.toJson(marginInformation);
            JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
            map = gson.fromJson(jsonObject, map.getClass());
        } else if (reportConfigObject.getReportID() == LsfConstants.FINANCE_AND_BROKERAGE_REPORT) { //Finance and Brokerage Report
            String outstandingSum = "0";
            String totalExposure = "0";
            String accumulatedProfit = "0";
            String contractProfitSum = "0";
            String totalFees = "0";
            String totalCommission = "0";
            List<FinanceBrokerageInfo> financeBrokerageInfoList = lsfRepository.getFinanceBrokerageInfo();
            FinanceBrokerageInfoSummary financeBrokerageInfoSummary = new FinanceBrokerageInfoSummary();
            financeBrokerageInfoSummary.setMasterCashAccountBalance(getMasterAccountBalance());
            if (financeBrokerageInfoList != null && financeBrokerageInfoList.size() > 0) {
                for (FinanceBrokerageInfo financeBrokerageInfo : financeBrokerageInfoList) {
                    outstandingSum = String.valueOf(Double.parseDouble(outstandingSum) + Double.parseDouble(financeBrokerageInfo.getOutstandingLoan()));
                    accumulatedProfit = String.valueOf(Double.parseDouble(accumulatedProfit) + Double.parseDouble(financeBrokerageInfo.getAccumulatedProfit()));
                    contractProfitSum = String.valueOf(Double.parseDouble(contractProfitSum) + Double.parseDouble(financeBrokerageInfo.getContractProfit()));
                    totalFees = String.valueOf(Double.parseDouble(totalFees) + Double.parseDouble(financeBrokerageInfo.getTotalFees()));
                    totalCommission = String.valueOf(Double.parseDouble(totalCommission) + Double.parseDouble(financeBrokerageInfo.getTotalCommission()));
                }
                totalExposure = String.valueOf((Double.parseDouble(outstandingSum) / (Double.parseDouble(financeBrokerageInfoSummary.getMasterCashAccountBalance()) + Double.parseDouble(outstandingSum))));
                financeBrokerageInfoSummary.setOutstandingSum(outstandingSum);
                financeBrokerageInfoSummary.setTotalExposure(totalExposure);
                financeBrokerageInfoSummary.setAccumulatedProfit(accumulatedProfit);
                financeBrokerageInfoSummary.setContractProfitSum(contractProfitSum);
                financeBrokerageInfoSummary.setTotalFees(totalFees);
                financeBrokerageInfoSummary.setTotalCommission(totalCommission);
            }
            jsonString = gson.toJson(financeBrokerageInfoSummary);
            JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
            map = gson.fromJson(jsonObject, map.getClass());

        } else if (reportConfigObject.getReportID() == LsfConstants.INVESTMENT_OFFER_LETTER_RPOERT) {

            map.put("date", getCurrentTimeStamp());
            map.put("agreementDate", getCurrentTimeStamp());
            map.put("attention", getCurrentTimeStamp());


            map.put("approvedLimit", getCurrentTimeStamp());
            map.put("broker", getCurrentTimeStamp());
            map.put("investmentAccBroker", getCurrentTimeStamp());
            map.put("profitRate", getCurrentTimeStamp());
            List<MurabahApplication> murabahApplications = lsfRepository.geMurabahAppicationUserID(reportConfigObject.getCustomerID());
            MurabahApplication murabahApplication = murabahApplications.get(0);
            map.put("customerName", murabahApplication.getFullName());

        }
        for (String para : parameterList) {
            if (map.containsKey(para)) {
                responseMap.put(para, map.get(para).toString());
            }
        }
        return responseMap;
    }

    public Map getParameters(ReportConfigObject reportConfigObject, StockConcentrationRptData stockConcentrationRptData) {
        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> map = new HashMap<String, Object>();
        ArrayList<String> parameterList = reportConfigObject.getParameters();
        String jsonString = "";
        jsonString = gson.toJson(stockConcentrationRptData);
        JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
        map = gson.fromJson(jsonObject, map.getClass());
        if (reportConfigObject.getReportID() == LsfConstants.STOCK_CONCENTRATION_REPORT) {
            map.put("totalReceibableCash", stockConcentrationRptData.getTotalReceibableCash());
            map.put("totalBuyingPower", stockConcentrationRptData.getTotalBuyingPower());
            map.put("reportName", reportConfigObject.getReportName());
            map.put("totalPayableCash", stockConcentrationRptData.getTotalPayableCash());
            map.put("cashBalance", stockConcentrationRptData.getCashBalance());
            map.put("totalAsset", stockConcentrationRptData.getTotalAsset());
            map.put("buyingPwerPercentage", stockConcentrationRptData.getBuyingPwerPercentage());
            map.put("totalAssetPercentage", stockConcentrationRptData.getTotalAssetPercentage());
            map.put("imagePath", LSF_IMG_PATH);

        }
        for (String para : parameterList) {
            if (map.containsKey(para)) {
                responseMap.put(para, map.get(para).toString());
            }
        }
        return responseMap;
    }

    public Collection getData(ReportConfigObject reportConfigObject) {
        ArrayList<String> functionVariables = reportConfigObject.getFunctionVariables();
        Map<String, Object> map = new HashMap<String, Object>();
        Collection<Map<String, ?>> reportDataList = new ArrayList<>();
        if (reportConfigObject.getReportID() == 1) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("a", "abc");
            tempMap.put("b", "bcd");
            reportDataList.add(tempMap);
        } else if (reportConfigObject.getReportID() == LsfConstants.MARGIN_INFORMATION_REPORT) {
            Map<String, Object> tempMap = new HashMap<>();
            tempMap.put("a", "abc");
            tempMap.put("b", "bcd");
            reportDataList.add(tempMap);
        } else if (reportConfigObject.getReportID() == LsfConstants.FINANCE_AND_BROKERAGE_REPORT) {
            List<FinanceBrokerageInfo> financeBrokerageInfoList = lsfRepository.getFinanceBrokerageInfo();
            if (financeBrokerageInfoList != null && financeBrokerageInfoList.size() > 0) {
                for (FinanceBrokerageInfo financeBrokerageInfo : financeBrokerageInfoList) {
                    String jsonString = gson.toJson(financeBrokerageInfo);
                    JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
                    map = gson.fromJson(jsonObject, map.getClass());
                    Map<String, Object> response = new HashMap<>();
                    for (String para : functionVariables) {
                        if (map.containsKey(para)) {
                            response.put(para, map.get(para).toString());
                        }
                    }
                    reportDataList.add(response);
                }
            }
        } else if (reportConfigObject.getReportID() == LsfConstants.INVESTMENT_OFFER_LETTER_RPOERT) {
            List<Symbol> symbolWishList = lsfRepository.getWishListSymbols(reportConfigObject.getApplicationID(), "TDWL");
            if (symbolWishList != null && symbolWishList.size() > 0) {
                for (Symbol symbol : symbolWishList) {
                    String jsonString = gson.toJson(symbol);
                    JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
                    map = gson.fromJson(jsonObject, map.getClass());
                    Map<String, Object> response = new HashMap<>();
                    for (String para : functionVariables) {
                        if (map.containsKey(para)) {
                            response.put(para, map.get(para).toString());
                        }
                    }
                    reportDataList.add(response);
                }
            }
        }
        return reportDataList;
    }

    public Collection getData(ReportConfigObject reportConfigObject, StockConcentrationRptData stockConcentrationRptData) {
        ArrayList<String> functionVariables = reportConfigObject.getFunctionVariables();
        Map<String, Object> map = new HashMap<String, Object>();
        Collection<Map<String, ?>> reportDataList = new ArrayList<>();
        if (reportConfigObject.getReportID() == LsfConstants.STOCK_CONCENTRATION_REPORT) {
            List<Symbol> concentrationSymbolList = stockConcentrationRptData.getConcentrationSymbolList();
            if (concentrationSymbolList != null && concentrationSymbolList.size() > 0) {
                for (Symbol symbol: concentrationSymbolList) {
                    String jsonString = gson.toJson(symbol);
                    JsonObject jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
                    map = gson.fromJson(jsonObject, map.getClass());
                    Map<String, Object> response = new HashMap<>();
                    for (String para : functionVariables) {
                        if (map.containsKey(para)) {
                            response.put(para, map.get(para).toString());
                        }
                    }
                    reportDataList.add(response);
                }
            }
        }
        return reportDataList;
    }

    private String getMasterAccountBalance() {

        String institutionTradingAccount = null;
        String exchange = null;
        String cashAccountBalance = null;
        institutionTradingAccount = GlobalParameters.getInstance().getInstitutionTradingAcc();
        exchange = GlobalParameters.getInstance().getDefaultExchange();
        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
        if (exchange != null && institutionTradingAccount != null) {
            commonInqueryMessage.setReqType(LsfConstants.GET_ACCOUNT_INFO_BY_TRADING_ACCOUNT);
            commonInqueryMessage.setTradingAccountId(institutionTradingAccount);
            commonInqueryMessage.setExchange(exchange);
            String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage), LsfConstants.HTTP_PRODUCER_OMS_GET_MASTER_CASH_ACCOUNT);
            if (result != null) {
                Map<String, Object> resMap = new HashMap<>();
                resMap = gson.fromJson(result, resMap.getClass());
                String responseString = resMap.get("responseObject").toString();
                Map<String, Object> finalMap = gson.fromJson(responseString, resMap.getClass());
                cashAccountBalance = finalMap.get("availableCash").toString();
            }
        }
        return cashAccountBalance;
    }

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}

