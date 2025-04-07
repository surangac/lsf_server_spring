package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by manodyas on 8/24/2015.
 */
public class CustomerInfoResponse extends CommonResponse {
    private String fullName;
    private String fullAddress;
}
