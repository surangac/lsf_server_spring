package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 4/27/2015.
 */
public class Status {
    private int levelId;
    private int statusId;
    private String statusDescription;
    private String statusMessage;
    private String statusChangedUserid;
    private String statusChangedUserName;
    private String statusChangedDate;
    private int count;
    private String notificationType;
    private String appId;
    private String statusChangedIPAddress;

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusChangedUserid() {
        return statusChangedUserid;
    }

    public void setStatusChangedUserid(String statusChangedUserid) {
        this.statusChangedUserid = statusChangedUserid;
    }

    public String getStatusChangedDate() {
        return statusChangedDate;
    }

    public void setStatusChangedDate(String statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getStatusChangedUserName() {
        return statusChangedUserName;
    }

    public void setStatusChangedUserName(String statusChangedUserName) {
        this.statusChangedUserName = statusChangedUserName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getStatusChangedIPAddress() {
        return statusChangedIPAddress;
    }

    public void setStatusChangedIPAddress(String statusChangedIPAddress) {
        this.statusChangedIPAddress = statusChangedIPAddress;
    }
}
