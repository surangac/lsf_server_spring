package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by manodyas on 11/10/2016.
 */
public class ApplicationStatus {
    private String applicationID;
    private String customerID;
    private String customerName;
    private int currentLevel;
    private int overallStatus;
    private String orderFilledStatus;
    private double orderFilledValue;
    private double settledAmount;
    private String customerApproveStatus;
    private String settlementStatus;
    private String statusDescription;
    private String settlementDescription;
    private String liquidatedStatus;
    private int customerActivityID;
    private String portfolioNo;
}
