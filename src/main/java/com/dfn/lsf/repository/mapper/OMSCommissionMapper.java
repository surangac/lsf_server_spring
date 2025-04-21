package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.OMSCommission;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OMSCommissionMapper implements RowMapper<OMSCommission> {
    @Override
    public OMSCommission mapRow(ResultSet rs, int i) throws SQLException {
        OMSCommission commission = new OMSCommission();
        commission.setBrokerCommission(rs.getDouble("t05_broker_commission"));
        commission.setExchangeCommission(rs.getDouble("t05_exg_commission"));
        return  commission;
    }
}
