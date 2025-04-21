package com.dfn.lsf.repository.mapper;


import com.dfn.lsf.model.TradingAcc;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TradingAccMapper implements RowMapper<TradingAcc> {
    @Override
    public TradingAcc mapRow(ResultSet rs, int i) throws SQLException {
        TradingAcc obj = new TradingAcc();

        obj.setAccountId(rs.getString("L06_TRADING_ACC_ID"));
        obj.setLsfType(rs.getBoolean("L06_IS_LSF_TYPE"));
        obj.setExchange(rs.getString("L06_EXCHANGE"));
        obj.setCollateralId(rs.getString("L06_L05_COLLATERAL_ID"));
        obj.setApplicationId(rs.getString("L06_L01_APP_ID"));

        return obj;
    }
}

