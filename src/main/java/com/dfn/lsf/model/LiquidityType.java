package com.dfn.lsf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LiquidityType {
    private int liquidId;
    private String liquidName;
    private Double marginabilityPercent;
    private Double stockConcentrationPercent;
    
    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof LiquidityType)
        {
            sameSame = this.getLiquidId()==((LiquidityType) object).getLiquidId();
        }
        return sameSame;
    }
}
