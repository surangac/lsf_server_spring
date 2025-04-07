package com.dfn.lsf.util;

/**
 * Constants for LSF application
 * This replaces the original LsfConstants class
 */
public class LsfConstants {
    
    /*------------Admin-Client Message Types ---------------------------*/
    public static final int MESSAGE_TYPE_EXECUTE_CORE_PROCESS = 1;
    public static final int MESSAGE_TYPE_VALIDATE_BANK_ACC = 2;
    public static final int MESSAGE_TYPE_MASTER_DATA_PROCESS = 3;
    public static final int MESSAGE_TYPE_PATH_VALIDATE_PROCESS = 4;
    public static final int MESSAGE_TYPE_REPORT_GENERATION_PROCESSS = 5;
    public static final int MESSAGE_TYPE_EXCHANGE_SYMBOL_PROCESS = 6;
    public static final int MESSAGE_TYPE_LOAN_PERSIST_PROCESS = 7;
    public static final int MESSAGE_TYPE_APPLICATION_LIST_PROCESS = 8;
    public static final int MESSAGE_TYPE_CREDIT_PROPOSAL_PROCESS = 9;
    public static final int MESSAGE_TYPE_AUTHORIZATION_PROCESS = 10;
    public static final int MESSAGE_TYPE_NOTIFICATION_PROCESSOR = 11;
    public static final int MESSAGE_TYPE_CUSTOMER_SEARCH_PROCESSOR = 12;
    public static final int MESSAGE_TYPE_SETTLEMENT_INQUIRY_PROCESS = 13;
    public static final int MESSAGE_TYPE_REPORT_GENERATION_PROCESS = 14;
    public static final int MESSAGE_TYPE_SETTLEMENT_PROCESS = 15;
    public static final int MESSAGE_TYPE_COMMON_INQUIRY_PROCESS = 16;
    public static final int MESSAGE_TYPE_DOCUMENT_ADMINISTRATION_PROCESS = 17;
    public static final int MESSAGE_TYPE_APPLICATION_DOCUMENT_PROCESS = 18;
    public static final int MESSAGE_TYPE_APPLICATION_COLLATERAL_PROCESS = 19;
    public static final int MESSAGE_TYPE_CUSTOMER_INQUIRY_PROCESS = 20;
    public static final int MESSAGE_TYPE_REPORTING_PROCESS = 21;
    public static final int MESSAGE_TYPE_OTP_PROCESS = 22;
    public static final int MESSAGE_TYPE_AUDIT_INQUIRY = 23;
    public static final int MESSAGE_TYPE_PENDING_ACTIVITY_INQUIRY = 24;
    public static final int MESSAGE_TYPE_PENDING_ACTIVITY_ADMIN = 25;
    public static final int MESSAGE_TYPE_ADMIN_COMMON_REJECT = 26;
    public static final int MESSAGE_TYPE_PROFIT_CALCULATION_INQUIRY = 27;
    
    /*---Application List Related--*/
    public static final String GET_MURABAH_APPLICATION_LIST = "getMurabahApplicationList";
    public static final String GET_MURABAH_APPLICATION_COUNTER = "getMurabahApplicationCounter";
    public static final String GET_APPLICATION_USERNAME_LIST = "getApplicationUsernameList";
    public static final String GET_APPLICATION_HISTORY_DETAILS = "applicationHistoryDetails";
    public static final String GET_APPLICATION_HISTORY = "applicationHistory";
    public static final String REVERSE_APPLICATION = "reverseApplication";
    public static final String REPLY_TO_REVERSED = "replyToReversed";
    public static final String GET_APPLICATIONS_FTV = "applicationsFTV";
    public static final String GET_MURABAH_APPLICATION_USER_INFO = "getMurabahApplicationUserInfo";
    public static final String GET_SAVED_ANSWER_FOR_USER = "getSavedAnswerForUser";
    
    /*----Common Inquiry Related-----*/
    public static final String REQ_DETAILED_FTV_LIST = "reqDetailedFTVList";
    public static final String REQ_APPROVED_PURCHASE_ORDERS = "reqApprovedPurchaseOrders";
    public static final String REQ_GET_BLACKLISTED_APPLICATION = "reqGetBlackListedApplication";
    public static final String REQ_CONVERT_NUMBER_TO_STRING = "convertNumberToString";
    public static final String REQ_CONCENTRATION_RPT_DATA = "reqStockconcentrationrpt";
    public static final String REQ_MURABAHA_PRODUCTS = "reqMurabahaProducts";
    public static final String UPDATE_MURABAHA_PRODUCTS = "updateMurabahaProduct";
    public static final String CHANGE_MURABAHA_PRODUCTS_STATUS = "changeProductStatus";
    public static final String REQ_GET_MURABAHA_PRODUCT = "getMurabahaProduct";
    public static final String GET_PHYSICAL_DELIVER_LIST = "getPhysicalDeliverList";
    public static final String CHANGE_PO_STATUS = "changePOStatus";
    
