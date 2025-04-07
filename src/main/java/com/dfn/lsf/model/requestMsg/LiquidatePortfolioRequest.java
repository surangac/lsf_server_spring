package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 9/20/2015.
 */
public class LiquidatePortfolioRequest {
    private int reqType;
    private String tradingAccountId;
    private String exchange;
    private String params;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getTradingAccountId() {
        return tradingAccountId;
    }

    public void setTradingAccountId(String tradingAccountId) {
        this.tradingAccountId = tradingAccountId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
