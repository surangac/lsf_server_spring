package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/20/2015.
 */
public class LiquidityType {
    private int liquidId;
    private String liquidName;
    private Double marginabilityPercent;
    private Double stockConcentrationPercent;

    public Double getMarginabilityPercent() {
        return marginabilityPercent;
    }

    public void setMarginabilityPercent(Double marginabilityPercent) {
        this.marginabilityPercent = marginabilityPercent;
    }

    public Double getStockConcentrationPercent() {
        return stockConcentrationPercent;
    }

    public void setStockConcentrationPercent(Double stockConcentrationPercent) {
        this.stockConcentrationPercent = stockConcentrationPercent;
    }

    public int getLiquidId() {
        return liquidId;
    }

    public void setLiquidId(int liquidId) {
        this.liquidId = liquidId;
    }

    public String getLiquidName() {
        return liquidName;
    }

    public void setLiquidName(String liquidName) {
        this.liquidName = liquidName;
    }

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
