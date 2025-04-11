package com.dfn.lsf.util;

/**
 * Constants for LSF application
 * This replaces the original LsfConstants class
 */
public class LsfConstants {

    public static final int APP_LSF_CLIENT = 1;//"LSFCLIENT";
    public static final int APPLICATION_ID = 2;

    public static final int APP_LSF_ADMIN = 2;

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
    public static final String REQ_UPLOADABLE_DOCS = "reqUploadableDocs";
    public static final String REQ_CONCENTRATION_LIST = "reqConcentrationList";
    public static final String REQ_MARGIN_GROUP_LIST = "reqMargineGroupList";
    public static final String REQ_DEFAULT_MARGIN_GROUP = "reqDefaultMarginGroup";
    public static final String ADD_UPLOADABLE_DOCS = "addUploadableDocs";
    public static final String UPDATE_UPLOADABLE_DOCS = "updateUploadableDocs";
    public static final String UPDATE_CONCENTRATION_LIST = "updateConcentrationList";
    public static final String UPDATE_MARGIN_GROUP_LIST = "updateMargineGroupList";
    public static final String UPDATE_TENOR_DETAILS_LIST = "updateTenorDetailsList";
    public static final String GET_TENOR_DETAILS_LIST = "getTenorDetailsList";
    public static final String UPDATE_GLOBAL_PARAMETERS = "updateGlobalParameters";
    public static final String GET_GLOBAL_PARAMETERS = "getGlobalParameters";
    public static final String UPDATE_COMMISSION_STRUCTURE = "updateCommissionStructure";
    public static final String REQ_COMMISSION_STRUCTURE = "reqCommissionStructure";
    public static final String DELETE_COMMISSION_STRUCTURE = "deleteCommissionStructure";
    public static final String STATUS_CHANGE_TENOR = "statusChangeTenor";
    public static final String SET_STATUS_ADMIN_DOCS = "setStatusAdminDocs";
    public static final String REMOVE_ADMIN_DOC = "removeAdminDoc";
    public static final String SET_STATUS_MARGINABILITY_GROUP = "setStatusMarginabilityGroup";
    public static final String SET_STATUS_CONCENTRATION_GROUP = "setStatusConcentrationGroup";
    public static final String REQ_ORDER_ACC_PRIORITY = "reqOrderAccPriority";
    public static final String GET_APPLICATION_LIST_FOR_ADMIN_DOC_UPLOAD = "getApplicationListForAdminDocUpload";
    public static final String REMOVE_CUSTOM_DOC_FROM_APPLICATION = "removeCustomDocFromApplication";
    public static final String REMOVE_MARGINABILITY_GROUP = "removeMarginabilityGroup";
    public static final String REMOVE_CONCENTRATION_GROUP = "removeConcentrationGroup";
    public static final String REMOVE_TENOR_GROUP = "removeTenorGroup";
    public static final String ADD_ADMIN_USER = "addAdminUser";
    public static final String ADD_QUESTIONNAIRE_CONTENT = "addQuestionnaireContent";
    public static final String GET_QUESTIONNAIRE_CONTENT = "getQuestionnaireContent";
    public static final String ENABLE_DISABLE_PUBLIC_ACCESS = "enableDisablePublicAccess";
    public static final String REQ_GET_AGREEMENT_LIST = "getAgreements";
    public static final String REQ_ADD_COMMODITIES = "addCommodity";
    public static final String REQ_GET_COMMODITIES = "getActiveCommodityList";
    public static final String UPDATE_SYMBOL_MARGINABILITY = "updateSymbolMarginability";

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
    public static final String CONFIRM_ROLLOVER_BY_USER = "confirmRolloverByUser";

    /*---Report Related--*/
    public static final String GENERATE_REPORT = "generateReport";
    public static final String GET_REPORT_STATUS = "getReportStatus";
    public static final String GET_REPORT_LIST = "getReportList";
    public static final String DOWNLOAD_REPORT = "downloadReport";

    public static final String REQ_CUSTOMER_DETAILS_IFLEX = "reqCustomerDetailsIflex";
    public static final String REQ_TRADING_ACC_LIST = "reqTradingAccList";
    public static final String GET_CUSTOMER_DETAILS_ORDER_CONTRACT = "getCustomerDetailsOrderContract";
    public static final String GET_CUSTOMER_SUMMARY_INFO = "getCustomerSummaryInfo";
    public static final String GET_CUSTOMER_RISK_SCORE = "getCustomerRiskScore";

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

