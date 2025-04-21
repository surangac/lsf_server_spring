package com.dao.mapper.symbol;

import com.dfn.lsf.gbl.bo.symbol.SymbolClassifyLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 17/3/2016.
 */
public class SymbolClassifyLogMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        SymbolClassifyLog symbolClassifyLog = new SymbolClassifyLog();
        symbolClassifyLog.setSymbolCode(rs.getString("l31_symbol_code"));
        symbolClassifyLog.setPreviousLiquidType(rs.getString("PreviousLiqType"));
        symbolClassifyLog.setUpdatedLiquidType(rs.getString("UpdatedLiqType"));

        return symbolClassifyLog;
    }
}