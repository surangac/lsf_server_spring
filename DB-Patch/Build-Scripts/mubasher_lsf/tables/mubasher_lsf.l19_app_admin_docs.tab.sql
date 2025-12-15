DECLARE
    l_count   NUMBER := 0;
    l_ddl     VARCHAR2 (1000)
        := 'ALTER TABLE L19_APP_ADMIN_DOCS ADD(L19_FILE_CATEGORY VARCHAR(100))';
BEGIN
    SELECT COUNT (*)
      INTO l_count
      FROM all_tab_columns
     WHERE     owner = UPPER ('MUBASHER_LSF')
           AND table_name = UPPER ('L19_APP_ADMIN_DOCS')
           AND column_name = UPPER ('L19_FILE_CATEGORY');

    IF l_count = 0
    THEN
        EXECUTE IMMEDIATE l_ddl;
    END IF;
END;
/
