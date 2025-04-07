package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by manodyas on 10/20/2015.
 */
public class ReportCompletedResponse {
    private int completeState;
    private String fileName;
    private String destination;
    private String userName;
    private String reportFormat;
    private String reportName;
    
    public ReportCompletedResponse(int completeState, String fileName, String destination, String userName, String reportFormat, String reportName) {
        this.completeState = completeState;
        this.fileName = fileName;
        this.destination = destination;
        this.userName = userName;
        this.reportFormat = reportFormat;
        this.reportName = reportName;
    }

    public int getCompleteState() {
        return completeState;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDestination() {
        return destination;
    }

    public String getUserName() {
        return userName;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public String getReportName() {
        return reportName;
    }
}
