package com.dfn.lsf.model;

import lombok.Data;

@Data
public class BasketItem {
    private String id;
    private String symbolCode;
    private int quantity;
    private double price;
    private String status;
    private double executedPrice;
    private int executedQuantity;
} 