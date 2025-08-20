package com.dfn.lsf.model.responseMsg;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccountDeletionRequestState {
    private boolean isSent;
    private String failureReason;
    private int errorCode;

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
