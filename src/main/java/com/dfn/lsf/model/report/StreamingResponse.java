package com.dfn.lsf.model.report;

import java.io.OutputStream;

/**
 * Created by isurul on 11/2/2016.
 */
public class StreamingResponse implements ReportResponse {

    private OutputStream data;

    public OutputStream getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = (OutputStream) data;
    }

    @Override
    public void setData(Object data, Object summary) {
        this.data = (OutputStream) data;
    }

    @Override
    public OutputStream returnResponse() {
        return data;
    }
}
