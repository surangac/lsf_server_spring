package com.dfn.lsf.model;

import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MurabahApplicationListResponse extends CommonResponse {
    public List<MurabahApplication> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<MurabahApplication> applicationList) {
        this.applicationList = applicationList;
    }

    private List<MurabahApplication> applicationList;
}
