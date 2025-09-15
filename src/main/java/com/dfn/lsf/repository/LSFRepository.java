package com.dfn.lsf.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.application.ApplicationRating;
import com.dfn.lsf.model.application.QuestionnaireEntry;
import com.dfn.lsf.model.notification.AdminUser;
import com.dfn.lsf.model.notification.Message;
import com.dfn.lsf.model.notification.Notification;
import com.dfn.lsf.model.notification.NotificationMsgConfiguration;
import com.dfn.lsf.model.notification.WebNotification;
import com.dfn.lsf.model.report.FinanceBrokerageInfo;
import com.dfn.lsf.model.report.MarginInformation;
import com.dfn.lsf.model.report.ReportConfiguration;
import com.dfn.lsf.model.requestMsg.DepositWithdrawRequest;
import com.dfn.lsf.model.responseMsg.CommissionDetail;
import com.dfn.lsf.model.responseMsg.FTVInfo;
import com.dfn.lsf.model.responseMsg.FtvSummary;
import com.dfn.lsf.model.responseMsg.OrderContractCustomerInfo;
import com.dfn.lsf.model.responseMsg.PendingActivity;
import com.dfn.lsf.model.responseMsg.RiskwavierQuestionConfig;
import com.dfn.lsf.model.responseMsg.SettlementSummaryResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for LSF operations
 * This replaces the original LSFDaoI interface
 */
public interface LSFRepository {
    
    String updateMurabahApplication(MurabahApplication murabahApplication);

    List<MurabahApplication> getMurabahAppicationApplicationID(String applicationID);

    MurabahApplication getApplicationByLSFTradingAccount(String lsfTradingAccountID, int accountType);

    MurabahApplication getMurabahApplication(String applicationId);

    List<Status> getApplicationPermanentlyRejectedReason(String applicationID);

    List<MurabahApplication> geMurabahAppicationUserID(String customerID);

    List<MurabahApplication> getAllMurabahAppicationsForUserID(String customerID);

    List<MurabahApplication> geMurabahAppicationUserIDFilteredByClosedApplication(String customerID);

    List<MurabahApplication> getNotGrantedApplication(String customerID);

    List<MurabahApplication> getAllMurabahApplications();

    List<MurabahApplication> getFilteredApplication(int filterCriteria, String filterValue, String fromDate, String toDate, int requestStatus);

    List<MurabahApplication> getSnapshotCurrentLevel(int requestStatus);

    List<MurabahApplication> getCommoditySnapshotCurrentLevel(int requestStatus);

    List<MurabahApplication> getHistoryApplication(int filterCriteria, String filterValue, String fromDate, String toDate, int requestStatus);

    List<MurabahApplication> getReversedApplication(int reversedStatus);

    String reverseApplication(MurabahApplication murabahApplication);

    String createApplicationComment(Comment comment, String applicationID);

    List<Comment> getApplicationComment(String commentID, String applicationID);

    List<Comment> getApplicationComment(String applicationID);

    String approveApplication(int approveState, String applicationID, String statusMessage, String statusChangedUserID, String statusChangedUserName, String statusChangedIP);
    String commodityAppStatus(String applicationID, int currentLevel, String statusMessage, String statusChangedUserID, String statusChangedUserName, String statusChangedIP);

    String updateMarginabilityGroupAndStockConcentration(String stockConcentrationGroup, String marginabilityGroup, String applicationID);

    List<MurabahApplication> getMurabahApplicationForCurrentLevelAndOverRoleStatus(String currentLevel);

    //Get muraba application according to pagination
    List<MurabahApplication> getLimitedApprovedMurabahApplication(String pageSize, String pageNumber);

    //Get total number of application which are approved by admin (overRoleStatus > 0)
    String getApprovedCustomerRecordSize();

    //rate application
    long rateApplication(ApplicationRating applicationRating);

    //get application rating
    List<ApplicationRating> getApplicationRating(ApplicationRating applicationRating);

    //Add questionnaire entry
    void addQuestionnaireEntry(QuestionnaireEntry questionnaireEntry);

