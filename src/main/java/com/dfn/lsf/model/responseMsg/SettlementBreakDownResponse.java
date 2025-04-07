package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.OrderProfit;

/**
 * Created by manodyas on 9/23/2015.
 */
public class SettlementBreakDownResponse {
    private String applicationID;
    private List<OrderProfit> profitList;

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public List<OrderProfit> getProfitList() {
        return profitList;
    }

    public void setProfitList(List<OrderProfit> profitList) {
        this.profitList = profitList;
    }
}
