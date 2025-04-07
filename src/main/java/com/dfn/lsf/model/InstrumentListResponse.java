package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class InstrumentListResponse extends CommonResponse{
    private List<InstumentType> instumentTypeList;

    public List<InstumentType> getInstumentTypeList() {
        return instumentTypeList;
    }

    public void setInstumentTypeList(List<InstumentType> instumentTypeList) {
        this.instumentTypeList = instumentTypeList;
    }
}
