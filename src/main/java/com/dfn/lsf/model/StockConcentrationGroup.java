package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 5/20/2015.
 */
public class StockConcentrationGroup {
    private String id;
    private String groupName;
    private String createdDate;
    private int status;
    private String createdBy;
    private String approvedBy;
    private int isDefault;
    private List<LiquidityType> concentrationList;

    public List<LiquidityType> getConcentrationList() {
        return concentrationList;
    }

    public void setConcentrationList(List<LiquidityType> concentrationList) {
        this.concentrationList = concentrationList;
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
}
