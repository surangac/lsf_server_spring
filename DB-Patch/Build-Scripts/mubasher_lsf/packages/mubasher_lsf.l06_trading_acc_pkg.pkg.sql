CREATE OR REPLACE
PACKAGE l06_trading_acc_pkg
/* Formatted on 10/28/2015 12:53:24 PM (QP5 v5.206) */
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
/*ADVICE(15): This item should be defined in a deeper scope [558] */

    PROCEDURE l06_add_edit (pkey                     OUT NUMBER,
                            pl06_trading_acc_id          VARCHAR2,
/*ADVICE(19): Mode of parameter is not specified with IN parameter [521] */
                            pl06_exchange                VARCHAR2,
/*ADVICE(21): Mode of parameter is not specified with IN parameter [521] */
                            pl06_is_lsf_type             NUMBER,
/*ADVICE(23): Mode of parameter is not specified with IN parameter [521] */
                            pl06_l05_collateral_id       NUMBER,
/*ADVICE(25): Mode of parameter is not specified with IN parameter [521] */
                            pl06_l01_app_id              NUMBER);
/*ADVICE(27): Mode of parameter is not specified with IN parameter [521] */

    PROCEDURE l06_get_account_in_application (
        pview                    OUT refcursor,
        pl06_l05_collateral_id       NUMBER,
/*ADVICE(32): Mode of parameter is not specified with IN parameter [521] */
        pl06_l01_app_id              NUMBER,
/*ADVICE(34): Mode of parameter is not specified with IN parameter [521] */
        pl06_is_lsf_type             NUMBER DEFAULT 0);
/*ADVICE(36): Mode of parameter is not specified with IN parameter [521] */

    PROCEDURE l06_update_revaluation (
        pkey                           OUT NUMBER,
        pl06_trading_acc_id                VARCHAR2,
/*ADVICE(41): Mode of parameter is not specified with IN parameter [521] */
        pl06_total_market_pf_value         NUMBER,
/*ADVICE(43): Mode of parameter is not specified with IN parameter [521] */
        pl06_total_w_market_pf_value       NUMBER);

    procedure l06_upadte_exchange_acc(
    pl06_from_trading_acc varchar2,
    pl06_to_trading_acc varchar2,
    pl06_l01_app_id              NUMBER,
    pl06_is_lsf_type             NUMBER DEFAULT 0);

    procedure l06_get_app_by_trading_acc(pview                    OUT refcursor,
                                        pl06_trading_acc varchar2,
                                        pl06_is_lsf_type number default 1);
/*ADVICE(45): Mode of parameter is not specified with IN parameter [521] */
END;                                                           -- Package spec
/*ADVICE(47): END of program unit, package or type is not labeled [408] */
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l06_trading_acc_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l06_trading_acc_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l06_trading_acc_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l06_trading_acc_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l06_trading_acc_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY l06_trading_acc_pkg
/* Formatted on 10/28/2015 12:55:37 PM (QP5 v5.206) */
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

    PROCEDURE l06_add_edit (pkey                     OUT NUMBER,
                            pl06_trading_acc_id          VARCHAR2,
/*ADVICE(16): Mode of parameter is not specified with IN parameter [521] */
                            pl06_exchange                VARCHAR2,
/*ADVICE(18): Mode of parameter is not specified with IN parameter [521] */
                            pl06_is_lsf_type             NUMBER,
/*ADVICE(20): Mode of parameter is not specified with IN parameter [521] */
                            pl06_l05_collateral_id       NUMBER,
/*ADVICE(22): Mode of parameter is not specified with IN parameter [521] */
                            pl06_l01_app_id              NUMBER)
/*ADVICE(24): Mode of parameter is not specified with IN parameter [521] */
    IS
        v_rec_count   NUMBER := 0;
/*ADVICE(27): NUMBER has no precision [315] */
    BEGIN
        SELECT COUNT (*)
