package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by surangac on 4/2/2015.
 */
public class Symbol {
    private String symbolCode;
    private String shortDescription;
    private String exchange;
    private double previousClosed;
    private int availableQty;
    private int colleteralQty;
    private double contibutionTocollateral;
    private double marketValue;
    private LiquidityType liquidityType;
    private int transferedQty;
    private double percentage;
    private String blockedReference;
    private int transStatus;  //0-blocked,1-block release,2-transfered
    private int allowedForCollateral;
    private String liquidName;
    private double lastTradePrice;
    private String arabicName;
    private String englishName;
    private String shortDescriptionAR;
    private String pendingSettle;
    private LiquidityType concentrationType;
    private int openBuyQty;
    private int openSellQty;
    private String broker;
    private String unitOfMeasure;
    private double marginabilityPercentage;
    private int instrumentType = -1;
    private String securityType;

    public int getOpenBuyQty() {
        return openBuyQty;
    }

    public void setOpenBuyQty(int openBuyQty) {
        this.openBuyQty = openBuyQty;
    }

    public int getOpenSellQty() {
        return openSellQty;
    }

    public void setOpenSellQty(int openSellQty) {
        this.openSellQty = openSellQty;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    public LiquidityType getLiquidityType() {
        return liquidityType;
    }

    public void setLiquidityType(LiquidityType liquidityType) {
        this.liquidityType = liquidityType;
    }

    public double getContibutionTocollateral() {
        return contibutionTocollateral;
    }

    public void setContibutionTocollateral(double contibutionTocollateral) {
        this.contibutionTocollateral = contibutionTocollateral;
    }

    public String getSymbolCode() {
        return symbolCode;
    }

    public void setSymbolCode(String symbolCode) {
        this.symbolCode = symbolCode;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public double getPreviousClosed() {
        return previousClosed;
    }

    public void setPreviousClosed(double previousClosed) {
        this.previousClosed = previousClosed;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public int getColleteralQty() {
        return colleteralQty;
    }

    public void setColleteralQty(int colleteralQty) {
        this.colleteralQty = colleteralQty;
    }

    public int getTransferedQty() {
        return transferedQty;
    }

    public void setTransferedQty(int transferedQty) {
        this.transferedQty = transferedQty;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof Symbol)
        {
            sameSame = (this.getExchange().equals(((Symbol)object).getExchange()) && this.getSymbolCode().equals(((Symbol) object).getSymbolCode()));
        }
        return sameSame;
    }

    public String getBlockedReference() {
        return blockedReference;
    }

    public void setBlockedReference(String blockedReference) {
        this.blockedReference = blockedReference;
    }

    public int getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    public int getAllowedForCollateral() {
        return allowedForCollateral;
    }

    public void setAllowedForCollateral(int allowedForCollateral) {
        this.allowedForCollateral = allowedForCollateral;
    }

    public String getLiquidName() {
        return liquidName;
    }

    public void setLiquidName(String liquidName) {
        this.liquidName = liquidName;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getShortDescriptionAR() {
        return shortDescriptionAR;
    }

    public void setShortDescriptionAR(String shortDescriptionAR) {
        this.shortDescriptionAR = shortDescriptionAR;
    }

    public String getPendingSettle() {
        return pendingSettle;
    }

    public void setPendingSettle(String pendingSettle) {
        this.pendingSettle = pendingSettle;
    }

    public LiquidityType getConcentrationType() {
        return concentrationType;
    }

    public void setConcentrationType(LiquidityType concentrationType) {
        this.concentrationType = concentrationType;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public double getMarginabilityPercentage() {

        return marginabilityPercentage;
    }

    public void setMarginabilityPercentage(final double marginabilityPercentage) {

        this.marginabilityPercentage = marginabilityPercentage;
    }

    public int getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(int instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getSecurityType() {
        return securityType;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public void mapFromOms(Symbol sourceSymbol) {
        this.setShortDescription(sourceSymbol.getShortDescription());
        this.setOpenBuyQty(sourceSymbol.getOpenBuyQty());
        this.setPreviousClosed(sourceSymbol.getPreviousClosed());
        this.setLastTradePrice(sourceSymbol.getLastTradePrice());
        this.setAvailableQty(sourceSymbol.getAvailableQty());
        this.setMarketValue(this.getAvailableQty() * (this.getLastTradePrice() > 0
                                                                              ? this.getLastTradePrice()
                                                                              : this.getPreviousClosed()));
        this.setLiquidityType(sourceSymbol.getLiquidityType());
        this.setTransferedQty(sourceSymbol.getTransferedQty());
        this.setLiquidityType(sourceSymbol.getLiquidityType());
        this.setMarginabilityPercentage(sourceSymbol.getMarginabilityPercentage());
    }
}
