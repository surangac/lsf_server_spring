package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.requestMsg.ShareTransferRequest;
import com.dfn.lsf.model.responseMsg.OrderContractCustomerInfo;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_CUSTOMER_INQUIRY_PROCESS;

/**
 * Defined in InMessageHandlerAdminCbr,InMessageHandlerCbr
 * route : CUSTOMER_INQUIRY_ROUTE
 * Handling Message types :
 * - MESSAGE_TYPE_CUSTOMER_INQUIRY_PROCESS = 20;
 */
@Service
@MessageType(MESSAGE_TYPE_CUSTOMER_INQUIRY_PROCESS)
@RequiredArgsConstructor
public class CustomerInquiryProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CustomerInquiryProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(request, map.getClass());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                switch (subMessageType) {
                    case LsfConstants.REQ_CUSTOMER_DETAILS_IFLEX:/*----validating customer status & send the response
                     to lsf client--*/
                        return getCustomerDetails(map);
                    case LsfConstants.REQ_PF_VALUE:/*-----------Get Portfolio Value for Trading Account-----------*/
                        return getPFValuefromOMS(map);
                    case LsfConstants.GET_MURABAH_APPLICATION_USER_INFO:/*-----------Send Customer Details during
                    first Login If client has create an application- Client----------*/
                        return getMurabahApplicationUserInfo(map);
                    case LsfConstants.GET_SAVED_ANSWER_FOR_USER:/*-----------Send Customer Questionier Answers -
                    Client----------*/
                        return getSavedAnswerForUser(map);
                    case LsfConstants.REQ_TRADING_ACC_LIST:/*-----------Send Customer Questionier Answers -
                    Client----------*/
                        return getTradingAccountList(map);
                    case "getAppAccounts":
                        return getApplicationAccounts(map);
                    case LsfConstants.GET_CUSTOMER_DETAILS_ORDER_CONTRACT: /*-----Get Customer Details for Order
                    Contract-----*/
                        return getCustomerDetailsForOrderContract(map);
                    case LsfConstants.GET_CUSTOMER_RISK_SCORE:
                        return getCustomerRiskScore(map);
                }
            }
        } catch (Exception ex) {
            cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            ex.printStackTrace();
        }
        return gson.toJson(cmr);
    }

    private String getCustomerDetails(Map<String, Object> map) {
        String customerID = map.get("customerId").toString();
        String corellationID = "";
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        logger.info("===========LSF : (reqCustomerDetailsIflex)REQUEST, customerID  :"
                    + customerID
                    + " , CorrelationID:"
                    + corellationID);
        List<CashAcc> cashAccList = new ArrayList<>();
        CommonInqueryMessage customerInfoRequest = new CommonInqueryMessage();
        customerInfoRequest.setReqType(LsfConstants.GET_CUSTOMER_INFO);
        customerInfoRequest.setCustomerId(customerID);
        String result = helper.getCustomerRelatedOMSData(gson.toJson(customerInfoRequest));
        if (result == null) {
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(200);
            cmr.setResponseMessage("No information received for user , User ID:" + customerID + "  from OMS");
            return gson.toJson(cmr);
        } else {
            CustomerInfoResponse customerInfoResponse = gson.fromJson(result, CustomerInfoResponse.class);
            CustomerInfo customerInfo = new CustomerInfo();
            LinkedTreeMap<Object, Object> resMap =
                    (LinkedTreeMap<Object, Object>) customerInfoResponse.getResponseObject();
            if (resMap.containsKey("fullName")) {
                customerInfo.setNameInFull(resMap.get("fullName").toString());
            }
            if (resMap.containsKey("fullAddress")) {
                customerInfo.setAddress(resMap.get("fullAddress").toString());
            }
            if (resMap.containsKey("mobile")) {
                customerInfo.setMobileNo(resMap.get("mobile").toString());
            }
            if (resMap.containsKey("email")) {
                customerInfo.setEmail(resMap.get("email").toString());
            }
            if (resMap.containsKey("occupation")) {
                customerInfo.setOccupation(resMap.get("occupation").toString());
            }
            if (resMap.containsKey("monthlySal")) {
                customerInfo.setAvgMonthlyIncome(Double.parseDouble(resMap.get("monthlySal").toString()));
            }
            if (resMap.containsKey("homeTelephone")) {
                customerInfo.setTelephoneNo(resMap.get("homeTelephone").toString());
            }
            if (resMap.containsKey("fax1")) {
                customerInfo.setFax(resMap.get("fax1").toString());
            }
            if (resMap.containsKey("custRefenceNo")) {
                customerInfo.setCustomerReferenceNumber(resMap.get("custRefenceNo").toString());
            }
            if (resMap.containsKey("zipCode")) {
                customerInfo.setZipCode(resMap.get("zipCode").toString());
            }
            if (resMap.containsKey("bankBranchName")) {
                customerInfo.setBankBranchName(resMap.get("bankBranchName").toString());
            }
            if (resMap.containsKey("city")) {
                customerInfo.setCity(resMap.get("city").toString());
            }
            if (resMap.containsKey("poBox")) {
                customerInfo.setPoBox(resMap.get("poBox").toString());
            }
            if (resMap.containsKey("employer")) {
                customerInfo.setEmployeer(resMap.get("employer").toString());
            }
            if (resMap.containsKey("isArabic")) {
                customerInfo.setPreferedLanguage(resMap.get("isArabic").toString().equals("true") ? "A" : "E");
            }
            if (resMap.containsKey("empAddress")) {
                customerInfo.setEmployerAdrs(resMap.get("empAddress").toString());
            }
            if (resMap.containsKey("aproxNetWorth")) {
                customerInfo.setNetWorth(resMap.get("aproxNetWorth").toString());
            }
            if (resMap.containsKey("investExprnc")) {
                customerInfo.setInvestExprnc(resMap.get("investExprnc").toString());
            }
            if (resMap.containsKey("riskAppetite")) {
                customerInfo.setRiskAppetite(resMap.get("riskAppetite").toString());
            }
            CommonInqueryMessage customerCashAccountRequest = new CommonInqueryMessage();
            customerCashAccountRequest.setReqType(LsfConstants.GET_NON_LSF_CASH_ACCOUNT_DETAILS);
            customerCashAccountRequest.setCustomerId(customerID);
            String cashAccountResult = helper.cashAccountRelatedOMS(gson.toJson(customerCashAccountRequest));
            if (cashAccountResult != null) {
                CashAccountResponse cashAccountResponse = new CashAccountResponse();
                cashAccountResponse = gson.fromJson((String) cashAccountResult, CashAccountResponse.class);
                List<Object> tempcCashAccountList = (List<Object>) cashAccountResponse.getResponseObject();
                for (int j = 0; j < tempcCashAccountList.size(); j++) {
                    Map<Object, Object> cash = (Map<Object, Object>) tempcCashAccountList.get(j);
                    CashAcc cashAcc = CashAcc.builder().build();
                    if (cash.containsKey("accountNo")) {
                        cashAcc.setAccountId(cash.get("accountNo").toString());
                    }
                    if (cash.containsKey("currency")) {
                        cashAcc.setCurrencyCode(cash.get("currency").toString());
                    }
                    cashAccList.add(cashAcc);
                }
                CashAcc cashAc1 = CashAcc.builder().build();
                cashAc1.setAccountId("001");
                cashAc1.setCurrencyCode("SAR");
                cashAccList.add(cashAc1);
                customerInfo.setBankAccounts(cashAccList);
            }
            logger.info("===========LSF : (reqCustomerDetailsIflex)LSF-SERVER RESPONSE  :"
                        + gson.toJson(customerInfo)
                              .toString()
                        + " , CorrelationID:"
                        + corellationID);
            return gson.toJson(customerInfo).toString();
        }
    }

    private String getPFValuefromOMS(Map<String, Object> map) {
        logger.info("===========LSF : (reqPFvalue)-REQUEST, params: " + gson.toJson(map));
        CommonResponse cmr = new CommonResponse();
        try {
            if (map.containsKey("tradingAcc")) {
//                String accountNo = map.get("tradingAcc").toString();
//                ShareTransferRequest pfRequest = new ShareTransferRequest();
//                pfRequest.setReqType(LsfConstants.GET_PORTFOLIO_VALUE);
//                pfRequest.setFromTradingAccountId(accountNo);
//                if (map.containsKey("tradingAccExchange")) {
//                    pfRequest.setExchange(map.get("tradingAccExchange").toString());
//                }
//                cmr = helper.processOMSCommonResponse(helper.portfolioRelatedOMS(gson.toJson(pfRequest)));

                var customerId = map.get("customerId").toString();
                String accountNo = map.get("tradingAcc").toString();
                var defaultGroup = lsfRepository.getDefaultMarginGroups();
                if (defaultGroup == null) {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Default Margin Group not found!");
                    return gson.toJson(cmr);
                }
                var groupId = defaultGroup.getFirst().getId();

                List<TradingAccOmsResp> nonLsfAccounts = helper.getPFDetailsNonLSF(customerId, groupId);
                var tradingAccount = nonLsfAccounts.stream().filter(tradingAccOmsResp -> tradingAccOmsResp.getAccountId().equals(accountNo))
                                                   .findFirst();

                if (tradingAccount.isPresent()) {
                    var tradingAcc = tradingAccount.get();
                    double totalPfValue = tradingAcc.getSymbolList().stream()
                                                    .mapToDouble(symbol -> (symbol.getMarketValue()/100) * symbol.getMarginabilityPercentage())
                                                    .sum();

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("amount", String.format("%.2f", totalPfValue));

                    cmr.setResponseObject(responseMap);
                    cmr.setResponseCode(200);
                    cmr.setResponseMessage("Portfolio Value retrieved successfully.");
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Trading account not found: " + accountNo);
                }
            }

        } catch (Exception ex) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("PF request Failed!");
            cmr.setErrorCode(LsfConstants.ERROR_PF_REQUEST_FAILED);
        }
        logger.info("===========LSF : (reqPFvalue)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String getMurabahApplicationUserInfo(Map<String, Object> returnMap) {

        logger.info("===========LSF : (getMurabahApplicationUserInfo)-REQUEST, params: " + gson.toJson(returnMap));
        int filterCriteria = 0;
        String applicationID = "";
        boolean isModified = false;
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        List<MurabahApplication> murabahApplications = null;
        List<Symbol> symbols = null;
        MurabahApplication murabahApplication = null;
        List<MurabahApplication> toResponse = new ArrayList<>();
        MurabahApplicationListResponse listResponse = new MurabahApplicationListResponse();
        if (returnMap.containsKey("filterCriteria")) {
            filterCriteria = Integer.parseInt(returnMap.get("filterCriteria").toString());
            if (filterCriteria == LsfConstants.APPLICATION_ID) {
                if (returnMap.containsKey("filterValue")) {
                    applicationID = returnMap.get("filterValue").toString();
                    murabahApplications = lsfRepository.getMurabahAppicationApplicationID(applicationID);
                    if (murabahApplications != null) {
                        if (!murabahApplications.isEmpty()) {
                            murabahApplication = murabahApplications.getFirst();
                            CommonInqueryMessage customerInfoRequest = new CommonInqueryMessage();
                            customerInfoRequest.setReqType(LsfConstants.GET_CUSTOMER_INFO);
                            customerInfoRequest.setCustomerId(murabahApplication.getCustomerId());
                            String result = helper.getCustomerRelatedOMSData(gson.toJson(customerInfoRequest));
                            if (result != null) {
                                CustomerInfoResponse customerInfoResponse = gson.fromJson(
                                        (String) result,
                                        CustomerInfoResponse.class); // getting the user information from OMS and
                                // overriding changed data
                                LinkedTreeMap<Object, Object> resMapFromOMS =
                                        (LinkedTreeMap<Object, Object>) customerInfoResponse.getResponseObject();
                                if (resMapFromOMS.containsKey("fullName")) {
                                    if (murabahApplication.getFullName() != null) {
                                        if (!murabahApplication.getFullName().equalsIgnoreCase(resMapFromOMS.get("fullName").toString())) {
                                            murabahApplication.setFullName(resMapFromOMS.get("fullName").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setFullName(resMapFromOMS.get("fullName").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("fullAddress")) {
                                    if (murabahApplication.getAddress() != null) {
                                        if (!murabahApplication.getAddress()
                                                               .equalsIgnoreCase(resMapFromOMS.get("fullAddress")
                                                                                              .toString())) {
                                            murabahApplication.setAddress(resMapFromOMS.get("fullAddress").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setAddress(resMapFromOMS.get("fullAddress").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("mobile")) {
                                    if (murabahApplication.getMobileNo() != null) {
                                        if (!murabahApplication.getMobileNo()
                                                               .equalsIgnoreCase(resMapFromOMS.get("mobile")
                                                                                              .toString())) {
                                            murabahApplication.setMobileNo(resMapFromOMS.get("mobile").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setMobileNo(resMapFromOMS.get("mobile").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("email")) {
                                    if (murabahApplication.getEmail() != null) {
                                        if (!murabahApplication.getEmail()
                                                               .equalsIgnoreCase(resMapFromOMS.get("email")
                                                                                              .toString())) {
                                            murabahApplication.setEmail(resMapFromOMS.get("email").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setEmail(resMapFromOMS.get("email").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("occupation")) {
                                    if (murabahApplication.getOccupation() != null) {
                                        if (!murabahApplication.getOccupation()
                                                               .equalsIgnoreCase(resMapFromOMS.get("occupation")
                                                                                              .toString())) {
                                            murabahApplication.setOccupation(resMapFromOMS.get("occupation")
                                                                                          .toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setOccupation(resMapFromOMS.get("occupation").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("monthlySal")) {
                                    if (String.valueOf(murabahApplication.getAvgMonthlyIncome()) != null) {
                                        if (!String.valueOf(murabahApplication.getAvgMonthlyIncome())
                                                   .equalsIgnoreCase(resMapFromOMS.get("monthlySal").toString())) {
                                            murabahApplication.setAvgMonthlyIncome(Double.parseDouble(resMapFromOMS.get(
                                                    "monthlySal").toString()));
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setAvgMonthlyIncome(Double.parseDouble(resMapFromOMS.get(
                                                "monthlySal").toString()));
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("homeTelephone")) {
                                    if (murabahApplication.getTeleNo() != null) {
                                        if (!murabahApplication.getTeleNo()
                                                               .equalsIgnoreCase(resMapFromOMS.get("homeTelephone")
                                                                                              .toString())) {
                                            murabahApplication.setTeleNo(resMapFromOMS.get("homeTelephone").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setTeleNo(resMapFromOMS.get("homeTelephone").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("fax1")) {
                                    if (murabahApplication.getFax() != null) {
                                        if (!murabahApplication.getFax()
                                                               .equalsIgnoreCase(resMapFromOMS.get("fax1")
                                                                                              .toString())) {
                                            murabahApplication.setFax(resMapFromOMS.get("fax1").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setFax(resMapFromOMS.get("fax1").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("zipCode")) {
                                    if (murabahApplication.getZipCode() != null) {
                                        if (!murabahApplication.getZipCode()
                                                               .equalsIgnoreCase(resMapFromOMS.get("zipCode")
                                                                                              .toString())) {
                                            murabahApplication.setZipCode(resMapFromOMS.get("zipCode").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setZipCode(resMapFromOMS.get("zipCode").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("bankBranchName")) {
                                    if (murabahApplication.getBankBranchName() != null) {
                                        if (!murabahApplication.getBankBranchName()
                                                               .equalsIgnoreCase(resMapFromOMS.get("bankBranchName")
                                                                                              .toString())) {
                                            murabahApplication.setBankBranchName(resMapFromOMS.get("bankBranchName")
                                                                                              .toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setBankBranchName(resMapFromOMS.get("bankBranchName")
                                                                                          .toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("city")) {
                                    if (murabahApplication.getCity() != null) {
                                        if (!murabahApplication.getCity()
                                                               .equalsIgnoreCase(resMapFromOMS.get("city")
                                                                                              .toString())) {
                                            murabahApplication.setCity(resMapFromOMS.get("city").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setCity(resMapFromOMS.get("city").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("poBox")) {
                                    if (murabahApplication.getPoBox() != null) {
                                        if (!murabahApplication.getPoBox()
                                                               .equalsIgnoreCase(resMapFromOMS.get("poBox")
                                                                                              .toString())) {
                                            murabahApplication.setPoBox(resMapFromOMS.get("poBox").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPoBox(resMapFromOMS.get("poBox").toString());
                                        isModified = true;
                                    }
                                }

                                if (resMapFromOMS.containsKey("employer")) {
                                    if (murabahApplication.getEmployer() != null) {
                                        if (!murabahApplication.getEmployer()
                                                               .equalsIgnoreCase(resMapFromOMS.get("employer")
                                                                                              .toString())) {
                                            murabahApplication.setEmployer(resMapFromOMS.get("employer").toString());
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setEmployer(resMapFromOMS.get("employer").toString());
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("isArabic")) {
                                    String omsPreferedLanguage = resMapFromOMS.get("isArabic").toString().equals("true")
                                                                 ? "A"
                                                                 : "E";
                                    if (murabahApplication.getPreferedLanguage() != null) {
                                        if (!murabahApplication.getPreferedLanguage()
                                                               .equalsIgnoreCase(omsPreferedLanguage)) {
                                            murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("isArabic")) {
                                    String omsPreferedLanguage = resMapFromOMS.get("isArabic").toString().equals("true")
                                                                 ? "A"
                                                                 : "E";
                                    if (murabahApplication.getPreferedLanguage() != null) {
                                        if (!murabahApplication.getPreferedLanguage()
                                                               .equalsIgnoreCase(omsPreferedLanguage)) {
                                            murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("isArabic")) {
                                    String omsPreferedLanguage = resMapFromOMS.get("isArabic").toString().equals("true")
                                                                 ? "A"
                                                                 : "E";
                                    if (murabahApplication.getPreferedLanguage() != null) {
                                        if (!murabahApplication.getPreferedLanguage()
                                                               .equalsIgnoreCase(omsPreferedLanguage)) {
                                            murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("isArabic")) {
                                    String omsPreferedLanguage = resMapFromOMS.get("isArabic").toString().equals("true")
                                                                 ? "A"
                                                                 : "E";
                                    if (murabahApplication.getPreferedLanguage() != null) {
                                        if (!murabahApplication.getPreferedLanguage()
                                                               .equalsIgnoreCase(omsPreferedLanguage)) {
                                            murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                        isModified = true;
                                    }
                                }
                                if (resMapFromOMS.containsKey("isArabic")) {
                                    String omsPreferedLanguage = resMapFromOMS.get("isArabic").toString().equals("true")
                                                                 ? "A"
                                                                 : "E";
                                    if (murabahApplication.getPreferedLanguage() != null) {
                                        if (!murabahApplication.getPreferedLanguage()
                                                               .equalsIgnoreCase(omsPreferedLanguage)) {
                                            murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                            isModified = true;
                                        }
                                    } else {
                                        murabahApplication.setPreferedLanguage(omsPreferedLanguage);
                                        isModified = true;
                                    }
                                }
                            }
                            if (isModified) {
                                lsfRepository.updateMurabahApplication(murabahApplication);
                                logger.debug("===========LSF : Updating Application Customer Info, Customer ID "
                                             + murabahApplication.getCustomerId()
                                             + " , Applciation ID"
                                             + murabahApplication.getId());
                            }
                            symbols = lsfRepository.getInitialAppPortfolio(applicationID);
                            murabahApplication.setPflist(symbols);
                            List<Agreement> agreementList = lsfRepository.getActiveAgreements(Integer.parseInt(applicationID));
                            if (agreementList != null) {
                                murabahApplication.setAgreementList(agreementList);
                            }
                            List<PurchaseOrder> poList =
                                    lsfRepository.getPurchaseOrderForApplication(murabahApplication.getId());
                            if (poList != null && !poList.isEmpty()) {
                                try {
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    LocalDateTime apprvDate = LocalDateTime.parse(poList.getFirst().getApprovedDate(), formatter);
                                    murabahApplication.setRemainTimeToSell(LSFUtils.getRemainTimeForGracePrd(
                                            Date.from(apprvDate.atZone(ZoneId.systemDefault()).toInstant()),
                                            GlobalParameters.getInstance().getGracePeriodforCommoditySell()));
                                    listResponse.setCustomerContractComment(poList.getFirst().getApproveComment());

                                } catch (DateTimeParseException e) {
                                    logger.error("Error parsing approved date for application {}: {}", 
                                            murabahApplication.getId(), e.getMessage());
                                    throw new RuntimeException("Invalid date format in approved date", e);
                                }
                            }
                            if (murabahApplication.getRollOverAppId() == null) {
                                murabahApplication.setRollOverAppId("-1");
                            }
                            toResponse.add(murabahApplication);
                            listResponse.setApplicationList(toResponse);
                        }
                    }
                }
            }
        }
        logger.info("===========LSF : (getMurabahApplicationUserInfo)LSF-SERVER RESPONSE  :"
                    + gson.toJson(listResponse)
                    + " , CorrelationID:"
                    + corellationID);
        return gson.toJson(listResponse);
    }

    private String getSavedAnswerForUser(Map<String, Object> map) {
        logger.debug("===========LSF : (getSavedAnswerForUser)-REQUEST, params:" + gson.toJson(map));
        List<UserAnswer> answersList = null;
        UserAnswerResponse userAnswerResponse = new UserAnswerResponse();
        Map<String, String> answers = new HashMap<>();
        String corellationID = "";
        if (map.containsKey("corellationID")) {
            corellationID = map.get("corellationID").toString();
        }
        String userName = null;
        if (map.containsKey("customerId")) {
            userName = map.get("customerId").toString();
            answersList = lsfRepository.getSavedAnswerForUser(Integer.parseInt(userName));
            if (answersList != null) {

                for (UserAnswer userAnswer : answersList) {
                    if (userAnswer.getQuestionID() == 1) {
                        answers.put("Q1", userAnswer.getAnswer());
                        answers.put("ipAddress", answersList.get(0).getIpAddress());
                        answers.put("timeStamp", answersList.get(0).getDate());
                    }
                    if (userAnswer.getQuestionID() == 2) {
                        answers.put("Q2", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 3) {
                        answers.put("Q3", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 4) {
                        answers.put("Q4", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 5) {
                        answers.put("Q5", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 6) {
                        answers.put("Q6", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 7) {
                        answers.put("Q7", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 8) {
                        answers.put("Q8", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 9) {
                        answers.put("Q9", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 10) {
                        answers.put("Q10", userAnswer.getAnswer());
                    }
                    if (userAnswer.getQuestionID() == 11) {
                        answers.put("Q11", userAnswer.getAnswer());
                    }
                }
            }
            logger.info("===========LSF : (getSavedAnswerForUser)LSF-SERVER RESPONSE  :"
                        + gson.toJson(answers)
                        + " , CorrelationID:"
                        + corellationID);
            return gson.toJson(answers);
        } else {
            logger.error("===========LSF : Request Does not contains the customer ID");
            return "Please Check Request Params";
        }
    }

    private String getTradingAccountList(Map<String, Object> map) {
        CommonInqueryMessage trReq = new CommonInqueryMessage();
        trReq.setReqType(LsfConstants.GET_TRADING_ACCOUNT_LIST);
        trReq.setCustomerId(map.get("customerId").toString());
        logger.info("===========LSF(reqTradingAccList): REQUEST , customerID" + trReq.getCustomerId());
        String resMap = helper.portfolioRelatedOMS(gson.toJson(trReq));
        logger.info("===========LSF (reqTradingAccList): LSF-SERVER RESPONSE  :"
                    + gson.toJson(resMap)
                    + ", customerID :"
                    + trReq.getCustomerId());
        return resMap;
    }

    private String getApplicationAccounts(Map<String, Object> map) {
        MurabahApplication murabahApplication = null;
        String applicationID = map.get("applicationID").toString();
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCollateral(applicationID);
        UserAccountDetails userAccountDetails = null;
        userAccountDetails = lsfRepository.getApplicationAccountDetails(applicationID, collaterals.getId());
        if (userAccountDetails.getLsfTypeCashAccount() == null
            || userAccountDetails.getLsfTypeTradingAccount() == null) {
            murabahApplication = lsfRepository.getMurabahApplication(applicationID);
            CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(
                    murabahApplication.getCustomerId(),
                    murabahApplication.getId());
            TradingAcc lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(
                    murabahApplication.getCustomerId(),
                    murabahApplication.getId());
            userAccountDetails.setLsfTypeCashAccount(lsfCashAccount.getAccountId());
            userAccountDetails.setLsfTypeTradingAccount(lsfTradingAccount.getAccountId());
        }
        return gson.toJson(userAccountDetails);
    }

    private String getCustomerDetailsForOrderContract(Map<String, Object> map) {
        String applicationID = map.get("applicationID").toString();
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationID);
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        boolean isCommodityApplication = murabahApplication.getFinanceMethod().equalsIgnoreCase("2");
        logger.info("===========LSF(getCustomerDetailsOrderContract): REQUEST , appID" + applicationID);
        OrderContractCustomerInfo contractCustomerInfo = lsfRepository.getOrderContractCustomerInfo(applicationID);
        if (contractCustomerInfo != null) {
            //  contractCustomerInfo.setContractSignDate(dateFormat.format(new Date()));
            contractCustomerInfo.setCRNumber("1010240489"); // as per the ABIC request
            contractCustomerInfo.setCRIssueDate("1428/11/11");// as per the ABIC request4
            contractCustomerInfo.setAddress(murabahApplication.getAddress());
         /*   if(collaterals != null){
                if(collaterals.getLsfTypeCashAccounts() != null && collaterals.getLsfTypeCashAccounts().size() > 0){
                    contractCustomerInfo.setInvestmentAccountNumber(collaterals.getLsfTypeCashAccounts().get(0)
                    .getAccountId());
                }
            }
            if(collaterals != null){
                if(collaterals.getLsfTypeTradingAccounts() != null && collaterals.getLsfTypeTradingAccounts().size()
                > 0){
                    contractCustomerInfo.setMurabahaPFNumber(collaterals.getLsfTypeTradingAccounts().get(0)
                    .getAccountId());
                }
            }*/
            CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(
                    contractCustomerInfo.getCustomerID(),
                    applicationID);
            if (lsfCashAccount != null) {
                contractCustomerInfo.setInvestmentAccountNumber(lsfCashAccount.getInvestmentAccountNumber() == null
                                                                ? lsfCashAccount.getAccountId()
                                                                : lsfCashAccount.getInvestmentAccountNumber());
            }

            TradingAcc lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(
                    contractCustomerInfo.getCustomerID(),
                    applicationID);
            if (lsfTradingAccount != null) {
                contractCustomerInfo.setMurabahaPFNumber(lsfTradingAccount.getAccountId());
            }

            contractCustomerInfo.setCustomerCity(contractCustomerInfo.getBranchCity());

            double adminFee = isCommodityApplication ? GlobalParameters.getInstance().getComodityAdminFee() : GlobalParameters.getInstance().getShareAdminFee();
            adminFee = adminFee + GlobalParameters.getInstance().getTransferCharges();
            double vatAmount=LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(adminFee));
            contractCustomerInfo.setTransferringFee(adminFee);
            contractCustomerInfo.setTransferCharges(vatAmount);
            contractCustomerInfo.setCollaterals(collaterals);
            contractCustomerInfo.setCustomerActivityID(murabahApplication.getCustomerActivityID());
            contractCustomerInfo.setIsExchangeAccountCreated(collaterals.isExchangeAccountCreated());
            contractCustomerInfo.setDiscountOnProfit(murabahApplication.getDiscountOnProfit());
        } else {
            contractCustomerInfo = new OrderContractCustomerInfo();
        }
        logger.info("===========LSF(getCustomerDetailsOrderContract): RESPONSE :" + gson.toJson(contractCustomerInfo));
        return gson.toJson(contractCustomerInfo);
    }

    private String getCustomerRiskScore(Map<String, Object> reqMap) {
        logger.info("===========LSF : (getCustomerRiskScore)-REQUEST, params: " + gson.toJson(reqMap));
        CommonResponse cmr = new CommonResponse();
        try {
            if (reqMap.containsKey("customerID")) {
                String customerID = reqMap.get("customerID").toString();
                CommonInqueryMessage cim = new CommonInqueryMessage();
                cim.setReqType(LsfConstants.SERVICE_TYPE_CUSTOMER_RISK_SCORE);
                cim.setCustomerId(customerID);
                String result = (String) helper.sendMessageToOms(gson.toJson(cim));
                JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
                String responseObject = jsonObject.get("responseObject").getAsString();
                String[] values = responseObject.split(Pattern.quote("||"));
                cmr.setResponseCode(Integer.parseInt(values[0]));
                cmr.setResponseMessage(values[1]);
            }
        } catch (Exception ex) {
            logger.info("===========LSF : EX  : " + ex.getMessage());
            logger.info("===========LSF : EX2  : " + ex.toString());

            cmr.setResponseCode(500);
            cmr.setErrorMessage("PF request Failed!");
            cmr.setErrorCode(LsfConstants.ERROR_PF_REQUEST_FAILED);
        }
        logger.info("===========LSF : (getCustomerRiskScore)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
}
