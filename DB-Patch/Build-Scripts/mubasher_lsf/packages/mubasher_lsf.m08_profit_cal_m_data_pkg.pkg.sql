-- Start of DDL Script for Package MUBASHER_LSF.M08_PROFIT_CAL_M_DATA_PKG
-- Generated 17-Nov-2025 10:52:56 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m08_profit_cal_m_data_pkg
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

    PROCEDURE m08_get_profit_cal_last_entry (pview OUT refcursor);

    PROCEDURE m08_add_profit_cal_entry (
        pkey                         OUT NUMBER,
        pm08_eligible_app_count   IN     NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.m08_profit_cal_m_data_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.m08_profit_cal_m_data_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.m08_profit_cal_m_data_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.m08_profit_cal_m_data_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.m08_profit_cal_m_data_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m08_profit_cal_m_data_pkg
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
    PROCEDURE m08_get_profit_cal_last_entry (pview OUT refcursor)
    IS
BEGIN
OPEN pview FOR
SELECT *
FROM (  SELECT *
        FROM m08_profit_cal_m_data a
        ORDER BY a.m08_job_date DESC)
WHERE ROWNUM = 1;
END;

    -- Enter further code below as specified in the Package spec.
    PROCEDURE m08_add_profit_cal_entry (
        pkey                         OUT NUMBER,
        pm08_eligible_app_count   IN     NUMBER)
IS
BEGIN
INSERT INTO m08_profit_cal_m_data
VALUES (SYSDATE,
        pm08_eligible_app_count,
        0,
        SYSDATE,
        NULL);

pkey := 1;
END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M08_PROFIT_CAL_M_DATA_PKG

