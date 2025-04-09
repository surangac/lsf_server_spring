package com.dfn.lsf.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BPSummary {
    private String marginabilityType;
    private BigDecimal buyingPower;
}