    public static final int ERROR_ABNORMAL_ACTIVITY = 9018;
    public static final int ERROR_IP_ADDRESS_IS_NOT_DETECTED = 9019;
    public static final int ERROR_ERROR_WHILE_VALIDATING_THE_SSO_TOKEN = 9020;
    public static final int ERROR_INVALID_SSO_TOKEN = 9021;
    public static final int PREVIOUS_APP_PERMANTLY_REJECTED = -1;
    /*----Admin Action Related---*/
    public static final long MINIMUM_TIME_GAP_BETWEEN_ADMIN_APPROVALS = 3000;

    public static final int SHARE_BLOCK_STATUS = 0;
    public static final int SHARE_RELEASE_STATUS = 1;
    public static final int SHARE_TRANSFERED_STATUS = 2;
    public static final int SHARE_TRANSFER_FAILED_FROM_EXCHANGE = -1;

    public static final int STATUS_COLLATERLS_SUBMITTED = 101;
    public static final int STATUS_COLLATERLS_SUBMISSION_FAILED = 1101;

    public static final int STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_CASH_TRANSFER = 1111;

    public static final int STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_SHARE_TRANSFER = 1112;

    public static final int STATUS_ACCOUNT_DELETION_REQUEST_SENT_TO_OMS = 113;

    public static final int STATUS_ACCOUNT_DELETION_REQUEST_FAILED_DUE_TO_SHARE_TRANSFER_FAILURE_WITH_EXCHANGE = 1114;

    public static final int STATUS_ACCOUNT_DELETION_REQUEST_FAILED_WITH_EXCHANGE = 1115;

    public static final int STATUS_ACCOUNT_DELETION_SUCCEED_FROM_OMS = 116;

    public static final int STATUS_PO_FILLED_WAITING_FOR_ACCEPTANCE = 103;
    public static final int STATUS_INVESTOR_ACCOUNT_CREATION_FAILED = 1105;
    public static final int STATUS_EXCHANGE_ACCOUNT_CREATION_FAILED = 1108;
    public static final int STATUS_COLLATERAL_SHARE_TRANSFER_FAILED_FROM_EXCHANGE = 1117;
    public static final int STATUS_BASKET_SHARE_TRANSFER_REQUEST_SENT_TO_OMS = 120;
    public static final int STATUS_BASKET_SHARE_TRANSFER_REQUEST_FAILED_TO_SEND_OMS = 1120;

    public static final int REQUEST_SENT_TO_OMS = 1;
    public static final int REQUEST_DID_NOT_ACCEPTED_CASH_TRANSFER_FAILED = -1;
    public static final int REQUEST_DID_NOT_ACCEPTED_SHARE_TRANSFER_FAILED = -2;
    public static final int REQUEST_DID_NOT_ACCEPTED_SELL_PENDING_AVAILABLE = -3;

    public static final String HTTP_PRODUCER_OMS_GET_LSF_CASH_ACCOUNT_USERID =
            "http_producer_oms_get_lsf_cash_account_userId";
    public static final String HTTP_PRODUCER_OMS_GET_MASTER_CASH_ACCOUNT = "http_producer_oms_get_master_cash_account";
    public static final String HTTP_PRODUCER_OMS_CASH_TRANSFER_MASTER_CASH_ACCOUNT =
            "http_producer_oms_cash_transfer_master_account";
    public static final String HTTP_PRODUCER_OMS_LIQUIDATE_PORTFOLIO = "http_producer_oms_liquidate_portfolio";
    public static final String HTTP_PRODUCER_OMS_REQ_GET_LSF_TYPE_TRADING_ACCOUNT =
            "http_producer_oms_req_get_lsf_type_trading_account";
    public static final String HTTP_PRODUCER_OMS_REQ_HANDLER = "http_producer_oms_req_handler";
    public static final String HTTP_PRODUCER_LSF_ADMIN_REPORT_ACK = "http_producer_lsf_admin_report_ack";

    public static final int BASKET_TRANSFER_SENT = 1;
    public static final int BASKET_TRANSFER_FAILED = -1;

