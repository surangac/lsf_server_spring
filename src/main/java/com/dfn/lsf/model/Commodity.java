package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Commodity {
    private String symbolCode;
    private String symbolName;
    private String shortDescription;
    private String exchange;
    private String broker;
    private double price;
    private String unitOfMeasure;
    private int status;
    private double percentage;
    private int soldAmnt;
    private String arabicName;
    private String englishName;
    private double boughtAmnt;
}
