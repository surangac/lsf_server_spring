-- Start of DDL Script for Package MUBASHER_LSF.L01_APPLICATION_PKG
-- Generated 17-Nov-2025 10:52:49 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE                           mubasher_lsf.l01_application_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l01_add_update (
        pkey                               OUT NUMBER,
        pl01_app_id                     IN     NUMBER,
        pl01_customer_id                IN     VARCHAR2,
        pl01_full_name                  IN     VARCHAR2,
        pl01_occupation                 IN     VARCHAR2,
        pl01_employer                   IN     VARCHAR2,
        pl01_is_self_emp                IN     NUMBER DEFAULT 0,
        pl01_line_of_bisiness           IN     VARCHAR2,
        pl01_avg_monthly_income         IN     NUMBER,
        pl01_finance_req_amt            IN     NUMBER,
        pl01_address                    IN     VARCHAR2,
        pl01_mobile_no                  IN     VARCHAR2,
        pl01_telephone_no               IN     VARCHAR2,
        pl01_email                      IN     VARCHAR2,
        pl01_fax                        IN     VARCHAR2,
        pl01_dib_acc                    IN     VARCHAR2,
        pl01_trading_acc                IN     VARCHAR2,
        pl01_is_other_brk_available     IN     NUMBER DEFAULT 0,
        pl01_other_brk_names            IN     VARCHAR2,
        pl01_other_brk_avg_pf           IN     NUMBER,
        pl01_overall_status             IN     NUMBER,
        pl01_current_level              IN     NUMBER,
        pl01_type_of_facility           IN     VARCHAR2,
        pl01_facility_type              IN     VARCHAR2,
        pl01_proposal_limit             IN     NUMBER,
        pl01_revised_to                 IN     NUMBER DEFAULT NULL,
        pl01_revised_from               IN     NUMBER DEFAULT NULL,
        pl01_is_locked                  IN     NUMBER,
        pl01_l12_stock_conc_grp_id      IN     NUMBER,        -- default null,
        pl01_l11_marginability_grp_id   IN     NUMBER,        -- default null,
        pl01_l15_tenor_id               IN     NUMBER DEFAULT NULL,
        pl01_initial_rapv               IN     NUMBER,
        pl01_cash_acc                   IN     VARCHAR2,
        pl01_cash_balance               IN     NUMBER,
        pl01_trading_acc_exchange              VARCHAR2,
        pl01_review_date                       VARCHAR2 DEFAULT NULL,
        pl01_admin_fee_charged                 NUMBER DEFAULT 0,
        pl01_max_symbol_cnt             IN     NUMBER,
        pl01_customer_ref_no                   VARCHAR2 DEFAULT NULL,
        pl01_zip_code                          VARCHAR2 DEFAULT NULL,
        pl01_bank_brch_name                    VARCHAR2 DEFAULT NULL,
        pl01_city                              VARCHAR2 DEFAULT NULL,
        pl01_pobox                             VARCHAR2 DEFAULT NULL,
        pl01_prefered_language                 VARCHAR2 DEFAULT 'en',
        pl01_discount_on_profit                NUMBER DEFAULT 0,
        pl01_profit_percentage                 NUMBER DEFAULT 0,
        pl01_automatic_settlement              NUMBER DEFAULT 0,
        pl01_product_type                      NUMBER DEFAULT 0,
        pl01_rollover_app_id            IN     NUMBER,
    	pl01_finance_method				IN 	   NUMBER DEFAULT 1,
        pl01_device_type                             VARCHAR2 DEFAULT NULL,
        pl01_ip_address                             VARCHAR2 DEFAULT NULL);

    PROCEDURE l01_get_all (pview OUT refcursor);

    PROCEDURE l01_get_by_application_id (pview         OUT refcursor,
                                         pl01_app_id       NUMBER);

    PROCEDURE l01_get_by_customer_id (pview              OUT refcursor,
                                      pl01_customer_id       NUMBER);


    PROCEDURE l01_get_apps_by_customer_id (pview              OUT refcursor,
                                           pl01_customer_id       NUMBER);

    PROCEDURE l01_get_not_closed_apps (pview              OUT refcursor,
                                       pl01_customer_id       NUMBER);

    PROCEDURE l01_get_notgranted_app (pview              OUT refcursor,
                                      pl01_customer_id       NUMBER);

    PROCEDURE l01_get_filtered_application (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2,
        pl01_from_date         IN     VARCHAR2,
        pl01_to_date           IN     VARCHAR2,
        pl01_request_status    IN     NUMBER DEFAULT 0);

    PROCEDURE l01_get_histry_application (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2,
        pl01_from_date         IN     VARCHAR2,
        pl01_to_date           IN     VARCHAR2,
        pl01_request_status    IN     NUMBER DEFAULT 0);

    PROCEDURE l01_get_reversed_application (
        pview                    OUT refcursor,
        pl01_request_status   IN     NUMBER);

    PROCEDURE l01_get_snapshot (pview                    OUT refcursor,
                                pl01_request_status   IN     NUMBER);

    PROCEDURE l01_reverse_application (pkey                   OUT NUMBER,
                                       pl01_app_id         IN     NUMBER,
                                       pl01_revised_to     IN     NUMBER,
                                       pl01_revised_from   IN     NUMBER,
                                       pl01_is_editable    IN     NUMBER,
                                       pl01_is_reversed    IN     NUMBER,
                                       pl01_is_edited      IN     NUMBER);


    PROCEDURE l01_get_app_status_summary (pview OUT refcursor);

    -------------------Sending Applications for Admin to select the app for Custom Document Upload-------------

    PROCEDURE l01_get_app_admin_doc_upload (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2);

    --   PROCEDURE l01_get_matching_config (pview            OUT refcursor,
    --                  pl01_app_id   IN     NUMBER);

    PROCEDURE l01_get_level_status (pview                   OUT refcursor,
                                    pl01_current_level   IN     NUMBER);

    ----------------------Application Settlement Related ----------------
    PROCEDURE l01_get_odrcntct_singed_app (pview OUT refcursor);

    -----------------------Profit Calculation Related--------------------
    PROCEDURE l01_get_prof_cal_eli_apps (pview OUT refcursor);


    ----------------------Customer Search Releated---------------------
    PROCEDURE l01_get_limit_approve_customer (
        pview                 OUT refcursor,
        pl01_page_size     IN     NUMBER,
        pl01_page_number   IN     NUMBER);

    PROCEDURE l01_get_total_approved_size (pkey OUT NUMBER);

    PROCEDURE l01_black_listed_applications (pview OUT refcursor);

    PROCEDURE l01_whitelistapplication (pkey                  OUT NUMBER,
                                        pl01_app_id        IN     NUMBER,
                                        pl01_customer_id   IN     VARCHAR2);

    ---------------Get failed deposits for purchase orders---------------
    PROCEDURE l01_faileddeposits (pview OUT refcursor);

    PROCEDURE l01_update_customer_otp (pkey                      OUT NUMBER,
                                       pl01_app_id                   NUMBER,
                                       pl01_otp                      VARCHAR,
                                       pl01_otp_generated_time       NUMBER);

    PROCEDURE l01_get_ord_cntrct_data (pview         OUT refcursor,
                                       pl01_app_id       NUMBER);

    PROCEDURE l01_set_app_close_state (pl01_app_id               NUMBER,
                                       pl01_acc_closed_status    NUMBER);

    PROCEDURE l01_update_activity (pkey                      OUT NUMBER,
                                   pl01_app_id            IN     NUMBER,
                                   pl01_acc_activity_id          NUMBER);

    PROCEDURE l01_get_incomplete_customers (pview OUT refcursor);

    PROCEDURE l01_get_apps_for_admin_reject (pview OUT refcursor);

    PROCEDURE l01_update_last_profit_date (pkey             OUT NUMBER,
                                           pl01_app_id   IN     NUMBER);

    PROCEDURE l01_get_physical_deliver_list (pview OUT refcursor);
    
    PROCEDURE l01_get_snapshot_commodity (pview                    OUT refcursor,
                                pl01_request_status   IN     NUMBER);
    
    PROCEDURE l01_update_aditional_details (
        pkey                               OUT NUMBER,
        pl01_app_id                     IN     NUMBER,
        pl01_additional_details         IN     VARCHAR2,
    	pl01_additional_doc_name		IN     VARCHAR2,
    	pl01_additional_doc_path		IN     VARCHAR2);
    
    PROCEDURE l01_update_facility_trn_status(pkey                      OUT NUMBER,
                                       pl01_app_id                   NUMBER,
                                       pstatus                      VARCHAR);
    
    PROCEDURE l01_has_roll_over(pkey   OUT NUMBER,
                                       pl01_app_id                   NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l01_application_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l01_application_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l01_application_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l01_application_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l01_application_pkg TO mubasher_lsf_role
/

-- Start of DDL Script for Package Body MUBASHER_LSF.L01_APPLICATION_PKG
-- Generated 12/1/2025 3:25:04 PM from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.l01_application_pkg
IS
    PROCEDURE l01_add_update (
        pkey                               OUT NUMBER,
        pl01_app_id                     IN     NUMBER,
        pl01_customer_id                IN     VARCHAR2,
        pl01_full_name                  IN     VARCHAR2,
        pl01_occupation                 IN     VARCHAR2,
        pl01_employer                   IN     VARCHAR2,
        pl01_is_self_emp                IN     NUMBER DEFAULT 0,
        pl01_line_of_bisiness           IN     VARCHAR2,
        pl01_avg_monthly_income         IN     NUMBER,
        pl01_finance_req_amt            IN     NUMBER,
        pl01_address                    IN     VARCHAR2,
        pl01_mobile_no                  IN     VARCHAR2,
        pl01_telephone_no               IN     VARCHAR2,
        pl01_email                      IN     VARCHAR2,
        pl01_fax                        IN     VARCHAR2,
        pl01_dib_acc                    IN     VARCHAR2,
        pl01_trading_acc                IN     VARCHAR2,
        pl01_is_other_brk_available     IN     NUMBER DEFAULT 0,
        pl01_other_brk_names            IN     VARCHAR2,
        pl01_other_brk_avg_pf           IN     NUMBER,
        pl01_overall_status             IN     NUMBER,
        pl01_current_level              IN     NUMBER,
        pl01_type_of_facility           IN     VARCHAR2,
        pl01_facility_type              IN     VARCHAR2,
        pl01_proposal_limit             IN     NUMBER,
        pl01_revised_to                 IN     NUMBER DEFAULT NULL,
        pl01_revised_from               IN     NUMBER DEFAULT NULL,
        pl01_is_locked                  IN     NUMBER,
        pl01_l12_stock_conc_grp_id      IN     NUMBER,         --default null,
        pl01_l11_marginability_grp_id   IN     NUMBER,        -- default null,
        pl01_l15_tenor_id               IN     NUMBER DEFAULT NULL,
        pl01_initial_rapv               IN     NUMBER,
        pl01_cash_acc                   IN     VARCHAR2,
        pl01_cash_balance               IN     NUMBER,
        pl01_trading_acc_exchange              VARCHAR2,
        pl01_review_date                       VARCHAR2 DEFAULT NULL,
        pl01_admin_fee_charged                 NUMBER DEFAULT 0,
        pl01_max_symbol_cnt             IN     NUMBER,
        pl01_customer_ref_no                   VARCHAR2 DEFAULT NULL,
        pl01_zip_code                          VARCHAR2 DEFAULT NULL,
        pl01_bank_brch_name                    VARCHAR2 DEFAULT NULL,
        pl01_city                              VARCHAR2 DEFAULT NULL,
        pl01_pobox                             VARCHAR2 DEFAULT NULL,
        pl01_prefered_language                 VARCHAR2 DEFAULT 'en',
        pl01_discount_on_profit                NUMBER DEFAULT 0,
        pl01_profit_percentage                 NUMBER DEFAULT 0,
        pl01_automatic_settlement              NUMBER DEFAULT 0,
        pl01_product_type                      NUMBER DEFAULT 0,
        pl01_rollover_app_id            IN     NUMBER,
        pl01_finance_method                IN        NUMBER DEFAULT 1,
        pl01_device_type                       VARCHAR2 DEFAULT NULL,
        pl01_ip_address                        VARCHAR2 DEFAULT NULL)
    IS
        default_stock_conc_grp_id      NUMBER;
        default_marginability_grp_id   NUMBER;
        irollover_count                NUMBER DEFAULT 0;
    BEGIN
        IF (pl01_app_id = -1)
        THEN
            SELECT seq_l01_app_id.NEXTVAL INTO pkey FROM DUAL;

            IF (pl01_rollover_app_id > 0)
            THEN
                SELECT seq_l01_rollover_count.NEXTVAL
                  INTO irollover_count
                  FROM DUAL;
            END IF;

            BEGIN
                SELECT l11_marginability_grp_id
                  INTO default_marginability_grp_id
                  FROM l11_marginability_group
                 WHERE l11_is_default = '1';
            EXCEPTION
                WHEN OTHERS
                THEN
                    default_marginability_grp_id := NULL;
            END;

            BEGIN
                SELECT l12_stock_conc_grp_id
                  INTO default_stock_conc_grp_id
                  FROM l12_stock_concentration_group
                 WHERE l12_is_default = '1';
            EXCEPTION
                WHEN OTHERS
                THEN
                    default_stock_conc_grp_id := NULL;
            END;

            INSERT INTO l01_application (l01_app_id,
                                         l01_customer_id,
                                         l01_full_name,
                                         l01_occupation,
                                         l01_employer,
                                         l01_is_self_emp,
                                         l01_line_of_bisiness,
                                         l01_avg_monthly_income,
                                         l01_finance_req_amt,
                                         l01_address,
                                         l01_mobile_no,
                                         l01_telephone_no,
                                         l01_email,
                                         l01_fax,
                                         l01_dib_acc,
                                         l01_trading_acc,
                                         l01_is_other_brk_available,
                                         l01_other_brk_names,
                                         l01_other_brk_avg_pf,
                                         l01_overall_status,
                                         l01_current_level,
                                         l01_type_of_facility,
                                         l01_date,
                                         l01_facility_type,
                                         l01_proposal_date,
                                         l01_proposal_limit,
                                         -- l01_revised_to,
                                         -- l01_revised_from,
                                         l01_is_locked,
                                         l01_l15_tenor_id,
                                         l01_l12_stock_conc_grp_id,
                                         l01_l11_marginability_grp_id,
                                         l01_initial_rapv,
                                         l01_cash_acc,
                                         l01_cash_balance,
                                         l01_trading_acc_exchange,
                                         l01_review_date,
                                         l01_admin_fee_charged,
                                         l01_max_symbol_cnt,
                                         l01_customer_ref_no,
                                         l01_zip_code,
                                         l01_bank_brch_name,
                                         l01_city,
                                         l01_pobox,
                                         l01_prefered_language,
                                         l01_discount_on_profit,
                                         l01_profit_percentage,
                                         l01_automatic_settlement,
                                         l01_product_type,
                                         l01_rollover_app_id,
                                         l01_rollover_count,
                                         l01_finance_method,
                                         l01_device_type,
                                         l01_ip_address)
                 VALUES (pkey,
                         pl01_customer_id,
                         pl01_full_name,
                         pl01_occupation,
                         pl01_employer,
                         pl01_is_self_emp,
                         pl01_line_of_bisiness,
                         pl01_avg_monthly_income,
                         pl01_finance_req_amt,
                         pl01_address,
                         pl01_mobile_no,
                         pl01_telephone_no,
                         pl01_email,
                         pl01_fax,
                         pl01_dib_acc,
                         pl01_trading_acc,
                         pl01_is_other_brk_available,
                         pl01_other_brk_names,
                         pl01_other_brk_avg_pf,
                         pl01_overall_status,
                         pl01_current_level,
                         pl01_type_of_facility,
                         SYSDATE,
                         pl01_facility_type,
                         SYSDATE,
                         pl01_proposal_limit,
                         -- pl01_revised_to,
                         --  pl01_revised_from,
                         pl01_is_locked,
                         pl01_l15_tenor_id,
                         default_stock_conc_grp_id,
                         default_marginability_grp_id,
                         pl01_initial_rapv,
                         pl01_cash_acc,
                         pl01_cash_balance,
                         pl01_trading_acc_exchange,
                         pl01_review_date,
                         pl01_admin_fee_charged,
                         pl01_max_symbol_cnt,
                         pl01_customer_ref_no,
                         pl01_zip_code,
                         pl01_bank_brch_name,
                         pl01_city,
                         pl01_pobox,
                         pl01_prefered_language,
                         pl01_discount_on_profit,
                         pl01_profit_percentage,
                         pl01_automatic_settlement,
                         pl01_product_type,
                         pl01_rollover_app_id,
                         irollover_count,
                         pl01_finance_method,
                         pl01_device_type,
                         pl01_ip_address);
        ELSE
            pkey := pl01_app_id;

            UPDATE l01_application
               SET l01_customer_id = pl01_customer_id,
                   l01_full_name = pl01_full_name,
                   l01_occupation = pl01_occupation,
                   l01_employer = pl01_employer,
                   l01_is_self_emp = pl01_is_self_emp,
                   l01_line_of_bisiness = pl01_line_of_bisiness,
                   l01_avg_monthly_income = pl01_avg_monthly_income,
                   l01_finance_req_amt = pl01_finance_req_amt,
                   l01_address = pl01_address,
                   l01_mobile_no = pl01_mobile_no,
                   l01_telephone_no = pl01_telephone_no,
                   l01_email = pl01_email,
                   l01_fax = pl01_fax,
                   l01_dib_acc = pl01_dib_acc,
                   l01_trading_acc = pl01_trading_acc,
                   l01_is_other_brk_available = pl01_is_other_brk_available,
                   l01_other_brk_names = pl01_other_brk_names,
                   l01_other_brk_avg_pf = pl01_other_brk_avg_pf,
                   l01_overall_status = pl01_overall_status,
                   l01_current_level = pl01_current_level,
                   l01_type_of_facility = pl01_type_of_facility,
                   l01_facility_type = pl01_facility_type,
                   l01_proposal_date = SYSDATE,
                   l01_proposal_limit = pl01_proposal_limit,
                   -- l01_revised_to = pl01_revised_to,
                   -- l01_revised_from = pl01_revised_from,
                   l01_is_locked = pl01_is_locked,
                   l01_l15_tenor_id = pl01_l15_tenor_id,
                   l01_l12_stock_conc_grp_id = pl01_l12_stock_conc_grp_id,
                   l01_l11_marginability_grp_id =
                       pl01_l11_marginability_grp_id,
                   l01_initial_rapv = pl01_initial_rapv,
                   l01_cash_acc = pl01_cash_acc,
                   l01_cash_balance = pl01_cash_balance,
                   l01_trading_acc_exchange = pl01_trading_acc_exchange,
                   l01_review_date = pl01_review_date,
                   l01_admin_fee_charged = pl01_admin_fee_charged,
                   l01_max_symbol_cnt = pl01_max_symbol_cnt,
                   l01_customer_ref_no = pl01_customer_ref_no,
                   l01_zip_code = pl01_zip_code,
                   l01_bank_brch_name = pl01_bank_brch_name,
                   l01_city = pl01_city,
                   l01_pobox = pl01_pobox,
                   l01_prefered_language = pl01_prefered_language,
                   l01_discount_on_profit = pl01_discount_on_profit,
                   l01_profit_percentage = pl01_profit_percentage,
                   l01_automatic_settlement = pl01_automatic_settlement,
                   l01_product_type = pl01_product_type,
                   l01_rollover_app_id = pl01_rollover_app_id,
                   l01_finance_method = pl01_finance_method
             WHERE l01_app_id = pkey;
        END IF;
    END;

    PROCEDURE l01_get_all (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM l01_application
            ORDER BY l01_app_id;
    END;

    PROCEDURE l01_get_by_application_id (pview         OUT refcursor,
                                         pl01_app_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application l01,
                   m11_agreements m11,
                   l32_appove_agreements l32
             WHERE     l01.l01_app_id = pl01_app_id
                   AND (    (l01.l01_app_id = l32.l32_l01_app_id)
                        AND (m11.m11_id = l32.l32_m11_id)
                        AND m11_agreement_type =
                                (CASE
                                     WHEN l01.l01_rollover_app_id > 0
                                     THEN
                                         '2'
                                     ELSE
                                         '1'
                                 END));
    END;

    PROCEDURE l01_get_by_customer_id (pview              OUT refcursor,
                                      pl01_customer_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM (  SELECT *
                          FROM l01_application l01
                         WHERE     l01.l01_customer_id = pl01_customer_id
                               AND l01.l01_current_level != 18
                               AND l01.l01_overall_status >= 0
                      ORDER BY l01_app_id DESC) suppliers2
               WHERE ROWNUM <= 1
            ORDER BY ROWNUM;
    END;

    PROCEDURE l01_get_apps_by_customer_id (pview              OUT refcursor,
                                           pl01_customer_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM l01_application l01
               WHERE l01.l01_customer_id = pl01_customer_id
            ORDER BY l01_app_id DESC;
    END;

    PROCEDURE l01_get_not_closed_apps (pview              OUT refcursor,
                                       pl01_customer_id       NUMBER)
    IS
        closed_status   NUMBER;
    BEGIN
        SELECT a.m01_app_completed_state
          INTO closed_status
          FROM m01_sys_paras a;

        OPEN pview FOR
              SELECT *
                FROM l01_application b,
                     m11_agreements m11,
                     l32_appove_agreements l32
               WHERE     (b.l01_current_level != closed_status)
                     AND (   (b.l01_overall_status >= 0)
                          OR (b.l01_overall_status = -999))
                     AND b.l01_customer_id = pl01_customer_id
                     AND b.l01_acc_closed_status != 1
                     AND (    (b.l01_app_id = l32.l32_l01_app_id)
                          AND (m11.m11_id = l32.l32_m11_id))
            ORDER BY l01_app_id DESC;
    END;


    PROCEDURE l01_get_notgranted_app (pview              OUT refcursor,
                                      pl01_customer_id       NUMBER)
    IS
        reapply_status   NUMBER;
    BEGIN
        SELECT a.m01_app_reapply_state
          INTO reapply_status
          FROM m01_sys_paras a;

        OPEN pview FOR
            SELECT *
              FROM l01_application b
             WHERE     (b.l01_current_level < reapply_status)
                   AND (   (b.l01_overall_status >= 0)
                        OR (b.l01_overall_status = -999))
                        AND b.l01_acc_closed_status != 1
                   AND l01_customer_id = pl01_customer_id;
    END;

    PROCEDURE l01_get_filtered_application (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2,
        pl01_from_date         IN     VARCHAR2,
        pl01_to_date           IN     VARCHAR2,
        pl01_request_status    IN     NUMBER DEFAULT 0)
    IS
          v_sql VARCHAR2(4000); -- Increased size to accommodate larger SQL statements
          v_criteria VARCHAR2(200);
          v_value VARCHAR2(200);
          v_filter VARCHAR2(400);
          v_date_filter VARCHAR2(400);
      BEGIN
          -- Initialize variables properly (don't use DEFAULT in variable declaration for initialization)
          v_criteria := NULL;
          v_value := NULL;
          v_filter := NULL;
          v_date_filter := NULL;

          -- Set criteria based on filter selection
          IF pl01_filter_criteria = 1 THEN
              v_criteria := 'l01_full_name';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 2 THEN
              v_criteria := 'l01_app_id';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 3 THEN
              v_criteria := 'l01_mobile_no';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 4 THEN
              v_criteria := 'l01_finance_req_amt';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 5 THEN
              v_criteria := 'l01_customer_id';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 6 THEN
              v_criteria := 'l01_email';
              v_value := pl01_filter_value;
          ELSIF pl01_filter_criteria = 7 THEN
                  v_criteria := 'l01_revised_to';
                  v_value := pl01_filter_value;
          END IF;

          -- Build the filter condition based on criteria
          IF pl01_filter_criteria = 1 THEN
              -- For name, use case-insensitive LIKE
              v_filter := 'a.' || v_criteria || ' LIKE ''%' || UPPER(v_value) || '%''';
          ELSIF pl01_filter_criteria BETWEEN 2 AND 6 AND v_value IS NOT NULL THEN
              -- For other criteria, use equality (with proper table alias)
              v_filter := 'a.' || v_criteria || '=''' || v_value || '''';
          ELSE
              -- Default case if no valid criteria provided
              v_filter := '1=1'; -- Always true condition
          END IF;

          -- Add date range filter if provided
          IF pl01_from_date IS NOT NULL AND pl01_to_date IS NOT NULL THEN
              v_date_filter := ' AND a.L01_DATE BETWEEN TO_DATE(''' || pl01_from_date ||
                               ''', ''DD-MM-YYYY'') AND TO_DATE(''' || pl01_to_date || ''', ''DD-MM-YYYY'') + 0.99999';
          END IF;

         --  Add request status filter if provided (not 0)
        IF pl01_request_status != 0 THEN
            IF v_filter IS NULL THEN
                v_date_filter := ' AND (a.L01_CURRENT_LEVEL = ' || pl01_request_status || ' OR a.l01_revised_to=' || pl01_request_status || ')' ;
            ELSE
                v_filter := v_filter || ' AND (a.L01_CURRENT_LEVEL = ' || pl01_request_status || ' OR a.l01_revised_to=' || pl01_request_status || ')' ;
            END IF;
        END IF;

          -- Construct the main SQL query with proper aliases and WHERE conditions
          v_sql :=
              'SELECT a.*,
                      l06.l06_trading_acc_id,
                      m11.m11_finance_method
               FROM l01_application a
               LEFT JOIN l06_trading_acc l06 ON a.l01_app_id = l06.l06_l01_app_id
               JOIN l32_appove_agreements l32 ON a.l01_app_id = l32.l32_l01_app_id
               JOIN m11_agreements m11 ON l32.l32_m11_id = m11.m11_id
               WHERE (l06.l06_is_lsf_type = 1 OR l06.l06_is_lsf_type IS NULL)
               AND ' || v_filter || v_date_filter;

          -- Output the SQL for debugging
          DBMS_OUTPUT.put_line(v_sql);

          -- Execute the query
          OPEN pview FOR v_sql;
            EXCEPTION
          WHEN OTHERS THEN
              -- Add error handling
              DBMS_OUTPUT.put_line('Error: ' || SQLERRM);
   END;

    PROCEDURE l01_get_reversed_application (
        pview                    OUT refcursor,
        pl01_request_status   IN     NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application
             WHERE l01_revised_to = pl01_request_status AND l01_overall_status >= 0;
    END;

    PROCEDURE l01_get_snapshot (pview                    OUT refcursor,
                                pl01_request_status   IN     NUMBER)
    IS
    BEGIN
        IF pl01_request_status = 14
        THEN
            OPEN pview FOR
                SELECT a.*, l32.*
                  FROM l01_application a,
                       l32_appove_agreements l32,
                       m11_agreements m11
                 WHERE     a.l01_current_level >= 14
                        AND a.l01_current_level < 18
                       AND a.l01_overall_status >= 0
                       AND a.l01_app_id = l32.l32_l01_app_id
                       AND m11.m11_id = l32.l32_m11_id
                       AND m11.m11_finance_method = '2'
                       AND m11.m11_agreement_type =
                               (CASE
                                    WHEN a.l01_rollover_app_id > 0 THEN '2'
                                    ELSE '1'
                                END);
        ELSE
            OPEN pview FOR
                SELECT a.*, l06.l06_trading_acc_id
                  FROM l01_application a, l06_trading_acc l06
                 WHERE     a.l01_current_level = pl01_request_status
                       AND a.l01_app_id = l06.l06_l01_app_id(+)
                       AND (   l06.l06_is_lsf_type = 1
                            OR l06.l06_is_lsf_type IS NULL)
                       AND a.l01_overall_status >= 0;
        END IF;
    END;


    PROCEDURE l01_reverse_application (pkey                   OUT NUMBER,
                                       pl01_app_id         IN     NUMBER,
                                       pl01_revised_to     IN     NUMBER,
                                       pl01_revised_from   IN     NUMBER,
                                       pl01_is_editable    IN     NUMBER,
                                       pl01_is_reversed    IN     NUMBER,
                                       pl01_is_edited      IN     NUMBER)
    IS
    BEGIN
        pkey := pl01_app_id;

        UPDATE l01_application
           SET l01_revised_to = pl01_revised_to,
               l01_revised_from = pl01_revised_from,
               l01_is_editable = pl01_is_editable,
               l01_is_reversed = pl01_is_reversed,
               l01_is_edited = pl01_is_edited
         WHERE l01_app_id = pl01_app_id;
    END;

    PROCEDURE l01_get_histry_application (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2,
        pl01_from_date         IN     VARCHAR2,
        pl01_to_date           IN     VARCHAR2,
        pl01_request_status    IN     NUMBER DEFAULT 0)
    IS
        v_sql          VARCHAR2 (200);
        v_criteria     VARCHAR2 (200) DEFAULT 0;
        v_value        VARCHAR2 (200) DEFAULT 0;
        v_filter       VARCHAR2 (400);
        v_currentlvl   VARCHAR2 (20) DEFAULT 0;
    BEGIN
        IF (pl01_filter_criteria = 1)
        THEN
            v_criteria := 'l01_full_name';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 2)
        THEN
            v_criteria := 'l01_app_id';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 3)
        THEN
            v_criteria := 'l01_mobile_no';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 4)
        THEN
            v_criteria := 'l01_finance_req_amt';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 5)
        THEN
            v_criteria := 'l01_customer_id';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 6)
        THEN
            v_criteria := 'l01_email';
            v_value := pl01_filter_value;
        END IF;

        IF (pl01_filter_criteria = 1)
        THEN
            v_filter :=
                UPPER (v_criteria) || ' like ''%' || UPPER (v_value) || '%''';
        ELSE
            v_filter := v_criteria || '=''' || v_value || '''';
        END IF;

        IF (pl01_request_status != 0)
        THEN
            v_currentlvl := 'L01_CURRENT_LEVEL';
        END IF;

        v_filter :=
               v_filter
            || ' and '
            || v_currentlvl
            || '>'''
            || pl01_request_status;


        v_sql :=
               'select * from l01_application where '
            || v_filter
            || ''' AND to_date(l01_date) >='''
            || TO_CHAR (TO_DATE (pl01_from_date, 'yyyy/MM/dd hh24:MI:SS'))
            || ''''
            || ' AND to_date(l01_date) <='''
            || TO_CHAR (TO_DATE (pl01_to_date, 'yyyy/MM/dd hh24:MI:SS'))
            || '''order by l01_date';

        DBMS_OUTPUT.put_line (v_sql);

        OPEN pview FOR v_sql;
    END;

    PROCEDURE l01_get_app_status_summary (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT a.l01_app_id,
                     a.l01_customer_id,
                     a.l01_full_name,
                     b.m02_state_description,
                     a.l01_current_level,
                     a.l01_overall_status,
                     c.l14_ord_status,
                     c.l14_ord_completed_value,
                     c.l14_ord_settled_amount,
                     c.l14_customer_approve_state,
                     c.l14_settlement_status,
                     c.l14_liquidation_status,
                     a.l01_acc_activity_id,
                     l06.l06_trading_acc_id,
                     CASE
                   WHEN a.l01_rollover_count IS NULL OR a.l01_rollover_count = 0
                       THEN
                       TO_CHAR(a.l01_app_id)
                   ELSE
                       TO_CHAR(a.l01_app_id) || 'R' || TO_CHAR(a.l01_rollover_count)
                   END
                   AS l01_display_application_id
                FROM l01_application a,
                     m02_app_state_flow b,
                     l14_purchase_order c,
                     l06_trading_acc l06
               WHERE     a.l01_current_level = b.m02_state
                     AND a.l01_app_id = c.l14_app_id(+)
                     AND a.l01_app_id = l06.l06_l01_app_id(+)
                     AND b.m02_app_type = 0
                     AND (   l06.l06_is_lsf_type = 1
                          OR l06.l06_is_lsf_type IS NULL)
            -- AND a.l01_current_level >= 14
            ORDER BY l01_customer_id, l01_app_id;
    END;


    -------------------Sending Applications for Admin to select the app for Custom Document Upload-------------
    PROCEDURE l01_get_app_admin_doc_upload (
        pview                     OUT refcursor,
        pl01_filter_criteria   IN     NUMBER,
        pl01_filter_value      IN     VARCHAR2)
    IS
        v_sql        VARCHAR2 (200);
        v_criteria   VARCHAR2 (200) DEFAULT 0;
        v_value      VARCHAR2 (200) DEFAULT 0;
        v_filter     VARCHAR2 (400);
    BEGIN
        IF (pl01_filter_criteria = 1)
        THEN
            v_criteria := 'l01_full_name';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 2)
        THEN
            v_criteria := 'l01_app_id';
            v_value := pl01_filter_value;
        ELSIF (pl01_filter_criteria = 5)
        THEN
            v_criteria := 'l01_customer_id';
            v_value := pl01_filter_value;
        END IF;

        IF (pl01_filter_criteria = 1)
        THEN
            --    v_filter := v_criteria || ' like ''%' || v_value || '%''';
            v_filter :=
                UPPER (v_criteria) || ' like ''%' || UPPER (v_value) || '%''';
        ELSE
            v_filter := v_criteria || '=''' || v_value || '''';
        END IF;

        v_sql :=
               'select * from l01_application where '
            || v_filter
            || 'and l01_overall_status >= 0';

        --  DBMS_OUTPUT.put_line (v_sql);

        OPEN pview FOR v_sql;
    END;

    PROCEDURE l01_get_level_status (pview                   OUT refcursor,
                                    pl01_current_level   IN     NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application
             WHERE l01_current_level = pl01_current_level;
    END;

    ----------------------Application Settlement Related ----------------
    PROCEDURE l01_get_odrcntct_singed_app (pview OUT refcursor)
    IS
        v_accepted_level   NUMBER;
        v_client           VARCHAR2 (20);
    BEGIN
        /*
        SELECT m01_client_code
          INTO v_client
          FROM m01_sys_paras a;

        IF v_client = 'ABIC'
        THEN
            v_accepted_level := 16;
        END IF;

        IF v_client = 'DIB'
        THEN
            v_accepted_level := 15;
        END IF;
*/

        OPEN pview FOR
            SELECT *
              FROM l01_application a,
                   l14_purchase_order b
             WHERE     a.l01_current_level != 18
                   AND b.l14_app_id = a.l01_app_id
                   AND b.l14_customer_approve_state = 1
                   AND a.l01_acc_activity_id NOT IN
                           (1110, 1117, 1118, 1112, 1114, 1115, 1120)
                   AND b.l14_settlement_status = 0;
    -- a.l01_current_level = 16 AND a.l01_overall_status = 15;

    END;

    PROCEDURE l01_get_prof_cal_eli_apps (pview OUT refcursor)
    IS
        v_accepted_level   NUMBER;
        v_client           VARCHAR2 (20);
    BEGIN
        SELECT m01_client_code
          INTO v_client
          FROM m01_sys_paras a;

        IF v_client = 'ABIC'
        THEN
            v_accepted_level := 16;
        END IF;

        IF v_client = 'DIB'
        THEN
            v_accepted_level := 15;
        END IF;


        OPEN pview FOR
            SELECT *
              FROM l01_application a, l14_purchase_order b
             WHERE     a.l01_current_level = v_accepted_level
                   AND a.l01_overall_status = v_accepted_level - 1
                   AND b.l14_app_id = a.l01_app_id
                   AND b.l14_customer_approve_state = 1
                   AND a.l01_acc_activity_id NOT IN
                           (1110, 1117, 1118, 1112, 1114, 1115, 1120)
                   AND b.l14_settlement_status = 0
                   AND a.l01_product_type != 3;
    -- a.l01_current_level = 16 AND a.l01_overall_status = 15;

    END;

    PROCEDURE l01_get_limit_approve_customer (
        pview                 OUT refcursor,
        pl01_page_size     IN     NUMBER,
        pl01_page_number   IN     NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM (SELECT a.*, ROWNUM rn
                      FROM (  SELECT b.*, m02.m02_state_description
                                FROM l01_application b, m02_app_state_flow m02
                               WHERE     b.l01_overall_status >= 0
                                     AND b.l01_overall_status < 17
                                     AND b.l01_current_level = m02.m02_state
                                     AND m02_app_type = 0
                            ORDER BY b.l01_full_name, b.l01_app_id desc) a
                     WHERE ROWNUM <
                               ( (pl01_page_number * pl01_page_size) + 1))
             WHERE rn >= ( ( (pl01_page_number - 1) * pl01_page_size) + 1);
    END;

    PROCEDURE l01_get_total_approved_size (pkey OUT NUMBER)
    IS
    BEGIN
        SELECT COUNT (*)
          INTO pkey
          FROM l01_application a
         WHERE a.l01_overall_status >= 0 AND a.l01_overall_status < 17;
    END;

    PROCEDURE l01_black_listed_applications (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application a
             WHERE a.l01_overall_status = -999;
    END;

    PROCEDURE l01_whitelistapplication (pkey                  OUT NUMBER,
                                        pl01_app_id        IN     NUMBER,
                                        pl01_customer_id   IN     VARCHAR2)
    IS
        v_appclosestate   NUMBER;
    BEGIN
        SELECT a.m01_app_completed_state
          INTO v_appclosestate
          FROM m01_sys_paras a;

        UPDATE l01_application
           SET l01_current_level = v_appclosestate,
               l01_overall_status = (v_appclosestate - 1) * -1
         WHERE     l01_app_id = pl01_app_id
               AND l01_customer_id = pl01_customer_id;

        pkey := 1;
    END;

    --Get failed deposits for purchase orders
    PROCEDURE l01_faileddeposits (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application
             WHERE l01_current_level = 50 AND l01_overall_status < 0;
    END;

    PROCEDURE l01_update_customer_otp (pkey                      OUT NUMBER,
                                       pl01_app_id                   NUMBER,
                                       pl01_otp                      VARCHAR,
                                       pl01_otp_generated_time       NUMBER)
    IS
    BEGIN
        UPDATE l01_application
           SET l01_otp = pl01_otp,
               l01_otp_generated_time = pl01_otp_generated_time
         WHERE l01_app_id = pl01_app_id;

        pkey := 1;
    END;

    PROCEDURE l01_get_ord_cntrct_data (pview         OUT refcursor,
                                       pl01_app_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.l01_bank_brch_name,
                   c.l14_customer_approve_date,
                   m01_c1_city AS l01_city,
                   b.l02_sts_changed_user_name,
                   a.l01_customer_id,
                      m01_c1_arabic_first_name
                   || ' '
                   || m01_c1_arabic_second_name
                   || ' '
                   || m01_c1_arabic_name
                       AS l01_full_name,
                   a.l01_pobox,
                   NVL (a.l01_zip_code, '     ') AS l01_zip_code,
                   a.l01_telephone_no,
                   a.l01_email,
                   a.l01_mobile_no,
                   c.l14_accepted_client_ip,
                   m01_c1_nin,
                   c.l14_sima_charges,
                   c.l14_transfer_charges,
                   c.l14_vat_amount
              FROM l01_application a,
                   l02_app_state b,
                   l14_purchase_order c,
                   mubasher_oms.m01_customers_for_dt dt
             WHERE     a.l01_app_id = b.l02_l01_app_id
                   AND b.l02_level_id = 2
                   AND a.l01_customer_id = dt.m01_c1_customer_id
                   AND a.l01_app_id = pl01_app_id
                   AND a.l01_customer_id = c.l14_customer_id
                   AND a.l01_app_id = c.l14_app_id;
    END;

    PROCEDURE l01_set_app_close_state (pl01_app_id               NUMBER,
                                       pl01_acc_closed_status    NUMBER)
    IS
    BEGIN
        UPDATE l01_application
           SET l01_acc_closed_status = pl01_acc_closed_status
         WHERE l01_app_id = pl01_app_id;
    END;

    PROCEDURE l01_update_activity (pkey                      OUT NUMBER,
                                   pl01_app_id            IN     NUMBER,
                                   pl01_acc_activity_id          NUMBER)
    IS
    BEGIN
        UPDATE l01_application
           SET l01_acc_activity_id = pl01_acc_activity_id
         WHERE l01_app_id = pl01_app_id;

        pkey := 1;
    END;

    PROCEDURE l01_get_incomplete_customers (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.l01_app_id,
                   a.l01_full_name,
                   a.l01_customer_id,
                   c.l14_ord_settled_amount,
                   c.l14_settlement_status,
                   c.l14_purchase_ord_id,
                   a.l01_acc_closed_status,
                   a.l01_acc_activity_id,
                   CASE
                   WHEN l01_rollover_count IS NULL OR l01_rollover_count = 0
                       THEN
                       TO_CHAR(l01_app_id)
                   ELSE
                       TO_CHAR(l01_app_id) || 'R' || TO_CHAR(l01_rollover_count)
                   END
                   AS l01_display_application_id,
                   lsftypecash.l07_cash_acc_id AS lsftypecash,
                   nonlsftypecash.l07_cash_acc_id AS nonlsftypecash,
                   lsftypetrading.l06_trading_acc_id AS lsftypetrading,
                   NVL (nonlsftypetrading.l06_trading_acc_id,
                        l01_trading_acc)
                       AS nonlsftypetrading,
                   CASE a.l01_acc_activity_id
                       WHEN 1101
                       THEN
                           'Collateral Submission failed'
                       WHEN 1102
                       THEN
                           'purchase order creation failed'
                       WHEN 1105
                       THEN
                           'investment account creation failed'
                       WHEN 1106
                       THEN
                           'exchange account creation request failed in OMS'
                       WHEN 1108
                       THEN
                           'exchange account creation failed from exchange'
                       WHEN 1109
                       THEN
                           'Admin fee failed'
                       WHEN 1110
                       THEN
                           'collateral holdings transfer failed'
                       WHEN 1111
                       THEN
                           'account deletion failed due to cash transfer failed in OMS'
                       WHEN 1112
                       THEN
                           'account deletion failed due to holding transfer failed in OMS'
                       WHEN 1114
                       THEN
                           'account deletion failed due to holding transfer failed in Exchange'
                       WHEN 1115
                       THEN
                           'account deletion failed in Exchange'
                       WHEN 1117
                       THEN
                           'collateral holding transfer failed in Exchange'
                       WHEN 1118
                       THEN
                           'purchased holding transfer failed in exchange'
                       WHEN 1120
                       THEN
                           'purchased holding transfer failed in OMS'
                   END
                       AS current_status
              FROM l01_application a,
                   l14_purchase_order c,
                   l05_collaterals l05,
                   (SELECT *
                      FROM l07_cash_account
                     WHERE l07_is_lsf_type = 1) lsftypecash,
                   (SELECT *
                      FROM l07_cash_account
                     WHERE l07_is_lsf_type = 0) nonlsftypecash,
                   (SELECT *
                      FROM l06_trading_acc
                     WHERE l06_is_lsf_type = 1) lsftypetrading,
                   (SELECT *
                      FROM l06_trading_acc
                     WHERE l06_is_lsf_type = 0) nonlsftypetrading
             WHERE     a.l01_app_id = c.l14_app_id
                   AND a.l01_app_id = lsftypecash.l07_l01_app_id(+)
                   AND a.l01_app_id = nonlsftypecash.l07_l01_app_id(+)
                   AND a.l01_app_id = lsftypetrading.l06_l01_app_id(+)
                   AND a.l01_app_id = nonlsftypetrading.l06_l01_app_id(+)
                   AND a.l01_current_level != 18
                   AND a.l01_overall_status >= 0
                   AND a.l01_app_id = l05.L05_L01_APP_ID
                  -- AND l05.L05_IS_EXCHANGE_ACC_CREATED = 0;
                   AND a.l01_acc_activity_id IN
                           (1101,
                            1102,
                            1105,
                            1106,
                            1108,
                            1109,
                            1110,
                            1111,
                            1112,
                            1114,
                            1115,
                            1117,
                            1118,
                            1120);
    END;

    PROCEDURE l01_get_apps_for_admin_reject (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01.*, l14.l14_purchase_ord_id, m02.m02_state_description
              FROM (SELECT *
                      FROM l01_application
                     WHERE     l01_overall_status >= 0
                           AND l01_current_level < 15
                           AND l01_acc_closed_status = 0) l01,
                   (SELECT *
                      FROM l14_purchase_order
                     WHERE NVL (l14_ord_completed_value, 0) = 0) l14,
                   m02_app_state_flow m02
             WHERE     l01.l01_app_id = l14.l14_app_id(+)
                   AND l01.l01_current_level = m02.m02_state
                   AND m02.M02_APP_TYPE = 0;
    END;

    PROCEDURE l01_update_last_profit_date (pkey             OUT NUMBER,
                                           pl01_app_id   IN     NUMBER)
    IS
    BEGIN
        UPDATE l01_application
           SET l01_last_profit_date = SYSDATE
         WHERE l01_app_id = pl01_app_id;

        pkey := 1;
    END;

    PROCEDURE l01_get_physical_deliver_list (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01.*, l14.*
              FROM l01_application l01, l14_purchase_order l14
             WHERE     l01.l01_app_id = l14.l14_app_id
                   AND l14.l14_physical_delivery = '1'
                   AND l01.l01_current_level != '18';
    END;

    PROCEDURE l01_get_snapshot_commodity (pview                    OUT refcursor,
                                pl01_request_status   IN     NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.*, l32.*,lca.L07_INVESTOR_ACCOUNT
                      FROM l01_application a,
                           l32_appove_agreements l32,
                           m11_agreements m11,
                           (SELECT * FROM L07_CASH_ACCOUNT WHERE L07_IS_LSF_TYPE =1) lca
                     WHERE     a.l01_current_level = pl01_request_status
                           AND a.l01_overall_status >= 0
                           AND a.l01_app_id = l32.l32_l01_app_id
                           AND m11.m11_id = l32.l32_m11_id
                           AND m11.m11_finance_method = '2'
                           AND a.L01_APP_ID = lca.L07_L01_APP_ID(+)
                           AND m11.m11_agreement_type =
                                   (CASE
                                        WHEN a.l01_rollover_app_id > 0 THEN '2'
                                        ELSE '1'
                                    END);

    END;

    PROCEDURE l01_update_aditional_details (
        pkey                               OUT NUMBER,
        pl01_app_id                     IN     NUMBER,
        pl01_additional_details         IN     VARCHAR2,
        pl01_additional_doc_name        IN     VARCHAR2,
        pl01_additional_doc_path        IN     VARCHAR2)
    IS
    BEGIN

        UPDATE l01_application
        SET L01_ADITIONAL_DETAILS=pl01_additional_details,
        L01_ADDITIONAL_DOC_NAME=pl01_additional_doc_name,
        L01_ADDITIONAL_DOC_PATH=pl01_additional_doc_path
        WHERE l01_app_id=pl01_app_id;

        pkey :=1;

    END;

    PROCEDURE l01_update_facility_trn_status(pkey                      OUT NUMBER,
                                       pl01_app_id                   NUMBER,
                                       pstatus                      VARCHAR)
    IS
    BEGIN
        UPDATE l01_application
           SET L01_FACILITY_TRANSFER_STATUS = pstatus
         WHERE l01_app_id = pl01_app_id;

        pkey := 1;
    END;

    PROCEDURE l01_has_roll_over(pkey   OUT NUMBER,
                                       pl01_app_id                   NUMBER)
    IS
        v_rollover_count number := 0 ;
    BEGIN

        SELECT count(*) INTO v_rollover_count FROM  MUBASHER_LSF.L01_APPLICATION WHERE L01_ROLLOVER_APP_ID=pl01_app_id AND L01_OVERALL_STATUS >0;

        pkey := v_rollover_count;
    END;
END;
/


-- End of DDL Script for Package Body MUBASHER_LSF.L01_APPLICATION_PKG

