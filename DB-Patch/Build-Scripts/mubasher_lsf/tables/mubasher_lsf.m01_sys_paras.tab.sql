DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_SHARE_FIXED_FEE NUMBER (15, 2) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_SHARE_FIXED_FEE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_COMODITY_FIXED_FEE NUMBER (15, 2) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_COMODITY_FIXED_FEE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/


DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_SHARE_ADMIN_FEE NUMBER (15, 2) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_SHARE_ADMIN_FEE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_COMODITY_ADMIN_FEE NUMBER (15, 2) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_COMODITY_ADMIN_FEE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_MIN_ROLLOVER_RATIO NUMBER (3))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_MIN_ROLLOVER_RATIO');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_MIN_ROLLOVER_PERIOD NUMBER (10))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_MIN_ROLLOVER_PERIOD');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_MAX_ROLLOVER_PERIOD NUMBER (10))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_MAX_ROLLOVER_PERIOD');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_GRACE_PER_COMMODITY_SELL NUMBER (18, 0))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_GRACE_PER_COMMODITY_SELL');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/


COMMENT ON COLUMN M01_SYS_PARAS.M01_GRACE_PER_COMMODITY_SELL IS 'Automatic sell commodity afer grace period in minutes'
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_ORDER_ACCEPTANCE_LIMIT NUMBER (5, 2) DEFAULT 150 NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_ORDER_ACCEPTANCE_LIMIT');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/


DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M01_SYS_PARAS ADD(M01_INSTITUTION_INVEST_ACC VARCHAR2(100))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M01_SYS_PARAS')
           AND column_name = UPPER ('M01_INSTITUTION_INVEST_ACC');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

