package com.dfn.lsf.model.responseMsg;

import java.util.List;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.model.Symbol;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDetailedInfo {
    private String customerID;
    private String applicationID;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String tradingAccount;
    private String cashAccount;
    private String proposalDate;
    private double initialRAPV;
    private double financeRequiredAmount;
    private double proposedLimit;
    private String lsfTradingAccount;
    private String lsfCashAccount;
    private List<Symbol> pfCollateralList;
    private List<CashAcc> cashCollateralList;
    private double initialPFCollaterals;
    private double cashCollateral;
    private double totalCollateralValue;
    private List<PurchaseOrder> purchaseOrders;
    private boolean isSettled;
    private String settlementDate;
    private String settledDate;
    private String pendingStatus;
    private List<Status> statusList;
    List<FtvSummary> dailyFtvList;
}
