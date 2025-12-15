-- Start of DDL Script for Package MUBASHER_LSF.L11_MARGINABILITY_GROUP_PKG
-- Generated 17-Nov-2025 10:52:51 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l11_marginability_group_pkg
/* Formatted on 08-Dec-2015 11:25:58 (QP5 v5.206) */
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

    PROCEDURE l11_add_update (
        pkey                        OUT NUMBER,
        pl11_marginability_grp_id       NUMBER,
        pl11_group_name                 VARCHAR2,
        pl11_status                     NUMBER DEFAULT 0,
        pl11_created_by                 VARCHAR2,
        pl11_approved_by                VARCHAR2,
        pl11_is_default                 NUMBER DEFAULT 0,
        pl11_additional_details VARCHAR2,
        pl11_global_marginability_perc NUMBER);

    PROCEDURE l11_get_all_groups (pview OUT refcursor,pl11_status varchar2);

    PROCEDURE l11_get_group_by_id (pview                       OUT refcursor,
                                   pl11_marginability_grp_id       NUMBER);

    PROCEDURE l11_get_default_group (pview OUT refcursor);

    PROCEDURE l11_add_liquidity_type (pkey                        OUT NUMBER,
                                      pl17_marginability_grp_id       NUMBER,
                                      pl17_liquid_id                  NUMBER,
                                      pl17_marginability_perc         NUMBER);

    PROCEDURE l11_get_liquid_types_in_group (
        pview                       OUT refcursor,
        pl17_marginability_grp_id       NUMBER);

    PROCEDURE l11_change_status (pkey                        OUT NUMBER,
                                 pl11_marginability_grp_id       NUMBER,
                                 pl11_approved_by                VARCHAR2,
                                 pl11_status                     NUMBER);

    PROCEDURE l11_remove (pkey OUT NUMBER, pl11_marginability_grp_id NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l11_marginability_group_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l11_marginability_group_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l11_marginability_group_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l11_marginability_group_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l11_marginability_group_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l11_marginability_group_pkg
/* Formatted on 08-Dec-2015 11:25:54 (QP5 v5.206) */
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

    PROCEDURE l11_add_update (
        pkey                        OUT NUMBER,
        pl11_marginability_grp_id       NUMBER,
        pl11_group_name                 VARCHAR2,
        pl11_status                     NUMBER DEFAULT 0,
        pl11_created_by                 VARCHAR2,
        pl11_approved_by                VARCHAR2,
        pl11_is_default                 NUMBER DEFAULT 0,
        pl11_additional_details VARCHAR2,
        pl11_global_marginability_perc NUMBER)
    IS
        v_default_count   NUMBER := 0;
        v_is_default      NUMBER := 0;
    BEGIN
        v_is_default := pl11_is_default;

        IF (v_is_default > 0)
        THEN
            UPDATE l11_marginability_group
               SET l11_is_default = 0
             WHERE l11_is_default = 1;
        ELSE
            SELECT COUNT (*)
              INTO v_default_count
              FROM l11_marginability_group
             WHERE l11_is_default = 1;

            IF (v_default_count = 0)
            THEN
                v_is_default := 1;
            END IF;
        END IF;

        IF (pl11_marginability_grp_id = -1)
        THEN
            SELECT NVL (MAX (l11_marginability_grp_id), 0) + 1
              INTO pkey
              FROM l11_marginability_group;

            INSERT INTO l11_marginability_group (l11_marginability_grp_id,
                                                 l11_group_name,
                                                 l11_created_date,
                                                 l11_status,
                                                 l11_created_by,
                                                 l11_approved_by,
                                                 l11_is_default,
                                                 L11_ADDITIONAL_DETAILS,
                                                 L11_GLOBAL_MARGINABILITY_PERC)
                 VALUES (pkey,
                         pl11_group_name,
                         SYSDATE,
                         pl11_status,
                         pl11_created_by,
                         pl11_approved_by,
                         v_is_default,
                         pl11_additional_details,
                         pl11_global_marginability_perc);
        ELSE
            pkey := pl11_marginability_grp_id;

            UPDATE l11_marginability_group
               SET l11_group_name = pl11_group_name,
                   l11_status = pl11_status,
                   l11_created_by = pl11_created_by,
                   l11_approved_by = pl11_approved_by,
                   l11_is_default = v_is_default,
                   L11_ADDITIONAL_DETAILS=pl11_additional_details,
                   L11_GLOBAL_MARGINABILITY_PERC=pl11_global_marginability_perc
             WHERE l11_marginability_grp_id = pkey;
        END IF;
    END;

    PROCEDURE l11_get_all_groups (pview OUT refcursor,pl11_status varchar2)
    IS
    BEGIN
        OPEN pview FOR 
            SELECT * FROM l11_marginability_group
        WHERE CASE 
                WHEN pl11_status = '*' THEN 1
                WHEN l11_status = TO_NUMBER(pl11_status) THEN 1
                ELSE 0
              END = 1;
    END;

    PROCEDURE l11_get_group_by_id (pview                       OUT refcursor,
                                   pl11_marginability_grp_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l11_marginability_group
             WHERE l11_marginability_grp_id = pl11_marginability_grp_id;
    END;


    PROCEDURE l11_get_default_group (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l11_marginability_group l
             WHERE l.l11_is_default = 1;
    END;

    PROCEDURE l11_add_liquidity_type (pkey                        OUT NUMBER,
                                      pl17_marginability_grp_id       NUMBER,
                                      pl17_liquid_id                  NUMBER,
                                      pl17_marginability_perc         NUMBER)
    IS
        preccount   NUMBER := 0;
    BEGIN
        SELECT COUNT (*)
          INTO preccount
          FROM l17_marg_liquidity_type
         WHERE     l17_marginability_grp_id = pl17_marginability_grp_id
               AND l17_liquid_id = pl17_liquid_id;

        IF (preccount > 0)
        THEN
            UPDATE l17_marg_liquidity_type
               SET l17_marginability_perc = pl17_marginability_perc
             WHERE     l17_marginability_grp_id = pl17_marginability_grp_id
                   AND l17_liquid_id = pl17_liquid_id;

            pkey := 1;
        ELSE
            INSERT
              INTO l17_marg_liquidity_type (l17_marginability_grp_id,
                                            l17_liquid_id,
                                            l17_marginability_perc)
            VALUES (
                       pl17_marginability_grp_id,
                       pl17_liquid_id,
                       pl17_marginability_perc);

            pkey := 2;
        END IF;
    END;

    PROCEDURE l11_get_liquid_types_in_group (
        pview                       OUT refcursor,
        pl17_marginability_grp_id       NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT l17.l17_marginability_grp_id,
                   l10.l10_liquid_id,
                   l17.l17_marginability_perc,
                   l10.l10_liquid_name
              FROM l17_marg_liquidity_type l17, l10_liquidity_type l10
             WHERE     l17.l17_liquid_id = l10.l10_liquid_id
                   AND l17.l17_marginability_grp_id =
                           pl17_marginability_grp_id;
    END;

    PROCEDURE l11_change_status (pkey                        OUT NUMBER,
                                 pl11_marginability_grp_id       NUMBER,
                                 pl11_approved_by                VARCHAR2,
                                 pl11_status                     NUMBER)
    IS
    BEGIN
        UPDATE l11_marginability_group
           SET l11_approved_by = pl11_approved_by,
               l11_status = pl11_status,
               l11_approved_date = SYSDATE
         WHERE l11_marginability_grp_id = pl11_marginability_grp_id;
    END;

    PROCEDURE l11_remove (
        pkey OUT NUMBER,
        pl11_marginability_grp_id IN NUMBER
    )
    IS
        child_record_exception EXCEPTION;
        PRAGMA EXCEPTION_INIT(child_record_exception, -2292);
    BEGIN
        DELETE FROM l11_marginability_group a
        WHERE a.l11_marginability_grp_id = pl11_marginability_grp_id;

        pkey := 1; -- success
    EXCEPTION
        WHEN child_record_exception THEN
            pkey := -1; -- child record found
        WHEN OTHERS THEN
            pkey := -99; -- generic error
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L11_MARGINABILITY_GROUP_PKG

