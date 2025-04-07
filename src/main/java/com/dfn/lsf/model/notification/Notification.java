package com.dfn.lsf.model.notification;

/**
 * Created by Atchuthan on 5/29/2015.
 */
public class Notification {
    private String uid;
    private String status;
    private Header header = new Header();
    private Body body = new Body();

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        this.header.setUid(uid);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Header getHeader() {
        return header;
    }

    public Body getBody() {
        return body;
    }
}

