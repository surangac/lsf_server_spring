-- Start of DDL Script for Package MUBASHER_LSF.L23_ORDER_PROFIT_LOG_PKG
-- Generated 17-Nov-2025 10:52:53 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE                           mubasher_lsf.l23_order_profit_log_pkg
IS
    TYPE refcursor IS REF CURSOR;

    -- Existing procedures
    PROCEDURE l23_add (pkey                  OUT NUMBER,
                       pl23_application_id       NUMBER,
                       pl23_order_id             NUMBER,
                       pl23_profit_amt           NUMBER,
                       pl23_cum_profit_amt       NUMBER,
                       pl23_lsf_cash_acc_balance    number default 0);

    -- NEW: Add with specific date
    PROCEDURE l23_add_with_date (pkey                  OUT NUMBER,
                                 pl23_application_id       NUMBER,
                                 pl23_order_id             NUMBER,
                                 pl23_profit_amt           NUMBER,
                                 pl23_cum_profit_amt       NUMBER,
                                 pl23_lsf_cash_acc_balance    NUMBER DEFAULT 0,
                                 pl23_date                 DATE);

    PROCEDURE l23_get_lstone (pview                 OUT refcursor,
                              pl23_application_id       NUMBER,
                              pl23_order_id             NUMBER);

    PROCEDURE l23_get_summation_application (
        pview                 OUT refcursor,
        pl23_application_id       NUMBER,
        pl23_order_id             NUMBER);

    PROCEDURE l23_get_all_for_application (pview                 OUT refcursor,
                                           pl23_application_id       NUMBER,
                                           pl23_order_id             NUMBER);

    PROCEDURE l23_get_entry_for_date (pview                 OUT refcursor,
                                      pl23_application_id       NUMBER,
                                      pl23_date_str             varchar2);

    -- NEW: Check if profit entry exists for specific date
    PROCEDURE l23_check_entry_exists (pkey               OUT NUMBER,
                                      pl23_application_id       NUMBER,
                                      pl23_order_id             NUMBER,
                                      pl23_date                 DATE);

    -- NEW: Get profit entry for specific date with order ID
    PROCEDURE l23_get_entry_by_date_order (pview                 OUT refcursor,
                                           pl23_application_id       NUMBER,
                                           pl23_order_id             NUMBER,
                                           pl23_date                 DATE);

    -- NEW: Get last profit entry (most recent by date)
    PROCEDURE l23_get_last_entry (pview                 OUT refcursor,
                                  pl23_application_id       NUMBER,
                                  pl23_order_id             NUMBER);

    PROCEDURE l23_correct_profit_entry (pkey                  OUT NUMBER,
                                       pdate_str      IN VARCHAR2,
                                       papp_id        IN VARCHAR2,
                                       pcustomer_id      VARCHAR2);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l23_order_profit_log_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l23_order_profit_log_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l23_order_profit_log_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l23_order_profit_log_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l23_order_profit_log_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY                           mubasher_lsf.l23_order_profit_log_pkg
