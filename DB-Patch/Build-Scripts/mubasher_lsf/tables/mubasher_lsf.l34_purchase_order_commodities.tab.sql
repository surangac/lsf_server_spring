CREATE TABLE mubasher_lsf.l34_purchase_order_commodities
(
    l34_m12_commodity_code    VARCHAR2 (20 BYTE) NOT NULL,
    l34_m12_exchange          VARCHAR2 (20 BYTE) NOT NULL,
    l34_l16_purchase_ord_id   VARCHAR2 (20 BYTE) NOT NULL,
    l34_percentage            NUMBER (5, 2),
    l34_sold_amnt             NUMBER (18, 5)
)
    SEGMENT CREATION IMMEDIATE
NOPARALLEL
LOGGING
MONITORING
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L34_PURCHASE_ORDER_COMMODITIES ADD(L34_BOUGHT_AMNT NUMBER(18,5) NULL)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L34_PURCHASE_ORDER_COMMODITIES')
           AND column_name = UPPER ('L34_BOUGHT_AMNT');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

--DECLARE
--    l_count   NUMBER := 0;
--    l_ddl     VARCHAR2 (1000)
--        := 'ALTER TABLE L34_PURCHASE_ORDER_COMMODITIES ADD(L34_SOLD_AMNT NUMBER(18,5) NULL)';
--BEGIN
--    SELECT COUNT (*)
--      INTO l_count
--      FROM all_tab_columns
--     WHERE     owner = UPPER ('MUBASHER_LSF')
--           AND table_name = UPPER ('L34_PURCHASE_ORDER_COMMODITIES')
--           AND column_name = UPPER ('L34_SOLD_AMNT');
--
--    IF l_count = 0
--    THEN
--        EXECUTE IMMEDIATE l_ddl;
--    END IF;
--END;
--/

-- ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES ADD L34_SOLD_AMNT_TMP NUMBER(18,5) NULL;

-- UPDATE L34_PURCHASE_ORDER_COMMODITIES
-- SET L34_SOLD_AMNT_TMP=L34_SOLD_AMNT;

-- ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES DROP COLUMN L34_SOLD_AMNT;

-- ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES ADD L34_SOLD_AMNT NUMBER(18,5) NULL;

-- UPDATE L34_PURCHASE_ORDER_COMMODITIES
-- SET L34_SOLD_AMNT=L34_SOLD_AMNT_TMP;

-- UPDATE L34_PURCHASE_ORDER_COMMODITIES
-- SET L34_SOLD_AMNT_TMP=NULL;

-- ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES DROP COLUMN L34_SOLD_AMNT_TMP;
-- /

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE INDEX idx_l34_purchase_ord_id ON l34_purchase_order_commodities(l34_l16_purchase_ord_id)';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_indexes
     WHERE     owner = UPPER ('mubasher_lsf')
           AND table_name = UPPER ('l34_purchase_order_commodities')
           AND index_name = UPPER ('idx_l34_purchase_ord_id');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
