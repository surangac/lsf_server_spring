-- Start of DDL Script for Package MUBASHER_LSF.M01_SYS_PARAS_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m01_sys_paras_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE m01_add_update (
        pkey                                OUT NUMBER,
        p01_max_guidance_limit           IN     NUMBER,
        p01_min_guidance_limit           IN     NUMBER,
        p01_ftv_ratio_for_operative      IN     NUMBER,
        p01_allow_instalment_settle      IN     NUMBER,
        p01_operating_limit_type         IN     NUMBER,
        p01_max_contribution             IN     NUMBER,
        p01_notify_days_bf_review        IN     NUMBER,
        p01_sharia_symbol_as_collatera   IN     NUMBER,
        p01_documentation_fee_pecent     IN     NUMBER,
        p01_utilize_cash_first           IN     NUMBER,
        p01_first_margine_call_percent   IN     NUMBER,
        p01_second_margine_call_percen   IN     NUMBER,
        p01_liquid_margine_call_perent   IN     NUMBER,
        p01_is_prefunded                 IN     NUMBER,
        p01_no_of_calling_attempts       IN     NUMBER,
        p01_rimind_days_prior_to_payem   IN     NUMBER,
        p01_days_wait_before_liquidati   IN     NUMBER,
        p01_symbol_revalue_interval      IN     NUMBER,
        p01_alert_prior_to_fd_expiry     IN     NUMBER,
        p01_settling_cash_acc            IN     NUMBER,
        p01_enable_otp                   IN     NUMBER,
        p01_margine_calls_per_day        IN     NUMBER,
        p01_base_currency                IN     VARCHAR2,
        p01_profit_calc_method           IN     NUMBER,
        p01_client_code                  IN     VARCHAR2,
        pm01_order_acc_priority          IN     NUMBER DEFAULT 0,
        pm01_institution_trading_acc     IN     VARCHAR2 DEFAULT NULL,
        pm01_default_exchange            IN     VARCHAR2,
        pm01_admin_fee                   IN     NUMBER,
        pm01_decimal_count               IN     NUMBER,
        pm01_time_gap_calling_atmpt             NUMBER DEFAULT 1,
        pm01_institution_cash_acc               VARCHAR2 DEFAULT '',
        pm01_admin_fee_percentage               NUMBER,
        pm01_max_symbol_cnt                     NUMBER,
        pm01_collatreal_to_margin_perc   IN     NUMBER,
        pm01_market_open_time            IN     VARCHAR2,
        pm01_market_closed_time          IN     VARCHAR2,
        pm01_sima_charges                IN     NUMBER,
        pm01_transfer_charges            IN     NUMBER,
        pm01_agreed_limit                       NUMBER,
        pm01_max_brokerage_limit                NUMBER DEFAULT 0,
        pm01_vat_amount                         NUMBER DEFAULT 0,
        pm01_max_active_contracts               NUMBER DEFAULT 1,
        pm01_share_admin_fee             IN     NUMBER DEFAULT 0,
        pm01_comodity_admin_fee          IN     NUMBER DEFAULT 0,
        pm01_comodity_fixed_fee          IN     NUMBER DEFAULT 0,
        pm01_share_fixed_fee             IN     NUMBER DEFAULT 0,
        pm01_min_rollover_ratio          IN     NUMBER DEFAULT 0,
        pm01_min_rollover_period         IN     NUMBER DEFAULT 0,
        pm01_max_rollover_period         IN     NUMBER DEFAULT 0,
        pm01_grace_per_commodity_sell    IN     NUMBER,
    	pm01_institution_invest_acc		 IN		VARCHAR2,
    	pm01_order_acceptance_limit      IN     NUMBER DEFAULT 150);

    PROCEDURE m01_get (pview OUT refcursor);

    PROCEDURE m01_enable_lsf (pm01_lsf_public_access_enabled NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.m01_sys_paras_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.m01_sys_paras_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.m01_sys_paras_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.m01_sys_paras_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.m01_sys_paras_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m01_sys_paras_pkg
IS
    PROCEDURE m01_add_update (
        pkey                                OUT NUMBER,
        p01_max_guidance_limit           IN     NUMBER,
        p01_min_guidance_limit           IN     NUMBER,
        p01_ftv_ratio_for_operative      IN     NUMBER,
        p01_allow_instalment_settle      IN     NUMBER,
        p01_operating_limit_type         IN     NUMBER,
        p01_max_contribution             IN     NUMBER,
        p01_notify_days_bf_review        IN     NUMBER,
        p01_sharia_symbol_as_collatera   IN     NUMBER,
        p01_documentation_fee_pecent     IN     NUMBER,
        p01_utilize_cash_first           IN     NUMBER,
        p01_first_margine_call_percent   IN     NUMBER,
        p01_second_margine_call_percen   IN     NUMBER,
        p01_liquid_margine_call_perent   IN     NUMBER,
        p01_is_prefunded                 IN     NUMBER,
        p01_no_of_calling_attempts       IN     NUMBER,
        p01_rimind_days_prior_to_payem   IN     NUMBER,
        p01_days_wait_before_liquidati   IN     NUMBER,
        p01_symbol_revalue_interval      IN     NUMBER,
        p01_alert_prior_to_fd_expiry     IN     NUMBER,
        p01_settling_cash_acc            IN     NUMBER,
        p01_enable_otp                   IN     NUMBER,
        p01_margine_calls_per_day        IN     NUMBER,
        p01_base_currency                IN     VARCHAR2,
        p01_profit_calc_method           IN     NUMBER,
        p01_client_code                  IN     VARCHAR2,
        pm01_order_acc_priority          IN     NUMBER DEFAULT 0,
        pm01_institution_trading_acc     IN     VARCHAR2 DEFAULT NULL,
        pm01_default_exchange            IN     VARCHAR2,
        pm01_admin_fee                   IN     NUMBER,
        pm01_decimal_count               IN     NUMBER,
        pm01_time_gap_calling_atmpt             NUMBER DEFAULT 1,
        pm01_institution_cash_acc               VARCHAR2 DEFAULT '',
        pm01_admin_fee_percentage               NUMBER,
        pm01_max_symbol_cnt                     NUMBER,
        pm01_collatreal_to_margin_perc   IN     NUMBER,
        pm01_market_open_time            IN     VARCHAR2,
        pm01_market_closed_time          IN     VARCHAR2,
        pm01_sima_charges                IN     NUMBER,
        pm01_transfer_charges            IN     NUMBER,
        pm01_agreed_limit                       NUMBER,
        pm01_max_brokerage_limit                NUMBER DEFAULT 0,
        pm01_vat_amount                         NUMBER DEFAULT 0,
        pm01_max_active_contracts               NUMBER DEFAULT 1,
        pm01_share_admin_fee             IN     NUMBER DEFAULT 0,
        pm01_comodity_admin_fee          IN     NUMBER DEFAULT 0,
        pm01_comodity_fixed_fee          IN     NUMBER DEFAULT 0,
        pm01_share_fixed_fee             IN     NUMBER DEFAULT 0,
        pm01_min_rollover_ratio          IN     NUMBER DEFAULT 0,
        pm01_min_rollover_period         IN     NUMBER DEFAULT 0,
        pm01_max_rollover_period         IN     NUMBER DEFAULT 0,
        pm01_grace_per_commodity_sell    IN     NUMBER,
        pm01_institution_invest_acc		 IN		VARCHAR2,
        pm01_order_acceptance_limit      IN     NUMBER DEFAULT 150)
    IS
    BEGIN
        UPDATE m01_sys_paras
           SET m01_max_guidance_limit = p01_max_guidance_limit,
               m01_min_guidance_limit = p01_min_guidance_limit,
               m01_ftv_ratio_for_operative = p01_ftv_ratio_for_operative,
               m01_allow_instalment_settle = p01_allow_instalment_settle,
               m01_operating_limit_type = p01_operating_limit_type,
               m01_max_contribution = p01_max_contribution,
               m01_notify_days_bf_review = p01_notify_days_bf_review,
               m01_sharia_symbol_as_collatera = p01_sharia_symbol_as_collatera,
               m01_documentation_fee_pecent = p01_documentation_fee_pecent,
               m01_utilize_cash_first = p01_utilize_cash_first,
               m01_first_margine_call_percent = p01_first_margine_call_percent,
               m01_second_margine_call_percen = p01_second_margine_call_percen,
               m01_liquid_margine_call_perent = p01_liquid_margine_call_perent,
               m01_is_prefunded = p01_is_prefunded,
               m01_no_of_calling_attempts = p01_no_of_calling_attempts,
               m01_rimind_days_prior_to_payem = p01_rimind_days_prior_to_payem,
               m01_days_wait_before_liquidati = p01_days_wait_before_liquidati,
               m01_symbol_revalue_interval = p01_symbol_revalue_interval,
               m01_alert_prior_to_fd_expiry = p01_alert_prior_to_fd_expiry,
               m01_settling_cash_acc = p01_settling_cash_acc,
               m01_enable_otp = p01_enable_otp,
               m01_margine_calls_per_day = p01_margine_calls_per_day,
               m01_base_currency = p01_base_currency,
               m01_profit_calc_method = p01_profit_calc_method,
               m01_client_code = p01_client_code,
               m01_order_acc_priority = pm01_order_acc_priority,
               m01_institution_trading_acc = pm01_institution_trading_acc,
               m01_default_exchange = pm01_default_exchange,
               m01_admin_fee = pm01_admin_fee,
               m01_decimal_count = pm01_decimal_count,
               m01_time_gap_btw_calling_atmpt = pm01_time_gap_calling_atmpt,
               m01_institution_cash_acc = pm01_institution_cash_acc,
               m01_admin_fee_percentage = pm01_admin_fee_percentage,
               m01_max_symbol_cnt = pm01_max_symbol_cnt,
               m01_collatreal_to_margin_perc = pm01_collatreal_to_margin_perc,
               m01_market_open_time = pm01_market_open_time,
               m01_market_closed_time = pm01_market_closed_time,
               m01_sima_charges = pm01_sima_charges,
               m01_transfer_charges = pm01_transfer_charges,
               m01_agreed_limit = pm01_agreed_limit,
               m01_max_brokerage_limit = pm01_max_brokerage_limit,
               m01_vat_amount = pm01_vat_amount,
               m01_max_active_contracts = pm01_max_active_contracts,
               m01_share_admin_fee = pm01_share_admin_fee,
               m01_comodity_admin_fee = pm01_comodity_admin_fee,
               m01_comodity_fixed_fee = pm01_comodity_fixed_fee,
               m01_share_fixed_fee = pm01_share_fixed_fee,
               m01_min_rollover_ratio = pm01_min_rollover_ratio,
               m01_min_rollover_period = pm01_min_rollover_period,
               m01_max_rollover_period = pm01_max_rollover_period,
               m01_grace_per_commodity_sell = pm01_grace_per_commodity_sell,
               m01_institution_invest_acc = pm01_institution_invest_acc,
               m01_order_acceptance_limit = pm01_order_acceptance_limit;

        pkey := '1';
    END;

    PROCEDURE m01_get (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR SELECT * FROM m01_sys_paras;
    END;

    PROCEDURE m01_enable_lsf (pm01_lsf_public_access_enabled NUMBER)
    IS
    BEGIN
        UPDATE m01_sys_paras
           SET m01_lsf_public_access_enabled = pm01_lsf_public_access_enabled;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M01_SYS_PARAS_PKG

