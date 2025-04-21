package com.dao.mapper.application;

import com.dfn.lsf.gbl.bo.application.ApplicationRating;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 16/3/2016.
 */
public class ApplicationRatingMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        ApplicationRating applicationRating = new ApplicationRating();
        applicationRating.setClientId(rs.getLong("l30_client_id"));
        applicationRating.setAppId(rs.getLong("l30_app_id"));
        applicationRating.setRating(rs.getInt("l30_rating"));
        applicationRating.setUpdatedDate(rs.getDate("l30_updated_date"));
        applicationRating.setUpdatedBy(rs.getString("l30_updated_by"));

        return applicationRating;
    }
}
