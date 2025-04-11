package com.dfn.lsf.service.reporting;

import com.dfn.lsf.service.impl.MReportGenerationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MReportGenerationProcessor.class);

    String templatePath = new File("../reports/templates").getAbsolutePath() + "\\";
    String destinationPath = "";

    public String generateReport(Collection<Map<String, ?>> reportDataList, Map parameterMap, Map reportObject) {
        String result = "-1";
        try {
            String reportCode = reportObject.get("reportCode").toString();
            String applicationID = reportObject.get("id").toString();
            String fileFormat = reportObject.get("fileFormat").toString();
            String reportFile = destinationPath + reportCode + "_" + applicationID + "." + fileFormat;
            String templateFile = templatePath + reportObject.get("template").toString();
            Path pthReport = FileSystems.getDefault().getPath(reportFile);
            logger.info("Template Path : " + templateFile + ", Destination Path : " + destinationPath);
            if (!Files.exists(pthReport, LinkOption.NOFOLLOW_LINKS)) {
                logger.info("File is not available, Start to generate report : " + reportFile);
                generateJasperReport(reportDataList, parameterMap, templateFile, reportFile, fileFormat);
            } else {
                logger.info("File Already Exists Removing & Regenerating File: " + reportFile);
                Files.delete(pthReport);
                generateJasperReport(reportDataList, parameterMap, templateFile, reportFile, fileFormat);
            }

            result = "1";
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (JRException jre) {
            jre.printStackTrace();
        }
        return result;
    }

    private void generateJasperReport(Collection<Map<String, ?>> reportDataList,
                                      Map parameterMap,
                                      String templateFile,
                                      String detinationFile,
                                      String fileFormat) throws IOException, JRException {
        String sourceFilePath = templateFile;
        JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
        JRProperties.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
        InputStream inputStream = new FileInputStream(sourceFilePath);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
        JRMapCollectionDataSource mapCollectionDataSource = new JRMapCollectionDataSource(reportDataList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, mapCollectionDataSource);

        switch (fileFormat) {
            case "pdf":
                JasperExportManager.exportReportToPdfFile(jasperPrint, detinationFile);

                break;
            case "xlsx":
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, detinationFile);
                exporter.exportReport();
                break;
            case "html":
                JasperExportManager.exportReportToHtmlFile(jasperPrint, detinationFile);
                break;
        }

        inputStream.close();
        System.out.println("finished");
    }
}
