package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.Symbol;

/**
 * Created by surangac on 8/5/2015.
 */
public class TradingAccResponse {
    private List<Symbol> responseObject;

    public List<Symbol> getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(List<Symbol> responseObject) {
        this.responseObject = responseObject;
    }
}
