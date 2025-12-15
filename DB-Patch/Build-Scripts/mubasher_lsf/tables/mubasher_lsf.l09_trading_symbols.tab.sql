DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L09_TRADING_SYMBOLS ADD(L09_AVAILABLE_QTY NUMBER (18, 0) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L09_TRADING_SYMBOLS')
           AND column_name = UPPER ('L09_AVAILABLE_QTY');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L09_TRADING_SYMBOLS ADD(L09_CLOSE_PRICE NUMBER (18, 5) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L09_TRADING_SYMBOLS')
           AND column_name = UPPER ('L09_CLOSE_PRICE');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L09_TRADING_SYMBOLS ADD(L09_LTP NUMBER (18, 5) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L09_TRADING_SYMBOLS')
           AND column_name = UPPER ('L09_LTP');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

COMMENT ON COLUMN L09_TRADING_SYMBOLS.L09_AVAILABLE_QTY IS 'total available QTY'
/
