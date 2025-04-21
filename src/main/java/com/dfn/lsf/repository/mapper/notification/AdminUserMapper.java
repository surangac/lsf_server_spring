package com.dao.mapper.notification;

import com.dfn.lsf.gbl.bo.notification.AdminUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by isurul on 15/3/2016.
 */
public class AdminUserMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        AdminUser adminUser = new AdminUser();
        adminUser.setUserName(rs.getString("m05_user_name"));
        adminUser.setName(rs.getString("m05_name"));
        adminUser.setNin(rs.getString("m05_nin"));
        adminUser.setRole(rs.getString("m05_role"));
        adminUser.setEmail(rs.getString("m05_email"));
        adminUser.setMobile(rs.getString("m05_mobile"));

        return adminUser;
    }
}
