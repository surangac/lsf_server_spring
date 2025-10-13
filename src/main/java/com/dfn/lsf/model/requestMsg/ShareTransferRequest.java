package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Share transfer request model
 */
@Getter
@Setter
@NoArgsConstructor
public class ShareTransferRequest {
    private int reqType;
    private String id;
    private String applicationid;
    private String fromTradingAccountId;
    private String toTradingAccountId;
    private String fromExchange;
    private String toExchange;
    private String symbol;
    private String exchange;
    private int quantity;
    private int sendToExchange;
    private String params;
    private String basketReference;
    private int txnCode;
    private String appId;
}
