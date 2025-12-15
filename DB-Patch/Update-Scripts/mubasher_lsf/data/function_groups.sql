MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'adminGroup' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('adminGroup', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Sales RM Approval' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Sales RM Approval', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Sales RM Manager' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Sales RM Manager', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Risk Approval' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Risk Approval', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Risk Review' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Risk Review', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Operation Team' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Operation Team', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'NAFITH Check' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('NAFITH Check', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Management Team' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Management Team', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Brokerage Team' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Brokerage Team', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Reports Only' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Reports Only', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Finance Team' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Finance Team', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

MERGE INTO MUBASHER_LSF.function_groups fg
USING (SELECT 'Sales RM Officer' AS group_id FROM dual) src
ON (fg.group_id = src.group_id)
WHEN NOT MATCHED THEN
  INSERT (group_id, exp_date)
  VALUES ('Sales RM Officer', TO_DATE('2200-09-11 11:27:10', 'YYYY-MM-DD HH24:MI:SS'))
/

