package com.dfn.lsf.model.responseMsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by manodyas on 2/15/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class FTVInfo {

    private String tradingAcc;
    private double amountGranted;
    private double amountUtilized;
    private double commission;
    private double commissionPreviousDay;
    private double pfMarketValue;
    private double marginablePfMarketValue;
    private double ftvPreviousDay;
    private String startDate;
    private int tenor;
    private String expireDate;
    private int dayesLeft;
    private double cumProfit;

    private String customerID;
    private String customerName;
    private String applicationID;
    private double operativeLimit;
    private double limitUtilized;
    private double remainingOperativeLimit;
    private double outstandingAmount;
    private double ftv;
    private double adminFeeCharged;
    private double maximumWithdrawAmount;
    private double availableCashBalance;
    private String displayApplicationId;
}
