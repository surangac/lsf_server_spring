package com.dfn.lsf.repository.mapper;


import com.dfn.lsf.model.StockConcentrationGroup;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StockConcentrationGroupMapper implements RowMapper<StockConcentrationGroup>{
    @Override
    public StockConcentrationGroup mapRow(ResultSet rs, int i) throws SQLException {
        StockConcentrationGroup obj = new StockConcentrationGroup();

        obj.setId(rs.getString("L12_STOCK_CONC_GRP_ID"));
        obj.setGroupName(rs.getString("L12_GROUP_NAME"));
        obj.setCreatedDate(rs.getString("L12_CREATED_DATE"));
        obj.setStatus(rs.getInt("L12_STATUS"));
        obj.setCreatedBy(rs.getString("L12_CREATED_BY"));
        obj.setApprovedBy(rs.getString("L12_APPROVED_BY"));
        obj.setIsDefault(rs.getInt("l12_is_default"));
        return obj;
    }
}
