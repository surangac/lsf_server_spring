package com.dfn.lsf.model;

import java.util.Date;

import lombok.Data;

@Data
public class Installments {
    private int instalmentNumber;
    private int instalmentDate;
    private String installmentDateString;
    private double installmentAmount;
    private int installmentStatus;
    private String installmentCompletedDate;
    private String orderId;
    private String applicationID;
    private double cashBalance;
    public String customerID;
} 