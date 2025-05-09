package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class CommissionStructure {
    private int id;
    private double fromValue;
    private double toValue;
    private double flatAmount;
    private double percentageAmount;
    private double sibourRate;
    private double libourRate;
    private String createdUserId;
    private String createdUserName;
    private String CreatedDate;
}
