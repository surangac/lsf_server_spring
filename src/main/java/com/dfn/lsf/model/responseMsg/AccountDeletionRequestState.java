package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 2/8/2017.
 */
public class AccountDeletionRequestState {
    private boolean isSent;
    private String failureReason;

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
}
