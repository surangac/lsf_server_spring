package com.dfn.lsf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surangac on 4/2/2015.
 */
public class MurabahApplication extends MessageHeader {
    private String id;
    private String customerId;
    private String fullName;
    private String occupation;
    private String employer;
    private boolean isSelfEmp;
    private String lineOfBusiness;
    private double avgMonthlyIncome;
    private double financeRequiredAmt;
    private String tenor;
    private String address;
    private String mobileNo;
    private String teleNo;
    private String email;
    private String fax;
    private String dibAcc;
    private String tradingAcc;
    private boolean isOtherBrkAvailable;
    private String otherBrkNames;
    private String otherBrkAvgPf;
    private String overallStatus;
    private int currentLevel;
    private String typeofFacility;
    private String date;
    private String facilityType;
    private String proposalDate;
    private double proposedLimit;
    private List<Status> appStatus;
    private List<Symbol> pflist;
    private String marginabilityGroup;
    private String stockConcentrationGroup;
    private String reversedFrom;
    private String reversedTo;
    private boolean isEditable;
    private boolean isReversed;
    private boolean isEdited;
    private double initialRAPV;
    private List<Comment> commentList;
    private int documentUploadedStatus;
    private double availableCashBalance;
    private String tradingAccExchange;
    private String reviewDate;
    private double adminFeeCharged;
    private int maximumNumberOfSymbols;
    private String otp;
    private long otpGeneratedTime;
    private String cashAccount;
    private String customerReferenceNumber;
    private String zipCode;
    private String bankBranchName;
    private String city;
    private String poBox;
    private int customerActivityID;
    private int purchaseOrderId;
    private String statusDescription;
    private String preferedLanguage;
    private int discountOnProfit=1;
    private double profitPercentage;
    private int automaticSettlementAllow=1;
    private String mlPortfolioNo = null;
    private int productType = 0;
    private String financeMethod = null;
    private List<Agreement> agreementList = null;
    private String rollOverAppId = null;
    private List<PurchaseOrder> purchaseOrderList = null;
    private String institutionInvestAccount = null;
    private String investorAcc;
    private RemainTime remainTimeToSell;
    private int rollOverSeqNumber;
    private boolean automaticSettlement;
    private String lastProfitDate;
    private int lsfAccountDeletionState;
    private String additionalDetails;
    private String additionalDocName;
    private String additionalDocPath;
    private String facilityTransferStatus;

    public String getAdditionalDetails() {
        return additionalDetails;
    }
    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
    public String getAdditionalDocName() {
        return additionalDocName;
    }
    public void setAdditionalDocName(String additionalDocName) {
        this.additionalDocName = additionalDocName;
    }
    public String getAdditionalDocPath() {
        return additionalDocPath;
    }
    public void setAdditionalDocPath(String additionalDocPath) {
        this.additionalDocPath = additionalDocPath;
    }

    public String getMlPortfolioNo() {
        return mlPortfolioNo;
    }

    public void setMlPortfolioNo(String mlPortfolioNo) {
        this.mlPortfolioNo = mlPortfolioNo;
    }

    public String getMarginabilityGroup() {
        return marginabilityGroup;
    }

    public void setMarginabilityGroup(String marginabilityGroup) {
        this.marginabilityGroup = marginabilityGroup;
    }

    public String getStockConcentrationGroup() {
        return stockConcentrationGroup;
    }

