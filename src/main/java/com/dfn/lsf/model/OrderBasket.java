package com.dfn.lsf.model;

import java.util.List;

import lombok.Data;

@Data
public class OrderBasket {
    private String id;
    private String reference;
    private List<BasketItem> items;
    private String status;
    private String type;
}
