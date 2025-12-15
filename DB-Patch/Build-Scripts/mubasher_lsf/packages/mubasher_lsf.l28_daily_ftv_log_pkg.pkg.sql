-- Start of DDL Script for Package MUBASHER_LSF.L28_DAILY_FTV_LOG_PKG
-- Generated 17-Nov-2025 10:52:54 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l28_daily_ftv_log_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l28_add_update (pkey                          OUT NUMBER,
                              pl28_application_id               NUMBER,
                              pl28_ftv                          NUMBER,
                              pl28_total_colt_value             NUMBER,
                              pl28_total_pf_colt_value          NUMBER,
                              pl28_margine_call_triggered       NUMBER,
                              pl28_liquidation_triggered        NUMBER);

    PROCEDURE l28_get (pview OUT refcursor, pl28_application_id NUMBER);

    PROCEDURE l28_get_for_today (pview                 OUT refcursor,
                                 pl28_application_id       NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l28_daily_ftv_log_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l28_daily_ftv_log_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l28_daily_ftv_log_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l28_daily_ftv_log_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l28_daily_ftv_log_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l28_daily_ftv_log_pkg
IS
    PROCEDURE l28_add_update (pkey                          OUT NUMBER,
                              pl28_application_id               NUMBER,
                              pl28_ftv                          NUMBER,
                              pl28_total_colt_value             NUMBER,
                              pl28_total_pf_colt_value          NUMBER,
                              pl28_margine_call_triggered       NUMBER,
                              pl28_liquidation_triggered        NUMBER)
    IS
        v_rec_count   NUMBER (2, 0);
BEGIN
SELECT COUNT (1)
INTO v_rec_count
FROM l28_daily_ftv_log
WHERE     l28_application_id = pl28_application_id
  AND TRUNC (l28_date) = TRUNC (SYSDATE);

IF (v_rec_count = 0)
        THEN
            INSERT INTO l28_daily_ftv_log (l28_application_id,
                                           l28_date,
                                           l28_ftv,
                                           l28_total_colt_value,
                                           l28_total_pf_colt_value,
                                           l28_margine_call_triggered,
                                           l28_liquidation_triggered)
                 VALUES (pl28_application_id,
                         SYSDATE,
                         pl28_ftv,
                         pl28_total_colt_value,
                         pl28_total_pf_colt_value,
                         pl28_margine_call_triggered,
                         pl28_liquidation_triggered);
ELSE
UPDATE l28_daily_ftv_log
SET l28_ftv = pl28_ftv,
    l28_total_colt_value = pl28_total_colt_value,
    l28_total_pf_colt_value = pl28_total_pf_colt_value,
    l28_margine_call_triggered = pl28_margine_call_triggered,
    l28_liquidation_triggered = pl28_liquidation_triggered,
    l28_date = SYSDATE
WHERE     l28_application_id = pl28_application_id
  AND TRUNC (l28_date) = TRUNC (SYSDATE);
END IF;
END;

    PROCEDURE l28_get (pview OUT refcursor, pl28_application_id NUMBER)
IS
BEGIN
OPEN pview FOR
SELECT a.l28_application_id,
       TRUNC (a.l28_date) AS l28_date,
       a.l28_ftv,
       a.l28_total_colt_value,
       a.l28_total_pf_colt_value,
       a.l28_margine_call_triggered,
       a.l28_liquidation_triggered
FROM l28_daily_ftv_log a
WHERE l28_application_id = pl28_application_id
ORDER BY l28_date;
END;

    PROCEDURE l28_get_for_today (pview                 OUT refcursor,
                                 pl28_application_id       NUMBER)
IS
BEGIN
OPEN pview FOR
SELECT a.l28_application_id,
       TRUNC (a.l28_date) AS l28_date,
       a.l28_ftv,
       a.l28_total_colt_value,
       a.l28_total_pf_colt_value,
       a.l28_margine_call_triggered,
       a.l28_liquidation_triggered
FROM l28_daily_ftv_log a
WHERE     l28_application_id = pl28_application_id
  AND l28_date = SYSDATE
ORDER BY l28_date;
END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L28_DAILY_FTV_LOG_PKG

