package com.dfn.lsf.repository.mapper.application;

import com.dfn.lsf.model.ProfitCalculationMasterEntry;
import com.dfn.lsf.util.LSFUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfitMasterEntryMapper implements RowMapper<ProfitCalculationMasterEntry> {
    @Override
    public ProfitCalculationMasterEntry mapRow(ResultSet rs, int i) throws SQLException {
        ProfitCalculationMasterEntry entry = new ProfitCalculationMasterEntry();
        if(rs.getString("m08_job_date") != null){
            entry.setJobDate(rs.getDate("m08_job_date"));
            entry.setJobDateStr(LSFUtils.formatDateToString(entry.getJobDate()));
        }
        entry.setEligibleAppCount(rs.getInt("m08_eligible_app_count"));
        entry.setCompletedAppCount(rs.getInt("m08_completed_app_count"));
        if(rs.getString("m08_start_time") != null){
            entry.setJobStartTime(rs.getDate("m08_start_time"));
        }
        if(rs.getString("m08_end_time") != null){
            entry.setJobEndTime(rs.getDate("m08_end_time"));
        }
        return entry;

    }
}
