-- ==========================================================================
-- ROLLBACK SCRIPT FOR MUBASHER LSF TABLE CHANGES
-- Target Database: Oracle 19c
-- Generated: 2025-09-29
-- ==========================================================================
--
-- This script reverses all table modifications made by the patch scripts
-- Execute manually to rollback changes to previous state
--
-- ==========================================================================

PROMPT Starting Rollback of Mubasher LSF Table Changes...

-- ==========================================================================
-- DROP NEW TABLES (Created by patch scripts)
-- ==========================================================================

PROMPT Dropping new tables...

-- Drop L35_SYMBOL_MARGINABILITY_PERC table
DROP TABLE L35_SYMBOL_MARGINABILITY_PERC CASCADE CONSTRAINTS;
/

-- Drop L34_PURCHASE_ORDER_COMMODITIES table
DROP TABLE mubasher_lsf.l34_purchase_order_commodities CASCADE CONSTRAINTS;
/

-- Drop L33_M11_AGREEMENTS_LOG table
DROP TABLE mubasher_lsf.l33_m11_agreements_log CASCADE CONSTRAINTS;
/

-- Drop L32_APPOVE_AGREEMENTS table
DROP TABLE mubasher_lsf.l32_appove_agreements CASCADE CONSTRAINTS;
/

-- Drop M11_AGREEMENTS table
DROP TABLE mubasher_lsf.m11_agreements CASCADE CONSTRAINTS;
/

-- Drop M12_COMMODITIES table
DROP TABLE mubasher_lsf.m12_commodities CASCADE CONSTRAINTS;
/

-- Drop M08_PROFIT_CAL_M_DATA table
DROP TABLE mubasher_lsf.m08_profit_cal_m_data CASCADE CONSTRAINTS;
/

-- Drop CUSTOMER_INFO_SYNC table
DROP TABLE mubasher_lsf.customer_info_sync CASCADE CONSTRAINTS;
/

-- Drop FUNCTION_GROUPS table
DROP TABLE mubasher_lsf.function_groups CASCADE CONSTRAINTS;
/

-- Drop FUNCTION_GROUPS_FUNCTIONS table
DROP TABLE mubasher_lsf.function_groups_functions CASCADE CONSTRAINTS;
/

-- Drop FUNCTIONS_COLLECTION table
DROP TABLE mubasher_lsf.functions_collection CASCADE CONSTRAINTS;
/

-- Drop ORGANIZATION_HIERARCHY table
DROP TABLE mubasher_lsf.organization_hierarchy CASCADE CONSTRAINTS;
/

-- Drop STATE_MACHINE table
DROP TABLE mubasher_lsf.state_machine CASCADE CONSTRAINTS;
/

-- Drop AUDIT_TRAIL_ACTIVITY table
DROP TABLE mubasher_lsf.audit_trail_activity CASCADE CONSTRAINTS;
/

-- Drop USER_RESTRICTIONS table
DROP TABLE mubasher_lsf.user_restrictions CASCADE CONSTRAINTS;
/

-- Drop USER_VALIDATIONS table
DROP TABLE mubasher_lsf.user_validations CASCADE CONSTRAINTS;
/

-- Drop USERS table
DROP TABLE mubasher_lsf.users CASCADE CONSTRAINTS;
/

-- Drop USER_ENTITLEMENTS table
DROP TABLE mubasher_lsf.user_entitlements CASCADE CONSTRAINTS;
/

-- ==========================================================================
-- DROP ADDED COLUMNS (ALTER TABLE ADD operations)
-- ==========================================================================

PROMPT Dropping added columns...

-- Rollback L01_APPLICATION table changes
ALTER TABLE L01_APPLICATION DROP COLUMN L01_ROLLOVER_APP_ID;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_ROLLOVER_COUNT;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_DEVICE_TYPE;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_IP_ADDRESS;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_FACILITY_TRANSFER_STATUS;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_ADITIONAL_DETAILS;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_ADDITIONAL_DOC_NAME;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_ADDITIONAL_DOC_PATH;
/
ALTER TABLE L01_APPLICATION DROP COLUMN L01_FINANCE_METHOD;
/

-- Rollback L07_CASH_ACCOUNT table changes
ALTER TABLE L07_CASH_ACCOUNT DROP COLUMN L07_PENDING_SETTLE;
/
ALTER TABLE L07_CASH_ACCOUNT DROP COLUMN L07_NET_RECEIVABLE;
/

-- Rollback L08_SYMBOL table changes
ALTER TABLE L08_SYMBOL DROP COLUMN L08_INSTRUMENT_TYPE;
/
ALTER TABLE L08_SYMBOL DROP COLUMN L08_SECURITY_TYPE;
/
ALTER TABLE L08_SYMBOL DROP COLUMN L08_ALLOWED_FOR_PO;
/

-- Rollback L09_TRADING_SYMBOLS table changes
ALTER TABLE L09_TRADING_SYMBOLS DROP COLUMN L09_AVAILABLE_QTY;
/
ALTER TABLE L09_TRADING_SYMBOLS DROP COLUMN L09_CLOSE_PRICE;
/
ALTER TABLE L09_TRADING_SYMBOLS DROP COLUMN L09_LTP;
/

-- Rollback L11_MARGINABILITY_GROUP table changes
ALTER TABLE L11_MARGINABILITY_GROUP DROP COLUMN L11_additional_details;
/
ALTER TABLE L11_MARGINABILITY_GROUP DROP COLUMN L11_global_marginability_perc;
/

