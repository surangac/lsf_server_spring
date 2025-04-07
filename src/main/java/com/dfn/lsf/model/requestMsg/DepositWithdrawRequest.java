package com.dfn.lsf.model.requestMsg;

/**
 * Created by manodyas on 1/26/2016.
 */
public class DepositWithdrawRequest {
    private String reqType;
    private String referenceNo;
    private String purchaseOrderID;
    private int txType;
    private String fromBankAcc;
    private String toBankAcc;
    private double amount;
    private String currency;
    private String valueDate;
    private String bankId;
    private String payMethod;

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public void setPurchaseOrderID(String purchaseOrderID) {
        this.purchaseOrderID = purchaseOrderID;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public int getTxType() {
        return txType;
    }

    public void setTxType(int txType) {
        this.txType = txType;
    }

    public String getFromBankAcc() {
        return fromBankAcc;
    }

    public void setFromBankAcc(String fromBankAcc) {
        this.fromBankAcc = fromBankAcc;
    }

    public String getToBankAcc() {
        return toBankAcc;
    }

    public void setToBankAcc(String toBankAcc) {
        this.toBankAcc = toBankAcc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
