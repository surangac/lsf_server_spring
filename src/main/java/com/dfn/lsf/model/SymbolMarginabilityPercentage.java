package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolMarginabilityPercentage {
    private String symbolCode;
    private String exchange;
    private double marginabilityPercentage;
    private int isMarginable;
    private String groupName;
    private double groupId;
    private String securityName;
    private String securityNameAr;
}
