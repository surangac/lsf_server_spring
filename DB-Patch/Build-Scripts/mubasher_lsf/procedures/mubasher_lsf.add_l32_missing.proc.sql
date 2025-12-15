-- Start of DDL Script for Procedure MUBASHER_LSF.ADD_L32_MISSING
-- Generated 12/4/2025 8:54:34 AM from MUBASHER_LSF@(DESCRIPTION =(ADDRESS_LIST =(ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.14.243)(PORT = 1529)))(CONNECT_DATA =(SERVICE_NAME = ABICQA)))

CREATE OR REPLACE
PROCEDURE add_l32_missing
   ( param2 OUT number)
   IS
BEGIN
  -- Loop through each record in l01_application
  FOR rec IN (SELECT l01_app_id FROM l01_application)
  LOOP
    -- Check if the record exists in l32_appove_agreements
    DECLARE
      v_id NUMBER;
      pkey NUMBER;
    BEGIN
      SELECT l32_id INTO v_id
      FROM l32_appove_agreements
      WHERE l32_l01_app_id = rec.l01_app_id;

      -- If ID exists (greater than 0)
      IF v_id > 0 THEN
        -- Generate new primary key from the sequence
        SELECT seq_l32_id.NEXTVAL INTO pkey FROM DUAL;

        -- Insert new row into l32_appove_agreements
        INSERT INTO l32_appove_agreements (l32_id,
                                           l32_m11_id,
                                           l32_l01_app_id,
                                           l32_agreement_date,
                                           l32_agreement_status,
                                           l32_m11_version)
        VALUES (pkey,
                22,
                rec.l01_app_id,
                SYSDATE,
                '0',
                '0');
      END IF;
    EXCEPTION
      -- If no data found, do nothing (v_id does not exist)
      WHEN NO_DATA_FOUND THEN
        NULL;
    END;
  END LOOP;
END;
/



-- End of DDL Script for Procedure MUBASHER_LSF.ADD_L32_MISSING