    //get questionnaire entries
    List<QuestionnaireEntry> getQuestionnaireEntries();

    //get questionnaire entries
    List<MurabahApplication> getDepositFailedApplications();

    // white list application

    String whiteListApplication(String applicationId,String customerId);

    String updateFacilityTransferStatus(String applicationId, String status);

    UserAccountDetails getApplicationAccountDetails(String applicationId, String colletralID);

    OrderContractCustomerInfo getOrderContractCustomerInfo(String applicationId);

    List<ApplicationStatus> applicationStatusSummary();

    String updateLastProfitCycleDate(String applicationId);
    List<PhysicalDeliverOrder> getPhysicalDeliveryFromDB();


    /*-----------------------Murabah Application Related Portfolio------------------*/
    String updateInitailAppPortfolio(Symbol symbol, String applicationID);

    List<Symbol> getInitialAppPortfolio(String applicationID);

    /*-----------------------User Document Related------------------*/
    String updateCustomerDocument(Documents document, String applicationID);

    List<Documents> getComparedCustomerDocumentList(String applicationID);

    List<Documents> getCustomerDocumentListByAppID(String applicationID);

    List<Documents> getCustomerDocumentListByDocID(String applicationID, String documentID);

    String removeCustomerDocs(String applicationID, String documentID);

    String updateDocumentMaster(Documents documents);

    List<Documents> getDocumentMasterList();

    List<MurabahApplication> getDocumentRelatedAppsByDocID(int documentID);

    String removeCustomDocFromApplication(int documentID, List<String> applicationList);

    String changeStatusDocumentMaster(int documentID, String approvedBy, int approveStatus);

    String removeAdminDoc(String documentId);

    String addCustomDocByAdmin(String applicationID, String documentID);

    List<MurabahApplication> getApplicationListForAdminDocUpload(int filterCriteria, String filterValue);

    /*-----------------------Admin Document Related------------------*/
    List<Documents> getApplicationAdminDocs(String applicationID);

    String updateApplicationAdminDocs(Documents document, String applicationID);

    String removeApplicationAdminDocs(String applicationID, String documentID);

    /*------------------------User Session Related-------------------*/
    String addUpdateUserSession(String sessionID, String userID, int channelID, int sessionStatus, String ipAddress, String omsSessionID, int status);

    List<UserSession> getUserSession(String userID);

    String updateSessionStateWithOMSResponse(String omsSessionID, int status);

    /*------------------------Application Status Flow Related-------------------*/
    List<Status> getApplicationStatusFlow();

    List<MarginabilityGroup> getMarginabilityGroups(String filterStatus);

    List<MarginabilityGroup> getDefaultMarginGroups();

    MarginabilityGroup getMarginabilityGroup(String groupId);

    List<LiquidityType> getMarginabilityGroupLiquidTypes(String groupId);

    List<SymbolMarginabilityPercentage> getMarginabilityPercByGroup(String groupId);

    String updateMarginabilityGroup(MarginabilityGroup marginabilityGroup);
    String updateSymbolMarginability(List<Map<String, Object>> marginabilityGroups);

    String updateMarginabilityGroupLiqTypes(MarginabilityGroup marginabilityGroup);

    String updateSymbolMarginabilityPercentages(MarginabilityGroup marginabilityGroup);

    String deleteFromSymbolMarginabilityGrp(MarginabilityGroup marginabilityGroup);

    String updateCommissionStructure(CommissionStructure commissionStructure);

    List<CommissionStructure> getCommissionStructure();

    String deleteCommissionStructure(String pm10id);

    String moveToCloseDeuToPONotAcceptance(String applicationID);

    //-----------Purchase Order Related ---------//
    String addPurchaseOrder(PurchaseOrder purchaseOrder, String ipAddress);

    List<PurchaseOrder> getAllPurchaseOrder(String applicationId);
    List<PurchaseOrder> getAllPurchaseOrderforCommodity(String applicationId);

    List<PurchaseOrder> getPOForReminding();

    PurchaseOrder getSinglePurchaseOrder(String orderId);

    String getNextAvailablePOID();

    String upadtePurchaseOrderInstallments(Installments installments);

