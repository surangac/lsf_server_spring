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
        applicationStatus.setApplicationID(resultSet.getString("l01_app_id"));
        applicationStatus.setCustomerID(resultSet.getString("l01_customer_id"));
        applicationStatus.setCustomerName(resultSet.getString("l01_full_name"));
        applicationStatus.setStatusDescription(resultSet.getString("m02_state_description"));
        applicationStatus.setCurrentLevel(resultSet.getInt("l01_current_level"));
        applicationStatus.setOverallStatus(resultSet.getInt("l01_overall_status"));
        applicationStatus.setOrderFilledStatus(resultSet.getString("l14_ord_status"));
        applicationStatus.setOrderFilledValue(resultSet.getDouble("l14_ord_completed_value"));
        applicationStatus.setSettledAmount(resultSet.getDouble("l14_ord_settled_amount"));
        applicationStatus.setCustomerApproveStatus(resultSet.getString("l14_customer_approve_state"));
        applicationStatus.setSettlementStatus(resultSet.getString("l14_settlement_status"));
        applicationStatus.setLiquidatedStatus(resultSet.getString("l14_liquidation_status"));
        applicationStatus.setCustomerActivityID(resultSet.getInt("l01_acc_activity_id"));
        applicationStatus.setPortfolioNo(resultSet.getString("l06_trading_acc_id"));
        return applicationStatus;
    }
}
