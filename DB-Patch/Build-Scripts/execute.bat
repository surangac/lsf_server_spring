cd mubasher_lsf
sqlplus mubasher_lsf/password@%1 @master.sql

cd ..\
sqlplus sys/%2@%1 as sysdba @run.sql

