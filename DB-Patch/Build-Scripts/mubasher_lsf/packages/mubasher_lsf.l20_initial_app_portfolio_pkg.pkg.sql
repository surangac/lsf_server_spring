-- Start of DDL Script for Package MUBASHER_LSF.L20_INITIAL_APP_PORTFOLIO_PKG
-- Generated 17-Nov-2025 10:52:52 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE mubasher_lsf.l20_initial_app_portfolio_pkg
/* Formatted on 8/6/2015 5:06:44 PM (QP5 v5.206) */
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

    PROCEDURE l20_add_initial_portfolio (
        pkey                      OUT NUMBER,
        pl20_app_id            IN     NUMBER,
        pl20_symbol_code       IN     VARCHAR2,
        pl20_exchange          IN     VARCHAR2,
        pl20_previous_closed   IN     NUMBER,
        pl20_available_qty     IN     NUMBER);

     PROCEDURE l20_get_initial_portfolio (
        pview                OUT refcursor,
        pl20_app_id            IN     NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l20_initial_app_portfolio_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l20_initial_app_portfolio_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l20_initial_app_portfolio_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l20_initial_app_portfolio_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l20_initial_app_portfolio_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE
PACKAGE BODY mubasher_lsf.l20_initial_app_portfolio_pkg
/* Formatted on 8/6/2015 5:12:34 PM (QP5 v5.206) */
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

    PROCEDURE l20_add_initial_portfolio (
        pkey                      OUT NUMBER,
/*ADVICE(16): Unreferenced parameter [552] */
        pl20_app_id            IN     NUMBER,
        pl20_symbol_code       IN     VARCHAR2,
        pl20_exchange          IN     VARCHAR2,
        pl20_previous_closed   IN     NUMBER,
        pl20_available_qty     IN     NUMBER)
    IS
    -- Enter the procedure variables here. As shown below
    --  variable_name        datatype  NOT NULL DEFAULT default_value;
    BEGIN
        INSERT INTO l20_initial_app_portfolio (l20_app_id,
                                               l20_symbol_code,
                                               l20_exchange,
                                               l20_previous_closed,
                                               l20_available_qty)
             VALUES (pl20_app_id,
                     pl20_symbol_code,
                     pl20_exchange,
                     pl20_previous_closed,
                     pl20_available_qty);
         pkey := 1;
    END;

    PROCEDURE l20_get_initial_portfolio (
        pview                OUT refcursor,
        pl20_app_id            IN     NUMBER)

IS
BEGIN
      OPEN pview FOR
            SELECT *
              FROM l20_initial_app_portfolio a
             WHERE a.l20_app_id = pl20_app_id;

END;
/*ADVICE(37): END of program unit, package or type is not labeled [408] */
-- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L20_INITIAL_APP_PORTFOLIO_PKG

