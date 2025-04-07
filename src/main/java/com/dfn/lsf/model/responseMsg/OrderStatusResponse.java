package com.dfn.lsf.model.responseMsg;

/**
 * Created by surangac on 9/7/2015.
 */
public class OrderStatusResponse {
    private int orderId;
    private int orderStatus;
    private double completedOrderValue;
    private double vatAmount;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getCompletedOrderValue() {
        return completedOrderValue;
    }

    public void setCompletedOrderValue(double completedOrderValue) {
        this.completedOrderValue = completedOrderValue;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }
}
