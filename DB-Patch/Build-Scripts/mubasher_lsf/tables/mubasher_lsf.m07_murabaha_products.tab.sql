DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE M07_MURABAHA_PRODUCTS ADD(M07_FINANCE_METHOD_CONFIG NUMBER(2,0) DEFAULT 1)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('M07_MURABAHA_PRODUCTS')
           AND column_name = UPPER ('M07_FINANCE_METHOD_CONFIG');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

COMMENT ON COLUMN "MUBASHER_LSF"."M07_MURABAHA_PRODUCTS"."M07_FINANCE_METHOD_CONFIG" IS '0 - None , 1 - Share , 2 - Commodity , 3 - Both';
/
