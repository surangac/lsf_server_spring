DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE user_entitlements (func_id  NUMBER, function_name VARCHAR2(200 BYTE), exp_date DATE, group_id VARCHAR2(100 BYTE), user_id VARCHAR2(100 BYTE) NOT NULL, FOREIGN KEY (user_id) REFERENCES users(user_name))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('user_entitlements');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
