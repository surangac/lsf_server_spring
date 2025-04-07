package com.dfn.lsf.model.responseMsg;

import com.dfn.lsf.model.MApplicationCollaterals;

/**
 * Created by manodyas on 6/6/2016.
 */
public class OrderContractCustomerInfo {
    private String branch;
    private String contractSignDate;
    private String branchCity;
    private String CRNumber;
    private String CRIssueDate;
    private String ABICRepresented;
    private String customerName;
    private String customerID;
    private String address;
    private String investmentAccountNumber;
    private String murabahaPFNumber;
    private String PO;
    private String postCode;
    private String customerCity;
    private String telephoneNo;
    private String email;
    private String mobileNumber;
    private double transferringFee;
    private MApplicationCollaterals collaterals;
    private String orderContractSignedIP;
    private String customerNIN;
    private int customerActivityID;
    private boolean isExchangeAccountCreated;
    private double simaCharges;
    private double transferCharges;
    private double vatAmountforOrder;
    private double vatAmountforAdminFee;
    private int discountOnProfit;

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getContractSignDate() {
        return contractSignDate;
    }

    public void setContractSignDate(String contractSignDate) {
        this.contractSignDate = contractSignDate;
    }

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }

    public String getCRNumber() {
        return CRNumber;
    }

    public void setCRNumber(String CRNumber) {
        this.CRNumber = CRNumber;
    }

    public String getCRIssueDate() {
        return CRIssueDate;
    }

    public void setCRIssueDate(String CRIssueDate) {
        this.CRIssueDate = CRIssueDate;
    }

    public String getABICRepresented() {
        return ABICRepresented;
    }

    public void setABICRepresented(String ABICRepresented) {
        this.ABICRepresented = ABICRepresented;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInvestmentAccountNumber() {
        return investmentAccountNumber;
    }

    public void setInvestmentAccountNumber(String investmentAccountNumber) {
        this.investmentAccountNumber = investmentAccountNumber;
    }

    public String getMurabahaPFNumber() {
        return murabahaPFNumber;
    }

    public void setMurabahaPFNumber(String murabahaPFNumber) {
        this.murabahaPFNumber = murabahaPFNumber;
    }

    public String getPO() {
        return PO;
    }

    public void setPO(String PO) {
        this.PO = PO;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getTransferringFee() {
        return transferringFee;
    }

    public void setTransferringFee(double transferringFee) {
        this.transferringFee = transferringFee;
    }

    public MApplicationCollaterals getCollaterals() {
        return collaterals;
    }

    public void setCollaterals(MApplicationCollaterals collaterals) {
        this.collaterals = collaterals;
    }

    public String getOrderContractSignedIP() {
        return orderContractSignedIP;
    }

    public void setOrderContractSignedIP(String orderContractSignedIP) {
        this.orderContractSignedIP = orderContractSignedIP;
    }

    public String getCustomerNIN() {
        return customerNIN;
    }

    public void setCustomerNIN(String customerNIN) {
        this.customerNIN = customerNIN;
    }

    public int getCustomerActivityID() {
        return customerActivityID;
    }

    public void setCustomerActivityID(int customerActivityID) {
        this.customerActivityID = customerActivityID;
    }

    public boolean isExchangeAccountCreated() {
        return isExchangeAccountCreated;
    }

    public void setIsExchangeAccountCreated(boolean isExchangeAccountCreated) {
        this.isExchangeAccountCreated = isExchangeAccountCreated;
    }

    public double getSimaCharges() {
        return simaCharges;
    }

    public void setSimaCharges(double simaCharges) {
        this.simaCharges = simaCharges;
    }

    public double getTransferCharges() {
        return transferCharges;
    }

    public void setTransferCharges(double transferCharges) {
        this.transferCharges = transferCharges;
    }

    public double getVatAmountforOrder() {
        return vatAmountforOrder;
    }

    public void setVatAmountforOrder(double vatAmountforOrder) {
        this.vatAmountforOrder = vatAmountforOrder;
    }

    public double getVatAmountforAdminFee() {
        return vatAmountforAdminFee;
    }

    public void setVatAmountforAdminFee(double vatAmountforAdminFee) {
        this.vatAmountforAdminFee = vatAmountforAdminFee;
    }

    public int getDiscountOnProfit() {
        return discountOnProfit;
    }

    public void setDiscountOnProfit(int discountOnProfit) {
        this.discountOnProfit = discountOnProfit;
    }
}
