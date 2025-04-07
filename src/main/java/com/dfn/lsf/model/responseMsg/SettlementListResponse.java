package com.dfn.lsf.model.responseMsg;

import java.util.List;

/**
 * Created by manodyas on 9/24/2015.
 */
public class SettlementListResponse {
    private List<SettlementSummaryResponse> settlementSummaryResponseList;


    public List<SettlementSummaryResponse> getSettlementSummaryResponseList() {
        return settlementSummaryResponseList;
    }

    public void setSettlementSummaryResponseList(List<SettlementSummaryResponse> settlementSummaryResponseList) {
        this.settlementSummaryResponseList = settlementSummaryResponseList;
    }

  }
