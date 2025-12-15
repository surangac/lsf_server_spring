-- Start of DDL Script for Package MUBASHER_LSF.L08_SYMBOL_PKG
-- Generated 17-Nov-2025 10:52:51 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l08_symbol_pkg
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

    PROCEDURE l01_get_symbol_liqid_type (pview                 OUT refcursor,
                                         pl08_symbol_code   IN     VARCHAR2,
                                         pl08_exchange      IN     VARCHAR2);

    PROCEDURE l08_add_update (
        pkey                      OUT NUMBER,
        p08_symbol_code        IN     VARCHAR2,
        p08_exchange           IN     VARCHAR2,
        p08_short_desc         IN     VARCHAR2,
        p08_previous_closed    IN     VARCHAR2,
        p08_available_qty      IN     VARCHAR2,
        p08_market_value       IN     VARCHAR2,
        pl08_ltp                      VARCHAR2,
        p08_short_desc_ar      IN     VARCHAR2 DEFAULT NULL,
        pl08_instrument_type   IN     VARCHAR2,
        pl08_security_type     IN     VARCHAR2);

    PROCEDURE l08_update_liquid_type (
        pkey                             OUT NUMBER,
        p08_symbol_code               IN     VARCHAR2,
        p08_exchange                  IN     VARCHAR2,
        --p08_l10_liquid_id             IN     VARCHAR2,
        pl08_allowed_for_collateral          NUMBER DEFAULT 1,
        --pl08_concentration_type       IN     NUMBER,
        pl08_allowed_for_po			IN NUMBER DEFAULT 0
        );

    PROCEDURE l08_get_all_symbols (pview OUT refcursor);

    PROCEDURE l08_get_all_symbols_classf (pview OUT refcursor);

    PROCEDURE l08_get_symbol_dis (pview OUT refcursor, psymbolcode VARCHAR2);

    PROCEDURE l08_get_symbol_margin_perc (pview                OUT refcursor,
                                          p08_symbol_code   IN     VARCHAR2,
                                          p08_exchange      IN     VARCHAR2,
                                          pl08_app_id       IN     VARCHAR2);

    PROCEDURE l08_get_all_instrument_types (pview OUT refcursor);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l08_symbol_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l08_symbol_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l08_symbol_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l08_symbol_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l08_symbol_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l08_symbol_pkg
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

    PROCEDURE l01_get_symbol_liqid_type (pview                 OUT refcursor,
                                         pl08_symbol_code   IN     VARCHAR2,
                                         pl08_exchange      IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
            SELECT a.l08_symbol_code,
                   a.l08_exchange,
                   a.l08_short_desc,
                   a.l08_previous_closed,
                   a.l08_available_qty,
                   a.l08_market_value,
                   a.l08_l10_liquid_id,
                   l10.l10_liquid_id,
                   l10.l10_liquid_name,
                   conctype.l10_liquid_name AS conc_name,
                   conctype.l10_liquid_id AS conc_id,
                   a.l08_ltp,
                   a.l08_allowed_for_collateral,
                   a.l08_ltp,
                   a.l08_short_desc_ar,
                   a.l08_concentration_type,
                   l35."marginability_percentage",
                   a.l08_instrument_type,
                   a.l08_security_type,
                   a.l08_allowed_for_po
              FROM l08_symbol a,
                   l10_liquidity_type l10,
                   l10_liquidity_type conctype,
                   (SELECT l08.l08_symbol_code,
                           l08.l08_exchange,
                           l35.l35_marginability_percentage
                               AS "marginability_percentage"
                      FROM     l08_symbol l08
                           LEFT OUTER JOIN
                               (SELECT l35.l35_l08_symbol_code,
                                       l35.l35_l08_exchange,
                                       l35.l35_marginability_percentage,
                                       l11.l11_global_marginability_perc
                                  FROM l11_marginability_group l11,
                                       l35_symbol_marginability_perc l35
                                 WHERE     l11.l11_marginability_grp_id =
                                               l35.l35_l11_marginability_grp_id
                                       AND l11_is_default = 1) l35
                           ON     l08.l08_symbol_code =
                                      l35.l35_l08_symbol_code
                              AND l08.l08_exchange = l35.l35_l08_exchange) l35
             WHERE     a.l08_l10_liquid_id = l10.l10_liquid_id(+)
                   AND a.l08_concentration_type = conctype.l10_liquid_id(+)
                   AND a.l08_symbol_code =
                           DECODE (pl08_symbol_code,
                                   '1', a.l08_symbol_code,
                                   pl08_symbol_code)
                   AND a.l08_exchange = pl08_exchange
                   AND l35.l08_symbol_code = a.l08_symbol_code
                   AND l35.l08_exchange = a.l08_exchange;
    END;


    PROCEDURE l08_add_update (
        pkey                      OUT NUMBER,
        p08_symbol_code        IN     VARCHAR2,
        p08_exchange           IN     VARCHAR2,
        p08_short_desc         IN     VARCHAR2,
        p08_previous_closed    IN     VARCHAR2,
        p08_available_qty      IN     VARCHAR2,
        p08_market_value       IN     VARCHAR2,
        pl08_ltp                      VARCHAR2,
        p08_short_desc_ar      IN     VARCHAR2 DEFAULT NULL,
        pl08_instrument_type   IN     VARCHAR2,
        pl08_security_type     IN     VARCHAR2)
    IS
        symbol_count   NUMBER DEFAULT 0;
    BEGIN
        SELECT COUNT (*)
          INTO symbol_count
          FROM l08_symbol
         WHERE l08_symbol_code = p08_symbol_code;

        IF (symbol_count = 0)
        THEN
            INSERT INTO l08_symbol (l08_symbol_code,
                                    l08_exchange,
                                    l08_short_desc,
                                    l08_previous_closed,
                                    l08_available_qty,
                                    l08_market_value,
                                    l08_ltp,
                                    l08_short_desc_ar,
                                    l08_instrument_type,
                                    l08_security_type)
                 VALUES (p08_symbol_code,
                         p08_exchange,
                         p08_short_desc,
                         p08_previous_closed,
                         p08_available_qty,
                         p08_market_value,
                         pl08_ltp,
                         p08_short_desc_ar,
                         pl08_instrument_type,
                         pl08_security_type);

            pkey := 1;
        ELSE
            UPDATE l08_symbol
               SET l08_short_desc = p08_short_desc,
                   l08_previous_closed = p08_previous_closed,
                   l08_available_qty = p08_available_qty,
                   l08_market_value = p08_market_value,
                   l08_ltp = pl08_ltp,
                   l08_short_desc_ar = p08_short_desc_ar,
                   l08_instrument_type = pl08_instrument_type,
                   l08_security_type = pl08_security_type
             WHERE     l08_symbol_code = p08_symbol_code
                   AND l08_exchange = p08_exchange;
        END IF;
    END;

    PROCEDURE l08_update_liquid_type (
        pkey                             OUT NUMBER,
        p08_symbol_code               IN     VARCHAR2,
        p08_exchange                  IN     VARCHAR2,
        --p08_l10_liquid_id             IN     VARCHAR2,
        pl08_allowed_for_collateral          NUMBER DEFAULT 1,
        --pl08_concentration_type       IN     NUMBER,
        pl08_allowed_for_po			IN NUMBER DEFAULT 0
        )
    IS
    BEGIN
        UPDATE l08_symbol
           SET 
           		--l08_l10_liquid_id = p08_l10_liquid_id,
               l08_allowed_for_collateral = pl08_allowed_for_collateral,
               --l08_concentration_type = pl08_concentration_type,
               l08_allowed_for_po = pl08_allowed_for_po
         WHERE     l08_symbol_code = p08_symbol_code
               AND l08_exchange = p08_exchange;
    END;

    PROCEDURE l08_get_all_symbols (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR SELECT * FROM l08_symbol;
    END;

    PROCEDURE l08_get_all_symbols_classf (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l08_symbol a, l10_liquidity_type b
             WHERE a.l08_l10_liquid_id = b.l10_liquid_id;
    END;

    PROCEDURE l08_get_symbol_dis (pview OUT refcursor, psymbolcode VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
            SELECT symbolcode,
                   symboldescription_1 AS dis_eng,
                   symboldescription_2 AS dis_ar,
                   symbolshortdescription_1 AS short_dis_eng,
                   symbolshortdescription_2 AS short_dis_ar
              FROM mubasher_price.esp_symbolmap
             WHERE symbol = psymbolcode;
    END;

    PROCEDURE l08_get_symbol_margin_perc (pview                OUT refcursor,
                                          p08_symbol_code   IN     VARCHAR2,
                                          p08_exchange      IN     VARCHAR2,
                                          pl08_app_id       IN     VARCHAR2)
    IS
    BEGIN
        OPEN pview FOR
            SELECT CASE
                       WHEN l35.l35_marginability_percentage IS NULL
                       THEN
                           l11."global_marginability_perc"
                       ELSE
                           l35.l35_marginability_percentage
                   END
                       AS "marginability_perc"
              FROM     (SELECT l11.l11_global_marginability_perc
                                   AS "global_marginability_perc",
                               l01.l01_app_id AS "l11_app_id"
                          FROM l11_marginability_group l11,
                               l01_application l01
                         WHERE     l01.l01_l11_marginability_grp_id =
                                       l11.l11_marginability_grp_id
                               AND l01.l01_app_id = pl08_app_id) l11
                   LEFT OUTER JOIN
                       (SELECT l35.l35_marginability_percentage,
                               l01.l01_app_id AS "l35_app_id"
                          FROM l01_application l01,
                               l35_symbol_marginability_perc l35
                         WHERE     l01.l01_l11_marginability_grp_id =
                                       l35.l35_l11_marginability_grp_id
                               AND l35.l35_l08_symbol_code = p08_symbol_code
                               AND l35.l35_l08_exchange = p08_exchange
                               AND l01.l01_app_id = pl08_app_id) l35
                   ON l11."l11_app_id" = l35."l35_app_id";
    END;

    PROCEDURE l08_get_all_instrument_types (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT DISTINCT l08_instrument_type, l08_security_type
              FROM l08_symbol
             WHERE l08_security_type IS NOT NULL;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L08_SYMBOL_PKG

