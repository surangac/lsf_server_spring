// package com.dfn.lsf.repository.impl;

// import com.dfn.lsf.repository.LSFRepository;
// import com.dfn.lsf.model.MurabahApplication;
// import com.dfn.lsf.model.PurchaseOrder;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.dao.EmptyResultDataAccessException;
// import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
// import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
// import org.springframework.jdbc.core.simple.SimpleJdbcCall;
// import org.springframework.stereotype.Repository;
// import com.dfn.lsf.util.DBConstants;

// import javax.sql.DataSource;
// import java.sql.Timestamp;
// import java.text.SimpleDateFormat;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.Date;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;

// /**
//  * JDBC implementation of LSFRepository
//  * This replaces the original LSFDaoSQLImpl class
//  */
// @Repository
// public class LSFJdbcRepository implements LSFRepository {
    
//     private static final Logger logger = LoggerFactory.getLogger(LSFJdbcRepository.class);
    
//     @Autowired
//     private JdbcTemplate jdbcTemplate;
    
//     @Autowired
//     private DataSource dataSource;
    
//     private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
//     @Autowired
//     public LSFJdbcRepository(DataSource dataSource) {
//         this.dataSource = dataSource;
//         this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
//     }
    
//     @Override
//     public List<Map<String, Object>> getApplicationCollateralFtvList() {
//         try {
//             String sql = "SELECT FTV FROM LSF_M_APPLICATION_COLLATERALS";
//             return jdbcTemplate.queryForList(sql);
//         } catch (Exception e) {
//             logger.error("Error getting application collateral FTV list", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getDetailedFTVList(String fromDate, String toDate, int settleStatus) {
//         try {
//             StringBuilder sql = new StringBuilder();
//             sql.append("SELECT * FROM LSF_FTV_DETAILED_INFO WHERE 1=1");
            
//             if (fromDate != null && !fromDate.isEmpty()) {
//                 sql.append(" AND TRUNC(CREATED_DATE) >= TO_DATE('").append(fromDate).append("', 'DDMMYYYY')");
//             }
            
//             if (toDate != null && !toDate.isEmpty()) {
//                 sql.append(" AND TRUNC(CREATED_DATE) <= TO_DATE('").append(toDate).append("', 'DDMMYYYY')");
//             }
            
//             if (settleStatus != -1) {
//                 sql.append(" AND SETTLEMENT_STATUS = ").append(settleStatus);
//             }
            
//             return jdbcTemplate.queryForList(sql.toString());
//         } catch (Exception e) {
//             logger.error("Error getting detailed FTV list", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getCommissionDetails(String toDate) {
//         try {
//             String sql = "SELECT * FROM LSF_COMMISSION_DETAILS WHERE TRUNC(CREATED_DATE) <= TO_DATE(?, 'DDMMYYYY')";
//             return jdbcTemplate.queryForList(sql, toDate);
//         } catch (Exception e) {
//             logger.error("Error getting commission details", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getApprovedPurchaseOrderApplicationList(Object fromDate, Object toDate) {
//         try {
//             String sql = "SELECT * FROM LSF_MURABAH_APPLICATION WHERE STATUS = 'APPROVED'";
//             if (fromDate != null) {
//                 sql += " AND CREATED_DATE >= ?";
//             }
//             if (toDate != null) {
//                 sql += " AND CREATED_DATE <= ?";
//             }
            
