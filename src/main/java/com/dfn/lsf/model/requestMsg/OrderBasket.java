package com.dfn.lsf.model.requestMsg;

import java.util.List;

import com.dfn.lsf.model.Symbol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderBasket {
    private String customerId;
    private double loanAmount;
    private List<Symbol> symbolList;
    private String expiryDate;
    private String basketReference;
    private String tradingAccountId;
}
