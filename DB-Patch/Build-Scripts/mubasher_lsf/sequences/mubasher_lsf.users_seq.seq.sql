DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'CREATE SEQUENCE users_seq START WITH 3 INCREMENT BY 1 NOCACHE NOCYCLE';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_sequences
     WHERE     sequence_owner = UPPER ('MUBASHER_LSF')
           AND sequence_name = UPPER ('users_seq');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