//             if (fromDate != null && toDate != null) {
//                 return jdbcTemplate.queryForList(sql, fromDate, toDate);
//             } else if (fromDate != null) {
//                 return jdbcTemplate.queryForList(sql, fromDate);
//             } else if (toDate != null) {
//                 return jdbcTemplate.queryForList(sql, toDate);
//             } else {
//                 return jdbcTemplate.queryForList(sql);
//             }
//         } catch (Exception e) {
//             logger.error("Error getting approved purchase order application list", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getApplicationStatus(String applicationId) {
//         try {
//             String sql = "SELECT * FROM LSF_APPLICATION_STATUS WHERE APPLICATION_ID = ? ORDER BY CREATED_DATE";
//             return jdbcTemplate.queryForList(sql, applicationId);
//         } catch (Exception e) {
//             logger.error("Error getting application status", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getBlackListedApplications() {
//         try {
//             String sql = "SELECT * FROM LSF_MURABAH_APPLICATION WHERE IS_BLACKLISTED = 1";
//             return jdbcTemplate.queryForList(sql);
//         } catch (Exception e) {
//             logger.error("Error getting blacklisted applications", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getApplicationFlow() {
//         try {
//             String sql = "SELECT * FROM LSF_M_APPLICATION_FLOW";
//             return jdbcTemplate.queryForList(sql);
//         } catch (Exception e) {
//             logger.error("Error getting application flow", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public boolean updateApplicationFlowStatus(String applicationId, String userId, int status, 
//             String ipAddress, String comments) {
//         try {
//             String sql = "INSERT INTO LSF_APPLICATION_STATUS (APPLICATION_ID, USER_ID, STATUS, IP_ADDRESS, " +
//                     "COMMENTS, CREATED_DATE) VALUES (?, ?, ?, ?, ?, ?)";
            
//             int result = jdbcTemplate.update(sql, 
//                     applicationId, 
//                     userId, 
//                     status, 
//                     ipAddress, 
//                     comments, 
//                     new Timestamp(new Date().getTime()));
            
