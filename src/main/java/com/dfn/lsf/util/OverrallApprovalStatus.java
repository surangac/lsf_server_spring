package com.dfn.lsf.util;

public enum OverrallApprovalStatus {
    PENDING(0, "Pending,Waiting for CP"),
    READYFORAPPROVAL(1,"Credit Proporsal Generated"),
    LEVEL1APPROVED(2,"Credit Proporsal LVL1 Approved"),
    LEVEL1REJECT(-2,"Credit Proporsal LVL1 Rejected"),
    LEVEL2APPROVED(3,"Waiting for FAL Acceptance"),
    LEVEL2REJECT(-3,"Credit Proporsal Rejected LVL2"),
    FAL_APPROVED(4,"FAL Accepted"),
    FAL_REJECTED(-4,"FAL Rejected by Client"),
    FAL_APPROVED_LEVEL1(5,"FAL Approved LVL1"),
    FAL_REJECTED_LEVEL1(-5,"FAL Rejected LVL1"),
    FAL_APPROVED_LEVEL2(6,"FAL Approved LVL2"),
    FAL_REJECTED_LEVEL2(-6,"FAL Rejected LVL2"),
    GENERATE_IOF(7,"Investment Offer Generated"),
    IOF_ACCEPTEDLVL1(8,"Investment Offer LVL1 Approved"),
    IOF_REJECTEDLVL1(-8,"Investment Offer LVL1 Rejected"),
    IOF_ACCEPTEDLVL2(9,"Investment Offer LVL2 Approved"),
    IOF_REJECTEDLVL2(-9,"Investment Offer LVL2 Rejected"),
    SUBMIT_COLLATERAL(10,"Collateral Submitted"),
    PURCHASE_ORDER_SUBMIT(11,"Submit Purchase Order"),
    WAITING_FOR_CUSTOMER_CONFIRMATION(12,"Waiting for Final Customer Confirmation"),
    CUSTOMER_FINAL_CONFIRMATION_GRANTED(13,"Final Customer Confirmation Granted"),
    CUSTOMER_FINAL_CONFIRMATION_REJECTED(-13,"Final Customer Confirmation Rejected"),
    COMPLETED(20,"Process Completed"),
    LIQUID(1,"Liquid"),
    SEMI_LIQUID(2,"Semi Liquid"),
    NON_LIQUID(3,"Non Liquid");

    OverrallApprovalStatus(int statusId,String description){
        this.statusId=statusId;
        this.description=description;
    }
    public int statusCode(){
        return this.statusId;
    }
    public String statusDescription(){
        return this.description;
    }

    int statusId;
    String description;
}
