package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cash transfer request model
 */
@Getter
@Setter
@NoArgsConstructor
public class CashTransferRequest {
    
    private String messageType;
    private String subMessageType;
    private String securityKey;
    private String sourceAccount;
    private String destinationAccount;
    private double amount;
    private String applicationId;
    private String description;
}