SPOOL log.run.mubasher_lsf.procedures REPLACE

WHENEVER SQLERROR EXIT
SET ECHO OFF
SET DEFINE OFF
SET SQLBLANKLINES ON


PROMPT Running add_l32_missing.proc.sql
@@mubasher_lsf.add_l32_missing.proc.sql

SPOOL OFF