/*ADVICE(30): SELECT COUNT used to obtain number of rows for specified
                  WHERE clause [309] */
          INTO v_rec_count
          FROM l06_trading_acc
         WHERE     l06_l05_collateral_id = pl06_l05_collateral_id
               AND l06_trading_acc_id = pl06_trading_acc_id
               AND l06_l01_app_id = pl06_l01_app_id;

        IF (v_rec_count > 0)
        THEN
            UPDATE l06_trading_acc
               SET l06_exchange = pl06_exchange,
                   l06_is_lsf_type = pl06_is_lsf_type
             WHERE     l06_l05_collateral_id = pl06_l05_collateral_id
                   AND l06_trading_acc_id = pl06_trading_acc_id
                   AND l06_l01_app_id = pl06_l01_app_id;
        ELSE
            INSERT INTO l06_trading_acc (l06_trading_acc_id,
                                         l06_exchange,
                                         l06_is_lsf_type,
                                         l06_l05_collateral_id,
                                         l06_l01_app_id)
                 VALUES (pl06_trading_acc_id,
                         pl06_exchange,
                         pl06_is_lsf_type,
                         pl06_l05_collateral_id,
                         pl06_l01_app_id);
        END IF;

        pkey := 1;
    END;
/*ADVICE(61): END of program unit, package or type is not labeled [408] */

    PROCEDURE l06_get_account_in_application (
        pview                    OUT refcursor,
/*ADVICE(65): This item has not been declared, or it refers to a label [131] */
        pl06_l05_collateral_id       NUMBER,
/*ADVICE(67): Mode of parameter is not specified with IN parameter [521] */
        pl06_l01_app_id              NUMBER,
/*ADVICE(69): Mode of parameter is not specified with IN parameter [521] */
        pl06_is_lsf_type             NUMBER DEFAULT 0)
/*ADVICE(71): Mode of parameter is not specified with IN parameter [521] */
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l06_trading_acc
             WHERE     l06_l05_collateral_id = pl06_l05_collateral_id
                   AND l06_l01_app_id = pl06_l01_app_id
                   AND l06_is_lsf_type = pl06_is_lsf_type;
    END;
/*ADVICE(81): END of program unit, package or type is not labeled [408] */

    PROCEDURE l06_update_revaluation (
        pkey                           OUT NUMBER,
        pl06_trading_acc_id                VARCHAR2,
/*ADVICE(86): Mode of parameter is not specified with IN parameter [521] */
        pl06_total_market_pf_value         NUMBER,
/*ADVICE(88): Mode of parameter is not specified with IN parameter [521] */
        pl06_total_w_market_pf_value       NUMBER)
/*ADVICE(90): Mode of parameter is not specified with IN parameter [521] */
    IS
    BEGIN
        UPDATE l06_trading_acc a
           SET a.l06_last_modified_time = SYSDATE,
               a.l06_total_market_pf_value = pl06_total_market_pf_value,
               a.l06_total_w_market_pf_value = pl06_total_w_market_pf_value
         WHERE a.l06_trading_acc_id = pl06_trading_acc_id;

        pkey := 1;
    END;

    procedure l06_upadte_exchange_acc(
    pl06_from_trading_acc varchar2,
    pl06_to_trading_acc varchar2,
    pl06_l01_app_id              NUMBER,
    pl06_is_lsf_type             NUMBER DEFAULT 0)
    is
    begin
    -- update the collateral level wheter trading account is created or not
        update l05_collaterals
        set l05_is_exchange_acc_created=1
        where l05_l01_app_id=pl06_l01_app_id;
--  (select l06_l01_app_id from l06_trading_acc where l06_trading_acc_id=pl06_from_trading_acc);

        update l06_trading_acc
        set l06_trading_acc_id=pl06_to_trading_acc
        where l06_l01_app_id=pl06_l01_app_id and l06_is_lsf_type=pl06_is_lsf_type;

    end;

    procedure l06_get_app_by_trading_acc(pview                    OUT refcursor,
                                        pl06_trading_acc varchar2,
                                        pl06_is_lsf_type number default 1)
    is
    begin
    OPEN pview FOR
            select l01.* from l01_application l01,l06_trading_acc l06
        where l01.l01_app_id=l06.l06_l01_app_id
        and l01_current_level<18 and l01_overall_status>0
        and l06.l06_trading_acc_id=pl06_trading_acc
        and l06.l06_is_lsf_type=pl06_is_lsf_type;
    end;

/*ADVICE(101): END of program unit, package or type is not labeled [408] */
END;
/*ADVICE(103): END of program unit, package or type is not labeled [408] */
/
