package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PhysicalDeliverOrder {
    private String clientName;
    private String applicationId;
    private String poId;
    private int isReqForDelivery;
    private String mobileNo;
    private String rolloverId;
    private String otherInfo;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public int getIsReqForDelivery() {
        return isReqForDelivery;
    }

    public void setIsReqForDelivery(int isReqForDelivery) {
        this.isReqForDelivery = isReqForDelivery;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getRolloverId() {
        return rolloverId;
    }

    public void setRolloverId(String rolloverId) {
        this.rolloverId = rolloverId;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }
}
