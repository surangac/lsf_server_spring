DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE mubasher_lsf.m12_commodities (m12_id NUMBER (18, 0) NOT NULL, m12_commodity_code VARCHAR2 (20 BYTE) NOT NULL, m12_commodity_name VARCHAR2 (20 BYTE) NOT NULL, m12_exchange VARCHAR2 (20 BYTE) NOT NULL, m12_broker VARCHAR2 (20 BYTE) NOT NULL, m12_description VARCHAR2 (50 BYTE), m12_unit_of_measure VARCHAR2 (20 BYTE), m12_price NUMBER (18, 5), m12_status NUMBER (2, 0))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('m12_commodities');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M12_COMMODITIES ADD(M12_ALLOWED_FOR_PO NUMBER(2,0) DEFAULT 0 NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M12_COMMODITIES')
           AND column_name = UPPER ('M12_ALLOWED_FOR_PO');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

