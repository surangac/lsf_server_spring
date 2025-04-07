package com.dfn.lsf.model.responseMsg;

public class CommissionDetail {
    private String commission;
    private String previousDayCommission;
    private String tradingAccId;

    public String getCommission() {
        return commission;
    }

    public String getTradingAccId() {
        return tradingAccId;
    }

    public void setTradingAccId(String tradingAccId) {
        this.tradingAccId = tradingAccId;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getPreviousDayCommission() {
        return previousDayCommission;
    }

    public void setPreviousDayCommission(String previousDayCommission) {
        this.previousDayCommission = previousDayCommission;
    }
}
