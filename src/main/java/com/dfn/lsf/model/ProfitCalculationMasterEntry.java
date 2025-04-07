package com.dfn.lsf.model;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProfitCalculationMasterEntry {
    private Date jobDate;
    private String jobDateStr;
    private int eligibleAppCount;
    private int completedAppCount;
    private Date jobStartTime;
    private Date jobEndTime;

    private List<ProfitCalMurabahaApplication> murabahaApplicationList;

    public Date getJobDate() {
        return jobDate;
    }

    public void setJobDate(Date jobDate) {
        this.jobDate = jobDate;
    }

    public String getJobDateStr() {
        return jobDateStr;
    }

    public void setJobDateStr(String jobDateStr) {
        this.jobDateStr = jobDateStr;
    }

    public int getEligibleAppCount() {
        return eligibleAppCount;
    }

    public void setEligibleAppCount(int eligibleAppCount) {
        this.eligibleAppCount = eligibleAppCount;
    }

    public int getCompletedAppCount() {
        return completedAppCount;
    }

    public void setCompletedAppCount(int completedAppCount) {
        this.completedAppCount = completedAppCount;
    }

    public Date getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public Date getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public List<ProfitCalMurabahaApplication> getMurabahaApplicationList() {
        return murabahaApplicationList;
    }

    public void setMurabahaApplicationList(List<ProfitCalMurabahaApplication> murabahaApplicationList) {
        this.murabahaApplicationList = murabahaApplicationList;
    }
}
