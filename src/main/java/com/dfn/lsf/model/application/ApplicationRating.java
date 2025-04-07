package com.dfn.lsf.model.application;

import java.util.Date;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRating {
    private long clientId;
    private long appId;
    private int rating;
    private Date updatedDate;
    private String updatedBy;

    public HashMap<String, Object> getAttributeMap() {
        HashMap<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("pl30_client_id", clientId);
        attributeMap.put("pl30_app_id", appId);
        attributeMap.put("pl30_rating", rating);
        attributeMap.put("pl30_updated_date", updatedDate);
        attributeMap.put("pl30_updated_by", updatedBy);
        return attributeMap;
    }

    public HashMap<String, Object> getAttributeMapForSearch() {
        HashMap<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("pl30_client_id", clientId);
        attributeMap.put("pl30_app_id", appId);
        return attributeMap;
    }
}
