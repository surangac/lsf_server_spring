DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE TABLE mubasher_lsf.m11_agreements (m11_id NUMBER (18, 0) NOT NULL, m11_product_type NUMBER (2, 0) NOT NULL, m11_finance_method NUMBER (2, 0) NOT NULL, m11_agreement_type NUMBER (2, 0) NOT NULL, m11_file_extension VARCHAR2 (30 BYTE) NOT NULL, m11_file_name VARCHAR2 (100 BYTE) NOT NULL, m11_file_path VARCHAR2 (250 BYTE) NOT NULL, m11_version VARCHAR2 (20 BYTE) NOT NULL, m11_uploaded_user_id VARCHAR2 (20 BYTE), m11_uploaded_user_name VARCHAR2 (100 BYTE), m11_uploaded_ip VARCHAR2 (20 BYTE))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tables
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('m11_agreements');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE mubasher_lsf.m11_agreements ADD CONSTRAINT pk_m11 PRIMARY KEY (m11_id) USING INDEX';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_constraints
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('m11_agreements')
           AND constraint_name = UPPER ('pk_m11');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

COMMENT ON COLUMN mubasher_lsf.m11_agreements.m11_agreement_type IS
    '1-agreement, 2-contract rollover'
/
COMMENT ON COLUMN mubasher_lsf.m11_agreements.m11_finance_method IS
    '1 - share finance, 2 - commodities'
/
COMMENT ON COLUMN mubasher_lsf.m11_agreements.m11_product_type IS 'm07_type'
/