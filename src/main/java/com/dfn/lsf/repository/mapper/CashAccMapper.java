package com.dfn.lsf.repository.mapper;


import com.dfn.lsf.model.CashAcc;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CashAccMapper implements RowMapper<CashAcc> {

    @Override
    public CashAcc mapRow(ResultSet rs, int i) throws SQLException {
        CashAcc obj = new CashAcc();

        obj.setAccountId(rs.getString("L07_CASH_ACC_ID"));
        obj.setCurrencyCode(rs.getString("L07_CURRENCY_CODE"));
        obj.setCashBalance(rs.getDouble("L07_CASH_BALANCE"));
        obj.setAmountAsColletarals(rs.getDouble("L07_AMT_AS_COLLAT"));
        obj.setAmountTransfered(rs.getDouble("L07_AMT_TRANSFERRED"));
        obj.setLsfType(rs.getBoolean("L07_IS_LSF_TYPE"));
        obj.setCollateralId(rs.getString("L07_L05_COLLATERAL_ID"));
        obj.setApplicationId(rs.getString("L07_L01_APP_ID"));
        obj.setBlockedReference(rs.getString("L07_BLOCK_REFERENCE"));
        obj.setTransStatus(rs.getInt("L07_STATUS"));
        return obj;

    }
}
