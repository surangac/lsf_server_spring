package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.SettlementSummaryResponse;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SettlementListMapper implements RowMapper<SettlementSummaryResponse> {
    @Override
    public SettlementSummaryResponse mapRow(ResultSet rs, int i) throws SQLException {
        SettlementSummaryResponse settlementSummary = new SettlementSummaryResponse();
        settlementSummary.setApplicationID(rs.getString("l01_app_id"));
        settlementSummary.setCustomerID(rs.getString("l01_customer_id"));
        settlementSummary.setSettlementDate(rs.getString("l14_settlement_date"));
        settlementSummary.setCustomerName(rs.getString("l01_full_name"));
        settlementSummary.setTradingAccNumber(rs.getString("l06_trading_acc_id"));
        settlementSummary.setLoanAmount(rs.getDouble("l14_ord_completed_value"));
        settlementSummary.setSettelmentStatus(rs.getInt("l14_settlement_status"));
        settlementSummary.setLsfAccountDeletionState(rs.getInt("l01_acc_closed_status"));
        settlementSummary.setAvailableCashBalance(rs.getDouble("l07_cash_balance"));
        settlementSummary.setCumulativeProfit(rs.getDouble("cumProfit"));
        if(rs.getInt("l14_customer_approve_state") == 0){
            settlementSummary.setIsCustomerApproved(false);
        }else{
            settlementSummary.setIsCustomerApproved(true);
        }
        return settlementSummary;
    }
}
