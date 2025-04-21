package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.notification.Header;
import com.dfn.lsf.model.notification.Notification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Atchuthan on 8/24/2015.
 */
public class NotificationHeaderMapper implements RowMapper<Notification> {
    @Override
    public Notification mapRow(ResultSet rs, int i) throws SQLException {
        Notification obj = new Notification();
        obj.setUid(rs.getString("N01_UID"));
        obj.setStatus("N01_STATUS");
        Header header = obj.getHeader();
        header.setTime(rs.getString("N01_TIME"));
        header.setUserId(rs.getString("N01_USER_ID"));
        header.setSource(rs.getString("N01_SOURCE"));
        header.setNotificationType(rs.getString("N01_NOTIFICATION_TYPE"));
        header.setMessageType(rs.getString("N01_MESSAGE_TYPE"));
        header.setLanguage(rs.getString("N01_LANGUAGE"));
        header.setAttachment(rs.getBoolean("N01_IS_ATTACHMENT_AVAILABLE"));
        List<String> mobileNumbers = new ArrayList<String>();
        mobileNumbers.add(rs.getString("N01_MOBILE_NUMBERS"));
        header.setMobileNumbers(mobileNumbers);
        header.setFromAddress(rs.getString("N01_FROM_ADDRESS"));
        header.setToAddresses(new ArrayList<String>());
        header.setCcAddresses(new ArrayList<String>());
        header.setBccAddresses(new ArrayList<String>());
        return obj;
    }
}
