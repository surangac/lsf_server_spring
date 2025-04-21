package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.SymbolMarginabilityPercentage;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SymbolMarginabilityPercentageMapper  implements RowMapper<SymbolMarginabilityPercentage> {

    @Override
    public SymbolMarginabilityPercentage mapRow(final ResultSet rs, final int i) throws SQLException {

        SymbolMarginabilityPercentage sm = new SymbolMarginabilityPercentage();

        if(isColumnExists(rs, "security_name") && rs.getString("security_name") != null) {
            sm.setSecurityName(rs.getString("security_name"));
        }

        if(isColumnExists(rs, "security_name_ar") && rs.getString("security_name_ar") != null) {
            sm.setSecurityNameAr(rs.getString("security_name_ar"));
        }

        if(isColumnExists(rs, "exchange") && rs.getString("exchange") != null) {
            sm.setExchange(rs.getString("exchange"));
        }

        if(isColumnExists(rs, "group_name") && rs.getString("group_name") != null) {
            sm.setGroupName(rs.getString("group_name"));
        }

        if(isColumnExists(rs, "is_marginable")) {
            sm.setIsMarginable(rs.getInt("is_marginable"));
        }

        if(isColumnExists(rs, "symbol_code") && rs.getString("symbol_code") != null) {
            sm.setSymbolCode(rs.getString("symbol_code"));
        }

        if(isColumnExists(rs, "marginability_percentage")) {
            sm.setMarginabilityPercentage(rs.getDouble("marginability_percentage"));
        }

        if(isColumnExists(rs, "marginability_group_id")) {
            sm.setGroupId(rs.getDouble("marginability_group_id"));
        }

        return sm;
    }

    private boolean isColumnExists(final ResultSet rs, final String column) throws SQLException {

        final ResultSetMetaData meta = rs.getMetaData();
        final int columnCount = meta.getColumnCount();

        boolean flag = false;
        for (int i = 1; i <=columnCount; i++) {
            if(meta.getColumnName(i).equals(column)) {
                flag = true;
            }
        }

        return flag;
    }
}
