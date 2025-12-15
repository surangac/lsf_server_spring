-- Start of DDL Script for Package MUBASHER_LSF.L33_M11_AGREEMENTS_LOG_PKG
-- Generated 17-Nov-2025 10:52:55 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE
PACKAGE              mubasher_lsf.l33_m11_agreements_log_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE add_update_l33 (pkey                             OUT NUMBER,
                              pl33_m11_id                   IN     NUMBER,
                              pl33_m11_updated_version      IN     VARCHAR2,
                              pl33_m11_previous_version     IN     VARCHAR2,
                              pl33_m11_uploaded_time        IN     DATE,
                              pl33_m11_file_extension       IN     VARCHAR2,
                              pl33_m11_file_name            IN     VARCHAR2,
                              pl33_m11_file_path            IN     VARCHAR2,
                              pl33_m11_uploaded_user_id     IN     VARCHAR2,
                              pl33_m11_uploaded_user_name   IN     VARCHAR2,
                              pl33_m11_uploaded_ip          IN     VARCHAR2,
                              pl33_status                   IN     NUMBER);
END;
/


CREATE OR REPLACE
PACKAGE BODY              mubasher_lsf.l33_m11_agreements_log_pkg
IS
    PROCEDURE add_update_l33 (pkey                             OUT NUMBER,
                              pl33_m11_id                   IN     NUMBER,
                              pl33_m11_updated_version      IN     VARCHAR2,
                              pl33_m11_previous_version     IN     VARCHAR2,
                              pl33_m11_uploaded_time        IN     DATE,
                              pl33_m11_file_extension       IN     VARCHAR2,
                              pl33_m11_file_name            IN     VARCHAR2,
                              pl33_m11_file_path            IN     VARCHAR2,
                              pl33_m11_uploaded_user_id     IN     VARCHAR2,
                              pl33_m11_uploaded_user_name   IN     VARCHAR2,
                              pl33_m11_uploaded_ip          IN     VARCHAR2,
                              pl33_status                   IN     NUMBER)
    IS
        icount   NUMBER (2);
BEGIN
SELECT COUNT (l33_id)
INTO icount
FROM l33_m11_agreements_log
WHERE     l33_m11_id = pl33_m11_id
  AND l33_m11_updated_version = pl33_m11_previous_version;

IF (icount > 0)
        THEN
UPDATE l33_m11_agreements_log
SET l33_status = 0
WHERE     l33_m11_id = pl33_m11_id
  AND l33_m11_updated_version = pl33_m11_previous_version;
END IF;

SELECT seq_l33_id.NEXTVAL INTO pkey FROM DUAL;

INSERT INTO l33_m11_agreements_log (l33_id,
                                    l33_m11_id,
                                    l33_m11_updated_version,
                                    l33_m11_previous_version,
                                    l33_m11_uploaded_time,
                                    l33_m11_file_extension,
                                    l33_m11_file_name,
                                    l33_m11_file_path,
                                    l33_m11_uploaded_user_id,
                                    l33_m11_uploaded_user_name,
                                    l33_m11_uploaded_ip,
                                    l33_status)
VALUES (pkey,
        pl33_m11_id,
        pl33_m11_updated_version,
        pl33_m11_previous_version,
        pl33_m11_uploaded_time,
        pl33_m11_file_extension,
        pl33_m11_file_name,
        pl33_m11_file_path,
        pl33_m11_uploaded_user_id,
        pl33_m11_uploaded_user_name,
        pl33_m11_uploaded_ip,
        pl33_status);
END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L33_M11_AGREEMENTS_LOG_PKG

