DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE SEQUENCE seq_l01_rollover_count INCREMENT BY 1 START WITH 1161 MINVALUE 1 MAXVALUE 9999999999999999999999999999 NOCYCLE NOORDER CACHE 20';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_sequences
     WHERE     sequence_owner = UPPER ('MUBASHER_LSF')
           AND sequence_name = UPPER ('seq_l01_rollover_count');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/