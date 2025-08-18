package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class MurabahApplicationListResponse extends CommonResponse {
    public List<MurabahApplication> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<MurabahApplication> applicationList) {
        this.applicationList = applicationList;
    }

    private List<MurabahApplication> applicationList;
    private String customerContractComment;
    private String authAbicToSell;
}
