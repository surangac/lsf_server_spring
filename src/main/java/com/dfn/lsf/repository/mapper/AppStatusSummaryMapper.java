package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.ApplicationStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 11/10/2016.
 */
public class AppStatusSummaryMapper implements RowMapper<ApplicationStatus> {
    @Override
    public ApplicationStatus mapRow(ResultSet resultSet, int i) throws SQLException {
        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setApplicationID(resultSet.getString("l01_app_id") != null ? resultSet.getString("l01_app_id") : "");
        applicationStatus.setCustomerID(resultSet.getString("l01_customer_id") != null ? resultSet.getString("l01_customer_id") : "");
        applicationStatus.setCustomerName(resultSet.getString("l01_full_name") != null ? resultSet.getString("l01_full_name") : "");
        applicationStatus.setStatusDescription(resultSet.getString("m02_state_description") != null ? resultSet.getString("m02_state_description") : "");
        applicationStatus.setCurrentLevel(resultSet.getObject("l01_current_level") != null ? resultSet.getInt("l01_current_level") : 0);
        applicationStatus.setOverallStatus(resultSet.getObject("l01_overall_status") != null ? resultSet.getInt("l01_overall_status") : 0);
        applicationStatus.setOrderFilledStatus(resultSet.getString("l14_ord_status") != null ? resultSet.getString("l14_ord_status") : "");
        applicationStatus.setOrderFilledValue(resultSet.getObject("l14_ord_completed_value") != null ? resultSet.getDouble("l14_ord_completed_value") : 0.0);
        applicationStatus.setSettledAmount(resultSet.getObject("l14_ord_settled_amount") != null ? resultSet.getDouble("l14_ord_settled_amount") : 0.0);
        applicationStatus.setCustomerApproveStatus(resultSet.getString("l14_customer_approve_state") != null ? resultSet.getString("l14_customer_approve_state") : "");
        applicationStatus.setSettlementStatus(resultSet.getString("l14_settlement_status") != null ? resultSet.getString("l14_settlement_status") : "");
        applicationStatus.setLiquidatedStatus(resultSet.getString("l14_liquidation_status") != null ? resultSet.getString("l14_liquidation_status") : "");
        applicationStatus.setCustomerActivityID(resultSet.getObject("l01_acc_activity_id") != null ? resultSet.getInt("l01_acc_activity_id") : 0);
        applicationStatus.setPortfolioNo(resultSet.getString("l06_trading_acc_id") != null ? resultSet.getString("l06_trading_acc_id") : "");
        applicationStatus.setDisplayApplicationID(resultSet.getString("l01_app_id") != null ? resultSet.getString("l01_app_id") : "");
        return applicationStatus;
    }
}
