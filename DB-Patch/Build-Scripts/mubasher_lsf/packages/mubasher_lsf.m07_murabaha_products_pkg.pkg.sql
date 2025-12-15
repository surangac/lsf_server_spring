-- Start of DDL Script for Package MUBASHER_LSF.M07_MURABAHA_PRODUCTS_PKG
-- Generated 17-Nov-2025 10:52:56 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m07_murabaha_products_pkg 
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

    PROCEDURE m07_get_products (pview OUT refcursor);

    PROCEDURE m07_update_product (pkey                            OUT NUMBER,
                                  pm07_type                    IN     NUMBER,
                                  pm07_name                    IN     VARCHAR2,
                                  pm07_description             IN     VARCHAR2,
                                  pm07_ar_name                 IN     VARCHAR2,
                                  pm07_ar_description          IN     VARCHAR2,
                                  pm07_finance_method_config   IN     NUMBER);

    PROCEDURE m07_change_product_status (pkey             OUT NUMBER,
                                         pm07_type     IN     NUMBER,
                                         pm07_status   IN     NUMBER);

    PROCEDURE m07_get_product (pview OUT refcursor, pm07_type IN NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.m07_murabaha_products_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.m07_murabaha_products_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.m07_murabaha_products_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.m07_murabaha_products_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.m07_murabaha_products_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m07_murabaha_products_pkg 
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

    PROCEDURE m07_get_products (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM m07_murabaha_products
            ORDER BY m07_type;
    END;

    PROCEDURE m07_update_product (pkey                            OUT NUMBER,
                                  pm07_type                    IN     NUMBER,
                                  pm07_name                    IN     VARCHAR2,
                                  pm07_description             IN     VARCHAR2,
                                  pm07_ar_name                 IN     VARCHAR2,
                                  pm07_ar_description          IN     VARCHAR2,
                                  pm07_finance_method_config   IN     NUMBER)
    IS
    BEGIN
        UPDATE m07_murabaha_products
           SET m07_name = pm07_name,
               m07_description = pm07_description,
               m07_ar_name = pm07_ar_name,
               m07_ar_description = pm07_ar_description,
               m07_status = 0,
               m07_finance_method_config = pm07_finance_method_config
         WHERE m07_type = pm07_type;

        pkey := 1;
    END;

    PROCEDURE m07_change_product_status (pkey             OUT NUMBER,
                                         pm07_type     IN     NUMBER,
                                         pm07_status   IN     NUMBER)
    IS
    BEGIN
        UPDATE m07_murabaha_products
           SET m07_status = pm07_status
         WHERE m07_type = pm07_type;

        pkey := 1;
    END;

    PROCEDURE m07_get_product (pview OUT refcursor, pm07_type IN NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM m07_murabaha_products a
             WHERE a.m07_type = pm07_type;
    END;
-- Enter further code below as specified in the Package spec.
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M07_MURABAHA_PRODUCTS_PKG

