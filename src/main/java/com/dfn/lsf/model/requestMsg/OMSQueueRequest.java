package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class OMSQueueRequest {
    private int messageType;
    private int correlationId;
    private int status;
    private double filledValue;
    private String customerId;
    private String pendingId;
    private double amount;
    private String cashAccNo;
    private String investorAccount;
    private String tradingAccount;
    private String exchangeAccount;
    private String mubasherNo;
    private String exchange;
    private String symbol;
    private int quantity;
    private double price;
    private double commission;
    private String sessionID;
    private int isLsf;
    private String basketReference;
    private double vat;
    private String contractId;
    private String openOrderValues;
    private String referenceNo;
    private String code;
}
