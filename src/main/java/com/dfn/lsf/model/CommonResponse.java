package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonResponse {
    private int responseCode;
    private int errorCode;
    private String errorMessage;
    private String responseMessage;
    private Object responseObject;
    private List<String> parameterList;
}
