DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_ROLLOVER_APP_ID NUMBER (18))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_ROLLOVER_APP_ID');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_ROLLOVER_COUNT NUMBER (18))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_ROLLOVER_COUNT');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_DEVICE_TYPE VARCHAR2 (20))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_DEVICE_TYPE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_IP_ADDRESS VARCHAR2 (20))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_IP_ADDRESS');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_FACILITY_TRANSFER_STATUS VARCHAR2 (100) DEFAULT NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_FACILITY_TRANSFER_STATUS');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_ADITIONAL_DETAILS VARCHAR2 (500) NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_ADITIONAL_DETAILS');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_ADDITIONAL_DOC_NAME VARCHAR2 (100) NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_ADDITIONAL_DOC_NAME');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_ADDITIONAL_DOC_PATH VARCHAR2 (500) NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_ADDITIONAL_DOC_PATH');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L01_APPLICATION ADD(L01_FINANCE_METHOD INTEGER NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L01_APPLICATION')
           AND column_name = UPPER ('L01_FINANCE_METHOD');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

COMMENT ON COLUMN MUBASHER_LSF.L01_APPLICATION.L01_FINANCE_METHOD IS '1= share finance';
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE INDEX idx_l01_current_level_status ON l01_application(l01_current_level, l01_overall_status)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_indexes
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l01_application')
           AND index_name = UPPER ('idx_l01_current_level_status');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

GRANT SELECT ON mubasher_lsf.l01_application TO MUBASHER_OMS;
/


/*
-- Create indexes for better performance
--CREATE INDEX idx_l02_app_id ON l02_app_state(l02_l01_app_id);
--CREATE INDEX idx_l32_app_id ON l32_appove_agreements(l32_l01_app_id);
--CREATE INDEX idx_l14_app_id ON l14_purchase_order(l14_app_id);
--CREATE INDEX idx_l34_purchase_ord_id ON l34_purchase_order_commodities(l34_l16_purchase_ord_id);
--CREATE INDEX idx_l22_purchase_ord_id ON l22_installments(l22_purchase_ord_id);
--CREATE INDEX idx_l07_app_id ON l07_cash_account(l07_l01_app_id);
--CREATE INDEX idx_l06_app_id_type ON l06_trading_acc(l06_l01_app_id, l06_is_lsf_type);
--
---- Composite indexes for complex queries
--CREATE INDEX idx_l01_current_overall ON l01_application(l01_current_level, l01_overall_status);
--CREATE INDEX idx_l01_date_status ON l01_application(l01_date, l01_current_level, l01_overall_status);
--
---- Update statistics
--EXEC DBMS_STATS.GATHER_TABLE_STATS('YOUR_SCHEMA', 'L01_APPLICATION');
--EXEC DBMS_STATS.GATHER_TABLE_STATS('YOUR_SCHEMA', 'L02_APP_STATE');
--EXEC DBMS_STATS.GATHER_TABLE_STATS('YOUR_SCHEMA', 'L32_APPOVE_AGREEMENTS');
--EXEC DBMS_STATS.GATHER_TABLE_STATS('YOUR_SCHEMA', 'L14_PURCHASE_ORDER');
*/