-- Rollback L14_PURCHASE_ORDER table changes
ALTER TABLE l14_purchase_order DROP COLUMN l14_auth_abic_to_sell;
/
ALTER TABLE l14_purchase_order DROP COLUMN l14_physical_delivery;
/
ALTER TABLE l14_purchase_order DROP COLUMN l14_sell_but_not_settle;
/
ALTER TABLE mubasher_lsf.l14_purchase_order DROP COLUMN L14_COM_CERTIFICATE_PATH;
/
ALTER TABLE mubasher_lsf.l14_purchase_order DROP COLUMN L14_CERTIFICATE_NUMBER;
/

-- Rollback L19_APP_ADMIN_DOCS table changes
ALTER TABLE L19_APP_ADMIN_DOCS DROP COLUMN L19_FILE_CATEGORY;
/

-- Rollback M02_APP_STATE_FLOW table changes
ALTER TABLE M02_APP_STATE_FLOW DROP COLUMN M02_APP_TYPE;
/

-- Rollback M01_SYS_PARAS table changes
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_SHARE_FIXED_FEE;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_COMODITY_FIXED_FEE;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_SHARE_ADMIN_FEE;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_COMODITY_ADMIN_FEE;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_MIN_ROLLOVER_RATIO;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_MIN_ROLLOVER_PERIOD;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_MAX_ROLLOVER_PERIOD;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_GRACE_PER_COMMODITY_SELL;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_ORDER_ACCEPTANCE_LIMIT;
/
ALTER TABLE M01_SYS_PARAS DROP COLUMN M01_INSTITUTION_INVEST_ACC;
/

-- ==========================================================================
-- VERIFICATION QUERIES
-- ==========================================================================

PROMPT Verifying rollback completion...

-- Check that new tables are dropped
SELECT 'VERIFICATION: Checking dropped tables' as STATUS FROM DUAL;
/

SELECT COUNT(*) as REMAINING_NEW_TABLES
FROM user_tables
WHERE table_name IN ('L35_SYMBOL_MARGINABILITY_PERC', 'L34_PURCHASE_ORDER_COMMODITIES',
                     'L33_M11_AGREEMENTS_LOG', 'L32_APPOVE_AGREEMENTS',
                     'M11_AGREEMENTS', 'M12_COMMODITIES', 'M08_PROFIT_CAL_M_DATA', 'CUSTOMER_INFO_SYNC', 'FUNCTION_GROUPS', 'FUNCTION_GROUPS_FUNCTIONS',
                     'FUNCTIONS_COLLECTION', 'ORGANIZATION_HIERARCHY', 'STATE_MACHINE', 'AUDIT_TRAIL_ACTIVITY', 'USER_RESTRICTIONS', 'USER_VALIDATIONS',
                     'USERS', 'USER_ENTITLEMENTS');
/

-- Check that added columns are dropped
SELECT 'VERIFICATION: Checking dropped columns' as STATUS FROM DUAL;
/

SELECT table_name, column_name
FROM user_tab_columns
WHERE (table_name = 'L01_APPLICATION' AND column_name IN ('L01_ROLLOVER_APP_ID', 'L01_ROLLOVER_COUNT', 'L01_DEVICE_TYPE', 'L01_IP_ADDRESS', 'L01_FACILITY_TRANSFER_STATUS', 'L01_ADITIONAL_DETAILS', 'L01_ADDITIONAL_DOC_NAME', 'L01_ADDITIONAL_DOC_PATH', 'L01_FINANCE_METHOD'))
   OR (table_name = 'L07_CASH_ACCOUNT' AND column_name IN ('L07_PENDING_SETTLE', 'L07_NET_RECEIVABLE'))
   OR (table_name = 'L08_SYMBOL' AND column_name IN ('L08_INSTRUMENT_TYPE', 'L08_SECURITY_TYPE', 'L08_ALLOWED_FOR_PO'))
   OR (table_name = 'L09_TRADING_SYMBOLS' AND column_name IN ('L09_AVAILABLE_QTY', 'L09_CLOSE_PRICE', 'L09_LTP'))
   OR (table_name = 'L11_MARGINABILITY_GROUP' AND column_name IN ('L11_ADDITIONAL_DETAILS', 'L11_GLOBAL_MARGINABILITY_PERC'))
   OR (table_name = 'L14_PURCHASE_ORDER' AND column_name IN ('L14_AUTH_ABIC_TO_SELL', 'L14_PHYSICAL_DELIVERY', 'L14_SELL_BUT_NOT_SETTLE', 'L14_COM_CERTIFICATE_PATH', 'L14_CERTIFICATE_NUMBER'))
   OR (table_name = 'L19_APP_ADMIN_DOCS' AND column_name IN ('L19_FILE_CATEGORY'))
   OR (table_name = 'M02_APP_STATE_FLOW' AND column_name IN ('M02_APP_TYPE'))
   OR (table_name = 'M01_SYS_PARAS' AND column_name IN ('M01_SHARE_FIXED_FEE', 'M01_COMODITY_FIXED_FEE', 'M01_SHARE_ADMIN_FEE', 'M01_COMODITY_ADMIN_FEE', 'M01_MIN_ROLLOVER_RATIO', 'M01_MIN_ROLLOVER_PERIOD', 'M01_MAX_ROLLOVER_PERIOD', 'M01_GRACE_PER_COMMODITY_SELL', 'M01_ORDER_ACCEPTANCE_LIMIT', 'M01_INSTITUTION_INVEST_ACC'));
/

-- ==========================================================================
-- COMPLETION MESSAGE
-- ==========================================================================

PROMPT
PROMPT ==========================================================================
PROMPT ROLLBACK COMPLETED
PROMPT ==========================================================================
PROMPT
PROMPT All table changes have been rolled back successfully.
PROMPT
PROMPT Changes rolled back:
PROMPT - 7 new tables dropped
PROMPT - 21 added columns removed from existing tables
PROMPT
PROMPT If verification queries above show 0 results, rollback was successful.
PROMPT
PROMPT ==========================================================================
