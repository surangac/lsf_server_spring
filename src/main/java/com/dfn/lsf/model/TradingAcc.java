package com.dfn.lsf.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 5/25/2015.
 */
public class TradingAcc {
    private String accountId;
    private String exchange;
    private boolean isLsfType;
    private String collateralId;
    private List<Symbol> symbolsForColleteral;
    private String applicationId;

    public List<Symbol> getSymbolsForColleteral() {
        return symbolsForColleteral;
    }

    public void setSymbolsForColleteral(List<Symbol> symbolsForColleteral) {
        this.symbolsForColleteral = symbolsForColleteral;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public boolean isLsfType() {
        return isLsfType;
    }

    public void setLsfType(boolean isLsfType) {
        this.isLsfType = isLsfType;
    }

    public String getCollateralId() {
        return collateralId;
    }

    public void setCollateralId(String collateralId) {
        this.collateralId = collateralId;
    }

    public Symbol isSymbolExist(String symbol,String exchange){
        if(this.symbolsForColleteral==null){
            this.symbolsForColleteral=new ArrayList<>();
        }
        for(Symbol smb:this.symbolsForColleteral){
            if((smb.getSymbolCode().equals(symbol)) && (smb.getExchange().equals(exchange))){
                return  smb;
            }
        }
        Symbol sObj=new Symbol();
        sObj.setSymbolCode(symbol);
        sObj.setExchange(exchange);
        this.symbolsForColleteral.add(sObj);
        return  sObj;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void mapFromOmsResponse(TradingAccOmsResp omsResp) {
        this.accountId = omsResp.getAccountId();
        this.exchange = omsResp.getExchange();
        this.isLsfType = omsResp.isLsf();
        this.collateralId = omsResp.getRelCashAccNo();
    }
}
