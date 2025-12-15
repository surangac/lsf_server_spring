DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE audit_trail_activity (id VARCHAR2(50 BYTE) PRIMARY KEY, "message" VARCHAR2(4000 BYTE), "timestamp" TIMESTAMP (6) WITH TIME ZONE, "level" VARCHAR2(50 BYTE), "meta" CLOB)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('audit_trail_activity');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
