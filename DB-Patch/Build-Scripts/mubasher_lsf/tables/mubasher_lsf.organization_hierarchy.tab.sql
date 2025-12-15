DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE organization_hierarchy (id VARCHAR2(50 BYTE) PRIMARY KEY, appid VARCHAR2(50 BYTE), "desc" VARCHAR2(200 BYTE), children CLOB, ancestors CLOB, immediateparents CLOB, users CLOB)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('organization_hierarchy');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
