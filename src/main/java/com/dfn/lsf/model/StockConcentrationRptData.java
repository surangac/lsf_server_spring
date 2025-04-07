package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StockConcentrationRptData {
    private double totalBuyingPower;
    private double totalReceibableCash;
    private double totalPayableCash;
    private double cashBalance;
    private double totalAsset;
    private double buyingPwerPercentage;
    private double totalAssetPercentage;
    private List<Symbol> concentrationSymbolList;

    public double getTotalBuyingPower() {
        return totalBuyingPower;
    }

    public void setTotalBuyingPower(double totalBuyingPower) {
        this.totalBuyingPower = totalBuyingPower;
    }

    public double getTotalReceibableCash() {
        return totalReceibableCash;
    }

    public void setTotalReceibableCash(double totalReceibableCash) {
        this.totalReceibableCash = totalReceibableCash;
    }

    public double getTotalPayableCash() {
        return totalPayableCash;
    }

    public void setTotalPayableCash(double totalPayableCash) {
        this.totalPayableCash = totalPayableCash;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(double totalAsset) {
        this.totalAsset = totalAsset;
    }

    public double getBuyingPwerPercentage() {
        return buyingPwerPercentage;
    }

    public void setBuyingPwerPercentage(double buyingPwerPercentage) {
        this.buyingPwerPercentage = buyingPwerPercentage;
    }

    public double getTotalAssetPercentage() {
        return totalAssetPercentage;
    }

    public void setTotalAssetPercentage(double totalAssetPercentage) {
        this.totalAssetPercentage = totalAssetPercentage;
    }

    public List<Symbol> getConcentrationSymbolList() {
        return concentrationSymbolList;
    }

    public void setConcentrationSymbolList(List<Symbol> concentrationSymbolList) {
        this.concentrationSymbolList = concentrationSymbolList;
    }
}
