package com.dfn.lsf.repository.mapper;


import com.dfn.lsf.model.CashAcc;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CashAccMapper implements RowMapper<CashAcc> {

    @Override
    public CashAcc mapRow(ResultSet rs, int i) throws SQLException {
        CashAcc obj = CashAcc.builder()
        .accountId(rs.getString("L07_CASH_ACC_ID"))
        .currencyCode(rs.getString("L07_CURRENCY_CODE"))
                             .cashBalance(rs.getDouble("L07_CASH_BALANCE"))
        .amountAsColletarals(rs.getDouble("L07_AMT_AS_COLLAT"))
                             .amountTransfered(rs.getDouble("L07_AMT_TRANSFERRED"))
                             .isLsfType(rs.getBoolean("L07_IS_LSF_TYPE"))
                             .collateralId(rs.getString("L07_L05_COLLATERAL_ID"))
                             .applicationId(rs.getString("L07_L01_APP_ID"))
                             .blockedReference(rs.getString("L07_BLOCK_REFERENCE"))
                             .transStatus(rs.getInt("L07_STATUS")).build();
        return obj;

    }
}
