package com.dfn.lsf.repository.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Atchuthan on 8/24/2015.
 */
public class NotificationBodyMapper implements RowMapper<Map<String, String>> {
    @Override
    public Map<String, String> mapRow(ResultSet rs, int i) throws SQLException {
        Map<String, String> obj = new HashMap<>();
        obj.put(rs.getString("KEY"),rs.getString("VALUE"));
        return obj;
    }
}
