package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.Installments;
import com.dfn.lsf.model.OrderProfit;

/**
 * Created by manodyas on 9/23/2015.
 */
public class SettlementSummaryResponse {
    private String applicationID;
    private String customerID;
    private String customerName;
    private String orderID;
    private double loanAmount;
    private double cumulativeProfit;
    private double totalSettlementAmount;
    private String settlementDate;
    private double availableCashBalance;
    private int settelmentStatus;
    private double loanProfit;
    private boolean isCustomerApproved;
    private List<Installments> installmentsList;
    private int lsfAccountDeletionState;
    private String tradingAccNumber;
    private int discountOnProfit;
    private OrderProfit orderProfit;
    private int productType;
    private int minRollOverPrd;
    private int maxRollOverPrd;
    private double minRollOverRatio;
    private int isInRollOverPrd;
    private String rollOverAppId;
    private double ftv;

    public int getDiscountOnProfit() {
        return discountOnProfit;
    }

    public void setDiscountOnProfit(int discountOnProfit) {
        this.discountOnProfit = discountOnProfit;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getCumulativeProfit() {
        return cumulativeProfit;
    }

    public void setCumulativeProfit(double cumulativeProfit) {
        this.cumulativeProfit = cumulativeProfit;
    }

    public double getTotalSettlementAmount() {
        return totalSettlementAmount;
    }

    public void setTotalSettlementAmount(double totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public double getAvailableCashBalance() {
        return availableCashBalance;
    }

    public void setAvailableCashBalance(double availableCashBalance) {
        this.availableCashBalance = availableCashBalance;
    }

    public int getSettelmentStatus() {
        return settelmentStatus;
    }

    public void setSettelmentStatus(int settelmentStatus) {
        this.settelmentStatus = settelmentStatus;
    }

    public double getLoanProfit() {
        return loanProfit;
    }

    public void setLoanProfit(double loanProfit) {
        this.loanProfit = loanProfit;
    }

    public boolean isCustomerApproved() {
        return isCustomerApproved;
    }

    public void setIsCustomerApproved(boolean isCustomerApproved) {
        this.isCustomerApproved = isCustomerApproved;
    }

    public List<Installments> getInstallmentsList() {
        return installmentsList;
    }

    public void setInstallmentsList(List<Installments> installmentsList) {
        this.installmentsList = installmentsList;
    }

    public int getLsfAccountDeletionState() {
        return lsfAccountDeletionState;
    }

    public void setLsfAccountDeletionState(int lsfAccountDeletionState) {
        this.lsfAccountDeletionState = lsfAccountDeletionState;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTradingAccNumber() {
        return tradingAccNumber;
    }

    public void setTradingAccNumber(String tradingAccNumber) {
        this.tradingAccNumber = tradingAccNumber;
    }

    public OrderProfit getOrderProfit() {
        return orderProfit;
    }

    public void setOrderProfit(OrderProfit orderProfit) {
        this.orderProfit = orderProfit;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public int getMinRollOverPrd() {
        return minRollOverPrd;
    }

    public void setMinRollOverPrd(int minRollOverPrd) {
        this.minRollOverPrd = minRollOverPrd;
    }

    public int getMaxRollOverPrd() {
        return maxRollOverPrd;
    }

    public void setMaxRollOverPrd(int maxRollOverPrd) {
        this.maxRollOverPrd = maxRollOverPrd;
    }

    public double getMinRollOverRatio() {
        return minRollOverRatio;
    }

    public void setMinRollOverRatio(double minRollOverRatio) {
        this.minRollOverRatio = minRollOverRatio;
    }

    public int getIsInRollOverPrd() {
        return isInRollOverPrd;
    }

    public void setIsInRollOverPrd(int isInRollOverPrd) {
        this.isInRollOverPrd = isInRollOverPrd;
    }

    public String getRollOverAppId() {
        return rollOverAppId;
    }

    public void setRollOverAppId(String rollOverAppId) {
        this.rollOverAppId = rollOverAppId;
    }

    public double getFtv() {
        return ftv;
    }

    public void setFtv(double ftv) {
        this.ftv = ftv;
    }
}
