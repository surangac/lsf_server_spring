SPOOL log.run.mubasher_lsf.triggers REPLACE

WHENEVER SQLERROR EXIT
SET ECHO OFF
SET DEFINE OFF
SET SQLBLANKLINES ON

@@mubasher_lsf.users_bir_trg.trig.sql

SPOOL OFF
