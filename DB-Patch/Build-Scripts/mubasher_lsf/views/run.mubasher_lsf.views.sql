spool log.run.mubasher_lsf.views replace

whenever sqlerror exit
set echo off
set define off
set sqlblanklines on

PROMPT Running vw_l08_symbol_base.view.sql
@@mubasher_lsf.vw_l08_symbol_base.view.sql
@@mubasher_lsf.vw_l14_profit.view.sql
@@mubasher_lsf.vw_l14_purchase_order.view.sql

spool off
