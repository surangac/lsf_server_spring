CREATE TABLE mubasher_lsf.m08_profit_cal_m_data
(
    m08_job_date              DATE NOT NULL,
    m08_eligible_app_count    NUMBER,
    m08_completed_app_count   NUMBER,
    m08_start_time            DATE,
    m08_end_time              DATE
)
    SEGMENT CREATION IMMEDIATE
NOPARALLEL
LOGGING
MONITORING
/