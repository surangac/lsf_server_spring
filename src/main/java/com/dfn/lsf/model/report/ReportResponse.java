package com.dfn.lsf.model.report;

/**
 * Created by isurul on 11/2/2016.
 */

public interface ReportResponse {
    void setData(Object dataList);
    void setData(Object dataList, Object summary);
    Object returnResponse();
}
