-- Start of DDL Script for Package MUBASHER_LSF.R01_REPORTS
-- Generated 17-Nov-2025 10:52:58 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE              mubasher_lsf.r01_reports
/* Formatted on 11/2/2015 1:19:12 PM (QP5 v5.206) */
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

    PROCEDURE r01_report_margin_info (pview      OUT refcursor,
                                      fromdate       VARCHAR2,
                                      todate         VARCHAR2);

    PROCEDURE r01_finance_brokerage_info (pview OUT refcursor);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.r01_reports TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.r01_reports TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.r01_reports TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.r01_reports TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.r01_reports TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.r01_reports
/* Formatted on 11/2/2015 1:18:58 PM (QP5 v5.206) */
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

    PROCEDURE r01_report_margin_info (pview      OUT refcursor,
                                      fromdate       VARCHAR2,
                                      todate         VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
              SELECT COUNT (l01_app_id) AS noofclient,
                     SUM (l14_ord_settlement_amount) AS margincommitment,
                     SUM (l14_ord_settlement_amount) AS outstandingbalance,
                     SUM (l06_total_market_pf_value) AS totalpfmktvalue,
                     SUM (l06_total_w_market_pf_value) AS totalpfmktvalue_w,
                     SUM (outsamt) AS clientcontributintomktvalue,
                     SUM (outswamt) AS clientcontributintomktvalue_w,
                     (SUM (outsamt) / SUM (l06_total_market_pf_value))
                         AS marginratio,
                     (SUM (outswamt) / SUM (l06_total_w_market_pf_value))
                         AS marginratio_w,
                     COUNT (mrgcalls) AS noofmargincalls,
                     COUNT (liquidationmade) AS noofliqudationcalls,
                     SUM (l24_transfer_amount) AS liqudatedamt
                FROM (SELECT a.l01_app_id,
                             l14.l14_ord_settlement_amount,
                             l06_total_market_pf_value,
                             l06_total_w_market_pf_value,
                             (  l06_total_market_pf_value
                              - l05.l05_outstanding_amt)
                                 AS outsamt,
                             (  l06_total_w_market_pf_value
                              - l05.l05_outstanding_amt)
                                 AS outswamt,
                             (  (  l06_total_market_pf_value
                                 - l05.l05_outstanding_amt)
                              / l06_total_market_pf_value)
                                 AS marginratio,
                             (  (  l06_total_w_market_pf_value
                                 - l05.l05_outstanding_amt)
                              / l06_total_w_market_pf_value)
                                 AS marginwratio,
                             1 AS mrgcalls,
                             1 AS liquidationmade,
                             l24_transfer_amount
                        FROM l01_application a,
                             l14_purchase_order l14,
                             l05_collaterals l05,
                             l06_trading_acc l06,
                             l24_liquidation_log l24
                       WHERE     a.l01_app_id = l14.l14_app_id
                             AND a.l01_app_id = l05.l05_l01_app_id
                             AND a.l01_app_id = l06.l06_l01_app_id
                             AND l06.l06_is_lsf_type = 1
                             AND a.l01_app_id = l24.l24_l01_application_id(+)
                             AND l14.l14_created_date >=
                                     TO_CHAR (TO_DATE (fromdate, 'yyyy-MM-dd'))
                             AND l14.l14_created_date <=
                                     TO_CHAR (TO_DATE (todate, 'yyyy-MM-dd')))
            GROUP BY l01_app_id;
    END;

    PROCEDURE r01_finance_brokerage_info (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01_full_name AS customer_name,
                   l06_trading_acc_id AS portfolio_number,
                   l14_ord_value AS outstanding_loan,
                   ROUND (
                       (l14_ord_value / (l07_cash_balance + abic_outstanding)),
                       4)
                       AS customer_exposure,
                   0.5 AS accumulated_profit,
                   l14_profit_amount AS contract_profit,
                   m01_admin_fee AS total_fees,
                   10.5 AS total_commission
              -- accumulated_profit

              FROM (SELECT a.l01_full_name,
                           b.l06_trading_acc_id,
                           c.l14_ord_value,
                           d.l07_cash_balance,
                           e.m01_admin_fee,
                           c.l14_profit_amount
                      FROM l01_application a,
                           l06_trading_acc b,
                           l14_purchase_order c,
                           l07_cash_account d,
                           m01_sys_paras e
                     WHERE     a.l01_app_id = b.l06_l01_app_id
                           AND a.l01_app_id = c.l14_app_id
                           AND a.l01_app_id = d.l07_l01_app_id
                           AND b.l06_is_lsf_type = 1
                           AND d.l07_is_lsf_type = 1
                           AND a.l01_current_level = 16
                           AND a.l01_overall_status = 15),
                   (SELECT SUM (a.l14_ord_value) AS abic_outstanding
                      FROM l14_purchase_order a, l01_application b
                     WHERE     b.l01_app_id = a.l14_app_id
                           AND b.l01_current_level = 16
                           AND b.l01_overall_status = 15);
    END;
-- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.R01_REPORTS

