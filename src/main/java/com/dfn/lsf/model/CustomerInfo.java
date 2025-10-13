package com.dfn.lsf.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CUSTOMER_INFO_SYNC")
@Getter
@Setter
public class CustomerInfo {

    @Id
    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "NAME_IN_FULL")
    private String nameInFull;

    @Column(name = "OCCUPATION")
    private String occupation;

    @Column(name = "EMPLOYEER")
    private String employeer;

    @Column(name = "AVG_MONTHLY_INCOME")
    private Double avgMonthlyIncome;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "MOBILE_NO")
    private String mobileNo;

    @Column(name = "TELEPHONE_NO")
    private String telephoneNo;

    @Column(name = "FAX")
    private String fax;

    @Transient
    private List<CashAcc> bankAccounts;

    @Column(name = "CUSTOMER_REFERENCE_NUMBER")
    private String customerReferenceNumber;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "BANK_BRANCH_NAME")
    private String bankBranchName;

    @Column(name = "CITY")
    private String city;

    @Column(name = "PO_BOX")
    private String poBox;

    @Column(name = "PREFERED_LANGUAGE")
    private String preferedLanguage;

    @Column(name = "EMPLOYER_ADRS")
    private String employerAdrs;

    @Column(name = "NET_WORTH")
    private String netWorth;

    @Column(name = "INVEST_EXPRNC")
    private String investExprnc;

    @Column(name = "RISK_APPETITE")
    private String riskAppetite;

    @Column(name = "KYC_EXPIRY_DATE")
    private String kycExpiryDate;

    @Column(name = "HOME_TELEPHONE")
    private String homeTelephone;

    @Column(name = "NIN")
    private String nin;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
