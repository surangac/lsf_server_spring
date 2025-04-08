package com.dfn.lsf.service.security;

/**
 * Created by manodyas on 7/7/2015.
 */
public class SessionValidationInternalResponse {
    private boolean isValidate;
    private String rejectReason;

    public boolean isValidate() {
        return isValidate;
    }

    public void setIsValidate(boolean isValidate) {
        this.isValidate = isValidate;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
