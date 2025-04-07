package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SymbolMarginabilityPercentage {

    private String symbolCode;
    private String securityName;
    private String securityNameAr;
    private double marginabilityPercentage;
    private int isMarginable;

    public String getSecurityNameAr() {

        return securityNameAr;
    }

    public void setSecurityNameAr(final String securityNameAr) {

        this.securityNameAr = securityNameAr;
    }

    private String exchange;
    private String groupName;
    private double groupId;

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(final String groupName) {

        this.groupName = groupName;
    }

    public int getIsMarginable() {

        return isMarginable;
    }

    public void setIsMarginable(final int isMarginable) {

        this.isMarginable = isMarginable;
    }

    public String getSymbolCode() {

        return symbolCode;
    }

    public void setSymbolCode(final String symbolCode) {

        this.symbolCode = symbolCode;
    }

    public String getSecurityName() {

        return securityName;
    }

    public void setSecurityName(final String securityName) {

        this.securityName = securityName;
    }

    public double getMarginabilityPercentage() {

        return marginabilityPercentage;
    }

    public void setMarginabilityPercentage(final double marginabilityPercentage) {

        this.marginabilityPercentage = marginabilityPercentage;
    }

    public String getExchange() {

        return exchange;
    }

    public void setExchange(final String exchange) {

        this.exchange = exchange;
    }

    public double getGroupId() {

        return groupId;
    }

    public void setGroupId(final double groupId) {

        this.groupId = groupId;
    }
}
