package com.dfn.lsf.model;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 6/30/2015.
 */
public class UserSession {
    private String userId;
    private String serssionId;
    private int channelId;
    private Date lastActiveTime;
    private int sessionStatus;
    private int status;
    private String omsSessionID;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSerssionId() {
        return serssionId;
    }

    public void setSerssionId(String serssionId) {
        this.serssionId = serssionId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public Date getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Date lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public int getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOmsSessionID() {
        return omsSessionID;
    }

    public void setOmsSessionID(String omsSessionID) {
        this.omsSessionID = omsSessionID;
    }
}
