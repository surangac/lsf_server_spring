package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/26/2015.
 */
public class MApplicationDocuments {
    private String applicationId;
    private List<Documents> applicationDocuments;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public List<Documents> getApplicationDocuments() {
        return applicationDocuments;
    }

    public void setApplicationDocuments(List<Documents> applicationDocuments) {
        this.applicationDocuments = applicationDocuments;
    }
}
