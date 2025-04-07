package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 4/8/2015.
 */
public class MurabahApplicationListResponse extends CommonResponse {
    public List<MurabahApplication> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<MurabahApplication> applicationList) {
        this.applicationList = applicationList;
    }

    private List<MurabahApplication> applicationList;
}
