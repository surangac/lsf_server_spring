-- Start of DDL Script for Package MUBASHER_LSF.L03_DOCUMENTS_PKG
-- Generated 17-Nov-2025 10:52:50 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.l03_documents_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE l03_add (pkey               OUT NUMBER,
                       pl03_doc_id            NUMBER,
                       pl03_doc_name          VARCHAR2,
                       pl03_is_required       NUMBER,
                       pl03_created_by        VARCHAR2,
                       pl03_is_global         NUMBER);

    PROCEDURE l03_get_all (pview OUT refcursor);

    PROCEDURE l03_change_status (pkey                    OUT NUMBER,
                                 pl03_doc_id                 NUMBER,
                                 pl03_lvl1_approved_by       VARCHAR2,
                                 pl03_status                 NUMBER);

    PROCEDURE l03_remove_doc (pkey OUT NUMBER, pl03_doc_id NUMBER);
END;
/

-- Grants for Package
GRANT EXECUTE ON mubasher_lsf.l03_documents_pkg TO mubasher_readonly_role
/
GRANT EXECUTE ON mubasher_lsf.l03_documents_pkg TO mubasher_debug_role
/
GRANT DEBUG ON mubasher_lsf.l03_documents_pkg TO mubasher_debug_role
/
GRANT EXECUTE ON mubasher_lsf.l03_documents_pkg TO mubasher_lsf_role
/
GRANT DEBUG ON mubasher_lsf.l03_documents_pkg TO mubasher_lsf_role
/

CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.l03_documents_pkg
IS

    PROCEDURE l03_add (pkey               OUT NUMBER,
                       pl03_doc_id            NUMBER,
                       pl03_doc_name          VARCHAR2,
                       pl03_is_required       NUMBER,
                       pl03_created_by        VARCHAR2,
                       pl03_is_global         NUMBER)
    IS
        v_rec_count   NUMBER := 0;
    BEGIN
	    
	    SELECT count(l03_doc_id) INTO v_rec_count FROM l03_documents WHERE l03_doc_id = pl03_doc_id;
	    
        IF (v_rec_count = 0)
        THEN
            SELECT NVL (MAX (l03_doc_id), 0) + 1 INTO v_rec_count FROM l03_documents;

            INSERT INTO l03_documents (l03_doc_id,
                                       L03_DOC_NAME,
                                       L03_IS_REQUIRED,
                                       l03_created_by,
                                       l03_created_date,
                                       l03_status,
                                       l03_is_global)
                 VALUES (v_rec_count,
                         pl03_doc_name,
                         pl03_is_required,
                         pl03_created_by,
                         SYSDATE,
                         0,
                         pl03_is_global);
            
            pkey := v_rec_count;
        ELSE
            pkey := pl03_doc_id;

            UPDATE l03_documents
               SET l03_doc_name = pl03_doc_name,
                   l03_is_required = pl03_is_required,
                   l03_status = 0,
                   l03_is_global = pl03_is_global
             WHERE l03_doc_id = pkey;
        END IF;
            
    END;

    PROCEDURE l03_get_all (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR SELECT * FROM l03_documents;
    END;

    PROCEDURE l03_change_status (pkey                    OUT NUMBER,
                                 pl03_doc_id                 NUMBER,
                                 pl03_lvl1_approved_by       VARCHAR2,
                                 pl03_status                 NUMBER)
    IS
    BEGIN
        UPDATE l03_documents
           SET l03_lvl1_approved_by = pl03_lvl1_approved_by,
               l03_status = pl03_status,
               l03_lvl1_approved_date = SYSDATE
         WHERE l03_doc_id = pl03_doc_id;

        pkey := 1;
    END;

    PROCEDURE l03_remove_doc (pkey OUT NUMBER, pl03_doc_id NUMBER)
    IS
    BEGIN
        DELETE FROM l03_documents a
              WHERE a.l03_doc_id = pl03_doc_id;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.L03_DOCUMENTS_PKG

