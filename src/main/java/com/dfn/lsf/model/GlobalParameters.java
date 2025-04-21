package com.dfn.lsf.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalParameters {
    private static GlobalParameters _instance;
    private static LocalDateTime lastLoadTime;

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
    private double timeGapBetweenCallingAttempts;
    private boolean isMultipleOrderAllowed;
    private double minimumOrderValue;
    private String institutionCashAccount;
    private double administrationFeePercent;
    private int publicAccessEnabled;
    private int maximumNumberOfSymbols;
    private double colletralToMarginPercentage;
    private String marketOpenTime;
    private String marketClosedTime;
    private double simaCharges;
    private double transferCharges;
    private int maximumRetryCount;
    private int agreedLimit;
    private int getAppCloseLevel;
    private Long maxBrokerageLimit;
    private double vatPercentage;
    private int maxNumberOfActiveContracts;
    private String settlementClTimerString;
    private double shareAdminFee;
    private double comodityAdminFee;
    private double comodityFixedFee;
    private double shareFixedFee;
    private int minRolloverRatio;
    private int minRolloverPeriod;
    private int maxRolloverPeriod;
    private int gracePeriodforCommoditySell;
    private String institutionInvestAccount;

    public static void reset(GlobalParameters globalParameters) {
        _instance = globalParameters;
        lastLoadTime = LocalDateTime.now();
    }

    public static GlobalParameters getInstance() {
        if (_instance == null) {
            _instance = new GlobalParameters();
        }
        return _instance;
    }

    public static LocalDateTime getLastLoadTime() {
        return lastLoadTime;
    }
}
