-- Start of DDL Script for Package MUBASHER_LSF.M11_AGREEMENTS_PKG
-- Generated 17-Nov-2025 10:52:57 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m11_agreements_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE m11_add_update (pkey                         OUT NUMBER,
                              pm11_id                   IN     NUMBER,
                              pm11_product_type         IN     NUMBER,
                              pm11_finance_method       IN     NUMBER,
                              pm11_agreement_type       IN     NUMBER,
                              pm11_file_extension       IN     VARCHAR2,
                              pm11_file_name            IN     VARCHAR2,
                              pm11_file_path            IN     VARCHAR2,
                              pm11_version              IN     VARCHAR2,
                              pm11_uploaded_user_id     IN     VARCHAR2,
                              pm11_uploaded_user_name   IN     VARCHAR2,
                              pm11_uploaded_ip          IN     VARCHAR2);

    PROCEDURE m11_update_by_admin (pkey                         OUT NUMBER,
                                   pm11_product_type         IN     NUMBER,
                                   pm11_finance_method       IN     NUMBER,
                                   pm11_agreement_type       IN     NUMBER,
                                   pm11_file_extension       IN     VARCHAR2,
                                   pm11_file_name            IN     VARCHAR2,
                                   pm11_file_path            IN     VARCHAR2,
                                   pm11_version              IN     VARCHAR2,
                                   pm11_uploaded_user_id     IN     VARCHAR2,
                                   pm11_uploaded_user_name   IN     VARCHAR2,
                                   pm11_uploaded_ip          IN     VARCHAR2);

    PROCEDURE m11_get_active_for_product (
        pview                    OUT refcursor,
        pm11_finance_method   IN     NUMBER,
        pm11_product_type     IN     NUMBER);
END;
/


CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m11_agreements_pkg
IS
    PROCEDURE m11_add_update (pkey                         OUT NUMBER,
                              pm11_id                   IN     NUMBER,
                              pm11_product_type         IN     NUMBER,
                              pm11_finance_method       IN     NUMBER,
                              pm11_agreement_type       IN     NUMBER,
                              pm11_file_extension       IN     VARCHAR2,
                              pm11_file_name            IN     VARCHAR2,
                              pm11_file_path            IN     VARCHAR2,
                              pm11_version              IN     VARCHAR2,
                              pm11_uploaded_user_id     IN     VARCHAR2,
                              pm11_uploaded_user_name   IN     VARCHAR2,
                              pm11_uploaded_ip          IN     VARCHAR2)
    IS
BEGIN
        IF (pm11_id = -1)
        THEN
SELECT seq_m11_id.NEXTVAL INTO pkey FROM DUAL;

INSERT INTO m11_agreements (m11_id,
                            m11_product_type,
                            m11_finance_method,
                            m11_agreement_type,
                            m11_file_extension,
                            m11_file_name,
                            m11_file_path,
                            m11_version,
                            m11_uploaded_user_id,
                            m11_uploaded_user_name,
                            m11_uploaded_ip)
VALUES (pkey,
        pm11_product_type,
        pm11_finance_method,
        pm11_agreement_type,
        pm11_file_extension,
        pm11_file_name,
        pm11_file_path,
        pm11_version,
        pm11_uploaded_user_id,
        pm11_uploaded_user_name,
        pm11_uploaded_ip);
ELSE
            pkey := pm11_id;

UPDATE m11_agreements
SET m11_product_type = pm11_product_type,
    m11_finance_method = pm11_finance_method,
    m11_agreement_type = pm11_agreement_type,
    m11_file_extension = pm11_file_extension,
    m11_file_name = pm11_file_name,
    m11_file_path = pm11_file_path,
    m11_version = pm11_version,
    m11_uploaded_user_id = pm11_uploaded_user_id,
    m11_uploaded_user_name = pm11_uploaded_user_name,
    m11_uploaded_ip = pm11_uploaded_ip
WHERE m11_id = pkey;
END IF;
END;

    PROCEDURE m11_update_by_admin (pkey                         OUT NUMBER,
                                   pm11_product_type         IN     NUMBER,
                                   pm11_finance_method       IN     NUMBER,
                                   pm11_agreement_type       IN     NUMBER,
                                   pm11_file_extension       IN     VARCHAR2,
                                   pm11_file_name            IN     VARCHAR2,
                                   pm11_file_path            IN     VARCHAR2,
                                   pm11_version              IN     VARCHAR2,
                                   pm11_uploaded_user_id     IN     VARCHAR2,
                                   pm11_uploaded_user_name   IN     VARCHAR2,
                                   pm11_uploaded_ip          IN     VARCHAR2)
IS
        im11_version   NUMBER (18);
        il33_id        NUMBER (18);
        im11_id        NUMBER (18);
        icount         NUMBER (2);
BEGIN
SELECT COUNT (m11_id)
INTO icount
FROM m11_agreements
WHERE     m11_product_type = pm11_product_type
  AND m11_finance_method = pm11_finance_method
  AND m11_agreement_type = pm11_agreement_type;

IF (icount > 0)
        THEN
SELECT m11_version, m11_id
INTO im11_version, im11_id
FROM m11_agreements
WHERE     m11_product_type = pm11_product_type
  AND m11_finance_method = pm11_finance_method
  AND m11_agreement_type = pm11_agreement_type;



UPDATE m11_agreements
SET m11_file_extension = pm11_file_extension,
    m11_file_name = pm11_file_name,
    m11_file_path = pm11_file_path,
    m11_version = pm11_version,
    m11_uploaded_user_id = pm11_uploaded_user_id,
    m11_uploaded_user_name = pm11_uploaded_user_name,
    m11_uploaded_ip = pm11_uploaded_ip
WHERE m11_id = im11_id;


l33_m11_agreements_log_pkg.add_update_l33 (
                il33_id,
                im11_id,
                pm11_version,
                im11_version,
                SYSDATE,
                pm11_file_extension,
                pm11_file_name,
                pm11_file_path,
                pm11_uploaded_user_id,
                pm11_uploaded_user_name,
                pm11_uploaded_ip,
                1);

            pkey := im11_id;
ELSE
            m11_agreements_pkg.m11_add_update (im11_id,
                                               -1,
                                               pm11_product_type,
                                               pm11_finance_method,
                                               pm11_agreement_type,
                                               pm11_file_extension,
                                               pm11_file_name,
                                               pm11_file_path,
                                               pm11_version,
                                               pm11_uploaded_user_id,
                                               pm11_uploaded_user_name,
                                               pm11_uploaded_ip);

            l33_m11_agreements_log_pkg.add_update_l33 (
                il33_id,
                im11_id,
                pm11_version,
                0,
                SYSDATE,
                pm11_file_extension,
                pm11_file_name,
                pm11_file_path,
                pm11_uploaded_user_id,
                pm11_uploaded_user_name,
                pm11_uploaded_ip,
                1);

            pkey := im11_id;
END IF;
END;

    PROCEDURE m11_get_active_for_product (
        pview                    OUT refcursor,
        pm11_finance_method   IN     NUMBER,
        pm11_product_type     IN     NUMBER)
IS
BEGIN
OPEN pview FOR
SELECT *
FROM m11_agreements m11
WHERE     m11.m11_product_type = pm11_product_type
  AND m11.m11_finance_method = pm11_finance_method;
END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M11_AGREEMENTS_PKG

