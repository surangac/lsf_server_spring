package com.dfn.lsf.model.requestMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CloseLSFAccountRequest {
    private int reqType;
    private String fromTradingAccountId;
    private String toTradingAccountId;
    private String fromCashAccountId;
    private String toCashAccountId;
    private String appId;
}
