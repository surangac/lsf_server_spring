package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 4/6/2015.
 */
public class ValidataionMessage extends MessageHeader {
    private String customerId;
    private String validationType;
    private String valueToBeValidate;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public String getValueToBeValidate() {
        return valueToBeValidate;
    }

    public void setValueToBeValidate(String valueToBeValidate) {
        this.valueToBeValidate = valueToBeValidate;
    }
}
