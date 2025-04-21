package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Symbol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/31/2015.
 */
public class LoadSymbolMapper implements RowMapper<Symbol> {
    @Override
    public Symbol mapRow(ResultSet rs, int i) throws SQLException {
        Symbol symbol = new Symbol();
        symbol.setSymbolCode(rs.getString("l08_symbol_code"));
        symbol.setExchange(rs.getString("l08_exchange"));
        symbol.setShortDescription(rs.getString("l08_short_desc"));
        symbol.setPreviousClosed(Double.parseDouble(rs.getString("l08_previous_closed")));
        symbol.setAvailableQty(rs.getInt("l08_available_qty"));
        symbol.setMarketValue(Double.parseDouble(rs.getString("l08_market_value")));
        return symbol;
    }
}
