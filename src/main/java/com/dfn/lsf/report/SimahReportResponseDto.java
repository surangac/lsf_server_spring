package com.dfn.lsf.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Deprecated
public class SimahReportResponseDto {

    // Credit Instrument Basic Information
    @JsonProperty("AREF")
    private String creditInstrumentNumber;

    @JsonProperty("AOPN")
    private String issueDate;

    @JsonProperty("APRD")
    private String productType;

    @JsonProperty("ALMT")
    private BigDecimal productLimit;

    @JsonProperty("ASAL")
    private String salaryAssignmentFlag;

    @JsonProperty("AEXP")
    private String productExpiryDate;

    @JsonProperty("APST")
    private String productStatus;

    @JsonProperty("AINST")
    private BigDecimal instalmentAmount;

    @JsonProperty("AVINST")
    private BigDecimal averageInstalmentAmount;

    @JsonProperty("AFRQ")
    private String paymentFrequency;

    @JsonProperty("ATNR")
    private Integer tenure;

    @JsonProperty("ASEC")
    private String securityType;

    @JsonProperty("ADWNP")
    private BigDecimal downPayment;

    @JsonProperty("ABPAY")
    private BigDecimal balloonPayment;

    @JsonProperty("ADAMT")
    private BigDecimal dispensedAmount;

    @JsonProperty("AMAX")
    private BigDecimal maxInstalmentAmount;

    @JsonProperty("ASP")
    private String subProductType;

    @JsonProperty("ATLEAMT")
    private BigDecimal totalLeasingAmount;

    @JsonProperty("ACLSRESON")
    private String reasonForClosureCode;

    @JsonProperty("AFACTORING")
    private String factoringFlag;

    // Installment Information (5 installments)
    @JsonProperty("STRTINSDT1")
    private String installmentStartDate1;

    @JsonProperty("INSAMT1")
    private BigDecimal installmentAmount1;

    @JsonProperty("STRTINSDT2")
    private String installmentStartDate2;

    @JsonProperty("INSAMT2")
    private BigDecimal installmentAmount2;

    @JsonProperty("STRTINSDT3")
    private String installmentStartDate3;

    @JsonProperty("INSAMT3")
    private BigDecimal installmentAmount3;

    @JsonProperty("STRTINSDT4")
    private String installmentStartDate4;

    @JsonProperty("INSAMT4")
    private BigDecimal installmentAmount4;

    @JsonProperty("STRTINSDT5")
    private String installmentStartDate5;

    @JsonProperty("INSAMT5")
    private BigDecimal installmentAmount5;

    // Contract Information
    @JsonProperty("CONTRACTNO")
    private String contractNumber;

    @JsonProperty("FRSTINSDT")
    private String firstInstallmentDate;

    @JsonProperty("CSTRATE")
    private BigDecimal costRate;

    @JsonProperty("AMTRATE")
    private BigDecimal amountRate;

    @JsonProperty("FIXRATE")
    private BigDecimal fixedRate;

    @JsonProperty("ACON")
    private Integer numberOfCreditInstrumentHolders;

    @JsonProperty("ACYCID")
    private String cycleId;

    // Payment Information
    @JsonProperty("ALSPD")
    private String lastPaymentDate;

    @JsonProperty("ALSTAM")
    private BigDecimal lastAmountPaid;

    @JsonProperty("AACS")
    private String paymentStatus;

    @JsonProperty("ACUB")
    private BigDecimal outstandingBalance;

    @JsonProperty("AODB")
    private BigDecimal pastDueBalance;

    @JsonProperty("AASOF")
    private String asOfDate;

    @JsonProperty("ANXPD")
    private String nextPaymentDate;

    @JsonProperty("PMETHPAY")
    private String preferredMethodOfPayment;

    @JsonProperty("PNOPI")
    private Integer numberOfPaidInstallments;

    @JsonProperty("PECCC")
    private String earlyPayoff;

    @JsonProperty("PNOUI")
    private Integer numberOfUnpaidInstallments;

    @JsonProperty("PAPTP")
    private BigDecimal amountPaidToThirdParty;

    @JsonProperty("PDRW")
    private String rightOfWithdrawalCode;

    @JsonProperty("PAPR")
    private BigDecimal aprPercentage;

    @JsonProperty("PDTP")
    private String terminationProcedureCode;

    @JsonProperty("PDOC")
    private String ownershipChangeCode;

    @JsonProperty("PDOWN")
    private BigDecimal amountOfOwnershipAsPerLaw;

    @JsonProperty("PDAF")
    private BigDecimal adminNotarialFees;

    @JsonProperty("PAYTYPE")
    private String paymentType;

