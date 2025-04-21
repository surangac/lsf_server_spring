package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Symbol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/6/2015.
 */
public class InitialPortfolioMapper implements RowMapper<Symbol> {
    @Override
    public Symbol mapRow(ResultSet rs, int i) throws SQLException {
        Symbol obj = new Symbol();
        obj.setSymbolCode(rs.getString("L20_SYMBOL_CODE"));
        obj.setExchange(rs.getString("L20_EXCHANGE"));
      //  obj.setShortDescription(rs.getString("L08_SHORT_DESC"));
        obj.setPreviousClosed(rs.getDouble("L20_PREVIOUS_CLOSED"));
        obj.setAvailableQty(rs.getInt("L20_AVAILABLE_QTY"));
        return  obj;
    }
}
