package com.dfn.lsf.model.report;


/**
 * Created by isurul on 18/3/2016.
 */
public class GeneratedResponse implements ReportResponse {
    private String user;
    private String filePath;

    public GeneratedResponse(String user, String filePath) {
        this.user = user;
        this.filePath = filePath;
    }

    @Override
    public void setData(Object dataList) {

    }

    @Override
    public void setData(Object dataList, Object summary) {

    }
}
