package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class OMSCommission {
    private double brokerCommission;
    private double exchangeCommission;


    public double getBrokerCommission() {
        return brokerCommission;
    }

    public void setBrokerCommission(double brokerCommission) {
        this.brokerCommission = brokerCommission;
    }

    public double getExchangeCommission() {
        return exchangeCommission;
    }

    public void setExchangeCommission(double exchangeCommission) {
        this.exchangeCommission = exchangeCommission;
    }
}
