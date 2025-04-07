package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/20/2015.
 */
public class MarginabilityGroup {
    private String id;
    private String groupName;
    private String createdDate;
    private int status;
    private String createdBy;
    private String approvedBy;
    private int isDefault;
    private List<LiquidityType> marginabilityList;
    private String additionalDetails;
    private double globalMarginablePercentage;

    public List<SymbolMarginabilityPercentage> getMarginableSymbols() {

        return marginableSymbols;
    }

    public void setMarginableSymbols(final List<SymbolMarginabilityPercentage> marginableSymbols) {

        this.marginableSymbols = marginableSymbols;
    }

    private List<SymbolMarginabilityPercentage> marginableSymbols;

    public List<LiquidityType> getMarginabilityList() {
        return marginabilityList;
    }

    public void setMarginabilityList(List<LiquidityType> marginabilityList) {
        this.marginabilityList = marginabilityList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getAdditionalDetails() {

        return additionalDetails;
    }

    public void setAdditionalDetails(final String additionalDetails) {

        this.additionalDetails = additionalDetails;
    }

    public double getGlobalMarginablePercentage() {

        return globalMarginablePercentage;
    }

    public void setGlobalMarginablePercentage(final double globalMarginablePercentage) {

        this.globalMarginablePercentage = globalMarginablePercentage;
    }
}
