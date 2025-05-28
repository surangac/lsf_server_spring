package com.dfn.lsf.util;

public class DBConstants {
    public static final String SCHEMA = "MUBASHER_LSF";
    /*----------------------Packages-----------------------------------------*/
    public static final String PKG_A03_AUDIT = "a03_audit_pkg";
    public static final String PKG_A04_QUESTIONER_PKG = "a04_questioner_pkg";
    public static final String PKG_L01_APPLICATION = "l01_application_pkg";
    public static final String PKG_L02_APP_STATE = "l02_app_state_pkg";
    public static final String PKG_l03_documents = "l03_documents_pkg";
    public static final String PKG_L04_APPLICATION_DOC = "l04_app_docs_pkg";
    public static final String PKG_L05_COLLATERALS = "l05_collaterals_pkg";
    public static final String PKG_L06_TRADING_ACCOUNT = "l06_trading_acc_pkg";
    public static final String PKG_L07_CAH_ACCOUNT = "l07_cash_account_pkg";
    public static final String PKG_L08_SYMBOL = "l08_symbol_pkg";
    public static final String PKG_L09_TRADING_SYMBOLS = "l09_trading_symbols_pkg";
    public static final String PKG_L35_SYMBOL_MARGINABILITY = "l35_symbol_marginability_pkg";

    public static final String PKG_L11_MARGINABILITY_GROUP = "l11_marginability_group_pkg";
    public static final String PKG_L12_STOCK_CONCENTRATION = "l12_stock_concentration_pkg";
    public static final String PKG_L13_SYMBOL_WISH_LIST = "l13_symbol_wishlist_pkg";
    public static final String PKG_L14_PURCHASE_ORDER = "l14_purchase_order_pkg";
    public static final String PKG_L15_TENOR_PKG = "l15_tenor_pkg";
    public static final String PKG_L16_PO_SYMBOLS = "l16_purchase_order_symbol_pkg";

    public static final String PKG_L20_APP_PORTFOLIO = "l20_initial_app_portfolio_pkg";
    public static final String PKG_L21_APP_COMMENTS = "l21_app_comments_pkg";
    public static final String PKG_L22_PO_INSTALLMENTS = "l22_installments_pkg";
    public static final String PKG_L23_ORDER_PROFIT_LOG = "l23_order_profit_log_pkg";
    public static final String PKG_L24_LIQUIDATION_LOG = "l24_liquidation_log_pkg";
    public static final String PKG_L25_CALLING_ATTEMPT_LOG_PKG = "l25_calling_attempt_log_pkg";
    public static final String PKG_L26_EXTERNAL_REQUEST_LOG = "l26_external_request_log_pkg";
    public static final String PKG_L27_EXTERNAL_COLLATERALS = "l27_external_collaterals_pkg";
    public static final String PKG_L28_DAILY_FTV_LOG_PKG = "l28_daily_ftv_log_pkg";
    public static final String PKG_L29_MARGINE_CALL_LOG_PKG = "l29_margine_call_log_pkg";
    public static final String PKG_L30_CLIENT_RATINGS = "l30_client_ratings_pkg";
    public static final String PKG_L31_SYMBOL_CLASSIFY_LOG = "l31_symbol_classif_log";

    public static final String PKG_M01_SYS_PARAS = "m01_sys_paras_pkg";
    public static final String PKG_M02_APP_STATE_FLOW = "m02_app_state_flow_pkg";
    public static final String PKG_M03_REPORT_SETTINGS_PKG = "m03_report_settings_pkg";
    public static final String PKG_M05_ADMIN_USERS = "m05_admin_users_pkg";
    public static final String PKG_M06_RISK_WAIVER_QUESTIONNAIRE = "m06_riskwavier_qst_config_pkg";
    public static final String PKG_N04_WEB_NOTIFICATION = "n04_web_notifications_pkg";
    public static final String PKG_M10_COMMISSION_STRUCTURE_PKG = "m10_commission_structure_pkg";
    public static final String PKG_M06_RISKWAVIER_QST_CONFIG_PKG = "m06_riskwavier_qst_config_pkg";