    public void setStockConcentrationGroup(String stockConcentrationGroup) {
        this.stockConcentrationGroup = stockConcentrationGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelfEmp() {
        return isSelfEmp;
    }

    public void setSelfEmp(boolean isSelfEmp) {
        this.isSelfEmp = isSelfEmp;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getTypeofFacility() {
        return typeofFacility;
    }

    public void setTypeofFacility(String typeofFacility) {
        this.typeofFacility = typeofFacility;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(String proposalDate) {
        this.proposalDate = proposalDate;
    }

    public double getProposedLimit() {
        return proposedLimit;
    }

    public void setProposedLimit(double proposedLimit) {
        this.proposedLimit = proposedLimit;
    }

    public void addNewStatus(Status newStatus) {

        if (this.appStatus == null)
            this.appStatus = new ArrayList<Status>();
        this.appStatus.add(newStatus);
    }

    public List<Status> getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(List<Status> appStatus) {
        this.appStatus = appStatus;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getLineOfBusiness() {
        return lineOfBusiness;
    }

    public void setLineOfBusiness(String lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public double getAvgMonthlyIncome() {
        return avgMonthlyIncome;
    }

    public void setAvgMonthlyIncome(double avgMonthlyIncome) {
        this.avgMonthlyIncome = avgMonthlyIncome;
    }

    public double getFinanceRequiredAmt() {
        return financeRequiredAmt;
    }

    public void setFinanceRequiredAmt(double financeRequiredAmt) {
        this.financeRequiredAmt = financeRequiredAmt;
    }

    public String getTenor() {
        return tenor;
    }

    public void setTenor(String tenor) {
        this.tenor = tenor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTeleNo() {
        return teleNo;
    }

    public void setTeleNo(String teleNo) {
        this.teleNo = teleNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getDibAcc() {
        return dibAcc;
    }

    public void setDibAcc(String dibAcc) {
        this.dibAcc = dibAcc;
    }

    public String getTradingAcc() {
        return tradingAcc;
    }

    public void setTradingAcc(String tradingAcc) {
        this.tradingAcc = tradingAcc;
    }

    public boolean isOtherBrkAvailable() {
        return isOtherBrkAvailable;
    }

    public void setOtherBrkAvailable(boolean isOtherBrkAvailable) {
        this.isOtherBrkAvailable = isOtherBrkAvailable;
    }

    public String getOtherBrkNames() {
        return otherBrkNames;
    }

    public void setOtherBrkNames(String otherBrkNames) {
        this.otherBrkNames = otherBrkNames;
    }

    public String getOtherBrkAvgPf() {
        return otherBrkAvgPf;
    }

    public void setOtherBrkAvgPf(String otherBrkAvgPf) {
        this.otherBrkAvgPf = otherBrkAvgPf;
    }

    public List<Symbol> getPflist() {
        return pflist;
    }

    public void setPflist(List<Symbol> pflist) {
        this.pflist = pflist;
    }

    public String getReversedFrom() {
        return reversedFrom;
    }

    public void setReversedFrom(String reversedFrom) {
        this.reversedFrom = reversedFrom;
    }

    public String getReversedTo() {
        return reversedTo;
    }

    public void setReversedTo(String reversedTo) {
        this.reversedTo = reversedTo;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void setIsReversed(boolean isReversed) {
        this.isReversed = isReversed;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public double getInitialRAPV() {
        return initialRAPV;
    }

    public void setInitialRAPV(double initialRAPV) {
        this.initialRAPV = initialRAPV;
    }

    public int getDocumentUploadedStatus() {
        return documentUploadedStatus;
    }

    public void setDocumentUploadedStatus(int documentUploadedStatus) {
        this.documentUploadedStatus = documentUploadedStatus;
    }

    public double getAvailableCashBalance() {
        return availableCashBalance;
    }

    public void setAvailableCashBalance(double availableCashBalance) {
        this.availableCashBalance = availableCashBalance;
    }

    public String getTradingAccExchange() {
        return tradingAccExchange;
    }

    public void setTradingAccExchange(String tradingAccExchange) {
        this.tradingAccExchange = tradingAccExchange;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public double getAdminFeeCharged() {
        return adminFeeCharged;
    }

    public void setAdminFeeCharged(double adminFeeCharged) {
        this.adminFeeCharged = adminFeeCharged;
    }

    public int getMaximumNumberOfSymbols() {
        return maximumNumberOfSymbols;
    }

    public void setMaximumNumberOfSymbols(int maximumNumberOfSymbols) {
        this.maximumNumberOfSymbols = maximumNumberOfSymbols;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public long getOtpGeneratedTime() {
        return otpGeneratedTime;
    }

    public void setOtpGeneratedTime(long otpGeneratedTime) {
        this.otpGeneratedTime = otpGeneratedTime;
    }

    public String getCashAccount() {
        return cashAccount;
    }

    public void setCashAccount(String cashAccount) {
        this.cashAccount = cashAccount;
    }

    public String getCustomerReferenceNumber() {
        return customerReferenceNumber;
    }

    public void setCustomerReferenceNumber(String customerReferenceNumber) {
        this.customerReferenceNumber = customerReferenceNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public int getCustomerActivityID() {
        return customerActivityID;
    }

    public void setCustomerActivityID(int customerActivityID) {
        this.customerActivityID = customerActivityID;
    }

    public int getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(int purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getPreferedLanguage() {
        return preferedLanguage;
    }

    public void setPreferedLanguage(String preferedLanguage) {
        this.preferedLanguage = preferedLanguage;
    }

    public int getDiscountOnProfit() {
        return discountOnProfit;
    }

    public void setDiscountOnProfit(int discountOnProfit) {
        this.discountOnProfit = discountOnProfit;
    }

    public double getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(double profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public int getAutomaticSettlementAllow() {
        return automaticSettlementAllow;
    }

    public void setAutomaticSettlementAllow(int automaticSettlementAllow) {
        this.automaticSettlementAllow = automaticSettlementAllow;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public String getFinanceMethod() {
        return financeMethod;
    }

    public void setFinanceMethod(String financeMethod) {
        this.financeMethod = financeMethod;
    }

    public List<Agreement> getAgreementList() {
        return agreementList;
    }

    public void setAgreementList(List<Agreement> agreementList) {
        this.agreementList = agreementList;
    }

    public String getRollOverAppId() {
        return rollOverAppId;
    }

    public void setRollOverAppId(String rollOverAppId) {
        this.rollOverAppId = rollOverAppId;
    }

    public List<PurchaseOrder> getPurchaseOrderList() {
        return purchaseOrderList;
    }

    public void setPurchaseOrderList(List<PurchaseOrder> purchaseOrderList) {
        this.purchaseOrderList = purchaseOrderList;
    }

    public String getInstitutionInvestAccount() {
        return institutionInvestAccount;
    }

    public void setInstitutionInvestAccount(String institutionInvestAccount) {
        this.institutionInvestAccount = institutionInvestAccount;
    }

    public String getInvestorAcc() {
        return investorAcc;
    }

    public void setInvestorAcc(String investorAcc) {
        this.investorAcc = investorAcc;
    }

    public RemainTime getRemainTimeToSell() {
        return remainTimeToSell;
    }

    public void setRemainTimeToSell(RemainTime remainTimeToSell) {
        this.remainTimeToSell = remainTimeToSell;
    }

    public int getRollOverSeqNumber() {
        return rollOverSeqNumber;
    }

    public void setRollOverSeqNumber(int rollOverSeqNumber) {
        this.rollOverSeqNumber = rollOverSeqNumber;
    }

    public void setAutomaticSettlement(boolean automaticSettlement) {
        this.automaticSettlement = automaticSettlement;
    }

    public void setLastProfitDate(String lastProfitDate) {
        this.lastProfitDate = lastProfitDate;
    }

    public int getLsfAccountDeletionState() {
        return lsfAccountDeletionState;
    }

    public void setLsfAccountDeletionState(int lsfAccountDeletionState) {
        this.lsfAccountDeletionState = lsfAccountDeletionState;
    }

    public boolean isRollOverApp() {
        return rollOverAppId != null && !rollOverAppId.equals("-1") && rollOverSeqNumber > 0;
    }

    public String displayApplicationId() {
        return isRollOverApp() ? id + "R" + rollOverSeqNumber : id;
    }

    public String getFacilityTransferStatus() {
        return facilityTransferStatus;
    }
    public void setFacilityTransferStatus(String facilityTransferStatus) {
        this.facilityTransferStatus = facilityTransferStatus;
    }
}