    List<Installments> getPurchaseOrderInstallments(String orderId);

    String updatePurchaseOrderSymbols(Symbol symbol, String orderId);
    String updatePurchaseOrderCommodity(Commodity symbol, String orderId);
    String updatePurchaseOrderByAdmin(PurchaseOrder po);
    String updatePurchaseCommodityList(String orderId, List<Commodity> commodities);
    String updateCommodityPOExecution(PurchaseOrder po);
    List<Symbol> getPurchaseOrderSymbols(String orderId);

    String approveRejectOrder(PurchaseOrder purchaseOrder, String ipAddress);
    String approveRejectPOCommodity(PurchaseOrder purchaseOrder);

    String upadateOrderStatus(String orderId, int orderStatus, double complatedValue, double profit,double profitPercentage,double vatAmount, String statusChangedIP);

    String updateCustomerOrderStatus(String ordeID, String approveStatus, String approveComment, String ipAddress, String approvedBy, int approvedById);

    List<Symbol> getWishListSymbols(String applicationId, String exchange);

    String updateSymbol(Symbol symbol);

    String updateLiquidType(Symbol symbol);

    String updateWishListSymbols(MApplicationSymbolWishList wishList);

    String updatePurchaseOrderAcceptanceReminder(String  orderID, int attemptCount, int approveStatus, String lastUpdatedTime);

    String updateCallingAttemptLog(String applicatioNID, String orderID, int callingAttempt);

    void updateSymbolClassifyLogStatus(long customerId);

    List<SymbolClassifyLog> getSymbolClassifyLog(long customerId);

    String updatePOLiquidateState(String poID);

    String updateBasketTransferState(int orderID, int transferStatus);


    /*--------------------------Tenor Related-------------------------*/
    List<Tenor> getTenorList();

    Tenor getTenor(int tenorId);

    boolean removeTenorGroup(String tenorID);

    void deleteAllTenors();

    String updateTenor(Tenor tenor);

    String changeStatusTenor(Tenor tenor);


    List<LiquidityType> getLiquidityTypes();

    LiquidityType getSymbolLiquidityType(Symbol symbol);

    double getSymbolMarginabilityPerc(String symbol, String exchange, String appId);



    /*--------------------------Symbol param Related-----------------------*/
    List<Symbol> loadSymbols(String exchange, String symbolCode);
    List<Symbol> loadAllSymbols();
    List<SymbolMarginabilityPercentage> getSymbolMarginabilityGroups(String symbolCode, String exchange);
    List<Symbol> loadSymbolsForClassification();
    List<Symbol> getSymbolDescription(String symbolCode);
    List<SymbolMarginabilityPercentage> getSymbolMarginabilityPercentage(String applicationId);

    /*--------------------------Sys param Related-----------------------*/
    String updateGlobalParameters(GlobalParameters globalParameters);

    List<GlobalParameters> getGlobalParameters();
    List<Map<String, Object>>  getGlobalParametersData();
    String changePublicAccessState(int state);

    /*---------------- Application Collaterals --------------*/
    MApplicationCollaterals getApplicationCollateral(String applicationId);

    MApplicationCollaterals getApplicationCompleteCollateral(String applicationId);

    MApplicationCollaterals getApplicationCompleteCollateralForRollOver(String originalAppId);

    MApplicationCollaterals getCollateralForRollOverCollaterelWindow(String applicationId, MApplicationCollaterals applicationCollaterals);
    String addEditCollaterals(MApplicationCollaterals mApplicationCollaterals, String approvedBy, int approvedById);

    String addInitialCollaterals(MApplicationCollaterals mApplicationCollaterals);

    String addEditCompleteCollateral(MApplicationCollaterals mApplicationCollaterals, String approvedBy, int approvedById);

    String updateCollateralWithCompleteTradingAcc(MApplicationCollaterals mApplicationCollaterals);

    String changeStatusCollateral(MApplicationCollaterals mApplicationCollaterals);

    String updateSymbolTransferState(String tradingAccountID, String applicationID, String symbolCode, int state);

    /*--------- Cash Account ---------- */
    String updateCashAccount(CashAcc cashAcc);

