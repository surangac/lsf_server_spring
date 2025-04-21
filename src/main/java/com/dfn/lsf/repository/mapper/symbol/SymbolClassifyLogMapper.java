package com.dfn.lsf.repository.mapper.symbol;

import com.dfn.lsf.model.SymbolClassifyLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 17/3/2016.
 */
public class SymbolClassifyLogMapper implements RowMapper<SymbolClassifyLog> {
    @Override
    public SymbolClassifyLog mapRow(ResultSet rs, int i) throws SQLException {
        SymbolClassifyLog symbolClassifyLog = new SymbolClassifyLog();
        symbolClassifyLog.setSymbolCode(rs.getString("l31_symbol_code"));
        symbolClassifyLog.setPreviousLiquidType(rs.getString("PreviousLiqType"));
        symbolClassifyLog.setUpdatedLiquidType(rs.getString("UpdatedLiqType"));

        return symbolClassifyLog;
    }
}