    public static final String PKG_N01_NOTIFICATION = "n01_notification_pkg";
    public static final String PKG_N03_NOTIFICATION_MESSAGE_CONFIG = "n03_notification_msg_confi_pkg";
    public static final String PKG_N04_MESSAGE_OUT = "n04_message_out_pkg";
    public static final String PKG_U01_USER_SESSION = "u01_user_session_pkg";
    public static final String PKG_R01_REPORTS = "r01_reports";
    public static final String PKG_M04_REPORTS = "m04_reports_pkg";
    public static final String PKG_M07_MURABAHA_PRODUCTS = "m07_murabaha_products_pkg";
    public static final String PKG_M08_PROFIT_CAL_M_DATA = "m08_profit_cal_m_data_pkg";
    public static final String ML_OMS_PKG = "ml_oms_pkg";

    /*----------------------Procs-----------------------------------------*/
    /*-----------------------Murabah Application Related------------------*/
    public static final String PROC_ADD_UPDATE_APPLICATION = "l01_add_update";
    public static final String PROC_L01_GET_ALL = "l01_get_all";
    public static final String PROC_GET_BY_APPLICATION_ID = "l01_get_by_application_id";
    public static final String PROC_L01_GET_APP_STATE = "l02_get_app_state";
    public static final String PROC_L01_GET_APP_REJECTED_REASON = "l02_get_app_rejected_reason";
    public static final String PROC_GET_BY_CUSTOMER_ID = "l01_get_by_customer_id";
    public static final String PROC_GET_APPS_BY_CUSTOMER_ID = "l01_get_apps_by_customer_id";
    public static final String PROC_GET_NOT_CLOSED_APPS = "l01_get_not_closed_apps";
    public static final String PROC_L01_GET_NOT_GRANTED = "l01_get_notGranted_app";
    public static final String PROC_L01_GET_FILTERED_APPLICATION = "l01_get_filtered_application";
    public static final String PROC_L01_GET_SNAPSHOT = "l01_get_snapshot";
    public static final String PROC_L01_GET_COMMODITY_SNAPSHOT = "l01_get_snapshot_commodity";
    public static final String PROC_L01_GET_HISTORY_APPLICATION = "l01_get_histry_application";
    public static final String PROC_L01_GET_REVERSED_APPLICATION = "l01_get_reversed_application";
    public static final String PROC_L01_REVERSE_APPLICATION = "l01_reverse_application";
    public static final String PROC_L21_ADD_APPLICATION_COMMENT = "l21_add_application_comment";
    public static final String PROC_L21_GET_APPLICATION_COMMENT = "l21_get_application_comment";
    public static final String PROC_L21_GET_APPLICATION_COMMENT_ID = "l21_get_application_comment_id";
    public static final String PROC_M02_APPROVE_APPLICATION = "m02_approve_application";
    public static final String PROC_L01_UPDATE_STOCK_MARG_GRP = "l01_update_stock_marg_grp";
    public static final String PROC_L01_GET_LEVEL_STATUS = "l01_get_level_status";
    public static final String PROC_L01_GET_APPROVED_CUSTOMER = "l01_get_approved_customer";
    public static final String PROC_L01_GET_EXACT_CUSTOMER = "l01_get_exact_customer";
    public static final String PROC_L01_SEARCH_CUSTOMER = "l01_search_customer";
    public static final String PROC_L01_GET_LIMITED_APPROVED_CUSTOMER = "l01_get_limit_approve_customer";
    public static final String PROC_L01_GET_TOTAL_APPROVED_RECORD_SIZE = "l01_get_total_approved_size";
    public static final String PROC_L01_WHITE_LIST_CUSTOMER = "l01_whiteListApplication";
    public static final String PROC_L01_GET_FAIlED_DEPOSITS = "l01_failedDeposits";
    public static final String PROC_L01_UPDATE_CUSTOMER_OTP = "l01_update_customer_otp";
    public static final String PROC_L01_GET_ORD_CNTRCT_DATA = "l01_get_ord_cntrct_data";
    public static final String PROC_L01_GET_APP_STATUS_SUMMARY = "l01_get_app_status_summary";
    public static final String PROC_L01_SET_APPLICATION_ACCOUNT_CLOSE_STATE = "l01_set_app_close_state";
    public static final String PROC_L01_UPDATE_ACTIVITY = "l01_update_activity";
    public static final String PROC_L01_GET_INCOMPLETE_CUSTOMERS = "l01_get_incomplete_customers";
    public static final String PROC_L01_GET_APPS_FOR_ADMIN_REJECT = "l01_get_apps_for_admin_reject";
    public static final String PROC_M04_SETTLEMENT_LIST = "m04_settlement_list";
    public static final String PROC_CSHDTL_FOR_CONCENTRAION_RTP = "getCshDtl_for_concetration_rtp";
    public static final String PROC_CSHDTL_FOR_CONCENTRAION_RTP_TODAY = "getCshDtl_for_rtp_today";
    public static final String PROC_STKDTL_FOR_CONCENTRAION_RTP = "getStkDtl_for_concetration_rtp";
    public static final String PROC_STKDTL_FOR_CONCENTRAION_RTP_TODAY = "getStkDtl_for_rtp_today";
    public static final String PROC_L01_UPDATE_LAST_PROFIT_DATE = "l01_update_last_profit_date";
    public static final String PROC_L01_GET_PHYSICAL_DELIVER_LIST = "l01_get_physical_deliver_list";