    /*---Admin Master Data Related--*/
    public static final String REQ_APP_FLOW = "reqAppFlow";
    public static final String REQ_LIQUID_TYPS = "reqLiquidTyps";
    public static final String REQ_DOCUMENT_MASTER_LIST = "reqDocumentMasterList";
    
    /*---Authentication Related--*/
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String VALIDATE_SESSION = "validateSession";
    
    /*---Notification Related--*/
    public static final String SENT_NOTIFICATION_FOR_A_CUSTOMER = "sentNotificationForACustomer";
    public static final String SENT_NOTIFICATION_FOR_GROUP = "sentNotificationForGroup";
    public static final String GET_CUSTOM_MESSAGE_HISTORY = "getCustomMessageHistory";
    public static final String SENT_WEB_NOTIFICATION = "sentWebNotification";
    public static final String REQ_CLIENT_WEB_NOTIFICATIONS = "reqWebNotifications";
    public static final String UPDATE_CLIENT_READ_NOTIFICATIONS = "updateReadNotification";
    public static final String REQ_MSG_CONFIGURATION = "reqMsgConfiguration";
    public static final String REQ_MSG_CONFIGURATION_LIST = "reqMsgConfigurationList";
    public static final String GET_NOTIFICATION_HISTORY = "getNotificationHistory";
    
    /*---Core Operations Related--*/
    public static final String CASH_TRANSFER = "cashTransfer";
    public static final String SHARE_TRANSFER = "shareTransfer";
    public static final String GET_PORTFOLIO_DETAILS = "getPortfolioDetails";
    public static final String CREATE_PURCHASE_ORDER = "createPurchaseOrder";
    public static final String RUN_REVALUE_PROCESS = "runRevalueProcess";
    public static final String RUN_INITIAL_VALUATION = "runInitialValuation";
    public static final String REQ_PURCHASE_ORDER_EXECUTION = "reqPurchaseOrderExecution";
    public static final String REQ_PURCHASE_ORDER_LIST = "reqPurchaseOrderList";
    public static final String REQ_DASH_BOARD_DATA = "reqDashBoardData";
    public static final String REQ_DASH_BOARD_PF_SUMMARY = "reqDashBoardPFSummary";
    public static final String APPROVE_PURCHASE_ORDER = "approvePurchaseOrder";
    public static final String CALCULATE_PROFIT = "calculateProfit";
    public static final String REQ_PF_VALUE = "reqPFvalue";
    public static final String REQ_APPROVE_ORDER_AGREEMENT = "reqApproveOrderAgreement";
    public static final String GET_FAILED_DEPOSITS_FOR_PO = "getFailedDeposits";
    public static final String UPDATE_PO_BY_ADMIN = "updatePOByAdmin";
    public static final String COMMODITY_PO_EXECUTE = "commodityPOExecute";
    public static final String CONFIRM_AUTH_ABIC_TO_SELL_BY_USER = "confirmAuthAbicToSellbyUser";
    public static final String REVERT_TO_SELL_DELIVER_BY_ADMIN = "revertToSellOrDeliver";
    
    /*---Settlement Related--*/
    public static final String SETTLEMENT_SUMMARY_APPLICATION = "settlementSummaryApplication";
    public static final String SETTLEMENT_BREAKDOWN_APPLICATION = "settlementBreakDownApplication";
    public static final String PERFORM_EARLY_SETTLEMENT = "performEarlySettlement";
    public static final String PERFORM_MANUAL_SETTLEMENT = "performManualSettlement";
    public static final String GET_SETTLEMENT_LIST = "getSettlementList";
    public static final String GET_LIST_FOR_MANUAL_SETTELEMENT = "getListForManualSettlement";
    public static final String SETTLEMENT_PROCESS = "settlementProcess";
    
    /*---Report Related--*/
    public static final String GENERATE_REPORT = "generateReport";
    public static final String GET_REPORT_STATUS = "getReportStatus";
    public static final String GET_REPORT_LIST = "getReportList";
    public static final String DOWNLOAD_REPORT = "downloadReport";