    List<CashAcc> getCashAccountsInCollateral(String applicationId, String collateralId, int isLsfType);

    String updateRevaluationCashAccountRelatedInfo(String cashAccountID, CashAcc cashAcc);

    /*------- Trading Account --------- */
    String updateTradingAccount(TradingAcc tradingAcc);

    List<TradingAcc> getTradingAccountInCollateral(String applicationId, String collateralId, int isLsfType);
    List<TradingAcc> getLSFTypeTradingAccountByCashAccount(String cashAccountID);

    String updateRevaluationInfo(String tradingAccountID, double totalPFMarketValue, double totalWeightedPFMarketValue);
    String updateExchangeAccount(String tradingAccountID, String exchangeAccountNumber,String applicationId,int isLsfType);

    /*--- Trading Account Symbols ----- */
    String updateTradingAccountSymbols(TradingAcc tradingAcc, Symbol symbol);

    List<Symbol> getSymbolsInTradingAccount(String tradingAccountId,String applicationId);

    /*--------------------------Marginability Group Related-----------------------*/
    String removeMarginabilityGroup(String marginabilityGroupID);

    /*--------------------------Stock Concentration Group Related-----------------------*/
    String updateStockConcentrationGroup(StockConcentrationGroup concentrationGroup);

    String updateStockConcentrationGroupLiqTypes(StockConcentrationGroup concentrationGroup);

    List<StockConcentrationGroup> getStockConcentrationGroup();

    List<LiquidityType> getStockConcentrationGroupLiquidTypes(String groupId);

    String changeStatusMarginabilityGroup(MarginabilityGroup marginabilityGroup);

    String changeStatusConcentrationGroup(StockConcentrationGroup stockConcentrationGroup);

    boolean removeStockConcentrationGroup(String concentrationGroupID);

    /*-----------------------Notification Related-------------------------*/
    String addNotificationHeader(Notification notification);

    String addNotificationBody(String uid, String key, String value);

    String addWebNotification(WebNotification webNotification);

    List<WebNotification> getWebNotification(String applicationId);

    String updateReadWebNotification(String applicationId, String messageId);

    String updateNotificationMsgConfig(NotificationMsgConfiguration notificationMsgConfiguration);

    List<NotificationMsgConfiguration> getNotificationMsgConfiguration();

    List<NotificationMsgConfiguration> getNotificationMsgConfigurationForLevelStatus(String currentLevel, String overRoleStatus);

    List<NotificationMsgConfiguration> getNotificationMsgConfigurationForNotificationType(String notificationType);

    List<NotificationMsgConfiguration> getNotificationMsgConfigurationForApplication(String applicationID);

    List<Notification> getNotificationHeader(); //get notification header status = 2;

    List<Map<String, String>> getNotificationBody(String uid); //get notification body for a particular header;
    List<Message> getCustomMessageHistory();
    List<Message> getNotificationHistory();

    /*-----------------------Message Related-------------------------*/
    String addMessageOut(Message message);

    String updateStatusMessageOut(String uid, int status);


    /*---------------------ActivityLog Related-----------------------------*/
    String addActivityLog(ActivityLog activityLog);

    String addAuditDetails(Long id, String rawMassege, String messageType, String subMessageType, String ip, String channelID, String userID, String correlationID);

    /*---------------------User Surveyor Related -----------------------------*/
    String saveQuestionerAnswer(int userID, int questionID, String answer, String ipAddress);

    List<UserAnswer> getSavedAnswerForUser(int userID);

    /*---------------------Application Settlement Releated -----------------------------*/
    List<MurabahApplication> getOrderContractSingedApplications();

    List<PurchaseOrder> getPurchaseOrderForApplication(String applicationID);

    List<OrderProfit> getLastEntryForApplication(String applicationID, String orderID);

    String updateProfit(OrderProfit orderProfit, double lsfTypeCashBalance);
    String updateProfit_withDate(OrderProfit orderProfit, double lsfTypeCashBalance);
    boolean isProfitEntryExistsForDate(String applicationID, String orderID, LocalDate date);
    OrderProfit getProfitEntryForDate(String applicationID, String orderID, LocalDate date);
    List<NotificationMsgConfiguration> getNotificationMsgConfigurationForSettlement(int applicationCurrentLevel, String applicationCurrentStatus);

