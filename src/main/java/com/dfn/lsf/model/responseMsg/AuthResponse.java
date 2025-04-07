package com.dfn.lsf.model.responseMsg;

import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.MurabahaProduct;

import java.util.List;

/**
 * Created by surangac on 6/16/2015.
 */
public class AuthResponse {
    private String customerId;
    private String userName;
    private List<MurabahApplication> applicationList;
    private String sessionId;
    private String lastLoginTime;
    private int currentLevel;
    private int overallStatus;
    private String securityKey;
    private int isFirstTime;
    private int previousApplicationStatus;
    private String previousStateDescription;
    private String corellationID;
    private String defaultCurrency;
    private int numberOfDecimalPlaces;
    private boolean isMultipleOrderAllowed;
    private double minimumOrderValue;
    private double minLoanLimit;
    private double maxLoanLimit;
    private List<RiskwavierQuestionConfig> questionConfigList;
    private boolean isOTPEnabled;
    private int maximumRetryCount;
    private int currentActivityCode;
    private int noOfPendingContracts;
    private int noOfGrantedContracts;
    private int maxNoOfGrantedContracts;
    private List<MurabahaProduct> productsList;


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<MurabahApplication> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<MurabahApplication> applicationList) {
        this.applicationList = applicationList;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(int overallStatus) {
        this.overallStatus = overallStatus;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public int getIsFirstTime() {
        return isFirstTime;
    }

    public void setIsFirstTime(int isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    public int getPreviousApplicationStatus() {
        return previousApplicationStatus;
    }

    public void setPreviousApplicationStatus(int previousApplicationStatus) {
        this.previousApplicationStatus = previousApplicationStatus;
    }

    public String getPreviousStateDescription() {
        return previousStateDescription;
    }

    public void setPreviousStateDescription(String previousStateDescription) {
        this.previousStateDescription = previousStateDescription;
    }

    public String getCorellationID() {
        return corellationID;
    }

    public void setCorellationID(String corellationID) {
        this.corellationID = corellationID;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public int getNumberOfDecimalPlaces() {
        return numberOfDecimalPlaces;
    }

    public void setNumberOfDecimalPlaces(int numberOfDecimalPlaces) {
        this.numberOfDecimalPlaces = numberOfDecimalPlaces;
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

    public double getMinLoanLimit() {
        return minLoanLimit;
    }

    public void setMinLoanLimit(double minLoanLimit) {
        this.minLoanLimit = minLoanLimit;
    }

    public double getMaxLoanLimit() {
        return maxLoanLimit;
    }

    public void setMaxLoanLimit(double maxLoanLimit) {
        this.maxLoanLimit = maxLoanLimit;
    }

    public List<RiskwavierQuestionConfig> getQuestionConfigList() {
        return questionConfigList;
    }

    public void setQuestionConfigList(List<RiskwavierQuestionConfig> questionConfigList) {
        this.questionConfigList = questionConfigList;
    }

    public boolean isOTPEnabled() {
        return isOTPEnabled;
    }

    public void setIsOTPEnabled(boolean isOTPEnabled) {
        this.isOTPEnabled = isOTPEnabled;
    }

    public int getMaximumRetryCount() {
        return maximumRetryCount;
    }

    public void setMaximumRetryCount(int maximumRetryCount) {
        this.maximumRetryCount = maximumRetryCount;
    }

    public int getCurrentActivityCode() {
        return currentActivityCode;
    }

    public void setCurrentActivityCode(int currentActivityCode) {
        this.currentActivityCode = currentActivityCode;
    }

    public int getNoOfPendingContracts() {
        return noOfPendingContracts;
    }

    public void setNoOfPendingContracts(int noOfPendingContracts) {
        this.noOfPendingContracts = noOfPendingContracts;
    }

    public int getNoOfGrantedContracts() {
        return noOfGrantedContracts;
    }

    public void setNoOfGrantedContracts(int noOfGrantedContracts) {
        this.noOfGrantedContracts = noOfGrantedContracts;
    }

    public int getMaxNoOfGrantedContracts() {
        return maxNoOfGrantedContracts;
    }

    public void setMaxNoOfGrantedContracts(int maxNoOfGrantedContracts) {
        this.maxNoOfGrantedContracts = maxNoOfGrantedContracts;
    }

    public List<MurabahaProduct> getProductsList() {
        return productsList;
    }

    public void setProductsList(List<MurabahaProduct> productsList) {
        this.productsList = productsList;
    }
}
