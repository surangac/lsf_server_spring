package com.dfn.lsf.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

/**
 * Created by manodyas on 4/3/2016.
 */
public class UserAccountDetails {
    private String lsfTypeCashAccount;
    private String lsfTypeTradingAccount;
    private String marginCashAccount;
    private String marginTradingAccount;

    public String getLsfTypeCashAccount() {
        return lsfTypeCashAccount;
    }

    public void setLsfTypeCashAccount(String lsfTypeCashAccount) {
        this.lsfTypeCashAccount = lsfTypeCashAccount;
    }

    public String getLsfTypeTradingAccount() {
        return lsfTypeTradingAccount;
    }

    public void setLsfTypeTradingAccount(String lsfTypeTradingAccount) {
        this.lsfTypeTradingAccount = lsfTypeTradingAccount;
    }

    public String getMarginCashAccount() {
        return marginCashAccount;
    }

    public void setMarginCashAccount(String marginCashAccount) {
        this.marginCashAccount = marginCashAccount;
    }

    public String getMarginTradingAccount() {
        return marginTradingAccount;
    }

    public void setMarginTradingAccount(String marginTradingAccount) {
        this.marginTradingAccount = marginTradingAccount;
    }
}
