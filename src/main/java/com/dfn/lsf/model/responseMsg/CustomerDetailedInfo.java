package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.model.Symbol;

/**
 * Created by manodyas on 11/1/2016.
 */
public class CustomerDetailedInfo {
    private String customerID;
    private String applicationID;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String tradingAccount;
    private String cashAccount;
    private String proposalDate;
    private double initialRAPV;
    private double financeRequiredAmount;
    private double proposedLimit;
    private String lsfTradingAccount;
    private String lsfCashAccount;
    private List<Symbol> pfCollateralList;
    private List<CashAcc> cashCollateralList;
    private double initialPFCollaterals;
    private double cashCollateral;
    private double totalCollateralValue;
    private List<PurchaseOrder> purchaseOrders;
    private boolean isSettled;
    private String settlementDate;
    private String settledDate;
    private String pendingStatus;
    private List<Status> statusList;
    List<FtvSummary> dailyFtvList;

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(String tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public String getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(String cashAccount) {
        this.cashAccount = cashAccount;
    }

    public String getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(String proposalDate) {
        this.proposalDate = proposalDate;
    }

    public double getInitialRAPV() {
        return initialRAPV;
    }

    public void setInitialRAPV(double initialRAPV) {
        this.initialRAPV = initialRAPV;
    }

    public double getFinanceRequiredAmount() {
        return financeRequiredAmount;
    }

    public void setFinanceRequiredAmount(double financeRequiredAmount) {
        this.financeRequiredAmount = financeRequiredAmount;
    }

    public double getProposedLimit() {
        return proposedLimit;
    }

    public void setProposedLimit(double proposedLimit) {
        this.proposedLimit = proposedLimit;
    }

    public String getLsfTradingAccount() {
        return lsfTradingAccount;
    }

    public void setLsfTradingAccount(String lsfTradingAccount) {
        this.lsfTradingAccount = lsfTradingAccount;
    }

    public String getLsfCashAccount() {
        return lsfCashAccount;
    }

    public void setLsfCashAccount(String lsfCashAccount) {
        this.lsfCashAccount = lsfCashAccount;
    }

    public List<Symbol> getPfCollateralList() {
        return pfCollateralList;
    }

    public void setPfCollateralList(List<Symbol> pfCollateralList) {
        this.pfCollateralList = pfCollateralList;
    }

    public double getInitialPFCollaterals() {
        return initialPFCollaterals;
    }

    public void setInitialPFCollaterals(double initialPFCollaterals) {
        this.initialPFCollaterals = initialPFCollaterals;
    }

    public List<CashAcc> getCashCollateralList() {
        return cashCollateralList;
    }

    public void setCashCollateralList(List<CashAcc> cashCollateralList) {
        this.cashCollateralList = cashCollateralList;
    }

    public double getCashCollateral() {
        return cashCollateral;
    }

    public void setCashCollateral(double cashCollateral) {
        this.cashCollateral = cashCollateral;
    }

    public double getTotalCollateralValue() {
        return totalCollateralValue;
    }

    public void setTotalCollateralValue(double totalCollateralValue) {
        this.totalCollateralValue = totalCollateralValue;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setIsSettled(boolean isSettled) {
        this.isSettled = isSettled;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(String settledDate) {
        this.settledDate = settledDate;
    }

    public String getPendingStatus() {
        return pendingStatus;
    }

    public void setPendingStatus(String pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public List<Status> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Status> statusList) {
        this.statusList = statusList;
    }

    public List<FtvSummary> getDailyFtvList() {
        return dailyFtvList;
    }

    public void setDailyFtvList(List<FtvSummary> dailyFtvList) {
        this.dailyFtvList = dailyFtvList;
    }
}