    String  addLiquidationLog(int liquidationReference, String cashFromAccount, String cashToAccount, double transferAmount, String applicationID, int status, String poID);

    List<LiquidationLog>  getLiquidationLog(int liquidationReference);

    String  updateLiquidationLogState(int liquidationReference, int status);

    String moveToLiquidateState(String applicationID, String message);

    String moveToClosedState(String applicationID, String message, String orderID);

    String updateAccountDeletionState(String applicationID, int state);

    String closeApplication(String applicationID);

    List<PurchaseOrder> getApplicationForManualSettlement();

    String checkFinalInstallmentApplication(String applicationID);
    String checkFinalInstallmentPO(String applicationID, String purchaseOrderID);
    String createInstallment(int  installmentNumber, String purchaseOrderID, String createdBy);
    String approveInstallment(int  installmentNumber, String purchaseOrderID, String approvedBy);
    String updatePOToSettledState(int purchaseOrderID);
    List<Installments> getCreatedInstallments();
    /*---------------------Application Settlement Inquiry Related -----------------------------*/

    OrderProfit getSummationOfProfitUntilToday(String applicationID, String orderID);
    List<OrderProfit> getAllOrderProfitsForApplication(String applicationID, String orderID);

    /*---------------------Reporting Related -----------------------------*/
    ReportConfigObject getReportConfigObject(int reportID);
    MarginInformation getMarginInformation(String fromDate, String toDate);
    List<FinanceBrokerageInfo> getFinanceBrokerageInfo();

    ReportConfiguration getReportConfiguration(String reportName);
    List<Map<String, Object>> getDataForReporting(String packageName, String procedureName, String className, Map<String, String> parameters);
    List<Map<String, Object>> getParamsForReporting(String packageName, String procedureName, String className, Map<String, String> parameters);

    /*--------------------B2B Request Related--------------*/
    String addDeposit(DepositWithdrawRequest depositRequest, String applicationID);
    String addWithdraw(DepositWithdrawRequest depositRequest, String applicationID);
    String updateDepositStatus(String referenceID, int status, int type);
    PurchaseOrder getPurchaseOrderByReference(String referenceID);

    String addExternalCollaterals(ExternalCollaterals externalCollaterals);
    List<ExternalCollaterals> getExternalCollaterals(int applicationId,int collateralId);
    String updateExternalCollaterals(ExternalCollaterals externalCollaterals);
    List<ExternalCollaterals> getExternalCollateralsForApplication(int applicationId);

    List<SettlementSummaryResponse> getSettlementListReport(int settlementStatus,String fromDate,String toDate);


    /*-------------------Margin Related ---------------*/
    String addFTVLog(MApplicationCollaterals collaterals);
    String addMarginCallLog(MApplicationCollaterals collaterals, int marginType);
    /*-------------------Risk Wavier Question ---------------*/
     List<RiskwavierQuestionConfig> getAllQustionSettings();

    /*-------Pending Activity-----------------------*/
    List<PendingActivity> getPendingActivityList();
    List<MurabahApplication> getApplicationListForAdminCommonReject();




    /*-------------------Notifications---------------*/
    //Add admin user to M05_ADMIN_USERS
    void addAdminUser(AdminUser adminUser);
    List<AdminUser> getAdminUsers();

    String updateApplicationOtp(MurabahApplication application);

    List<FtvSummary> getFTVsummaryForDashBoard(String applicationId);
    FtvSummary getFTVforToday(String applicationId);

    String updateActivity(String applicationId,int activityId);

    MurabahApplication getApplicationByCashAccount(String cashAccountID, int accountType);

    /*-----Validation Related----------*/
    double getMasterAccountOutstanding();
    String updateInvestorAcc(String cashAcc,String investorAcc,String applicationID);

    String updateAdminFee(double simaCharges,double transferCharges,double vatAmount,String poId);

