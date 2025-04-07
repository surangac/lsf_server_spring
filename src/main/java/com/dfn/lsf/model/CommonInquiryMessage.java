package com.dfn.lsf.model;

import lombok.Data;

@Data
public class CommonInquiryMessage {

    private String reqType;
    private String basketReference;
    private String securityKey;
    private String applicationId;

    private String customerId;
    private String tradingAccountId;
    private String exchange;
    private OrderBasket lsfBasket;
    private String currency;
    private String params;
    private int changeParameter;
    private String value;
    private String contractId;
}
