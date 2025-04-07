package com.dfn.lsf.model.notification;

import java.util.List;

/**
 * Created by Atchuthan on 7/13/2015.
 */
public class NotificationTrigger {
    private String id;
    private List<NotificationTemplate> notificationTemplates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<NotificationTemplate> getNotificationTemplates() {
        return notificationTemplates;
    }

    public void setNotificationTemplates(List<NotificationTemplate> notificationTemplates) {
        this.notificationTemplates = notificationTemplates;
    }
}