    List<Map<String, Object>> getCashAccDataForConcentration(String toDate);
    List<Map<String, Object>> getCashAccDataForConcentrationToday();
    List<Map<String, Object>> getStockDataForConcentration(String toDate);
    List<Map<String, Object>> getStockDataForConcentrationToday();
    List<MurabahaProduct> getMurabahaProducts();
    String updateMurabahaProduct(MurabahaProduct murabahaProduct);
    String changeMurabahaProductStatus(MurabahaProduct murabahaProduct);
    OMSCommission getExchangeAccCommission(String ExchangeAccount);
    MurabahaProduct getMurabahaProduct(int productId);


    /*--------Profit Calculation Job Related------*/

    String insertProfitCalculationMasterEntry(int profitCalculationEligibleAppCount);
    List<ProfitCalMurabahaApplication> getProfitCalculationEligibleApplications();
    ProfitCalculationMasterEntry getLastProfitCalculationEntry();

    List<OrderProfit> getProfitEntryForApplicationAndDate(String applicationID, String dateStr);
    String correctProfitEntry(String applicationID,String customerID, String dateStr);
    String updateAgreementByAdmin(Agreement agreement, String ip, String userID, String userName);
    String updateStatusByUser(int applicationID, int status);
    String initialAgreementStatus(int id, int financeMethod, int productType, int agreementType);
    List<Agreement> getActiveAgreements(int id);
    List<Agreement> getActiveAgreementsForProduct(int financeMethod,int productType);
    String addCommodityToMaster(Commodity commodity);
    List<Commodity> getAllActiveCommodities();
    String deleteCommodity(String pm10id);
    List<Commodity> getPurchaseOrderCommodities(String orderId);
    String addAuthAbicToSellStatus(PurchaseOrder purchaseOrder);
    List<PurchaseOrder> getPOForSetAuthAbicToSell(int gracePrd);
    List<InstumentType> loadSymbolInstrumentTypes();
    List<Status> getApplicationStatus(String applicationID);
    List<MApplicationCollaterals> getApplicationCollateralFtvList();
    List<CommissionDetail> getCommissionDetails(String reportDate);
    List<FTVInfo> getDetailedFTVList(String fromDate, String toDate, int settlementSts);
    List<MurabahApplication> getApprovedPurchaseOrderApplicationList(Object fromDate, Object toDate);
    List<MurabahApplication> getBlackListedApplications();
    List<Agreement> getAgreements();
    String updateAdditionalDetails(PhysicalDeliverOrder physicalDeliverOrder);
    int hasRollOver(String applicationId);
    MurabahApplication getRolloverApplication(String applicationID);

//    @Query(value = "SELECT po.*, ca.L07_INVESTOR_ACCOUNT " +
//                   "FROM l14_purchase_order po, l07_cash_account ca " +
//                   "WHERE po.l14_app_id IN (:applicationIds) " +
//                   "AND ca.l07_l01_app_id = po.l14_app_id " +
//                   "AND ca.L07_IS_LSF_TYPE = 1",
//           nativeQuery = true)
    Map<String, List<PurchaseOrder>> getAllPurchaseOrdersForApplicationsBatch(@Param("applicationIds") List<String> applicationIds);

//    @Query(value = "SELECT * FROM l14_installments " +
//                   "WHERE l14_purchase_order_id IN (:purchaseOrderIds)",
//           nativeQuery = true)
    Map<String, List<Installments>> getPurchaseOrderInstallmentsBatch(@Param("purchaseOrderIds") List<String> purchaseOrderIds);

//    @Query(value = "SELECT l34.*, m12.* " +
//                   "FROM l34_purchase_order_commodities l34, m12_commodities m12 " +
//                   "WHERE l34.l34_m12_commodity_code = m12.m12_commodity_code " +
//                   "AND l34.l34_m12_exchange = m12.m12_exchange " +
//                   "AND l34.l34_l16_purchase_ord_id IN (:purchaseOrderIds)",
//           nativeQuery = true)
    Map<String, List<Commodity>> getPurchaseOrderCommoditiesBatch(@Param("purchaseOrderIds") List<String> purchaseOrderIds);

}