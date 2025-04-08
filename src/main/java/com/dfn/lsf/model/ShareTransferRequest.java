package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Share transfer request model
 */
@Getter
@Setter
@NoArgsConstructor
public class ShareTransferRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String sourceAccount;
    private String destinationAccount;
    private String symbolCode;
    private int quantity;
    private String applicationId;
    private String description;
}