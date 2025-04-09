package com.dfn.lsf.model.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportConfiguration {
    private int reportId;
    private String reportName;
    private String reportDescription;
    private String packageName;
    private String dataProcedure;
    private List<String> dataParameters;
    private String paramProcedure;
    private List<String> paramParameters;
    private String className;
    private String templatePath;
    private String reportDestination;
    private String format;

    private Map<String, String> requestMap;

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDataProcedure() {
        return dataProcedure;
    }

    public void setDataProcedure(String dataProcedure) {
        this.dataProcedure = dataProcedure;
    }

    public List<String> getDataParameters() {
        return dataParameters;
    }

    public void setDataParameters(List<String> dataParameters) {
        this.dataParameters = dataParameters;
    }

    public void setDataParameters(String parametersAsString) {
        if (parametersAsString == null) {
            this.dataParameters = new ArrayList<>();
        } else {
            parametersAsString = parametersAsString.replaceAll(" ", "");
            String[] parameterArray = parametersAsString.split(",");
            this.dataParameters = Arrays.asList(parameterArray);
        }
    }

    public String getParamProcedure() {
        return paramProcedure;
    }

    public void setParamProcedure(String paramProcedure) {
        this.paramProcedure = paramProcedure;
    }

    public List<String> getParamParameters() {
        return paramParameters;
    }

    public void setParamParameters(List<String> paramParameters) {
        this.paramParameters = paramParameters;
    }

    public void setParamParameters(String parametersAsString) {
        if (parametersAsString == null) {
            this.paramParameters = new ArrayList<>();
        } else {
            parametersAsString = parametersAsString.replaceAll(" ", "");
            String[] parameterArray =  parametersAsString.split(",");
            this.paramParameters = Arrays.asList(parameterArray);
        }
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getReportDestination() {
        return reportDestination;
    }

    public void setReportDestination(String reportDestination) {
        this.reportDestination = reportDestination;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, String> getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(Map<String, String> requestMap) {
        this.requestMap = requestMap;
    }
}
