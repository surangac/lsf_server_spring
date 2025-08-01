package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.PendingActivity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 2/12/2017.
 */
public class PendingActivityMapper implements RowMapper<PendingActivity> {
    @Override
    public PendingActivity mapRow(ResultSet resultSet, int i) throws SQLException {
        PendingActivity pendingActivity = new PendingActivity();
        pendingActivity.setApplicationID(resultSet.getString("l01_app_id"));
        pendingActivity.setActivityID(resultSet.getInt("l01_acc_activity_id"));
        pendingActivity.setUserID(resultSet.getString("l01_customer_id"));
        pendingActivity.setActivityDescription(resultSet.getString("current_status"));
        pendingActivity.setOrderID(resultSet.getString("l14_purchase_ord_id"));
        pendingActivity.setNonLSFTradingAccount(resultSet.getString("nonlsftypetrading"));
        pendingActivity.setNonLSFCashAccount(resultSet.getString("nonlsftypecash"));
        pendingActivity.setLsfTypeTradingAccount(resultSet.getString("lsftypetrading"));
        pendingActivity.setLsfTypeCashAccount(resultSet.getString("lsftypecash"));
        pendingActivity.setDisplayApplicationID(resultSet.getString("l01_display_application_id"));
        return pendingActivity;
    }
}
