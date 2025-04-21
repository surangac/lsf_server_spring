package com.dao.mapper;

import com.dfn.lsf.gbl.bo.OMSCommission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OMSCommissionMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        OMSCommission commission = new OMSCommission();
        commission.setBrokerCommission(rs.getDouble("t05_broker_commission"));
        commission.setExchangeCommission(rs.getDouble("t05_exg_commission"));
        return  commission;
    }
}
