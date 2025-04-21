package com.dao.mapper;

import com.dfn.lsf.gbl.bo.Tenor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Nuskya on 7/20/2015.
 */
public class TenorMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        Tenor obj = new Tenor();
        obj.setTenorId(rs.getString("L15_TENOR_ID"));
        obj.setProfitPercentage(rs.getDouble("L15_PROFIT_PERCENT"));
        obj.setDuration(rs.getInt("L15_DURATION"));
        obj.setCreatedDate(rs.getString("L15_CREATED_DATE"));
        obj.setCreatedBy(rs.getString("L15_CREATED_BY"));
        obj.setApprovedby(rs.getString("l15_lvl1_approved_by"));
        obj.setApprovedDate(rs.getString("l15_lvl1_approved_date"));
        obj.setStatus(rs.getInt("l15_status"));
        return obj;
    }
}
