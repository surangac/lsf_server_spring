package com.dfn.lsf.model.notification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Atchuthan on 5/29/2015.
 */
public class Body {
    private Map<String, String> data = new HashMap<>();

    public Map<String, String> getData() {
        return data;
    }

    public void add(String key, String value) {
        data.put(key, value);
    }
}
