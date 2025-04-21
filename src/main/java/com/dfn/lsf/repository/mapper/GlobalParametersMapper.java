package com.dao.mapper;

import com.dfn.lsf.gbl.bo.GlobalParameters;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Atchuthan on 8/6/2015.
 */
public class GlobalParametersMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GlobalParameters _instant = GlobalParameters.getInstance();
        _instant.setMaxGuidanceLimit(rs.getLong("M01_MAX_GUIDANCE_LIMIT"));
        _instant.setMinGuidanceLimit(rs.getInt("M01_MIN_GUIDANCE_LIMIT"));
        _instant.setFtvForOperativeLimit(rs.getDouble("M01_FTV_RATIO_FOR_OPERATIVE"));
        if(rs.getInt("M01_ALLOW_INSTALMENT_SETTLE")==1){
            _instant.setAllowInstalmentSettlement(true);
        }else if(rs.getInt("M01_ALLOW_INSTALMENT_SETTLE")==0) {
            _instant.setAllowInstalmentSettlement(false);
        }
        if(rs.getInt("M01_OPERATING_LIMIT_TYPE")==1){
            _instant.setOperatingLimitType(true);
        }else if(rs.getInt("M01_OPERATING_LIMIT_TYPE")==0){
            _instant.setOperatingLimitType(false);
        }
        _instant.setScriptMaxContribution(rs.getDouble("M01_MAX_CONTRIBUTION"));
        _instant.setNoOfDaysPriorNotifyReviewDate(rs.getInt("M01_NOTIFY_DAYS_BF_REVIEW"));
        if(rs.getInt("M01_SHARIA_SYMBOL_AS_COLLATERA")==1){
            _instant.setShariaSymbolsAsCollateral(true);
        }else if(rs.getInt("M01_SHARIA_SYMBOL_AS_COLLATERA")==0){
            _instant.setShariaSymbolsAsCollateral(false);
        }
        _instant.setDocumentationFeePercentage(rs.getDouble("M01_DOCUMENTATION_FEE_PECENT"));
        if(rs.getInt("M01_UTILIZE_CASH_FIRST")==1){
            _instant.setUtilizeCustomerCashFirst(true);
        }else if(rs.getInt("M01_UTILIZE_CASH_FIRST")==0){
            _instant.setUtilizeCustomerCashFirst(false);
        }
        _instant.setFirstMarginCall(rs.getDouble("M01_FIRST_MARGINE_CALL_PERCENT"));
        _instant.setSecondMarginCall(rs.getDouble("M01_SECOND_MARGINE_CALL_PERCEN"));
        _instant.setLiquidationCall(rs.getDouble("M01_LIQUID_MARGINE_CALL_PERENT"));
        if(rs.getInt("M01_IS_PREFUNDED")==1){
            _instant.setPreFunded(true);
        }else if(rs.getInt("M01_IS_PREFUNDED")==0){
            _instant.setPreFunded(false);
        }
        _instant.setNoOfCallingAttemptsPerDay(rs.getInt("M01_NO_OF_CALLING_ATTEMPTS"));
        _instant.setNoOfDaysPriorRemindingThePayment(rs.getInt("M01_RIMIND_DAYS_PRIOR_TO_PAYEM"));
        _instant.setNoOfDaysWaitsBeforeLiquidation(rs.getInt("M01_DAYS_WAIT_BEFORE_LIQUIDATI"));
        _instant.setSymbolReValuationInterval(rs.getInt("M01_SYMBOL_REVALUE_INTERVAL"));
        _instant.setAlertCustomerPriorToFDExpiry(rs.getInt("M01_ALERT_PRIOR_TO_FD_EXPIRY"));
        if(rs.getInt("M01_SETTLING_CASH_ACC")==1){
            _instant.setPriorityCashACForSettlement(true);
        }else if(rs.getInt("M01_SETTLING_CASH_ACC")==0){
            _instant.setPriorityCashACForSettlement(false);
        }
        if(rs.getInt("M01_ENABLE_OTP")==1){
            _instant.setMurabahaOTP(true);
        }else if(rs.getInt("M01_ENABLE_OTP")==0){
            _instant.setMurabahaOTP(false);
        }
        _instant.setNoOfMarginCallsPerDay(rs.getInt("M01_MARGINE_CALLS_PER_DAY"));
        _instant.setBaseCurrency(rs.getString("M01_BASE_CURRENCY"));
        _instant.setProfitCalculateMethode(rs.getInt("M01_PROFIT_CALC_METHOD"));
        _instant.setClientCode(rs.getString("M01_CLIENT_CODE"));
        _instant.setSendOrderApprovalFirst(rs.getInt("M01_SEND_ORDER_APPROVAL_FIRST"));
        _instant.setOrderAccPriority(rs.getInt("M01_ORDER_ACC_PRIORITY"));
        _instant.setInstitutionTradingAcc(rs.getString("M01_INSTITUTION_TRADING_ACC"));
        _instant.setDefaultExchange(rs.getString("M01_DEFAULT_EXCHANGE"));
        _instant.setAdministrationFee(rs.getDouble("m01_admin_fee"));
        _instant.setNumberOfDecimalPlaces(rs.getInt("m01_decimal_count"));
        _instant.setTimeGapBetweenCallingAttempts(rs.getDouble("M01_TIME_GAP_BTW_CALLING_ATMPT"));
        _instant.setIsMultipleOrderAllowed(rs.getBoolean("m01_is_multiple_order_allowed"));
        _instant.setMinimumOrderValue(rs.getDouble("m01_minimum_order_value"));
        _instant.setInstitutionCashAccount(rs.getString("m01_institution_cash_acc"));
        _instant.setAdministrationFeePercent(rs.getDouble("m01_admin_fee_percentage"));
        _instant.setPublicAccessEnabled(rs.getInt("m01_lsf_public_access_enabled"));
        _instant.setMaximumNumberOfSymbols(rs.getInt("m01_max_symbol_cnt"));
        _instant.setColletralToMarginPercentage(rs.getDouble("m01_collatreal_to_margin_perc"));
        _instant.setMarketOpenTime(rs.getString("m01_market_open_time"));
        _instant.setMarketClosedTime(rs.getString("m01_market_closed_time"));
        _instant.setSimaCharges(rs.getDouble("m01_sima_charges"));
        _instant.setTransferCharges(rs.getDouble("m01_transfer_charges"));
        _instant.setMaximumRetryCount(rs.getInt("m01_otp_failure_attempts"));
        _instant.setAgreedLimit(rs.getInt("m01_agreed_limit"));
        _instant.setGetAppCloseLevel(rs.getInt("m01_app_completed_state"));
        _instant.setMaxBrokerageLimit(rs.getLong("m01_max_brokerage_limit"));
        _instant.setVatPercentage(rs.getDouble("m01_vat_amount"));
        _instant.setMaxNumberOfActiveContracts(rs.getInt("m01_max_active_contracts"));
        _instant.setSettlementClTimerString(rs.getString("m01_settlement_cal_time"));
        _instant.setShareFixedFee(rs.getDouble("m01_share_fixed_fee"));
        _instant.setComodityFixedFee(rs.getDouble("m01_comodity_fixed_fee"));
        _instant.setShareAdminFee(rs.getDouble("m01_share_admin_fee"));
        _instant.setComodityAdminFee(rs.getDouble("m01_comodity_admin_fee"));
        _instant.setGracePeriodforCommoditySell(rs.getInt("m01_grace_per_commodity_sell"));
        _instant.setInstitutionInvestAccount(rs.getString("m01_institution_invest_acc"));

        return _instant;
    }
}
