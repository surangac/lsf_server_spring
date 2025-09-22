package com.dfn.lsf.model.responseMsg;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OMSQueueResponse {
    private int reqType;
    private String params;
    private boolean approved;
    private int rejectCode;
    private String messageParam;
    private String code;
}
