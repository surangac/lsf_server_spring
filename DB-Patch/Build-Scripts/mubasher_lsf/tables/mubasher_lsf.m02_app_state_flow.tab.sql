-- 06-22--

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M02_APP_STATE_FLOW ADD(M02_APP_TYPE NUMBER(38,0) DEFAULT 0 NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M02_APP_STATE_FLOW')
           AND column_name = UPPER ('M02_APP_TYPE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

COMMENT ON COLUMN MUBASHER_LSF.M02_APP_STATE_FLOW.M02_APP_TYPE IS '0 for eaquity and Commodity, 1 for Roll over';

-- ALTER TABLE MUBASHER_LSF.M02_APP_STATE_FLOW DROP CONSTRAINT SYS_C00153345;
-- DROP INDEX MUBASHER_LSF.XPKM02_APP_STATE_FLOW;

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*)
    INTO v_count
    FROM all_indexes
    WHERE index_name = 'XPKM02_APP_STATE_FLOW'
      AND owner = 'MUBASHER_LSF';

    IF v_count > 0 THEN
        EXECUTE IMMEDIATE 'DROP INDEX MUBASHER_LSF.XPKM02_APP_STATE_FLOW';
    END IF;
END;
/


--DECLARE
--    v_count NUMBER;
--BEGIN
--    SELECT COUNT(*)
--    INTO v_count
--    FROM user_constraints
--    WHERE table_name = 'M02_APP_STATE_FLOW'
--      AND constraint_name = 'M02_APP_STATE_FLOW_UNIQUE';
--
--    IF v_count = 0 THEN
--        EXECUTE IMMEDIATE '
--            ALTER TABLE MUBASHER_LSF.M02_APP_STATE_FLOW
--            ADD CONSTRAINT M02_APP_STATE_FLOW_UNIQUE
--            UNIQUE (M02_STATE, M02_APP_TYPE) ENABLE';
--    END IF;
--END;
--/

