package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.FTVInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 2/15/2016.
 */
public class FTVDetailedInfoMapper implements RowMapper<FTVInfo> {
    @Override
    public FTVInfo mapRow(ResultSet rs, int i) throws SQLException {
        FTVInfo ftvInfo = new FTVInfo();
        ftvInfo.setCustomerID(rs.getString("l01_customer_id"));
        ftvInfo.setCustomerName(rs.getString("l01_full_name"));
        ftvInfo.setApplicationID(rs.getString("l01_app_id"));
        ftvInfo.setOperativeLimit(rs.getDouble("l05_operative_limit_amt"));
        ftvInfo.setLimitUtilized(rs.getDouble("l05_utilized_limit_amt"));
        ftvInfo.setRemainingOperativeLimit(rs.getDouble("l05_rem_operative_limit_amt"));
        ftvInfo.setOutstandingAmount(rs.getDouble("l05_outstanding_amt"));
        ftvInfo.setFtv(rs.getDouble("l05_ftv"));
        ftvInfo.setAdminFeeCharged(rs.getDouble("l01_admin_fee_charged"));

        ftvInfo.setTradingAcc(rs.getString("l06_trading_acc_id"));
        ftvInfo.setAmountGranted(rs.getDouble("l01_finance_req_amt"));
        ftvInfo.setAmountUtilized(rs.getDouble("l14_ord_completed_value"));
        ftvInfo.setCumProfit(rs.getDouble("cumProfit"));
        ftvInfo.setPfMarketValue(rs.getDouble("PF_market_value"));
        ftvInfo.setMarginablePfMarketValue(rs.getDouble("Marginable_PF_value"));
        ftvInfo.setFtvPreviousDay(rs.getDouble("l05_ftv_pre_date"));
        ftvInfo.setStartDate(rs.getString("Start_date"));
        ftvInfo.setTenor(rs.getInt("Tenor"));
        ftvInfo.setExpireDate(rs.getString("Expiry_date"));
        ftvInfo.setDayesLeft(rs.getInt("Days_left"));
        return ftvInfo;
    }
}
