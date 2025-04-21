package com.dao.mapper;

import com.dfn.lsf.gbl.bo.UserSession;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by manodyas on 8/12/2015.
 */
public class UserSessionMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        UserSession obj = new UserSession();
        obj.setSerssionId(rs.getString("u01_session_id"));
        obj.setUserId(rs.getString("u01_user_id"));
        obj.setChannelId(rs.getInt("u01_chanel_id"));
        obj.setLastActiveTime(rs.getDate("u01_last_active_date_time"));
        obj.setSessionStatus(rs.getInt("u01_session_status"));
        obj.setStatus(rs.getInt("u01_status"));
        return obj;
    }
}
