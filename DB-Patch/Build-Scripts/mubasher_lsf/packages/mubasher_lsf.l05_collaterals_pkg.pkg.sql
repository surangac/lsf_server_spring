-- Start of DDL Script for Package MUBASHER_LSF.L05_COLLATERALS_PKG
-- Generated 17-Nov-2025 10:52:50 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE mubasher_lsf.l05_collaterals_pkg
IS
    --
    -- To modify this template, edit file PKGSPEC.TXT in TEMPLATE
    -- directory of SQL Navigator
    --
    -- Purpose: Briefly explain the functionality of the package
    --
    -- MODIFICATION HISTORY
    -- Person      Date    Comments
    -- ---------   ------  ------------------------------------------
    -- Enter package declarations as shown below

    TYPE refcursor IS REF CURSOR;

    PROCEDURE l05_add_edit (
        pkey                             OUT NUMBER,
        pl05_l01_app_id                      NUMBER,
        pl05_collateral_id                   NUMBER,
        pl05_net_total_collat                NUMBER,
        pl05_total_cash_collat               NUMBER,
        pl05_approved_limit_amt              NUMBER,
        pl05_utilized_limit_amt              NUMBER,
        pl05_operative_limit_amt             NUMBER,
        pl05_rem_operative_limit_amt         NUMBER,
        pl05_outstanding_amt                 NUMBER,
        pl05_is_ready_for_collat_trans       NUMBER,
        pl05_ftv                             NUMBER,
        pl05_first_margin_call               NUMBER,
        pl05_second_margin_call              NUMBER,
        pl05_liquidation_call                NUMBER,
        pl05_total_external_collat           NUMBER,
        pl05_total_portfolio_collat          NUMBER,
        pl05_margine_call_attempts           NUMBER DEFAULT 0,
        pl05_block_amount                    NUMBER DEFAULT 0,
        pl05_margine_call_date               VARCHAR2 DEFAULT NULL,
        pl05_liquidate_call_date             VARCHAR2 DEFAULT NULL,
        pl05_customer_id                     VARCHAR2,
        pl05_customer_name                     VARCHAR2,
        pl05_ip_address                         VARCHAR2);

    PROCEDURE l05_get (pview OUT refcursor, pl05_l01_app_id NUMBER);

    PROCEDURE l05_change_status (pkey                    OUT NUMBER,
                                 pl05_l01_app_id             NUMBER,
                                 pl05_collateral_id          NUMBER,
                                 pl05_status_change_by       VARCHAR2,
                                 pl05_status                 NUMBER,
                                 p_message                   VARCHAR2,
                                 p_status_changed_ip         VARCHAR2);

    PROCEDURE l05_get_ftv_list (pview OUT refcursor);

    PROCEDURE l05_get_commission_details (pview        OUT refcursor,
                                          reportdate       VARCHAR2);

    PROCEDURE l05_get_ftv_detailed_info (pview       OUT refcursor,
                                         fromdate        VARCHAR2,
                                         todate          VARCHAR2,
                                         stlstatus       NUMBER);

    PROCEDURE l05_update_initial_collateral (
        pl05_l01_app_id                 NUMBER,
        pl05_initial_cash_collateral    NUMBER,
        pl05_initial_pf_collateral      NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l05_collaterals_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l05_collaterals_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l05_collaterals_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l05_collaterals_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l05_collaterals_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.l05_collaterals_pkg
/* Formatted on 10/27/2016 7:02:29 PM (QP5 v5.206) */
IS
    --
    -- To modify this template, edit file PKGBODY.TXT in TEMPLATE
    -- directory of SQL Navigator
    --
    -- Purpose: Briefly explain the functionality of the package body
    --
    -- MODIFICATION HISTORY
    -- Person      Date    Comments
    -- ---------   ------  ------------------------------------------
    -- Enter procedure, function bodies as shown below

    PROCEDURE l05_add_edit (
        pkey                             OUT NUMBER,
        pl05_l01_app_id                      NUMBER,
        pl05_collateral_id                   NUMBER,
        pl05_net_total_collat                NUMBER,
        pl05_total_cash_collat               NUMBER,
        pl05_approved_limit_amt              NUMBER,
        pl05_utilized_limit_amt              NUMBER,
        pl05_operative_limit_amt             NUMBER,
        pl05_rem_operative_limit_amt         NUMBER,
        pl05_outstanding_amt                 NUMBER,
        pl05_is_ready_for_collat_trans       NUMBER,
        pl05_ftv                             NUMBER,
        pl05_first_margin_call               NUMBER,
        pl05_second_margin_call              NUMBER,
        pl05_liquidation_call                NUMBER,
        pl05_total_external_collat           NUMBER,
        pl05_total_portfolio_collat          NUMBER,
        pl05_margine_call_attempts           NUMBER DEFAULT 0,
        pl05_block_amount                    NUMBER DEFAULT 0,
        pl05_margine_call_date               VARCHAR2 DEFAULT NULL,
        pl05_liquidate_call_date             VARCHAR2 DEFAULT NULL,
        pl05_customer_id                     VARCHAR2,
        pl05_customer_name                     VARCHAR2,
        pl05_ip_address                         VARCHAR2)
    IS
        v_count         NUMBER := 0;
        v_key           NUMBER;
        pkey1           VARCHAR2 (100) := '';
        v_client_code   VARCHAR2 (20) := '';
    BEGIN
        SELECT COUNT (*)
          INTO v_count
          FROM l05_collaterals
         WHERE     l05_l01_app_id = pl05_l01_app_id
               AND l05_collateral_id = pl05_collateral_id;

        IF (v_count > 0)
        THEN
            pkey := pl05_collateral_id;

            UPDATE l05_collaterals
               SET l05_net_total_collat = pl05_net_total_collat,
                   l05_total_cash_collat = pl05_total_cash_collat,
                   l05_approved_limit_amt = pl05_approved_limit_amt,
                   l05_utilized_limit_amt = pl05_utilized_limit_amt,
                   l05_operative_limit_amt = pl05_operative_limit_amt,
                   l05_rem_operative_limit_amt = pl05_rem_operative_limit_amt,
                   l05_outstanding_amt = pl05_outstanding_amt,
                   l05_updated_date = SYSDATE,
                   l05_is_ready_for_collat_trans =
                       pl05_is_ready_for_collat_trans,
                   l05_ftv = pl05_ftv,
                   l05_first_margin_call = pl05_first_margin_call,
                   l05_second_margin_call = pl05_second_margin_call,
                   l05_liquidation_call = pl05_liquidation_call,
                   l05_total_external_collat = pl05_total_external_collat,
                   l05_total_portfolio_collat = pl05_total_portfolio_collat,
                   l05_margine_call_attempts = pl05_margine_call_attempts,
                   l05_block_amount = pl05_block_amount,
                   l05_margine_call_date = pl05_margine_call_date,
                   l05_liquidate_call_date = pl05_liquidate_call_date
             WHERE     l05_l01_app_id = pl05_l01_app_id
                   AND l05_collateral_id = pl05_collateral_id;
        ELSE
            SELECT NVL (MAX (l05_collateral_id), 0) + 1
              INTO v_key
              FROM l05_collaterals;

            INSERT INTO l05_collaterals (l05_l01_app_id,
                                         l05_collateral_id,
                                         l05_net_total_collat,
                                         l05_total_cash_collat,
                                         l05_approved_limit_amt,
                                         l05_utilized_limit_amt,
                                         l05_operative_limit_amt,
                                         l05_rem_operative_limit_amt,
                                         l05_outstanding_amt,
                                         l05_updated_date,
                                         l05_is_ready_for_collat_trans,
                                         l05_ftv,
                                         l05_first_margin_call,
                                         l05_second_margin_call,
                                         l05_liquidation_call,
                                         l05_status,
                                         l05_total_external_collat,
                                         l05_total_portfolio_collat,
                                         l05_margine_call_attempts,
                                         l05_block_amount,
                                         l05_margine_call_date,
                                         l05_liquidate_call_date)
                 VALUES (pl05_l01_app_id,
                         v_key,
                         pl05_net_total_collat,
                         pl05_total_cash_collat,
                         pl05_approved_limit_amt,
                         pl05_utilized_limit_amt,
                         pl05_operative_limit_amt,
                         pl05_rem_operative_limit_amt,
                         pl05_outstanding_amt,
                         SYSDATE,
                         pl05_is_ready_for_collat_trans,
                         pl05_ftv,
                         pl05_first_margin_call,
                         pl05_second_margin_call,
                         pl05_liquidation_call,
                         0,
                         pl05_total_external_collat,
                         pl05_total_portfolio_collat,
                         pl05_margine_call_attempts,
                         pl05_block_amount,
                         pl05_margine_call_date,
                         pl05_liquidate_call_date);

            SELECT m01_client_code INTO v_client_code FROM m01_sys_paras;

            -- move the application to next Step
            IF (v_client_code = 'ABIC')
            THEN
                m02_app_state_flow_pkg.m02_approve_application (
                    pkey                         => pkey1,
                    approve_state                => 1,
                    pl01_app_id                  => pl05_l01_app_id,
                    pl02_message                 => 'Collateral Submited',
                    pl02_sts_changed_user_id     => pl05_customer_id,
                    pl02_sts_changed_user_name   => pl05_customer_name,
                    pl02_status_changed_ip       => pl05_ip_address);
            END IF;

            pkey := v_key;
        END IF;
    END;

    PROCEDURE l05_get (pview OUT refcursor, pl05_l01_app_id NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l05_collaterals
             WHERE l05_l01_app_id = pl05_l01_app_id;
    END;

    PROCEDURE l05_change_status (pkey                    OUT NUMBER,
                                 pl05_l01_app_id             NUMBER,
                                 pl05_collateral_id          NUMBER,
                                 pl05_status_change_by       VARCHAR2,
                                 pl05_status                 NUMBER,
                                 p_message                   VARCHAR2,
                                 p_status_changed_ip         VARCHAR2)
    IS
        pkey1   VARCHAR2 (100) := '';
    BEGIN
        UPDATE l05_collaterals
           SET l05_status = pl05_status,
               l05_status_change_by = pl05_status_change_by,
               l05_status_change_date = SYSDATE
         WHERE     l05_l01_app_id = pl05_l01_app_id
               AND l05_collateral_id = pl05_collateral_id;

        -- move the application to next Step
        m02_app_state_flow_pkg.m02_approve_application (
            pkey                         => pkey1,
            approve_state                => pl05_status,
            pl01_app_id                  => pl05_l01_app_id,
            pl02_message                 => p_message,
            pl02_sts_changed_user_id     => pl05_status_change_by,
            pl02_sts_changed_user_name   => pl05_status_change_by,
            pl02_status_changed_ip       => p_status_changed_ip);

        pkey := pl05_l01_app_id;
    END;

    PROCEDURE l05_get_ftv_list (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l05.*
              FROM l05_collaterals l05
             WHERE l05_ftv > 0;
    END;

    PROCEDURE l05_get_ftv_detailed_info (pview OUT refcursor,fromdate varchar2,todate varchar2,stlStatus number)
    IS
        v_close_state   NUMBER;
        v_admin_fee     NUMBER;
        v_sql          VARCHAR2 (3000);
        v_filterStr    varchar2(200) default '';
        v_dateFilter    VARCHAR2(500) := '';

    BEGIN

        SELECT m01_app_completed_state,
               (m01_sima_charges + m01_transfer_charges) AS adminfee
          INTO v_close_state, v_admin_fee
          FROM m01_sys_paras;

          if stlStatus =1 then -- unsettled
            v_filterStr:='AND a.l01_current_level <> '||v_close_state;
          else if stlStatus =0 then -- settled
            v_filterStr:='AND a.l01_current_level = '||v_close_state;
            end if;
        end if;

        IF fromdate IS NOT NULL AND TRIM(fromdate) <> ''
               AND todate IS NOT NULL AND TRIM(todate) <> '' THEN
                v_dateFilter := ' AND TO_DATE(l14.l14_settlement_date, ''ddMMyyyy'') BETWEEN TO_DATE('''
                                || fromdate || ''', ''ddMMyyyy'') AND TO_DATE('''
                                || todate || ''', ''ddMMyyyy'') ';
          END IF;

          v_sql :='SELECT a.l01_finance_req_amt,
                   l14_ord_completed_value,
                   l06.l06_trading_acc_id,
                   NVL (b.l05_outstanding_amt - l14.l14_ord_completed_value, 0)
                       AS cumprofit,
                   l01_customer_id,
                   l06.l06_total_market_pf_value AS pf_market_value,
                   l06.l06_total_w_market_pf_value AS marginable_pf_value,
                   l01_full_name,
                   l01_app_id,
                   CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0 THEN
                     TO_CHAR(l01_app_id)
                   ELSE
                     TO_CHAR(l01_app_id) || ''R'' || TO_CHAR(l01_rollover_count)
                 END AS l01_display_application_id,
                   l05_operative_limit_amt,
                   l05_utilized_limit_amt,
                   l05_rem_operative_limit_amt,
                   l05_outstanding_amt,
                   l05_ftv,
                   l28.l28_ftv AS l05_ftv_pre_date,
                   (l14.l14_sima_charges + l14.l14_transfer_charges)
                       AS l01_admin_fee_charged,
                   l14.l14_created_date AS start_date,
                   l14.l14_l15_tenor_id * 30 AS tenor,
                   TO_DATE (l14.l14_settlement_date, ''ddMMyyyy'') AS expiry_date,
                   (TO_DATE (l14.l14_settlement_date, ''ddMMyyyy'') - TRUNC (SYSDATE))
                       AS days_left
              FROM l01_application a,
                   l05_collaterals b,
                   l14_purchase_order l14,
                   l06_trading_acc l06,
                   (SELECT a.l28_application_id, a.l28_date, a.l28_ftv
                      FROM l28_daily_ftv_log a
                     WHERE TRUNC (l28_date) = TRUNC (SYSDATE - 1)) l28
             WHERE     a.l01_app_id = b.l05_l01_app_id
                   AND a.l01_app_id = l14.l14_app_id
                   AND a.l01_app_id = l28.l28_application_id(+)
                   AND b.l05_collateral_id = l06.l06_l05_collateral_id
                   AND b.l05_l01_app_id = l06.l06_l01_app_id
                   AND l06.l06_is_lsf_type = 1
                   AND a.l01_overall_status > 0 '
                  -- AND TO_DATE (l14.l14_settlement_date, ''ddMMyyyy'') BETWEEN TO_DATE ('''|| fromdate ||''', ''ddMMyyyy'')
                  --                                  AND TO_DATE ('''|| todate ||''', ''ddMMyyyy'')

                   ||v_filterStr || v_dateFilter;
       OPEN pview FOR v_sql;
    END;

    PROCEDURE l05_update_initial_collateral (
        pl05_l01_app_id                 NUMBER,
        pl05_initial_cash_collateral    NUMBER,
        pl05_initial_pf_collateral       NUMBER)
    IS
    BEGIN
        UPDATE l05_collaterals
           SET l05_initial_cash_collateral = pl05_initial_cash_collateral,
               l05_initial_pf_collateral = pl05_initial_pf_collateral
         WHERE l05_l01_app_id = pl05_l01_app_id;
    END;

        PROCEDURE l05_get_commission_details (pview OUT refcursor,
        reportDate                 varchar2
        )
    IS
    BEGIN
      mubasher_lsf.get_ml_account_commission(pview,reportDate);
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L05_COLLATERALS_PKG

