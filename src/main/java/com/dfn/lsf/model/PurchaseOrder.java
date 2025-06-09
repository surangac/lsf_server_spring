package com.dfn.lsf.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrder {
    private String id;
    private String customerName;
    private String applicationId;
    private String customerId;
    private String tradingAccNum;
    private double orderValue;
    private double orderSettlementAmount;
    private String settlementDateDisplay;
    private String settlementDate;
    private double profitPercentage;
    private String tradingAccount;
    private String exchange;
    private String settlementAccount;
    private boolean isOneTimeSettlement;
    private int installmentFrequency;
    private int settlementDurationInMonths;
    private List<Installments> installments;
    private int settlementStatus;
    private List<Symbol> symbolList;
    private String createdDate;
    private String tenorId;
    private int approvalStatus;
    private double profitAmount;
    private double sibourAmount;
    private double libourAmount;
    private String approvedDate;
    private String approvedByName;
    private String approvedById;
    private int orderStatus;
    private double orderCompletedValue;
    private int customerApproveStatus;
    private int noOfCallingAttempts;
    private String lastCalledTime;
    private String settledDate;
    private double totalOutStandingBalance;
    private int basketTransferState;
    private double simaCharges;
    private double transferCharges;
    private double vatAmount;
    private Date customerApprovedDate;
    private List<Commodity> commodityList;
    private int authAbicToSell;
    private int soldAmnt;
    private int cashTransferStatus = -1; //0-Cash blocked,1-Block release,2-transfered
    private String investorAcc;
    private int isPhysicalDelivery;
    private int sellButNotSettle;
    private RemainTime remainTimeToSell;
    private String certificatePath = null;
    private String approveComment;
    private int isLsfType;
} 