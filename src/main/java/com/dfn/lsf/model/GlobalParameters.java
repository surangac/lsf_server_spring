package com.dfn.lsf.model;


import lombok.Getter;
import lombok.Setter;

/**
 * Created by Atchuthan on 5/27/2015.
 */
@Getter
@Setter
public class GlobalParameters {
    private static GlobalParameters _instance;

    private GlobalParameters() {
    }

    public static void reset(GlobalParameters globalParameters) {
        _instance = globalParameters;
    }

    public static GlobalParameters getInstance() {
        if (_instance == null) {
            _instance = new GlobalParameters();
        }
        return _instance;
    }

    private Long maxGuidanceLimit;
    private int minGuidanceLimit;
    private Double ftvForOperativeLimit;
    private Boolean allowInstalmentSettlement;
    private Boolean operatingLimitType;
    private Double scriptMaxContribution;
    private int noOfDaysPriorNotifyReviewDate;
    private Boolean shariaSymbolsAsCollateral;
    private Double documentationFeePercentage;
    private Boolean utilizeCustomerCashFirst;
    private Double firstMarginCall;
    private Double secondMarginCall;
    private Double liquidationCall;
    private Boolean preFunded;
    private int noOfCallingAttemptsPerDay;
    private int noOfDaysPriorRemindingThePayment;
    private int noOfDaysWaitsBeforeLiquidation;
    private int symbolReValuationInterval;
    private Boolean priorityCashACForSettlement;
    private int alertCustomerPriorToFDExpiry;
    private Boolean murabahaOTP;
    private int noOfMarginCallsPerDay;
    private String baseCurrency;
    private int profitCalculateMethode;
    private String clientCode;
    private int sendOrderApprovalFirst;
    private int orderAccPriority;
    private String institutionTradingAcc;
    private String defaultExchange;
    private double administrationFee;
    private int numberOfDecimalPlaces;
    private double timeGapBetweenCallingAttempts; // from hrs
    private boolean isMultipleOrderAllowed;
    private double minimumOrderValue;
    private String institutionCashAccount;
    private double administrationFeePercent;
    private int publicAccessEnabled;
    private int maximumNumberOfSymbols;
    private double colletralToMarginPercentage;
    private String marketOpenTime;
    private String marketClosedTime;
    private  double simaCharges;
    private double transferCharges;
    private int maximumRetryCount;
    private int agreedLimit;
    private int getAppCloseLevel;
    private Long maxBrokerageLimit;
    private double vatPercentage;
    private int maxNumberOfActiveContracts;
    private String settlementClTimerString;
    private double comodityAdminFee;
    private double shareAdminFee;
    private double comodityFixedFee;
    private double shareFixedFee;
    private int minRolloverRatio;
    private int minRolloverPeriod;
    private int maxRolloverPeriod;
    private int gracePeriodforCommoditySell;
    private String institutionInvestAccount;
    public Long getMaxGuidanceLimit() {
        return maxGuidanceLimit;
    }

    public void setMaxGuidanceLimit(Long maxGuidanceLimit) {
        this.maxGuidanceLimit = maxGuidanceLimit;
    }

    public int getMinGuidanceLimit() {
        return minGuidanceLimit;
    }

    public void setMinGuidanceLimit(int minGuidanceLimit) {
        this.minGuidanceLimit = minGuidanceLimit;
    }

    public Double getFtvForOperativeLimit() {
        return ftvForOperativeLimit;
    }

    public void setFtvForOperativeLimit(Double ftvForOperativeLimit) {
        this.ftvForOperativeLimit = ftvForOperativeLimit;
    }

    public Boolean getAllowInstalmentSettlement() {
        return allowInstalmentSettlement;
    }

    public void setAllowInstalmentSettlement(Boolean allowInstalmentSettlement) {
        this.allowInstalmentSettlement = allowInstalmentSettlement;
    }

    public Boolean getOperatingLimitType() {
        return operatingLimitType;
    }

    public void setOperatingLimitType(Boolean operatingLimitType) {
        this.operatingLimitType = operatingLimitType;
    }

    public Double getScriptMaxContribution() {
        return scriptMaxContribution;
    }

    public void setScriptMaxContribution(Double scriptMaxContribution) {
        this.scriptMaxContribution = scriptMaxContribution;
    }

    public int getNoOfDaysPriorNotifyReviewDate() {
        return noOfDaysPriorNotifyReviewDate;
    }

    public void setNoOfDaysPriorNotifyReviewDate(int noOfDaysPriorNotifyReviewDate) {
        this.noOfDaysPriorNotifyReviewDate = noOfDaysPriorNotifyReviewDate;
    }

    public Boolean getShariaSymbolsAsCollateral() {
        return shariaSymbolsAsCollateral;
    }

    public void setShariaSymbolsAsCollateral(Boolean shariaSymbolsAsCollateral) {
        this.shariaSymbolsAsCollateral = shariaSymbolsAsCollateral;
    }

    public Double getDocumentationFeePercentage() {
        return documentationFeePercentage;
    }

    public void setDocumentationFeePercentage(Double documentationFeePercentage) {
        this.documentationFeePercentage = documentationFeePercentage;
    }

