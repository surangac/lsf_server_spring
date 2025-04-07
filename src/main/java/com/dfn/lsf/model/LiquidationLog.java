package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by manodyas on 9/21/2015.
 */
public class LiquidationLog {
   private int liquidationReference;
   private String fromAccount;
    private String toAccount;
    private double amountToBeSettled;
    private String applicationID;
    private int status;
    private String orderID;

    public int getLiquidationReference() {
        return liquidationReference;
    }

    public void setLiquidationReference(int liquidationReference) {
        this.liquidationReference = liquidationReference;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public double getAmountToBeSettled() {
        return amountToBeSettled;
    }

    public void setAmountToBeSettled(double amountToBeSettled) {
        this.amountToBeSettled = amountToBeSettled;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
