package com.dfn.lsf.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReportConfigObject {
    private int reportID;
    private String reportName;
    private String format;
    private String templatePath;
    private String reportDestination;
    private ArrayList parameters;
    private String fromDate;
    private String toDate;
    private String customerID;
    private String adminUserID;
    private ArrayList functionVariables;
    private String applicationID;
}
