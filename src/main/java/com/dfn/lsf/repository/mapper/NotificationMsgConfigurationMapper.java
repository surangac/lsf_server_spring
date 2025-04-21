package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.notification.NotificationMsgConfiguration;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Atchuthan on 8/13/2015.
 */
public class NotificationMsgConfigurationMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        NotificationMsgConfiguration obj = new NotificationMsgConfiguration();
        obj.setId(rs.getString("N03_ID"));
        obj.setCurrentLevel(rs.getString("N03_CURRENT_LEVEL"));
        obj.setOverRoleStatus(rs.getString("N03_OVEROLE_STATUS"));
        if (rs.getInt("N03_IS_SMS") == 1) {
            obj.setSms(true);
        } else {
            obj.setSms(false);
        }
        if (rs.getInt("N03_IS_MAIL") == 1) {
            obj.setMail(true);
        } else {
            obj.setMail(false);
        }
        if (rs.getInt("N03_IS_WEB") == 1) {
            obj.setWeb(true);
        } else {
            obj.setWeb(false);
        }
        obj.setWebSubject(rs.getString("N03_SUBJECT"));
        obj.setWebBody(rs.getString("N03_TEXT"));
        obj.setSmsTemplate(rs.getString("N03_SMS_TEMPLATE"));
        obj.setEmailSubject(rs.getString("N03_EMAIL_SUBJECT"));
        obj.setEmailBody(rs.getString("N03_EMAIL_BODY"));
        obj.setNotificationCode(rs.getString("n03_notification_code"));
        obj.setThirdPartyEmailTemplate(rs.getString("n03_tp_email_template"));
        obj.setThirdPartySMSTemplate(rs.getString("n03_tp_sms_template"));
        obj.setThirdPartyReference(rs.getString("n03_tp_reference"));
        return obj;
    }
}