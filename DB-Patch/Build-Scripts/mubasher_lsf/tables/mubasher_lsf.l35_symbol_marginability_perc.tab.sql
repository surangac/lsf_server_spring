DECLARE
  v_count NUMBER;
BEGIN
  SELECT COUNT(*)
  INTO v_count
  FROM user_tables
  WHERE table_name = 'L35_SYMBOL_MARGINABILITY_PERC';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      CREATE TABLE l35_symbol_marginability_perc
      (
          l35_l08_symbol_code            VARCHAR2(20 BYTE),
          l35_l08_exchange               VARCHAR2(20 BYTE),
          l35_l11_marginability_grp_id   NUMBER(18,0),
          l35_marginability_percentage   NUMBER(3,0),
          l35_is_marginable              NUMBER(2,0)
      )';
  END IF;
END;
/


DECLARE
  v_count NUMBER;
BEGIN
  SELECT COUNT(*)
  INTO v_count
  FROM user_constraints
  WHERE table_name = 'L35_SYMBOL_MARGINABILITY_PERC'
    AND constraint_name = 'L35_SYMBOL_MARGIN_GRP_PK';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      ALTER TABLE l35_symbol_marginability_perc
      ADD CONSTRAINT l35_symbol_margin_grp_pk
      PRIMARY KEY (l35_l08_symbol_code, l35_l08_exchange, l35_l11_marginability_grp_id)';
  END IF;
END;
/

DECLARE
  v_count NUMBER;
BEGIN
  SELECT COUNT(*)
  INTO v_count
  FROM user_constraints
  WHERE table_name = 'L11_MARGINABILITY_GROUP'
    AND constraint_name = 'L11_MARGINABILITY_GROUP_PK'
    AND constraint_type = 'P';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      ALTER TABLE l11_marginability_group
      ADD CONSTRAINT l11_marginability_group_pk
      PRIMARY KEY (l11_marginability_grp_id)';
  END IF;
END;
/


DECLARE
  v_count NUMBER;
BEGIN
  SELECT COUNT(*)
  INTO v_count
  FROM user_constraints
  WHERE table_name = 'L35_SYMBOL_MARGINABILITY_PERC'
    AND constraint_name = 'L11_MAR_L11_MAR_GRP_ID_FK';

  IF v_count = 0 THEN
    EXECUTE IMMEDIATE '
      ALTER TABLE l35_symbol_marginability_perc
      ADD CONSTRAINT l11_mar_l11_mar_grp_id_fk
      FOREIGN KEY (l35_l11_marginability_grp_id)
      REFERENCES l11_marginability_group (l11_marginability_grp_id)';
  END IF;
END;
/
