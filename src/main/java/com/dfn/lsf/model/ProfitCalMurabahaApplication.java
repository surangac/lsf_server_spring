package com.dfn.lsf.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProfitCalMurabahaApplication {
    private String id;
    private String customerId;
    private Date lastProfitCycleDate;
    private String lastProfitCycleDateStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getLastProfitCycleDate() {
        return lastProfitCycleDate;
    }

    public void setLastProfitCycleDate(Date lastProfitCycleDate) {
        this.lastProfitCycleDate = lastProfitCycleDate;
    }

    public String getLastProfitCycleDateStr() {
        return lastProfitCycleDateStr;
    }

    public void setLastProfitCycleDateStr(String lastProfitCycleDateStr) {
        this.lastProfitCycleDateStr = lastProfitCycleDateStr;
    }
}
