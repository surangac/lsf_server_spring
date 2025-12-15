spool log.run.mubasher_lsf.tables replace

whenever sqlerror exit
set echo off
set define off
set sqlblanklines on


PROMPT Running l11_marginability_group.tab.sql
@@mubasher_lsf.l11_marginability_group.tab.sql

PROMPT Running l01_application.tab.sql
@@mubasher_lsf.l01_application.tab.sql

PROMPT Running l07_cash_account.tab.sql
@@mubasher_lsf.l07_cash_account.tab.sql

PROMPT Running l08_symbol.tab.sql
@@mubasher_lsf.l08_symbol.tab.sql

PROMPT Running l09_trading_symbols.tab.sql
@@mubasher_lsf.l09_trading_symbols.tab.sql

PROMPT Running l14_purchase_order.tab..sql
@@mubasher_lsf.l14_purchase_order.tab.sql

PROMPT Running l19_app_admin_docs.tab.sql
@@mubasher_lsf.l19_app_admin_docs.tab.sql

PROMPT Running m01_sys_paras.tab.sql
@@mubasher_lsf.m01_sys_paras.tab.sql

PROMPT Running m11_agreements.tab.sql
@@mubasher_lsf.m11_agreements.tab.sql

PROMPT Running l32_appove_agreements.tab.sql
@@mubasher_lsf.l32_appove_agreements.tab.sql

PROMPT Running l33_m11_agreements_log.tab.sql
@@mubasher_lsf.l33_m11_agreements_log.tab.sql

PROMPT Running m12_commodities.tab.sql
@@mubasher_lsf.m12_commodities.tab.sql

PROMPT Running l34_purchase_order_commodities.tab.sql
@@mubasher_lsf.l34_purchase_order_commodities.tab.sql

PROMPT Running l02_app_state.tab.sql
@@mubasher_lsf.l02_app_state.tab.sql

PROMPT Running customer_info_sync.tab.sql
@@mubasher_lsf.customer_info_sync.tab.sql

PROMPT Running function_groups.tab.sql
@@mubasher_lsf.function_groups.tab.sql

PROMPT Running function_groups_functions.tab.sql
@@mubasher_lsf.function_groups_functions.tab.sql

PROMPT Running functions_collection.tab.sql
@@mubasher_lsf.functions_collection.tab.sql

PROMPT Running organization_hierarchy.tab.sql
@@mubasher_lsf.organization_hierarchy.tab.sql

PROMPT Running state_machine.tab.sql
@@mubasher_lsf.state_machine.tab.sql

PROMPT Running audit_trail_activity.tab.sql
@@mubasher_lsf.audit_trail_activity.tab.sql

PROMPT Running user_restrictions.tab.sql
@@mubasher_lsf.user_restrictions.tab.sql

PROMPT Running user_validations.tab.sql
@@mubasher_lsf.user_validations.tab.sql

PROMPT Running users.tab.sql
@@mubasher_lsf.users.tab.sql

PROMPT Running user_entitlements.tab.sql
@@mubasher_lsf.user_entitlements.tab.sql

PROMPT Running l22_installments.tab.sql
@@mubasher_lsf.l22_installments.tab.sql

PROMPT Running m07_murabaha_products.tab.sql
@@mubasher_lsf.m07_murabaha_products.tab.sql

PROMPT Running n03_notification_msg_config.tab.sql
@@mubasher_lsf.n03_notification_msg_config.tab.sql

PROMPT Running l35_symbol_marginability_perc.tab.sql
@@mubasher_lsf.l35_symbol_marginability_perc.tab.sql

PROMPT Running m02_app_state_flow.tab.sql
@@mubasher_lsf.m02_app_state_flow.tab.sql

spool off
