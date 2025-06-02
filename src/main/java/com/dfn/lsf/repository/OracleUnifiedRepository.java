package com.dfn.lsf.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import com.dfn.lsf.model.ActivityLog;
import com.dfn.lsf.model.Agreement;
import com.dfn.lsf.model.ApplicationStatus;
import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.Comment;
import com.dfn.lsf.model.CommissionStructure;
import com.dfn.lsf.model.Commodity;
import com.dfn.lsf.model.Documents;
import com.dfn.lsf.model.ExternalCollaterals;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.Installments;
import com.dfn.lsf.model.InstumentType;
import com.dfn.lsf.model.LiquidationLog;
import com.dfn.lsf.model.LiquidityType;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MApplicationSymbolWishList;
import com.dfn.lsf.model.MarginabilityGroup;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.MurabahaProduct;
import com.dfn.lsf.model.OMSCommission;
import com.dfn.lsf.model.OrderProfit;
import com.dfn.lsf.model.PhysicalDeliverOrder;
import com.dfn.lsf.model.ProfitCalMurabahaApplication;
import com.dfn.lsf.model.ProfitCalculationMasterEntry;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.ReportConfigObject;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.model.StockConcentrationGroup;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.SymbolClassifyLog;
import com.dfn.lsf.model.SymbolMarginabilityPercentage;
import com.dfn.lsf.model.Tenor;
import com.dfn.lsf.model.TradingAcc;
import com.dfn.lsf.model.UserAccountDetails;
import com.dfn.lsf.model.UserAnswer;
import com.dfn.lsf.model.UserSession;
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
import com.dfn.lsf.repository.mapper.MapperRegistry;
import com.dfn.lsf.repository.mapper.RowMapperFactory;
import com.dfn.lsf.repository.mapper.RowMapperRegistry;
import com.dfn.lsf.repository.oracle.OracleRepository;
import com.dfn.lsf.util.DBConstants;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.RowMapperI;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

@Repository
public class OracleUnifiedRepository implements LSFRepository {
    
    private static final Logger log = LoggerFactory.getLogger(OracleUnifiedRepository.class);
    
    // Spring components
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final MapperRegistry mapperRegistry;
    private final OracleRepository oracleRepository;
    private final RowMapperFactory rowMapperFactory;
    
    // Virtual thread executor for async operations
    private final Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    
    // Configuration settings
    @Value("${spring.datasource.schema:LSF}")
    private String dbSchema;
    
    
    public OracleUnifiedRepository(DataSource dataSource, RowMapperRegistry rowMapperRegistry,
                                  OracleRepository oracleRepository, RowMapperFactory rowMapperFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mapperRegistry = rowMapperRegistry;
        this.oracleRepository = oracleRepository;
        this.rowMapperFactory = rowMapperFactory;
        
        // Configure common SQL settings
        this.jdbcTemplate.setQueryTimeout(30); // 30 seconds timeout
    }
    
    // Common utilities
    
