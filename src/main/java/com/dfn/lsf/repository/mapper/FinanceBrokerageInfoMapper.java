package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.report.FinanceBrokerageInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 11/2/2015.
 */
public class FinanceBrokerageInfoMapper implements RowMapper<FinanceBrokerageInfo> {
    @Override
    public FinanceBrokerageInfo mapRow(ResultSet rs, int i) throws SQLException {
        FinanceBrokerageInfo financeBrokerageInfo = new FinanceBrokerageInfo();
        financeBrokerageInfo.setCustomerName(rs.getString("customer_name"));
        financeBrokerageInfo.setPortfolioNumber(rs.getString("portfolio_number"));
        financeBrokerageInfo.setOutstandingLoan(rs.getString("outstanding_loan"));
        financeBrokerageInfo.setCustomerExposure(rs.getString("customer_exposure"));
        financeBrokerageInfo.setAccumulatedProfit(rs.getString("accumulated_profit"));
        financeBrokerageInfo.setContractProfit(rs.getString("contract_profit"));
        financeBrokerageInfo.setTotalFees(rs.getString("total_fees"));
        financeBrokerageInfo.setTotalCommission(rs.getString("total_commission"));
        return financeBrokerageInfo;
    }
}
