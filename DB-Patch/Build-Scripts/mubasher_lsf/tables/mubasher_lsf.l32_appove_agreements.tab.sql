CREATE TABLE mubasher_lsf.l32_appove_agreements
(
    l32_id                 NUMBER (18, 0) NOT NULL,
    l32_m11_id             NUMBER (18, 0) NOT NULL,
    l32_l01_app_id         NUMBER (18, 0) NOT NULL,
    l32_agreement_date     DATE,
    l32_agreement_status   NUMBER (2, 0) NOT NULL
)
    SEGMENT CREATION IMMEDIATE
NOPARALLEL
LOGGING
MONITORING
/



ALTER TABLE mubasher_lsf.l32_appove_agreements
    ADD CONSTRAINT pk_l32 PRIMARY KEY (l32_id)
    USING INDEX
/

DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE l32_appove_agreements ADD(l32_m11_version VARCHAR2(20 BYTE))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('l32_appove_agreements')
           AND column_name = UPPER ('l32_m11_version');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/

