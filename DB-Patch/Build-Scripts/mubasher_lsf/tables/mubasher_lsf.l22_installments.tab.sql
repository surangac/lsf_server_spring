DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE INDEX idx_installments_po_id ON l22_installments(l22_purchase_ord_id)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_indexes
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l22_installments')
           AND index_name = UPPER ('idx_installments_po_id');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
