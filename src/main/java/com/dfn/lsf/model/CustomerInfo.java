package com.dfn.lsf.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * Created by surangac on 6/5/2015.
 */
public class CustomerInfo {

    private String customerId;
    private String nameInFull;
    private String occupation;
    private String employeer;
    private Double avgMonthlyIncome;
    private String email;
    private String address;
    private String mobileNo;
    private String telephoneNo;
    private String fax;
    private List<CashAcc> bankAccounts;
    private String customerReferenceNumber;
    private String zipCode;
    private String bankBranchName;
    private String city;
    private String poBox;
    private String preferedLanguage;
    private String employerAdrs;
    private String netWorth;
    private String investExprnc;
    private String riskAppetite;
    private String kycExpiryDate;
    private String homeTelephone;
}