    @JsonProperty("AMTPNDFINS")
    private BigDecimal amountOfPendingFutureInstallments;

    @JsonProperty("AMTUNPINS")
    private BigDecimal amountOfUnpaidInstallments;

    // Consumer ID Information
    @JsonProperty("CID1")
    private String idType;

    @JsonProperty("CID2")
    private String consumerId;

    @JsonProperty("CID3")
    private String idExpirationDate;

    @JsonProperty("CID4")
    private String nationalIdIqamaIssuingPlace;

    @JsonProperty("CMAR")
    private String maritalStatus;

    @JsonProperty("CNAT")
    private String nationalityCode;

    // Arabic Name Information
    @JsonProperty("CNMFA")
    private String familyNameArabic;

    @JsonProperty("CNM1A")
    private String firstNameArabic;

    @JsonProperty("CNM2A")
    private String cnm2Arabic;

    @JsonProperty("CNM3A")
    private String cnm3Arabic;

    @JsonProperty("CNMUA")
    private String fullNameArabic;

    // English Name Information
    @JsonProperty("CNMFE")
    private String familyNameEnglish;

    @JsonProperty("CNM1E")
    private String firstNameEnglish;

    @JsonProperty("CNM2E")
    private String cnm2English;

    @JsonProperty("CNM3E")
    private String cnm3English;

    @JsonProperty("CNMUE")
    private String fullNameEnglish;

    // Personal Information
    @JsonProperty("CDOB")
    private String dateOfBirth;

    @JsonProperty("CGND")
    private String gender;

    @JsonProperty("CINOI")
    private Integer numberOfDependents;

    @JsonProperty("CAPL")
    private String applicantType;

    @JsonProperty("CPER")
    private BigDecimal percentageAllocation;

    @JsonProperty("COUTBAL")
    private BigDecimal applicantOutstandingBalance;

    @JsonProperty("CAPPLIMIT")
    private BigDecimal applicantLimit;

    @JsonProperty("CLAP")
    private BigDecimal applicantLastAmountPaid;

    @JsonProperty("CINSTAMT")
    private BigDecimal applicantInstalmentAmount;

    @JsonProperty("CPLD")
    private String applicantLastPaymentDate;

    @JsonProperty("CNDDATE")
    private String applicantNextDueDate;

    @JsonProperty("CPDB")
    private BigDecimal applicantPastDueBalance;

    @JsonProperty("CPAYSTS")
    private String applicantPaymentStatus;

    // Current Address Information
    @JsonProperty("CADBNUM")
    private String currentBuildingNumber;

    @JsonProperty("CADBSTR")
    private String currentStreetEnglish;

    @JsonProperty("CADBSTRU")
    private String currentStreetArabic;

    @JsonProperty("CADDIS")
    private String currentDistrictEnglish;

    @JsonProperty("CADDISU")
    private String currentDistrictArabic;

    @JsonProperty("CADADNU")
    private String currentAdditionalNumber;

    @JsonProperty("CADUNTNUM")
    private String currentUnitNumber;

    // Employment Address Information
    @JsonProperty("EADBNUM")
    private String employmentBuildingNumber;

    @JsonProperty("EADBSTR")
    private String employmentStreetEnglish;

    @JsonProperty("EADBSTRU")
    private String employmentStreetArabic;

    @JsonProperty("EADDIS")
    private String employmentDistrictEnglish;

    @JsonProperty("EADDISU")
    private String employmentDistrictArabic;

    @JsonProperty("EADADNU")
    private String employmentAdditionalNumber;

    @JsonProperty("EADUNTNUM")
    private String employmentUnitNumber;

    @JsonProperty("EITHI")
    private BigDecimal otherIncome;

    // Default constructor
    public SimahReportResponseDto() {}