//             return result > 0;
//         } catch (Exception e) {
//             logger.error("Error updating application flow status", e);
//             return false;
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getApplicationFlowHistory(String applicationId) {
//         try {
//             String sql = "SELECT s.*, f.LEVEL_NAME, f.DESCRIPTION " +
//                     "FROM LSF_APPLICATION_STATUS s " +
//                     "JOIN LSF_M_APPLICATION_FLOW f ON s.STATUS = f.LEVEL_ID " +
//                     "WHERE s.APPLICATION_ID = ? " +
//                     "ORDER BY s.CREATED_DATE DESC";
//             return jdbcTemplate.queryForList(sql, applicationId);
//         } catch (Exception e) {
//             logger.error("Error getting application flow history", e);
//             return Collections.emptyList();
//         }
//     }
    
//     /*----- Authentication Operations -----*/
    
//     @Override
//     public Map<String, Object> authenticateUser(String username, String password, String ipAddress, int channelId) {
//         try {
//             // In a real implementation, this would perform actual authentication against the database
//             // For now, we'll simulate the authentication process
//             String sql = "SELECT * FROM LSF_USERS WHERE USERNAME = ? AND STATUS = 'ACTIVE'";
            
//             List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username);
            
//             if (users.isEmpty()) {
//                 logger.warn("User not found or inactive: {}", username);
//                 return Collections.emptyMap();
//             }
            
//             Map<String, Object> user = users.get(0);
            
//             // Validate password (in a real implementation, this would use proper password hashing)
//             // For the migration, we would use the same password validation logic as the original system
            
//             // Create session for authenticated user
//             String userId = user.get("USER_ID").toString();
//             String sessionKey = createUserSession(userId, username, ipAddress, channelId);
            
//             if (sessionKey != null) {
//                 Map<String, Object> result = new HashMap<>(user);
//                 result.put("SESSION_KEY", sessionKey);
//                 return result;
//             }
            
//             return Collections.emptyMap();
//         } catch (Exception e) {
//             logger.error("Error authenticating user", e);
//             return Collections.emptyMap();
//         }
//     }
    
//     @Override
//     public String createUserSession(String userId, String username, String ipAddress, int channelId) {
//         try {
//             String sessionKey = UUID.randomUUID().toString();
//             Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            
//             // Session expiry time (e.g., 1 hour from now)
//             Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));
            
//             String sql = "INSERT INTO LSF_USER_SESSION (SESSION_KEY, USER_ID, USERNAME, IP_ADDRESS, " +
//                     "CHANNEL_ID, CREATED_DATE, EXPIRY_DATE, STATUS) " +
//                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
            
//             int result = jdbcTemplate.update(sql, 
//                     sessionKey, 
//                     userId, 
//                     username, 
//                     ipAddress, 
//                     channelId, 
//                     currentTime, 
//                     expiryTime);
            
//             if (result > 0) {
//                 return sessionKey;
//             }
            
//             return null;
//         } catch (Exception e) {
//             logger.error("Error creating user session", e);
//             return null;
//         }
//     }
    
//     @Override
//     public boolean validateSession(String securityKey) {
//         try {
//             if (securityKey == null || securityKey.isEmpty()) {
//                 return false;
//             }
            
//             String sql = "SELECT COUNT(*) FROM LSF_USER_SESSION " +
//                     "WHERE SESSION_KEY = ? AND STATUS = 'ACTIVE' AND EXPIRY_DATE > ?";
            
//             Integer count = jdbcTemplate.queryForObject(sql, 
//                     Integer.class, 
//                     securityKey, 
//                     new Timestamp(System.currentTimeMillis()));
            
//             return count != null && count > 0;
//         } catch (Exception e) {
//             logger.error("Error validating session", e);
//             return false;
//         }
//     }
    
//     @Override
//     public Map<String, Object> getUserBySession(String securityKey) {
//         try {
//             String sql = "SELECT u.* FROM LSF_USERS u " +
//                     "JOIN LSF_USER_SESSION s ON u.USER_ID = s.USER_ID " +
//                     "WHERE s.SESSION_KEY = ? AND s.STATUS = 'ACTIVE' AND s.EXPIRY_DATE > ?";
            
//             return jdbcTemplate.queryForMap(sql, 
//                     securityKey, 
//                     new Timestamp(System.currentTimeMillis()));
//         } catch (EmptyResultDataAccessException e) {
//             logger.warn("No user found for session key: {}", securityKey);
//             return Collections.emptyMap();
//         } catch (Exception e) {
//             logger.error("Error getting user by session", e);
//             return Collections.emptyMap();
//         }
//     }
    
//     @Override
//     public boolean logoutUser(String securityKey) {
//         try {
//             String sql = "UPDATE LSF_USER_SESSION SET STATUS = 'INACTIVE' WHERE SESSION_KEY = ?";
            
//             int result = jdbcTemplate.update(sql, securityKey);
            
//             return result > 0;
//         } catch (Exception e) {
//             logger.error("Error logging out user", e);
//             return false;
//         }
//     }
    
//     /*----- Notification Operations -----*/
    
//     @Override
//     public List<Map<String, Object>> getNotificationConfigurations() {
//         try {
//             String sql = "SELECT * FROM LSF_NOTIFICATION_MSG_CONFIGURATION";
//             return jdbcTemplate.queryForList(sql);
//         } catch (Exception e) {
//             logger.error("Error getting notification configurations", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getCustomerNotifications(String customerId) {
//         try {
//             String sql = "SELECT * FROM LSF_WEB_NOTIFICATION " +
//                     "WHERE CUSTOMER_ID = ? AND STATUS = 'UNREAD' " +
//                     "ORDER BY CREATED_DATE DESC";
            
//             return jdbcTemplate.queryForList(sql, customerId);
//         } catch (Exception e) {
//             logger.error("Error getting customer notifications", e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public boolean sendNotification(String customerId, String messageType, String message, 
//             String subject, String notificationType) {
//         try {
//             Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            
//             // Different tables for different notification types
//             if ("WEB".equalsIgnoreCase(notificationType)) {
//                 String sql = "INSERT INTO LSF_WEB_NOTIFICATION (CUSTOMER_ID, MESSAGE_TYPE, " +
//                         "MESSAGE, SUBJECT, CREATED_DATE, STATUS) " +
//                         "VALUES (?, ?, ?, ?, ?, 'UNREAD')";
                
//                 int result = jdbcTemplate.update(sql, 
//                         customerId, 
//                         messageType, 
//                         message, 
//                         subject, 
//                         currentTime);
                
//                 return result > 0;
//             } else if ("SMS".equalsIgnoreCase(notificationType) || "EMAIL".equalsIgnoreCase(notificationType)) {
//                 // In a real implementation, this would send SMS/email via external service
//                 // For now, just log the notification in the database
//                 String sql = "INSERT INTO LSF_NOTIFICATION (CUSTOMER_ID, MESSAGE_TYPE, " +
//                         "MESSAGE, SUBJECT, NOTIFICATION_TYPE, CREATED_DATE, STATUS) " +
//                         "VALUES (?, ?, ?, ?, ?, ?, 'SENT')";
                
//                 int result = jdbcTemplate.update(sql, 
//                         customerId, 
//                         messageType, 
//                         message, 
//                         subject, 
//                         notificationType, 
//                         currentTime);
                
//                 return result > 0;
//             }
            
//             return false;
//         } catch (Exception e) {
//             logger.error("Error sending notification", e);
//             return false;
//         }
//     }
    
//     @Override
//     public boolean markNotificationAsRead(String notificationId) {
//         try {
//             String sql = "UPDATE LSF_WEB_NOTIFICATION SET STATUS = 'READ' WHERE ID = ?";
            
//             int result = jdbcTemplate.update(sql, notificationId);
            
//             return result > 0;
//         } catch (Exception e) {
//             logger.error("Error marking notification as read", e);
//             return false;
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getNotificationHistory(String customerId, String fromDate, String toDate) {
//         try {
//             StringBuilder sql = new StringBuilder(
//                     "SELECT * FROM LSF_NOTIFICATION WHERE CUSTOMER_ID = ?");
            
//             if (fromDate != null && !fromDate.isEmpty()) {
//                 sql.append(" AND CREATED_DATE >= TO_DATE(?, 'YYYY/MM/DD')");
//             }
            
//             if (toDate != null && !toDate.isEmpty()) {
//                 sql.append(" AND CREATED_DATE <= TO_DATE(?, 'YYYY/MM/DD')");
//             }
            
//             sql.append(" ORDER BY CREATED_DATE DESC");
            
//             if (fromDate != null && !fromDate.isEmpty() && toDate != null && !toDate.isEmpty()) {
//                 return jdbcTemplate.queryForList(sql.toString(), customerId, fromDate, toDate);
//             } else if (fromDate != null && !fromDate.isEmpty()) {
//                 return jdbcTemplate.queryForList(sql.toString(), customerId, fromDate);
//             } else if (toDate != null && !toDate.isEmpty()) {
//                 return jdbcTemplate.queryForList(sql.toString(), customerId, toDate);
//             } else {
//                 return jdbcTemplate.queryForList(sql.toString(), customerId);
//             }
//         } catch (Exception e) {
//             logger.error("Error getting notification history", e);
//             return Collections.emptyList();
//         }
//     }

//     @Override
//     public String transferCash(final String sourceAccount,
//                                final String destinationAccount,
//                                final double amount,
//                                final String userId,
//                                final String applicationId,
//                                final String description) {
//         return "";
//     }

//     @Override
//     public String transferShares(final String sourceAccount,
//                                  final String destinationAccount,
//                                  final String symbolCode,
//                                  final int quantity,
//                                  final String userId,
//                                  final String applicationId,
//                                  final String description) {
//         return "";
//     }

//     @Override
//     public Map<String, Object> getPortfolioDetails(final String tradingAccount) {
//         return Map.of();
//     }

//     @Override
//     public String createPurchaseOrder(final String applicationId,
//                                       final String tradingAccount,
//                                       final String cashAccount,
//                                       final String symbolCode,
//                                       final int quantity,
//                                       final double orderPrice,
//                                       final String userId) {
//         return "";
//     }

//     @Override
//     public List<Map<String, Object>> getSettlementsForProcessing(final int status) {
//         return List.of();
//     }

//     @Override
//     public boolean processSettlement(final String settlementId, final String userId) {
//         return false;
//     }

//     @Override
//     public List<Map<String, Object>> getReportList(String userId) {
//         try {
//             String sql = "SELECT * FROM LSF_REPORTS WHERE USER_ID = ? ORDER BY CREATED_DATE DESC";
//             return jdbcTemplate.queryForList(sql, userId);
//         } catch (Exception e) {
//             logger.error("Error getting report list for user: {}", userId, e);
//             return Collections.emptyList();
//         }
//     }
    
//     @Override
//     public Map<String, Object> getReportStatus(String reportId) {
//         try {
//             String sql = "SELECT * FROM LSF_REPORTS WHERE REPORT_ID = ?";
//             return jdbcTemplate.queryForMap(sql, reportId);
//         } catch (EmptyResultDataAccessException e) {
//             return Collections.emptyMap();
//         } catch (Exception e) {
//             logger.error("Error getting report status for report: {}", reportId, e);
//             return Collections.emptyMap();
//         }
//     }
    
//     @Override
//     public String generateReport(String reportType, Map<String, Object> parameters, String userId) {
//         try {
//             String reportId = UUID.randomUUID().toString();
//             String sql = "INSERT INTO LSF_REPORTS (REPORT_ID, REPORT_TYPE, PARAMETERS, USER_ID, STATUS, CREATED_DATE) " +
//                     "VALUES (?, ?, ?, ?, 'PENDING', ?)";
            
//             int result = jdbcTemplate.update(sql,
//                     reportId,
//                     reportType,
//                     new com.google.gson.Gson().toJson(parameters),
//                     userId,
//                     new Timestamp(System.currentTimeMillis()));
            
//             if (result > 0) {
//                 return reportId;
//             }
//             return null;
//         } catch (Exception e) {
//             logger.error("Error generating report of type: {} for user: {}", reportType, userId, e);
//             return null;
//         }
//     }
    
//     @Override
//     public Map<String, Object> getSettlementDetails(String settlementId) {
//         try {
//             String sql = "SELECT * FROM LSF_SETTLEMENTS WHERE SETTLEMENT_ID = ?";
//             return jdbcTemplate.queryForMap(sql, settlementId);
//         } catch (EmptyResultDataAccessException e) {
//             return Collections.emptyMap();
//         } catch (Exception e) {
//             logger.error("Error getting settlement details for settlement: {}", settlementId, e);
//             return Collections.emptyMap();
//         }
//     }
    
//     @Override
//     public List<Map<String, Object>> getSettlementList(String applicationId) {
//         try {
//             String sql = "SELECT * FROM LSF_SETTLEMENTS WHERE APPLICATION_ID = ? ORDER BY CREATED_DATE DESC";
//             return jdbcTemplate.queryForList(sql, applicationId);
//         } catch (Exception e) {
//             logger.error("Error getting settlement list for application: {}", applicationId, e);
//             return Collections.emptyList();
//         }
//     }

//     @Override
//     public PurchaseOrder getSinglePurchaseOrder(String orderId) {
//         PurchaseOrder po = null;
//         Map<String, Object> parameterMap = new HashMap<>();
//         parameterMap.put("pl14_purchase_ord_id", orderId);
//         List<PurchaseOrder> purchaseOrderList = oracleDaoImpl.getProcResult(DBConstants.PKG_L14_PURCHASE_ORDER, DBConstants.PROC_GET_ORDER, parameterMap, RowMapperI.PURCHASE_ORDER);

//         if (purchaseOrderList != null) {
//             if (!purchaseOrderList.isEmpty()) {
//                 po = purchaseOrderList.get(0);
//                 MurabahApplication application = getMurabahApplication(po.getApplicationId());
//                 po.setInstallments(this.getPurchaseOrderInstallments(po.getId()));
//                 if (application.getFinanceMethod().equalsIgnoreCase("1")){
//                     po.setCommodityList(this.getPurchaseOrderCommodities(po.getId()));
//                 }
//                 else if (application.getFinanceMethod().equalsIgnoreCase("2")) {
//                     po.setSymbolList(this.getPurchaseOrderSymbols(po.getId()));
//                 }
//             }
//         }
//         return po;
//     }
// }