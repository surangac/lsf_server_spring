package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by surangac on 5/25/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommonInqueryMessage {
    private int reqType;
    private String customerId;
    private String tradingAccountId;
    private String exchange;
    private OrderBasket lsfBasket;
    private String basketReference;
    private String currency;
    private  String params;
    private int changeParameter;
    private String value;
    private String contractId;
}
