package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.notification.WebNotification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Atchuthan on 8/13/2015.
 */
public class WebNotificationMapper implements RowMapper<WebNotification> {

    @Override
    public WebNotification mapRow(ResultSet rt, int i) throws SQLException {
        WebNotification obj =new WebNotification();
        obj.setMessageId(rt.getString("n04_message_id"));
        obj.setApplicationId(rt.getString("n04_l01_application_id"));
        obj.setSubject(rt.getString("n04_subject"));
        obj.setBody(rt.getString("n04_body"));
        obj.setReference(rt.getString("n04_reference"));
        obj.setDate(rt.getString("n04_date"));
        obj.setStatus(rt.getInt("n04_status"));
        return obj;
    }
}
