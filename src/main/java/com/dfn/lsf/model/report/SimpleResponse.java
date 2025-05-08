package com.dfn.lsf.model.report;

/**
 * Created by isurul on 11/2/2016.
 */
public class SimpleResponse implements ReportResponse {

    private String data;

    public String getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = (String) data;
    }

    @Override
    public void setData(Object data, Object summary) {
        this.data = (String) data;
    }
}
