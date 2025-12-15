DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE state_machine(id VARCHAR2(50 BYTE) PRIMARY KEY, "initial" NUMBER, final NUMBER, states CLOB)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('state_machine');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
