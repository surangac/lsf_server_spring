package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class StatusMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet rs, int i) throws SQLException {
        Status obj = new Status();

        obj.setLevelId(rs.getInt("L02_LEVEL_ID"));
        obj.setAppId(rs.getString("L02_L01_APP_ID"));
        obj.setStatusId(rs.getInt("L02_STATUS_ID"));
        obj.setStatusDescription(rs.getString("L02_DESCRIPTION"));
        obj.setStatusMessage(rs.getString("L02_MESSAGE"));
        obj.setStatusChangedUserid(rs.getString("L02_STS_CHANGED_USER_ID"));
        obj.setStatusChangedUserName(rs.getString("L02_STS_CHANGED_USER_NAME"));
        obj.setStatusChangedDate(rs.getString("L02_STS_CHANGED_DATE"));
        obj.setCount(rs.getInt("L02_COUNT"));
        obj.setNotificationType(rs.getString("L02_NOTIFY_TYPE"));

        return obj;
    }
}
