package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.CommissionDetail;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommissionDetailsMapper implements RowMapper<CommissionDetail> {
    @Override
    public CommissionDetail mapRow(ResultSet resultSet, int i) throws SQLException {
        CommissionDetail commissionDetail = new CommissionDetail();
        commissionDetail.setCommission(resultSet.getString("commasAtDate"));
        commissionDetail.setPreviousDayCommission(resultSet.getString("commasPreviousDay"));
        commissionDetail.setTradingAccId(resultSet.getString("u06_exchange_ac"));
        return commissionDetail;
    }
}
