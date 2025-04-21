package com.dfn.lsf.repository.mapper;


import com.dfn.lsf.model.ProfitCalMurabahaApplication;
import com.dfn.lsf.util.LSFUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfitCalMurabahaApplicationMapper implements RowMapper<ProfitCalMurabahaApplication> {
    @Override
    public ProfitCalMurabahaApplication mapRow(ResultSet rs, int i) throws SQLException {
        ProfitCalMurabahaApplication application = new ProfitCalMurabahaApplication();
        application.setId(rs.getString("L01_APP_ID"));
        application.setCustomerId(rs.getString("L01_CUSTOMER_ID"));
        if(rs.getString("l01_last_profit_date") != null){
            application.setLastProfitCycleDate(rs.getDate("l01_last_profit_date"));
            application.setLastProfitCycleDateStr(LSFUtils.formatDateToString(application.getLastProfitCycleDate()));
        }
        return application;
    }
}
