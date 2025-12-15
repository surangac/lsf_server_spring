DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE function_groups_functions (id NUMBER, function_id NUMBER, function_name VARCHAR2(100 BYTE), description VARCHAR2(500 BYTE), belonging_module VARCHAR2(100 BYTE), exp_date DATE, prev_date DATE, group_id VARCHAR2(100 BYTE) NOT NULL, FOREIGN KEY (group_id) REFERENCES function_groups(group_id))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('function_groups_functions');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