    /*-----------------------Murabah Application Related Portfolio------------------*/
    public static final String PROC_L20_ADD_INITIAL_PORTFOLIO = "l20_add_initial_portfolio";
    public static final String PROC_L20_GET_INITIAL_PORTFOLIO = "l20_get_initial_portfolio";


    /*-----------------------User Document Related------------------*/
    public static final String PROC_L04_ADD_UPDATE_USER_DOCS = "l04_add_update_user_docs";
    public static final String PROC_L04_GET_USER_DOCS = "l04_get_user_docs";
    public static final String PROC_L04_GET_USER_DOCS_BY_APPID = "l04_get_user_docs_by_appid";
    public static final String PROC_L04_GET_USER_DOCS_BY_DOC_ID = "l04_get_user_docs_by_doc_id";
    public static final String PROC_L04_REMOVE_USER_DOCS = "l04_remove_user_docs";
    public static final String PROC_L03_ADD = "l03_add";
    public static final String PROC_L03_GET_ALL = "l03_get_all";
    public static final String PROC_L03_CHANGE_STATUS = "l03_change_status";
    public static final String PROC_L03_REMOVE_DOC = "l03_remove_doc";
    public static final String PROC_L04_ADD_CUSTOM_DOC_BY_ADMIN = "l04_add_custom_doc_by_admin";
    public static final String PROC_L01_GET_APP_ADMIN_DOC_UPLOAD = "l01_get_app_admin_doc_upload";
    public static final String PROC_L04_GET_APPS_BY_DOC_ID = "l04_get_apps_by_doc_id";
    public static final String PROC_L04_REMOVE_CUSTOM_DOC_FROM_APP = "l04_remove_custom_doc_from_app";


    /*-----------------------Admin Document Related------------------*/
    public static final String PROC_L19_GET_UADMIN_DOCS_BY_APPLID = "l19_get_admin_docs_by_appid";
    public static final String PROC_L19_ADD_UPDATE_ADMIN_DOCS = "l19_add_update_admin_docs";
    public static final String PROC_L19_REMOVE_ADMIN_DOCS = "l19_remove_admin_docs";

    /*------------------------User Session Related-------------------*/
    public static final String PROC_U01_CREATE_UPDATE_SESSION = "u01_create_update_session";
    public static final String PROC_U01_GET_USER_SESSION = "u01_get_user_session";
    public static final String PROC_U01_UPDATE_SESSION_STATE = "u01_update_session_state";

    /*------------------------Application Status Flow Related-------------------*/
    public static final String PROC_M02_GET_APP_STATE_FLOW = "m02_get_app_state_flow";

    public static final String PROC_ADD_UPDATE_TENOR = "l15_add_update";
    public static final String PROC_DELETE_ALL_TENOR = "l15_delete_all";
    public static final String PROC_GET_ALL_TENOR = "l15_get_all";
    public static final String PROC_GET_TENOR = "l15_get_tenor";
    public static final String PROC_L15_CHANGE_STATUS = "l15_change_status";
    public static final String PROC_L15_REMOVE_TENOR_GROUP = "l15_remove_tenor_group";


