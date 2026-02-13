DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE mubasher_lsf.m08_profit_cal_m_data (m08_job_date DATE NOT NULL, m08_eligible_app_count NUMBER, m08_completed_app_count NUMBER, m08_start_time DATE, m08_end_time DATE)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('m08_profit_cal_m_data');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/