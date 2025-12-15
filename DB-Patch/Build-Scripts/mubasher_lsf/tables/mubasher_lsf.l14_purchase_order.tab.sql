DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l14_purchase_order ADD(l14_auth_abic_to_sell NUMBER (2))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l14_purchase_order')
           AND column_name = UPPER ('l14_auth_abic_to_sell');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l14_purchase_order ADD(l14_physical_delivery NUMBER (2))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l14_purchase_order')
           AND column_name = UPPER ('l14_physical_delivery');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l14_purchase_order ADD(l14_sell_but_not_settle NUMBER (2))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l14_purchase_order')
           AND column_name = UPPER ('l14_sell_but_not_settle');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l14_purchase_order ADD(L14_COM_CERTIFICATE_PATH VARCHAR2(1000))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l14_purchase_order')
           AND column_name = UPPER ('L14_COM_CERTIFICATE_PATH');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l14_purchase_order ADD(L14_CERTIFICATE_NUMBER VARCHAR2(500) NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l14_purchase_order')
           AND column_name = UPPER ('L14_CERTIFICATE_NUMBER');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE INDEX idx_l14_app_id ON l14_purchase_order(l14_app_id)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_indexes
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l14_purchase_order')
           AND index_name = UPPER ('idx_l14_app_id');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

ALTER TABLE mubasher_lsf."L14_PURCHASE_ORDER" MODIFY ("L14_ORD_SETTLED_AMOUNT" NUMBER(18,5) DEFAULT 0);
/