    public static final String PROC_L11_ADD_UPDTE = "l11_add_update";
    public static final String PROC_L11_ADD_LIQUIDITY_TYPE = "l11_add_liquidity_type";
    public static final String PROC_GET_ALL_GROUPS_L11 = "l11_get_all_groups";
    public static final String PROC_GET_ALL_GROUPS_L12 = "l12_get_all_groups";
    public static final String PROC_GET_MARGIN_GROUP_BY_ID = "l11_get_group_by_id";
    public static final String PROC_GET_DEFAULT_GROUP_L11 = "l11_get_default_group";
    public static final String PROC_GET_LIQUID_TYPES_IN_GROUP_L11 = "l11_get_liquid_types_in_group";
    public static final String PROC_L11_CHANGE_STATUS = "l11_change_status";
    public static final String PROC_L11_REMOVE = "l11_remove";
    public static final String PROC_GET_LIQUID_TYPES_IN_GROUP_L12 = "l12_get_liquid_types_in_group";
    public static final String PROC_GET_SYMBOL_LIQUIDITY_TYPE = "l01_get_symbol_liqid_type";
    public static final String PROC__l08_GET_SYMBOL_MARGINABILITY_PERC = "l08_get_symbol_margin_perc";
    public static final String PROC_l08_GET_ALL_SYMBOLS = "l08_get_all_symbols";
    public static final String PROC_l08_GET_ALL_SYMBOLS_CLASSF = "l08_get_all_symbols_classf";
    public static final String PROC_l08_GET_ALL_INSTRUMENT_TYPES = "l08_get_all_instrument_types";
    public static final String PROC_l08_GET_SYMBOL_DIS = "l08_get_symbol_dis";
    public static final String PROC_M10_ADD_EDIT = "m10_add_edit";
    public static final String PROC_M10_GET_ALL = "m10_get_all";
    public static final String PROC_M10_DELETE = "m10_delete";
    public static final String PROC_ADD_UPDATE_SYS_PARAS = "m01_add_update";
    public static final String PROC_ADD_GET_SYS_PARAS = "m01_get";
    public static final String PROC_M01_ENABLE_LSF = "m01_enable_lsf";
    public static final String PROC_GET_WISH_LIST_SYMBOLS = "l13_get_wishlist_symbols";
    public static final String PROC_ADD_WISH_LIST_SYMBOLS = "l13_add_to_wish_list";
    public static final String PROC_GET_COLLATERALS = "l05_get";
    public static final String PROC_GET_COLLATERALS_FTV_LIST = "l05_get_ftv_list";
    public static final String PROC_L05_GET_FTV_DETAILED_INFO = "l05_get_ftv_detailed_info";
    public static final String PROC_L05_GET_ML_ACCOUNT_COMMISSION = "l05_get_commission_details";
    public static final String PROC_GET_ADD_UPDATE_COLLATERALS = "l05_add_edit";
    public static final String PROC_L05_UPDATE_INITIAL_COLLATERALS = "l05_update_initial_collateral";
    public static final String PROC_L05_CHANGE_STATUS = "l05_change_status";
    public static final String PROC_ADD_ORDER = "l14_add";
    public static final String PROC_GET_ALL_ORDER = "l14_get_all";
    public static final String PROC_GET_ALL_ORDER_FOR_COMMODITY = "l14_get_all_for_commodity";
    public static final String PROC_GET_ORDER = "l14_get_order";
    public static final String PROC_APPROVE_REJECT_ORDER = "l14_approve_reject";
    public static final String PROC_APPROVE_REJECT_COM_ORDER = "l14_approve_reject_com_po";
    public static final String PROC_UPDATE_ORDER_STATUS = "l14_update_order_status";
    public static final String PROC_UPDATE_CUST_ORD_STATUS = "l14_update_cust_ord_status";
    public static final String PROC_L14_UPDATE_PO_REMINDER = "l14_update_po_reminder";
    public static final String PROC_L14_GET_APPLICATION_REMINDER = "l14_get_application_reminder";
    public static final String PROC_L14_GET_ORD_APRVED_APP = "l14_get_ord_aprved_app";
    public static final String PROC_L01_BLACK_LISTED_APPLICATIONS = "l01_black_listed_applications";
    public static final String PROC_L14_UPDATE_TO_LIQUIDATE_STATE = "l14_update_to_liquidate_state";
    public static final String PROC_L14_GET_APPS_FOR_MANUAL_SETMNT = "l14_get_apps_for_manual_stlmnt";
    public static final String PROC_L14_UPDATE_TO_SETTLE_STATE = "l14_update_to_settle_state";
    public static final String PROC_L14_GET_AVAILABLE_POID = "l14_get_available_poid";
    public static final String PROC_L14_UPDATE_BASKET_STATUS = "l14_update_basket_status";
    public static final String PROC_L14_GET_TOTAL_OUTSTANDING = "l14_get_total_outstanding";
    public static final String PROC_L14_UPDATE_ADMIN_FEE = "l14_update_admin_fee";
    public static final String L14_UPDATE_BY_ADMIN = "l14_update_by_admin";
    public static final String L14_UPDATE_COMDT_PO_EXECUTION = "l14_update_comdt_po_exec";
    public static final String L14_UPDATE_AUTH_ABIC_TO_SELL = "l14_update_auth_abic_to_sell";
    public static final String L14_GET_PO_FOR_SET_AUTH_ABIC_TO_SELL = "l14_get_po_set_abic_to_sell";
    public static final String PROC_L35_GET_SYMBOL_MARGINABILITY_PERCENTAGE = "l35_get_symbol_margin_perc";
    public static final String PROC_L35_ADD_SYMBOL_MARGINABILITY_PERCENTAGE = "l35_add_symbol_margin_perc";
    public static final String PROC_L35_GET_SYMBOL_GROUPS = "l35_get_symbol_groups";
    public static final String PROC_L35_UPDATE_SYMBOL_GROUPS = "l35_update_symbol_groups";
    public static final String PROC_L35_SYMBOL_PERC_BY_GROUP = "l35_get_margin_perc_by_group";

