package com.dfn.lsf.model.notification;

import java.util.Map;

/**
 * Created by Atchuthan on 7/13/2015.
 */
public class NotificationTemplate {
    private String name;
    private Map<String, String> types;
    // in case of web messages
    private String msgSubject;
    private String msgBody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getTypes() {
        return types;
    }

    public void setTypes(Map<String, String> types) {
        this.types = types;
    }

    public String getMsgSubject() {
        return msgSubject;
    }

    public void setMsgSubject(String msgSubject) {
        this.msgSubject = msgSubject;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }
}
