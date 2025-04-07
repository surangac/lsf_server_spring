package com.dfn.lsf.model;

import java.util.Date;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 4/27/2015.
 */
public class Documents {
    private String id;
    private String documentName;
    private String originalFileName;
    private String uploadedFileName;
    private String path;
    private String extension;
    private int uploadStatus;
    private boolean isRequired;
    private String mimeType;
    private String uploadedTime;
    private String uploadedBy;
    private String uploadedLevel;
    private String uploadedByUserID;
    private String createdDate;
    private String createdBy;
    private String approvedBy;
    private String approvedDate;
    private int status;
    private int isGlobal;
    private String uploadedIP;
    private Map<String, Integer> relatedApplications;
    private String fileCategory;

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Documents)
        {
            sameSame = this.id.equals(((Documents) object).getId());
        }
        return sameSame;
    }
}
