package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Symbol;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 10/25/2016.
 */
public class SymbolDescriptionMapper implements RowMapper<Symbol> {

    @Override
    public Symbol mapRow(ResultSet resultSet, int i) throws SQLException {
        Symbol symbol = new Symbol();
        symbol.setSymbolCode(resultSet.getString("symbolcode").toString());
        symbol.setArabicName(resultSet.getString("short_dis_ar").toString());
        symbol.setEnglishName(resultSet.getString("short_dis_eng").toString());
        return symbol;
    }
}
