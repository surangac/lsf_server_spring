package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.OrderProfit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashBoardData {
    private String applicationId;
    private double remainingOperativeLimit;
    private double remainingApprovedLimit;
    private double utilizedLimit;
    private double cashCollateral;
    private double pfCollateral;
    private double totalCollateral;
    private List<FtvSummary> dailyFtvList;
    private double outstandingBalance;
    private OrderProfit orderProfit;
    private int productType;
}
