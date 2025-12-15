-- Start of DDL Script for Package MUBASHER_LSF.L07_CASH_ACCOUNT_PKG
-- Generated 17-Nov-2025 10:52:51 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE              mubasher_lsf.l07_cash_account_pkg
/* Formatted on 11/2/2015 2:47:40 PM (QP5 v5.206) */
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

    PROCEDURE l07_add_edit (
        pkey                     OUT NUMBER,
        pl07_cash_acc_id             VARCHAR,
        pl07_currency_code           VARCHAR DEFAULT NULL,
        pl07_cash_balance            NUMBER DEFAULT 0,
        pl07_amt_as_collat           NUMBER DEFAULT 0,
        pl07_amt_transferred         NUMBER DEFAULT 0,
        pl07_is_lsf_type             INTEGER DEFAULT 0,
        pl07_l05_collateral_id       NUMBER,
        pl07_l01_app_id              NUMBER,
        pl07_block_reference         VARCHAR DEFAULT NULL,
        pl07_status                  NUMBER DEFAULT 0);

    PROCEDURE l07_get_account_in_application (
        pview                    OUT refcursor,
        pl07_l05_collateral_id       NUMBER,
        pl07_l01_app_id              NUMBER,
        pl07_is_lsf_type             INTEGER DEFAULT 0);

    PROCEDURE l07_update_revaluation_info (pkey                OUT NUMBER,
                                           pl07_cash_acc_id        VARCHAR2,
                                           pl07_cash_balance       NUMBER,
                                           pl07_pending_settle     NUMBER,
                                           pl07_net_receivable     NUMBER);

    procedure l07_get_app_by_cash_acc(pview                    OUT refcursor,
                                        pl07_cash_acc_id varchar2,
                                        pl07_is_lsf_type number default 1);

   procedure l07_update_investment_acc(pl07_cash_acc_id varchar2,pl07_investor_account varchar2,pl07_l01_app_id    NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l07_cash_account_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l07_cash_account_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l07_cash_account_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l07_cash_account_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l07_cash_account_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY              mubasher_lsf.l07_cash_account_pkg
/* Formatted on 11/2/2015 2:46:11 PM (QP5 v5.206) */
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

    PROCEDURE l07_add_edit (
        pkey                     OUT NUMBER,
        pl07_cash_acc_id             VARCHAR,
        pl07_currency_code           VARCHAR DEFAULT NULL,
        pl07_cash_balance            NUMBER DEFAULT 0,
        pl07_amt_as_collat           NUMBER DEFAULT 0,
        pl07_amt_transferred         NUMBER DEFAULT 0,
        pl07_is_lsf_type             INTEGER DEFAULT 0,
        pl07_l05_collateral_id       NUMBER,
        pl07_l01_app_id              NUMBER,
        pl07_block_reference         VARCHAR DEFAULT NULL,
        pl07_status                  NUMBER DEFAULT 0)
    IS
        v_rec_count   NUMBER := 0;
    BEGIN
        SELECT COUNT (*)
          INTO v_rec_count
          FROM l07_cash_account
         WHERE     l07_l01_app_id = pl07_l01_app_id
               AND l07_l05_collateral_id = pl07_l05_collateral_id
               AND l07_cash_acc_id = pl07_cash_acc_id;

        IF (v_rec_count > 0)
        THEN
            UPDATE l07_cash_account
               SET l07_currency_code = pl07_currency_code,
                   l07_cash_balance = pl07_cash_balance,
                   l07_amt_as_collat = pl07_amt_as_collat,
                   l07_amt_transferred = pl07_amt_transferred,
                   l07_is_lsf_type = pl07_is_lsf_type,
                   l07_block_reference = pl07_block_reference,
                   l07_status = pl07_status,
                   l07_last_modified_time = SYSDATE
             WHERE     l07_l01_app_id = pl07_l01_app_id
                   AND l07_l05_collateral_id = pl07_l05_collateral_id
                   AND l07_cash_acc_id = pl07_cash_acc_id;
        ELSE
            INSERT INTO l07_cash_account (l07_cash_acc_id,
                                          l07_currency_code,
                                          l07_cash_balance,
                                          l07_amt_as_collat,
                                          l07_amt_transferred,
                                          l07_is_lsf_type,
                                          l07_l05_collateral_id,
                                          l07_l01_app_id,
                                          l07_block_reference,
                                          l07_status,
                                          l07_last_modified_time)
                 VALUES (pl07_cash_acc_id,
                         pl07_currency_code,
                         pl07_cash_balance,
                         pl07_amt_as_collat,
                         pl07_amt_transferred,
                         pl07_is_lsf_type,
                         pl07_l05_collateral_id,
                         pl07_l01_app_id,
                         pl07_block_reference,
                         pl07_status,
                         SYSDATE);
        END IF;

        pkey := 1;
    END;

    PROCEDURE l07_get_account_in_application (
        pview                    OUT refcursor,
        pl07_l05_collateral_id       NUMBER,
        pl07_l01_app_id              NUMBER,
        pl07_is_lsf_type             INTEGER DEFAULT 0)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l07_cash_account
             WHERE     l07_l05_collateral_id = pl07_l05_collateral_id
                   AND l07_l01_app_id = pl07_l01_app_id
                   AND l07_is_lsf_type = pl07_is_lsf_type;
    END;

    PROCEDURE l07_update_revaluation_info (pkey                OUT NUMBER,
                                           pl07_cash_acc_id        VARCHAR2,
                                           pl07_cash_balance       NUMBER,
                                           pl07_pending_settle     NUMBER,
                                           pl07_net_receivable     NUMBER)
    IS
    BEGIN
        UPDATE l07_cash_account
           SET l07_cash_balance = pl07_cash_balance,l07_pending_settle=pl07_pending_settle,l07_net_receivable=pl07_net_receivable
         WHERE l07_cash_acc_id = pl07_cash_acc_id AND l07_is_lsf_type = 1;
    END;

    procedure l07_get_app_by_cash_acc(pview                    OUT refcursor,
                                        pl07_cash_acc_id varchar2,
                                        pl07_is_lsf_type number default 1)
    is
    begin
        OPEN pview FOR
            select l01.* from l01_application l01,l07_cash_account l07
        where l01.l01_app_id=l07.l07_l01_app_id
        and l01_current_level<18 and l01_overall_status>0
        and l07.l07_cash_acc_id=pl07_cash_acc_id
        and l07.l07_is_lsf_type=pl07_is_lsf_type;

    end;

    procedure l07_update_investment_acc(pl07_cash_acc_id varchar2,pl07_investor_account varchar2,pl07_l01_app_id    NUMBER)
    is
    begin
        update l07_cash_account
        set l07_investor_account=pl07_investor_account
        where l07_cash_acc_id=pl07_cash_acc_id and l07_l01_app_id=pl07_l01_app_id;
    end;

END;
/


-- End of DDL Script for Package MUBASHER_LSF.L07_CASH_ACCOUNT_PKG

