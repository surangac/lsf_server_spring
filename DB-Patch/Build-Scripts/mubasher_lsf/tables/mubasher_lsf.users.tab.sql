DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE users (id NUMBER, user_name VARCHAR2(100 BYTE) NOT NULL PRIMARY KEY, password VARCHAR2(200 BYTE) NOT NULL, first_name VARCHAR2(100 BYTE), last_name VARCHAR2(100 BYTE), telephone VARCHAR2(20 BYTE), mobile VARCHAR2(20 BYTE), nin VARCHAR2(50 BYTE), email VARCHAR2(200 BYTE), employ_no VARCHAR2(50 BYTE), organization VARCHAR2(200 BYTE), allowed_groups CLOB, userstatus NUMBER(1,0) DEFAULT 1, full_name VARCHAR2(200 BYTE), user_groups CLOB, removed_stats CLOB, last_request_time TIMESTAMP (6) WITH TIME ZONE, sessionId VARCHAR2(200 BYTE), cookies VARCHAR2(500 BYTE), fail_attamept_count NUMBER(10,0) DEFAULT 0, created_date TIMESTAMP (6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, modified_date TIMESTAMP (6) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, is_deleted NUMBER(1,0) DEFAULT 0)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE owner = UPPER ('MUBASHER_LSF') AND table_name = UPPER ('users');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
