DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE user_validations(id VARCHAR2(24 BYTE) PRIMARY KEY, username VARCHAR2(24 BYTE), password VARCHAR2(24 BYTE), allowedfunctions VARCHAR2(24 BYTE), firstname VARCHAR2(24 BYTE), userstatus VARCHAR2(24 BYTE))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('user_validations');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
