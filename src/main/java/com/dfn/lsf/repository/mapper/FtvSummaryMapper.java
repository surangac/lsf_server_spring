package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.responseMsg.FtvSummary;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by surangac on 5/16/2016.
 */
public class FtvSummaryMapper implements RowMapper<FtvSummary> {
    @Override
    public FtvSummary mapRow(ResultSet rs, int i) throws SQLException {
        FtvSummary ftvInfo = new FtvSummary();
        ftvInfo.setDateString(rs.getString("l28_date"));
        ftvInfo.setFtv(rs.getDouble("l28_ftv"));
        return ftvInfo;
    }
}
