package com.dfn.lsf.util;

public enum ErrorCodes {
    ERROR_EXCEPTION(0,"Exception occured,please check the server log"),
    ERROR_CREATING_CP(1,"Credit Proporsal is not Available or not in Ready For Approval State, can't Proceed"),
    ERROR_AUTHORIZATION_CP(2,"Credit Proporsal is not Available or not in Ready For Approval State, can't Proceed"),
    ERROR_INVALIED_TRADING_ACC(3,"Invalied Trading Acc"),
    ERROR_INVALIED_BANK_ACC(4,"Invalied Bank Acc");

    ErrorCodes(int statusId,String description){
        this.errorCode=statusId;
        this.description=description;
    }
    public int errorCode(){
        return this.errorCode;
    }
    public String errorDescription(){
        return this.description;
    }

    int errorCode;
    String description;
}
