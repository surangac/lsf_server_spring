package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
public class MarginabilityGroup {
    private String id;
    private String groupName;

    @CreatedDate
    private String createdDate;
    private int status;
    private String createdBy;
    private String approvedBy;
    private int isDefault;
    private List<LiquidityType> marginabilityList;
    private String additionalDetails;
    private double globalMarginablePercentage;
    private List<SymbolMarginabilityPercentage> marginableSymbols;
    private List<SymbolMarginabilityPercentage> deletedMarginableSymbols;
}