    public static final String PROC_ADD_UPDATE_SYMBOL = "l08_add_update";
    public static final String PROC_ADD_UPDATE_LIQUID_TYPE = "l08_update_liquid_type";
    public static final String PROC_ADD_UPDATE_STOCK_CONCENTRATION_GROUP = "l12_add_update";
    public static final String PROC_ADD_UPDATE_LIQUID_TYPE_STOCK_CONCENTRATION = "l12_add_liquidity_type";
    public static final String PROC_L12_CAHNGE_STATUS = "l12_change_status";
    public static final String PROC_L12_REMOVE = "l12_remove";

    public static final String PROC_L07_ADD_EDIT = "l07_add_edit";
    public static final String PROC_L07_GET_ACCOUNT_IN_APP = "l07_get_account_in_application";
    public static final String PROC_L07_UPDATE_REVALUATION_INFO = "l07_update_revaluation_info";
    public static final String PROC_L07_GET_APP_BY_CASH_ACCOUNT = "l07_get_app_by_cash_acc";
    public static final String PROC_L07_UPDATE_INVESTMENT_ACC = "l07_update_investment_acc";

    public static final String PROC_L06_ADD_EDIT = "l06_add_edit";
    public static final String PROC_L06_GET_ACCOUNT_IN_APP = "l06_get_account_in_application";
    public static final String PROC_L06_GET_TRADING_ACC_BY_CASH_ACC = "l06_get_trd_acc_by_cash_acc";
    public static final String PROC_L06_UPDATE_REVALUATION = "l06_update_revaluation";
    public static final String PROC_L06_UPDATE_EXCHANGE_ACC = "l06_upadte_exchange_acc";
    public static final String PROC_L06_GET_APP_BY_TRADING_ACC = "l06_get_app_by_trading_acc";

    public static final String PROC_L09_ADD_EDIT = "l09_add_edit";
    public static final String PROC_L09_GET_TRADING_SYMBOLS = "l09_get_account_symbols";
    public static final String PROC_L09_UPDATE_SYMBOL_STATE = "l09_update_symbol_status";


    public static final String PROC_L22_ADD_EDIT = "l22_add_edit";
    public static final String PROC_L22_GET_INSTALLMENTS = "l22_get";
    public static final String PROC_L22_IS_FINAL_INSTALLMENT_APP = "l22_is_final_installment_app";
    public static final String PROC_L22_IS_FINAL_INSTALLMENT_PO = "l22_is_final_installment_po";
    public static final String PROC_L22_GENERATE_INSTALLMENT = "l22_generate_installment";
    public static final String PROC_L22_APPROVE_INSTALLMENT = "l22_approve_installment";
    public static final String PROC_L22_GET_CREATED_INSTALLMENTS = "l22_get_created_installments";