    // Method to generate report map with non-null values only
    public Map<String, Object> generateReportData() {
        Map<String, Object> reportData = new HashMap<>();

        // Use reflection or manual mapping to include only non-null values
        if (creditInstrumentNumber != null) reportData.put("Credit Instrument Number", creditInstrumentNumber);
        if (issueDate != null) reportData.put("Issue Date", issueDate);
        if (productType != null) reportData.put("Product Type", productType);
        if (productLimit != null) reportData.put("Product Limit / Original Amount", productLimit);
        if (salaryAssignmentFlag != null) reportData.put("Salary Assignment Flag", salaryAssignmentFlag);
        if (productExpiryDate != null) reportData.put("Product Expiry Date", productExpiryDate);
        if (productStatus != null) reportData.put("Product Status", productStatus);
        if (instalmentAmount != null) reportData.put("Instalment Amount", instalmentAmount);
        if (averageInstalmentAmount != null) reportData.put("Average Instalment Amount", averageInstalmentAmount);
        if (paymentFrequency != null) reportData.put("Payment Frequency", paymentFrequency);
        if (tenure != null) reportData.put("Tenure", tenure);
        if (securityType != null) reportData.put("Security Type", securityType);
        if (downPayment != null) reportData.put("Down Payment", downPayment);
        if (balloonPayment != null) reportData.put("Balloon Payment", balloonPayment);
        if (dispensedAmount != null) reportData.put("Dispensed Amount", dispensedAmount);
        if (maxInstalmentAmount != null) reportData.put("Max Instalment Amount", maxInstalmentAmount);
        if (subProductType != null) reportData.put("Sub Product Type", subProductType);
        if (totalLeasingAmount != null) reportData.put("Total Leasing Amount", totalLeasingAmount);
        if (reasonForClosureCode != null) reportData.put("Reason For Closure Code", reasonForClosureCode);
        if (factoringFlag != null) reportData.put("Factoring Flag", factoringFlag);

        // Add installment information
        if (installmentStartDate1 != null) reportData.put("Installment Start Date 1", installmentStartDate1);
        if (installmentAmount1 != null) reportData.put("Installment Amount 1", installmentAmount1);
        if (installmentStartDate2 != null) reportData.put("Installment Start Date 2", installmentStartDate2);
        if (installmentAmount2 != null) reportData.put("Installment Amount 2", installmentAmount2);
        if (installmentStartDate3 != null) reportData.put("Installment Start Date 3", installmentStartDate3);
        if (installmentAmount3 != null) reportData.put("Installment Amount 3", installmentAmount3);
        if (installmentStartDate4 != null) reportData.put("Installment Start Date 4", installmentStartDate4);
        if (installmentAmount4 != null) reportData.put("Installment Amount 4", installmentAmount4);
        if (installmentStartDate5 != null) reportData.put("Installment Start Date 5", installmentStartDate5);
        if (installmentAmount5 != null) reportData.put("Installment Amount 5", installmentAmount5);

        // Add contract information
        if (contractNumber != null) reportData.put("Contract Number", contractNumber);
        if (firstInstallmentDate != null) reportData.put("First Installment Date", firstInstallmentDate);
        if (costRate != null) reportData.put("Cost Rate", costRate);
        if (amountRate != null) reportData.put("Amount Rate", amountRate);
        if (fixedRate != null) reportData.put("Fixed Rate", fixedRate);
        if (numberOfCreditInstrumentHolders != null) reportData.put("Number of Credit Instrument Holders", numberOfCreditInstrumentHolders);
        if (cycleId != null) reportData.put("Cycle ID", cycleId);

        // Add payment information
        if (lastPaymentDate != null) reportData.put("Last Payment Date", lastPaymentDate);
        if (lastAmountPaid != null) reportData.put("Last Amount Paid", lastAmountPaid);
        if (paymentStatus != null) reportData.put("Payment Status", paymentStatus);
        if (outstandingBalance != null) reportData.put("Outstanding Balance", outstandingBalance);
        if (pastDueBalance != null) reportData.put("Past Due Balance", pastDueBalance);
        if (asOfDate != null) reportData.put("As of Date", asOfDate);
        if (nextPaymentDate != null) reportData.put("Next Payment Date", nextPaymentDate);
        if (preferredMethodOfPayment != null) reportData.put("Preferred Method of Payment", preferredMethodOfPayment);
        if (numberOfPaidInstallments != null) reportData.put("Number of Paid Installments", numberOfPaidInstallments);
        if (earlyPayoff != null) reportData.put("Early Payoff", earlyPayoff);
        if (numberOfUnpaidInstallments != null) reportData.put("Number of Unpaid Installments", numberOfUnpaidInstallments);
        if (amountPaidToThirdParty != null) reportData.put("Amount Paid to 3rd Party", amountPaidToThirdParty);
        if (rightOfWithdrawalCode != null) reportData.put("Right of Withdrawal Code", rightOfWithdrawalCode);
        if (aprPercentage != null) reportData.put("APR Percentage", aprPercentage);
        if (terminationProcedureCode != null) reportData.put("Termination Procedure Code", terminationProcedureCode);
        if (ownershipChangeCode != null) reportData.put("Ownership Change Code", ownershipChangeCode);
        if (amountOfOwnershipAsPerLaw != null) reportData.put("Amount of Ownership as per Law", amountOfOwnershipAsPerLaw);
        if (adminNotarialFees != null) reportData.put("Admin & Notarial Fees", adminNotarialFees);
        if (paymentType != null) reportData.put("Payment Type", paymentType);
        if (amountOfPendingFutureInstallments != null) reportData.put("Amount of Pending Future Installments", amountOfPendingFutureInstallments);
        if (amountOfUnpaidInstallments != null) reportData.put("Amount of Unpaid Installments", amountOfUnpaidInstallments);

        // Add consumer information
        if (idType != null) reportData.put("ID Type", idType);
        if (consumerId != null) reportData.put("Consumer ID", consumerId);
        if (idExpirationDate != null) reportData.put("ID Expiration Date", idExpirationDate);
        if (nationalIdIqamaIssuingPlace != null) reportData.put("National ID / Iqama Issuing Place", nationalIdIqamaIssuingPlace);
        if (maritalStatus != null) reportData.put("Marital Status", maritalStatus);
        if (nationalityCode != null) reportData.put("Nationality Code", nationalityCode);

        // Add name information
        if (familyNameArabic != null) reportData.put("Family Name - Arabic", familyNameArabic);
        if (firstNameArabic != null) reportData.put("First Name - Arabic", firstNameArabic);
        if (cnm2Arabic != null) reportData.put("CNM2A", cnm2Arabic);
        if (cnm3Arabic != null) reportData.put("CNM3A", cnm3Arabic);
        if (fullNameArabic != null) reportData.put("Full Name - Arabic", fullNameArabic);
        if (familyNameEnglish != null) reportData.put("Family Name - English", familyNameEnglish);
        if (firstNameEnglish != null) reportData.put("First Name - English", firstNameEnglish);
        if (cnm2English != null) reportData.put("CNM2E", cnm2English);
        if (cnm3English != null) reportData.put("CNM3E", cnm3English);
        if (fullNameEnglish != null) reportData.put("Full Name - English", fullNameEnglish);

        // Add personal information
        if (dateOfBirth != null) reportData.put("Date of Birth", dateOfBirth);
        if (gender != null) reportData.put("Gender", gender);
        if (numberOfDependents != null) reportData.put("Number of Dependents", numberOfDependents);
        if (applicantType != null) reportData.put("Applicant Type", applicantType);
        if (percentageAllocation != null) reportData.put("Percentage Allocation", percentageAllocation);
        if (applicantOutstandingBalance != null) reportData.put("Applicant Outstanding Balance", applicantOutstandingBalance);
        if (applicantLimit != null) reportData.put("Applicant Limit", applicantLimit);
        if (applicantLastAmountPaid != null) reportData.put("Applicant Last Amount Paid", applicantLastAmountPaid);
        if (applicantInstalmentAmount != null) reportData.put("Applicant Instalment Amount", applicantInstalmentAmount);
        if (applicantLastPaymentDate != null) reportData.put("Applicant Last Payment Date", applicantLastPaymentDate);
        if (applicantNextDueDate != null) reportData.put("Applicant Next Due Date", applicantNextDueDate);
        if (applicantPastDueBalance != null) reportData.put("Applicant Past Due Balance", applicantPastDueBalance);
        if (applicantPaymentStatus != null) reportData.put("Applicant Payment Status", applicantPaymentStatus);

        if (currentBuildingNumber != null) reportData.put("Current Building Number", currentBuildingNumber);
        if (currentStreetEnglish != null) reportData.put("Current Street English", currentStreetEnglish);
        if (currentStreetArabic != null) reportData.put("Current Street Arabic", currentStreetArabic);
        if (currentDistrictEnglish != null) reportData.put("Current District English", currentDistrictEnglish);
        if (currentDistrictArabic != null) reportData.put("Current District Arabic", currentDistrictArabic);
        if (currentAdditionalNumber != null) reportData.put("Current Additional Number", currentAdditionalNumber);
        if (currentUnitNumber != null) reportData.put("Current Unit Number", currentUnitNumber);
        if (employmentBuildingNumber != null) reportData.put("Employment Building Number", employmentBuildingNumber);
        if (employmentStreetEnglish != null) reportData.put("Employment Street English", employmentStreetEnglish);
        if (employmentStreetArabic != null) reportData.put("Employment Street Arabic", employmentStreetArabic);
        if (employmentDistrictEnglish != null) reportData.put("Employment District English", employmentDistrictEnglish);
        if (employmentDistrictArabic != null) reportData.put("Employment District Arabic", employmentDistrictArabic);
        if (employmentAdditionalNumber != null) reportData.put("Employment Additional Number", employmentAdditionalNumber);
        if (employmentUnitNumber != null) reportData.put("Employment Unit Number", employmentUnitNumber);
        if (otherIncome != null) reportData.put("Other Income", otherIncome);

        return reportData;
    }
}