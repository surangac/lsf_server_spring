package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.PurchaseOrder;

/**
 * Created by manodyas on 10/16/2015.
 */
public class PurchaseOrderListResponse {
    private double administrationFee;
    private List<PurchaseOrder> purchaseOrderList;
    private String customerAddress;
    private List<FtvSummary> dailyFtvList;

    public double getAdministrationFee() {
        return administrationFee;
    }

    public void setAdministrationFee(double administrationFee) {
        this.administrationFee = administrationFee;
    }

    public List<PurchaseOrder> getPurchaseOrderList() {
        return purchaseOrderList;
    }

    public void setPurchaseOrderList(List<PurchaseOrder> purchaseOrderList) {
        this.purchaseOrderList = purchaseOrderList;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public List<FtvSummary> getDailyFtvList() {
        return dailyFtvList;
    }

    public void setDailyFtvList(List<FtvSummary> dailyFtvList) {
        this.dailyFtvList = dailyFtvList;
    }
}