    public static final String PROC_L16_ADD_EDIT = "l16_add_edit";
    public static final String PROC_L16_GET_PO_SYMBOLS = "l16_get_po_symbols";

    public static final String PROC_L25_ADD = "l25_add";
    public static final String PROC_L27_ADD = "l27_add";
    public static final String PROC_L27_GET = "l27_get";
    public static final String PROC_L27_UPDATE = "l27_update";
    public static final String PROC_L27_GET_ALL_FOR_APPLICATION = "l27_get_all_for_application";
    public static final String PROC_L28_ADD_UPDATE = "l28_add_update";
    public static final String PROC_L28_GET = "l28_get";
    public static final String PROC_L28_GET_FOR_TODAY = "l28_get_for_today";
    public static final String PROC_L29_ADD = "l29_add";

    public static final String PROC_L30_CLIENT_RATINGS_RATE_LOAN = "l30_rate_loan";
    public static final String PROC_L30_CLIENT_RATINGS_GET_RATINGS = "l30_get_ratings";

    public static final String PROC_M06_RISK_WAIVER_QUESTIONNAIRE_ADD = "m06_add_update";
    public static final String PROC_M06_RISK_WAIVER_QUESTIONNAIRE_GET = "m06_get_all";

    /*------------Notification related---------------*/
    public static final String PROC_N03_ADD_UPDATE = "n03_add_update";
    public static final String PROC_N03_GET_ALL = "n03_get_all";
    public static final String PROC_N03_GET_STATUS_LEVEL = "n03_get_level_status";
    public static final String PROC_N03_GET_CONFIG_NOTIFI_TYPE = "n03_get_config_notifi_type";
    public static final String PROC_N03_GET_MATCHING_CONFIG = "n03_get_matching_config";
    public static final String PROC_N04_ADD_UPDATE = "n04_add_update";
    public static final String PROC_N04_UPDATE_READ_NOTIFICATION = "n04_update_read_notification";
    public static final String PROC_N04_GET_WEB_NOTIFICATION = "n04_get_web_notification";
    public static final String PROC_N01_ADD_NOTIFICATION_HEADER = "n01_add_notification_header";
    public static final String PROC_N02_ADD_NOTIFICATION_BODY = "n02_add_notification_body";
    public static final String PROC_N01_GET_NOTIFICATION_HEADER = "n01_get_notification_header";
    public static final String PROC_N02_GET_NOTIFICATION_BODY = "n02_get_notification_body";
    public static final String PROC_N04_GET_CUSTOM_MESSAGE_HISTORY = "n04_get_custom_message_history";
    public static final String PROC_N04_GET__MESSAGE_HISTORY = "n04_get_message_history";


    /*------------Message related---------------*/
    public static final String PROC_N04_ADD_MESSAGE_OUT = "n04_add_message_out";
    public static final String PROC_N04_UPDATE_STATUS_MESSAGE_OUT = "n04_update_status_message_out";

    /*------------Activity log related---------------*/
    public static final String PROC_A03_ADD_UPDATE = "a03_add_update";

    public static final String PROC_L31_SYMBOL_CLASSIFY_LOG_UPDATE = "l31_update_cassif_status";
    public static final String PROC_L31_SYMBOL_CLASSIFY_LOG_GET = "l31_get_cassif_details";

    /*---------------------User Surveyor Related -----------------------------*/
    public static final String PROC_A04_ADD = "A04_ADD";
    public static final String PROC_A04_GET_ALL = "A04_get_all";


    /*---------------------Application Settlement Releated -----------------------------*/

