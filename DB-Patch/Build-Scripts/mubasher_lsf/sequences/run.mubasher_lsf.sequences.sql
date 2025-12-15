spool log.run.mubasher_lsf.sequences replace

whenever sqlerror exit
set echo off
set define off
set sqlblanklines on

PROMPT Running seq_l01_rollover_count.seq.sql
@@mubasher_lsf.seq_l01_rollover_count.seq.sql
@@mubasher_lsf.users_seq.seq.sql
@@mubasher_lsf.log_seq.seq.sql
@@mubasher_lsf.seq_l14_po_id.seq.sql
@@mubasher_lsf.seq_l32_id.seq.sql
@@mubasher_lsf.seq_m11_id.seq.sql
@@mubasher_lsf.seq_l33_id.pkg.sql
@@mubasher_lsf.seq_m12_id.pkg.sql

spool off