    public static final long MILISECONDS_TO_HOUR = 3600000;

    public static final int ERROR_INVALID_DETAILS = 9003;
    public static final int ERROR_ACCOUNT_CREATION_FAILED = 9004;
    public static final int ERROR_PURCHASE_ORDERS_CANNOT_BE_SUBMITTED_AFTER_PLEASE_TRY_TOMORROW = 9005;

    public static final int SETTLEMENT_PENDING = 0;
    public static final int PROFIT_CALC_TENOR_BASED = 1;

    public static final int ERROR_EXCHNAGE_ACCOUNT_NOT_YET_CREATED = 9006;
    public static final int ERROR_SIGNING_ORDER_AGREEMENT_IS_NOT_ALLOWED_MARCCKET_CLOSED = 9007;
    public static final int ERROR_ORDER_HAS_BEEN_lIQUIDATED_DUE_TO_NOT_ACCEPTANCE = 9008;

    public static final int STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARGED = 109;
    public static final int STATUS_EXCHANGE_ACCOUNT_CREATED_AND_ADMIN_FEE_CHARG_FAILED = 1109;

    public static final int STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_SENT = 110;
    public static final int STATUS_COLLATERALS_AND_PO_SYMBOL_TRANSFER_REQUEST_FAILED_TO_OMS = 1110;
    public static final int STATUS_PO_CREATED_WAITING_TO_ORDER_FILL = 102;
    public static final int STATUS_PO_CREATION_FAILED = 1102;

    public static final int STATUS_SENT_INVESTOR_ACCOUNT_CREATION = 104;
    public static final int STATUS_ORDER_CONTRACT_REJECTED = 1104;
    public static final int STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION = 107;
    public static final int STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION = 1106;
    public static final int STATUS_INVESTOR_ACCOUNT_CREATION_FAILED_OMS = 1121;

    public static final int ERROR_ORDER_SEND_TO_OMS_FAILED = 9001;
    public static final int ERROR_COLLATRAL_RELEASE_FAILED_FROM_OMS = 9002;
    public static final int ACCOUNT_DELETION_SUCCESS = 1; /*---Share Transfer and Account Deletion Succeed----*/
    public static final int EXCHANGE_ACCOUNT_DELETION_FAILED_FROM_EXCHANGE = -1; /*--Share transfer with exchange
    succeed and account deletion failed from  exchange----*/
    public static final int SHARE_TRANSFER_FAILED_WITH_EXCHANGE = -2;

    public static final String MURABAH_APPLICATION = "murabahApplication";
    public static final String SAVE_MSG_CONFIGURATION = "saveMsgConfiguration";
    public static final String UPDATE_MSG_CONFIGURATION = "updateMsgConfiguration";
    public static final String VIEW_MESSAGE_HISTORY = "viewMessageHistory";

    //Report code
    public static final int MARGIN_INFORMATION_REPORT = 2;
    public static final int FINANCE_AND_BROKERAGE_REPORT = 3;
    public static final int INVESTMENT_OFFER_LETTER_RPOERT = 4;
    public static final int STOCK_CONCENTRATION_REPORT = 5;
    public static final String CREDIT_PROPOSAL = "CP";
    public static final String FACILITY_AGREEMENT_LETTER = "FAL";
    public static final String INVESTMENT_OFFER_LETTER = "IOF";

    //OMS QUEUE MESSAGE TYPES
    public static final int UPDATE_ORDER_STATUS_PROCESS = 120;
    public static final int APPROVE_ORDER_FOR_FTV = 123;
    public static final int APPROVE_WITHDRAW_FOR_FTV = 124;
    public static final int LIQUIDATION_SUCCESS_RESPONSE = 126;
    public static final int DEPOSIT_SUCCESS_RESPONSE = 132;
    public static final int WITHDRAW_SUCCESS_RESPONSE = 133;
    public static final int INVESTOR_ACCOUNT_CREATION_RESPONSE = 140;
    public static final int EXCHANGE_ACCOUNT_CREATION_RESPONSE = 141;
    public static final int RIA_LOGOUT_RESPONSE = 142;
    public static final int EXCHANGE_ACCOUNT_DELETION_RESPONSE = 137;
    public static final int TRADE_HOLDING_UPDATE_RESPONSE = 147;

