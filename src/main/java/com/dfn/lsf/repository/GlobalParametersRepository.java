package com.dfn.lsf.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.dfn.lsf.model.GlobalParameters;

@Repository
public class GlobalParametersRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<GlobalParameters> globalParametersRowMapper;
    
    public GlobalParametersRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.globalParametersRowMapper = (rs, rowNum) -> {
            GlobalParameters params = new GlobalParameters();
            params.setMaxGuidanceLimit(rs.getLong("m01_max_guidance_limit"));
            params.setMinGuidanceLimit(rs.getInt("m01_min_guidance_limit"));
            params.setFtvForOperativeLimit(rs.getDouble("m01_ftv_ratio_for_operative"));
            params.setAllowInstalmentSettlement(rs.getBoolean("m01_allow_instalment_settle"));
            params.setOperatingLimitType(rs.getBoolean("m01_operating_limit_type"));
            params.setScriptMaxContribution(rs.getDouble("m01_max_contribution"));
            params.setNoOfDaysPriorNotifyReviewDate(rs.getInt("m01_notify_days_bf_review"));
            params.setShariaSymbolsAsCollateral(rs.getBoolean("m01_sharia_symbol_as_collatera"));
            params.setDocumentationFeePercentage(rs.getDouble("m01_documentation_fee_pecent"));
            params.setUtilizeCustomerCashFirst(rs.getBoolean("m01_utilize_cash_first"));
            params.setFirstMarginCall(rs.getDouble("m01_first_margine_call_percent"));
            params.setSecondMarginCall(rs.getDouble("m01_second_margine_call_percen"));
            params.setLiquidationCall(rs.getDouble("m01_liquid_margine_call_perent"));
            params.setPreFunded(rs.getBoolean("m01_is_prefunded"));
            params.setNoOfCallingAttemptsPerDay(rs.getInt("m01_no_of_calling_attempts"));
            params.setNoOfDaysPriorRemindingThePayment(rs.getInt("m01_rimind_days_prior_to_payem"));
            params.setNoOfDaysWaitsBeforeLiquidation(rs.getInt("m01_days_wait_before_liquidati"));
            params.setSymbolReValuationInterval(rs.getInt("m01_symbol_revalue_interval"));
            params.setPriorityCashACForSettlement(rs.getBoolean("m01_settling_cash_acc"));
            params.setAlertCustomerPriorToFDExpiry(rs.getInt("m01_alert_prior_to_fd_expiry"));
            params.setMurabahaOTP(rs.getBoolean("m01_enable_otp"));
            params.setNoOfMarginCallsPerDay(rs.getInt("m01_margine_calls_per_day"));
            params.setBaseCurrency(rs.getString("m01_base_currency"));
            params.setProfitCalculateMethode(rs.getInt("m01_profit_calc_method"));
            params.setClientCode(rs.getString("m01_client_code"));
            params.setOrderAccPriority(rs.getInt("m01_order_acc_priority"));
            params.setInstitutionTradingAcc(rs.getString("m01_institution_trading_acc"));
            params.setDefaultExchange(rs.getString("m01_default_exchange"));
            params.setAdministrationFee(rs.getDouble("m01_admin_fee"));
            params.setNumberOfDecimalPlaces(rs.getInt("m01_decimal_count"));
            params.setInstitutionCashAccount(rs.getString("m01_institution_cash_acc"));
            params.setAdministrationFeePercent(rs.getDouble("m01_admin_fee_percentage"));
            params.setColletralToMarginPercentage(rs.getDouble("m01_collatreal_to_margin_perc"));
            params.setMarketOpenTime(rs.getString("m01_market_open_time"));
            params.setMarketClosedTime(rs.getString("m01_market_closed_time"));
            params.setMaxBrokerageLimit(rs.getLong("m01_max_brokerage_limit"));
            params.setVatPercentage(rs.getDouble("m01_vat_amount"));
            params.setMaxNumberOfActiveContracts(rs.getInt("m01_max_active_contracts"));
            params.setShareAdminFee(rs.getDouble("m01_share_admin_fee"));
            params.setComodityAdminFee(rs.getDouble("m01_comodity_admin_fee"));
            params.setShareFixedFee(rs.getDouble("m01_share_fixed_fee"));
            params.setMinRolloverRatio(rs.getInt("m01_min_rollover_ratio"));
            params.setMinRolloverPeriod(rs.getInt("m01_min_rollover_period"));
            params.setMaxRolloverPeriod(rs.getInt("m01_max_rollover_period"));
            params.setGracePeriodforCommoditySell(rs.getInt("m01_grace_per_commodity_sell"));
            params.setInstitutionInvestAccount(rs.getString("m01_institution_invest_acc"));
            return params;
        };
    }
    
    public GlobalParameters getGlobalParameters() {
        String sql = "SELECT * FROM M01_SYS_PARAS";
        return jdbcTemplate.query(sql, globalParametersRowMapper)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public String updateGlobalParameters(GlobalParameters parameters) {
        String sql = "UPDATE M01_SYS_PARAS SET " +
            "m01_max_guidance_limit = ?, " +
            "m01_min_guidance_limit = ?, " +
            "m01_ftv_ratio_for_operative = ?, " +
            "m01_allow_instalment_settle = ?, " +
            "m01_operating_limit_type = ?, " +
            "m01_max_contribution = ?, " +
            "m01_notify_days_bf_review = ?, " +
            "m01_sharia_symbol_as_collatera = ?, " +
            "m01_documentation_fee_pecent = ?, " +
            "m01_utilize_cash_first = ?, " +
            "m01_first_margine_call_percent = ?, " +
            "m01_second_margine_call_percen = ?, " +
            "m01_liquid_margine_call_perent = ?, " +
            "m01_is_prefunded = ?, " +
            "m01_no_of_calling_attempts = ?, " +
            "m01_rimind_days_prior_to_payem = ?, " +
            "m01_days_wait_before_liquidati = ?, " +
            "m01_symbol_revalue_interval = ?, " +
            "m01_settling_cash_acc = ?, " +
            "m01_alert_prior_to_fd_expiry = ?, " +
            "m01_enable_otp = ?, " +
            "m01_margine_calls_per_day = ?, " +
            "m01_base_currency = ?, " +
            "m01_profit_calc_method = ?, " +
            "m01_client_code = ?, " +
            "m01_order_acc_priority = ?, " +
            "m01_institution_trading_acc = ?, " +
            "m01_default_exchange = ?, " +
            "m01_admin_fee = ?, " +
            "m01_decimal_count = ?, " +
            "m01_institution_cash_acc = ?, " +
            "m01_admin_fee_percentage = ?, " +
            "m01_collatreal_to_margin_perc = ?, " +
            "m01_market_open_time = ?, " +
            "m01_market_closed_time = ?, " +
            "m01_vat_amount = ?, " +
            "m01_max_active_contracts = ?, " +
            "m01_share_admin_fee = ?, " +
            "m01_comodity_admin_fee = ?, " +
            "m01_share_fixed_fee = ?, " +
            "m01_min_rollover_ratio = ?, " +
            "m01_min_rollover_period = ?, " +
            "m01_max_rollover_period = ?, " +
            "m01_grace_per_commodity_sell = ?, " +
            "m01_institution_invest_acc = ?";

        try {
            int rows = jdbcTemplate.update(sql,
                parameters.getMaxGuidanceLimit(),
                parameters.getMinGuidanceLimit(),
                parameters.getFtvForOperativeLimit(),
                parameters.getAllowInstalmentSettlement(),
                parameters.getOperatingLimitType(),
                parameters.getScriptMaxContribution(),
                parameters.getNoOfDaysPriorNotifyReviewDate(),
                parameters.getShariaSymbolsAsCollateral(),
                parameters.getDocumentationFeePercentage(),
                parameters.getUtilizeCustomerCashFirst(),
                parameters.getFirstMarginCall(),
                parameters.getSecondMarginCall(),
                parameters.getLiquidationCall(),
                parameters.getPreFunded(),
                parameters.getNoOfCallingAttemptsPerDay(),
                parameters.getNoOfDaysPriorRemindingThePayment(),
                parameters.getNoOfDaysWaitsBeforeLiquidation(),
                parameters.getSymbolReValuationInterval(),
                parameters.getPriorityCashACForSettlement(),
                parameters.getAlertCustomerPriorToFDExpiry(),
                parameters.getMurabahaOTP(),
                parameters.getNoOfMarginCallsPerDay(),
                parameters.getBaseCurrency(),
                parameters.getProfitCalculateMethode(),
                parameters.getClientCode(),
                parameters.getOrderAccPriority(),
                parameters.getInstitutionTradingAcc(),
                parameters.getDefaultExchange(),
                parameters.getAdministrationFee(),
                parameters.getNumberOfDecimalPlaces(),
                parameters.getInstitutionCashAccount(),
                parameters.getAdministrationFeePercent(),
                parameters.getColletralToMarginPercentage(),
                parameters.getMarketOpenTime(),
                parameters.getMarketClosedTime(),
                parameters.getVatPercentage(),
                parameters.getMaxNumberOfActiveContracts(),
                parameters.getShareAdminFee(),
                parameters.getComodityAdminFee(),
                parameters.getShareFixedFee(),
                parameters.getMinRolloverRatio(),
                parameters.getMinRolloverPeriod(),
                parameters.getMaxRolloverPeriod(),
                parameters.getGracePeriodforCommoditySell(),
                parameters.getInstitutionInvestAccount()
            );

            if (rows > 0) {
                return "Success";
            } else {
                return "No rows updated";
            }
        } catch (Exception e) {
            return "Error updating parameters: " + e.getMessage();
        }
    }
} 