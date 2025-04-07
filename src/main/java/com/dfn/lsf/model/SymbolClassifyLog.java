package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolClassifyLog {
    private String symbolCode;
    private String previousLiquidType;
    private String updatedLiquidType;
}