    /**
     * Create a parameter source from parameters, safely handling nulls
     */
    private MapSqlParameterSource createParamSource(Map<String, ?> params) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        if (params != null) {
            paramSource.addValues(params);
        }
        return paramSource;
    }
    /**
     * Execute a stored procedure asynchronously using Java 21 virtual threads
     * 
     * @param packageName Oracle package name
     * @param procedureName Procedure name
     * @param params Parameters
     * @return CompletableFuture with result
     */
    public CompletableFuture<Map<String, Object>> executeAsync(
            String packageName, String procedureName, Map<String, Object> params) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                        .withCatalogName(packageName)
                        .withProcedureName(procedureName);
                
                MapSqlParameterSource sqlParams = createParamSource(params);
                return jdbcCall.execute(sqlParams);
            } catch (Exception e) {
                log.error("Error in async execution of {}.{}", packageName, procedureName, e);
                return Collections.emptyMap();
            }
        }, virtualThreadExecutor);
    }
    
    /**
     * Execute a query asynchronously using Java 21 virtual threads
     * 
     * @param sql SQL query
     * @param params Query parameters
     * @return CompletableFuture with results
     */
    public CompletableFuture<List<Map<String, Object>>> queryAsync(String sql, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MapSqlParameterSource sqlParams = createParamSource(params);
                return namedParameterJdbcTemplate.queryForList(sql, sqlParams);
            } catch (Exception e) {
                log.error("Error in async query execution", e);
                return Collections.emptyList();
            }
        }, virtualThreadExecutor);
    }
    
    @Override
    @Cacheable(value = "murabahApplications", key = "#applicationId", unless = "#result == null")
    public MurabahApplication getMurabahApplication(String applicationId) {
        try {
            List<MurabahApplication> applications = getMurabahAppicationApplicationID(applicationId);
            return !applications.isEmpty() ? applications.get(0) : null;
        } catch (Exception e) {
            log.error("Error fetching murabah application: {}", applicationId, e);
            return null;
        }
    }
    
    @Override
    public List<MurabahApplication> getMurabahAppicationApplicationID(String applicationID) {
        try {
            RowMapper<MurabahApplication> rowMapper = rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION);
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName(DBConstants.PKG_L01_APPLICATION)
                    .withProcedureName(DBConstants.PROC_GET_BY_APPLICATION_ID)
                    .returningResultSet("pview", rowMapper);
            
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("pl01_app_id", applicationID);
            
            Map<String, Object> result = jdbcCall.execute(params);
            @SuppressWarnings("unchecked")
            List<MurabahApplication> applications = 
                    (List<MurabahApplication>) result.get("pview");
            
            if (applications != null && !applications.isEmpty()) {
                for (MurabahApplication application : applications) {
                    // Comment out until we have the proper getter method
                    // List<Map<String, Object>> statusData = getApplicationStatus(application.getId() != null ? application.getId() : "0");
                    // Convert Map to Status objects if needed
                    // application.setAppStatus(statusList);
                }
            }
            
            return applications != null ? applications : Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching murabah application: {}", applicationID, e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Symbol> getSymbolDescription(String symbolCode) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("psymbolCode", symbolCode);

        return oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_l08_GET_SYMBOL_DIS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_DESCRIPTION));
    }
    
    @Override
    public List<com.dfn.lsf.model.Commodity> getPurchaseOrderCommodities(String purchaseOrderRef) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl34_l16_purchase_ord_id", purchaseOrderRef);
        
        return oracleRepository.getProcResult(DBConstants.L34_PO_COMMODITIES_PKG,
                DBConstants.L34_GET_PO_COMMODITIES, parameterMap, 
                rowMapperFactory.getRowMapper(RowMapperI.COMMODITY_LIST));
    }

    @Override
    public MApplicationCollaterals getApplicationCompleteCollateral(String applicationId) {
        MApplicationCollaterals applicationCollaterals = this.getApplicationCollateral(applicationId);
        applicationCollaterals.setApplicationId(applicationId);
        // add normal Cash accounts to Collateral Object
        applicationCollaterals.setCashAccForColleterals(this.getCashAccountsInCollateral(applicationId, applicationCollaterals.getId(), 0));
        // add LSF Type Cash Accounts to Collateral Object
        applicationCollaterals.setLsfTypeCashAccounts(this.getCashAccountsInCollateral(applicationId, applicationCollaterals.getId(), 1));
        // get normal Trading accout list
        List<TradingAcc> tradingAccList = this.getTradingAccountInCollateral(applicationId, applicationCollaterals.getId(), 0);
        // update with Symbols in Trading Account
        for (TradingAcc tradingAcc : tradingAccList) {
            tradingAcc.setSymbolsForColleteral(this.getSymbolsInTradingAccount(tradingAcc.getAccountId(), tradingAcc.getApplicationId()));
        }
        // add Normal Trading accounts to Collateral Object

        applicationCollaterals.setTradingAccForColleterals(tradingAccList);
        // LSF type Trading account list
        List<TradingAcc> tradingAccListLsfType = this.getTradingAccountInCollateral(applicationId, applicationCollaterals.getId(), 1);
        // LSF type trading accounts corresponding Symbol list
        for (TradingAcc tradingAcc : tradingAccListLsfType) {
            tradingAcc.setSymbolsForColleteral(this.getSymbolsInTradingAccount(tradingAcc.getAccountId(), tradingAcc.getApplicationId()));
        }
        // add LSF Type to Collateral Object
        applicationCollaterals.setLsfTypeTradingAccounts(tradingAccListLsfType);

        // add External Collaterals
        if (applicationCollaterals.getId() != null) {
            applicationCollaterals.setExternalCollaterals(this.getExternalCollaterals(Integer.parseInt(applicationId), Integer.parseInt(applicationCollaterals.getId())));
        }
        return applicationCollaterals;
    }
    
    @Override
    public MApplicationCollaterals getApplicationCollateral(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL05_L01_APP_ID", applicationId);

        List<MApplicationCollaterals> collateras = oracleRepository.getProcResult(DBConstants.PKG_L05_COLLATERALS, 
                DBConstants.PROC_GET_COLLATERALS, parameterMap,
                rowMapperFactory.getRowMapper(RowMapperI.COLLATERALS));

        if (collateras != null) {
            return collateras.size() > 0 ? collateras.get(0) : new MApplicationCollaterals();
        } else {
            return new MApplicationCollaterals();
        }
    }
    
    @Override
    public List<CashAcc> getCashAccountsInCollateral(String applicationId, String collateralId, int isLsfType) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL07_L05_COLLATERAL_ID", collateralId);
        parameterMap.put("pL07_L01_APP_ID", applicationId);
        parameterMap.put("pL07_IS_LSF_TYPE", isLsfType);
        return oracleRepository.getProcResult(DBConstants.PKG_L07_CAH_ACCOUNT, DBConstants.PROC_L07_GET_ACCOUNT_IN_APP, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.CASH_ACCOUNT));
    }

    /// down here copied from source LSFRepository
    /// need to remove duplicates from down here
    /// refactor the code to use the new repository
    
    @Override
    @CacheEvict(value = "murabahApplications", key = "#murabahApplication.id")
    public String updateMurabahApplication(MurabahApplication murabahApplication) {

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(murabahApplication.getId()));
        parameterMap.put("pl01_customer_id", murabahApplication.getCustomerId());
        parameterMap.put("pl01_full_name", murabahApplication.getFullName());
        parameterMap.put("pl01_occupation", murabahApplication.getOccupation());
        parameterMap.put("pl01_employer", murabahApplication.getEmployer());
        if (murabahApplication.isSelfEmp() == true || murabahApplication.isSelfEmp() == false) {
            parameterMap.put("pl01_is_self_emp", murabahApplication.isSelfEmp() ? 1 : 0);
        }
        parameterMap.put("pl01_line_of_bisiness", murabahApplication.getLineOfBusiness());
        parameterMap.put("pl01_avg_monthly_income", Float.parseFloat(String.valueOf(murabahApplication.getAvgMonthlyIncome())));// Float.parseFloat(String.valueOf(murabahApplication.getAvgMonthlyIncome())));
        parameterMap.put("pl01_finance_req_amt", Float.parseFloat(String.valueOf(murabahApplication.getFinanceRequiredAmt())));//Float.parseFloat(String.valueOf(murabahApplication.getFinanceRequiredAmt())));
        parameterMap.put("pl01_address", murabahApplication.getAddress());
        parameterMap.put("pl01_mobile_no", murabahApplication.getMobileNo());
        parameterMap.put("pl01_telephone_no", murabahApplication.getTeleNo());
        parameterMap.put("pl01_email", murabahApplication.getEmail());
        parameterMap.put("pl01_fax", murabahApplication.getFax());
        parameterMap.put("pl01_dib_acc", murabahApplication.getDibAcc());
        parameterMap.put("pl01_trading_acc", murabahApplication.getTradingAcc());
        if (murabahApplication.isOtherBrkAvailable()) {
            parameterMap.put("pl01_is_other_brk_available", 1);
            parameterMap.put("pl01_other_brk_avg_pf", Float.parseFloat(murabahApplication.getOtherBrkAvgPf()));//Float.parseFloat(murabahApplication.getOtherBrkAvgPf()));
        } else if (!murabahApplication.isOtherBrkAvailable()) {
            parameterMap.put("pl01_is_other_brk_available", 0);
            parameterMap.put("pl01_other_brk_avg_pf", null);
        }
        parameterMap.put("pl01_other_brk_names", murabahApplication.getOtherBrkNames());
        // parameterMap.put("pl01_other_brk_avg_pf",Float.parseFloat(murabahApplication.getOtherBrkAvgPf()) );//Float.parseFloat(murabahApplication.getOtherBrkAvgPf()));
        parameterMap.put("pl01_overall_status", Integer.parseInt(murabahApplication.getOverallStatus()));
        parameterMap.put("pl01_current_level", murabahApplication.getCurrentLevel());
        parameterMap.put("pl01_type_of_facility", murabahApplication.getTypeofFacility());// murabahApplication.getTypeofFacility());
        parameterMap.put("pl01_facility_type", murabahApplication.getFacilityType());//murabahApplication.getFacilityType());
        parameterMap.put("pl01_proposal_limit", Float.parseFloat(String.valueOf(murabahApplication.getProposedLimit())));//Float.parseFloat(String.valueOf(murabahApplication.getProposedLimit())));
        parameterMap.put("pl01_revised_to", null);//Integer.parseInt(murabahApplication.getReversedTo()));
        parameterMap.put("pl01_revised_from", null);//Integer.parseInt(murabahApplication.getReversedFrom()));
        parameterMap.put("pl01_is_locked", 0);// Need to change
        parameterMap.put("pl01_l12_stock_conc_grp_id", murabahApplication.getStockConcentrationGroup());
        parameterMap.put("pl01_l11_marginability_grp_id", murabahApplication.getMarginabilityGroup());
        parameterMap.put("pl01_l15_tenor_id", Integer.parseInt(murabahApplication.getTenor()));
        parameterMap.put("pl01_initial_rapv", murabahApplication.getInitialRAPV());
        parameterMap.put("pl01_cash_acc", murabahApplication.getDibAcc());
        parameterMap.put("pl01_cash_balance", Float.parseFloat(String.valueOf(murabahApplication.getAvailableCashBalance())));//Float.parseFloat(String.valueOf(murabahApplication.getProposedLimit())));
        parameterMap.put("pL01_TRADING_ACC_EXCHANGE", murabahApplication.getTradingAccExchange());
        parameterMap.put("pL01_REVIEW_DATE", murabahApplication.getReviewDate());
        parameterMap.put("pl01_admin_fee_charged", murabahApplication.getAdminFeeCharged());
        parameterMap.put("pl01_max_symbol_cnt", murabahApplication.getMaximumNumberOfSymbols());
        parameterMap.put("pl01_customer_ref_no", murabahApplication.getCustomerReferenceNumber());
        parameterMap.put("pl01_zip_code", murabahApplication.getZipCode());
        parameterMap.put("pl01_bank_brch_name", murabahApplication.getBankBranchName());
        parameterMap.put("pl01_city", murabahApplication.getCity());
        parameterMap.put("pl01_pobox", murabahApplication.getPoBox());
        parameterMap.put("pl01_prefered_language", murabahApplication.getPreferedLanguage());
        parameterMap.put("pl01_discount_on_profit", murabahApplication.getDiscountOnProfit());
        parameterMap.put("pl01_profit_percentage", murabahApplication.getProfitPercentage());
        parameterMap.put("pl01_automatic_settlement", murabahApplication.getAutomaticSettlementAllow());
        parameterMap.put("pl01_product_type", murabahApplication.getProductType());
        parameterMap.put("pl01_rollover_app_id",murabahApplication.getRollOverAppId());
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_ADD_UPDATE_APPLICATION, parameterMap);
    }
    
    @Override
    public MurabahApplication getApplicationByLSFTradingAccount(String lsfTradingAccountID, int accountType) {
        List<MurabahApplication> murabahApplications = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl06_trading_acc", lsfTradingAccountID);
        parameterMap.put("pl06_is_lsf_type", accountType);
        murabahApplications = oracleRepository.getProcResult(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_GET_APP_BY_TRADING_ACC, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
        if (murabahApplications != null && murabahApplications.size() > 0) {
            return murabahApplications.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<Status>   getApplicationStatus(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl02_l01_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L02_APP_STATE, DBConstants.PROC_L01_GET_APP_STATE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.APPLICATION_STATUS));

    }

    @Override
    public List<Status> getApplicationPermanentlyRejectedReason(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl02_l01_app_id", Integer.parseInt(applicationID));
            return oracleRepository.getProcResult(DBConstants.PKG_L02_APP_STATE, DBConstants.PROC_L01_GET_APP_REJECTED_REASON, parameterMap,  rowMapperFactory.getRowMapper(RowMapperI.APPLICATION_STATUS));
    }

    @Override
    public List<MurabahApplication> geMurabahAppicationUserID(String customerID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_customer_id", customerID);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_GET_BY_CUSTOMER_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }
    
    @Override
    public List<MurabahApplication> getAllMurabahAppicationsForUserID(String customerID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_customer_id", customerID);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_GET_APPS_BY_CUSTOMER_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> geMurabahAppicationUserIDFilteredByClosedApplication(String customerID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_customer_id", customerID);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_GET_NOT_CLOSED_APPS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getNotGrantedApplication(String customerID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_customer_id", customerID);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_NOT_GRANTED, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }
    
    @Override
    public List<MurabahApplication> getAllMurabahApplications() {
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_ALL, null, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getFilteredApplication(int filterCriteria, String filterValue, String fromDate, String toDate, int requestStatus) {
        List<MurabahApplication> murabahApplications = null;
        List<MurabahApplication> murabahApplicationListResponse = new ArrayList<>();
        List<Status> statusList = null;
        List<Comment> commentList = null;
        List<Comment> finalCommentList = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_filter_criteria", filterCriteria);
        parameterMap.put("pl01_filter_value", filterValue);
        parameterMap.put("pl01_from_date", fromDate);
        parameterMap.put("pl01_to_date", toDate);
        parameterMap.put("pl01_request_status", requestStatus);
        murabahApplications = oracleRepository.<MurabahApplication>getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_FILTERED_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
        if (murabahApplications.size() > 0) {
            for (MurabahApplication murabahApplication : murabahApplications) {
                if (Integer.parseInt(murabahApplication.getOverallStatus()) >= 0) {
                    statusList = getApplicationStatus(murabahApplication.getId());
                    murabahApplication.setAppStatus(statusList);
                    commentList = getApplicationComment(murabahApplication.getId());
                    for (Comment comment : commentList) {
                        if (Integer.parseInt(comment.getParentID()) == 0) {
                            Comment tempComment = comment;
                            for (Comment reply : commentList) {
                                if (reply.getParentID().equalsIgnoreCase(tempComment.getCommentID().trim())) {
                                    tempComment.setReply(reply);
                                }
                            }
                            finalCommentList.add(tempComment);
                        }
                    }
                    murabahApplication.setInstitutionInvestAccount(GlobalParameters.getInstance().getInstitutionInvestAccount());
                    murabahApplication.setCommentList(finalCommentList);
                    murabahApplicationListResponse.add(murabahApplication);
                }

            }
        }
        return murabahApplicationListResponse;
    }
    
    @Override
    public List<MurabahApplication> getSnapshotCurrentLevel(int requestStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_request_status", requestStatus);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_SNAPSHOT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getCommoditySnapshotCurrentLevel(int requestStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_request_status", requestStatus);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_COMMODITY_SNAPSHOT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getHistoryApplication(int filterCriteria, String filterValue, String fromDate, String toDate, int requestStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_filter_criteria", filterCriteria);
        parameterMap.put("pl01_filter_value", filterValue);
        parameterMap.put("pl01_from_date", fromDate);
        parameterMap.put("pl01_to_date", toDate);
        parameterMap.put("pl01_request_status", requestStatus);
        return oracleRepository.<MurabahApplication>getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_HISTORY_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

//    @Override
//    public List<MurabahApplication> getReversedApplication(int reversedStatus) {
//        Map<String, Object> parameterMap = new HashMap<>();
//        parameterMap.put("pl01_request_status", reversedStatus);
//        return oracleRepository.<MurabahApplication>getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_REVERSED_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
//    }

    @Override
    public List<MurabahApplication> getReversedApplication(int reversedStatus) {
        List<MurabahApplication> murabahApplications = null;
        List<Status> statusList = null;
        List<Comment> finalCommentList = new ArrayList<>();
        List<Comment> commentList = null;
        List<Agreement> agreementList = null;
        List<PurchaseOrder> purchaseOrderList = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_request_status", reversedStatus);
        murabahApplications = oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_REVERSED_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
        if (murabahApplications.size() > 0) {
            for (MurabahApplication murabahApplication : murabahApplications) {
                statusList = getApplicationStatus(murabahApplication.getId());
                murabahApplication.setAppStatus(statusList);
                commentList = getApplicationComment(murabahApplication.getId());
                for (Comment comment : commentList) {
                    if (Integer.parseInt(comment.getParentID()) == 0) {
                        Comment tempComment = comment;
                        for (Comment reply : commentList) {
                            if (reply.getParentID().equalsIgnoreCase(tempComment.getCommentID().trim())) {
                                tempComment.setReply(reply);
                            }
                        }
                        finalCommentList.add(tempComment);
                    }
                }
                commentList = null;
                if (reversedStatus == 14) {
                    agreementList = getActiveAgreements(Integer.parseInt(murabahApplication.getId()));
                    murabahApplication.setAgreementList(agreementList);
                    agreementList = null;

                    purchaseOrderList = getAllPurchaseOrderforCommodity(murabahApplication.getId());
                    murabahApplication.setPurchaseOrderList(purchaseOrderList);
                    purchaseOrderList = null;
                    murabahApplication.setInstitutionInvestAccount(GlobalParameters.getInstance().getInstitutionInvestAccount());
                }
                murabahApplication.setCommentList(finalCommentList);
                finalCommentList = new ArrayList<>();
            }
        }
        return murabahApplications;
    }
    
    @Override
    public String reverseApplication(MurabahApplication murabahApplication) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", murabahApplication.getId());
        parameterMap.put("pl01_revised_to", murabahApplication.getReversedTo());
        parameterMap.put("pl01_revised_from", murabahApplication.getReversedFrom());
        parameterMap.put("pl01_is_editable", murabahApplication.isEditable());
        parameterMap.put("pl01_is_reversed", murabahApplication.isReversed());
        parameterMap.put("pl01_is_edited", murabahApplication.isEdited());
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_REVERSE_APPLICATION, parameterMap);
    }

    @Override
    public String createApplicationComment(Comment comment, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl21_reserved_from", Integer.parseInt(comment.getReversedFrom()));
        parameterMap.put("pl21_reserved_to", Integer.parseInt(comment.getReversedTo()));
        parameterMap.put("pl21_comment", comment.getComment());
        parameterMap.put("pl21_parent_id", Integer.parseInt(comment.getParentID().replaceAll("\\s+", "")));
        parameterMap.put("pl21_app_id", Integer.parseInt(applicationID));
        parameterMap.put("pl21_commented_by", comment.getCommentedBy());
        return oracleRepository.executeProc(DBConstants.PKG_L21_APP_COMMENTS, DBConstants.PROC_L21_ADD_APPLICATION_COMMENT, parameterMap);

    }

    @Override
    public List<Comment> getApplicationComment(String commentID, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl21_app_id", applicationID);
        parameterMap.put("pl21_commented_id", commentID);
        return oracleRepository.getProcResult(DBConstants.PKG_L21_APP_COMMENTS, DBConstants.PROC_L21_GET_APPLICATION_COMMENT_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.APP_COMMENT));

    }

    @Override
    public List<Comment> getApplicationComment(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl21_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L21_APP_COMMENTS, DBConstants.PROC_L21_GET_APPLICATION_COMMENT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.APP_COMMENT));

    }

    @Override
    public String approveApplication(int approveState, String applicationID, String statusMessage, String statusChangedUserID, String statusChangedUserName, String statusChangedIP) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("approve_state", approveState);
        parameterMap.put("pl01_app_id", applicationID);
        parameterMap.put("pl02_message", statusMessage);
        parameterMap.put("pl02_sts_changed_user_id", statusChangedUserID);
        parameterMap.put("pl02_sts_changed_user_name", statusChangedUserName);
        parameterMap.put("pl02_status_changed_ip", statusChangedIP);
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.PROC_M02_APPROVE_APPLICATION, parameterMap);
    }

    @Override
    @CacheEvict(value = "murabahApplications", key = "#applicationID")
    public String updateMarginabilityGroupAndStockConcentration(String stockConcentrationGroup, String marginabilityGroup, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_l12_stock_conc_grp_id", stockConcentrationGroup);
        parameterMap.put("pl01_l11_marginability_grp_id", marginabilityGroup);
        parameterMap.put("pl01_app_id", applicationID);
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_UPDATE_STOCK_MARG_GRP, parameterMap);

    }

    @Override
    public List<MurabahApplication> getMurabahApplicationForCurrentLevelAndOverRoleStatus(String currentLevel) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_current_level", currentLevel);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_LEVEL_STATUS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getLimitedApprovedMurabahApplication(String pageSize, String pageNumber) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_page_size", pageSize);
        parameterMap.put("pl01_page_number", pageNumber);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_LIMITED_APPROVED_CUSTOMER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public String getApprovedCustomerRecordSize() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_TOTAL_APPROVED_RECORD_SIZE, parameterMap);
    }

    //rate application
    @Override
    public long rateApplication(ApplicationRating applicationRating) {
            oracleRepository.getProcResult(DBConstants.PKG_L30_CLIENT_RATINGS,
                DBConstants.PROC_L30_CLIENT_RATINGS_RATE_LOAN,
                applicationRating.getAttributeMap());
        return 0;
    }

    //get application rating
    @Override
    public List<ApplicationRating> getApplicationRating(ApplicationRating applicationRating) {
        return oracleRepository.getProcResult(DBConstants.PKG_L30_CLIENT_RATINGS,
                DBConstants.PROC_L30_CLIENT_RATINGS_GET_RATINGS,
                applicationRating.getAttributeMapForSearch(), rowMapperFactory.getRowMapper(RowMapperI.APPLICATION_RATING));
    }

    //Add questionnaire entry
    @Override
    public void addQuestionnaireEntry(QuestionnaireEntry questionnaireEntry) {
        oracleRepository.getProcResult(DBConstants.PKG_M06_RISK_WAIVER_QUESTIONNAIRE,
                DBConstants.PROC_M06_RISK_WAIVER_QUESTIONNAIRE_ADD,
                questionnaireEntry.getAttributeMap());
    }

    //get questionnaire entries
    @Override
    public List<QuestionnaireEntry> getQuestionnaireEntries() {
        return oracleRepository.getProcResult(DBConstants.PKG_M06_RISK_WAIVER_QUESTIONNAIRE,
                DBConstants.PROC_M06_RISK_WAIVER_QUESTIONNAIRE_GET,
                new HashMap<String, Object>(), rowMapperFactory.getRowMapper(RowMapperI.QUESTIONNAIRE_ENTRY));
    }

    // white List application
    @Override
    public String whiteListApplication(String applicationId, String customerId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", applicationId);
        parameterMap.put("pl01_customer_id", customerId);
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_WHITE_LIST_CUSTOMER, parameterMap);
    }

    @Override
    public UserAccountDetails getApplicationAccountDetails(String applicationId, String colletralID) {
        UserAccountDetails userAccountDetails = new UserAccountDetails();
        List<CashAcc> marginCashAccounts = getCashAccountsInCollateral(applicationId, colletralID, 0);
        List<CashAcc> lsfCashAccounts = getCashAccountsInCollateral(applicationId, colletralID, 1);
        List<TradingAcc> marginTradingAccounts = getTradingAccountInCollateral(applicationId, colletralID, 0);
        List<TradingAcc> lsfTradingAccounts = getTradingAccountInCollateral(applicationId, colletralID, 1);
        if (marginCashAccounts != null && marginCashAccounts.size() > 0) {
            userAccountDetails.setMarginCashAccount(marginCashAccounts.get(0).getAccountId());
        }
        if (lsfCashAccounts != null && lsfCashAccounts.size() > 0) {
            userAccountDetails.setLsfTypeCashAccount(lsfCashAccounts.get(0).getAccountId());
        }
        if (marginTradingAccounts != null && marginTradingAccounts.size() > 0) {
            userAccountDetails.setMarginTradingAccount(marginTradingAccounts.get(0).getAccountId());
        }
        if (lsfTradingAccounts != null && lsfTradingAccounts.size() > 0) {
            userAccountDetails.setLsfTypeTradingAccount(lsfTradingAccounts.get(0).getAccountId());
        }
        return userAccountDetails;
    }

    @Override
    public OrderContractCustomerInfo getOrderContractCustomerInfo(String applicationId) {
        OrderContractCustomerInfo userInfo = null;
        List<OrderContractCustomerInfo> infoList = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", applicationId);
        infoList = oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_ORD_CNTRCT_DATA, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_CONTRACT_USER_INFO));
        if (infoList != null && infoList.size() > 0) {
            userInfo = infoList.get(0);
        }
        return userInfo;
    }

    @Override
    public List<ApplicationStatus> applicationStatusSummary() {
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION,
                DBConstants.PROC_L01_GET_APP_STATUS_SUMMARY, new HashMap<String, Object>(), rowMapperFactory.getRowMapper(RowMapperI.APP_STATUS_SUMMARY));
    }

    @Override
    @CacheEvict(value = "murabahApplications", key = "#applicationId")
    public String updateLastProfitCycleDate(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", applicationId);
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_UPDATE_LAST_PROFIT_DATE, parameterMap);
    }
    public List<PhysicalDeliverOrder> getPhysicalDeliveryFromDB(){
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_PHYSICAL_DELIVER_LIST, null, rowMapperFactory.getRowMapper(RowMapperI.PHYSICAL_DELIVERY_LIST));
    }

    //Failed deposits for PO
    @Override
    public List<MurabahApplication> getDepositFailedApplications() {
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION,
                DBConstants.PROC_L01_GET_FAIlED_DEPOSITS, new HashMap<String, Object>(), rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }


    /*-----------------------Murabah Application Related Portfolio------------------*/
    @Override
    public String updateInitailAppPortfolio(Symbol symbol, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl20_app_id", applicationID);
        parameterMap.put("pl20_symbol_code", symbol.getSymbolCode());
        parameterMap.put("pl20_exchange", symbol.getExchange());
        parameterMap.put("pl20_previous_closed", symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed());
        parameterMap.put("pl20_available_qty", symbol.getAvailableQty());
        return oracleRepository.executeProc(DBConstants.PKG_L20_APP_PORTFOLIO, DBConstants.PROC_L20_ADD_INITIAL_PORTFOLIO, parameterMap);

    }

    @Override
    public List<Symbol> getInitialAppPortfolio(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl20_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L20_APP_PORTFOLIO, DBConstants.PROC_L20_GET_INITIAL_PORTFOLIO, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.INITIAL_PORTFOLIO));

    }

    /*-----------------------User Document Related------------------*/

    @Override
    public String updateCustomerDocument(Documents document, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        parameterMap.put("pl04_l03_doc_id", document.getId());
        parameterMap.put("pl04_orig_file_name", document.getOriginalFileName());
        parameterMap.put("pl04_uploaded_file_name", document.getOriginalFileName());
        parameterMap.put("pl04_path", document.getPath());
        parameterMap.put("pl04_extention", document.getExtension());
        parameterMap.put("pl04_uploaded_status", document.getUploadStatus());
        parameterMap.put("pl04_mime_type", document.getMimeType());
        oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_ADD_UPDATE_USER_DOCS, parameterMap);
            return null;
        }

    @Override
    public List<Documents> getComparedCustomerDocumentList(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_GET_USER_DOCS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.USER_DOCUMENTS));

    }
    
    @Override
    public List<Documents> getCustomerDocumentListByDocID(String applicationID, String documentID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        parameterMap.put("pl04_l03_doc_id", documentID);
        return oracleRepository.getProcResult(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_GET_USER_DOCS_BY_DOC_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.USER_APPLICATION_DOCUMENTS));
    }

    @Override
    public String removeCustomerDocs(String applicationID, String documentID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        parameterMap.put("pl04_l03_doc_id", documentID);
        return oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_REMOVE_USER_DOCS, parameterMap);

    }

    @Override
    public String updateDocumentMaster(Documents documents) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl03_doc_id", Integer.parseInt(documents.getId()));
        parameterMap.put("pl03_doc_name", documents.getDocumentName());
        parameterMap.put("pl03_is_required", documents.isRequired() ? 1 : 0);
        parameterMap.put("pl03_created_by", documents.getCreatedBy());
        parameterMap.put("pl03_is_global", documents.getIsGlobal());
        return oracleRepository.executeProc(DBConstants.PKG_l03_documents, DBConstants.PROC_L03_ADD, parameterMap);
    }

    @Override
    public List<Documents> getDocumentMasterList() {
        return oracleRepository.getProcResult(DBConstants.PKG_l03_documents, DBConstants.PROC_L03_GET_ALL, null, rowMapperFactory.getRowMapper(RowMapperI.DOCUMENT_MASTER_DOCS));
    }
    
    @Override
    public List<MurabahApplication> getDocumentRelatedAppsByDocID(int documentID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l03_doc_id", documentID);
        return oracleRepository.getProcResult(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_GET_APPS_BY_DOC_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));

    }

    @Override
    public String removeCustomDocFromApplication(int documentID, List<String> applicationList) {
        Map<String, Object> parameterMap;
        for (String applicationID : applicationList) {
            parameterMap = new HashMap<>();
            parameterMap.put("pl04_l03_doc_id", documentID);
            parameterMap.put("pl04_l01_app_id", applicationID);
            oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_REMOVE_CUSTOM_DOC_FROM_APP, parameterMap);

        }
        return "1";
    }
    
    @Override
    public String changeStatusDocumentMaster(int documentID, String approvedBy, int approveStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL03_DOC_ID", documentID);
        parameterMap.put("pL03_LVL1_APPROVED_BY", approvedBy);
        parameterMap.put("pL03_STATUS", approveStatus);
        return oracleRepository.executeProc(DBConstants.PKG_l03_documents, DBConstants.PROC_L03_CHANGE_STATUS, parameterMap);
    }

    @Override
    public String removeAdminDoc(String documentId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL03_DOC_ID", documentId);
        return oracleRepository.executeProc(DBConstants.PKG_l03_documents, DBConstants.PROC_L03_REMOVE_DOC, parameterMap);
    }

    @Override
    public String addCustomDocByAdmin(String applicationID, String documentID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        parameterMap.put("pl04_l03_doc_id", documentID);
        return oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_ADD_CUSTOM_DOC_BY_ADMIN, parameterMap);

    }

    @Override
    public List<MurabahApplication> getApplicationListForAdminDocUpload(int filterCriteria, String filterValue) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_filter_criteria", filterCriteria);
        parameterMap.put("pl01_filter_value", filterValue);
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_APP_ADMIN_DOC_UPLOAD, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));

    }

    /*-----------------------Admin Document Related------------------*/
    @Override
    public List<Documents> getApplicationAdminDocs(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl19_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L19_GET_UADMIN_DOCS_BY_APPLID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ADMIN_APPLICATION_DOCUMENTS));
    }
    
    @Override
    public String updateApplicationAdminDocs(Documents document, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl19_app_id", Integer.parseInt(applicationID));
        parameterMap.put("pl19_orig_file_name", document.getOriginalFileName());
        parameterMap.put("pl19_uploaded_file_name", document.getUploadedFileName());
        parameterMap.put("pl19_path", document.getPath());
        parameterMap.put("pl19_extention", document.getExtension());
        parameterMap.put("pl19_uploaded_user_id", document.getUploadedByUserID());
        parameterMap.put("pl19_uploaded_user_name", document.getUploadedBy());
        parameterMap.put("pl19_uploaded_level", Integer.parseInt(document.getUploadedLevel()));
        parameterMap.put("pl19_mime_type", document.getMimeType());
        parameterMap.put("pl19_uploaded_ip", document.getUploadedIP());
        parameterMap.put("pl19_file_category", document.getFileCategory());
        return oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L19_ADD_UPDATE_ADMIN_DOCS, parameterMap);
    }

    @Override
    public String removeApplicationAdminDocs(String applicationID, String documentID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl19_app_id", applicationID);
        parameterMap.put("pl19_id", documentID);
        return oracleRepository.executeProc(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L19_REMOVE_ADMIN_DOCS, parameterMap);

    }

    @Override
    public String addUpdateUserSession(String sessionID, String userID, int channelID, int sessionStatus, String ipAddress, String omsSessionID, int status) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pu01_session_id", sessionID);
        parameterMap.put("pu01_user_id", userID);
        parameterMap.put("pu01_chanel_id", channelID);
        parameterMap.put("pu01_session_status", sessionStatus);
        parameterMap.put("pu01_ip_address", ipAddress);
        parameterMap.put("pu01_oms_session_id", omsSessionID);
        parameterMap.put("pu01_status", status);

        return oracleRepository.executeProc(DBConstants.PKG_U01_USER_SESSION, DBConstants.PROC_U01_CREATE_UPDATE_SESSION, parameterMap);
    }
    
    @Override
    public List<UserSession> getUserSession(String userID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pu01_user_id", userID);
        return oracleRepository.getProcResult(DBConstants.PKG_U01_USER_SESSION, DBConstants.PROC_U01_GET_USER_SESSION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.USER_SESSION));

    }

    @Override
    public String updateSessionStateWithOMSResponse(String omsSessionID, int status) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pu01_oms_session_id", omsSessionID);
        parameterMap.put("pu01_status", status);
        return oracleRepository.executeProc(DBConstants.PKG_U01_USER_SESSION, DBConstants.PROC_U01_UPDATE_SESSION_STATE, parameterMap);


    }

    /*------------------------Application Status Flow Related-------------------*/

    @Override
    public List<Status> getApplicationStatusFlow() {
        return oracleRepository.getProcResult(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.PROC_M02_GET_APP_STATE_FLOW, null, rowMapperFactory.getRowMapper(RowMapperI.APP_STATE_FLOW));
    }
    
    @Override
    public List<LiquidityType> getLiquidityTypes() {
        String GET_LIQUIDITY_TYPES="select * from l10_liquidity_type";
        return oracleRepository.query(GET_LIQUIDITY_TYPES, null, RowMapperI.LIQUIDITY_TYPES.toString());
    }

    @Override
    public List<Tenor> getTenorList() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_GET_ALL_TENOR, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.TENOR));
    }

    @Override
    public String updateTenor(Tenor tenor) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p15_duration", tenor.getDuration());
        parameterMap.put("p15_profit_percent", tenor.getProfitPercentage());
        parameterMap.put("p15_tenor_id", 1);
        parameterMap.put("p15_created_by", tenor.getCreatedBy());
        return oracleRepository.executeProc(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_ADD_UPDATE_TENOR, parameterMap);

    }
    
    @Override
    public String changeStatusTenor(int duration, String approvedBy, int status) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p15_duration", duration);
        parameterMap.put("pL15_LVL1_APPROVED_BY", approvedBy);
        parameterMap.put("pL15_STATUS", status);
        return oracleRepository.executeProc(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_L15_CHANGE_STATUS, parameterMap);
    }

    @Override
    public void deleteAllTenors() {
        Map<String, Object> parameterMap = new HashMap<>();
        oracleRepository.executeProc(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_DELETE_ALL_TENOR, parameterMap);
    }

    @Override
    public String updateMarginabilityGroup(MarginabilityGroup marginabilityGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl11_marginability_grp_id", marginabilityGroup.getId());
        parameterMap.put("pl11_group_name", marginabilityGroup.getGroupName());
        parameterMap.put("pl11_status", marginabilityGroup.getStatus());
        parameterMap.put("pl11_created_by", marginabilityGroup.getCreatedBy());
        parameterMap.put("pl11_approved_by", marginabilityGroup.getApprovedBy());
        parameterMap.put("pl11_is_default", marginabilityGroup.getIsDefault());
        parameterMap.put("pl11_additional_details", marginabilityGroup.getAdditionalDetails());
        parameterMap.put("pl11_global_marginability_perc", marginabilityGroup.getGlobalMarginablePercentage());
        return oracleRepository.executeProc(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_L11_ADD_UPDTE, parameterMap);
    }

    @Override
    public String updateMarginabilityGroupLiqTypes(MarginabilityGroup marginabilityGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        for (LiquidityType liq : marginabilityGroup.getMarginabilityList()) {
            parameterMap.clear();
            parameterMap.put("pl17_marginability_grp_id", marginabilityGroup.getId());
            parameterMap.put("pl17_liquid_id", liq.getLiquidId());
            parameterMap.put("pl17_marginability_perc", liq.getMarginabilityPercent());
            oracleRepository.executeProc(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_L11_ADD_LIQUIDITY_TYPE, parameterMap);
        }
        return "1";
    }
    
    @Override
    public String updateSymbolMarginabilityPercentages(MarginabilityGroup marginabilityGroup) {

        Map<String, Object> parameterMap = new HashMap<>();

        for (SymbolMarginabilityPercentage percentage : marginabilityGroup.getMarginableSymbols()) {
            parameterMap.clear();
            parameterMap.put("pl35_marginability_grp_id", marginabilityGroup.getId());
            parameterMap.put("pl35_symbol_code", percentage.getSymbolCode());
            parameterMap.put("pl35_exchange", percentage.getExchange());
            parameterMap.put("pl35_marginability_percentage", percentage.getMarginabilityPercentage());
            parameterMap.put("pl35_is_marginable", percentage.getIsMarginable());

            oracleRepository.executeProc(DBConstants.PKG_L35_SYMBOL_MARGINABILITY,
                                      DBConstants.PROC_L35_ADD_SYMBOL_MARGINABILITY_PERCENTAGE, parameterMap);
        }

        return "1";
    }

    @Override
    public String updateSymbolMarginability(List<Map<String, Object>> marginabilityGroups) {

        Map<String, Object> parameterMap = new HashMap<>();

        for(Map<String, Object> marginabilityGroup : marginabilityGroups) {
            parameterMap.clear();
            parameterMap.put("pl35_marginability_grp_id", marginabilityGroup.get("groupId"));
            parameterMap.put("pl35_symbol_code", marginabilityGroup.get("symbolCode"));
            parameterMap.put("pl35_exchange", marginabilityGroup.get("exchange"));
            parameterMap.put("pl35_marginability_percentage", marginabilityGroup.get("marginabilityPercentage"));

            oracleRepository.executeProc(DBConstants.PKG_L35_SYMBOL_MARGINABILITY,
                                      DBConstants.PROC_L35_UPDATE_SYMBOL_GROUPS, parameterMap);
        }

        return "1";
    }
    
    @Override
    public List<MarginabilityGroup> getMarginabilityGroups() {
        return oracleRepository.<MarginabilityGroup>getProcResult(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_GET_ALL_GROUPS_L11, null, rowMapperFactory.getRowMapper(RowMapperI.MARGINABILITY_GROUPS));
    }

    @Override
    public MarginabilityGroup getMarginabilityGroup(String groupId) {
        try {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl11_marginability_grp_id", groupId);
        List<MarginabilityGroup> marginabilityGroupList =
                    oracleRepository.<MarginabilityGroup>getProcResult(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_GET_MARGIN_GROUP_BY_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MARGINABILITY_GROUPS));
        if (marginabilityGroupList != null) {
            return marginabilityGroupList.size() > 0 ? marginabilityGroupList.get(0) : null;
        } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error getting marginability group by ID: " + groupId, e);
            return null;
        }
    }

    @Override
    public List<MarginabilityGroup> getDefaultMarginGroups() {
        return oracleRepository.<MarginabilityGroup>getProcResult(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_GET_DEFAULT_GROUP_L11, null, rowMapperFactory.getRowMapper(RowMapperI.MARGINABILITY_GROUPS));
    }

    @Override
    public List<LiquidityType> getMarginabilityGroupLiquidTypes(String groupId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl17_marginability_grp_id", groupId);
        return oracleRepository.getProcResult(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_GET_LIQUID_TYPES_IN_GROUP_L11, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.LIQUIDITY_TYPES));
    }

    @Override
    public List<SymbolMarginabilityPercentage> getMarginabilityPercByGroup(String groupId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("groupId", groupId);
        return oracleRepository.getProcResult(DBConstants.PKG_L35_SYMBOL_MARGINABILITY, DBConstants.PROC_L35_SYMBOL_PERC_BY_GROUP,parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_MARGINABILITY_PERCENTAGE));
    }

    @Override
    public LiquidityType getSymbolLiquidityType(Symbol symbol) {
        LiquidityType liquidityType = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl08_symbol_code", symbol.getSymbolCode());
        parameterMap.put("pl08_exchange", symbol.getExchange());
        List<LiquidityType> list = oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_GET_SYMBOL_LIQUIDITY_TYPE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.LIQUIDITY_TYPES));
        if (list.size() == 0) {
            liquidityType = new LiquidityType();
            liquidityType.setLiquidId(1);
            liquidityType.setLiquidName("Liquid");
            liquidityType.setMarginabilityPercent(100.0);
        } else {
            liquidityType = list.get(0);
        }
        return liquidityType;
    }

    @Override
    public double getSymbolMarginabilityPerc(String symbol, String exchange, String appId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p08_symbol_code", symbol);
        parameterMap.put("p08_exchange", exchange);
        parameterMap.put("pl08_app_id", appId);

        List<SymbolMarginabilityPercentage> result = oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL,
                                                                       DBConstants.PROC__l08_GET_SYMBOL_MARGINABILITY_PERC,
                                                                       parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_MARGINABILITY_PERCENTAGE));

        if(result != null && !result.isEmpty()) {
                return result.get(0).getMarginabilityPercentage();
            }

        return 0;
    }


    @Override
    public List<Symbol> loadSymbols(String exchange, String symbolCode) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl08_symbol_code", symbolCode);
        parameterMap.put("pl08_exchange", exchange);
        return oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_GET_SYMBOL_LIQUIDITY_TYPE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));
    }

    @Override
    @Cacheable(value = "allSymbols", unless = "#result == null")
    public List<Symbol> loadAllSymbols() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_l08_GET_ALL_SYMBOLS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));

    }

    @Override
    public List<SymbolMarginabilityPercentage> getSymbolMarginabilityGroups(String symbolCode, String exchange) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl35_symbol_code", symbolCode);
        parameterMap.put("pl35_exchange", exchange);
        return oracleRepository.getProcResult(DBConstants.PKG_L35_SYMBOL_MARGINABILITY, DBConstants.PROC_L35_GET_SYMBOL_GROUPS,
                                           parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_MARGINABILITY_PERCENTAGE));
    }

    @Override
    public List<Symbol> loadSymbolsForClassification() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_l08_GET_ALL_SYMBOLS_CLASSF, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));

    }

    @Override
    public List<SymbolMarginabilityPercentage> getSymbolMarginabilityPercentage(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("appId", applicationId);
        return oracleRepository.getProcResult(DBConstants.PKG_L35_SYMBOL_MARGINABILITY, DBConstants.PROC_L35_GET_SYMBOL_MARGINABILITY_PERCENTAGE,
                                           parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_MARGINABILITY_PERCENTAGE));
    }

    @Override
    public String updateCommissionStructure(CommissionStructure commissionStructure) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm10_id", commissionStructure.getId());
        parameterMap.put("pm10_from_value", commissionStructure.getFromValue());
        parameterMap.put("pm10_to_value", commissionStructure.getToValue());
        parameterMap.put("pm10_flat_amount", commissionStructure.getFlatAmount());
        parameterMap.put("pm10_percentage", commissionStructure.getPercentageAmount());
        parameterMap.put("pm10_sibour_rate", commissionStructure.getSibourRate());
        parameterMap.put("pm10_libour_rate", commissionStructure.getLibourRate());
        parameterMap.put("pm10_created_user_id", commissionStructure.getCreatedUserId());
        parameterMap.put("pm10_created_user", commissionStructure.getCreatedUserName());
        return oracleRepository.executeProc(DBConstants.PKG_M10_COMMISSION_STRUCTURE_PKG, DBConstants.PROC_M10_ADD_EDIT, parameterMap);
    }

    @Override
    public List<CommissionStructure> getCommissionStructure() {
            return oracleRepository.getProcResult(DBConstants.PKG_M10_COMMISSION_STRUCTURE_PKG, DBConstants.PROC_M10_GET_ALL, null, rowMapperFactory.getRowMapper(RowMapperI.COMMISSION_STRUCTURE));
    }

    @Override
    public String deleteCommissionStructure(String pm10id) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm10_id", pm10id);
        return oracleRepository.executeProc(DBConstants.PKG_M10_COMMISSION_STRUCTURE_PKG, DBConstants.PROC_M10_DELETE, parameterMap);
    }

    @Override
    public String moveToCloseDeuToPONotAcceptance(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", applicationID);
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.M02_SET_CLOSED_STATE_SYSTEM, parameterMap);

    }

    @Override
    public String updateGlobalParameters(GlobalParameters globalParameters) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p01_max_guidance_limit", globalParameters.getMaxGuidanceLimit());
        parameterMap.put("p01_min_guidance_limit", globalParameters.getMinGuidanceLimit());
        parameterMap.put("p01_ftv_ratio_for_operative", globalParameters.getFtvForOperativeLimit());
        if (globalParameters.getAllowInstalmentSettlement()) {
            parameterMap.put("p01_allow_instalment_settle", 1);
        } else {
            parameterMap.put("p01_allow_instalment_settle", 0);
        }
        if (globalParameters.getOperatingLimitType()) {
            parameterMap.put("p01_operating_limit_type", 1);
        } else {
            parameterMap.put("p01_operating_limit_type", 0);
        }
        parameterMap.put("p01_max_contribution", globalParameters.getScriptMaxContribution());
        parameterMap.put("p01_notify_days_bf_review", globalParameters.getNoOfDaysPriorNotifyReviewDate());
        if (globalParameters.getShariaSymbolsAsCollateral()) {
            parameterMap.put("p01_sharia_symbol_as_collatera", 1);
        } else {
            parameterMap.put("p01_sharia_symbol_as_collatera", 0);
        }
        parameterMap.put("p01_documentation_fee_pecent", globalParameters.getDocumentationFeePercentage());

        if (globalParameters.getUtilizeCustomerCashFirst()) {
            parameterMap.put("p01_utilize_cash_first", 1);
        } else {
            parameterMap.put("p01_utilize_cash_first", 0);
        }
        parameterMap.put("p01_first_margine_call_percent", globalParameters.getFirstMarginCall());
        parameterMap.put("p01_second_margine_call_percen", globalParameters.getSecondMarginCall());
        parameterMap.put("p01_liquid_margine_call_perent", globalParameters.getLiquidationCall());
        if (globalParameters.getPreFunded()) {
            parameterMap.put("p01_is_prefunded", 1);
        } else {
            parameterMap.put("p01_is_prefunded", 0);
        }
        parameterMap.put("p01_no_of_calling_attempts", globalParameters.getNoOfCallingAttemptsPerDay());
        parameterMap.put("p01_rimind_days_prior_to_payem", globalParameters.getNoOfDaysPriorRemindingThePayment());
        parameterMap.put("p01_days_wait_before_liquidati", globalParameters.getNoOfDaysWaitsBeforeLiquidation());
        parameterMap.put("p01_symbol_revalue_interval", globalParameters.getSymbolReValuationInterval());
        parameterMap.put("p01_alert_prior_to_fd_expiry", globalParameters.getAlertCustomerPriorToFDExpiry());
        if (globalParameters.getPriorityCashACForSettlement()) {
            parameterMap.put("p01_settling_cash_acc", 1);
        } else {
            parameterMap.put("p01_settling_cash_acc", 0);
        }
        if (globalParameters.getMurabahaOTP()) {
            parameterMap.put("p01_enable_otp", 1);
        } else {
            parameterMap.put("p01_enable_otp", 0);
        }
        parameterMap.put("p01_margine_calls_per_day", globalParameters.getNoOfMarginCallsPerDay());
        parameterMap.put("p01_base_currency", globalParameters.getBaseCurrency());
        parameterMap.put("p01_profit_calc_method", globalParameters.getProfitCalculateMethode());
        parameterMap.put("p01_client_code", globalParameters.getClientCode());
        parameterMap.put("pm01_default_exchange", globalParameters.getDefaultExchange());
        parameterMap.put("pM01_ORDER_ACC_PRIORITY", globalParameters.getOrderAccPriority());
        parameterMap.put("pM01_INSTITUTION_TRADING_ACC", globalParameters.getInstitutionTradingAcc());
        parameterMap.put("pm01_admin_fee", globalParameters.getAdministrationFee());
        parameterMap.put("pm01_decimal_count", globalParameters.getNumberOfDecimalPlaces());
        parameterMap.put("pm01_time_gap_calling_atmpt", globalParameters.getTimeGapBetweenCallingAttempts());
        parameterMap.put("pm01_admin_fee_percentage", globalParameters.getAdministrationFeePercent());
        parameterMap.put("pm01_institution_cash_acc", globalParameters.getInstitutionCashAccount());
        parameterMap.put("pm01_max_symbol_cnt", globalParameters.getMaximumNumberOfSymbols());
        parameterMap.put("pm01_collatreal_to_margin_perc", globalParameters.getColletralToMarginPercentage());
        parameterMap.put("pm01_market_open_time", globalParameters.getMarketOpenTime());
        parameterMap.put("pm01_market_closed_time", globalParameters.getMarketClosedTime());
        parameterMap.put("pm01_sima_charges", globalParameters.getSimaCharges());
        parameterMap.put("pm01_transfer_charges", globalParameters.getTransferCharges());
        parameterMap.put("pm01_agreed_limit", globalParameters.getAgreedLimit());
        parameterMap.put("pm01_max_brokerage_limit", globalParameters.getMaxBrokerageLimit());
        parameterMap.put("pm01_vat_amount", globalParameters.getVatPercentage());
        parameterMap.put("pm01_max_active_contracts", globalParameters.getMaxNumberOfActiveContracts());
        parameterMap.put("pm01_share_admin_fee",globalParameters.getShareAdminFee());
        parameterMap.put("pm01_comodity_admin_fee",globalParameters.getComodityAdminFee());
        parameterMap.put("pm01_comodity_fixed_fee",globalParameters.getComodityFixedFee());
        parameterMap.put("pm01_share_fixed_fee",globalParameters.getShareFixedFee());
        parameterMap.put("pm01_min_rollover_ratio",globalParameters.getMinRolloverRatio());
        parameterMap.put("pm01_min_rollover_period",globalParameters.getMinRolloverPeriod());
        parameterMap.put("pm01_max_rollover_period",globalParameters.getMaxRolloverPeriod());
        parameterMap.put("pm01_grace_per_commodity_sell",globalParameters.getGracePeriodforCommoditySell());
        parameterMap.put("pm01_institution_invest_acc",globalParameters.getInstitutionInvestAccount());

        return oracleRepository.executeProc(DBConstants.PKG_M01_SYS_PARAS, DBConstants.PROC_ADD_UPDATE_SYS_PARAS, parameterMap);
    }

    @Override
    public List<GlobalParameters> getGlobalParameters() {
        Map<String, Object> parameterMap = new HashMap<>();
                return oracleRepository.getProcResult(DBConstants.PKG_M01_SYS_PARAS, DBConstants.PROC_ADD_GET_SYS_PARAS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYS_PARAS));
    }

    @Override
    public List<Map<String, Object>> getGlobalParametersData() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_M01_SYS_PARAS, 
                DBConstants.PROC_ADD_GET_SYS_PARAS, 
                parameterMap, 
                rowMapperFactory.getRowMapper(RowMapperI.SYS_PARAS));
    }

    @Override
    public String changePublicAccessState(int state) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm01_lsf_public_access_enabled", state);
        return oracleRepository.executeProc(DBConstants.PKG_M01_SYS_PARAS, DBConstants.PROC_M01_ENABLE_LSF, parameterMap);

    }

    @Override
    public List<Symbol> getWishListSymbols(String applicationId, String exchange) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl13_l01_app_id", applicationId);
        parameterMap.put("pl13_l08_exchange", exchange);
        return oracleRepository.getProcResult(DBConstants.PKG_L13_SYMBOL_WISH_LIST, DBConstants.PROC_GET_WISH_LIST_SYMBOLS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));
    }

    @Override
    @CacheEvict(value = "allSymbols")
    public String updateSymbol(Symbol symbol) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p08_symbol_code", symbol.getSymbolCode());
        parameterMap.put("p08_exchange", symbol.getExchange());
        parameterMap.put("p08_short_desc", symbol.getShortDescription());
        parameterMap.put("p08_previous_closed", symbol.getPreviousClosed());
        parameterMap.put("p08_available_qty", symbol.getAvailableQty());
        parameterMap.put("p08_market_value", symbol.getMarketValue());
        parameterMap.put("pl08_ltp", symbol.getLastTradePrice());
        parameterMap.put("p08_short_desc_ar", symbol.getShortDescriptionAR());
        // parameterMap.put("pl08_allowed_for_collateral", 1);
        parameterMap.put("pl08_instrument_type", symbol.getInstrumentType());
        parameterMap.put("pl08_security_type", symbol.getSecurityType());
        return oracleRepository.executeProc(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_ADD_UPDATE_SYMBOL, parameterMap);
    }

    @Override
    public String updateLiquidType(Symbol symbol) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p08_symbol_code", symbol.getSymbolCode());
        parameterMap.put("p08_exchange", symbol.getExchange());
        parameterMap.put("p08_l10_liquid_id", symbol.getLiquidityType().getLiquidId());
        parameterMap.put("pl08_allowed_for_collateral", symbol.getAllowedForCollateral());
        parameterMap.put("pl08_concentration_type", symbol.getConcentrationType().getLiquidId());
        return oracleRepository.executeProc(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_ADD_UPDATE_LIQUID_TYPE, parameterMap);
    }

    @Override
    public Tenor getTenor(int tenorId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl15_tenor_id", tenorId);
        List<Tenor> list = oracleRepository.getProcResult(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_GET_TENOR, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.TENOR));
        if (list != null) {
            return list.size() > 0 ? list.get(0) : new Tenor();
        } else {
            return new Tenor();
        }
    }

    @Override
    public boolean removeTenorGroup(String tenorID) {
        boolean response = false;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl15_tenor_id", tenorID);
        if (oracleRepository.executeProc(DBConstants.PKG_L15_TENOR_PKG, DBConstants.PROC_L15_REMOVE_TENOR_GROUP, parameterMap).equalsIgnoreCase("1")) {
            response = true;
        }

        return response;
    }

    @Override
    public String addEditCollaterals(MApplicationCollaterals mApplicationCollaterals) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL05_L01_APP_ID", mApplicationCollaterals.getApplicationId());
        parameterMap.put("pl05_collateral_id", mApplicationCollaterals.getId());
        parameterMap.put("pl05_net_total_collat", Float.valueOf(String.valueOf(mApplicationCollaterals.getNetTotalColleteral())));
        parameterMap.put("pl05_total_cash_collat", Float.valueOf(String.valueOf(mApplicationCollaterals.getTotalCashColleteral())));
        parameterMap.put("pl05_approved_limit_amt", Float.valueOf(String.valueOf(mApplicationCollaterals.getApprovedLimitAmount())));
        parameterMap.put("pl05_utilized_limit_amt", Float.valueOf(String.valueOf(mApplicationCollaterals.getUtilizedLimitAmount())));
        parameterMap.put("pl05_operative_limit_amt", Float.valueOf(String.valueOf(mApplicationCollaterals.getOpperativeLimitAmount())));
        parameterMap.put("pl05_rem_operative_limit_amt", Float.valueOf(String.valueOf(mApplicationCollaterals.getRemainingOperativeLimitAmount())));
        parameterMap.put("pl05_outstanding_amt", Float.valueOf(String.valueOf(mApplicationCollaterals.getOutstandingAmount())));
        parameterMap.put("pl05_is_ready_for_collat_trans", mApplicationCollaterals.isReadyForColleteralTransfer() ? 1 : 0);
        parameterMap.put("pl05_ftv", mApplicationCollaterals.getFtv());
        parameterMap.put("pl05_first_margin_call", mApplicationCollaterals.isFirstMargineCall() ? 1 : 0);
        parameterMap.put("pl05_second_margin_call", mApplicationCollaterals.isSecondMargineCall() ? 1 : 0);
        parameterMap.put("pl05_liquidation_call", mApplicationCollaterals.isLiqudationCall() ? 1 : 0);
        parameterMap.put("pl05_total_external_collat", mApplicationCollaterals.getTotalExternalColleteral());
        parameterMap.put("pl05_total_portfolio_collat", mApplicationCollaterals.getTotalPFColleteral());
        parameterMap.put("pl05_margine_call_attempts", mApplicationCollaterals.getMargineCallAtempts());
        parameterMap.put("pl05_block_amount", mApplicationCollaterals.getBlockAmount());
        parameterMap.put("pl05_margine_call_date", mApplicationCollaterals.getMargineCallDate());
        parameterMap.put("pl05_liquidate_call_date", mApplicationCollaterals.getLiquidateCallDate());
        return oracleRepository.executeProc(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_GET_ADD_UPDATE_COLLATERALS, parameterMap);
    }


    @Override
    public String addInitialCollaterals(MApplicationCollaterals mApplicationCollaterals) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl05_l01_app_id", mApplicationCollaterals.getApplicationId());
        parameterMap.put("pl05_initial_cash_collateral", Float.valueOf(String.valueOf(mApplicationCollaterals.getInitialCashCollaterals())));
        parameterMap.put("pl05_initial_pf_collateral", Float.valueOf(String.valueOf(mApplicationCollaterals.getInitialPFCollaterals())));
        return oracleRepository.executeProc(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_L05_UPDATE_INITIAL_COLLATERALS, parameterMap);
    }

    @Override
    public String addEditCompleteCollateral(MApplicationCollaterals mApplicationCollaterals) {
        String collateralId = this.addEditCollaterals(mApplicationCollaterals);
        double totalPFValue = 0.0;
        if (collateralId != null) {
            mApplicationCollaterals.setId(collateralId);
            // saving Cash accounts in Collaterals
            for (CashAcc cashAcc : mApplicationCollaterals.getCashAccForColleterals()) {
                /*if(cashAcc.getAmountAsColletarals()>0){
                    cashAcc.setCollateralId(collateralId);
                    this.updateCashAccount(cashAcc);
                }*/
                cashAcc.setCollateralId(collateralId);
                this.updateCashAccount(cashAcc);

            }
            // saving LSF Type Cash Accounts
            if (mApplicationCollaterals.getLsfTypeCashAccounts() != null) {
                for (CashAcc cashAcc : mApplicationCollaterals.getLsfTypeCashAccounts()) {
                    cashAcc.setCollateralId(collateralId);
                    this.updateCashAccount(cashAcc);
                }
            }

            // save Trading Accounts
            if (mApplicationCollaterals.getTradingAccForColleterals() != null && mApplicationCollaterals.getTradingAccForColleterals().size() > 0) {
                for (TradingAcc tradingAcc : mApplicationCollaterals.getTradingAccForColleterals()) {
                    if (tradingAcc.getSymbolsForColleteral() != null) {
                        if (tradingAcc.getSymbolsForColleteral().size() > 0) {
                            tradingAcc.setCollateralId(collateralId);
                            this.updateTradingAccount(tradingAcc);
                            // saving Symbols in TRading Account
                            for (Symbol symbol : tradingAcc.getSymbolsForColleteral()) {
                                if ((symbol.getColleteralQty() > 0) || (symbol.getTransferedQty() > 0)) {
                                    this.updateTradingAccountSymbols(tradingAcc, symbol);
                                    totalPFValue = totalPFValue + symbol.getContibutionTocollateral();
                                }
                            }
                        }
                    }
                }
                mApplicationCollaterals.setTotalPFColleteral(totalPFValue);
                this.addEditCollaterals(mApplicationCollaterals);
            }

            // save LSF Type Trading Accounts
            if (mApplicationCollaterals.getLsfTypeTradingAccounts() != null) {
                for (TradingAcc tradingAcc : mApplicationCollaterals.getLsfTypeTradingAccounts()) {
                    tradingAcc.setCollateralId(collateralId);
                    this.updateTradingAccount(tradingAcc);
                }
            }

        }
        return collateralId;
    }

    @Override
    public String updateCollateralWithCompleteTradingAcc(MApplicationCollaterals mApplicationCollaterals) {

        String collateralId = this.addEditCollaterals(mApplicationCollaterals);
        if (collateralId != null) {
            // save LSF Type Trading Accounts
            if (mApplicationCollaterals.getLsfTypeTradingAccounts() != null) {
                for (TradingAcc tradingAcc : mApplicationCollaterals.getLsfTypeTradingAccounts()) {
                    tradingAcc.setCollateralId(collateralId);
                    this.updateTradingAccount(tradingAcc);

                    // saving Symbols in TRading Account
                    if (tradingAcc.getSymbolsForColleteral() != null) {
                        for (Symbol symbol : tradingAcc.getSymbolsForColleteral()) {
                            tradingAcc.setApplicationId(mApplicationCollaterals.getApplicationId());
                            this.updateTradingAccountSymbols(tradingAcc, symbol);
                        }
                    }
                }
            }
        }
        return collateralId;
    }

    @Override
    public String changeStatusCollateral(MApplicationCollaterals mApplicationCollaterals) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL05_L01_APP_ID", mApplicationCollaterals.getApplicationId());
        parameterMap.put("pL05_COLLATERAL_ID", mApplicationCollaterals.getId());
        parameterMap.put("pL05_STATUS_CHANGE_BY", mApplicationCollaterals.getStatusChangedBy());
        parameterMap.put("pL05_STATUS", mApplicationCollaterals.getStatus());
        parameterMap.put("p_message", mApplicationCollaterals.getStatusMessage());
        parameterMap.put("p_status_changed_ip", mApplicationCollaterals.getIpAddress());

        return oracleRepository.executeProc(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_L05_CHANGE_STATUS, parameterMap);
    }

    @Override
    public List<MApplicationCollaterals> getApplicationCollateralFtvList() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_GET_COLLATERALS_FTV_LIST, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.COLLATERALS));
    }

    @Override
    public String updateSymbolTransferState(String tradingAccountID, String applicationID, String symbolCode, int state) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl09_l06_trading_acc_id", tradingAccountID);
        parameterMap.put("pl09_l01_app_id", applicationID);
        parameterMap.put("pl09_l08_symbol_code", symbolCode);
        parameterMap.put("pl09_status", state);
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L09_UPDATE_SYMBOL_STATE, parameterMap);

    }

    @Override
    public String addPurchaseOrder(PurchaseOrder purchaseOrder) {
        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(purchaseOrder.getId()));
        parameterMap.put("pL14_APP_ID", Integer.parseInt(purchaseOrder.getApplicationId()));
        parameterMap.put("pL14_CUSTOMER_ID", purchaseOrder.getCustomerId());
        parameterMap.put("pL14_ORD_VALUE", purchaseOrder.getOrderValue());
        parameterMap.put("pL14_ORD_SETTLEMENT_AMOUNT", purchaseOrder.getOrderSettlementAmount());
        parameterMap.put("pL14_SETTLEMENT_DATE", purchaseOrder.getSettlementDate());
        parameterMap.put("pL14_TRADING_ACCOUNT", purchaseOrder.getTradingAccount());
        parameterMap.put("pL14_EXCHANGE", purchaseOrder.getExchange());
        parameterMap.put("pL14_SETTLEMENT_ACCOUNT", purchaseOrder.getSettlementAccount());
        parameterMap.put("pL14_IS_ONE_TIME_SETTLEMENT", purchaseOrder.isOneTimeSettlement() ? 1 : 0);
        parameterMap.put("pL14_INSTALLMENT_FREQUENCY", purchaseOrder.getInstallmentFrequency());
        parameterMap.put("pL14_SET_DURATION_MONTHS", purchaseOrder.getSettlementDurationInMonths());
        parameterMap.put("pL14_L15_TENOR_ID", Integer.parseInt(purchaseOrder.getTenorId()));
        parameterMap.put("pL14_PROFIT_AMOUNT", purchaseOrder.getProfitAmount());
        parameterMap.put("pL14_SIBOUR_AMOUNT", purchaseOrder.getSibourAmount());
        parameterMap.put("pL14_LIBOUR_AMOUNT", purchaseOrder.getLibourAmount());
        parameterMap.put("pL14_PROFIT_PERCENTAGE", purchaseOrder.getProfitPercentage());
        String key = oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_ADD_ORDER, parameterMap);
        if (key != null) {
            //save Installments
            if (purchaseOrder.getInstallments() != null) {
                if (purchaseOrder.getInstallments().size() > 0) {
                    for (Installments installment : purchaseOrder.getInstallments()) {
                        installment.setOrderId(key);
                        this.upadtePurchaseOrderInstallments(installment);
                    }
                }
            }
            // save Symblol List
            if (purchaseOrder.getSymbolList() != null){
                for (Symbol symbol : purchaseOrder.getSymbolList()) {
                    if (symbol.getPercentage() > 0) {
                        this.updatePurchaseOrderSymbols(symbol, key);
                    }
                }
            } else if (purchaseOrder.getCommodityList() != null) {
                for (Commodity commodity : purchaseOrder.getCommodityList()) {
                    if (commodity.getPercentage() > 0) {
                        this.updatePurchaseOrderCommodity(commodity, key);
                    }
                }
            }
        }
        return key;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrder(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL14_APP_ID", applicationId);
        MurabahApplication application = getMurabahApplication(applicationId);
        if (application == null) {
            log.error("Application with ID " + applicationId + " not found.");
            return Collections.emptyList();
        }
        List<PurchaseOrder> purchaseOrderList = oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_GET_ALL_ORDER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));

        log.info("getAllPurchaseOrder finance method : "+application.getFinanceMethod()+ " pL14_APP_ID : "+applicationId);
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            purchaseOrder.setInstallments(this.getPurchaseOrderInstallments(purchaseOrder.getId()));
            if (application.getFinanceMethod().equalsIgnoreCase("1")){
                purchaseOrder.setSymbolList(this.getPurchaseOrderSymbols(purchaseOrder.getId()));
            } else if (application.getFinanceMethod().equalsIgnoreCase("2")) {
                purchaseOrder.setCommodityList(this.getPurchaseOrderCommodities(purchaseOrder.getId()));
            }
        }
        return purchaseOrderList;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrderforCommodity(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL14_APP_ID", applicationId);
        List<PurchaseOrder> purchaseOrderList = oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_GET_ALL_ORDER_FOR_COMMODITY, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));
        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            purchaseOrder.setInstallments(this.getPurchaseOrderInstallments(purchaseOrder.getId()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date apprvDate = new java.util.Date();
            try {
                apprvDate = sdf.parse(purchaseOrder.getApprovedDate());
            }
            catch (ParseException e){
                log.error("Error while passing date :"+e.getMessage());
            }

            purchaseOrder.setCommodityList(this.getPurchaseOrderCommodities(purchaseOrder.getId()));
            purchaseOrder.setRemainTimeToSell(LSFUtils.getRemainTimeForGracePrd(apprvDate,GlobalParameters.getInstance().getGracePeriodforCommoditySell()));
        }
        return purchaseOrderList;
    }
    @Override
    public List<PurchaseOrder> getPOForReminding() {
        Map<String, Object> parameterMap = new HashMap<>();
        List<PurchaseOrder> purchaseOrderList = oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_GET_APPLICATION_REMINDER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));
        return purchaseOrderList;
    }

    @Override
    public PurchaseOrder getSinglePurchaseOrder(String orderId) {
        PurchaseOrder po = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", orderId);
        List<PurchaseOrder> purchaseOrderList = oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_GET_ORDER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));

        if (purchaseOrderList != null) {
            if (purchaseOrderList.size() > 0) {
                po = purchaseOrderList.get(0);
                MurabahApplication application = getMurabahApplication(po.getApplicationId());
                po.setInstallments(this.getPurchaseOrderInstallments(po.getId()));
                if (application.getFinanceMethod().equalsIgnoreCase("2")){
                    po.setCommodityList(this.getPurchaseOrderCommodities(po.getId()));
                }
                else if (application.getFinanceMethod().equalsIgnoreCase("1")) {
                    po.setSymbolList(this.getPurchaseOrderSymbols(po.getId()));
                }
            }
        }
        return po;
    }
    
    @Override
    public String getNextAvailablePOID() {
        Map<String, Object> parameterMap = new HashMap<>();
        String key = oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_GET_AVAILABLE_POID, parameterMap);
        return key;
    }

    @Override
    public String updateCashAccount(CashAcc cashAcc) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL07_CASH_ACC_ID", cashAcc.getAccountId());
        parameterMap.put("pL07_CURRENCY_CODE", cashAcc.getCurrencyCode());
        parameterMap.put("pL07_CASH_BALANCE", cashAcc.getCashBalance());
        parameterMap.put("pL07_AMT_AS_COLLAT", cashAcc.getAmountAsColletarals());
        parameterMap.put("pL07_AMT_TRANSFERRED", cashAcc.getAmountTransfered());
        parameterMap.put("pL07_IS_LSF_TYPE", cashAcc.isLsfType() ? 1 : 0);
        parameterMap.put("pL07_L05_COLLATERAL_ID", cashAcc.getCollateralId());
        parameterMap.put("pL07_L01_APP_ID", cashAcc.getApplicationId());
        parameterMap.put("pL07_BLOCK_REFERENCE", cashAcc.getBlockedReference());
        parameterMap.put("pL07_STATUS", cashAcc.getTransStatus());
        return oracleRepository.executeProc(DBConstants.PKG_L07_CAH_ACCOUNT, DBConstants.PROC_L07_ADD_EDIT, parameterMap);
    }

    @Override
    public String updateRevaluationCashAccountRelatedInfo(String cashAccountID, CashAcc cashAcc) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl07_cash_acc_id", cashAccountID);
        parameterMap.put("pl07_cash_balance", cashAcc.getCashBalance());
        parameterMap.put("pl07_pending_settle", cashAcc.getPendingSettle());
        parameterMap.put("pl07_net_receivable", cashAcc.getNetReceivable());
        return oracleRepository.executeProc(DBConstants.PKG_L07_CAH_ACCOUNT, DBConstants.PROC_L07_UPDATE_REVALUATION_INFO, parameterMap);

    }

    @Override
    public String updateTradingAccount(TradingAcc tradingAcc) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL06_TRADING_ACC_ID", tradingAcc.getAccountId());
        parameterMap.put("pL06_EXCHANGE", tradingAcc.getExchange());
        parameterMap.put("pL06_IS_LSF_TYPE", tradingAcc.isLsfType() ? 1 : 0);
        parameterMap.put("pL06_L05_COLLATERAL_ID", tradingAcc.getCollateralId());
        parameterMap.put("pL06_L01_APP_ID", tradingAcc.getApplicationId());
        return oracleRepository.executeProc(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_ADD_EDIT, parameterMap);
    }

    @Override
    public List<TradingAcc> getTradingAccountInCollateral(String applicationId, String collateralId, int isLsfType) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL06_L05_COLLATERAL_ID", collateralId);
        parameterMap.put("pL06_L01_APP_ID", applicationId);
        parameterMap.put("pL06_IS_LSF_TYPE", isLsfType);
        return oracleRepository.getProcResult(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_GET_ACCOUNT_IN_APP, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.TRADING_ACCOUNT));
    }

    public List<TradingAcc> getLSFTypeTradingAccountByCashAccount(String cashAccountID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl07_cash_acc_id", cashAccountID);
        return oracleRepository.getProcResult(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_GET_TRADING_ACC_BY_CASH_ACC, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.TRADING_ACCOUNT));
    }

    @Override
    public String updateRevaluationInfo(String tradingAccountID, double totalPFMarketValue, double totalWeightedPFMarketValue) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl06_trading_acc_id", tradingAccountID);
        parameterMap.put("pl06_total_market_pf_value", totalPFMarketValue);
        parameterMap.put("pl06_total_w_market_pf_value", totalWeightedPFMarketValue);
        return oracleRepository.executeProc(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_UPDATE_REVALUATION, parameterMap);
    }

    public String updateExchangeAccount(String tradingAccountID, String exchangeAccountNumber, String applicationId, int isLsfType) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl06_from_trading_acc", tradingAccountID);
        parameterMap.put("pl06_to_trading_acc", exchangeAccountNumber);
        parameterMap.put("pl06_l01_app_id", applicationId);
        parameterMap.put("pl06_is_lsf_type", isLsfType);
        return oracleRepository.executeProc(DBConstants.PKG_L06_TRADING_ACCOUNT, DBConstants.PROC_L06_UPDATE_EXCHANGE_ACC, parameterMap);
    }

    @Override
    public String updateTradingAccountSymbols(TradingAcc tradingAcc, Symbol symbol) {
        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("pL09_L06_TRADING_ACC_ID", tradingAcc.getAccountId());
        parameterMap.put("pL09_L08_SYMBOL_CODE", symbol.getSymbolCode());
        parameterMap.put("pL09_L08_EXCHANGE", symbol.getExchange());
        parameterMap.put("pL09_COLLAT_QTY", symbol.getColleteralQty());
        parameterMap.put("pL09_CONTRIBUTION_TO_COLLAT", symbol.getContibutionTocollateral());
        parameterMap.put("pL09_TRANSFERRED_QTY", symbol.getTransferedQty());
        parameterMap.put("pL09_BLOCK_REFERENCE", symbol.getBlockedReference());
        parameterMap.put("pL09_STATUS", symbol.getTransStatus());
        parameterMap.put("pl09_l01_app_id", tradingAcc.getApplicationId());
        parameterMap.put("pl09_available_qty", symbol.getAvailableQty());
        parameterMap.put("pl09_close_price", symbol.getPreviousClosed());
        parameterMap.put("pl09_ltp", symbol.getLastTradePrice());
        return oracleRepository.executeProc(DBConstants.PKG_L09_TRADING_SYMBOLS, DBConstants.PROC_L09_ADD_EDIT, parameterMap);
    }

    @Override
    public List<Symbol> getSymbolsInTradingAccount(String tradingAccountId, String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL09_L06_TRADING_ACC_ID", tradingAccountId);
        parameterMap.put("pl09_l01_app_id", applicationId);
        return oracleRepository.getProcResult(DBConstants.PKG_L09_TRADING_SYMBOLS, DBConstants.PROC_L09_GET_TRADING_SYMBOLS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));
    }

    /*--------------------------Marginability Group Related-----------------------*/
    @Override
    public boolean removeMarginabilityGroup(String marginabilityGroupID) {
        boolean response = false;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl11_marginability_grp_id", Integer.parseInt(marginabilityGroupID));
        if (oracleRepository.executeProc(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_L11_REMOVE, parameterMap).equalsIgnoreCase("1")) {
            response = true;
        }

        return response;
    }

    @Override
    public String upadtePurchaseOrderInstallments(Installments installments) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL22_PURCHASE_ORD_ID", installments.getOrderId());
        parameterMap.put("pL22_INSTALLMENT_NUMBER", installments.getInstalmentNumber());
        parameterMap.put("pL22_INSTALLMENT_AMOUNT", installments.getInstallmentAmount());
        parameterMap.put("pL22_INSTALLMENT_DATE", installments.getInstalmentDate());
        parameterMap.put("pL22_INSTALLMENT_STATUS", 0);

        return oracleRepository.executeProc(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_ADD_EDIT, parameterMap);
    }

    @Override
    public List<Installments> getPurchaseOrderInstallments(String orderId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL22_PURCHASE_ORD_ID", orderId);
        return oracleRepository.getProcResult(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_GET_INSTALLMENTS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_INSTALLMENTS));
    }

    @Override
    public String updatePurchaseOrderSymbols(Symbol symbol, String orderId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL16_L08_SYMBOL_CODE", symbol.getSymbolCode());
        parameterMap.put("pL16_L08_EXCHANGE", symbol.getExchange());
        parameterMap.put("pL16_L14_PURCHASE_ORD_ID", orderId);
        parameterMap.put("pL16_PERCENTAGE", symbol.getPercentage());
        return oracleRepository.executeProc(DBConstants.PKG_L16_PO_SYMBOLS, DBConstants.PROC_L16_ADD_EDIT, parameterMap);
    }

    @Override
    public String updatePurchaseOrderCommodity(Commodity symbol, String orderId) {
        log.info("updatePurchaseOrderCommodity : " + orderId);
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl34_m12_commodity_code", symbol.getSymbolCode());
        parameterMap.put("pl34_m12_exchange", symbol.getExchange());
        parameterMap.put("pl34_l16_purchase_ord_id", orderId);
        parameterMap.put("pl34_percentage", symbol.getPercentage());
        parameterMap.put("pl34_sold_amnt",symbol.getSoldAmnt());
        parameterMap.put("pl34_bought_amnt",symbol.getBoughtAmnt());
        return oracleRepository.executeProc(DBConstants.L34_PO_COMMODITIES_PKG, DBConstants.L34_ADD_EDIT, parameterMap);
    }

    @Override
    public String updatePurchaseOrderByAdmin(PurchaseOrder po) {
        String key = "";
        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("pl14_purchase_ord_id", po.getId());
            parameterMap.put("pl14_physical_delivery",po.getIsPhysicalDelivery());
            parameterMap.put("pl14_sell_but_not_settle",po.getSellButNotSettle());
            key = oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.L14_UPDATE_BY_ADMIN, parameterMap);
            for (Commodity commodity : po.getCommodityList()) {
                this.updatePurchaseOrderCommodity(commodity, po.getId());
                if (key != null && key.equalsIgnoreCase("999")){
                    po.setApprovalStatus(0);
                    po.setApprovedByName("SYSTEM");
                    this.approveRejectOrder(po);
                }
            }
        }catch (Exception e){
            key = "0";
            log.error("updatePurchaseOrderByAdmin failed : " + po.getId());
        }

        return key;
        }
    @Override
    public String updateCommodityPOExecution(PurchaseOrder po) {
        List<String> responseParams = new ArrayList<>();

        try {
            log.info("updateCommodityPOExecution started.... status :"+ po.getOrderStatus()+"  path :"+po.getCertificatePath());
            String key;
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("pl14_purchase_ord_id", po.getId());
            parameterMap.put("pl14_ord_status",po.getOrderStatus());
            parameterMap.put("pl14_com_certificate_path",po.getCertificatePath());
            key = oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.L14_UPDATE_COMDT_PO_EXECUTION, parameterMap);
            log.info("updateCommodityPOExecution success");
            return key;
        }catch (Exception e){
            return "0";
        }
    }
    @Override
    public List<Symbol> getPurchaseOrderSymbols(String orderId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL22_PURCHASE_ORD_ID", orderId);
        return oracleRepository.getProcResult(DBConstants.PKG_L16_PO_SYMBOLS, DBConstants.PROC_L16_GET_PO_SYMBOLS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_SYMBOLS));
    }

    @Override
    public String approveRejectOrder(PurchaseOrder purchaseOrder) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL14_APP_ID", Integer.parseInt(purchaseOrder.getApplicationId()));
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(purchaseOrder.getId()));
        //parameterMap.put("pL14_APPROVED_BY_ID",Integer.parseInt(purchaseOrder.getApprovedById()));
        parameterMap.put("pL14_APPROVED_BY_NAME", purchaseOrder.getApprovedByName());
        parameterMap.put("pL14_APPROVAL_STATUS", purchaseOrder.getApprovalStatus());
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_APPROVE_REJECT_ORDER, parameterMap);
    }
    @Override
    public String approveRejectPOCommodity(PurchaseOrder purchaseOrder) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL14_APP_ID", Integer.parseInt(purchaseOrder.getApplicationId()));
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(purchaseOrder.getId()));
        //parameterMap.put("pL14_APPROVED_BY_ID",Integer.parseInt(purchaseOrder.getApprovedById()));
        parameterMap.put("pL14_APPROVED_BY_NAME", purchaseOrder.getApprovedByName());
        parameterMap.put("pL14_APPROVAL_STATUS", purchaseOrder.getApprovalStatus());
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_APPROVE_REJECT_COM_ORDER, parameterMap);
    }

    @Override
    public String upadateOrderStatus(String orderId, int orderStatus, double complatedValue, double profit, double profitPercentage, double vatAmount) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", orderId);
        parameterMap.put("pl14_ordStatus", orderStatus);
        parameterMap.put("pl14_orderCompletedValue", complatedValue);
        parameterMap.put("pl14_last_called_time", LSFUtils.getCurrentMiliSecondAsString());
        parameterMap.put("pl14_profit_amount", profit);
        parameterMap.put("pl14_profit_percentage", profitPercentage);
        parameterMap.put("pl14_vat_amount", vatAmount);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_UPDATE_ORDER_STATUS, parameterMap);

    }

    @Override
    public String updateCustomerOrderStatus(String orderId, String approveStatus, String approveComment, String ipAddress) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(orderId));
        parameterMap.put("pl14_customer_approve_state", Integer.parseInt(approveStatus));
        parameterMap.put("pl14_customer_comment", approveComment);
        parameterMap.put("pl14_accepted_client_ip", ipAddress);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_UPDATE_CUST_ORD_STATUS, parameterMap);

    }

    @Override
    public String updateStockConcentrationGroup(StockConcentrationGroup concentrationGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p12_stock_conc_grp_id", concentrationGroup.getId());
        parameterMap.put("p12_group_name", concentrationGroup.getGroupName());
        parameterMap.put("p12_created_by", concentrationGroup.getCreatedBy());
        parameterMap.put("p12_is_default", concentrationGroup.getIsDefault());
        return oracleRepository.executeProc(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_ADD_UPDATE_STOCK_CONCENTRATION_GROUP, parameterMap);
    }

    @Override
    public String updateStockConcentrationGroupLiqTypes(StockConcentrationGroup concentrationGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        for (LiquidityType liq : concentrationGroup.getConcentrationList()) {
            parameterMap.clear();
            parameterMap.put("p18_stock_conc_grp_id", concentrationGroup.getId());
            parameterMap.put("p18_liquid_id", liq.getLiquidId());
            parameterMap.put("p18_stock_concentrate_perce", liq.getStockConcentrationPercent());
            oracleRepository.executeProc(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_ADD_UPDATE_LIQUID_TYPE_STOCK_CONCENTRATION, parameterMap);
        }
        return "1";
    }

    @Override
    public List<StockConcentrationGroup> getStockConcentrationGroup() {
        return oracleRepository.getProcResult(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_GET_ALL_GROUPS_L12, null, rowMapperFactory.getRowMapper(RowMapperI.STOCK_CONCENTRATION_GROUP));
    }

    @Override
    public List<LiquidityType> getStockConcentrationGroupLiquidTypes(String groupId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p18_stock_conc_grp_id", Integer.parseInt(groupId));
        return oracleRepository.getProcResult(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_GET_LIQUID_TYPES_IN_GROUP_L12, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.LIQUIDITY_TYPES));
    }

    @Override
    public String changeStatusMarginabilityGroup(MarginabilityGroup marginabilityGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl11_marginability_grp_id", Integer.parseInt(marginabilityGroup.getId()));
        parameterMap.put("pL11_APPROVED_BY", marginabilityGroup.getApprovedBy());
        parameterMap.put("pL11_STATUS", marginabilityGroup.getStatus());
        return oracleRepository.executeProc(DBConstants.PKG_L11_MARGINABILITY_GROUP, DBConstants.PROC_L11_CHANGE_STATUS, parameterMap);
    }

    @Override
    public String changeStatusConcentrationGroup(StockConcentrationGroup stockConcentrationGroup) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl12_stock_conc_grp_id", stockConcentrationGroup.getId());
        parameterMap.put("pL12_STATUS", stockConcentrationGroup.getStatus());
        parameterMap.put("pL12_APPROVED_BY", stockConcentrationGroup.getApprovedBy());
        return oracleRepository.executeProc(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_L12_CAHNGE_STATUS, parameterMap);
    }

    @Override
    public boolean removeStockConcentrationGroup(String concentrationGroupID) {
        boolean response = false;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl12_stock_conc_grp_id", Integer.parseInt(concentrationGroupID));
        if (oracleRepository.executeProc(DBConstants.PKG_L12_STOCK_CONCENTRATION, DBConstants.PROC_L12_REMOVE, parameterMap).equalsIgnoreCase("1")) {
            response = true;
        }

        return response;
    }


    @Override
    public String addNotificationHeader(Notification notification) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p01_uid", notification.getUid());
        parameterMap.put("p01_status", notification.getStatus());
        parameterMap.put("p01_user_id", notification.getHeader().getUserId());
        parameterMap.put("p01_source", notification.getHeader().getSource());
        parameterMap.put("p01_notification_type", notification.getHeader().getNotificationType());
        parameterMap.put("p01_message_type", notification.getHeader().getMessageType());
        parameterMap.put("p01_language", notification.getHeader().getLanguage());
        parameterMap.put("p01_is_attachment_available", notification.getHeader().isAttachment());
        parameterMap.put("p01_mobile_numbers", notification.getHeader().getMobileNumbers());//
        parameterMap.put("p01_from_address", notification.getHeader().getFromAddress());//
        parameterMap.put("p01_to_addresses", notification.getHeader().getToAddresses());// todo all five with comma separator
        parameterMap.put("p01_cc_addresses", notification.getHeader().getCcAddresses());//
        parameterMap.put("p01_bcc_addresses", notification.getHeader().getBccAddresses());//
        return oracleRepository.executeProc(DBConstants.PKG_N01_NOTIFICATION, DBConstants.PROC_N01_ADD_NOTIFICATION_HEADER, parameterMap);
    }

    @Override
    public String addNotificationBody(String uid, String key, String value) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p01_uid", uid);
        parameterMap.put("p02_key", key);
        parameterMap.put("p02_value", value);
        return oracleRepository.executeProc(DBConstants.PKG_N01_NOTIFICATION, DBConstants.PROC_N02_ADD_NOTIFICATION_BODY, parameterMap);
    }

    @Override
    public String addWebNotification(WebNotification webNotification) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p04_message_id", webNotification.getMessageId());
        parameterMap.put("p04_l01_application_id", webNotification.getApplicationId());
        parameterMap.put("p04_subject", webNotification.getSubject());
        parameterMap.put("p04_body", webNotification.getBody());
        parameterMap.put("p04_reference", webNotification.getReference());
        parameterMap.put("p04_status", webNotification.getStatus());
        return oracleRepository.executeProc(DBConstants.PKG_N04_WEB_NOTIFICATION, DBConstants.PROC_N04_ADD_UPDATE, parameterMap);
    }

    @Override
    public List<WebNotification> getWebNotification(String applicationId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p04_l01_application_id", applicationId);
        List<WebNotification> list = oracleRepository.getProcResult(DBConstants.PKG_N04_WEB_NOTIFICATION, DBConstants.PROC_N04_GET_WEB_NOTIFICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.WEB_NOTIFICATION));
        return list;
    }

    @Override
    public String updateReadWebNotification(String applicationId, String messageId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p04_l01_application_id", applicationId);
        parameterMap.put("p04_message_id", messageId);
        return oracleRepository.executeProc(DBConstants.PKG_N04_WEB_NOTIFICATION, DBConstants.PROC_N04_UPDATE_READ_NOTIFICATION, parameterMap);
    }

    @Override
    public String updateNotificationMsgConfig(NotificationMsgConfiguration notificationMsgConfiguration) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p03_id", notificationMsgConfiguration.getId());
        parameterMap.put("p03_current_level", notificationMsgConfiguration.getCurrentLevel());
        parameterMap.put("p03_overole_status", notificationMsgConfiguration.getOverRoleStatus());
        if (notificationMsgConfiguration.isSms()) {
            parameterMap.put("p03_is_sms", 1);
        } else {
            parameterMap.put("p03_is_sms", 0);
        }

        if (notificationMsgConfiguration.isMail()) {
            parameterMap.put("p03_is_mail", 1);
        } else {
            parameterMap.put("p03_is_mail", 0);
        }

        if (notificationMsgConfiguration.isWeb()) {
            parameterMap.put("p03_is_web", 1);
        } else {
            parameterMap.put("p03_is_web", 0);
        }
        parameterMap.put("p03_subject", notificationMsgConfiguration.getWebSubject());
        parameterMap.put("p03_text", notificationMsgConfiguration.getWebBody());
        parameterMap.put("p03_sms_template", notificationMsgConfiguration.getSmsTemplate());
        parameterMap.put("p03_email_subject", notificationMsgConfiguration.getEmailSubject());
        parameterMap.put("p03_email_body", notificationMsgConfiguration.getEmailBody());
        return oracleRepository.executeProc(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.PROC_N03_ADD_UPDATE, parameterMap);
    }

    @Override
    public List<NotificationMsgConfiguration> getNotificationMsgConfiguration() {
        Map<String, Object> parameterMap = new HashMap<>();
        return  oracleRepository.getProcResult(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.PROC_N03_GET_ALL, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_MSG_CONFIGURATION));
    }

    @Override
    public List<NotificationMsgConfiguration> getNotificationMsgConfigurationForLevelStatus(String currentLevel, String overRoleStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p03_current_level", currentLevel);
        parameterMap.put("p03_overole_status", overRoleStatus);
        return oracleRepository.getProcResult(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.PROC_N03_GET_STATUS_LEVEL, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_MSG_CONFIGURATION));
    }

    @Override
    public List<NotificationMsgConfiguration> getNotificationMsgConfigurationForNotificationType(String notificationType) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pn03_notification_code", notificationType);
        return oracleRepository.getProcResult(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.PROC_N03_GET_CONFIG_NOTIFI_TYPE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_MSG_CONFIGURATION));
    }

    @Override
    public List<NotificationMsgConfiguration> getNotificationMsgConfigurationForApplication(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(applicationID));
        return oracleRepository.getProcResult(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.PROC_N03_GET_MATCHING_CONFIG, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_MSG_CONFIGURATION));
    }

    @Override
    public List<Notification> getNotificationHeader() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_N01_NOTIFICATION, DBConstants.PROC_N01_GET_NOTIFICATION_HEADER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_HEADER));
    }

    @Override
    public List<Map<String, String>> getNotificationBody(String uid) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p01_uid", uid);
        return oracleRepository.getProcResult(DBConstants.PKG_N01_NOTIFICATION, DBConstants.PROC_N02_GET_NOTIFICATION_BODY, parameterMap, mapperRegistry.getMapRowMapperString());  
    }

    @Override
    public List<Message> getCustomMessageHistory() {
        Map<String, Object> parameterMap = new HashMap<>();
        return  oracleRepository.getProcResult(DBConstants.PKG_N04_MESSAGE_OUT, DBConstants.PROC_N04_GET_CUSTOM_MESSAGE_HISTORY, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MESSAGE));
    }

    @Override
    public List<Message> getNotificationHistory() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_N04_MESSAGE_OUT, DBConstants.PROC_N04_GET__MESSAGE_HISTORY, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MESSAGE));
    }   

    @Override
    public String addMessageOut(Message message) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p04_uid", message.getUid());
        parameterMap.put("p04_user_id", message.getUserId());
        parameterMap.put("p04_notification_type", message.getNotificationType());
        parameterMap.put("p04_language", message.getLanguage());
        parameterMap.put("p04_attachment", message.isAttachment());
        parameterMap.put("p04_mobile_no", message.getMobileNumbers());
        parameterMap.put("p04_from_address", message.getFromAddress());//
        parameterMap.put("p04_to_addresses", message.getToAddresses());// todo all five with comma separator
        parameterMap.put("p04_cc_addresses", message.getCcAddresses());//
        parameterMap.put("p04_bcc_addresses", message.getBccAddresses());
        parameterMap.put("p04_message", message.getMessage());
        parameterMap.put("p04_subject", message.getSubject());
        parameterMap.put("p04_status", message.getStatus());
        if (message.isCustom()) {
            parameterMap.put("p04_is_custom", 1);
        } else {
            parameterMap.put("p04_is_custom", 0);
        }
        parameterMap.put("p04_sent_by", message.getSentBy());
        parameterMap.put("pn04_tp_sms", message.getThirdPartySMS());
        parameterMap.put("pn04_tp_email", message.getThirdPartyEmail());
        return oracleRepository.executeProc(DBConstants.PKG_N04_MESSAGE_OUT, DBConstants.PROC_N04_ADD_MESSAGE_OUT, parameterMap);
    }

    @Override
    public String updateStatusMessageOut(String uid, int status) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("p04_uid", uid);
        parameterMap.put("p04_status", status);
        return oracleRepository.executeProc(DBConstants.PKG_N04_MESSAGE_OUT, DBConstants.PROC_N04_UPDATE_STATUS_MESSAGE_OUT, parameterMap);
    }


    @Override
    public String addActivityLog(ActivityLog activityLog) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pa03_id", activityLog.getId());
        parameterMap.put("pa03_category_id", activityLog.getCategoryId());
        parameterMap.put("pa03_activity_id", activityLog.getActivityId());
        parameterMap.put("pa03_date", activityLog.getDate());
        parameterMap.put("pa03_user_id", activityLog.getUserId());
        parameterMap.put("pa03_ip", activityLog.getIp());
        parameterMap.put("pa03_discription", activityLog.getDescription());
        return oracleRepository.executeProc(DBConstants.PKG_A03_AUDIT, DBConstants.PROC_A03_ADD_UPDATE, parameterMap);
    }

    @Override
    public String addAuditDetails(Long id, String rawMassege, String messageType, String subMessageType, String ip, String channelID, String userID, String correlationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pa03_id", String.valueOf(id));
        parameterMap.put("pa03_raw_message", rawMassege);
        parameterMap.put("pa03_message_type", messageType);
        parameterMap.put("pa03_sub_message_type", subMessageType);
        parameterMap.put("pa03_ip", ip);
        parameterMap.put("pa03_channel_id", channelID);
        parameterMap.put("pa03_user_id", userID);
        parameterMap.put("pa03_corrilation_id", correlationID);
        return oracleRepository.executeProc(DBConstants.PKG_A03_AUDIT, DBConstants.PROC_A03_ADD_UPDATE, parameterMap);
    }

    @Override
    public String updateWishListSymbols(MApplicationSymbolWishList wishList) {
        Map<String, Object> parameterMap = new HashMap<>();
        for (Symbol symbol : wishList.getWishListSymbols()) {
            parameterMap.clear();
            parameterMap.put("pl13_l01_app_id", wishList.getId());
            parameterMap.put("pl13_l08_symbol_code", symbol.getSymbolCode());
            parameterMap.put("pl13_l08_exchange", symbol.getExchange());
            oracleRepository.executeProc(DBConstants.PKG_L13_SYMBOL_WISH_LIST, DBConstants.PROC_ADD_WISH_LIST_SYMBOLS, parameterMap);
        }
        return "1";
    }


    @Override
    public String updatePurchaseOrderAcceptanceReminder(String orderID, int attemptCount, int approveStatus, String lastUpdatedTime) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(orderID));
        parameterMap.put("pl14_no_of_calling_attempts", attemptCount);
        parameterMap.put("pl14_customer_approve_state", approveStatus);
        parameterMap.put("pl14_last_called_time", lastUpdatedTime);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_UPDATE_PO_REMINDER, parameterMap);

    }

    @Override
    public String updateCallingAttemptLog(String applicationID, String orderID, int callingAttempt) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl25_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl25_purchase_ord_id", orderID);
        parameterMap.put("pl25_calling_attempt", callingAttempt);
        return oracleRepository.executeProc(DBConstants.PKG_L25_CALLING_ATTEMPT_LOG_PKG, DBConstants.PROC_L25_ADD, parameterMap);

    }

    @Override
    public void updateSymbolClassifyLogStatus(long customerId) {
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl31_customer_id", customerId);
        oracleRepository.getProcResult(DBConstants.PKG_L31_SYMBOL_CLASSIFY_LOG,
                DBConstants.PROC_L31_SYMBOL_CLASSIFY_LOG_UPDATE,
                parameterMap);
    }

    @Override
    public List<SymbolClassifyLog> getSymbolClassifyLog(long customerId) {
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl31_customer_id", customerId);
        return oracleRepository.getProcResult(DBConstants.PKG_L31_SYMBOL_CLASSIFY_LOG,
                DBConstants.PROC_L31_SYMBOL_CLASSIFY_LOG_GET,
                parameterMap, rowMapperFactory.getRowMapper(RowMapperI.SYMBOL_CLASSIFY_LOG));
    }

    @Override
    public String updatePOLiquidateState(String poID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(poID));
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_UPDATE_TO_LIQUIDATE_STATE, parameterMap);
    }


    @Override
    public String updateBasketTransferState(int orderID, int transferStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", orderID);
        parameterMap.put("pl14_bskt_transfer_status", transferStatus);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_UPDATE_BASKET_STATUS, parameterMap);

    }


    /*---------------------User Surveyor Related -----------------------------*/
    @Override
    public String saveQuestionerAnswer(int userID, int questionID, String answer, String ipAddress) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pA04_CUSOMER_ID", userID);
        parameterMap.put("pA04_QUESTION_ID", questionID);
        parameterMap.put("pA04_ANSWER", answer);
        parameterMap.put("pa04_ip", ipAddress);
        return oracleRepository.executeProc(DBConstants.PKG_A04_QUESTIONER_PKG, DBConstants.PROC_A04_ADD, parameterMap);
    }

    @Override
    public List<UserAnswer> getSavedAnswerForUser(int userID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pA04_CUSOMER_ID", userID);
        return oracleRepository.getProcResult(DBConstants.PKG_A04_QUESTIONER_PKG, DBConstants.PROC_A04_GET_ALL, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.USER_ANSWER));
    }

    /*---------------------Application Settlement Releated -----------------------------*/
    @Override
    public List<MurabahApplication> getOrderContractSingedApplications() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.L01_GET_ODRCNTCT_SINGED_APP, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrderForApplication(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pL14_APP_ID", Integer.parseInt(applicationID));
        return oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_GET_ALL_ORDER, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));
    }

    @Override
    public List<OrderProfit> getLastEntryForApplication(String applicationID, String orderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl23_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl23_order_id", Integer.parseInt(orderID));
        return oracleRepository.getProcResult(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_GET_LSTONE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_PROFIT));
    }

    @Override
    public String updateProfit(OrderProfit orderProfit, double lsfTypeCashBalance) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl23_application_id", orderProfit.getApplicationID());
        parameterMap.put("pl23_order_id", orderProfit.getOrderID());
        parameterMap.put("pl23_profit_amt", orderProfit.getProfitAmount());
        parameterMap.put("pl23_cum_profit_amt", orderProfit.getCumulativeProfitAmount());
        parameterMap.put("pl23_cum_profit_amt", orderProfit.getCumulativeProfitAmount());
        parameterMap.put("pl23_lsf_cash_acc_balance", lsfTypeCashBalance);
        return oracleRepository.executeProc(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_ADD, parameterMap);

    }

    @Override
    public List<NotificationMsgConfiguration> getNotificationMsgConfigurationForSettlement(int applicationCurrentLevel, String applicationCurrentStatus) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pn03_current_level", applicationCurrentLevel);
        parameterMap.put("pn03_overole_status", Integer.parseInt(applicationCurrentStatus));
        return oracleRepository.getProcResult(DBConstants.PKG_N03_NOTIFICATION_MESSAGE_CONFIG, DBConstants.N03_GET_STTLEMENT_MSG_TMPLT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.NOTIFICATION_MSG_CONFIGURATION));
    }

    @Override
    public String addLiquidationLog(int liquidationReference, String cashFromAccount, String cashToAccount, double transferAmount, String applicationID, int status, String poID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl24_liquidate_reference", liquidationReference);
        parameterMap.put("pl24_cash_from_account", cashFromAccount);
        parameterMap.put("pl24_cash_to_account", cashToAccount);
        parameterMap.put("pl24_transfer_amount", transferAmount);
        parameterMap.put("pl24_l01_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl24_status", status);
        parameterMap.put("pl24_order_id", Integer.parseInt(poID));
        return oracleRepository.executeProc(DBConstants.PKG_L24_LIQUIDATION_LOG, DBConstants.L24_ADD_LIQUIDATION_ENTRY, parameterMap);

    }

    @Override
    public List<LiquidationLog> getLiquidationLog(int liquidationReference) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl24_liquidate_reference", liquidationReference);
        return oracleRepository.getProcResult(DBConstants.PKG_L24_LIQUIDATION_LOG, DBConstants.L24_GET_LIQUIDATION_LOG, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.LIQUIDATION_LOG));
    }

    @Override
    public String updateLiquidationLogState(int liquidationReference, int status) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl24_liquidate_reference", liquidationReference);
        parameterMap.put("pl24_status", status);
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.L24_UPDATE_LOG_STATUS, parameterMap);

    }

    @Override
    public String moveToLiquidateState(String applicationID, String message) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(applicationID));
        parameterMap.put("pl02_message", message);
        parameterMap.put("pl02_sts_changed_user_id", "0001");
        parameterMap.put("pl02_sts_changed_user_name", "SYSTEM");
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.M02_SET_LIQUIDATE_STATE, parameterMap);
    }

    @Override
    public String moveToClosedState(String applicationID, String message, String orderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(applicationID));
        parameterMap.put("pl02_message", message);
        parameterMap.put("pl02_sts_changed_user_id", "0001");
        parameterMap.put("pl02_sts_changed_user_name", "SYSTEM");
        parameterMap.put("pl02_order_id", orderID);
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.M02_SET_CLOSED_STATE, parameterMap);
    }

    @Override
    public String updateAccountDeletionState(String applicationID, int state) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(applicationID));
        parameterMap.put("pl01_acc_closed_status", state);
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_SET_APPLICATION_ACCOUNT_CLOSE_STATE, parameterMap);

    }

    @Override       
    public String closeApplication(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl01_app_id", Integer.parseInt(applicationID));
        return oracleRepository.executeProc(DBConstants.PKG_M02_APP_STATE_FLOW, DBConstants.M02_CLOSE_APP, parameterMap);

    }

    @Override
    public List<PurchaseOrder> getApplicationForManualSettlement() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_GET_APPS_FOR_MANUAL_SETMNT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));

    }

    @Override
    public String checkFinalInstallmentApplication(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl05_l01_app_id", applicationID);
        return oracleRepository.executeProc(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_IS_FINAL_INSTALLMENT_APP, parameterMap);

    }

    @Override
    public String checkFinalInstallmentPO(String applicationID, String purchaseOrderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl05_l01_app_id", applicationID);
        parameterMap.put("pl14_po_id", purchaseOrderID);
        return oracleRepository.executeProc(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_IS_FINAL_INSTALLMENT_PO, parameterMap);

    }

    @Override
    public String createInstallment(int installmentNumber, String purchaseOrderID, String createdBy) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_po_id", purchaseOrderID);
        parameterMap.put("pl22_installment_number", installmentNumber);
        parameterMap.put("pl22_settlement_created_by", createdBy);
        return oracleRepository.executeProc(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_GENERATE_INSTALLMENT, parameterMap);
    }

    @Override
    public String approveInstallment(int installmentNumber, String purchaseOrderID, String approvedBy) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_po_id", purchaseOrderID);
        parameterMap.put("pl22_installment_number", installmentNumber);
        parameterMap.put("pl22_settlement_approved_by", approvedBy);
        return oracleRepository.executeProc(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_APPROVE_INSTALLMENT, parameterMap);

    }

    @Override
    public String updatePOToSettledState(int purchaseOrderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", purchaseOrderID);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_UPDATE_TO_SETTLE_STATE, parameterMap);

    }

    @Override       
    public List<Installments> getCreatedInstallments() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L22_PO_INSTALLMENTS, DBConstants.PROC_L22_GET_CREATED_INSTALLMENTS, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_INSTALLMENTS));

    }

    /*---------------------Application Settlement Inquiry Related -----------------------------*/
    @Override
    public OrderProfit getSummationOfProfitUntilToday(String applicationID, String orderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl23_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl23_order_id", Integer.parseInt(orderID));
        List<OrderProfit> orderProfitList = oracleRepository.getProcResult(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_GET_SUMMATION_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_PROFIT));
        if (orderProfitList != null && orderProfitList.size() > 0) {
            return orderProfitList.get(0);
        } else {
            return null;
        }

    }
    
    @Override
    public List<OrderProfit> getAllOrderProfitsForApplication(String applicationID, String orderID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl23_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl23_order_id", Integer.parseInt(orderID));
        List<OrderProfit> orderProfitList = oracleRepository.getProcResult(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_GET_ALL_FOR_APPLICATION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_PROFIT));
        if (orderProfitList != null && orderProfitList.size() > 0) {
            return orderProfitList;
        } else {
            return null;
        }

    }

    /*---------------------Reporting Related -----------------------------*/
    @Override
    public ReportConfigObject getReportConfigObject(int reportID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm03_report_id", reportID);
        List<ReportConfigObject> reportConfigObjects = oracleRepository.getProcResult(DBConstants.PKG_M03_REPORT_SETTINGS_PKG, DBConstants.M03_GET_REPORT_CONFIG, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.REPORT_CONFIG_OBJECT));
        return reportConfigObjects.get(0);
    }

    @Override
    public MarginInformation getMarginInformation(String fromDate, String toDate) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("fromdate", fromDate);
        parameterMap.put("todate", toDate);
        List<MarginInformation> marginInformations = oracleRepository.getProcResult(DBConstants.PKG_R01_REPORTS, DBConstants.R01_REPORT_MARGIN_INFO, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MARGIN_INFO));
        if (marginInformations != null && marginInformations.size() > 0) {
            return marginInformations.get(0);
        } else {
            return new MarginInformation();
        }

    }
    
    @Override
    public List<FinanceBrokerageInfo> getFinanceBrokerageInfo() {
        Map<String, Object> parameterMap = new HashMap<>();
        return  oracleRepository.getProcResult(DBConstants.PKG_R01_REPORTS, DBConstants.R01_FINANCE_BROKERAGE_INFO, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.FINANCE_BROKERAGE_INFO));
    }

    @Override
    public ReportConfiguration getReportConfiguration(String reportName) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("report_name", reportName);
        List<ReportConfiguration> reportConfigList = oracleRepository.getProcResult(
                "m04_reports_pkg", "report_config",
                    parameterMap, rowMapperFactory.getRowMapper(RowMapperI.REPORT_CONFIG));
        return reportConfigList.get(0);
    }

    @Override
    public List<Map<String, Object>> getDataForReporting(String packageName, String procedureName, String className, Map<String, String> parameters) {
        if (packageName != null && procedureName != null) {
            return  oracleRepository.getProcResult(packageName, procedureName, parameters);
        } 
            return null;
        }

    @Override
    public List<Map<String, Object>>  getParamsForReporting(String packageName, String procedureName, String className, Map<String, String> parameters) {
        if (packageName != null && procedureName != null) {
            return  oracleRepository.getProcResult(packageName, procedureName, parameters);
        }
        return null;
    }


    /*-------------------------------------------------------------------------*/
    @Override
    public String addWithdraw(DepositWithdrawRequest depositRequest, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl26_purchase_ord_id", depositRequest.getPurchaseOrderID());
        parameterMap.put("pl26_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl26_from_acc", depositRequest.getFromBankAcc());
        parameterMap.put("pl26_to_acc", depositRequest.getToBankAcc());
        parameterMap.put("pl26_amount", depositRequest.getAmount());
        parameterMap.put("pl26_status", LsfConstants.INITIALED_IN_LSF);
        parameterMap.put("pl26_req_type", LsfConstants.WITHDRAW);
        parameterMap.put("pl26_txn_reference", depositRequest.getReferenceNo());
        return oracleRepository.executeProc(DBConstants.PKG_L26_EXTERNAL_REQUEST_LOG, DBConstants.L26_ADD, parameterMap);

    }

    @Override
    public String addDeposit(DepositWithdrawRequest depositRequest, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl26_purchase_ord_id", depositRequest.getPurchaseOrderID());
        parameterMap.put("pl26_application_id", Integer.parseInt(applicationID));
        parameterMap.put("pl26_from_acc", depositRequest.getFromBankAcc());
        parameterMap.put("pl26_to_acc", depositRequest.getToBankAcc());
        parameterMap.put("pl26_amount", depositRequest.getAmount());
        parameterMap.put("pl26_status", LsfConstants.INITIALED_IN_LSF);
        parameterMap.put("pl26_req_type", LsfConstants.DEPOSIT);
        parameterMap.put("pl26_txn_reference", depositRequest.getReferenceNo());

        return oracleRepository.executeProc(DBConstants.PKG_L26_EXTERNAL_REQUEST_LOG, DBConstants.L26_ADD, parameterMap);

    }

    @Override
    public String updateDepositStatus(String referenceID, int status, int type) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl26_txn_reference", referenceID);
        parameterMap.put("pl26_status", status);
        parameterMap.put("pl26_req_type", String.valueOf(type));
        return oracleRepository.executeProc(DBConstants.PKG_L26_EXTERNAL_REQUEST_LOG, DBConstants.L26_UPDATE_STATUS, parameterMap);

    }

    
    @Override
    public PurchaseOrder getPurchaseOrderByReference(String referenceID) {
        PurchaseOrder po = null;
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl26_txn_reference", referenceID);
        List<PurchaseOrder> purchaseOrderList = oracleRepository.getProcResult(DBConstants.PKG_L26_EXTERNAL_REQUEST_LOG, DBConstants.L26_FIND_BY_REF, paraMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));
        if (purchaseOrderList != null) {
            if (purchaseOrderList.size() > 0) {
                po = purchaseOrderList.get(0);
                po.setInstallments(this.getPurchaseOrderInstallments(po.getId()));
                po.setSymbolList(this.getPurchaseOrderSymbols(po.getId()));
                po.setCommodityList(this.getPurchaseOrderCommodities(po.getId()));
            }
        }
        return po;
    }


    @Override
    public String addExternalCollaterals(ExternalCollaterals externalCollaterals) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl27_application_id", externalCollaterals.getApplicationId());
        parameterMap.put("pl27_collateral_id", externalCollaterals.getCollateralId());
        parameterMap.put("pl27_collateral_type", externalCollaterals.getCollateralType());
        parameterMap.put("pl27_reference", externalCollaterals.getReference());
        parameterMap.put("pl27_collateral_amount", externalCollaterals.getCollateralAmount());
        parameterMap.put("pl27_expire_date", externalCollaterals.getExpireDate());

        return oracleRepository.executeProc(DBConstants.PKG_L27_EXTERNAL_COLLATERALS, DBConstants.PROC_L27_ADD, parameterMap);
    }

    @Override   
    public List<ExternalCollaterals> getExternalCollaterals(int applicationId, int collateralId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl27_application_id", applicationId);
        paraMap.put("pl27_collateral_id", collateralId);
        return  oracleRepository.getProcResult(DBConstants.PKG_L27_EXTERNAL_COLLATERALS, DBConstants.PROC_L27_GET, paraMap, rowMapperFactory.getRowMapper(RowMapperI.EXTERNAL_COLLATERALS));
    }
    
    @Override
    public String updateExternalCollaterals(ExternalCollaterals externalCollaterals) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl27_id", externalCollaterals.getId());
        parameterMap.put("pl27_haircut_percent", externalCollaterals.getHaircutPercent());
        parameterMap.put("pl27_applicable_amount", externalCollaterals.getApplicableAmount());
        parameterMap.put("pl27_add_main_collateral", externalCollaterals.isAddToCollateral());
        parameterMap.put("pl27_approved_user_id", externalCollaterals.getApprovedUserId());
        return oracleRepository.executeProc(DBConstants.PKG_L27_EXTERNAL_COLLATERALS, DBConstants.PROC_L27_UPDATE, parameterMap);
    }

    @Override
    public List<ExternalCollaterals> getExternalCollateralsForApplication(int applicationId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl27_application_id", applicationId);
        return  oracleRepository.getProcResult(DBConstants.PKG_L27_EXTERNAL_COLLATERALS, DBConstants.PROC_L27_GET_ALL_FOR_APPLICATION, paraMap, rowMapperFactory.getRowMapper(RowMapperI.EXTERNAL_COLLATERALS));
    }

    /*--------------------Common Inquiry Related--------------*/
    @Override
    public List<FTVInfo> getDetailedFTVList(String fromDate, String toDate, int settlementSts) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("fromdate", fromDate);
        paraMap.put("todate", toDate);
        paraMap.put("stlStatus", settlementSts);
        return  oracleRepository.getProcResult(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_L05_GET_FTV_DETAILED_INFO, paraMap, rowMapperFactory.getRowMapper(RowMapperI.FTV_DETAILED_INFO));
    }

    @Override
    public List<CommissionDetail> getCommissionDetails(String reportDate) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("reportDate", reportDate);
        return  oracleRepository.getProcResult(DBConstants.PKG_L05_COLLATERALS, DBConstants.PROC_L05_GET_ML_ACCOUNT_COMMISSION, paraMap, rowMapperFactory.getRowMapper(RowMapperI.COMMISSION_DETAILS));
    }
    
    @Override
    public List<MurabahApplication> getApprovedPurchaseOrderApplicationList(Object fromDate, Object toDate) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl14_fromDate", fromDate);
        paraMap.put("pl14_toDate", toDate);
        return  oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_GET_ORD_APRVED_APP, paraMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<MurabahApplication> getBlackListedApplications() {
        Map<String, Object> paraMap = new HashMap<>();
        return  oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_BLACK_LISTED_APPLICATIONS, paraMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
    }

    @Override
    public List<SettlementSummaryResponse> getSettlementListReport(int settlementStatus, String fromDate, String toDate) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("settlementstatus", settlementStatus);
        paraMap.put("fromdate", fromDate);
        paraMap.put("todate", toDate);
        return  oracleRepository.getProcResult(DBConstants.PKG_M04_REPORTS, DBConstants.PROC_M04_SETTLEMENT_LIST, paraMap, rowMapperFactory.getRowMapper(RowMapperI.SETTLEMENT_LIST));
    }


    /*-------------------Margin Related ---------------*/
    @Override
    public String addFTVLog(MApplicationCollaterals collaterals) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl28_application_id", collaterals.getApplicationId());
        paraMap.put("pl28_ftv", collaterals.getFtv());
        paraMap.put("pl28_total_colt_value", collaterals.getNetTotalColleteral());
        paraMap.put("pl28_total_pf_colt_value", collaterals.getTotalPFColleteral());
        paraMap.put("pl28_margine_call_triggered", collaterals.isFirstMargineCall());
        paraMap.put("pl28_liquidation_triggered", collaterals.isLiqudationCall());
        return oracleRepository.executeProc(DBConstants.PKG_L28_DAILY_FTV_LOG_PKG, DBConstants.PROC_L28_ADD_UPDATE, paraMap);
    }

    @Override
    public String addMarginCallLog(MApplicationCollaterals collaterals, int marginType) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl29_application_id", collaterals.getApplicationId());
        paraMap.put("pl29_collateral_id", collaterals.getId());
        paraMap.put("pl29_margine_type", marginType);
        paraMap.put("pl29_outstanding_balance", collaterals.getOutstandingAmount());
        paraMap.put("pl29_block_amount", 123);
        paraMap.put("pl29_net_collateral", collaterals.getNetTotalColleteral());
        paraMap.put("pl29_ftv", collaterals.getFtv());
        return oracleRepository.executeProc(DBConstants.PKG_L29_MARGINE_CALL_LOG_PKG, DBConstants.PROC_L29_ADD, paraMap);

    }

    /*-------------------Risk Wavier Question ---------------*/
    @Override
    public List<RiskwavierQuestionConfig> getAllQustionSettings() {
        Map<String, Object> paraMap = new HashMap<>();
        List<RiskwavierQuestionConfig> configList = oracleRepository.getProcResult(DBConstants.PKG_M06_RISKWAVIER_QST_CONFIG_PKG, DBConstants.M06_GET_ALL, paraMap, rowMapperFactory.getRowMapper(RowMapperI.RISK_WAVIER_QUESTION_CONFIG));
        return configList;
    }


    /*-------Pending Activity-----------------------*/
    @Override
    public List<PendingActivity> getPendingActivityList() {
        Map<String, Object> paraMap = new HashMap<>();
        List<PendingActivity> pendingActivityList = oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_INCOMPLETE_CUSTOMERS, paraMap, rowMapperFactory.getRowMapper(RowMapperI.PENDING_ACTIVITY));
        return pendingActivityList;
    }

    @Override
    public List<MurabahApplication> getApplicationListForAdminCommonReject() {
        Map<String, Object> paraMap = new HashMap<>();
        List<MurabahApplication> murabahApplicationList = oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_GET_APPS_FOR_ADMIN_REJECT, paraMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
        return murabahApplicationList;
    }

    /*-------------------Notifications---------------*/
    @Override
    public void addAdminUser(AdminUser adminUser) {
        oracleRepository.getProcResult(DBConstants.PKG_M05_ADMIN_USERS,
                DBConstants.PROC_M05_ADMIN_USERS_ADD_EDIT,
                adminUser.getAttributeMap(), rowMapperFactory.getRowMapper(RowMapperI.ADMIN_USER));
    }

    @Override   
    public List<AdminUser> getAdminUsers() {
        return oracleRepository.getProcResult(DBConstants.PKG_M05_ADMIN_USERS,
                DBConstants.PROC_M05_ADMIN_USERS_GET_ALL,
                new HashMap<String, Object>(), rowMapperFactory.getRowMapper(RowMapperI.ADMIN_USER));
    }

    @Override
    @CacheEvict(value = "murabahApplications", key = "#application.id")
    public String updateApplicationOtp(MurabahApplication application) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl01_app_id", application.getId());
        paraMap.put("pl01_otp", application.getOtp());
        paraMap.put("pl01_otp_generated_time", application.getOtpGeneratedTime());
        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_UPDATE_CUSTOMER_OTP, paraMap);
    }

    @Override
    public List<FtvSummary> getFTVsummaryForDashBoard(String applicationId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl28_application_id", applicationId);
        List<FtvSummary> ftvInfoList = oracleRepository.getProcResult(DBConstants.PKG_L28_DAILY_FTV_LOG_PKG, DBConstants.PROC_L28_GET, paraMap, rowMapperFactory.getRowMapper(RowMapperI.FTV_SUMMARY_INFO));
        return ftvInfoList;
    }

    @Override
    public FtvSummary getFTVforToday(String applicationId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl28_application_id", applicationId);
        List<FtvSummary> ftvSummaryList = oracleRepository.getProcResult(DBConstants.PKG_L28_DAILY_FTV_LOG_PKG, DBConstants.PROC_L28_GET_FOR_TODAY, paraMap, rowMapperFactory.getRowMapper(RowMapperI.FTV_SUMMARY_INFO));
        if (ftvSummaryList != null && ftvSummaryList.size() > 0){
            return ftvSummaryList.get(0);
        }else
            return null;
        }

    @Override
    @CacheEvict(value = "murabahApplications", key = "#applicationId")
    public String updateActivity(String applicationId, int activityId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("pl01_app_id", applicationId);
        paraMap.put("pl01_acc_activity_id", activityId);

        return oracleRepository.executeProc(DBConstants.PKG_L01_APPLICATION, DBConstants.PROC_L01_UPDATE_ACTIVITY, paraMap);
    }
    
    @Override
    public MurabahApplication getApplicationByCashAccount(String cashAccountID, int accountType) {
        List<MurabahApplication> murabahApplications = null;
        //   statusList = getApplicationStatus(applicationID);
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl07_cash_acc_id", cashAccountID);
        parameterMap.put("pl07_is_lsf_type", accountType);
        murabahApplications = oracleRepository.getProcResult(DBConstants.PKG_L07_CAH_ACCOUNT, DBConstants.PROC_L07_GET_APP_BY_CASH_ACCOUNT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAH_APPLICATION));
        if (murabahApplications != null && murabahApplications.size() > 0) {
            return murabahApplications.get(0);
        } else {
            return null;
        }
    }

    /*-----Validation Related----------*/
    @Override
    public double getMasterAccountOutstanding() {
        Map<String, Object> parameterMap = new HashMap<>();
        String key = oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_GET_TOTAL_OUTSTANDING, parameterMap);
        return Double.parseDouble(key);
    }

    @Override
    public String updateInvestorAcc(String cashAcc, String investorAcc, String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl07_cash_acc_id", cashAcc);
        parameterMap.put("pl07_investor_account", investorAcc);
        parameterMap.put("pl07_l01_app_id", applicationID);
        return oracleRepository.executeProc(DBConstants.PKG_L07_CAH_ACCOUNT, DBConstants.PROC_L07_UPDATE_INVESTMENT_ACC, parameterMap);
    }

    @Override
    public String updateAdminFee(double simaCharges, double transferCharges, double vatAmount, String poId) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id", Integer.parseInt(poId));
        parameterMap.put("pl14_sima_charges", simaCharges);
        parameterMap.put("pl14_transfer_charges", transferCharges);
        parameterMap.put("pl14_vat_amount", vatAmount);
        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_L14_UPDATE_ADMIN_FEE, parameterMap);
    }
    
    @Override
    public List<Map<String, Object>> getCashAccDataForConcentration(String toDate) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("vdate", toDate);
        return oracleRepository.getProcResult(DBConstants.PKG_M04_REPORTS, DBConstants.PROC_CSHDTL_FOR_CONCENTRAION_RTP, parameterMap);
    }

    @Override
    public List<Map<String, Object>> getCashAccDataForConcentrationToday() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_M04_REPORTS, DBConstants.PROC_CSHDTL_FOR_CONCENTRAION_RTP_TODAY, parameterMap);
    }

    @Override
    public List<Map<String, Object>> getStockDataForConcentration(String toDate) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("vdate", toDate);
        return oracleRepository.getProcResult(DBConstants.PKG_M04_REPORTS, DBConstants.PROC_STKDTL_FOR_CONCENTRAION_RTP, parameterMap);
    }

    @Override
    public List<Map<String, Object>> getStockDataForConcentrationToday() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_M04_REPORTS, DBConstants.PROC_STKDTL_FOR_CONCENTRAION_RTP_TODAY, parameterMap);
    }

    @Override
    public List<MurabahaProduct> getMurabahaProducts(){
        String GET_MURABAHA_PRODUCTS = "select * from m07_murabaha_products";
        return oracleRepository.query(GET_MURABAHA_PRODUCTS, null, "MurabahaProduct");
    }

    @Override
    public List<Agreement> getAgreements(){
        String GET_AGREEMENTS = "select * from m11_agreements";
        return oracleRepository.query(GET_AGREEMENTS, null, "AgreementList");
    }


    @Override
    public String updateMurabahaProduct(MurabahaProduct murabahaProduct) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm07_type", murabahaProduct.getProductType());
        parameterMap.put("pm07_name", murabahaProduct.getProductName());
        // parameterMap.put("pm07_name ", murabahaProduct.getProductName());
        parameterMap.put("pm07_description", murabahaProduct.getProductDescription());
        parameterMap.put("pm07_ar_name", murabahaProduct.getProductNameAR());
        parameterMap.put("pm07_ar_description", murabahaProduct.getProductDescriptionAR());
        parameterMap.put("pm07_finance_method_config",murabahaProduct.getFinanceMethodConfig());

        return oracleRepository.executeProc(DBConstants.PKG_M07_MURABAHA_PRODUCTS, DBConstants.M07_UPDATE_PRODUCTS, parameterMap);
    }

    @Override
    public String changeMurabahaProductStatus(MurabahaProduct murabahaProduct) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm07_type", murabahaProduct.getProductType());
        parameterMap.put("pm07_status", murabahaProduct.getStatus());

        return oracleRepository.executeProc(DBConstants.PKG_M07_MURABAHA_PRODUCTS, DBConstants.M07_CHANGE_PRODUCT_STATUS, parameterMap);

    }

    @Override
    public MurabahaProduct getMurabahaProduct(int productId) {

        List<MurabahaProduct> murabahaProducts = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm07_type", productId);
        murabahaProducts = oracleRepository.getProcResult(DBConstants.PKG_M07_MURABAHA_PRODUCTS, DBConstants.M07_GET_PRODUCT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.MURABAHA_PRODUCT));
        if (murabahaProducts != null && murabahaProducts.size() > 0) {
            return murabahaProducts.get(0);
        } else {
            return null;
        }
    }
    

    @Override
    public OMSCommission getExchangeAccCommission(String ExchangeAccount) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pexchangeAcc", ExchangeAccount);
        List<OMSCommission> omsCommissions = oracleRepository.getProcResult(DBConstants.ML_OMS_PKG, DBConstants.ML_TOTAL_COMMISSION, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.OMS_COMMISSION));

        if(omsCommissions.size() > 0){
            return  omsCommissions.get(0);
        }else {
            return  null;
        }
    }

    /*--------Profit Calculation Job Related------*/
    
    @Override       
    public String insertProfitCalculationMasterEntry(int profitCalculationEligibleAppCount) {
        List<String> responseParams = new ArrayList<>();
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm08_eligible_app_count", profitCalculationEligibleAppCount);
        return oracleRepository.executeProc(DBConstants.PKG_M08_PROFIT_CAL_M_DATA, DBConstants.M08_ADD_PROFIT_CAL_ENTRY, parameterMap);
    }

    @Override
    public List<ProfitCalMurabahaApplication> getProfitCalculationEligibleApplications() {
        return oracleRepository.getProcResult(DBConstants.PKG_L01_APPLICATION, DBConstants.L01_GET_PROFIT_CAL_ELIGIBLE_APPLICATIONS, null, rowMapperFactory.getRowMapper(RowMapperI.PROFIT_CAL_MURABAHA_APPLICATION));

    }

    @Override
    public ProfitCalculationMasterEntry getLastProfitCalculationEntry() {
        List<ProfitCalculationMasterEntry> profitEntries = null;
        profitEntries = oracleRepository.getProcResult(DBConstants.PKG_M08_PROFIT_CAL_M_DATA, DBConstants.M08_GET_PROFIT_CAL_LAST_ENTRY, null, rowMapperFactory.getRowMapper(RowMapperI.PROFIT_CAL_MURABAHA_APPLICATION));
        if (profitEntries != null && profitEntries.size() > 0) {
            return profitEntries.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<OrderProfit> getProfitEntryForApplicationAndDate(String applicationID, String dateStr) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl23_application_id", applicationID);
        parameterMap.put("pl23_date_str", dateStr);
        List<OrderProfit> orderProfitList = oracleRepository.getProcResult(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_GET_ENTRY_FOR_DATE, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.ORDER_PROFIT));
        if (orderProfitList != null && orderProfitList.size() > 0) {
            return orderProfitList;
        } else {
            return null;
        }
    }

    @Override
    public String correctProfitEntry(String applicationID, String customerID, String dateStr) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pdate_str", dateStr);
        parameterMap.put("papp_id", applicationID);
        parameterMap.put("pcustomer_id", customerID);
        return oracleRepository.executeProc(DBConstants.PKG_L23_ORDER_PROFIT_LOG, DBConstants.L23_CORRECT_PROFIT_ENTRY, parameterMap);
    }

    @Override
    public String updateAgreementByAdmin(Agreement agreement, String ip, String userID, String userName){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm11_product_type", agreement.getProductType());
        parameterMap.put("pm11_finance_method", agreement.getFinanceMethod());
        parameterMap.put("pm11_agreement_type", agreement.getAgreementType());
        parameterMap.put("pm11_file_extension", agreement.getFileExtension());
        parameterMap.put("pm11_file_name", agreement.getFileName());
        parameterMap.put("pm11_file_path", agreement.getFilePath());
        parameterMap.put("pm11_version", agreement.getVersion());
        parameterMap.put("pm11_uploaded_user_id", userID);
        parameterMap.put("pm11_uploaded_user_name", userName);
        parameterMap.put("pm11_uploaded_ip", ip);
        return oracleRepository.executeProc(DBConstants.M11_AGREEMENT_PKG, DBConstants.M11_UPDATE_BY_ADMIN, parameterMap);
    }

    @Override
    public String updateStatusByUser(int applicationID, int status){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl32_l01_app_id",applicationID);
        parameterMap.put("pl32_agreement_status",status);

        return oracleRepository.executeProc(DBConstants.L32_APPROVE_AGREEMENTS_PKG,DBConstants.APPROVE_AGREEMENT_BY_USER, parameterMap);
    }

    @Override
    public String initialAgreementStatus(int id, int financeMethod, int productType, int agreementType){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl32_l01_app_id",id);
        parameterMap.put("pfinancemethod",financeMethod);
        parameterMap.put("pproduct_type",productType);
        parameterMap.put("pm11_agreement_type",agreementType);

        return oracleRepository.executeProc(DBConstants.L32_APPROVE_AGREEMENTS_PKG,DBConstants.L32_INITIAL_ADD, parameterMap);
    }

    @Override
    public List<Agreement> getActiveAgreements(int id){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl32_l01_app_id", id);
        return oracleRepository.getProcResult(DBConstants.L32_APPROVE_AGREEMENTS_PKG, DBConstants.L32_GET_AGREEMENT_BY_ID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.AGREEMENT_LIST));
    }

    @Override
    public List<Agreement> getActiveAgreementsForProduct(int financeMethod,int productType){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm11_finance_method", financeMethod);
        parameterMap.put("pm11_product_type", productType);
        return oracleRepository.getProcResult(DBConstants.M11_AGREEMENT_PKG, DBConstants.M11_GET_ACTIVE_FOR_PRODUCT, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.AGREEMENT_LIST));
    }

    @Override
    public String addCommodityToMaster(Commodity commodity){
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm12_commodity_code",commodity.getSymbolCode());
        parameterMap.put("pm12_commodity_name",commodity.getSymbolName());
        parameterMap.put("pm12_exchange",commodity.getExchange());
        parameterMap.put("pm12_broker",commodity.getBroker());
        parameterMap.put("pm12_description",commodity.getShortDescription());
        parameterMap.put("pm12_unit_of_measure",commodity.getUnitOfMeasure());
        parameterMap.put("pm12_price",commodity.getPrice());
        parameterMap.put("pm12_status",commodity.getStatus());

        return oracleRepository.executeProc(DBConstants.M12_COMMODITIES_PKG,DBConstants.M12_ADD_UPDATE, parameterMap);
    }

    @Override
    public String deleteCommodity(String pm12id) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm12_id", pm12id);
        return oracleRepository.executeProc(DBConstants.M12_COMMODITIES_PKG, DBConstants.PROC_M12_DELETE, parameterMap);
    }

    @Override
    public List<Commodity> getAllActiveCommodities(){
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.M12_COMMODITIES_PKG, DBConstants.M12_GET_ALL_ACTIVE_COMMODITY, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.COMMODITY_LIST));
    }

    @Override
    public String addAuthAbicToSellStatus(PurchaseOrder po) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl14_purchase_ord_id",po.getId());
        parameterMap.put("pl14_auth_abic_to_sell",po.getAuthAbicToSell());
        parameterMap.put("pl14_physical_delivery",po.getIsPhysicalDelivery());

        return oracleRepository.executeProc(DBConstants.PKG_L14_PURCHASE_ORDER,DBConstants.L14_UPDATE_AUTH_ABIC_TO_SELL, parameterMap);
    }

    @Override
    public List<PurchaseOrder> getPOForSetAuthAbicToSell(int gracePrd) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pm01_grace_per_commodity_sell",gracePrd);
        return oracleRepository.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.L14_GET_PO_FOR_SET_AUTH_ABIC_TO_SELL, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.PURCHASE_ORDER));
    }

    @Override
    public List<InstumentType> loadSymbolInstrumentTypes() {
        Map<String, Object> parameterMap = new HashMap<>();
        return oracleRepository.getProcResult(DBConstants.PKG_L08_SYMBOL, DBConstants.PROC_l08_GET_ALL_INSTRUMENT_TYPES, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.EXCHANGE_INSTRUMENT_TYPE));
    }

    @Override
    public List<Documents> getCustomerDocumentListByAppID(String applicationID) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pl04_l01_app_id", applicationID);
        return oracleRepository.getProcResult(DBConstants.PKG_L04_APPLICATION_DOC, DBConstants.PROC_L04_GET_USER_DOCS_BY_APPID, parameterMap, rowMapperFactory.getRowMapper(RowMapperI.USER_APPLICATION_DOCUMENTS));

    }

    // private final RowMapper<MurabahApplication> murabahApplicationRowMapper = (rs, rowNum) -> {
    //     MurabahApplication application = new MurabahApplication();
    //     application.setId(rs.getString("l01_app_id"));
    //     application.setCustomerId(rs.getString("l01_customer_id"));
    //     application.setFullName(rs.getString("l01_full_name"));
    //     application.setOccupation(rs.getString("l01_occupation"));
    //     application.setEmployer(rs.getString("l01_employer"));
    //     application.setSelfEmp(rs.getInt("l01_is_self_emp") == 1);
    //     application.setLineOfBusiness(rs.getString("l01_line_of_bisiness"));
    //     application.setAvgMonthlyIncome(rs.getDouble("l01_avg_monthly_income"));
    //     application.setFinanceRequiredAmt(rs.getDouble("l01_finance_req_amt"));
    //     application.setAddress(rs.getString("l01_address"));
    //     application.setMobileNo(rs.getString("l01_mobile_no"));
    //     application.setTeleNo(rs.getString("l01_telephone_no"));
    //     application.setEmail(rs.getString("l01_email"));
    //     application.setFax(rs.getString("l01_fax"));
    //     application.setDibAcc(rs.getString("l01_dib_acc"));
    //     application.setTradingAcc(rs.getString("l01_trading_acc"));
    //     application.setOtherBrkAvailable(rs.getInt("l01_is_other_brk_available") == 1);
    //     application.setOtherBrkNames(rs.getString("l01_other_brk_names"));
    //     application.setOtherBrkAvgPf(rs.getString("l01_other_brk_avg_pf"));
    //     application.setOverallStatus(rs.getString("l01_overall_status"));
    //     application.setCurrentLevel(rs.getInt("l01_current_level"));
    //     application.setTypeofFacility(rs.getString("l01_type_of_facility"));
    //     application.setDate(rs.getString("l01_date"));
    //     application.setFacilityType(rs.getString("l01_facility_type"));
    //     application.setProposalDate(rs.getString("l01_proposal_date"));
    //     application.setProposedLimit(rs.getDouble("l01_proposal_limit"));
    //     application.setReversedTo(rs.getString("l01_revised_to"));
    //     application.setReversedFrom(rs.getString("l01_revised_from"));
    //     application.setIsEditable(rs.getInt("l01_is_locked") == 0);
    //     application.setIsReversed(rs.getInt("l01_is_reversed") == 1);
    //     application.setIsEdited(rs.getInt("l01_is_edited") == 1);
    //     application.setInitialRAPV(rs.getDouble("l01_initial_rapv"));
    //     application.setCashAccount(rs.getString("l01_cash_acc"));
    //     application.setAvailableCashBalance(rs.getDouble("l01_cash_balance"));
    //     application.setTradingAccExchange(rs.getString("l01_trading_acc_exchange"));
    //     application.setReviewDate(rs.getString("l01_review_date"));
    //     application.setAdminFeeCharged(rs.getDouble("l01_admin_fee_charged"));
    //     application.setMaximumNumberOfSymbols(rs.getInt("l01_max_symbol_cnt"));
    //     application.setOtp(rs.getString("l01_otp"));
    //     application.setOtpGeneratedTime(rs.getLong("l01_otp_generated_time"));
    //     application.setCustomerReferenceNumber(rs.getString("l01_customer_ref_no"));
    //     application.setZipCode(rs.getString("l01_zip_code"));
    //     application.setBankBranchName(rs.getString("l01_bank_brch_name"));
    //     application.setCity(rs.getString("l01_city"));
    //     application.setPoBox(rs.getString("l01_pobox"));
    //     application.setLsfAccountDeletionState(rs.getInt("l01_acc_closed_status"));
    //     application.setPreferedLanguage(rs.getString("l01_prefered_language"));
    //     application.setDiscountOnProfit(rs.getInt("l01_discount_on_profit"));
    //     application.setProfitPercentage(rs.getDouble("l01_profit_percentage"));
    //     application.setAutomaticSettlement(rs.getInt("l01_automatic_settlement") == 1);
    //     application.setProductType(rs.getInt("l01_product_type"));
    //     application.setLastProfitDate(rs.getString("l01_last_profit_date"));
    //     application.setRolloverAppId(rs.getString("l01_rollover_app_id"));
    //     application.setRolloverCount(rs.getInt("l01_rollover_count"));
    //     return application;
    // };

    // private final RowMapper<MarginabilityGroup> marginabilityGroupRowMapper = (rs, rowNum) -> {
    //     MarginabilityGroup marginabilityGroup = new MarginabilityGroup();
    //     marginabilityGroup.setId(rs.getString("l11_marginability_grp_id"));
    //     marginabilityGroup.setGroupName(rs.getString("l11_group_name"));
    //     marginabilityGroup.setCreatedDate(rs.getString("l11_created_date"));
    //     marginabilityGroup.setStatus(rs.getInt("l11_status"));
    //     marginabilityGroup.setCreatedBy(rs.getString("l11_created_by"));
    //     marginabilityGroup.setApprovedBy(rs.getString("l11_approved_by"));
    //     marginabilityGroup.setIsDefault(rs.getInt("l11_is_default"));
    //     marginabilityGroup.setAdditionalDetails(rs.getString("l11_additional_details"));
    //     marginabilityGroup.setGlobalMarginablePercentage(rs.getDouble("l11_global_marginability_perc"));
    //     return marginabilityGroup;
    // };
}