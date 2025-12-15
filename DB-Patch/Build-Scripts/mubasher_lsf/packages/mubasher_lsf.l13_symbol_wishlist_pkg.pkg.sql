-- Start of DDL Script for Package MUBASHER_LSF.L13_SYMBOL_WISHLIST_PKG
-- Generated 17-Nov-2025 10:52:51 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l13_symbol_wishlist_pkg
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

   PROCEDURE l13_get_wishlist_symbols
    ( pview OUT refcursor,
    pl13_l01_app_id     varchar2,
    pl13_l08_exchange   varchar2 default null);

    procedure l13_add_to_wish_list(pkey   OUT NUMBER,
   pl13_l01_app_id      number,
   pl13_l08_symbol_code varchar2,
   pl13_l08_exchange varchar2);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l13_symbol_wishlist_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l13_symbol_wishlist_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l13_symbol_wishlist_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l13_symbol_wishlist_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l13_symbol_wishlist_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l13_symbol_wishlist_pkg
/* Formatted on 3/17/2016 11:14:13 AM (QP5 v5.206) */
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

    PROCEDURE l13_get_wishlist_symbols (
        pview               OUT refcursor,
        pl13_l01_app_id         VARCHAR2,
        pl13_l08_exchange       VARCHAR2 DEFAULT NULL)
    IS
        v_company_code   VARCHAR2 (20) := 'ABIC';
    BEGIN
	    OPEN pview FOR
	    SELECT a.l08_symbol_code,
                       a.l08_exchange,
                       a.l08_short_desc,
                       a.l08_previous_closed,
                       a.l08_available_qty,
                       a.l08_market_value,
                       a.l08_l10_liquid_id,
                       l10.l10_liquid_name,
                       a.l08_allowed_for_collateral,
                       a.l08_ltp,
                       a.l08_short_desc_ar,
                       a.l08_allowed_for_po
                  FROM l08_symbol a, l10_liquidity_type l10
                 WHERE     a.l08_l10_liquid_id = l10.l10_liquid_id(+)
                       AND a.l08_exchange = pl13_l08_exchange;
     --                  AND a.L08_ALLOWED_FOR_PO = 1;
	    /*
        SELECT m01_client_code INTO v_company_code FROM m01_sys_paras;

        IF (v_company_code = 'ABIC')
        THEN
            OPEN pview FOR
                SELECT a.l08_symbol_code,
                       a.l08_exchange,
                       a.l08_short_desc,
                       a.l08_previous_closed,
                       a.l08_available_qty,
                       a.l08_market_value,
                       a.l08_l10_liquid_id,
                       l10.l10_liquid_name,
                       a.l08_allowed_for_collateral,
                       a.l08_ltp,
                       a.l08_short_desc_ar
                  FROM l08_symbol a, l10_liquidity_type l10
                 WHERE     a.l08_l10_liquid_id = l10.l10_liquid_id(+)
                       AND l08_exchange = pl13_l08_exchange
                       AND a.l08_l10_liquid_id IN
                               (SELECT l10_liquid_id
                                  FROM l18_stock_liquidity_type l18,
                                       l10_liquidity_type l10
                                 WHERE     l18.l18_liquid_id =
                                               l10.l10_liquid_id
                                       AND l18.l18_stock_conc_grp_id =
                                               (SELECT NVL (
                                                           l01_l12_stock_conc_grp_id,
                                                           -1)
                                                  FROM l01_application l
                                                 WHERE l.l01_app_id =
                                                           pl13_l01_app_id)
                                       AND l18_stock_concentrate_perce > 0);
        ELSE
            OPEN pview FOR
                SELECT a.l08_symbol_code,
                       a.l08_exchange,
                       a.l08_short_desc,
                       a.l08_previous_closed,
                       a.l08_available_qty,
                       a.l08_market_value,
                       a.l08_l10_liquid_id,
                       l10.l10_liquid_name,
                       a.l08_allowed_for_collateral,
                       a.l08_ltp,
                       a.l08_short_desc_ar
                  FROM l08_symbol a,
                       l13_symbol_wishlist l13,
                       l10_liquidity_type l10
                 WHERE     l13.l13_l08_symbol_code = a.l08_symbol_code
                       AND l13.l13_l08_exchange = a.l08_exchange
                       AND a.l08_l10_liquid_id = l10.l10_liquid_id(+)
                       AND l13.l13_l01_app_id = pl13_l01_app_id;
        END IF;
        */
    END;

    PROCEDURE l13_add_to_wish_list (pkey                   OUT NUMBER,
                                    pl13_l01_app_id            NUMBER,
                                    pl13_l08_symbol_code       VARCHAR2,
                                    pl13_l08_exchange          VARCHAR2)
    IS
        v_rec_count   NUMBER := 0;
    BEGIN
        SELECT COUNT (*)
          INTO v_rec_count
          FROM l13_symbol_wishlist
         WHERE     l13_l01_app_id = pl13_l01_app_id
               AND l13_l08_symbol_code = pl13_l08_symbol_code
               AND l13_l08_exchange = pl13_l08_exchange;

        IF (v_rec_count = 0)
        THEN
            INSERT
              INTO l13_symbol_wishlist (l13_l01_app_id,
                                        l13_l08_symbol_code,
                                        l13_l08_exchange)
            VALUES (pl13_l01_app_id, pl13_l08_symbol_code, pl13_l08_exchange);
        END IF;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L13_SYMBOL_WISHLIST_PKG