    public Boolean getUtilizeCustomerCashFirst() {
        return utilizeCustomerCashFirst;
    }

    public void setUtilizeCustomerCashFirst(Boolean utilizeCustomerCashFirst) {
        this.utilizeCustomerCashFirst = utilizeCustomerCashFirst;
    }

    public Double getFirstMarginCall() {
        return firstMarginCall;
    }

    public void setFirstMarginCall(Double firstMarginCall) {
        this.firstMarginCall = firstMarginCall;
    }

    public Double getSecondMarginCall() {
        return secondMarginCall;
    }

    public void setSecondMarginCall(Double secondMarginCall) {
        this.secondMarginCall = secondMarginCall;
    }

    public Double getLiquidationCall() {
        return liquidationCall;
    }

    public void setLiquidationCall(Double liquidationCall) {
        this.liquidationCall = liquidationCall;
    }

    public Boolean getPreFunded() {
        return preFunded;
    }

    public void setPreFunded(Boolean preFunded) {
        this.preFunded = preFunded;
    }

    public int getNoOfCallingAttemptsPerDay() {
        return noOfCallingAttemptsPerDay;
    }

    public void setNoOfCallingAttemptsPerDay(int noOfCallingAttemptsPerDay) {
        this.noOfCallingAttemptsPerDay = noOfCallingAttemptsPerDay;
    }

    public int getNoOfDaysPriorRemindingThePayment() {
        return noOfDaysPriorRemindingThePayment;
    }

    public void setNoOfDaysPriorRemindingThePayment(int noOfDaysPriorRemindingThePayment) {
        this.noOfDaysPriorRemindingThePayment = noOfDaysPriorRemindingThePayment;
    }

    public int getNoOfDaysWaitsBeforeLiquidation() {
        return noOfDaysWaitsBeforeLiquidation;
    }

    public void setNoOfDaysWaitsBeforeLiquidation(int noOfDaysWaitsBeforeLiquidation) {
        this.noOfDaysWaitsBeforeLiquidation = noOfDaysWaitsBeforeLiquidation;
    }

    public int getSymbolReValuationInterval() {
        return symbolReValuationInterval;
    }

    public void setSymbolReValuationInterval(int symbolReValuationInterval) {
        this.symbolReValuationInterval = symbolReValuationInterval;
    }

    public Boolean getPriorityCashACForSettlement() {
        return priorityCashACForSettlement;
    }

    public void setPriorityCashACForSettlement(Boolean priorityCashACForSettlement) {
        this.priorityCashACForSettlement = priorityCashACForSettlement;
    }

    public int getAlertCustomerPriorToFDExpiry() {
        return alertCustomerPriorToFDExpiry;
    }

    public void setAlertCustomerPriorToFDExpiry(int alertCustomerPriorToFDExpiry) {
        this.alertCustomerPriorToFDExpiry = alertCustomerPriorToFDExpiry;
    }

    public Boolean getMurabahaOTP() {
        return murabahaOTP;
    }

    public void setMurabahaOTP(Boolean murabahaOTP) {
        this.murabahaOTP = murabahaOTP;
    }

    public int getNoOfMarginCallsPerDay() {
        return noOfMarginCallsPerDay;
    }

