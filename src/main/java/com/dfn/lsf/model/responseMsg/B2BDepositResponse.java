package com.dfn.lsf.model.responseMsg;

/**
 * Created by manodyas on 1/26/2016.
 */
public class B2BDepositResponse {
    private int messageType;
    private String transactionId;
    private int status;
    private String narration;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
