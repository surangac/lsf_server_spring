package com.dfn.lsf.model.requestMsg;

import com.dfn.lsf.model.GlobalParameters;

/**
 * Created by Atchuthan on 5/28/2015.
 */
public class GlobalParameterUpdateRequest {
    private int messageType;
    private String masterDataType;
    GlobalParameters globalParameters;
    private double totalOutstandingAmount;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(String masterDataType) {
        this.masterDataType = masterDataType;
    }

    public GlobalParameters getGlobalParameters() {
        return globalParameters;
    }

    public void setGlobalParameters(GlobalParameters globalParameters) {
        this.globalParameters = globalParameters;
    }

    public double getTotalOutstandingAmount() {
        return totalOutstandingAmount;
    }

    public void setTotalOutstandingAmount(double totalOutstandingAmount) {
        this.totalOutstandingAmount = totalOutstandingAmount;
    }
}