    /*------------OMS Request Related Request Types---------------------------*/
    public static final int GET_TRADING_ACCOUNT_LIST = 1;
    public static final int SYNC_SYMBOLS = 2;
    public static final int VALIDATING_TRADING_ACCOUNT = 3;
    public static final int GET_PF_SYMBOLS_FOR_COLLETRALS = 4;
    public static final int GET_NON_LSF_CASH_ACCOUNT_DETAILS = 5;
    public static final int GET_LSF_TYPE_TRADING_ACCOUNTS = 6;
    public static final int GET_LSF_TYPE_CASH_ACCOUNTS = 7;
    public static final int SEND_PO_INSTRUCTIONS = 11;
    public static final int GET_EXECUTION_DETAILS = 12;
    public static final int GET_CUSTOMER_INFO = 13;
    public static final int CREATE_ACCOUNT = 14;
    public static final int CASH_BLOCK_REQUEST = 15;
    public static final int CASH_RELEASE_REQUEST = 16;
    public static final int SHARE_BLOCK_REQUEST = 17;
    public static final int SHARE_RELEASE_REQUEST = 18;
    public static final int GET_PORTFOLIO_VALUE = 19;
    public static final int SHARE_TRANSFER_FINAL_ORDER = 21;
    public static final int LIQUIDATE_SYMBOLS = 22;
    public static final int CUSTOMER_SEARCH = 25;
    public static final int LIQUIDATE_PORTFOLIO = 26;
    public static final int GET_ACCOUNT_INFO_BY_TRADING_ACCOUNT = 27;
    public static final int VALIDATE_SSO_TOKEN = 28;
    public static final int ADMIN_FEE_REQUEST = 29;
    public static final int ORDER_BASKET_CANCELLATION = 30;
    public static final int TRADING_ENABLE_LSF_TRADING_ACOOUNT = 31;
    public static final int BLACK_LIST_CUSTOMER = 34;
    public static final int ENABLE_DISABLE_PUBLIC_ACCESS_OMS = 35;
    public static final int LSF_TYPE_ACCOUNT_CLOSER = 37;
    public static final int SERVICE_TYPE_GET_EXCHANGE_ALL_SYMBOLS = 38;
    public static final int GET_NON_SHARIA_PF_SYMBOLS_FOR_COLLETRALS = 39;
    public static final int CREATE_INVESTOR_ACCOUNT = 40;
    public static final int CREATE_EXCHANGE_ACCOUNT = 41;
    public static final int CASH_REFUND_TO_MASTER_ACCOUNT = 42;
    public static final int CHECK_LSF_TRADING_ACCOUNT_STATUS = 43;
    public static final int CANCEL_PENDING_ML_BASKETS = 44;
    public static final int SERVICE_TYPE_CUSTOMER_RISK_SCORE = 46;

    /*---Customer Info OMS---*/
    public static final String HTTP_PRODUCER_OMS_CUSTOMER_INFO = "http_producer_OMS_customer_info";
    /*---Cash Account Related OMS---*/
    public static final String HTTP_PRODUCER_OMS_CASH_ACCOUNT_RELATED = "http_producer_oms_cash_account_related";
    /*---Cash Account Related OMS---*/
    public static final String HTTP_PRODUCER_OMS_SYMBOL_RELATED = "http_producer_OMS_symbol_related_related";
    /*---SSO OMS Validation Related---*/
    public static final String HTTP_PRODUCER_OMS_REQ_VALIDATE_SSO = "http_producer_oms_req_validate_sso";
    /*---Purchase Order Related---*/
    public static final String HTTP_PRODUCER_OMS_PURCHASE_ORDER = "http_producer_oms_purchase_order";
    /*---Portfolio Related---*/
    public static final String HTTP_PRODUCER_OMS_PORTFOLIO_RELATED = "http_producer_oms_portfolio_related";
    /*--B2B  OMS Related---*/
    public static final String HTTP_PRODUCER_OMS_B2B_RELATED = "omsQueueProducer";
    /*--B2B  OMS Related---*/
    public static final String HTTP_PRODUCER_OMS_COMMON_REQUEST_RELATED = "http_producer_oms_common_request_related";
    /*--B2B  SMS Related---*/
    public static final String HTTP_PRODUCER_SMS_RELATED = "thirdPartyNotificationProducer";
    /*--B2B  Email Related---*/
    public static final String HTTP_PRODUCER_EMAIL_RELATED = "thirdPartyNotificationProducer";
    /*-----OMS Validation Related----*/
    public static final String HTTP_PRODUCER_OMS_VALIDATION_RELATED = "omsQueueProducer";
}