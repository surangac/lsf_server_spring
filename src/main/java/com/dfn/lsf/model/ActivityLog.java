package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityLog {
    private String id;
    private String categoryId;
    private String activityId;
    private String date;
    private String userId;
    private String userName;
    private String ip;
    private String description;
}
