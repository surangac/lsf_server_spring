package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Tenor {

    private String tenorId;
    private int duration;
    private double profitPercentage;
    private String createdDate;
    private String createdBy;
    private String updatedBy;
    private int status;
    private String approvedby;
    private String approvedDate;
}
