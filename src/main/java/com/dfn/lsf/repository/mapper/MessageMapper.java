package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.notification.Message;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Atchuthan on 8/26/2015.
 */
public class MessageMapper implements RowMapper<Message> {

    @Override
    public Message mapRow(ResultSet rs, int i) throws SQLException {
        Message obj = new Message();
        obj.setUid(rs.getString("N04_UID"));
        obj.setTime(rs.getString("N04_TIME"));
        obj.setUserId(rs.getString("N04_USER_ID"));
        obj.setNotificationType(rs.getString("N04_NOTIFICATION_TYPE"));
        obj.setLanguage(rs.getString("N04_LANGUAGE"));
        obj.setAttachment(rs.getBoolean("N04_ATTACHMENT"));
        List<String> mobileNumbers = new ArrayList<String>();
        mobileNumbers.add(rs.getString("N04_MOBILE_NO"));
        obj.setMobileNumbers(mobileNumbers);
        obj.setFromAddress(rs.getString("N04_FROM_ADDRESS"));
        obj.setToAddresses(new ArrayList<String>());
        obj.setCcAddresses(new ArrayList<String>());
        obj.setBccAddresses(new ArrayList<String>());
        obj.setSubject(rs.getString("N04_SUBJECT"));
        obj.setMessage(rs.getString("N04_MESSAGE"));
        obj.setStatus(rs.getInt("N04_STATUS"));
        if(rs.getInt("N04_IS_CUSTOM")==1){
            obj.setCustom(true);
        }else {
            obj.setCustom(false);
        }
        obj.setSentBy(rs.getString("N04_SENT_BY"));

        return obj;
    }
}
