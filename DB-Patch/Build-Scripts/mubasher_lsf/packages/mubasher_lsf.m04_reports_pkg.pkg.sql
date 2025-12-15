-- Start of DDL Script for Package MUBASHER_LSF.M04_REPORTS_PKG
-- Generated 19-Nov-2025 15:34:28 from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PACKAGE m04_reports_pkg
/* Formatted on 17-Nov-2016 11:10:42 (QP5 v5.206) */
IS
    --------------------------------------------------------------------------------
    ------------------------------REPORT CONFIGURATIONS-----------------------------

    PROCEDURE report_config (report_name   IN     VARCHAR2,
                             pview            OUT SYS_REFCURSOR);

    --------------------------------------------------------------------------------
    ---------------------------------DIB REPORTS------------------------------------

    PROCEDURE applied_customers (fromdate              IN     VARCHAR2,
                                 todate                IN     VARCHAR2,
                                 falacceptedval        IN     VARCHAR2,
                                 invofferacceptedval   IN     VARCHAR2,
                                 facilityavailed       IN     VARCHAR2,
                                 pview                    OUT SYS_REFCURSOR);

    PROCEDURE submitted_customers (fromdate   IN     VARCHAR2,
                                   todate     IN     VARCHAR2,
                                   pview         OUT SYS_REFCURSOR);

    PROCEDURE availed_customers (fromdate   IN     VARCHAR2,
                                 todate     IN     VARCHAR2,
                                 pview         OUT SYS_REFCURSOR);

    PROCEDURE facility_rejected (fromdate   IN     VARCHAR2,
                                 todate     IN     VARCHAR2,
                                 pview         OUT SYS_REFCURSOR);

    PROCEDURE confirmation_rejected (fromdate   IN     VARCHAR2,
                                     todate     IN     VARCHAR2,
                                     pview         OUT SYS_REFCURSOR);

    PROCEDURE accepted_customers (fromdate   IN     VARCHAR2,
                                  todate     IN     VARCHAR2,
                                  pview         OUT SYS_REFCURSOR);

    PROCEDURE liquidated_customers (fromdate   IN     VARCHAR2,
                                    todate     IN     VARCHAR2,
                                    pview         OUT SYS_REFCURSOR);

    PROCEDURE first_margin_customers (fromdate   IN     VARCHAR2,
                                      todate     IN     VARCHAR2,
                                      pview         OUT SYS_REFCURSOR);

    PROCEDURE second_margin_customers (fromdate   IN     VARCHAR2,
                                       todate     IN     VARCHAR2,
                                       pview         OUT SYS_REFCURSOR);

    PROCEDURE daily_position (pview OUT SYS_REFCURSOR);

    PROCEDURE availed_murabaha (fromdate      IN     VARCHAR2,
                                todate        IN     VARCHAR2,
                                murabaharef   IN     VARCHAR2,
                                customerref   IN     VARCHAR2,
                                pview            OUT SYS_REFCURSOR);

    --------------------------------------------------------------------------------
    ---------------------------------ABIC REPORTS-----------------------------------

    PROCEDURE m04_margin_info (fromdate   IN     VARCHAR2,
                               todate     IN     VARCHAR2,
                               pview         OUT SYS_REFCURSOR);

    PROCEDURE m04_margin_info_fields (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_finance_brokerage (pview            OUT SYS_REFCURSOR,
                                     customerref   IN     VARCHAR2,
                                     todate        IN     VARCHAR2);

    PROCEDURE m04_finance_brokerage_fields (pview       OUT SYS_REFCURSOR,
                                            todate   IN     VARCHAR2);

    PROCEDURE m04_share_trading (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_share_trading_fields (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_margin_call (fromdate   IN     VARCHAR2,
                               todate     IN     VARCHAR2,
                               pview OUT SYS_REFCURSOR);

    PROCEDURE m04_margin_call_fields (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_liquidation_call (fromdate   IN     VARCHAR2,
                                    todate     IN     VARCHAR2,
                                    pview OUT SYS_REFCURSOR);

    PROCEDURE m04_liquidation_call_fields (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_concentration (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_concentration_fields (pview OUT SYS_REFCURSOR);

    PROCEDURE m04_settlement_status_report (status   IN     NUMBER,
                                            pview       OUT SYS_REFCURSOR);

        PROCEDURE m04_settlement_list (settlementstatus   IN     NUMBER,
    fromdate varchar2,
    todate varchar2,
    pview       OUT SYS_REFCURSOR);

    procedure portfolio_wise_profit_report(fromdate varchar2,
    todate varchar2,
    pview       OUT SYS_REFCURSOR);

        procedure m04_performance_report(fromdate varchar2,
    todate varchar2,
    pview       OUT SYS_REFCURSOR);

        PROCEDURE m04_client_portfolio_overview (pview OUT SYS_REFCURSOR);

        PROCEDURE m04_charge_report(pview OUT SYS_REFCURSOR, customerref IN VARCHAR2);

        PROCEDURE m04_charge_report_summary(pview OUT SYS_REFCURSOR);

    --------------------------------------------------------------------------------
    ------------------------------------TESTING-------------------------------------

    PROCEDURE test_procedure (customerref   IN     VARCHAR2,
                              pview            OUT SYS_REFCURSOR);

    --------------------------------------------------------------------------------


    PROCEDURE applied_customers_count (
        fromdate              IN     VARCHAR2,
        todate                IN     VARCHAR2,
        falacceptedval        IN     VARCHAR2,
        invofferacceptedval   IN     VARCHAR2,
        facilityavailed       IN     VARCHAR2,
        pview                    OUT SYS_REFCURSOR);

    procedure getCshDtl_for_concetration_rtp(vdate in varchar2,pview                    OUT SYS_REFCURSOR);
procedure getCshDtl_for_rtp_today(pview                    OUT SYS_REFCURSOR);
    procedure getStkDtl_for_concetration_rtp(vdate in varchar2,pview                    OUT SYS_REFCURSOR);
   procedure getStkDtl_for_rtp_today(pview                    OUT SYS_REFCURSOR);

    PROCEDURE simah_report (
        pview                     OUT SYS_REFCURSOR,
        fromdate         IN     VARCHAR2,
        todate           IN     VARCHAR2);
END;
/



-- End of DDL Script for Package MUBASHER_LSF.M04_REPORTS_PKG



-- Start of DDL Script for Package Body MUBASHER_LSF.M04_REPORTS_PKG
-- Generated 19-Nov-2025 15:27:12 from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PACKAGE BODY m04_reports_pkg
/* Formatted on 7/25/2018 6:06:13 PM (QP5 v5.206) */
IS
    --------------------------------------------------------------------------------
    ------------------------------report configurations-----------------------------
    PROCEDURE report_config (report_name   IN     VARCHAR2,
                             pview            OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM m04_reports a
             WHERE a.m04_report_name = report_name;
    END;

    --------------------------------------------------------------------------------

    --------------------------------------------------------------------------------
    ---------------------------------dib reports------------------------------------
    --------------------------------------------------------------------------------

    --------------- 2.15.1  share finance facility applied customers ---------------
    PROCEDURE applied_customers (fromdate              IN     VARCHAR2,
                                 todate                IN     VARCHAR2,
                                 falacceptedval        IN     VARCHAR2,
                                 invofferacceptedval   IN     VARCHAR2,
                                 facilityavailed       IN     VARCHAR2,
                                 pview                    OUT SYS_REFCURSOR)
    IS
        app_state_flow_high   NUMBER := 0;
        app_state_flow_low    NUMBER := 100;
    BEGIN
        IF UPPER (facilityavailed) = 'true'
        THEN
            app_state_flow_low := 17;
            app_state_flow_high := 100;
        ELSIF UPPER (invofferacceptedval) = 'true'
        THEN
            app_state_flow_low := 13;
            app_state_flow_high := 17;
        ELSIF UPPER (falacceptedval) = 'true'
        THEN
            app_state_flow_low := 9;
            app_state_flow_high := 13;
        ELSE
            app_state_flow_low := 0;
            app_state_flow_high := 12;
        END IF;

        OPEN pview FOR
            SELECT a.l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   a.l01_app_id AS "application form ref",
                   TO_CHAR (a.l01_date, 'yyyy-mm-dd')
                       AS "application submitted date",
                   a.l01_full_name AS "customer first name",
                   a.l01_full_name AS "customer last name",
                   NVL (a.l01_occupation, ' ') AS "occupation",
                   a.l01_avg_monthly_income AS "average monthly income",
                   a.l01_dib_acc AS "dib a/c no",
                   a.l01_trading_acc AS "trading a/c no",
                   a.l01_finance_req_amt AS "finance requested amount",
                   a.l01_l15_tenor_id AS "tenor",
                   a.l01_mobile_no AS "mobile no",
                   a.l01_email AS "email",
                   CASE
                       WHEN a.l01_current_level >= 17 THEN 'true'
                       WHEN a.l01_current_level < 17 THEN 'false'
                   END
                       AS "facility availed",
                   CASE
                       WHEN a.l01_current_level >= 13 THEN 'true'
                       WHEN a.l01_current_level < 13 THEN 'false'
                   END
                       AS "investment offer accepted",
                   CASE
                       WHEN a.l01_current_level >= 9 THEN 'true'
                       WHEN a.l01_current_level < 9 THEN 'false'
                   END
                       AS "fal accepted"
              FROM l01_application a
             WHERE     a.l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                      AND TO_DATE (todate, 'yyyy-mm-dd') + 1
                   AND l01_current_level BETWEEN app_state_flow_low
                                             AND app_state_flow_high;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.2  investment offer submitted customers -------------------
    PROCEDURE submitted_customers (fromdate   IN     VARCHAR2,
                                   todate     IN     VARCHAR2,
                                   pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   a.l01_app_id AS "fal ref no",
                   a.l01_app_id AS "investment offer ref no",
                   TO_CHAR (a.l01_date, 'yyyy-mm-dd') AS "submitted date",
                   a.l01_full_name AS "customer first name",
                   a.l01_full_name AS "customer last name",
                   b.l05_approved_limit_amt AS "guidance limit",
                   a.l01_l15_tenor_id AS "tenor",
                   b.l05_net_total_collat AS "allocated collateral",
                   b.l05_operative_limit_amt AS "approved/operative limit",
                   b.l05_rem_operative_limit_amt AS "maximum purchase amount",
                   d.l07_cash_acc_id AS "share finance cash a/c no",
                   d.l07_cash_balance AS "cash balance (sf cash a/c)",
                   c.l06_trading_acc_id AS "share finance sec a/c no",
                   c.l06_total_market_pf_value AS "script value (sf sec a/c)"
              FROM l01_application a,
                   l05_collaterals b,
                   l06_trading_acc c,
                   l07_cash_account d
             WHERE     a.l01_app_id = b.l05_l01_app_id
                   AND a.l01_app_id = c.l06_l01_app_id
                   AND a.l01_app_id = d.l07_l01_app_id
                   AND c.l06_is_lsf_type = 1
                   AND d.l07_is_lsf_type = 1
                   AND a.l01_current_level >= 13
                   AND a.l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                      AND TO_DATE (todate, 'yyyy-mm-dd') + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.3  share finance facility availed customers ---------------
    PROCEDURE availed_customers (fromdate   IN     VARCHAR2,
                                 todate     IN     VARCHAR2,
                                 pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   a.l01_app_id AS "fal ref no",
                   a.l01_app_id AS "investment offer ref no",
                   TO_CHAR (a.l01_date, 'yyyy-mm-dd') AS "submitted date",
                   a.l01_full_name AS "customer first name",
                   a.l01_full_name AS "customer last name",
                   b.l05_approved_limit_amt AS "guidance limit",
                   a.l01_l15_tenor_id AS "tenor",
                   b.l05_net_total_collat AS "allocated collateral",
                   b.l05_operative_limit_amt AS "approved/operative limit",
                   b.l05_rem_operative_limit_amt AS "maximum purchase amount",
                   b.l05_utilized_limit_amt AS "availed operative limit",
                   b.l05_outstanding_amt AS "outstanding",
                   b.l05_rem_operative_limit_amt
                       AS "remaining operative limit",
                   d.l07_cash_acc_id AS "share finance cash a/c no",
                   d.l07_cash_balance AS "cash balance (sf cash a/c)",
                   c.l06_trading_acc_id AS "share finance sec a/c no",
                   c.l06_total_market_pf_value AS "script value (sf sec a/c)"
              FROM l01_application a,
                   l05_collaterals b,
                   l06_trading_acc c,
                   l07_cash_account d
             WHERE     a.l01_app_id = b.l05_l01_app_id
                   AND a.l01_app_id = c.l06_l01_app_id
                   AND a.l01_app_id = d.l07_l01_app_id
                   AND c.l06_is_lsf_type = 1
                   AND d.l07_is_lsf_type = 1
                   AND a.l01_current_level >= 50
                   AND a.l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                      AND TO_DATE (todate, 'yyyy-mm-dd') + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.4  share finance facility rejected customers --------------
    PROCEDURE facility_rejected (fromdate   IN     VARCHAR2,
                                 todate     IN     VARCHAR2,
                                 pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   TO_CHAR (l02_sts_changed_date, 'yyyy-mm-dd')
                       AS "fal rejected date",
                   l02_message AS "fal rejected reason",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit"
              FROM l01_application, l02_app_state, l05_collaterals
             WHERE     l01_app_id = l05_l01_app_id
                   AND l01_app_id = l02_l01_app_id
                   AND                            --l01_current_level = -8 and
                      l01_overall_status = -8
                   AND l02_level_id = -8
                   AND l02_sts_changed_date BETWEEN TO_DATE (fromdate,
                                                             'yyyy-mm-dd')
                                                AND   TO_DATE (todate,
                                                               'yyyy-mm-dd')
                                                    + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.5  murabaha confirmation rejected customers ---------------
    PROCEDURE confirmation_rejected (fromdate   IN     VARCHAR2,
                                     todate     IN     VARCHAR2,
                                     pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   confirmed AS "no of confirmed murabaha",
                   rejected AS "no of rejected murabaha",
                   action_date AS "murabaha confirmed date",
                   reason AS "rejected reason"
              FROM l01_application,
                   l05_collaterals,
                   (  SELECT MAX (l01_customer_id) AS customer_id,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = 1 THEN 1
                                 END)
                                 AS confirmed,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = -1
                                     THEN
                                         1
                                 END)
                                 AS rejected,
                             MAX (l14_customer_approve_date) AS action_date,
                             MAX (l14_customer_comment) AS reason
                        FROM l01_application, l14_purchase_order
                       WHERE     l01_customer_id = l14_customer_id
                             AND l14_customer_approve_date BETWEEN TO_DATE (
                                                                       fromdate,
                                                                       'yyyy-mm-dd')
                                                               AND   TO_DATE (
                                                                         todate,
                                                                         'yyyy-mm-dd')
                                                                   + 1
                    GROUP BY l01_customer_id)
             WHERE     l01_customer_id = customer_id
                   AND l01_app_id = l05_l01_app_id
                   AND rejected > 0;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.6  murabaha confirmation accepted customers ---------------
    PROCEDURE accepted_customers (fromdate   IN     VARCHAR2,
                                  todate     IN     VARCHAR2,
                                  pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   confirmed AS "no of confirmed murabaha",
                   rejected AS "no of rejected murabaha",
                   action_date AS "murabaha confirmed date"
              FROM l01_application,
                   l05_collaterals,
                   (  SELECT MAX (l01_customer_id) AS customer_id,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = 1 THEN 1
                                 END)
                                 AS confirmed,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = -1
                                     THEN
                                         1
                                 END)
                                 AS rejected,
                             MAX (l14_customer_approve_date) AS action_date
                        FROM l01_application, l14_purchase_order
                       WHERE     l01_customer_id = l14_customer_id
                             AND l14_customer_approve_date BETWEEN TO_DATE (
                                                                       fromdate,
                                                                       'yyyy-mm-dd')
                                                               AND   TO_DATE (
                                                                         todate,
                                                                         'yyyy-mm-dd')
                                                                   + 1
                    GROUP BY l01_customer_id)
             WHERE     l01_customer_id = customer_id
                   AND l01_app_id = l05_l01_app_id
                   AND confirmed > 0;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.7  portfolio liquidated customers -------------------------
    PROCEDURE liquidated_customers (fromdate   IN     VARCHAR2,
                                    todate     IN     VARCHAR2,
                                    pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   TO_CHAR (l01_date, 'yyyy-mm-dd') AS "submitted date",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   confirmed AS "no of confirmed murabaha",
                   ' ' AS "liquidated date",
                   l24_liquidate_reference AS "liquidate reason"
              FROM l01_application,
                   l05_collaterals,
                   l24_liquidation_log,
                   (  SELECT MAX (l01_customer_id) AS coutomer_id,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = 1 THEN 1
                                 END)
                                 AS confirmed,
                             MAX (l14_customer_approve_date) AS action_date
                        FROM l01_application, l14_purchase_order
                       WHERE l01_customer_id = l14_customer_id
                    GROUP BY l01_customer_id)
             WHERE     l01_app_id = l05_l01_app_id
                   AND l01_app_id = l24_l01_application_id
                   AND l01_customer_id = coutomer_id
                   AND l24_status >= 0
                   AND l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                    AND TO_DATE (todate, 'yyyy-mm-dd') + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.8  customers reached first margin level -------------------
    PROCEDURE first_margin_customers (fromdate   IN     VARCHAR2,
                                      todate     IN     VARCHAR2,
                                      pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   TO_CHAR (l01_date, 'yyyy-mm-dd') AS "submitted date",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   l05_utilized_limit_amt AS "availed operative limit",
                   l05_ftv AS "current ftv ratio",
                   ' ' AS "first margin date"
              FROM l01_application, l05_collaterals
             WHERE     l01_app_id = l05_l01_app_id
                   AND l05_first_margin_call > 0
                   AND l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                    AND TO_DATE (todate, 'yyyy-mm-dd') + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.9  customers reached second margin level ------------------
    PROCEDURE second_margin_customers (fromdate   IN     VARCHAR2,
                                       todate     IN     VARCHAR2,
                                       pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   TO_CHAR (l01_date, 'yyyy-mm-dd') AS "submitted date",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   l05_utilized_limit_amt AS "availed operative limit",
                   l05_ftv AS "current ftv ratio",
                   ' ' AS "second margin date"
              FROM l01_application, l05_collaterals
             WHERE     l01_app_id = l05_l01_app_id
                   AND l05_second_margin_call > 0
                   AND l01_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd')
                                    AND TO_DATE (todate, 'yyyy-mm-dd') + 1;
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.10 customer's daily position ------------------------------
    PROCEDURE daily_position (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer ref",
                   ' ' AS "customer cif no",
                   l01_app_id AS "fal ref no",
                   l01_app_id AS "investment offer ref no",
                   TO_CHAR (l01_date, 'yyyy-mm-dd') AS "submitted date",
                   l01_full_name AS "customer first name",
                   l01_full_name AS "customer last name",
                   l05_approved_limit_amt AS "guidance limit",
                   l01_l15_tenor_id AS "tenor",
                   l05_net_total_collat AS "allocated collateral",
                   l05_operative_limit_amt AS "approved/operative limit",
                   l05_rem_operative_limit_amt AS "maximum purchase amount",
                   l05_utilized_limit_amt AS "availed operative limit",
                   l05_outstanding_amt AS "outstanding",
                   l05_utilized_limit_amt AS "blocked amount",
                   l05_rem_operative_limit_amt AS "remaining operative limit",
                   l07_cash_acc_id AS "share finance cash a/c no",
                   l07_cash_balance AS "cash balance (sf cash a/c)",
                   l06_trading_acc_id AS "share finance sec a/c no",
                   l06_total_market_pf_value AS "script value (sf sec a/c)",
                   l05_ftv AS "ftv ratio",
                   confirmed AS "no of availed murabaha"
              FROM l01_application,
                   l05_collaterals,
                   l06_trading_acc,
                   l07_cash_account,
                   (  SELECT MAX (l01_customer_id) AS coutomer_id,
                             COUNT (
                                 CASE
                                     WHEN l14_customer_approve_state = 1 THEN 1
                                 END)
                                 AS confirmed,
                             MAX (l14_customer_approve_date) AS action_date
                        FROM l01_application, l14_purchase_order
                       WHERE l01_customer_id = l14_customer_id
                    GROUP BY l01_customer_id)
             WHERE     l01_app_id = l05_l01_app_id
                   AND l01_app_id = l06_l01_app_id
                   AND l01_app_id = l07_l01_app_id
                   AND l01_customer_id = coutomer_id
                   AND l06_is_lsf_type = 1
                   AND l07_is_lsf_type = 1
                   AND l01_current_level >= 50
                   AND TO_CHAR (l01_date, 'yyyy-mm-dd') =
                           TO_CHAR (SYSDATE, 'yyyy-mm-dd');
    END;

    --------------------------------------------------------------------------------
    --------------- 2.15.11 availed murabaha - customer wise -----------------------
    PROCEDURE availed_murabaha (fromdate      IN     VARCHAR2,
                                todate        IN     VARCHAR2,
                                murabaharef   IN     VARCHAR2,
                                customerref   IN     VARCHAR2,
                                pview            OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT l01_customer_id AS "customer ref",
                     l01_full_name AS "customer name",
                     l14_purchase_ord_id AS "murabaha facility ref",
                     l14_ord_value AS "murabaha amount",
                     TO_CHAR (l14_created_date, 'yyyy-mm-dd') AS "availed date",
                     TO_CHAR (
                         ADD_MONTHS (l14_created_date, l14_set_duration_months),
                         'yyyy-mm-dd')
                         AS "expiry date",
                     l14_ord_settlement_amount AS "settlement amount",
                     l14_profit_percentage AS "profit percentage",
                     l14_profit_amount AS "profit amount",
                     TO_CHAR (TO_DATE (l14_settlement_date, 'ddmmyyyy'),
                              'yyyy-mm-dd')
                         AS "settlement date"
                FROM l01_application, l14_purchase_order
               WHERE     l14_app_id = l01_app_id
                     AND (   (    murabaharef IS NOT NULL
                              AND l14_purchase_ord_id = murabaharef)
                          OR (murabaharef IS NULL))
                     AND (   (    customerref IS NOT NULL
                              AND l01_customer_id = customerref)
                          OR (customerref IS NULL))
                     AND l14_created_date BETWEEN TO_DATE (fromdate,
                                                           'yyyy-mm-dd')
                                              AND   TO_DATE (todate,
                                                             'yyyy-mm-dd')
                                                  + 1
            ORDER BY l01_customer_id;
          /*select l01_customer_id as "customer ref",
     l01_full_name as "customer name",
     '123' as "murabaha facility ref",
     '123' as "murabaha amount",
     '123' as "availed date",
     '123' as "expiry date",
     '123' as "settlement amount",
     '123' as "profit percentage",
     '123' as "profit amount",
     '123' as "settlement date"
from l01_application, l02_app_state
where
l01_app_id = l02_l01_app_id
order by l01_customer_id;*/
    END;

    --------------------------------------------------------------------------------


    --------------------------------------------------------------------------------
    ---------------------------------abic reports-----------------------------------
    --------------------------------------------------------------------------------

    --------------------------------------------------------------------------------
    ----------------- 1.11.01 margin information - tadawul - CMA Report -------------------------
    PROCEDURE m04_margin_info (fromdate   IN     VARCHAR2,
                               todate     IN     VARCHAR2,
                               pview         OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT COUNT (l01_app_id) AS "no of clients",
                   SUM (l14_ord_completed_value) AS "margin commitment",
                   (SUM (l14_ord_completed_value) + SUM (l23_cum_profit_amt))
                       AS "outstanding balance",
                   SUM (l06_total_market_pf_value) + SUM (l07_cash_balance)
                       AS "total portfolio market value",
                   SUM (l06_total_w_market_pf_value) + SUM (l07_cash_balance)
                       AS "portfolio value(weighted)",
                   SUM (outsamt) AS "client contribution",
                   SUM (outswamt) AS "client contribution weighted",
                   (CASE SUM (l06_total_market_pf_value)
                        WHEN 0
                        THEN
                            0
                        ELSE
                            (  SUM (outsamt)
                             / (  SUM (l06_total_market_pf_value)
                                + SUM (l07_cash_balance)))
                    END)
                       AS "margin ratio",
                   (CASE SUM (l06_total_w_market_pf_value)
                        WHEN 0
                        THEN
                            0
                        ELSE
                            (  SUM (outswamt)
                             / (  SUM (l06_total_w_market_pf_value)
                                + SUM (l07_cash_balance)))
                    END)
                       AS "margin ratio weighted",
                   COUNT (mrgcalls) AS "no of margin calls(7 Day)",
                   COUNT (liquidationmade)
                       AS "No Of liquidation calls(7 Day)",
                   SUM (NVL (l24_transfer_amount, 0))
                       AS "liquidated amount(7 Day)"
              FROM (SELECT a.l01_app_id,
                           NVL (l14.l14_ord_settlement_amount, 0)
                               AS l14_ord_settlement_amount,
                           NVL (l14.l14_ord_completed_value, 0)
                               AS l14_ord_completed_value,
                           NVL (l23.l23_cum_profit_amt, 0)
                               AS l23_cum_profit_amt,
                           NVL (l07_cash_balance, 0) AS l07_cash_balance,
                           NVL (l06_total_market_pf_value, 0)
                               AS l06_total_market_pf_value,
                           NVL (l06_total_w_market_pf_value, 0)
                               AS l06_total_w_market_pf_value,
                           (  (  NVL (l06_total_market_pf_value, 0)
                               + NVL (l07_cash_balance, 0))
                            - (  NVL (l14_ord_completed_value, 0)
                               + NVL (l23_cum_profit_amt, 0)) --l05.l05_outstanding_amt
                                                             )
                               AS outsamt,
                           (  (  NVL (l06_total_w_market_pf_value, 0)
                               + NVL (l07_cash_balance, 0))
                            - (  NVL (l14_ord_completed_value, 0)
                               + NVL (l23_cum_profit_amt, 0)) --l05.l05_outstanding_amt
                                                             )
                               AS outswamt,
                           (CASE l06_total_market_pf_value
                                WHEN 0
                                THEN
                                    0
                                ELSE
                                    (  (  l06_total_market_pf_value
                                        - (  NVL (l14_ord_completed_value, 0)
                                           + NVL (l23_cum_profit_amt, 0)))
                                     / l06_total_market_pf_value)
                            END)
                               AS marginratio,
                           (CASE l06_total_w_market_pf_value
                                WHEN 0
                                THEN
                                    0
                                ELSE
                                    (  (  l06_total_w_market_pf_value
                                        - (  NVL (l14_ord_completed_value, 0)
                                           + NVL (l23_cum_profit_amt, 0)))
                                     / l06_total_w_market_pf_value)
                            END)
                               AS marginwratio,
                           margine_calls AS mrgcalls,
                           l24_l01_application_id AS liquidationmade,
                           l24_transfer_amount
                      FROM l01_application a,
                           l14_purchase_order l14,
                           l05_collaterals l05,
                           (SELECT *
                              FROM l06_trading_acc
                             WHERE l06_is_lsf_type = 1) l06,
                           (SELECT *
                              FROM l07_cash_account
                             WHERE l07_is_lsf_type = 1) l07,
                           l24_liquidation_log l24,
                           (  SELECT l29_application_id,
                                     COUNT (1) AS margine_calls
                                FROM l29_margine_call_log
                               WHERE l29_margine_call_log.l29_date BETWEEN   TO_DATE (
                                                                                 fromdate,
                                                                                 'yyyy-mm-dd')
                                                                           - 7
                                                                       AND   TO_DATE (
                                                                                 todate,
                                                                                 'yyyy-mm-dd')
                                                                           + 1
                            GROUP BY l29_application_id) l29,
                           (SELECT l23_application_id,
                                   l23_profit_amt,
                                   l23_cum_profit_amt,
                                   l23_date
                              FROM (SELECT l23_application_id,
                                           l23_profit_amt,
                                           l23_cum_profit_amt,
                                           l23_date,
                                           RANK ()
                                           OVER (
                                               PARTITION BY l23_application_id
                                               ORDER BY l23_date DESC)
                                               dest_rank
                                      FROM l23_order_profit_log)
                             WHERE dest_rank = 1) l23
                     WHERE     a.l01_app_id = l14.l14_app_id
                           AND a.l01_app_id = l05.l05_l01_app_id
                           AND a.l01_app_id = l06.l06_l01_app_id
                           AND a.l01_app_id = l07.l07_l01_app_id(+)
                           AND a.l01_app_id = l24.l24_l01_application_id(+)
                           AND a.l01_app_id = l29.l29_application_id(+)
                           AND a.l01_app_id = l23.l23_application_id(+)
                           AND l01_overall_status > 0
                           AND l01_current_level < 18);
    -- and (((trunc(l14.l14_settled_date) between to_char (to_date (fromdate, 'yyyy-mm-dd'))
    -- and to_char (to_date (todate, 'yyyy-mm-dd')))
    -- or l14.l14_settled_date is null)
    --   and l01_overall_status>0)
    --      );
    END;


    PROCEDURE m04_margin_info_fields (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR SELECT 'lsf' AS "department" FROM DUAL;
    END;

    --------------------------------------------------------------------------------
    ------------------ 1.11.03 finance and brokerage report ------------------------
    PROCEDURE m04_finance_brokerage (pview            OUT SYS_REFCURSOR,
                                     customerref   IN     VARCHAR2,
                                     todate        IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
              SELECT TO_CHAR (NVL (l23_date, SYSDATE), 'dd-Mon-yyyy') AS "date",
                     l01_app_id,
                     l01.l01_customer_id AS "Customer Id",
                     l01_full_name AS "Customer Name",
                     l06_trading_acc_id AS "portfolio number",
                     NVL (l23_lsf_cash_acc_balance, 0) AS "available cash",
                     (l14_ord_completed_value + NVL (l23_cum_profit_amt, 0))
                         AS "Settlement amount",
                     ROUND (
                           l14_ord_completed_value
                         * 100
                         / NULLIF (
                               (  NVL (l23_lsf_cash_acc_balance, 0)
                                + NVL (l14_ord_completed_value, 0)),
                               0),
                         2)
                         AS "customer exposure",
                     TO_CHAR (l14_created_date, 'dd-Mon-yyyy')
                         AS "start of contract",
                     l14_ord_completed_value AS "purchase order value",
                     NVL (l23_profit_amt, 0) AS "Profit",
                     NVL (l23_cum_profit_amt, 0) AS "accumulated profit",
                     NVL (l14_profit_amount, 0) AS "Max contract profit",
                     (m01_sima_charges + m01_transfer_charges) AS "admin fee",
                     l05_ftv AS "coverage ratio",
                     (l14_profit_amount - NVL (l23_cum_profit_amt, 0))
                         AS "discount",
                     TO_CHAR (TO_DATE (l14_settlement_date, 'ddmmyyyy'),
                              'dd-mon-yyyy')
                         AS "contract maturity",
                     'no' AS "renewal status",
                     (l14_ord_completed_value + NVL (l23_cum_profit_amt, 0))
                         AS "settlement value",
                     l06_total_market_pf_value AS "holding value",
                     (  NVL (l23_lsf_cash_acc_balance, 0)
                      + l06_total_market_pf_value)
                         AS "total pf value",
                     '0' AS "contract renewal",
                     'Active' AS "Status",
                     CASE
                        WHEN m11.M11_FINANCE_METHOD = 1
                        THEN 'CS'
                        ELSE 'COMM'
                     END AS "Underlying Type"
                FROM l01_application l01,
                     l05_collaterals,
                     l06_trading_acc,
                     l14_purchase_order,
                     (SELECT *
                        FROM l23_order_profit_log
                       WHERE l23_date <= TO_DATE (todate, 'yyyy-mm-dd') + 1) l23,
                     l24_liquidation_log,
                     m01_sys_paras,
                     L32_APPOVE_AGREEMENTS l32,
                     M11_AGREEMENTS m11
               WHERE     l01_app_id = l05_l01_app_id
                     AND l01_app_id = l06_l01_app_id
                     AND l01_current_level < 18
                     AND l01_overall_status > 0
                     AND l06_is_lsf_type = 1
                     AND l01_app_id = l14_app_id
                     AND l01_app_id = l24_l01_application_id(+)
                     AND l01_app_id = l23.l23_application_id(+)
                     AND l01_customer_id = customerref
                     AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                     AND l32.L32_M11_ID=m11.M11_ID
            ORDER BY l23.l23_date;
    END;

    PROCEDURE m04_finance_brokerage_fields (pview       OUT SYS_REFCURSOR,
                                            todate   IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
              SELECT MAX("app id") AS "app id",
                     SUM ("available cash") AS "available cash",
                     SUM ("outstanding loan") AS "abic outstanding",
                     SUM ("customer exposure") AS "total exposure",
                     SUM ("accumulated profit") AS "accumulated profit",
                     SUM ("contract profit") AS "contract profit",
                     SUM ("total fees") AS "total fees",
                     SUM ("total commission") AS "total commission"
                FROM (SELECT l01_app_id,
                             l23_lsf_cash_acc_balance AS "available cash",
                             CASE
                                 WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                                       THEN
                                       TO_CHAR(l01_app_id)
                                   ELSE
                                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                                   END
                               AS "app id",
                             (  l14_ord_completed_value
                              + NVL (l23_cum_profit_amt, 0))
                                 AS "outstanding loan",
                             ROUND (
                                   (  l14_ord_completed_value
                                    + NVL (l23_cum_profit_amt, 0))
                                 * 100
                                 / NULLIF (
                                       (  NVL (l23_lsf_cash_acc_balance, 0)
                                        + (  l14_ord_completed_value
                                           + NVL (l23_cum_profit_amt, 0))),
                                       0),
                                 2)
                                 AS "customer exposure",
                             l23_cum_profit_amt AS "accumulated profit",
                             l14_profit_amount AS "contract profit",
                             (m01_sima_charges + m01_transfer_charges)
                                 AS "total fees",
                             (  NVL (l23_cum_profit_amt, 0)
                              + m01_sima_charges
                              + m01_transfer_charges)
                                 AS "total commission"
                        FROM l01_application,
                             (SELECT l14_profit_amount,
                                     l14_ord_settlement_amount,
                                     l14_app_id,
                                     l14_ord_completed_value
                                FROM l14_purchase_order
                               WHERE TRUNC (l14_created_date) <=
                                         TRUNC (TO_DATE (todate, 'yyyy-mm-dd'))),
                             (SELECT l23_application_id,
                                     l23_profit_amt,
                                     l23_cum_profit_amt,
                                     l23_lsf_cash_acc_balance,
                                     l23_date
                                FROM (SELECT l23_application_id,
                                             l23_profit_amt,
                                             l23_cum_profit_amt,
                                             l23_lsf_cash_acc_balance,
                                             l23_date,
                                             RANK ()
                                             OVER (
                                                 PARTITION BY l23_application_id
                                                 ORDER BY l23_date DESC)
                                                 dest_rank
                                        FROM l23_order_profit_log
                                       WHERE TRUNC (l23_date) <=
                                                 TRUNC (
                                                     TO_DATE (todate,
                                                              'yyyy-mm-dd')))
                               WHERE dest_rank = 1),
                             m01_sys_paras
                       WHERE     l01_current_level < 18
                             AND l01_overall_status > 0
                             AND l01_app_id = l14_app_id
                             AND l01_app_id = l23_application_id(+))
            GROUP BY l01_app_id;
    END;

    --------------------------------------------------------------------------------
    ---------------------- 1.11.04 share trading facility --------------------------
    PROCEDURE m04_share_trading (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT
              CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS "Application ID",
                     l01_full_name AS "customer name",
                     'manager' AS "relationship manager",
                     l06_trading_acc_id AS "portfolio account",
                     l07_cash_acc_id AS "cash account",
                     l05_ftv AS "actual coverage ratio",
                     l08_symbol_code AS "share code",
                     l08_short_desc AS "share name",
                     l10_liquid_name AS "share type",
                     CASE
                         WHEN b.lasttradeprice > 0
                         THEN
                             NVL (b.lasttradeprice, 0)
                         ELSE
                             NVL (b.previousclosed, 0)
                     END
                         AS "share price",
                     (l09_collat_qty + l09_transferred_qty) AS "no of shares",
                     CASE
                         WHEN b.lasttradeprice > 0
                         THEN
                             (  (l09_collat_qty + l09_transferred_qty)
                              * NVL (b.lasttradeprice, 0))
                         ELSE
                             (  (l09_collat_qty + l09_transferred_qty)
                              * NVL (b.previousclosed, 0))
                     END
                         AS "market value",
                     200 AS "approved coverage ratio",
                     m01_first_margine_call_percent AS "Notification ratio",
                     m01_liquid_margine_call_perent AS "Liquidation ratio",
                     l14_ord_value AS "t facilities",
                     l01_proposal_limit AS "facilities aginst shares",
                     (l14_ord_completed_value + NVL (l23_cum_profit_amt, 0))
                         AS "outstanding",
                    CASE
                        WHEN m11.M11_FINANCE_METHOD = 1
                        THEN 'CS'
                        ELSE 'COMM'
                    END AS "Underlying Type"
                FROM l01_application,
                     l05_collaterals,
                     l06_trading_acc,
                     l07_cash_account,
                     l09_trading_symbols,
                     l08_symbol,
                     l10_liquidity_type,
                     l14_purchase_order,
                     mubasher_oms.vw_dc_esp_todays_snapshots b,
                     L32_APPOVE_AGREEMENTS l32,
                     M11_AGREEMENTS m11,
                     (SELECT l23_application_id,
                             l23_profit_amt,
                             l23_cum_profit_amt,
                             l23_lsf_cash_acc_balance,
                             l23_date
                        FROM (SELECT l23_application_id,
                                     l23_profit_amt,
                                     l23_cum_profit_amt,
                                     l23_lsf_cash_acc_balance,
                                     l23_date,
                                     RANK ()
                                     OVER (PARTITION BY l23_application_id
                                           ORDER BY l23_date DESC)
                                         dest_rank
                                FROM l23_order_profit_log)
                       WHERE dest_rank = 1) l23,
                     m01_sys_paras
               WHERE     l01_app_id = l06_l01_app_id
                     AND l01_app_id = l07_l01_app_id
                     AND l01_app_id = l05_l01_app_id
                     AND l01_app_id = l23.l23_application_id(+)
                     AND l06_is_lsf_type = 1
                     AND l07_is_lsf_type = 1
                     AND l06_trading_acc_id = l09_l06_trading_acc_id
                     AND l09_l08_symbol_code = l08_symbol_code
                     AND l08_symbol_code = b.symbol
                     AND l08_l10_liquid_id = l10_liquid_id
                     AND L01_APP_ID=l32.L32_L01_APP_ID
                     AND l32.L32_M11_ID=m11.M11_ID
                     AND l01_app_id = l14_app_id
                     AND l14_settlement_status = 0
                     AND l01_overall_status > 0
            ORDER BY l06_trading_acc_id;
    END;

    PROCEDURE m04_share_trading_fields (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR SELECT 'lsf' AS "department" FROM DUAL;
    END;

    --------------------------------------------------------------------------------
    ---------------------- 1.11.05 margin call report --------------------------
    PROCEDURE m04_margin_call (fromdate   IN     VARCHAR2,
                               todate     IN     VARCHAR2,
                               pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer no",
                   l01_full_name AS "customer name",
                   l07_cash_acc_id AS "cash a/c no",
                   l06_trading_acc_id AS "security a/c no",
                   'sar' AS "currency",
                   NVL (l23_lsf_cash_acc_balance, 0) AS "cash a/c blance",
                   l06_total_market_pf_value AS "portfolio value",
                   (  NVL (l23_lsf_cash_acc_balance, 0)
                    + l06_total_market_pf_value)
                       AS "total assets",
                   m01_first_margine_call_percent AS "Margin limit",
                   m01_liquid_margine_call_perent AS "Liquidation limit",
                   l29_ftv AS "coverage",
                   l07_amt_as_collat AS "blocked cash",
                   l29_date AS "last notification date",
                   l29_date AS "last remind date",
                   CASE l29_margine_type
                       WHEN 1 THEN '1st margin'
                       WHEN 2 THEN '2nd margin'
                       WHEN 3 THEN 'liquidation'
                       ELSE 'margin'
                   END
                       AS "last notification type",
                   CASE
                        WHEN m11.M11_FINANCE_METHOD = 1
                        THEN 'CS'
                        ELSE 'COMM'
                   END AS "Underlying Type"
              FROM (SELECT l29_application_id,
                           l29_date,
                           l29_margine_type,
                           l29_ftv,
                           ROW_NUMBER ()
                           OVER (PARTITION BY l29_application_id
                                 ORDER BY l29_date DESC)
                               AS rn
                      FROM l29_margine_call_log
                     WHERE TRUNC (l29_date) BETWEEN TRUNC (TO_DATE (fromdate, 'yyyy-mm-dd'))
                                                AND (TO_DATE (todate, 'yyyy-mm-dd'))),
                   l01_application,
                   l06_trading_acc,
                   l07_cash_account,
                   L32_APPOVE_AGREEMENTS l32,
                   M11_AGREEMENTS m11,
                   (SELECT l23_application_id,
                           l23_profit_amt,
                           l23_cum_profit_amt,
                           l23_lsf_cash_acc_balance,
                           l23_date
                      FROM (SELECT l23_application_id,
                                   l23_profit_amt,
                                   l23_cum_profit_amt,
                                   l23_lsf_cash_acc_balance,
                                   l23_date,
                                   RANK ()
                                   OVER (PARTITION BY l23_application_id
                                         ORDER BY l23_date DESC)
                                       dest_rank
                              FROM l23_order_profit_log)
                     WHERE dest_rank = 1) l23,
                   m01_sys_paras
             WHERE     rn = 1
                   AND l29_application_id = l01_app_id
                   AND l29_application_id = l06_l01_app_id
                   AND l29_application_id = l07_l01_app_id
                   AND l01_app_id = l23.l23_application_id(+)
                   AND L01_APP_ID=l32.L32_L01_APP_ID
                   AND l32.L32_M11_ID=m11.M11_ID
                   AND l01_current_level < 18
                   AND l01_overall_status > 0
                   AND l06_is_lsf_type = 1
                   AND l07_is_lsf_type = 1;
    END;

    PROCEDURE m04_margin_call_fields (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR SELECT 'lsf' AS "department" FROM DUAL;
    END;

    --------------------------------------------------------------------------------
    ---------------------- 1.11.06 liquidation call report --------------------------
    PROCEDURE m04_liquidation_call (fromdate   IN     VARCHAR2,
                                    todate     IN     VARCHAR2,
                                    pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_customer_id AS "customer no",
                   l01_full_name AS "customer name",
                   l07_cash_acc_id AS "cash a/c no",
                   l06_trading_acc_id AS "security a/c no",
                   'sar' AS "currency",
                   NVL (l23_lsf_cash_acc_balance, 0) AS "cash a/c blance",
                   l06_total_market_pf_value AS "portfolio value",
                   (  NVL (l23_lsf_cash_acc_balance, 0)
                    + l06_total_market_pf_value)
                       AS "total assets",
                   m01_first_margine_call_percent AS "Margin limit",
                   m01_liquid_margine_call_perent AS "Liquidation limit",
                   l29_ftv AS "coverage",
                   l07_amt_as_collat AS "blocked cash",
                   l29_date AS "last notification date",
                   l29_date AS "last remind date",
                   CASE l29_margine_type
                       WHEN 1 THEN '1st margin'
                       WHEN 2 THEN '2nd margin'
                       WHEN 3 THEN 'liquidation'
                       ELSE 'margin'
                   END
                       AS "last notification type",
                   CASE
                        WHEN m11.M11_FINANCE_METHOD = 1
                        THEN 'CS'
                        ELSE 'COMM'
                   END AS "Underlying Type"
              FROM (SELECT l29_application_id,
                           l29_date,
                           l29_margine_type,
                           l29_ftv,
                           ROW_NUMBER ()
                           OVER (PARTITION BY l29_application_id
                                 ORDER BY l29_date DESC)
                               AS rn
                      FROM l29_margine_call_log
                     WHERE TRUNC (l29_date) BETWEEN TRUNC (TO_DATE (fromdate, 'yyyy-mm-dd'))
                                                AND TRUNC (TO_DATE (todate, 'yyyy-mm-dd'))),
                   l01_application,
                   l06_trading_acc,
                   l07_cash_account,
                   L32_APPOVE_AGREEMENTS l32,
                   M11_AGREEMENTS m11,
                   (SELECT l23_application_id,
                           l23_profit_amt,
                           l23_cum_profit_amt,
                           l23_lsf_cash_acc_balance,
                           l23_date
                      FROM (SELECT l23_application_id,
                                   l23_profit_amt,
                                   l23_cum_profit_amt,
                                   l23_lsf_cash_acc_balance,
                                   l23_date,
                                   RANK ()
                                   OVER (PARTITION BY l23_application_id
                                         ORDER BY l23_date DESC)
                                       dest_rank
                              FROM l23_order_profit_log)
                     WHERE dest_rank = 1) l23,
                   m01_sys_paras
             WHERE     rn = 1
                   AND l29_application_id = l01_app_id
                   AND l29_application_id = l06_l01_app_id
                   AND l29_application_id = l07_l01_app_id
                   AND l01_app_id = l23.l23_application_id(+)
                   AND L01_APP_ID=l32.L32_L01_APP_ID
                   AND l32.L32_M11_ID=m11.M11_ID
                   AND l06_is_lsf_type = 1
                   AND l07_is_lsf_type = 1
                   AND l01_current_level < 18
                   AND l01_overall_status > 0
                   AND l29_margine_type = 3;
    END;

    PROCEDURE m04_liquidation_call_fields (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR SELECT 'lsf' AS "department" FROM DUAL;
    END;

    --------------------------------------------------------------------------------
    ---------------------- 1.11.07 concentration report --------------------------
    PROCEDURE m04_concentration (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT l01_bank_brch_name AS "branch code",
                     l07_cash_acc_id AS "cash a/c no",
                     l06_trading_acc_id AS "investor a/c no",
                     l01_customer_id AS "customer no",
                     l01_full_name AS "customer name",
                     l01_proposal_limit AS "approved margin limit",
                     l14_ord_value AS "margin amount",
                     --'n/a' AS "online limit",
                     DECODE (NVL (l23_lsf_cash_acc_balance, 0),
                             0, l07_cash_balance,
                             l23_lsf_cash_acc_balance)
                         AS "cash balance",
                     l06_total_market_pf_value AS "total share value",
                     (  DECODE (NVL (l23_lsf_cash_acc_balance, 0),
                                0, l07_cash_balance,
                                l23_lsf_cash_acc_balance)
                      + l06_total_market_pf_value)
                         AS "total assets",
                     symbol AS "symbol code",
                     l09_transferred_qty AS "number of shares",
                     CASE
                         WHEN b.lasttradeprice > 0
                         THEN
                             NVL (b.lasttradeprice, 0)
                         ELSE
                             NVL (b.previousclosed, 0)
                     END
                         AS "market price",
                     CASE
                         WHEN b.lasttradeprice > 0
                         THEN
                             (  (l09_collat_qty + l09_transferred_qty)
                              * NVL (b.lasttradeprice, 0))
                         ELSE
                             (  (l09_collat_qty + l09_transferred_qty)
                              * NVL (b.previousclosed, 0))
                     END
                         AS "share value",
                     --'n/a' AS "region",
                     l18_stock_concentrate_perce AS "symbol concentration %",
                     --'' AS "concentration risk",
                     l05_ftv AS "coverage %",
                     m01_first_margine_call_percent AS "margin call %",
                     m01_second_margine_call_percen AS "reminder call %",
                     m01_liquid_margine_call_perent AS "liquidation call %",
                     CASE
                        WHEN m11.M11_FINANCE_METHOD = 1
                        THEN 'CS'
                        ELSE 'COMM'
                     END AS "Underlying Type"
                FROM l01_application l01,
                     l05_collaterals,
                     l06_trading_acc,
                     l07_cash_account,
                     l09_trading_symbols,
                     l08_symbol,
                     m01_sys_paras,
                     l14_purchase_order,
                     l18_stock_liquidity_type,
                     L32_APPOVE_AGREEMENTS l32,
                     M11_AGREEMENTS m11,
                     (SELECT l23_application_id,
                             l23_profit_amt,
                             l23_cum_profit_amt,
                             l23_lsf_cash_acc_balance,
                             l23_date
                        FROM (SELECT l23_application_id,
                                     l23_profit_amt,
                                     l23_cum_profit_amt,
                                     l23_lsf_cash_acc_balance,
                                     l23_date,
                                     RANK ()
                                     OVER (PARTITION BY l23_application_id
                                           ORDER BY l23_date DESC)
                                         dest_rank
                                FROM l23_order_profit_log)
                       WHERE dest_rank = 1) l23,
                     mubasher_oms.vw_dc_esp_todays_snapshots b
               WHERE     l01_app_id = l06_l01_app_id
                     AND l01_app_id = l07_l01_app_id
                     AND l01_app_id = l05_l01_app_id
                     AND l01_app_id = l23.l23_application_id(+)
                     AND l09_l08_symbol_code = b.symbol
                     AND l06_is_lsf_type = 1
                     AND l07_is_lsf_type = 1
                     AND l06_trading_acc_id = l09_l06_trading_acc_id(+)
                     AND l09_l08_symbol_code = l08_symbol_code(+)
                     AND l01_app_id = l14_app_id
                     AND l14_settlement_status = 0
                     AND l01_overall_status > 0
                     AND l08_l10_liquid_id = l18_liquid_id(+)
                     AND l01.l01_l12_stock_conc_grp_id = l18_stock_conc_grp_id
                     AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                     AND l32.L32_M11_ID=m11.M11_ID
                        ORDER BY l06_trading_acc_id;
    END;

    PROCEDURE m04_concentration_fields (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR SELECT 'lsf' AS "department" FROM DUAL;
    END;


    PROCEDURE m04_settlement_status_report (status   IN     NUMBER,
                                            pview       OUT SYS_REFCURSOR)
    IS
    BEGIN
        IF status = 1
        THEN
            BEGIN
                OPEN pview FOR
                      SELECT
                      CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS "application id",
                             l01_customer_id AS "customer id",
                             l01.l01_full_name AS "Customer Name",
                             TO_CHAR (l01_date, 'dd-Mon-yyyy')
                                 AS "application date",
                             TO_CHAR (l14.l14_settled_date, 'dd-Mon-yyyy')
                                 AS "Settle Date",
                             l06.l06_trading_acc_id AS "ML Trading Acc",
                             l05.l05_outstanding_amt AS "Settlement Amount",
                             CASE
                                WHEN m11.M11_FINANCE_METHOD = 1
                                THEN 'CS'
                                ELSE 'COMM'
                             END AS "Underlying Type"
                        FROM l01_application l01,
                             l14_purchase_order l14,
                             l06_trading_acc l06,
                             l05_collaterals l05,
                             L32_APPOVE_AGREEMENTS l32,
                             M11_AGREEMENTS m11
                       WHERE     l01_app_id = l14.l14_app_id
                             AND l01.l01_app_id = l06.l06_l01_app_id
                             AND l06.l06_is_lsf_type = 1
                             AND l01.l01_app_id = l05.l05_l01_app_id
                             AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                             AND l32.L32_M11_ID=m11.M11_ID
                             --AND l01_current_level = 18
                             AND l01_overall_status > 0
                             AND l14_settlement_status = status
                    ORDER BY l14_settled_date DESC;
            END;
        ELSE
            OPEN pview FOR
                  SELECT CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS "application id",
                         l01_customer_id AS "customer id",
                         l01.l01_full_name AS "Customer Name",
                         TO_CHAR (l01_date, 'dd-Mon-yyyy')
                             AS "application date",
                         TO_CHAR (
                             TO_DATE (l14.l14_settlement_date, 'ddmmyyyy'),
                             'dd-Mon-yyyy')
                             AS "Settle Date",
                         l06.l06_trading_acc_id AS "ML Trading Acc",
                         l05.l05_outstanding_amt AS "Settlement Amount",
                         CASE
                            WHEN m11.M11_FINANCE_METHOD = 1
                            THEN 'CS'
                            ELSE 'COMM'
                         END AS "Underlying Type"
                    FROM l01_application l01,
                         l14_purchase_order l14,
                         l06_trading_acc l06,
                         l05_collaterals l05,
                         L32_APPOVE_AGREEMENTS l32,
                         M11_AGREEMENTS m11
                   WHERE     l01_app_id = l14.l14_app_id
                         AND l01.l01_app_id = l06.l06_l01_app_id
                         AND l06.l06_is_lsf_type = 1
                         AND l01.l01_app_id = l05.l05_l01_app_id
                         AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                         AND l32.L32_M11_ID=m11.M11_ID
                         --AND l01_current_level < 18
                         AND l01_overall_status > 0
                         AND l14_settlement_status = status
                ORDER BY l14_settlement_date DESC;
        END IF;
    END;

    PROCEDURE m04_settlement_list (settlementstatus   IN     NUMBER,
                                   fromdate                  VARCHAR2,
                                   todate                    VARCHAR2,
                                   pview                 OUT SYS_REFCURSOR)
    IS
        v_sql         VARCHAR2 (2000);
        v_filterstr   VARCHAR2 (200) DEFAULT '';
    BEGIN
        IF settlementstatus = 0
        THEN                                                      -- unsettled
            v_filterstr := 'and l14.l14_settlement_status=0';
        ELSE
            IF settlementstatus = 1
            THEN                                                    -- settled
                v_filterstr := 'and l14.l14_settlement_status=1';
            END IF;
        END IF;

        v_sql :=
               'select to_char(TO_DATE (l14.l14_settlement_date, ''ddMMyyyy''),''dd-Mon-yyyy'') as l14_settlement_date,
                l01.l01_app_id,
                CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || ''R'' || TO_CHAR(l01_rollover_count)
                   END
                   AS l01_display_application_id,
                l01.l01_customer_id,l01.l01_full_name,l06.l06_trading_acc_id,
                l14.l14_ord_completed_value,
                l14.l14_settlement_status,l01.l01_acc_closed_status,
                l07.l07_cash_balance,
                (l05.l05_outstanding_amt - l14.l14_ord_completed_value) as cumProfit,l14.l14_customer_approve_state
                from l01_application l01,l14_purchase_order l14,l05_collaterals l05,l06_trading_acc l06,l07_cash_account l07
                where l01.l01_app_id=l14.l14_app_id
                and l01.l01_app_id=l05.l05_l01_app_id
                and l01.l01_app_id=l06.l06_l01_app_id(+)
                and l06.l06_is_lsf_type=1
                and l01.l01_app_id=l07.l07_l01_app_id(+)
                and l07.l07_is_lsf_type=1
                and TO_DATE (l14.l14_settlement_date, ''ddMMyyyy'') between TO_DATE ('''
            || fromdate
            || ''', ''ddMMyyyy'')
                                                    AND TO_DATE ('''
            || todate
            || ''', ''ddMMyyyy'') '
            || v_filterstr;

        OPEN pview FOR v_sql;
    END;

    PROCEDURE portfolio_wise_profit_report (fromdate       VARCHAR2,
                                            todate         VARCHAR2,
                                            pview      OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT
                 CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS "Contract",
                 l01.l01_customer_id AS "Customer Number",
                 l01.l01_full_name AS "Customer Name",
                 l06.l06_trading_acc_id AS "ML Portfolio No",
                 l14.L14_ORD_VALUE AS "Murabaha Amount",
                 l14.L14_PROFIT_AMOUNT AS "Profit Amount",
                 CONCAT(l14.l14_profit_percentage, '%') AS "Profit rate",

            --                   -- ML Start Date

                 to_char(TO_DATE (l14.L14_SETTLEMENT_DATE, 'ddMMyyyy'),'dd-Mon-yyyy')  AS "ML Expire Date",

                 CASE
                     WHEN l14.L14_SETTLED_DATE IS NOT NULL AND TO_DATE( l14.L14_SETTLEMENT_DATE, 'ddMMyyyy') != TRUNC (l14.L14_SETTLED_DATE)
                     THEN
                         to_char(TRUNC(l14.L14_SETTLED_DATE),'dd-Mon-yyyy')
                END AS "Early Settlement Day",

                -- Days YTD

                -- Profit Daily

                 NVL (l23.acc_profit, 0) AS "Accumulated Profit",

                CASE
                    WHEN l14.L14_ORD_SETTLED_AMOUNT IS NULL OR l14.L14_ORD_SETTLED_AMOUNT = 0
                    THEN '-'
                    ELSE to_char(l14.L14_ORD_SETTLED_AMOUNT)
                END AS "Settled Amount",

                -- No of Days

                CASE
                    WHEN l14.L14_SETTLEMENT_STATUS = 1
                    THEN 'Yes'
                    ELSE 'No'
                END AS "Settled",

                -- Monthly Profit

                CASE
                    WHEN m11.M11_FINANCE_METHOD = 1
                    THEN 'CS'
                    ELSE 'COMM'
                END AS "Underlying Type"

               FROM
                 l01_application l01,
                 l14_purchase_order l14,
                 l06_trading_acc l06,
                 L32_APPOVE_AGREEMENTS l32,
                 M11_AGREEMENTS m11,
                 (  SELECT a.l23_application_id,
                           SUM (a.l23_profit_amt) AS periodprofit,
                           MAX (l23_cum_profit_amt) AS acc_profit
                      FROM l23_order_profit_log a
                     WHERE TRUNC (l23_date) BETWEEN TRUNC (
                                                        TO_DATE (fromdate,
                                                                 'ddmmyyyy'))
                                                AND TRUNC (
                                                        TO_DATE (todate,
                                                                 'ddmmyyyy'))
                  GROUP BY l23_application_id) l23

               WHERE
                 l01.l01_app_id = l14.l14_app_id
                 AND l01.l01_app_id = l06.l06_l01_app_id(+)
                 AND l06.l06_is_lsf_type = 1
                 AND l01.l01_app_id = l23.l23_application_id(+)
                 AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                 AND l32.L32_M11_ID=m11.M11_ID
                And TRUNC (l01_date) BETWEEN TRUNC (
                                                        TO_DATE (fromdate,
                                                                 'ddmmyyyy'))
                                                AND TRUNC (
                                                        TO_DATE (todate,
                                                                 'ddmmyyyy'))
            ORDER BY l01_customer_id;
    END;

        PROCEDURE m04_performance_report (fromdate       VARCHAR2,
                                            todate         VARCHAR2,
                                            pview      OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT

                -- CIF

                l01.L01_CUSTOMER_ID AS "Customer Number",
                l01.L01_FULL_NAME AS "Customer Name",
                l35."Number of Contracts" AS "Number of Contracts",
                l06.l06_trading_acc_id AS "Portfolio Number",
                m07.M07_NAME AS "Product Type",

                CASE
                    WHEN m11.M11_FINANCE_METHOD = 1
                    THEN 'CS'
                    ELSE 'COMM'
                END AS "Underlying Type",

                to_char(TRUNC(l14.L14_CUSTOMER_APPROVE_DATE),'dd-Mon-yyyy') AS "Start Date",

                CASE
                     WHEN l14.L14_SETTLED_DATE IS NOT NULL
                     THEN
                         to_char(TRUNC(l14.L14_SETTLED_DATE),'dd-Mon-yyyy')
                    ELSE to_char(TO_DATE (l14.L14_SETTLEMENT_DATE, 'ddMMyyyy'),'dd-Mon-yyyy')
                END AS "Settlement Date",

                CASE
                    WHEN l14.L14_SETTLEMENT_STATUS = 1
                    THEN 'Settled'
                    ELSE 'Outstanding'
                END AS "Status",

                CASE
                    WHEN l01.L01_ROLLOVER_APP_ID IS NOT NULL
                    THEN 'Yes'
                    ELSE 'No'
                END AS "Rollover",

                (l15.L15_DURATION * 30) AS "Tenor",
                CONCAT(l14.L14_PROFIT_PERCENTAGE, '%') AS "Profit Rate",
                l14.L14_ORD_VALUE AS "Financing Amount",
                NVL (l36.acc_profit, 0) AS "Accumulated Profit",
                L14.L14_PROFIT_AMOUNT AS "Full Profit",

                -- Days

                CASE
                    WHEN l14.L14_SETTLED_DATE IS NOT NULL AND TO_DATE( l14.L14_SETTLEMENT_DATE, 'ddMMyyyy') != TRUNC (l14.L14_SETTLED_DATE)
                    THEN 'Yes'
                    ELSE 'No'
                END AS "Early Settlement"

              FROM
                    l01_application l01,
                    l14_purchase_order l14,
                    l06_trading_acc l06,
                    L15_TENOR l15,
                    M07_MURABAHA_PRODUCTS m07,
                    L32_APPOVE_AGREEMENTS l32,
                    M11_AGREEMENTS m11,
                    (
                        SELECT COUNT(L01_CUSTOMER_ID) AS "Number of Contracts", L01_CUSTOMER_ID
                        FROM L01_APPLICATION
                        WHERE
                            TRUNC(l01_date) BETWEEN TRUNC(TO_DATE(fromdate, 'ddMMyyyy'))
                                      AND TRUNC(TO_DATE(todate, 'ddMMyyyy'))
                        GROUP BY L01_CUSTOMER_ID
                    ) l35,
                    (  SELECT
                           a.l23_application_id,
                           MAX (l23_cum_profit_amt) AS acc_profit
                       FROM
                           l23_order_profit_log a
                       WHERE
                           TRUNC (l23_date) BETWEEN TRUNC (TO_DATE (fromdate, 'ddMMyyyy')) AND TRUNC (TO_DATE (todate, 'ddMMyyyy'))
                       GROUP BY l23_application_id
                    ) l36

              WHERE
                    l01.l01_app_id = l14.l14_app_id
                    AND l01.l01_app_id = l06.l06_l01_app_id(+)
                    AND l01.L01_L15_TENOR_ID = l15.L15_DURATION
                    AND l01.l01_app_id = l36.l23_application_id(+)
                    AND l01.L01_PRODUCT_TYPE = m07.M07_TYPE
                    AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                    AND l32.L32_M11_ID=m11.M11_ID

                    AND l06.l06_is_lsf_type = 1
                    AND l01.L01_CUSTOMER_ID = l35.L01_CUSTOMER_ID
                    And TRUNC (l01_date) BETWEEN TRUNC (TO_DATE (fromdate, 'ddMMyyyy')) AND TRUNC (TO_DATE (todate, 'ddMMyyyy'))

              ORDER BY l01.l01_customer_id;
    END;

        PROCEDURE m04_client_portfolio_overview (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
              SELECT

                -- CIF

                l01.L01_CUSTOMER_ID AS "Customer Number",
                l01.L01_FULL_NAME AS "Customer Name",
                l35.active_contracts AS "Active Contracts",
                l36."aggregated_financing" AS "Aggregated Financing"

                -- Overall CR
                -- Concentration %

             FROM
                l01_application l01,
                (
                    SELECT
                        COUNT(l14.l14_purchase_ord_id) AS active_contracts, l14.L14_app_id
                    FROM l14_purchase_order l14, L01_APPLICATION l01
                    WHERE l01.L01_APP_ID = l14.L14_APP_ID AND l14.l14_settlement_status = 0 AND l01.l01_current_level < 18
                    GROUP BY l14.L14_app_id
                ) l35,
                (
                    SELECT SUM(l14.L14_ORD_VALUE) as "aggregated_financing", l01.L01_CUSTOMER_ID
                    FROM L01_APPLICATION l01, L14_PURCHASE_ORDER l14
                    WHERE l01.L01_APP_ID=l14.L14_APP_ID
                    GROUP BY l01.L01_CUSTOMER_ID
                ) l36

             WHERE
                l01.l01_app_id = l35.L14_app_id
                AND l01.L01_CUSTOMER_ID=l36.L01_CUSTOMER_ID
            ORDER BY l01.L01_CUSTOMER_ID;
    END;

        PROCEDURE m04_charge_report(pview OUT SYS_REFCURSOR, customerref IN VARCHAR2)
            IS
            BEGIN
                OPEN pview FOR
                    SELECT

                        -- Customer ID
                        -- CIF

                        l01.L01_CUSTOMER_ID AS "Customer Number",
                        l01.L01_FULL_NAME AS "Customer Name",
                        l01.L01_L15_TENOR_ID AS "Period",
                        l14.L14_ORD_VALUE AS "Contract Amount",

                        -- Penalty Period (%)
                        -- Penalty Amount

                        CASE
                            WHEN m11.M11_FINANCE_METHOD = 1
                            THEN 'CS'
                            ELSE 'COMM'
                        END AS "Financing Type",

                        CASE
                            WHEN m11.M11_FINANCE_METHOD = 1
                            THEN (SELECT M01_SHARE_ADMIN_FEE FROM M01_SYS_PARAS)
                            ELSE (SELECT M01_COMODITY_ADMIN_FEE FROM M01_SYS_PARAS)
                        END AS "Admin Fees"

                    FROM
                        l01_application l01,
                        L14_PURCHASE_ORDER l14,
                        L32_APPOVE_AGREEMENTS l32,
                        M11_AGREEMENTS m11

                    WHERE
                        l01.L01_APP_ID=l14.L14_APP_ID
                        AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                        AND l32.L32_M11_ID=m11.M11_ID
                        AND l01.L01_CUSTOMER_ID = customerref;
            END;

        PROCEDURE m04_charge_report_summary(pview OUT SYS_REFCURSOR)
            IS
            BEGIN
                OPEN pview FOR
                    SELECT

                        -- Customer ID
                        -- CIF

                        l01.L01_CUSTOMER_ID AS "Customer Number",
                        l01.L01_FULL_NAME AS "Customer Name",
                        l01.L01_L15_TENOR_ID AS "Period",
                        l14.L14_ORD_VALUE AS "Contract Amount",

                        -- Penalty Period (%)
                        -- Penalty Amount

                        CASE
                            WHEN m11.M11_FINANCE_METHOD = 1
                            THEN 'CS'
                            ELSE 'COMM'
                        END AS "Financing Type",

                        CASE
                            WHEN m11.M11_FINANCE_METHOD = 1
                            THEN (SELECT M01_SHARE_ADMIN_FEE FROM M01_SYS_PARAS)
                            ELSE (SELECT M01_COMODITY_ADMIN_FEE FROM M01_SYS_PARAS)
                        END AS "Admin Fees"

                    FROM
                        l01_application l01,
                        L14_PURCHASE_ORDER l14,
                        L32_APPOVE_AGREEMENTS l32,
                        M11_AGREEMENTS m11

                    WHERE
                        l01.L01_APP_ID=l14.L14_APP_ID
                        AND l01.L01_APP_ID=l32.L32_L01_APP_ID
                        AND l32.L32_M11_ID=m11.M11_ID;
            END;

    --------------------------------------------------------------------------------
    ------------------------------------testing-------------------------------------
    --------------------------------------------------------------------------------
    --------------- x.x.x test procedure - works as template -----------------------
    PROCEDURE test_procedure (customerref   IN     VARCHAR2,
                              pview            OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT 'hello' AS "field 1",
                   'world' AS field2,
                   'of' AS field3,
                   'beauti' AS field4
              FROM DUAL;
    END;

    --------------------------------------------------------------------------------
    ------------------------------ count queries -----------------------------------
    --------------------------------------------------------------------------------

    PROCEDURE applied_customers_count (
        fromdate              IN     VARCHAR2,
        todate                IN     VARCHAR2,
        falacceptedval        IN     VARCHAR2,
        invofferacceptedval   IN     VARCHAR2,
        facilityavailed       IN     VARCHAR2,
        pview                    OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT COUNT (*)
              FROM l01_application a;
    END;

    PROCEDURE getcshdtl_for_concetration_rtp (vdate   IN     VARCHAR2,
                                              pview      OUT SYS_REFCURSOR)
    IS
    BEGIN
        mubasher_oms.pkg_ml_data.ml_type_acc_details (pview, vdate);
    END;

    PROCEDURE getstkdtl_for_concetration_rtp (vdate   IN     VARCHAR2,
                                              pview      OUT SYS_REFCURSOR)
    IS
    BEGIN
        mubasher_oms.pkg_ml_data.ml_symbol_wise_holdings (pview, vdate);
    END;

    PROCEDURE getstkdtl_for_rtp_today (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l09_l08_symbol_code AS S01_SYMBOL,
                   l08.l08_short_desc as SHORT_DESCRIPTION,
                   pl09_available_qty as NET_HOLDINGS,
                   CASE
                       WHEN b.lasttradeprice > 0
                       THEN
                           NVL (b.lasttradeprice, 0)
                       ELSE
                           NVL (b.previousclosed, 0)
                   END
                        as PREVIOUSE_CLOSED,
                   CASE
                       WHEN b.lasttradeprice > 0
                       THEN
                           (pl09_available_qty * NVL (b.lasttradeprice, 0))
                       ELSE
                           (pl09_available_qty * NVL (b.previousclosed, 0))
                   END
                       AS L08_MARKET_VALUE
              FROM (  SELECT l09_l08_symbol_code,
                             SUM (l09_available_qty) AS pl09_available_qty
                        FROM l09_trading_symbols a,
                             l06_trading_acc l06,
                             l01_application l01
                       WHERE     l06.l06_l01_app_id = l01.l01_app_id
                             AND a.l09_l01_app_id = l01.l01_app_id
                             AND a.l09_l06_trading_acc_id =
                                     l06.l06_trading_acc_id
                             AND l01.l01_overall_status > 0
                             -- and l01.l01_current_level >=16
                             AND l01.l01_current_level < 18
                             AND l06.l06_is_lsf_type = 1
                    GROUP BY l09_l08_symbol_code) a,
                   mubasher_oms.vw_dc_esp_todays_snapshots b,
                   l08_symbol l08
             WHERE     a.l09_l08_symbol_code = b.symbol
                   AND a.l09_l08_symbol_code = l08.l08_symbol_code;
    END;

    PROCEDURE getcshdtl_for_rtp_today (pview OUT SYS_REFCURSOR)
    IS
    BEGIN
        OPEN pview FOR
            SELECT SUM (a.l07_cash_balance) AS total_buying_power,
                   SUM (a.l07_pending_settle) AS total_pending_settle,
                   SUM (a.l07_net_receivable) AS total_net_receivable
              FROM l07_cash_account a, l01_application l01
             WHERE     l01.l01_app_id = a.l07_l01_app_id
                   AND l01.l01_overall_status > 0
                   AND l01.l01_current_level >= 16
                   AND l01.l01_current_level < 18
                   AND a.l07_is_lsf_type = 1;
    END;

     PROCEDURE simah_report (
        pview                  OUT SYS_REFCURSOR,
        fromdate         IN     VARCHAR2,
        todate           IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
            SELECT
            CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS "Credit Instrument Number",
            TO_CHAR (l01_date, 'dd/mm/yyyy') AS "Issue Date",
            'MGLD' AS "Product Type",
            L01_FINANCE_REQ_AMT AS "Prod. Limit / Orig. Amt",
            'N' AS "Salary Assignment Flag",
            TO_CHAR(to_date(l14.l14_settlement_date,'DDMMYYYY'),'DD-MM-YYYY') AS "Product Expiry Date",
                        CASE
                WHEN l01_overall_status < 0 THEN 'C'
                ELSE
                    CASE
                        WHEN l14.l14_settlement_status = 1 THEN 'C'
                        ELSE 'A'
                    END
            END AS "Product Status",
            -- decode(l14.l14_settlement_status,1,'C','A') AS "Product Status",
            L14_ORD_VALUE AS "Instalment Amount",
            L14_ORD_VALUE AS "Average Instalment Amount",
            'O' AS "Payment Frequency",
            L01_L15_TENOR_ID AS "Tenure",
            'SH' AS "Security Type.",
            '' AS "Down Payment",
            '' AS "Balloon Payment",
            '' AS "Dispensed Amount",
            '' AS "Max Instalment Amount",
            'NAPP' AS "Sub Product Type",
            '' AS "Total leasing amount",
            '' AS "Reason For Closure Code",
            '' AS "Factoring Flag",
            '' AS "Installment Start date",
            '' AS "Installment amount",
            '' AS "Installment Start date",
            '' AS "Installment amount",
            '' AS "Installment Start date",
            '' AS "Installment amount",
            '' AS "Installment Start date",
            '' AS "Installment amount",
            '' AS "Installment Start date  ",
            '' AS "Installment amount",
            L01_APP_ID AS "Contract Number",
            '' AS "First Installment date",
            '' AS "Cost Rate",
            '' AS "Amount Rate",
            '' AS "Fixed Rate",
            1 AS "No of Credit Holders",
            to_char(TRUNC(L01_DATE),'DD-MM-YYYY') AS "Cycle ID",
            '' AS "Last Payment Date",
            '' AS "Last Amount Paid",
            CASE L14_SETTLEMENT_STATUS WHEN 1 THEN 'C' ELSE '0' END AS "Payment Status",
            L14_ORD_SETTLEMENT_AMOUNT AS "Outstanding balance.",
            '' AS "Past Due balance.",
            '' AS "As of Date",
            '' AS "Next payment Date",
            '' AS "Prefer Method of payment",
            '' AS "Number of paid Instalment",
            '' AS "Early Payoff",
            '' AS "Number of unpaid Instalment",
            '' AS "Amount paid to 3rd party",
            '' AS "Right of Withdrawal Code",
            '' AS "APR percentage",
            '' AS "Termination Procedure Code",
            '' AS "Ownership Change Code",
            '' AS "Legal Ownership Amt",
            '' AS "Admin 1 Fees",
            '' AS "Payment Type",
            '' AS "Amt of Pending Installments",
            '' AS "Amount of unpaid installments",
            'T' AS "ID type",
            cstInfo.NIN AS "Consumer ID",
            '' AS "ID Expiration Date",
            '' AS "ID/Iqama Issued Place",
            'M' AS "Marital status",
            'SAU' AS "Nationality code",
            '' AS "Family Name - Arabic",
            '' AS "First Name - Arabic",
            '' AS "CNM2A",
            '' AS "CNM3A",
            '' AS "Full Name - Arabic",
            '' AS "Family name - English",
            '' AS "First name - English",
            '' AS "CNM2E",
            '' AS "CNM3E",
            L01_FULL_NAME AS "Full Name - English",
            '' AS "Date Of Birth",
            '' AS "Gender",
            '' AS "Number of Dependence",
            'P' AS "Applicant Type",
            '' AS "Percentage Allocation",
            '' AS "Applicant Outstanding Balance",
            '' AS "Applicant Limit",
            '' AS "Applicant Last Amount Paid",
            '' AS "Applicant Instalment Amount",
            '' AS "Applicant Last Payment Date",
            '' AS "Applicant Next Due Date",
            '' AS "Applicant Past Due Balance",
            '' AS "Applicant Payment Status",
            '' AS "Building Number",
            '' AS "Street English",
            '' AS "Street Arabic",
            '' AS "District English",
            '' AS "District Arabic",
            '' AS "Additional Number",
            '' AS "Until Number",
            '' AS "Building Number",
            '' AS "Street English",
            '' AS "Street Arabic",
            '' AS "District English",
            '' AS "District Arabic",
            '' AS "Additional Number",
            '' AS "Unit Number",
            '' AS "Other income"
                  FROM l01_application l01
                          INNER JOIN (
                                    SELECT l14_app_id,
                                           MAX(l14_purchase_ord_id) AS latest_ord_id
                                    FROM l14_purchase_order
                                    GROUP BY l14_app_id
                                ) latest_l14 ON l01.L01_APP_ID = latest_l14.l14_app_id
                                INNER JOIN l14_purchase_order l14 ON latest_l14.latest_ord_id = l14.l14_purchase_ord_id
                                LEFT JOIN l34_purchase_order_commodities l34 ON l14.l14_purchase_ord_id = l34.l34_l16_purchase_ord_id
                                INNER JOIN CUSTOMER_INFO_SYNC cstInfo ON l01.L01_CUSTOMER_ID = cstInfo.CUSTOMER_ID
                                WHERE ((l14.l14_settled_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd') AND TO_DATE (todate, 'yyyy-mm-dd') + 1)
                                      OR (l01.L01_CURRENT_LEVEL > 15 AND l14_settlement_status = 0) )
                                      AND l01.l01_overall_status > 0;
                                 -- AND l01.L01_DATE <= TO_DATE(todate, 'yyyy-mm-dd');
                                 -- l14.l14_customer_approve_date BETWEEN TO_DATE (fromdate, 'yyyy-mm-dd') AND TO_DATE (todate, 'yyyy-mm-dd') + 1

    END;
END;
/



-- End of DDL Script for Package Body MUBASHER_LSF.M04_REPORTS_PKG

