set define on
column dcol new_value SYSTIMESTAMP noprint
select TO_CHAR (SYSTIMESTAMP, 'YYYYMMDDhh24miss') dcol from dual;
spool log.&SYSTIMESTAMP..run.mubasher_lsf.data replace

whenever sqlerror exit
set echo off
set define off
set sqlblanklines on

PROMPT Running data_fixes.sql
@@mubasher_lsf.data_fixes.data.sql

--PROMPT Running abicml_user.sql
--@@abicml_user.sql

PROMPT Running production_users.sql
@@production_users.sql

PROMPT Running function_groups.sql
@@function_groups.sql

PROMPT Running function_groups_function.sql
@@function_groups_function.sql

--@@l11_create_groups.sql
--@@mp-default_0.sql
--@@mp-default_50.sql
--@@mp-default_75.sql
--@@mp-default_100.sql
--
--@@mp-default_0_vip25.sql
--@@mp-default_50_vip25.sql
--@@mp-default_75_vip25.sql
--@@mp-default_100_vip25.sql
--
--@@mp-vip50_0.sql
--@@mp-vip50_25.sql
--@@mp-vip50_75.sql
--@@mp-vip50_100.sql

spool off
