package com.dfn.lsf.model.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

/**
 * Created by isurul on 11/2/2016.
 */
public class GridResponse implements ReportResponse {

    private long recordCount;
    private long offset;
    private long limit;
    private List<?> fields;
    private Map<String, Object> parameters;

    public GridResponse() {}

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public List<?> getData() {
        return fields;
    }

    @Override
    public void setData(Object data) {
        this.fields = (List) data;
    }

    @Override
    public void setData(Object fields, Object parameters) {
        this.fields = fields == null ? new ArrayList<>() : (List) fields;
        this.parameters = parameters == null ? new HashMap<>() : (Map) parameters;
    }

    @Override
    public String returnResponse() {
        return (new Gson()).toJson(this);
    }
}
