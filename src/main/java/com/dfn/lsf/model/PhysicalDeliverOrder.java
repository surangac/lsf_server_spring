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
    private String certificatePath;
    private String additionalDetails;
    private String additionalDocName;
    private String additionalDocPath;
}
