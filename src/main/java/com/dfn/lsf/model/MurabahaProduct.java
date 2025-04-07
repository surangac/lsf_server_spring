package com.dfn.lsf.model;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MurabahaProduct {
    private int productType;
    private String profitMethod;
    private String productName;
    private String productDescription;
    private int status; // 0 - Pending , 1 - Approved
    private String productNameAR;
    private String productDescriptionAR;
    private List<Agreement> agreement = new ArrayList<>();
    private int financeMethodConfig; //0 - None , 1 - Share , 2 - Commodity , 3 - Both
    private List<PurchaseOrder> purchaseOrderList;

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getProfitMethod() {
        return profitMethod;
    }

    public void setProfitMethod(String profitMethod) {
        this.profitMethod = profitMethod;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProductNameAR() {
        return productNameAR;
    }

    public void setProductNameAR(String productNameAR) {
        this.productNameAR = productNameAR;
    }

    public String getProductDescriptionAR() {
        return productDescriptionAR;
    }

    public void setProductDescriptionAR(String productDescriptionAR) {
        this.productDescriptionAR = productDescriptionAR;
    }

    public List<Agreement> getAgreement() {
        return agreement;
    }

    public void setAgreement(List<Agreement> agreement) {
        this.agreement = agreement;
    }

    public int getFinanceMethodConfig() {
        return financeMethodConfig;
    }

    public void setFinanceMethodConfig(int financeMethodConfig) {
        this.financeMethodConfig = financeMethodConfig;
    }
}