    // admin common reject
    public static final int ERROR_FAILED_DURING_COLLATERAL_RELEASE = 9009;
    public static final int ERROR_YOU_CANNOT_CANCEL_THE_REQUEST_WITH_OPEN_ORDERS = 9010;
    public static final int ERROR_ERROR_WHILE_PROCESSING_THE_REQUEST = 9011;

    //collateral
    public static final int ERROR_ERROR_IN_COLLATERAL_UPDATING = 9012;
    public static final int ERROR_NET_COLLATRAL_VALUE_SHOULD_BE_GREATER_THAN_CONFIGURED_PERCENTAGE = 9013;
    public static final int ERROR_COLLATRAL_CANNOT_SUBMIT_DUE_TO_MARKET_CLOSED_STATE = 9014;
    public static final int ERROR_CASH_AMOUNT_VALIDATION_FAILED = 9015;

    //document upload
    public static final int ERROR_ALL_THE_DOCUMENTS_ARE_NOT_YET_UPLOADED = 9016;
    public static final int ERROR_PLEASE_UPLOAD_DOCUMENT = 9017;

    //inquiry
    public static final int ERROR_PF_REQUEST_FAILED = 9022;

    /*---Admin Customer Search Related--*/
    public static final String GET_CUSTOMER_LIST = "getCustomerList";
    public static final String R_CUSTOMER_SEARCH = "customerSearch";
    public static final String PAGE_COUNTER = "pageCounter";

    /*--------------Deposit Request Status-------------*/
    public static final int INITIALED_IN_LSF = 1;
    public static final int SENT_TO_B2B = 2;
    public static final int RESPONSE_RECEIVED_B2B_FAILED = 3;
    public static final int RESPONSE_RECEIVED_B2B_SUCCESS = 4;

    /*------------B2B Transaction Types---------*/
    public static final int DEPOSIT = 1;
    public static final int WITHDRAW = 2;

    public static final String LOAD_INIT_DATA = "loadInitData";

    /*---Admin Symbol Related--*/
    public static final String UPDATE_LIQUIDITY_TYPE = "updateLiquidityType";
    public static final String SEARCH_SYMBOL = "searchSymbol";
    public static final String GET_ALL_SYMBOLS = "getAllSymbols";
    public static final String GET_SYMBOL_MARGINABILITY_GROUPS = "getSymbolMarginabilityGroups";
    public static final String ADD_TO_WISH_LIST = "addToWishList";
    public static final String LOAD_SYMBOL_WISH_LIST = "loadSymbolWishList";
    public static final String UPDATE_SYMBOL_CLASSIFY_LOG_STATUS = "updateSymbolClassifyLogStatus";
    public static final String GET_SYMBOL_CLASSIFY_LOG = "getSymbolClassifyLog";
    public static final String GET_STOCK_CLASSIFICATION_DATA = "getStockClassificationData";
    public static final String GET_SYMBOL_MARGINABILITY_DATA = "getSymbolMarginabilityData";
    public static final String GET_SYMBOL_INSTRUMENTS_LIST = "getSymbolInstrumentslist";

    //profit calculation
    public static final int ERROR_ERROR_ON_CALCULATING_PROFIT = 9029;
    public static final String GET_SETTLEMENT_INSTALLMENT_LIST = "getSettlementInstallmentList";
    public static final String CONTRACT_ROLLOVER_PROCESS = "contractRollover";
    public static final int ERROR_NOT_IN_ROLLOVER_PERIOD = 9033;
    public static final int ERROR_NOT_IN_ROLLOVER_RATIO = 9034;

    //ML Application creation
    public static final int ERROR_APPLICATION_IS_AVAILABLE_FOR_THIS_CUSTOMER = 9023;
    public static final int ERROR_REQUESTED_LOAN_AMOUNT_CANNOT_BE_PROCESSED = 9024;
    public static final int ERROR_NO_RECORDS_TO_UPDATE = 9025;
    public static final int ERROR_NO_APPLICATION_ID = 9026;

    //OTP
    public static final int ERROR_OTP_GENERATION_FAILED = 9027;
    public static final int ERROR_OTP_VALIDATION_FAILED = 9028;

}