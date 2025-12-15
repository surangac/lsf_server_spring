-- Start of DDL Script for Package MUBASHER_LSF.L32_APPROVE_AGREEMENTS_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l32_approve_agreements_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l32_add_update (pkey                       OUT NUMBER,
                              pl32_id                 IN     NUMBER,
                              pl32_m11_id             IN     NUMBER,
                              pl32_l01_app_id         IN     NUMBER,
                              pl32_agreement_status   IN     NUMBER);

    PROCEDURE l32_initial_add (pkey                     OUT NUMBER,
                               pl32_l01_app_id       IN     NUMBER,
                               pfinancemethod        IN     NUMBER,
                               pproduct_type         IN     NUMBER,
                               pm11_agreement_type   IN     NUMBER);

    PROCEDURE l32_approve_agreement_by_user (
        pkey                       OUT NUMBER,
        pl32_l01_app_id         IN     NUMBER,
        pl32_agreement_status   IN     NUMBER);

    PROCEDURE l32_get_agreement_by_id (pview                OUT refcursor,
                                       pl32_l01_app_id   IN     NUMBER);
END;
/


CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l32_approve_agreements_pkg
IS
    PROCEDURE l32_add_update (pkey                       OUT NUMBER,
                              pl32_id                 IN     NUMBER,
                              pl32_m11_id             IN     NUMBER,
                              pl32_l01_app_id         IN     NUMBER,
                              pl32_agreement_status   IN     NUMBER)
    IS
    BEGIN
        IF (pl32_id = -1)
        THEN
            SELECT seq_l32_id.NEXTVAL INTO pkey FROM DUAL;

            INSERT INTO l32_appove_agreements (l32_id,
                                               l32_m11_id,
                                               l32_l01_app_id,
                                               l32_agreement_date,
                                               l32_agreement_status)
                 VALUES (pkey,
                         pl32_m11_id,
                         pl32_l01_app_id,
                         SYSDATE,
                         pl32_agreement_status);
        ELSE
            pkey := pl32_id;

            UPDATE l32_appove_agreements
               SET l32_m11_id = pl32_m11_id,
                   l32_l01_app_id = pl32_l01_app_id,
                   l32_agreement_date = SYSDATE,
                   l32_agreement_status = pl32_agreement_status
             WHERE l32_id = pkey;
        END IF;
    END;

    PROCEDURE l32_initial_add (pkey                     OUT NUMBER,
                               pl32_l01_app_id       IN     NUMBER,
                               pfinancemethod        IN     NUMBER,
                               pproduct_type         IN     NUMBER,
                               pm11_agreement_type   IN     NUMBER)
    IS
        im11_id   NUMBER (18);
    BEGIN
        FOR rec
            IN (SELECT m11_id, m11_version
                  FROM m11_agreements
                 WHERE     m11_finance_method = pfinancemethod
                       AND m11_product_type = pproduct_type
                       AND m11_agreement_type = pm11_agreement_type)
        LOOP
            SELECT seq_l32_id.NEXTVAL INTO pkey FROM DUAL;

            INSERT INTO l32_appove_agreements (l32_id,
                                               l32_m11_id,
                                               l32_l01_app_id,
                                               l32_agreement_date,
                                               l32_agreement_status,
                                               l32_m11_version)
                 VALUES (pkey,
                         rec.m11_id,
                         pl32_l01_app_id,
                         SYSDATE,
                         '0',
                         rec.m11_version);
        END LOOP;
    END;

    PROCEDURE l32_approve_agreement_by_user (
        pkey                       OUT NUMBER,
        pl32_l01_app_id         IN     NUMBER,
        pl32_agreement_status   IN     NUMBER)
    IS
    BEGIN
        FOR rec
            IN (SELECT l32_id
                  FROM l32_appove_agreements
                 WHERE     l32_l01_app_id = pl32_l01_app_id
                       AND l32_agreement_status = '0')
        LOOP
            UPDATE l32_appove_agreements
               SET l32_agreement_status = pl32_agreement_status
             WHERE l32_id = rec.l32_id;
        END LOOP;
    END;

    PROCEDURE l32_get_agreement_by_id (pview                OUT refcursor,
                                       pl32_l01_app_id   IN     NUMBER)
    IS
    BEGIN
        OPEN pview FOR
        SELECT m11_product_type,m11_finance_method,m11_agreement_type,
l33.L33_M11_FILE_EXTENSION as m11_file_extension,
L33_M11_FILE_NAME AS m11_file_name,
L32_M11_VERSION AS m11_version,
L33_M11_FILE_PATH AS m11_file_path
              FROM l32_appove_agreements l32, L33_M11_AGREEMENTS_LOG l33, M11_AGREEMENTS m11
             WHERE     l32.l32_l01_app_id = pl32_l01_app_id
                   AND l32.l32_m11_id = l33.L33_M11_ID AND l32.L32_M11_VERSION = l33.L33_M11_UPDATED_VERSION
                   AND m11.M11_ID = l33.L33_M11_ID;
--            SELECT *
--              FROM l32_appove_agreements l32, m11_agreements m11
--             WHERE     l32.l32_l01_app_id = pl32_l01_app_id
--                   AND l32.l32_m11_id = m11.m11_id;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L32_APPROVE_AGREEMENTS_PKG

