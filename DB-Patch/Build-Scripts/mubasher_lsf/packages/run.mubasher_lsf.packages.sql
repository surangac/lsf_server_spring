spool log.run.mubasher_lsf.packages replace

whenever sqlerror exit
set echo off
set define off
set sqlblanklines on

PROMPT Running l01_application_pkg.pkg.sql
@@mubasher_lsf.l01_application_pkg.pkg.sql
@@mubasher_lsf.l03_documents_pkg.pkg.sql
@@mubasher_lsf.l05_collaterals_pkg.pkg.sql
@@mubasher_lsf.l06_trading_acc_pkg.pkg.sql
@@mubasher_lsf.l07_cash_account_pkg.pkg.sql
@@mubasher_lsf.l08_symbol_pkg.pkg.sql
@@mubasher_lsf.l09_trading_symbols_pkg.pkg.sql
@@mubasher_lsf.l11_marginability_group_pkg.pkg.sql
@@mubasher_lsf.l13_symbol_wishlist_pkg.pkg.sql
@@mubasher_lsf.l14_purchase_order_pkg.pkg.sql
@@mubasher_lsf.l15_tenor_pkg.pkg.sql
@@mubasher_lsf.l16_purchase_order_symbol_pkg.pkg.sql
@@mubasher_lsf.l20_initial_app_portfolio_pkg.pkg.sql
@@mubasher_lsf.l23_order_profit_log_pkg.pkg.sql
@@mubasher_lsf.l28_daily_ftv_log_pkg.pkg.sql
@@mubasher_lsf.l32_approve_agreements_pkg.pkg.sql
@@mubasher_lsf.l33_m11_agreements_log_pkg.pkg.sql
@@mubasher_lsf.l34_po_commodities_pkg.pkg.sql
@@mubasher_lsf.l35_symbol_marginability_pkg.pkg.sql
@@mubasher_lsf.m01_sys_paras_pkg.pkg.sql
@@mubasher_lsf.m02_app_state_flow_pkg.pkg.sql
@@mubasher_lsf.m04_reports_pkg.pkg.sql
@@mubasher_lsf.m07_murabaha_products_pkg.pkg.sql
@@mubasher_lsf.m08_profit_cal_m_data_pkg.pkg.sql
@@mubasher_lsf.m11_agreements_pkg.pkg.sql
@@mubasher_lsf.m12_commodities_pkg.pkg.sql
@@mubasher_lsf.r01_reports_pkg.pkg.sql
@@mubasher_lsf.n04_message_out_pkg.pkg.sql






spool off
