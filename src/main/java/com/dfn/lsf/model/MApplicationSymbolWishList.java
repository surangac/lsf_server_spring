package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 5/27/2015.
 */
public class MApplicationSymbolWishList {
    private String  id;// applicationId;
    private List<Symbol> wishListSymbols;

    public List<Symbol> getWishListSymbols() {
        return wishListSymbols;
    }

    public void setWishListSymbols(List<Symbol> wishListSymbols) {
        this.wishListSymbols = wishListSymbols;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