IS
    -- Existing l23_add procedure (unchanged)
    PROCEDURE l23_add (pkey                        OUT NUMBER,
                       pl23_application_id             NUMBER,
                       pl23_order_id                   NUMBER,
                       pl23_profit_amt                 NUMBER,
                       pl23_cum_profit_amt             NUMBER,
                       pl23_lsf_cash_acc_balance       NUMBER DEFAULT 0)
    IS
    BEGIN
        DELETE FROM l23_order_profit_log
         WHERE     l23_application_id = pl23_application_id
               AND l23_order_id = pl23_order_id
               AND TRUNC (l23_date) = TRUNC (SYSDATE);

        INSERT INTO l23_order_profit_log (l23_application_id,
                                          l23_order_id,
                                          l23_date,
                                          l23_profit_amt,
                                          l23_cum_profit_amt,
                                          l23_lsf_cash_acc_balance)
        VALUES (pl23_application_id,
                pl23_order_id,
                SYSDATE,
                pl23_profit_amt,
                pl23_cum_profit_amt,
                pl23_lsf_cash_acc_balance);

        pkey := 1;
    EXCEPTION
        WHEN OTHERS
        THEN
            pkey := -1;
    END;

    -- NEW: Add profit entry with specific date
    PROCEDURE l23_add_with_date (pkey                        OUT NUMBER,
                                 pl23_application_id             NUMBER,
                                 pl23_order_id                   NUMBER,
                                 pl23_profit_amt                 NUMBER,
                                 pl23_cum_profit_amt             NUMBER,
                                 pl23_lsf_cash_acc_balance       NUMBER DEFAULT 0,
                                 pl23_date                       DATE)
    IS
    BEGIN
        -- Delete existing entry for the same application, order, and date
        DELETE FROM l23_order_profit_log
         WHERE     l23_application_id = pl23_application_id
               AND l23_order_id = pl23_order_id
               AND TRUNC (l23_date) = TRUNC (pl23_date);

        INSERT INTO l23_order_profit_log (l23_application_id,
                                          l23_order_id,
                                          l23_date,
                                          l23_profit_amt,
                                          l23_cum_profit_amt,
                                          l23_lsf_cash_acc_balance)
        VALUES (pl23_application_id,
                pl23_order_id,
                pl23_date,
                pl23_profit_amt,
                pl23_cum_profit_amt,
                pl23_lsf_cash_acc_balance);

        pkey := 1;
    EXCEPTION
        WHEN OTHERS
        THEN
            pkey := -1;
    END;

    -- NEW: Check if profit entry exists for specific date
    PROCEDURE l23_check_entry_exists (pkey               OUT NUMBER,
                                      pl23_application_id       NUMBER,
                                      pl23_order_id             NUMBER,
                                      pl23_date                 DATE)
    IS
        v_count NUMBER := 0;
    BEGIN
        SELECT COUNT(*)
          INTO v_count
          FROM l23_order_profit_log
         WHERE     l23_application_id = pl23_application_id
               AND l23_order_id = pl23_order_id
               AND TRUNC (l23_date) = TRUNC (pl23_date);

        pkey := v_count;
    EXCEPTION
        WHEN OTHERS
        THEN
            pkey := 0;
    END;

    -- NEW: Get profit entry for specific date with order ID
    PROCEDURE l23_get_entry_by_date_order (pview                 OUT refcursor,
                                           pl23_application_id       NUMBER,
                                           pl23_order_id             NUMBER,
                                           pl23_date                 DATE)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l23_order_profit_log
             WHERE     l23_application_id = pl23_application_id
                   AND l23_order_id = pl23_order_id
                   AND TRUNC (l23_date) = TRUNC (pl23_date);
    END;

    -- NEW: Get last profit entry (most recent by date)
    PROCEDURE l23_get_last_entry (pview                 OUT refcursor,
                                  pl23_application_id       NUMBER,
                                  pl23_order_id             NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM (SELECT *
                      FROM l23_order_profit_log
                     WHERE     l23_application_id = pl23_application_id
                           AND l23_order_id = pl23_order_id
                    ORDER BY l23_date DESC)
             WHERE ROWNUM = 1;
    END;

    -- Existing procedures (unchanged)
    PROCEDURE l23_get_lstone (pview                 OUT refcursor,
                              pl23_application_id       NUMBER,
                              pl23_order_id             NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l23_order_profit_log
             WHERE     l23_application_id = pl23_application_id
                   AND l23_order_id = pl23_order_id
                   AND TRUNC (l23_date) = TRUNC (SYSDATE - 1);
    END;

    PROCEDURE l23_get_summation_application (
        pview                 OUT refcursor,
        pl23_application_id       NUMBER,
        pl23_order_id             NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM (SELECT *
                      FROM l23_order_profit_log
                     WHERE     l23_application_id = pl23_application_id
                           AND l23_order_id = pl23_order_id
                    ORDER BY TRUNC (l23_date) DESC)
             WHERE ROWNUM = 1;
    END;

    PROCEDURE l23_get_all_for_application (pview                 OUT refcursor,
                                           pl23_application_id       NUMBER,
                                           pl23_order_id             NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l23_order_profit_log a
             WHERE     a.l23_application_id = pl23_application_id
                   AND a.l23_order_id = pl23_order_id
            ORDER BY l23_date ASC;
    END;

    PROCEDURE l23_get_entry_for_date (pview                 OUT refcursor,
                                      pl23_application_id       NUMBER,
                                      pl23_date_str             VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l23_order_profit_log b
             WHERE     b.l23_application_id = pl23_application_id
                   AND TRUNC (l23_date) =
                           (SELECT TRUNC (
                                       TO_DATE (pl23_date_str, 'ddMMyyyy'))
                              FROM DUAL);
    END;

    PROCEDURE l23_correct_profit_entry (pkey              OUT NUMBER,
                                        pdate_str      IN     VARCHAR2,
                                        papp_id        IN     VARCHAR2,
                                        pcustomer_id          VARCHAR2)
    IS
        v_date                      DATE;
        v_lsf_cash_account_number   VARCHAR2 (100);
        v_s02_cash_balance          NUMBER := 0;
        v_order_id                  NUMBER;
        v_loan_amount               NUMBER;
        v_profit_percentage         NUMBER;
        v_begin_cum_profit          NUMBER;
        v_discount_on_profit        NUMBER;
        v_utilization               NUMBER;
        v_profit                    NUMBER;
        v_final_cum_profit          NUMBER;
        v_outstanding_balance       NUMBER;
        v_entry_count               NUMBER := 0;
    BEGIN
        SELECT TRUNC (TO_DATE (pdate_str, 'ddMMyyyy')) INTO v_date FROM DUAL;

        SELECT a.l07_cash_acc_id
          INTO v_lsf_cash_account_number
          FROM l07_cash_account a
         WHERE a.l07_l01_app_id = papp_id AND a.l07_is_lsf_type = 1;

        SELECT a.s02_balance
          INTO v_s02_cash_balance
          FROM mubasher_oms.s02_cash_account_summary a
         WHERE     a.s02_accountno = v_lsf_cash_account_number
               AND TRUNC (a.s02_trimdate) = TRUNC (v_date);

        SELECT a.l14_purchase_ord_id,
               a.l14_ord_completed_value,
               a.l14_profit_percentage
          INTO v_order_id, v_loan_amount, v_profit_percentage
          FROM l14_purchase_order a
         WHERE a.l14_customer_id = pcustomer_id AND a.l14_app_id = papp_id;

        SELECT b.l23_cum_profit_amt
          INTO v_begin_cum_profit
          FROM l23_order_profit_log b
         WHERE     b.l23_application_id = papp_id
               AND b.l23_date BETWEEN (v_date - 1) AND (v_date - 1) + 0.99999;

        SELECT a.l01_discount_on_profit
          INTO v_discount_on_profit
          FROM l01_application a
         WHERE a.l01_app_id = papp_id AND a.l01_customer_id = pcustomer_id;

        v_utilization := v_loan_amount;

        IF v_discount_on_profit = 1
        THEN
            v_utilization := v_loan_amount - v_s02_cash_balance;
        END IF;

        v_profit := 0;

        IF v_utilization > 0
        THEN
            v_profit := (v_utilization * v_profit_percentage / 100) / 360;
        END IF;

        v_final_cum_profit := v_begin_cum_profit + v_profit;

        SELECT COUNT (*)
          INTO v_entry_count
          FROM l23_order_profit_log a
         WHERE     a.l23_application_id = papp_id
               AND TRUNC (a.l23_date) = TRUNC (v_date);

        IF v_entry_count = 1
        THEN
            UPDATE l23_order_profit_log
               SET l23_cum_profit_amt = (v_final_cum_profit)
             WHERE     l23_application_id = papp_id
                   AND TRUNC (l23_date) = TRUNC (v_date);
        ELSE
            INSERT INTO l23_order_profit_log (l23_application_id,
                                              l23_order_id,
                                              l23_date,
                                              l23_profit_amt,
                                              l23_cum_profit_amt,
                                              l23_lsf_cash_acc_balance)
            VALUES (papp_id,
                    v_order_id,
                    v_date,
                    v_profit,
                    v_final_cum_profit,
                    v_s02_cash_balance);
        END IF;

        UPDATE l05_collaterals
           SET l05_outstanding_amt = v_loan_amount + v_final_cum_profit
         WHERE l05_l01_app_id = papp_id;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L23_ORDER_PROFIT_LOG_PKG

