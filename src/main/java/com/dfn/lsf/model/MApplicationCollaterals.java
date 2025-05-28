package com.dfn.lsf.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MApplicationCollaterals {
    private String id;
    private String applicationId;
    private double netTotalColleteral;
    private double totalCashColleteral;
    private double totalExternalColleteral;
    private double totalPFColleteral;
    private double approvedLimitAmount;
    private double utilizedLimitAmount;
    private double opperativeLimitAmount;
    private double remainingOperativeLimitAmount;
    private double outstandingAmount; // this is unsettle amount, increases with PO placement and dicreases with settlement
    private List<TradingAcc> lsfTypeTradingAccounts;
    private List<CashAcc> lsfTypeCashAccounts;
    private List<TradingAcc> tradingAccForColleterals;
    private List<CashAcc> cashAccForColleterals;
    private List<ExternalCollaterals> externalCollaterals;
    private String updatedDate;
    private boolean readyForColleteralTransfer=false;
    private double ftv;
    private boolean firstMargineCall;
    private boolean secondMargineCall;
    private boolean liqudationCall;
    private String statusChangedBy;
    private String statusChangedDate;
    private int status;
    private int tenorID;
    private String statusMessage;
    private String ipAddress;
    private int margineCallAtempts;
    private double blockAmount;
    private int maximumNumberOfSymbols;
    private boolean allowInstalmentSettlement;
    private String margineCallDate;
    private String liquidateCallDate;
    private double totalPFMarketValue;
    private double totalWeightedPFValue;
    private List<Symbol> securityList;
    private List<BPSummary> buyingPowerSummary;
    private double initialCashCollaterals;
    private double initialPFCollaterals;
    private boolean isExchangeAccountCreated;
    private double adminFee;
    private double vatAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public double getTotalExternalColleteral() {
        return totalExternalColleteral;
    }

    public void setTotalExternalColleteral(double totalExternalColleteral) {
        this.totalExternalColleteral = totalExternalColleteral;
    }

    public double getNetTotalColleteral() {
        return netTotalColleteral;
    }

    public void setNetTotalColleteral(double netTotalColleteral) {
        this.netTotalColleteral = netTotalColleteral;
    }

    public List<TradingAcc> getTradingAccForColleterals() {
        return tradingAccForColleterals;
    }

    public void setTradingAccForColleterals(List<TradingAcc> tradingAccForColleterals) {
        this.tradingAccForColleterals = tradingAccForColleterals;
    }

    public List<CashAcc> getCashAccForColleterals() {
        return cashAccForColleterals;
    }

    public List<ExternalCollaterals> getExternalCollaterals() {
        return externalCollaterals;
    }

    public void setExternalCollaterals(List<ExternalCollaterals> externalCollateralses) {
        this.externalCollaterals = externalCollateralses;
    }

    public void setCashAccForColleterals(List<CashAcc> cashAccForColleterals) {
        this.cashAccForColleterals = cashAccForColleterals;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isLSFCashAccExist(){
        if(this.lsfTypeCashAccounts==null)
            return false;
        return this.lsfTypeCashAccounts.size() > 0;
    }
    public boolean isLSFTradingAccExist(){
        if(this.lsfTypeTradingAccounts==null)
            return false;
        return this.lsfTypeTradingAccounts.size() > 0;
    }

    public CashAcc isCashAccLSFTypeExist(String cashAccId){
        if(this.lsfTypeCashAccounts==null){
            this.lsfTypeCashAccounts=new ArrayList<>();
        }
        for(CashAcc csh:this.lsfTypeCashAccounts){
            if(csh.getAccountId().equals(cashAccId)){
                return  csh;
            }
        }
        CashAcc cashAcc= CashAcc.builder().build();
        cashAcc.setAccountId(cashAccId);
        this.lsfTypeCashAccounts.add(cashAcc);
        return cashAcc;
    }

    public CashAcc isCashAccExist(String cashAccId){
        if(this.cashAccForColleterals==null){
            this.cashAccForColleterals=new ArrayList<>();
        }
        for(CashAcc csh:this.cashAccForColleterals){
            if(csh.getAccountId().equals(cashAccId)){
                return  csh;
            }
        }
        CashAcc cashAcc= CashAcc.builder().build();
        cashAcc.setAccountId(cashAccId);
        this.cashAccForColleterals.add(cashAcc);
        return cashAcc;
    }

    public TradingAcc isTradingAccountExist(String tradingAccId){
        if(this.tradingAccForColleterals==null){
            this.tradingAccForColleterals=new ArrayList<>();
        }
        for(TradingAcc dt:this.tradingAccForColleterals){
            if(dt.getAccountId().equals(tradingAccId))
                return  dt;
        }
        TradingAcc tradingAcc=new TradingAcc();
        tradingAcc.setAccountId(tradingAccId);
        this.tradingAccForColleterals.add(tradingAcc);
        return tradingAcc;
    }

    public TradingAcc isTradingAccountLSFTypeExist(String tradingAccId){
        if(this.lsfTypeTradingAccounts==null){
            this.lsfTypeTradingAccounts=new ArrayList<>();
        }
        for(TradingAcc dt:this.lsfTypeTradingAccounts){
            if(dt.getAccountId().equals(tradingAccId))
                return  dt;
        }
        TradingAcc tradingAcc=new TradingAcc();
        tradingAcc.setAccountId(tradingAccId);
        this.lsfTypeTradingAccounts.add(tradingAcc);
        return tradingAcc;
    }

    public double getApprovedLimitAmount() {
        return approvedLimitAmount;
    }

    public void setApprovedLimitAmount(double approvedLimitAmount) {
        this.approvedLimitAmount = approvedLimitAmount;
    }

    public double getUtilizedLimitAmount() {
        return utilizedLimitAmount;
    }

    public void setUtilizedLimitAmount(double utilizedLimitAmount) {
        this.utilizedLimitAmount = utilizedLimitAmount;
    }

    public double getOpperativeLimitAmount() {
        return opperativeLimitAmount;
    }

    public void setOpperativeLimitAmount(double opperativeLimitAmount) {
        this.opperativeLimitAmount = opperativeLimitAmount;
    }

    public List<TradingAcc> getLsfTypeTradingAccounts() {
        return lsfTypeTradingAccounts;
    }

    public void setLsfTypeTradingAccounts(List<TradingAcc> lsfTypeTradingAccounts) {
        this.lsfTypeTradingAccounts = lsfTypeTradingAccounts;
    }

    public List<CashAcc> getLsfTypeCashAccounts() {
        return lsfTypeCashAccounts;
    }

    public void setLsfTypeCashAccounts(List<CashAcc> lsfTypeCashAccounts) {
        this.lsfTypeCashAccounts = lsfTypeCashAccounts;
    }

    public boolean isReadyForColleteralTransfer() {
        return readyForColleteralTransfer;
    }

    public void setReadyForColleteralTransfer(boolean readyForColleteralTransfer) {
        this.readyForColleteralTransfer = readyForColleteralTransfer;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public double getRemainingOperativeLimitAmount() {
        return remainingOperativeLimitAmount;
    }

    public void setRemainingOperativeLimitAmount(double remainingOperativeLimitAmount) {
        this.remainingOperativeLimitAmount = remainingOperativeLimitAmount;
    }

    public double getFtv() {
        return ftv;
    }

    public void setFtv(double ftv) {
        this.ftv = ftv;
    }

    public boolean isFirstMargineCall() {
        return firstMargineCall;
    }

    public void setFirstMargineCall(boolean firstMargineCall) {
        this.firstMargineCall = firstMargineCall;
    }

    public boolean isSecondMargineCall() {
        return secondMargineCall;
    }

    public void setSecondMargineCall(boolean secondMargineCall) {
        this.secondMargineCall = secondMargineCall;
    }

    public boolean isLiqudationCall() {
        return liqudationCall;
    }

    public void setLiqudationCall(boolean liqudationCall) {
        this.liqudationCall = liqudationCall;
    }

    public double getTotalCashColleteral() {
        return totalCashColleteral;
    }

    public void setTotalCashColleteral(double totalCashColleteral) {
        this.totalCashColleteral = totalCashColleteral;
    }

    public double getTotalPFColleteral() {
        return totalPFColleteral;
    }

    public void setTotalPFColleteral(double totalPFColleteral) {
        this.totalPFColleteral = totalPFColleteral;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof MApplicationCollaterals)
        {
            sameSame = this.getApplicationId().equals(((MApplicationCollaterals)object).getApplicationId());
        }
        return sameSame;
    }

    public String getStatusChangedBy() {
        return statusChangedBy;
    }

    public void setStatusChangedBy(String statusChangedBy) {
        this.statusChangedBy = statusChangedBy;
    }

    public String getStatusChangedDate() {
        return statusChangedDate;
    }

    public void setStatusChangedDate(String statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTenorID() {
        return tenorID;
    }

    public void setTenorID(int tenorID) {
        this.tenorID = tenorID;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getMargineCallAtempts() {
        return margineCallAtempts;
    }

    public void setMargineCallAtempts(int margineCallAtempts) {
        this.margineCallAtempts = margineCallAtempts;
    }

    public double getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(double blockAmount) {
        this.blockAmount = blockAmount;
    }

    public int getMaximumNumberOfSymbols() {
        return maximumNumberOfSymbols;
    }

    public void setMaximumNumberOfSymbols(int maximumNumberOfSymbols) {
        this.maximumNumberOfSymbols = maximumNumberOfSymbols;
    }

    public boolean isAllowInstalmentSettlement() {
        return allowInstalmentSettlement;
    }

    public void setAllowInstalmentSettlement(boolean allowInstalmentSettlement) {
        this.allowInstalmentSettlement = allowInstalmentSettlement;
    }

    public String getMargineCallDate() {
        return margineCallDate;
    }

    public void setMargineCallDate(String margineCallDate) {
        this.margineCallDate = margineCallDate;
    }

    public String getLiquidateCallDate() {
        return liquidateCallDate;
    }

    public void setLiquidateCallDate(String liquidateCallDate) {
        this.liquidateCallDate = liquidateCallDate;
    }

    public double getTotalPFMarketValue() {
        return totalPFMarketValue;
    }

    public void setTotalPFMarketValue(double totalPFMarketValue) {
        this.totalPFMarketValue = totalPFMarketValue;
    }

    public double getTotalWeightedPFValue() {
        return totalWeightedPFValue;
    }

    public void setTotalWeightedPFValue(double totalWeightedPFValue) {
        this.totalWeightedPFValue = totalWeightedPFValue;
    }

    public List<Symbol> getSecurityList() {
        return securityList;
    }

    public void setSecurityList(List<Symbol> securityList) {
        this.securityList = securityList;
    }

    public List<BPSummary> getBuyingPowerSummary() {
        return buyingPowerSummary;
    }

    public void setBuyingPowerSummary(List<BPSummary> buyingPowerSummary) {
        this.buyingPowerSummary = buyingPowerSummary;
    }

    public double getInitialCashCollaterals() {
        return initialCashCollaterals;
    }

    public void setInitialCashCollaterals(double initialCashCollaterals) {
        this.initialCashCollaterals = initialCashCollaterals;
    }

    public double getInitialPFCollaterals() {
        return initialPFCollaterals;
    }

    public void setInitialPFCollaterals(double initialPFCollaterals) {
        this.initialPFCollaterals = initialPFCollaterals;
    }

    public boolean isExchangeAccountCreated() {
        return isExchangeAccountCreated;
    }

    public void setIsExchangeAccountCreated(boolean isExchangeAccountCreated) {
        this.isExchangeAccountCreated = isExchangeAccountCreated;
    }

    public double getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(double adminFee) {
        this.adminFee = adminFee;
    }

    public double getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(double vatAmount) {
        this.vatAmount = vatAmount;
    }
}
