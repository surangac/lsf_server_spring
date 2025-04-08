package com.dfn.lsf.model.responseMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by surangac on 8/11/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProfitResponse {
    private double profitAmount;
    private double profitPercent;
    private double sibourAmount;
    private double LibourAmount;
    private double totalProfit;
}
