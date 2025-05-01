package com.dfn.lsf.service.reporting;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.stereotype.Component;

import com.dfn.lsf.model.report.ReportConfiguration;
import com.dfn.lsf.model.report.GeneratedResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportFactory {
    private final LSFRepository lsfRepository;

    public Collection<?> getReportData(HashMap<String, String> requestMap, ReportConfiguration reportConfig) {
        List<String> inputParameters = reportConfig.getDataParameters();
        HashMap<String, String> parameterMap = new HashMap<>(inputParameters.size());
        for (String parameter : inputParameters) {
            parameterMap.put(parameter, requestMap.get(parameter));
        }
        return lsfRepository.getDataForReporting(reportConfig.getPackageName(),
                                            reportConfig.getDataProcedure(), reportConfig.getClassName(), parameterMap);
    }

    public Map<String, Object> getReportParameters(HashMap<String, String> requestMap, ReportConfiguration reportConfig) {
        List<String> inputParameters = reportConfig.getParamParameters();
        HashMap<String, String> parameterMap = new HashMap<>(inputParameters.size());
        for (String parameter : inputParameters) {
            parameterMap.put(parameter, requestMap.get(parameter));
        }
        List<Map<String, Object>> responseList = lsfRepository.getParamsForReporting(reportConfig.getPackageName(),
                reportConfig.getParamProcedure(), reportConfig.getClassName(), parameterMap);

        return responseList != null && !responseList.isEmpty() ? responseList.get(0) : new HashMap<>();
    }

    public GeneratedResponse saveReport(Collection<Map<String, ?>> reportDataList, Map parameterMap,
                                                     ReportConfiguration reportConfig) {
        InputStream inputStream = null;
        GeneratedResponse response = null;
        try {
            inputStream = new FileInputStream(reportConfig.getTemplatePath());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JRMapCollectionDataSource mapCollectionDataSource = new JRMapCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, mapCollectionDataSource);

            String fileFormat = reportConfig.getFormat().toUpperCase();
            String filePath = reportConfig.getReportDestination() + "/" + reportConfig.getReportName() + "." + fileFormat;
            JRExporter exporter = null;
            switch (fileFormat) {
                case "PDF":
                    exporter = new JRPdfExporter();
                    break;
                case "XLSX":
                    exporter = new JRXlsxExporter();
                    break;
                case "HTML":
                    exporter = new JRHtmlExporter();
                    break;
                default:
                    exporter = new JRPdfExporter();
            }
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, filePath);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            exporter.exportReport();

            /*following line is for test purposes*/
            //JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Projects\\DIP-Server_3.9.6\\deploy\\test" + ".pdf");
            log.info("Report generated successfully");
            inputStream.close();
            response = new GeneratedResponse("user", filePath);
            log.info("Report Generated : " + filePath);
        } catch (IOException e) {
            log.info("Error in generating the report: Can't find report template!!!");
        } catch (JRException e) {
            log.info("Error in generating the report: something went wrong with Jasper!!!");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public OutputStream getReportAsStream(Collection<Map<String, ?>> reportDataList, Map parameterMap,
                                                 ReportConfiguration reportConfig) {
        OutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(reportConfig.getTemplatePath());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JRMapCollectionDataSource mapCollectionDataSource = new JRMapCollectionDataSource(reportDataList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, mapCollectionDataSource);

            String fileFormat = reportConfig.getFormat().toUpperCase();
            JRExporter exporter = null;
            switch (fileFormat) {
                case "PDF":
                    exporter = new JRPdfExporter();
                    break;
                case "XLSX":
                    exporter = new JRXlsxExporter();
                    break;
                case "HTML":
                    exporter = new JRHtmlExporter();
                    break;
                default:
                    exporter = new JRPdfExporter();
            }
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
            exporter.exportReport();

        /*following line is for test purposes*/
            //JasperExportManager.exportReportToPdfFile(jasperPrint, "D:\\Projects\\DIP-Server_3.9.6\\deploy\\test" + ".pdf");
            log.info("Report generated successfully");
            inputStream.close();
        } catch (IOException e) {
            log.info("Error in generating the report: Can't find report template!!!");
        } catch (JRException e) {
            log.info("Error in generating the report: something went wrong with Jasper!!!");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        return outputStream;
    }
}
