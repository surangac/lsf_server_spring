package com.dfn.lsf.model;

import java.util.List;

import com.dfn.lsf.model.responseMsg.FtvSummary;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PurchaseOrderListResponse {
    private double administrationFee;
    private List<PurchaseOrder> purchaseOrderList;
    private String customerAddress;
    private List<FtvSummary> dailyFtvList;
}
