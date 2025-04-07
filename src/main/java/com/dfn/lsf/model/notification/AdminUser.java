package com.dfn.lsf.model.notification;

import java.util.HashMap;

/**
 * Created by isurul on 15/3/2016.
 */
public class AdminUser {
    private String userName;
    private String name;
    private String nin;
    private String role;
    private String email;
    private String mobile;

    public HashMap<String, String> getAttributeMap() {
        HashMap<String, String> attributeMap = new HashMap<>();
        attributeMap.put("pm05_user_name", userName);
        attributeMap.put("pm05_name", name);
        attributeMap.put("pm05_nin", nin);
        attributeMap.put("pm05_role", role);
        attributeMap.put("pm05_email", email);
        attributeMap.put("pm05_mobile", mobile);
        return attributeMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
