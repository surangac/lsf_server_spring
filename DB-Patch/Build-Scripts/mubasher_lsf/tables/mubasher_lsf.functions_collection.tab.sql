DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE functions_collection(id VARCHAR2(50 BYTE) PRIMARY KEY, function_name VARCHAR2(200 BYTE), description VARCHAR2(500 BYTE), belonging_module VARCHAR2(50 BYTE), functionlistversion NUMBER)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('functions_collection');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
