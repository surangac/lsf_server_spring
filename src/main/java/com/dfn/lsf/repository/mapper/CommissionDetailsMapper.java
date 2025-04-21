package com.dao.mapper;

import com.dfn.lsf.gbl.bo.responseMsg.CommissionDetail;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommissionDetailsMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int i) throws SQLException {
        CommissionDetail commissionDetail = new CommissionDetail();
        commissionDetail.setCommission(resultSet.getString("commasAtDate"));
        commissionDetail.setPreviousDayCommission(resultSet.getString("commasPreviousDay"));
        commissionDetail.setTradingAccId(resultSet.getString("u06_exchange_ac"));
        return commissionDetail;
    }
}
