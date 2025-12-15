DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L07_CASH_ACCOUNT ADD(L07_PENDING_SETTLE NUMBER (18, 5) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L07_CASH_ACCOUNT')
           AND column_name = UPPER ('L07_PENDING_SETTLE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L07_CASH_ACCOUNT ADD(L07_NET_RECEIVABLE NUMBER (18, 5) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L07_CASH_ACCOUNT')
           AND column_name = UPPER ('L07_NET_RECEIVABLE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE INDEX idx_l07_app_id_type ON l07_cash_account(l07_l01_app_id, L07_IS_LSF_TYPE)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_indexes
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l07_cash_account')
           AND index_name = UPPER ('idx_l07_app_id_type');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
