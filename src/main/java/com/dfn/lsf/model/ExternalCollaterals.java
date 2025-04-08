package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExternalCollaterals {
    private int id;
    private int applicationId;
    private int collateralId;
    private String collateralType;
    private String reference;
    private double collateralAmount;
    private String expireDate;
    private double haircutPercent;
    private double applicableAmount;
    private boolean addToCollateral;
    private String approvedUserId;
}