    public void setNoOfMarginCallsPerDay(int noOfMarginCallsPerDay) {
        this.noOfMarginCallsPerDay = noOfMarginCallsPerDay;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public int getProfitCalculateMethode() {
        return profitCalculateMethode;
    }

    public void setProfitCalculateMethode(int profitCalculateMethode) {
        this.profitCalculateMethode = profitCalculateMethode;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public int getSendOrderApprovalFirst() {
        return sendOrderApprovalFirst;
    }

    public void setSendOrderApprovalFirst(int sendOrderApprovalFirst) {
        this.sendOrderApprovalFirst = sendOrderApprovalFirst;
    }

    public int getOrderAccPriority() {
        return orderAccPriority;
    }

    public void setOrderAccPriority(int orderAccPriority) {
        this.orderAccPriority = orderAccPriority;
    }

    public String getInstitutionTradingAcc() {
        return institutionTradingAcc;
    }

    public void setInstitutionTradingAcc(String institutionTradingAcc) {
        this.institutionTradingAcc = institutionTradingAcc;
    }

    public String getDefaultExchange() {
        return defaultExchange;
    }

    public void setDefaultExchange(String defaultExchange) {
        this.defaultExchange = defaultExchange;
    }

    public double getAdministrationFee() {
        return administrationFee;
    }

    public void setAdministrationFee(double administrationFee) {
        this.administrationFee = administrationFee;
    }

    public int getNumberOfDecimalPlaces() {
        return numberOfDecimalPlaces;
    }

    public void setNumberOfDecimalPlaces(int numberOfDecimalPlaces) {
        this.numberOfDecimalPlaces = numberOfDecimalPlaces;
    }

    public double getTimeGapBetweenCallingAttempts() {
        return timeGapBetweenCallingAttempts;
    }

    public void setTimeGapBetweenCallingAttempts(double timeGapBetweenCallingAttempts) {
        this.timeGapBetweenCallingAttempts = timeGapBetweenCallingAttempts;
    }

    public boolean isMultipleOrderAllowed() {
        return isMultipleOrderAllowed;
    }

    public void setIsMultipleOrderAllowed(boolean isMultipleOrderAllowed) {
        this.isMultipleOrderAllowed = isMultipleOrderAllowed;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public String getInstitutionCashAccount() {
        return institutionCashAccount;
    }

    public void setInstitutionCashAccount(String institutionCashAccount) {
        this.institutionCashAccount = institutionCashAccount;
    }

    public double getAdministrationFeePercent() {
        return administrationFeePercent;
    }

    public void setAdministrationFeePercent(double administrationFeePercent) {
        this.administrationFeePercent = administrationFeePercent;
    }

    public int getPublicAccessEnabled() {
        return publicAccessEnabled;
    }

    public void setPublicAccessEnabled(int publicAccessEnabled) {
        this.publicAccessEnabled = publicAccessEnabled;
    }

    public int getMaximumNumberOfSymbols() {
        return maximumNumberOfSymbols;
    }

    public void setMaximumNumberOfSymbols(int maximumNumberOfSymbols) {
        this.maximumNumberOfSymbols = maximumNumberOfSymbols;
    }

    public double getColletralToMarginPercentage() {
        return colletralToMarginPercentage;
    }

    public void setColletralToMarginPercentage(double colletralToMarginPercentage) {
        this.colletralToMarginPercentage = colletralToMarginPercentage;
    }

    public String getMarketOpenTime() {
        return marketOpenTime;
    }

    public void setMarketOpenTime(String marketOpenTime) {
        this.marketOpenTime = marketOpenTime;
    }

    public String getMarketClosedTime() {
        return marketClosedTime;
    }

    public void setMarketClosedTime(String marketClosedTime) {
        this.marketClosedTime = marketClosedTime;
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

    public int getMaximumRetryCount() {
        return maximumRetryCount;
    }

    public void setMaximumRetryCount(int maximumRetryCount) {
        this.maximumRetryCount = maximumRetryCount;
    }

    public int getAgreedLimit() {
        return agreedLimit;
    }

    public void setAgreedLimit(int agreedLimit) {
        this.agreedLimit = agreedLimit;
    }

    public int getGetAppCloseLevel() {
        return getAppCloseLevel;
    }

    public void setGetAppCloseLevel(int getAppCloseLevel) {
        this.getAppCloseLevel = getAppCloseLevel;
    }

    public Long getMaxBrokerageLimit() {
        return maxBrokerageLimit;
    }

    public void setMaxBrokerageLimit(Long maxBrokerageLimit) {
        this.maxBrokerageLimit = maxBrokerageLimit;
    }

    public double getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(double vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public int getMaxNumberOfActiveContracts() {
        return maxNumberOfActiveContracts;
    }

    public void setMaxNumberOfActiveContracts(int maxNumberOfActiveContracts) {
        this.maxNumberOfActiveContracts = maxNumberOfActiveContracts;
    }


    public String getSettlementClTimerString() {
        return settlementClTimerString;
    }

    public void setSettlementClTimerString(String settlementClTimerString) {
        this.settlementClTimerString = settlementClTimerString;
    }

    public double getComodityAdminFee() {
        return comodityAdminFee;
    }

    public void setComodityAdminFee(double comodityAdminFee) {
        this.comodityAdminFee = comodityAdminFee;
    }

    public double getShareAdminFee() {
        return shareAdminFee;
    }

    public void setShareAdminFee(double shareAdminFee) {
        this.shareAdminFee = shareAdminFee;
    }

    public double getComodityFixedFee() {
        return comodityFixedFee;
    }

    public void setComodityFixedFee(double comodityFixedFee) {
        this.comodityFixedFee = comodityFixedFee;
    }

    public double getShareFixedFee() {
        return shareFixedFee;
    }

    public void setShareFixedFee(double shareFixedFee) {
        this.shareFixedFee = shareFixedFee;
    }

    public int getMinRolloverRatio() {
        return minRolloverRatio;
    }

    public void setMinRolloverRatio(int minRolloverRatio) {
        this.minRolloverRatio = minRolloverRatio;
    }

    public int getMinRolloverPeriod() {
        return minRolloverPeriod;
    }

    public void setMinRolloverPeriod(int minRolloverPeriod) {
        this.minRolloverPeriod = minRolloverPeriod;
    }

    public int getMaxRolloverPeriod() {
        return maxRolloverPeriod;
    }

    public void setMaxRolloverPeriod(int maxRolloverPeriod) {
        this.maxRolloverPeriod = maxRolloverPeriod;
    }

    public int getGracePeriodforCommoditySell() {
        return gracePeriodforCommoditySell;
    }

    public void setGracePeriodforCommoditySell(int gracePeriodforCommoditySell) {
        this.gracePeriodforCommoditySell = gracePeriodforCommoditySell;
    }

    public String getInstitutionInvestAccount() {
        return institutionInvestAccount;
    }

    public void setInstitutionInvestAccount(String institutionInvestAccount) {
        this.institutionInvestAccount = institutionInvestAccount;
    }
}
