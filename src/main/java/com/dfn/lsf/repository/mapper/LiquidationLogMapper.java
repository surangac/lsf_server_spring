package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.LiquidationLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 9/21/2015.
 */
public class LiquidationLogMapper implements RowMapper<LiquidationLog> {
    @Override
    public LiquidationLog mapRow(ResultSet rs, int i) throws SQLException {
        LiquidationLog liquidationLog = new LiquidationLog();
        liquidationLog.setLiquidationReference(rs.getInt("l24_liquidate_reference"));
        liquidationLog.setFromAccount(rs.getString("l24_cash_from_account"));
        liquidationLog.setToAccount(rs.getString("l24_cash_to_account"));
        liquidationLog.setAmountToBeSettled(rs.getDouble("l24_transfer_amount"));
        liquidationLog.setApplicationID(rs.getString("l24_l01_application_id"));
        liquidationLog.setStatus(rs.getInt("l24_status"));
        liquidationLog.setOrderID(rs.getString("l24_order_id"));
        return liquidationLog;
    }
}
