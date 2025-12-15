-- Start of DDL Script for Package MUBASHER_LSF.L15_TENOR_PKG
-- Generated 17-Nov-2025 10:52:52 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l15_tenor_pkg
/* Formatted on 08-Dec-2015 11:34:04 (QP5 v5.206) */
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l15_add_update (pkey                    OUT NUMBER,
                              p15_tenor_id         IN     NUMBER DEFAULT NULL,
                              p15_profit_percent   IN     NUMBER,
                              p15_duration         IN     NUMBER,
                              p15_created_by       IN     VARCHAR2);

    PROCEDURE l15_delete_all (pkey OUT NUMBER);

    PROCEDURE l15_get_all (pview OUT refcursor);

    PROCEDURE l15_get_tenor (pview OUT refcursor, pl15_tenor_id IN NUMBER);

    PROCEDURE l15_change_status (pkey                    OUT NUMBER,
                                 p15_duration                NUMBER,
                                 pl15_lvl1_approved_by       VARCHAR2,
                                 pl15_status                 NUMBER,
    							 p15_tenor_id              NUMBER DEFAULT NULL);

    PROCEDURE l15_remove_tenor_group (pkey                     OUT NUMBER,
                                 pl15_tenor_id       NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l15_tenor_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l15_tenor_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l15_tenor_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l15_tenor_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l15_tenor_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l15_tenor_pkg
/* Formatted on 12/8/2015 11:36:35 AM (QP5 v5.206) */
IS
    PROCEDURE l15_add_update (pkey                    OUT NUMBER,
                              p15_tenor_id         IN     NUMBER DEFAULT NULL,
                              p15_profit_percent   IN     NUMBER,
                              p15_duration         IN     NUMBER,
                              p15_created_by       IN     VARCHAR2)
    IS
        tener_id      NUMBER;
        tenor_count   NUMBER DEFAULT 0;
    BEGIN
        SELECT COUNT (*)
          INTO tenor_count
          FROM l15_tenor
         WHERE l15_tenor_id = p15_tenor_id;

        IF (tenor_count = 0)
        THEN
            SELECT NVL (MAX (p15_duration), 0) + 1 INTO pkey FROM l15_tenor;

            INSERT INTO l15_tenor (l15_tenor_id,
                                   l15_profit_percent,
                                   l15_duration,
                                   l15_created_date,
                                   l15_created_by,
                                   l15_status)
                 VALUES (pkey,
                         p15_profit_percent,
                         p15_duration,
                         SYSDATE,
                         p15_created_by,
                         0);

            pkey := p15_duration;
        ELSE
            UPDATE l15_tenor
               SET l15_profit_percent = p15_profit_percent,
                   l15_duration = p15_duration,
                   l15_created_date = SYSDATE,
                   l15_created_by = p15_created_by,
                   l15_status = 0
            WHERE l15_tenor_id = p15_tenor_id; -- AND l15_tenor_id = pl15_tenor_id;
        END IF;
    END;

    PROCEDURE l15_delete_all (pkey OUT NUMBER)
    IS
    BEGIN
        DELETE FROM l15_tenor;

        pkey := '1';
    END;

    PROCEDURE l15_get_all (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
              SELECT *
                FROM l15_tenor
            ORDER BY l15_duration;
    END;

    PROCEDURE l15_get_tenor (pview OUT refcursor, pl15_tenor_id IN NUMBER)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM l15_tenor
             WHERE l15_tenor_id = pl15_tenor_id;
    END;

    PROCEDURE l15_change_status (pkey                    OUT NUMBER,
                                 p15_duration                NUMBER,
                                 pl15_lvl1_approved_by       VARCHAR2,
                                 pl15_status                 NUMBER,
                                 p15_tenor_id              NUMBER DEFAULT NULL)
    IS
    BEGIN
        UPDATE l15_tenor
           SET l15_status = pl15_status,
               l15_lvl1_approved_date = SYSDATE,
               l15_lvl1_approved_by = pl15_lvl1_approved_by,
         	   l15_duration = p15_duration 
         WHERE l15_tenor_id = p15_tenor_id;

        pkey := 1;
    END;

    PROCEDURE l15_remove_tenor_group (pkey OUT NUMBER, pl15_tenor_id NUMBER)
    IS
    BEGIN
        DELETE FROM l15_tenor a
              WHERE a.l15_tenor_id = pl15_tenor_id;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L15_TENOR_PKG

