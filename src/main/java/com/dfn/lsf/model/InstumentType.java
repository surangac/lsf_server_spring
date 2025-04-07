package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class InstumentType {
    private int instrumentType = -1;
    private String securityType;

    public int getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(int instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }
}
