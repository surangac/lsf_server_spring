-- Start of DDL Script for Package MUBASHER_LSF.L14_PURCHASE_ORDER_PKG
-- Generated 17-Nov-2025 10:52:52 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l14_purchase_order_pkg
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

    PROCEDURE l14_add (pkey                          OUT NUMBER,
                       pl14_purchase_ord_id              NUMBER,
                       pl14_app_id                       NUMBER,
                       pl14_customer_id                  VARCHAR2,
                       pl14_ord_value                    NUMBER,
                       pl14_ord_settlement_amount        NUMBER,
                       pl14_settlement_date              VARCHAR2,
                       pl14_trading_account              VARCHAR2,
                       pl14_exchange                     VARCHAR2,
                       pl14_settlement_account           VARCHAR2,
                       pl14_is_one_time_settlement       NUMBER,
                       pl14_installment_frequency        NUMBER,
                       pl14_set_duration_months          NUMBER,
                       pl14_l15_tenor_id                 NUMBER,
                       pl14_profit_amount                NUMBER,
                       pl14_sibour_amount                NUMBER,
                       pl14_libour_amount                NUMBER,
                       pl14_profit_percentage            NUMBER DEFAULT 0,
                       pl14_accepted_client_ip             VARCHAR2,
                       pl14_customer_name                 VARCHAR2);

    PROCEDURE l14_get_all (pview OUT refcursor, pl14_app_id NUMBER);

    PROCEDURE l14_get_order (pview                  OUT refcursor,
                             pl14_purchase_ord_id       NUMBER);

    PROCEDURE l14_approve_reject (pkey                    OUT NUMBER,
                                  pl14_app_id                 NUMBER,
                                  pl14_purchase_ord_id        NUMBER,
                                  --pL14_APPROVED_BY_ID      number,
                                  pl14_approved_by_name       VARCHAR,
                                  pl14_approval_status        NUMBER,
                                  pl14_accepted_client_ip     VARCHAR2);

    PROCEDURE l14_update_order_status (pkey                       OUT NUMBER,
                                       pl14_purchase_ord_id           NUMBER,
                                       pl14_ordstatus                 NUMBER,
                                       pl14_ordercompletedvalue       NUMBER,
                                       pl14_last_called_time          NUMBER,
                                       pl14_profit_amount             NUMBER,
                                       pl14_profit_percentage         NUMBER,
                                       pl14_vat_amount                NUMBER,
                       pl14_accepted_client_ip        VARCHAR2);

    PROCEDURE l14_update_cust_ord_status (
        pkey                          OUT NUMBER,
        pl14_purchase_ord_id              NUMBER,
        pl14_customer_approve_state       NUMBER,
        pl14_customer_comment             VARCHAR2,
        pl14_accepted_client_ip           VARCHAR2,
        pl14_approved_by_name             VARCHAR2,
        pl14_approved_by_id               NUMBER);

    PROCEDURE l14_update_po_reminder (
        pkey                          OUT NUMBER,
        pl14_purchase_ord_id              NUMBER,
        pl14_no_of_calling_attempts       NUMBER,
        pl14_customer_approve_state       NUMBER,
        pl14_last_called_time             VARCHAR2);

    PROCEDURE l14_get_application_reminder (pview OUT refcursor);

    PROCEDURE l14_update_to_settle_state (pl14_purchase_ord_id NUMBER);

    PROCEDURE l14_get_ord_aprved_app (pview           OUT refcursor,
                                      pl14_fromdate       DATE,
                                      pl14_todate         DATE);

    PROCEDURE l14_update_to_liquidate_state (pl14_purchase_ord_id NUMBER);

    PROCEDURE l14_get_apps_for_manual_stlmnt (pview OUT refcursor);

    PROCEDURE l14_get_available_poid (pkey OUT NUMBER);

    PROCEDURE l14_update_basket_status (
        pkey                        OUT NUMBER,
        pl14_purchase_ord_id            NUMBER,
        pl14_bskt_transfer_status       NUMBER);

    PROCEDURE l14_get_total_outstanding (pkey OUT NUMBER);

    PROCEDURE l14_update_admin_fee (pkey                    OUT NUMBER,
                                    pl14_purchase_ord_id        NUMBER,
                                    pl14_sima_charges           NUMBER,
                                    pl14_transfer_charges       NUMBER,
                                    pl14_vat_amount             NUMBER);

    PROCEDURE l14_get_all_for_commodity (pview         OUT refcursor,
                                         pl14_app_id       NUMBER);

    PROCEDURE l14_update_by_admin (pkey                          OUT NUMBER,
                                   pl14_purchase_ord_id       IN     NUMBER,
                                   pl14_physical_delivery     IN     NUMBER,
                                   pl14_sell_but_not_settle   IN     NUMBER,
                                   pl14_com_certificate_path   IN     VARCHAR2,
                                   pl14_certificate_number         IN VARCHAR2);

    PROCEDURE l14_update_comdt_po_exec (
        pkey                           OUT NUMBER,
        pl14_purchase_ord_id        IN     NUMBER,
        pl14_ord_status             IN     NUMBER,
        pl14_com_certificate_path   IN     VARCHAR2);

    PROCEDURE l14_update_auth_abic_to_sell (
        pkey                        OUT NUMBER,
        pl14_purchase_ord_id     IN     NUMBER,
        pl14_auth_abic_to_sell   IN     NUMBER,
        pl14_physical_delivery   IN     NUMBER);

    PROCEDURE l14_get_po_set_abic_to_sell (
        pview                           OUT refcursor,
        pm01_grace_per_commodity_sell       NUMBER);

    PROCEDURE l14_approve_reject_com_po (pkey                    OUT NUMBER,
                                         pl14_app_id                 NUMBER,
                                         pl14_purchase_ord_id        NUMBER,
                                       --  pL14_APPROVED_BY_ID      number,
                                         pl14_approved_by_name       VARCHAR,
                                         pl14_approval_status        NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l14_purchase_order_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l14_purchase_order_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l14_purchase_order_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l14_purchase_order_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l14_purchase_order_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l14_purchase_order_pkg
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

    PROCEDURE l14_add (pkey                          OUT NUMBER,
                       pl14_purchase_ord_id              NUMBER,
                       pl14_app_id                       NUMBER,
                       pl14_customer_id                  VARCHAR2,
                       pl14_ord_value                    NUMBER,
                       pl14_ord_settlement_amount        NUMBER,
                       pl14_settlement_date              VARCHAR2,
                       pl14_trading_account              VARCHAR2,
                       pl14_exchange                     VARCHAR2,
                       pl14_settlement_account           VARCHAR2,
                       pl14_is_one_time_settlement       NUMBER,
                       pl14_installment_frequency        NUMBER,
                       pl14_set_duration_months          NUMBER,
                       pl14_l15_tenor_id                 NUMBER,
                       pl14_profit_amount                NUMBER,
                       pl14_sibour_amount                NUMBER,
                       pl14_libour_amount                NUMBER,
                       pl14_profit_percentage            NUMBER DEFAULT 0,
                       pl14_accepted_client_ip             VARCHAR2,
                       pl14_customer_name                 VARCHAR2)
    IS
        --v_key   NUMBER;
        pkey1   VARCHAR2 (100) := '';
    BEGIN
        --SELECT seq_l14_po_id.NEXTVAL INTO v_key FROM DUAL;

        INSERT INTO l14_purchase_order (l14_purchase_ord_id,
                                        l14_customer_id,
                                        l14_ord_value,
                                        l14_ord_settlement_amount,
                                        l14_settlement_date,
                                        l14_trading_account,
                                        l14_exchange,
                                        l14_settlement_account,
                                        l14_is_one_time_settlement,
                                        l14_installment_frequency,
                                        l14_set_duration_months,
                                        l14_approval_status,
                                        l14_created_date,
                                        l14_l15_tenor_id,
                                        l14_app_id,
                                        l14_profit_amount,
                                        l14_sibour_amount,
                                        l14_libour_amount,
                                        l14_profit_percentage)
             VALUES (pl14_purchase_ord_id,                            --v_key,
                     pl14_customer_id,
                     pl14_ord_value,
                     pl14_ord_settlement_amount,
                     pl14_settlement_date,
                     pl14_trading_account,
                     pl14_exchange,
                     pl14_settlement_account,
                     pl14_is_one_time_settlement,
                     pl14_installment_frequency,
                     pl14_set_duration_months,
                     0,
                     SYSDATE,
                     pl14_l15_tenor_id,
                     pl14_app_id,
                     pl14_profit_amount,
                     pl14_sibour_amount,
                     pl14_libour_amount,
                     pl14_profit_percentage);

        m02_app_state_flow_pkg.m02_approve_application (
            pkey                         => pkey1,
            approve_state                => 1,
            pl01_app_id                  => pl14_app_id,
            pl02_message                 => 'Purchase Order Submited',
            pl02_sts_changed_user_id     => pl14_customer_id,
            pl02_sts_changed_user_name   => pl14_customer_name,
            pl02_status_changed_ip      => pl14_accepted_client_ip);

        pkey := pl14_purchase_ord_id;                                -- v_key;
    END;

    PROCEDURE l14_get_all (pview OUT refcursor, pl14_app_id NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l14_purchase_order
             WHERE l14_app_id = pl14_app_id;
    END;

    PROCEDURE l14_get_order (pview                  OUT refcursor,
                             pl14_purchase_ord_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l14_purchase_order
             WHERE l14_purchase_ord_id = pl14_purchase_ord_id;
    END;


    PROCEDURE l14_approve_reject (pkey                    OUT NUMBER,
                                  pl14_app_id                 NUMBER,
                                  pl14_purchase_ord_id        NUMBER,
                                  --pL14_APPROVED_BY_ID      number,
                                  pl14_approved_by_name       VARCHAR,
                                  pl14_approval_status        NUMBER,
                                  pl14_accepted_client_ip     VARCHAR2)
    IS
        pkey1   VARCHAR2 (100) := '';
    BEGIN
        UPDATE l14_purchase_order
           SET l14_approved_by_id = 0,
               l14_approved_by_name = pl14_approved_by_name,
               l14_approved_date = SYSDATE,
               l14_approval_status = pl14_approval_status
         WHERE     l14_app_id = pl14_app_id
               AND l14_purchase_ord_id = pl14_purchase_ord_id;

        IF (pl14_approval_status = 1)
        THEN
            -- move the application to next Step
            m02_app_state_flow_pkg.m02_approve_application (
                pkey                         => pkey1,
                approve_state                => pl14_approval_status,
                pl01_app_id                  => pl14_app_id,
                pl02_message                 => 'Purchase Order Approved',
                pl02_sts_changed_user_id     => pl14_approved_by_name,
                pl02_sts_changed_user_name   => pl14_approved_by_name,
                pl02_status_changed_ip       => '127.0.0.1');
        END IF;

        pkey := 1;
    END;

    PROCEDURE l14_update_order_status (pkey                       OUT NUMBER,
                                       pl14_purchase_ord_id           NUMBER,
                                       pl14_ordstatus                 NUMBER,
                                       pl14_ordercompletedvalue       NUMBER,
                                       pl14_last_called_time          NUMBER,
                                       pl14_profit_amount             NUMBER,
                                       pl14_profit_percentage         NUMBER,
                                       pl14_vat_amount                NUMBER,
                       pl14_accepted_client_ip        VARCHAR2)
    IS
        pl14_app_id        NUMBER;
        pkey1              VARCHAR2 (100) := '';
        ppoapprovallevel   NUMBER;
        pcleintcode        VARCHAR2 (20);
        pappcurentlevel    NUMBER;
    BEGIN
        SELECT l14_app_id
          INTO pl14_app_id
          FROM l14_purchase_order
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        UPDATE l14_purchase_order
           SET l14_ord_status = pl14_ordstatus,
               l14_ord_completed_value = pl14_ordercompletedvalue,
               l14_customer_approve_state = 2,
               l14_no_of_calling_attempts = 1,
               l14_last_called_time = pl14_last_called_time,
               l14_profit_amount = pl14_profit_amount,
               l14_profit_percentage = pl14_profit_percentage,
               l14_vat_amount = pl14_vat_amount
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        -- calling settlment proc to chage the settlement according to completed value
        l22_installments_pkg.l22_update_settlement_amt (
            pkey                         => pkey,
            pl14_po_id                   => pl14_purchase_ord_id,
            pl14_order_completed_value   => pl14_ordercompletedvalue);

        SELECT m01_client_code INTO pcleintcode FROM m01_sys_paras;
        
        update l01_application
        SET L01_OVERALL_STATUS=13,l01_current_level=14
         WHERE     l01_app_id = pl14_app_id;

--        SELECT l01_current_level
--          INTO pappcurentlevel
--          FROM l01_application l01, l14_purchase_order l14
--         WHERE     l01.l01_app_id = l14.l14_app_id
--               AND l14.l14_purchase_ord_id = pl14_purchase_ord_id;

        IF pcleintcode = 'ABIC'
        THEN
            ppoapprovallevel := 15;
            pappcurentlevel := 14;
        END IF;

        IF pcleintcode = 'DIB'
        THEN
            ppoapprovallevel := 18;

            IF pappcurentlevel = 50
            THEN
                pappcurentlevel := 17;
            END IF;
        END IF;

        -- below validation put due to handle multiple Queue messages from OMS
        IF pappcurentlevel < ppoapprovallevel
        THEN
            -- move the application to next Step
            m02_app_state_flow_pkg.m02_approve_application (
                pkey                         => pkey1,
                approve_state                => 1,
                pl01_app_id                  => pl14_app_id,
                pl02_message                 => 'Purchase Order Status Received from OMS',
                pl02_sts_changed_user_id     => 'System',
                pl02_sts_changed_user_name   => 'System',
                pl02_status_changed_ip         => pl14_accepted_client_ip);
        END IF;

        pkey := 1;
    END;

    PROCEDURE l14_update_cust_ord_status (
        pkey                          OUT NUMBER,
        pl14_purchase_ord_id              NUMBER,
        pl14_customer_approve_state       NUMBER,
        pl14_customer_comment             VARCHAR2,
        pl14_accepted_client_ip           VARCHAR2,
        pl14_approved_by_name             VARCHAR2,
        pl14_approved_by_id               NUMBER)
    IS
        pl14_app_id                NUMBER;
        pkey1                      VARCHAR2 (100) := '';
        v_ismultipleorderallowed   NUMBER := 0;
        v_minimumodervalue         NUMBER := 0;
        pcleintcode                VARCHAR2 (20);
        v_order_startstate         NUMBER := 15;
    BEGIN
        SELECT l14_app_id
          INTO pl14_app_id
          FROM l14_purchase_order
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        UPDATE l14_purchase_order
           SET l14_customer_approve_state = pl14_customer_approve_state,
               l14_customer_approve_date = SYSDATE,
               l14_customer_comment = pl14_customer_comment,
               l14_accepted_client_ip = pl14_accepted_client_ip,
               l14_accepted_date = SYSDATE
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;


        SELECT a.m01_is_multiple_order_allowed,
               a.m01_minimum_order_value,
               a.m01_order_start_state,
               m01_client_code
          INTO v_ismultipleorderallowed,
               v_minimumodervalue,
               v_order_startstate,
               pcleintcode
          FROM m01_sys_paras a;


        IF pcleintcode = 'ABIC'
        THEN
            v_order_startstate := 13;
        END IF;

        IF pcleintcode = 'DIB'
        THEN
            v_order_startstate := 15;
        END IF;

        -- move the application to next Step
        IF (v_ismultipleorderallowed = 0)
        THEN
            m02_app_state_flow_pkg.m02_approve_application (
                pkey                         => pkey1,
                approve_state                => pl14_customer_approve_state,
                pl01_app_id                  => pl14_app_id,
                pl02_message                 => 'Final Acceptance from Customer',
                pl02_sts_changed_user_id     => pl14_approved_by_id,
                pl02_sts_changed_user_name   => pl14_approved_by_name,
                pl02_status_changed_ip       => pl14_accepted_client_ip);
        ELSE
            IF (pl14_customer_approve_state < 0)
            THEN
                -- if multiple order support and reject the final acceptance then application is set to -998 since
                -- there can be pending settlement available
                m02_app_state_flow_pkg.m02_approve_application (
                    pkey                         => pkey1,
                    approve_state                => -998,
                    pl01_app_id                  => pl14_app_id,
                    pl02_message                 => 'Final Acceptance from Customer',
                    pl02_sts_changed_user_id     => pl14_approved_by_id,
                    pl02_sts_changed_user_name   => pl14_approved_by_name, 
                    pl02_status_changed_ip       => pl14_accepted_client_ip);
            ELSE
                -- set the appliction to perchace order status
                m02_app_state_flow_pkg.l01_update_application_level (
                    pl01_current_level    => v_order_startstate,
                    pl01_overall_status   => v_order_startstate - 1,
                    pl01_app_id           => pl14_app_id);
            END IF;
        END IF;

        pkey := 1;
    END;

    PROCEDURE l14_update_po_reminder (
        pkey                          OUT NUMBER,
        pl14_purchase_ord_id              NUMBER,
        pl14_no_of_calling_attempts       NUMBER,
        pl14_customer_approve_state       NUMBER,
        pl14_last_called_time             VARCHAR2)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_customer_approve_state = pl14_customer_approve_state,
               l14_last_called_time = pl14_last_called_time,
               l14_no_of_calling_attempts = pl14_no_of_calling_attempts
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;

    PROCEDURE l14_get_application_reminder (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.*
              FROM l14_purchase_order a, l01_application l01
             WHERE     a.l14_app_id = l01.l01_app_id
                   AND l01.l01_acc_activity_id NOT IN
                           (1110, 1117, 1118, 1112, 1114, 1115, 1120)
                   AND a.l14_customer_approve_state != 1
                   AND a.l14_customer_approve_state != -1
                   AND l01.l01_overall_status > 0
                   AND l01.l01_current_level < 18
                   AND a.l14_ord_status = 2;
    END;

    PROCEDURE l14_update_to_settle_state (pl14_purchase_ord_id NUMBER)
    IS
        v_completed_value   NUMBER;
    BEGIN
        BEGIN
            SELECT l05_outstanding_amt
              INTO v_completed_value
              FROM l05_collaterals l05, l14_purchase_order l14
             WHERE     l05.l05_l01_app_id = l14.l14_app_id
                   AND l14_purchase_ord_id = pl14_purchase_ord_id;
        EXCEPTION
            WHEN OTHERS
            THEN
                v_completed_value := 0;
        END;

        UPDATE l14_purchase_order
           SET l14_settlement_status = 1,
               l14_settled_date = SYSDATE,
               l14_ord_settled_amount = v_completed_value
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;
    END;

    PROCEDURE l14_get_ord_aprved_app (pview           OUT refcursor,
                                      pl14_fromdate       DATE,
                                      pl14_todate         DATE)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l01.*
              FROM l01_application l01,
                   (SELECT DISTINCT (l14_app_id) AS l14_app_id
                      FROM l14_purchase_order
                     WHERE     l14_approval_status = 1
                           AND TRUNC (l14_approved_date) BETWEEN TRUNC (
                                                                     pl14_fromdate)
                                                             AND (  TRUNC (
                                                                        pl14_todate)
                                                                  + .99999)) l14
             WHERE l01.l01_app_id = l14.l14_app_id;
    END;

    -- Enter further code below as specified in the Package spec.

    PROCEDURE l14_update_to_liquidate_state (pl14_purchase_ord_id NUMBER)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_liquidation_status = 1, l14_liquidation_date = SYSDATE
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;
    END;

    PROCEDURE l14_get_apps_for_manual_stlmnt (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application a, l14_purchase_order b
             WHERE     a.l01_current_level = 16
                   AND a.l01_overall_status = 15
                   AND b.l14_app_id = a.l01_app_id
                   AND b.l14_customer_approve_state = 1
                   AND ( (SELECT TO_DATE (l14_settlement_date,
                                          'ddMMyyyy hh24:MI:SS')
                            FROM l14_purchase_order
                           WHERE     l01_current_level = 16
                                 AND l01_overall_status = 15
                                 AND a.l01_acc_closed_status != 1
                                 AND l14_app_id = l01_app_id
                                 AND l14_customer_approve_state = 1) <
                            TO_DATE (SYSDATE))
            UNION ALL
            SELECT *
              FROM l01_application a, l14_purchase_order b
             WHERE     a.l01_current_level = 16
                   AND a.l01_overall_status = 15
                   AND b.l14_app_id = a.l01_app_id
                   AND a.l01_acc_closed_status != 1
                   AND b.l14_customer_approve_state = 1
                   AND a.l01_app_id IN (SELECT l05_l01_app_id
                                          FROM l05_collaterals
                                         WHERE l05_liquidation_call = 1);
    END;

    PROCEDURE l14_get_available_poid (pkey OUT NUMBER)
    IS
        v_key   NUMBER;
    BEGIN
        SELECT seq_l14_po_id.NEXTVAL INTO v_key FROM DUAL;

        pkey := v_key;
    END;

    PROCEDURE l14_update_basket_status (
        pkey                        OUT NUMBER,
        pl14_purchase_ord_id            NUMBER,
        pl14_bskt_transfer_status       NUMBER)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_bskt_transfer_status = pl14_bskt_transfer_status
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;
    END;

    PROCEDURE l14_get_total_outstanding (pkey OUT NUMBER)
    IS
        v_tot_outs_value   NUMBER := 0;
    BEGIN
        SELECT NVL (
                   SUM (
                       DECODE (NVL (l14_ord_completed_value, 0),
                               0, NVL (l01_proposal_limit, 0),
                               NVL (l14_ord_completed_value, 0))),
                   0)
                   AS totaloutstandingamt
          INTO v_tot_outs_value
          FROM (SELECT *
                  FROM l01_application
                 WHERE l01_overall_status >= 0 AND l01_current_level < 18) l01,
               (SELECT *
                  FROM l14_purchase_order
                 WHERE l14_settlement_status = 0) l14
         WHERE l01.l01_app_id = l14.l14_app_id(+);

        pkey := v_tot_outs_value;
    END;

    PROCEDURE l14_update_admin_fee (pkey                    OUT NUMBER,
                                    pl14_purchase_ord_id        NUMBER,
                                    pl14_sima_charges           NUMBER,
                                    pl14_transfer_charges       NUMBER,
                                    pl14_vat_amount             NUMBER)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_sima_charges = pl14_sima_charges,
               l14_transfer_charges = pl14_transfer_charges,
               l14_vat_amount_admin_fee = pl14_vat_amount
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;

    PROCEDURE l14_get_all_for_commodity (pview         OUT refcursor,
                                         pl14_app_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l14_purchase_order l14,
                   l34_purchase_order_commodities l34,
                   l07_cash_account l07
             WHERE     l14.l14_app_id = pl14_app_id
                   AND l14_purchase_ord_id = l34_l16_purchase_ord_id
                   AND l07.l07_l01_app_id = pl14_app_id;
    END;

    PROCEDURE l14_update_by_admin (pkey                          OUT NUMBER,
                                   pl14_purchase_ord_id       IN     NUMBER,
                                   pl14_physical_delivery     IN     NUMBER,
                                   pl14_sell_but_not_settle   IN     NUMBER,
                                   pl14_com_certificate_path   IN     VARCHAR2,
                                   pl14_certificate_number         IN VARCHAR2)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_physical_delivery = pl14_physical_delivery,
               l14_sell_but_not_settle = pl14_sell_but_not_settle,
              -- L14_AUTH_ABIC_TO_SELL = decode(pl14_physical_delivery,1,0,1),
               l14_com_certificate_path = pl14_com_certificate_path,
               L14_CERTIFICATE_NUMBER = pl14_certificate_number
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;

    PROCEDURE l14_update_comdt_po_exec (
        pkey                           OUT NUMBER,
        pl14_purchase_ord_id        IN     NUMBER,
        pl14_ord_status             IN     NUMBER,
        pl14_com_certificate_path   IN     VARCHAR2)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_com_certificate_path = pl14_com_certificate_path,
               l14_ord_status = pl14_ord_status
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;

    PROCEDURE l14_update_auth_abic_to_sell (
        pkey                        OUT NUMBER,
        pl14_purchase_ord_id     IN     NUMBER,
        pl14_auth_abic_to_sell   IN     NUMBER,
        pl14_physical_delivery   IN     NUMBER)
    IS
    BEGIN
        UPDATE l14_purchase_order
           SET l14_auth_abic_to_sell = pl14_auth_abic_to_sell,
               l14_physical_delivery = pl14_physical_delivery,
               l14_customer_approve_state = 1
         WHERE l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;

    PROCEDURE l14_get_po_set_abic_to_sell (
        pview                           OUT refcursor,
        pm01_grace_per_commodity_sell       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l01_application l01,
                   l14_purchase_order l14,
                   l32_appove_agreements l32,
                   m11_agreements m11
             WHERE     (l14.l14_auth_abic_to_sell IS NULL OR l14.l14_auth_abic_to_sell =0)
                   AND l14.l14_app_id = l32.l32_l01_app_id
                   AND m11.m11_id = l32.l32_m11_id
                   AND m11.m11_finance_method = '2'
                   AND l01.l01_app_id = l14.l14_app_id
                   AND l14.l14_customer_approve_state = 1
                   AND m11.m11_agreement_type =
                           (CASE
                                WHEN l01.l01_rollover_app_id > 0 THEN '2'
                                ELSE '1'
                            END)
                   AND (SYSDATE - l14.l14_approved_date) * 24 * 60 >
                           pm01_grace_per_commodity_sell;
    END;

    PROCEDURE l14_approve_reject_com_po (pkey                    OUT NUMBER,
                                         pl14_app_id                 NUMBER,
                                         pl14_purchase_ord_id        NUMBER,
                                      --   pL14_APPROVED_BY_ID      number,
                                         pl14_approved_by_name       VARCHAR,
                                         pl14_approval_status        NUMBER)
    IS
        pkey1   VARCHAR2 (100) := '';
    BEGIN
        UPDATE l14_purchase_order
           SET l14_approved_by_id = 0,
               l14_approved_by_name = pl14_approved_by_name,
               l14_approved_date = SYSDATE,
               l14_approval_status = pl14_approval_status
         WHERE     l14_app_id = pl14_app_id
               AND l14_purchase_ord_id = pl14_purchase_ord_id;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L14_PURCHASE_ORDER_PKG

