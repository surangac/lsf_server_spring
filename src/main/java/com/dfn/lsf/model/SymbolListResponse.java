package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 5/13/2015.
 */
public class SymbolListResponse extends CommonResponse  {
    private List<Symbol> symbolsList;
    private StockConcentrationGroup concentrationGroup;
    private MarginabilityGroup marginabilityGroup;

    public List<Symbol> getSymbolsList() {
        return symbolsList;
    }

    public void setSymbolsList(List<Symbol> symbolsList) {
        this.symbolsList = symbolsList;
    }

    public StockConcentrationGroup getConcentrationGroup() {
        return concentrationGroup;
    }

    public void setConcentrationGroup(StockConcentrationGroup concentrationGroup) {
        this.concentrationGroup = concentrationGroup;
    }

    public MarginabilityGroup getMarginabilityGroup() {
        return marginabilityGroup;
    }

    public void setMarginabilityGroup(MarginabilityGroup marginabilityGroup) {
        this.marginabilityGroup = marginabilityGroup;
    }
}
