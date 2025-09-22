package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/13/2015.
 */
public class ApplicationStatusMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet rs, int i) throws SQLException {
        Status obj = new Status();
        obj.setLevelId(rs.getInt("l02_level_id"));
        obj.setStatusId(rs.getInt("l02_status_id"));
        obj.setStatusDescription(rs.getString("l02_description"));
        obj.setStatusMessage(rs.getString("l02_message"));
        obj.setStatusChangedUserid(rs.getString("l02_sts_changed_user_id"));
        obj.setStatusChangedUserName(rs.getString("l02_sts_changed_user_name"));
        obj.setStatusChangedDate(rs.getString("l02_sts_changed_date"));
        obj.setStatusChangedIPAddress(rs.getString("l02_status_changed_ip"));
        try {
            obj.setAppId(rs.getString("app_id"));
        } catch (Exception ex) {
            obj.setAppId("0");
        }
        return obj;
    }
}
