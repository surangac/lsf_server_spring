DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE mubasher_lsf.l33_m11_agreements_log(l33_id NUMBER (18, 0) NOT NULL, l33_m11_id NUMBER (18, 0) NOT NULL, l33_m11_updated_version VARCHAR2 (20 BYTE) NOT NULL, l33_m11_previous_version VARCHAR2 (20 BYTE) NOT NULL, l33_m11_uploaded_time DATE, l33_m11_file_extension VARCHAR2 (30 BYTE) NOT NULL, l33_m11_file_name VARCHAR2 (100 BYTE) NOT NULL, l33_m11_file_path VARCHAR2 (250 BYTE) NOT NULL, l33_m11_uploaded_user_id VARCHAR2 (20 BYTE), l33_m11_uploaded_user_name VARCHAR2 (100 BYTE), l33_m11_uploaded_ip VARCHAR2 (20 BYTE), l33_status NUMBER (2, 0))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l33_m11_agreements_log');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/


DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE mubasher_lsf.l33_m11_agreements_log ADD CONSTRAINT pk_l33 PRIMARY KEY (l33_id) USING INDEX';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_constraints
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l33_m11_agreements_log')
           AND constraint_name = UPPER ('pk_l33');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/