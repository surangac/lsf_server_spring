-- Start of DDL Script for Package MUBASHER_LSF.M12_COMMODITIES_PKG
-- Generated 17-Nov-2025 10:52:57 from MUBASHER_LSF@Mubasher_UAT

CREATE OR REPLACE 
PACKAGE              mubasher_lsf.m12_commodities_pkg
IS
    TYPE refcursor IS REF CURSOR;

    PROCEDURE m12_add_update (pkey                      OUT NUMBER,
                              pm12_commodity_code    IN     VARCHAR2,
                              pm12_commodity_name    IN     VARCHAR2,
                              pm12_exchange          IN     VARCHAR2,
                              pm12_broker            IN     VARCHAR2,
                              pm12_description       IN     VARCHAR2,
                              pm12_unit_of_measure   IN     VARCHAR2,
                              pm12_price             IN     NUMBER,
                              pm12_status            IN     NUMBER,
    						  pm12_allowd_for_po	 IN 	NUMBER);

    PROCEDURE m12_get_all_active_commodity (pview OUT refcursor);

    PROCEDURE m12_delete (pkey OUT NUMBER, pm12_id NUMBER);
END;
/


CREATE OR REPLACE 
PACKAGE BODY              mubasher_lsf.m12_commodities_pkg
IS
    PROCEDURE m12_add_update (pkey                      OUT NUMBER,
                              pm12_commodity_code    IN     VARCHAR2,
                              pm12_commodity_name    IN     VARCHAR2,
                              pm12_exchange          IN     VARCHAR2,
                              pm12_broker            IN     VARCHAR2,
                              pm12_description       IN     VARCHAR2,
                              pm12_unit_of_measure   IN     VARCHAR2,
                              pm12_price             IN     NUMBER,
                              pm12_status            IN     NUMBER,
                              pm12_allowd_for_po	 IN 	NUMBER)
    IS
        icount    NUMBER (2);
        im12_id   NUMBER (18);
    BEGIN
        SELECT COUNT (m12_commodity_code)
          INTO icount
          FROM m12_commodities
         WHERE     m12_commodity_code = pm12_commodity_code
               AND m12_exchange = pm12_exchange;

        IF (icount > 0)
        THEN
            SELECT m12_id
              INTO im12_id
              FROM m12_commodities
             WHERE     m12_commodity_code = pm12_commodity_code
                   AND m12_exchange = pm12_exchange;

            UPDATE m12_commodities
               SET m12_commodity_code = pm12_commodity_code,
                   m12_commodity_name = pm12_commodity_name,
                   m12_exchange = pm12_exchange,
                   m12_broker = pm12_broker,
                   m12_description = pm12_description,
                   m12_unit_of_measure = pm12_unit_of_measure,
                   m12_price = pm12_price,
                   m12_status = pm12_status,
                   M12_ALLOWED_FOR_PO = pm12_allowd_for_po
             WHERE m12_id = im12_id;

            pkey := im12_id;
        ELSE
            SELECT seq_m12_id.NEXTVAL INTO pkey FROM DUAL;

            INSERT INTO m12_commodities (m12_id,
                                         m12_commodity_code,
                                         m12_commodity_name,
                                         m12_exchange,
                                         m12_broker,
                                         m12_description,
                                         m12_unit_of_measure,
                                         m12_price,
                                         m12_status,
                                         M12_ALLOWED_FOR_PO)
            VALUES (pkey,
                    pm12_commodity_code,
                    pm12_commodity_name,
                    pm12_exchange,
                    pm12_broker,
                    pm12_description,
                    pm12_unit_of_measure,
                    pm12_price,
                    pm12_status,
            		pm12_allowd_for_po);
        END IF;
    END;

    PROCEDURE m12_get_all_active_commodity (pview OUT refcursor)
    IS
    BEGIN
        OPEN pview FOR
            SELECT *
              FROM m12_commodities m12
             WHERE m12.m12_status = '1';
    END;

    PROCEDURE m12_delete (pkey OUT NUMBER, pm12_id NUMBER)
    IS
    BEGIN
        DELETE FROM m12_commodities
         WHERE m12_id = pm12_id;

        pkey := 1;
    END;
END;
/


-- End of DDL Script for Package MUBASHER_LSF.M12_COMMODITIES_PKG