    public static final String L01_GET_ODRCNTCT_SINGED_APP = "l01_get_odrcntct_singed_app";
    public static final String L23_GET_LSTONE = "l23_get_lstone";
    public static final String L23_ADD = "l23_add";
    public static final String N03_GET_STTLEMENT_MSG_TMPLT = "n03_get_sttlement_msg_tmplt";
    public static final String L24_ADD_LIQUIDATION_ENTRY = "l24_add_liquidation_entry";
    public static final String L24_GET_LIQUIDATION_LOG = "l24_get_liquidation_log";
    public static final String L24_UPDATE_LOG_STATUS = "l24_update_log_status";
    public static final String M02_SET_LIQUIDATE_STATE = "m02_set_liquidate_state";
    public static final String M02_SET_CLOSED_STATE = "m02_set_closed_state";
    public static final String M02_SET_CLOSED_STATE_SYSTEM = "m02_set_closed_state_system";
    public static final String M02_CLOSE_APP = "m02_close_app";
    public static final String M02_GET_FLOW = "m02_get_flow";

    /*---------------------Application Settlement Inquiry Related -----------------------------*/
    public static final String L23_GET_SUMMATION_APPLICATION = "l23_get_summation_application";
    public static final String L23_GET_ALL_FOR_APPLICATION = "l23_get_all_for_application";
    public static final String L23_GET_ENTRY_FOR_DATE = "l23_get_entry_for_date";

    /*---------------------Reporting Related -----------------------------*/
    public static final String M03_GET_REPORT_CONFIG = "m03_get_report_config";
    public static final String R01_REPORT_MARGIN_INFO = "r01_report_margin_info";
    public static final String R01_FINANCE_BROKERAGE_INFO = "r01_finance_brokerage_info";

    /*--------------------B2B Transaction Related-------------*/
    public static final String L26_ADD = "l26_add";
    public static final String L26_UPDATE_STATUS = "l26_update_status";
    public static final String L26_FIND_BY_REF = "l26_find_by_ref";
    /*-------------------Risk Wavier Question ---------------*/
    public static final String M06_GET_ALL = "m06_get_all";

    /*--------------------Notifications -------------*/
    public static final String PROC_M05_ADMIN_USERS_ADD_EDIT = "m05_add_edit";
    public static final String PROC_M05_ADMIN_USERS_GET_ALL = "m05_get_all";
    public static final String ML_TOTAL_COMMISSION = "ml_total_commission";


    /*----------Murabaha Product Related-------------*/
    public static final String M07_GET_PRODUCTS = "m07_get_products";
    public static final String M07_UPDATE_PRODUCTS = "m07_update_product";
    public static final String M07_CHANGE_PRODUCT_STATUS = "m07_change_product_status";
    public static final String M07_GET_PRODUCT = "m07_get_product";

    /*-----------Murabaha Profit Calculation Job Related------------*/
    public static final String M08_ADD_PROFIT_CAL_ENTRY = "m08_add_profit_cal_entry";
    public static final String M08_GET_PROFIT_CAL_LAST_ENTRY = "m08_get_profit_cal_last_entry";
    public static final String L01_GET_PROFIT_CAL_ELIGIBLE_APPLICATIONS = "l01_get_prof_cal_eli_apps";
    public static final String L23_CORRECT_PROFIT_ENTRY = "l23_correct_profit_entry";
    public static final String M11_AGREEMENT_PKG = "m11_agreements_pkg";
    public static final String M11_GET_ACTIVE_FOR_PRODUCT = "m11_get_active_for_product";
    public static final String M11_UPDATE_BY_ADMIN = "m11_update_by_admin";
    public static final String L32_APPROVE_AGREEMENTS_PKG = "l32_approve_agreements_pkg";
    public static final String L32_ADD_UPDATE = "l32_add_update";
    public static final String L32_INITIAL_ADD = "l32_initial_add";
    public static final String APPROVE_AGREEMENT_BY_USER = "l32_approve_agreement_by_user";
    public static final String L32_GET_AGREEMENT_BY_ID = "l32_get_agreement_by_id";
    public static final String M12_COMMODITIES_PKG = "m12_commodities_pkg";
    public static final String M12_ADD_UPDATE = "m12_add_update";
    public static final String M12_GET_ALL_ACTIVE_COMMODITY = "m12_get_all_active_commodity";
    public static final String PROC_M12_DELETE = "m12_delete";
    public static final String L34_PO_COMMODITIES_PKG = "l34_po_commodities_pkg";
    public static final String L34_ADD_EDIT = "l34_add_edit";
    public static final String L34_GET_PO_COMMODITIES = "l34_get_po_commodities";
}
