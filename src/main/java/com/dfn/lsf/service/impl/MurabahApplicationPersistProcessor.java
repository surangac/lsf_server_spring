package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.application.ApplicationRating;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.responseMsg.TradingAccResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.service.security.CustomEncryption;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_LOAN_PERSIST_PROCESS;

/**
 * Defined in InMessageHandlerAdminCbr, InMessageHandlerCbr
 * route : APPLY_MURABAH_FACILITY_CLIENT
 * Handling Message types :
 * - MESSAGE_TYPE_LOAN_PERSIST_PROCESS = 7;
 */
@Service
@MessageType(MESSAGE_TYPE_LOAN_PERSIST_PROCESS)
@RequiredArgsConstructor
public class MurabahApplicationPersistProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MurabahApplicationPersistProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final NotificationManager notificationManager;
    private final LsfCoreService lsfCoreService;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map = gson.fromJson(request, map.getClass());
        String subMessageType = map.get("subMessageType").toString();
        switch (subMessageType) {
            case "createApplication":
                return createApplication(map);/*-----------creating Murabah App-----------*/
            case "updateApplicationPara":
                return updateApplicationParameters(map);/*----------Updating App Parameters-------*/
            case "validateTradingAcc":/*------------Validating Trading Account------------*/
                return validateTradingAcc(map);
            case "saveQuestionerForUser":/*------------Saving Questioner for User*/
                return saveQuestionerForUser(map);
            case "rateApplication":/*------------Rate loan application*/
                return rateApplication(map);
            case "getApplicationRating":/*------------Rate loan application*/
                return getApplicationRating(map);
            case "whiteListApplication":
                return whiteListApplication(map);
            default:
                return null;
        }
    }

    private String createApplication(Map<String, Object> map) {
        ArrayList<Symbol> pflist = null;
        String dibAcc;
        String customerId = "";


        if (map.containsKey("customerId")) {
            customerId = map.get("customerId").toString();
        }

        List<Symbol> symbolList = new ArrayList<>();
        CommonResponse cmr = new CommonResponse();
        if (map.get("tradingAcc").toString().equals("")) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_INVALIED_TRADING_ACC.errorDescription());
            return gson.toJson(cmr);
        }
        String tradingAcc = map.get("tradingAcc").toString();
        List<MurabahApplication> applicationList = lsfRepository.getNotGrantedApplication(customerId);
        //geMurabahAppicationUserIDFilteredByClosedApplication(userName);
        if (applicationList != null) {
            if (applicationList.size() != 0) {
                cmr.setResponseCode(500);
                cmr.setErrorMessage(
                        "You already have a pending contract. Kindly complete that contract before requesting for new"
                        + " contract");
                cmr.setErrorCode(LsfConstants.ERROR_APPLICATION_IS_AVAILABLE_FOR_THIS_CUSTOMER);
                logger.info("===========LSF : User has already created a Murabah Application , Application ID :"
                            + map.get("id"));
                return gson.toJson(cmr);
            }
        }
        // validation for Max number of contracts
        int allContracts = lsfCoreService.getNoOfOpenMurabahContracts(map.get("customerId").toString());
        if (allContracts >= GlobalParameters.getInstance().getMaxNumberOfActiveContracts()) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(
                    "You have applied for maximum number of contracts Customer can hold. Kindly contact Albilad "
                    + "capital for assistance");
            cmr.setErrorCode(LsfConstants.ERROR_APPLICATION_IS_AVAILABLE_FOR_THIS_CUSTOMER);
            logger.info("===========LSF : User has Reached max number of Opend Contracts count :" + allContracts);
            return gson.toJson(cmr);
        }

        MurabahApplication murabahApplication = new MurabahApplication();
        murabahApplication.setId("-1");
        murabahApplication.setCustomerId(map.get("customerId").toString());

        if (map.containsKey("address") && (map.get("address")) != null) {
            murabahApplication.setAddress(map.get("address").toString());
        }
        if (map.containsKey("avgMonthlyIncome") && (map.get("avgMonthlyIncome")) != null) {
            murabahApplication.setAvgMonthlyIncome(Double.parseDouble(map.get("avgMonthlyIncome").toString()));
        }
        if (map.containsKey("email") && (map.get("email")) != null) {
            murabahApplication.setEmail(map.get("email").toString());
        }
        if (map.containsKey("employer") && (map.get("employer")) != null) {
            murabahApplication.setEmployer(map.get("employer").toString());
        }
        if (map.containsKey("fax") && (map.get("fax")) != null) {
            murabahApplication.setFax(map.get("fax").toString());
        }

        if (map.containsKey("dibAcc")) {
            dibAcc = map.get("dibAcc").toString();
            murabahApplication.setDibAcc(dibAcc);
        }
        if (map.containsKey("availableCashBalance")) {
            murabahApplication.setAvailableCashBalance(Double.parseDouble(map.get("availableCashBalance").toString()));
        }
        if (map.containsKey("preferedLanguage")) {
            murabahApplication.setPreferedLanguage(map.get("preferedLanguage").toString());
        }
        murabahApplication.setFinanceRequiredAmt(Double.parseDouble(map.get("financeRequiredAmt").toString()));

        double totalOutstanding = lsfRepository.getMasterAccountOutstanding();
        Long maxBrokerageLimit = GlobalParameters.getInstance().getMaxBrokerageLimit();
        double maxAvailableCashBalanceInMasterAccount = (maxBrokerageLimit != null ? maxBrokerageLimit : 0.0) 
                                                        - totalOutstanding;
        logger.debug("===========MaxBrokerageLimit :" + GlobalParameters.getInstance().getMaxBrokerageLimit());
        logger.debug("===========ABIC Outstanding :" + totalOutstanding);
        logger.debug("===========Master Available Balance :" + maxAvailableCashBalanceInMasterAccount);
        if (murabahApplication.getFinanceRequiredAmt() > maxAvailableCashBalanceInMasterAccount) {
            cmr.setResponseCode(500);
            cmr.setResponseMessage(
                    "Requested loan amount Can't be processed at the moment. Please contact ALBILD Capital for "
                    + "further information");
            cmr.setErrorMessage(
                    "Requested loan amount Can't be processed at the moment. Please contact ALBILD Capital for "
                    + "further information");
            cmr.setErrorCode(LsfConstants.ERROR_REQUESTED_LOAN_AMOUNT_CANNOT_BE_PROCESSED);
            logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
            return gson.toJson(cmr);
        }

        murabahApplication.setProposedLimit(Double.parseDouble(map.get("financeRequiredAmt").toString()));
        if (map.containsKey("fullName") && (map.get("fullName")) != null) {
            murabahApplication.setFullName(map.get("fullName").toString());
        }
        if (map.containsKey("occupation") && (map.get("occupation")) != null) {
            murabahApplication.setOccupation(map.get("occupation").toString());
        }
        if (map.containsKey("isSelfEmp") && (map.get("isSelfEmp")) != null) {
            murabahApplication.setSelfEmp(Boolean.parseBoolean(map.get("isSelfEmp").toString()));
        }
        if (map.containsKey("lineOfBusiness") && (map.get("lineOfBusiness")) != null) {
            murabahApplication.setLineOfBusiness(map.get("lineOfBusiness").toString());
        }
        if (map.containsKey("mobileNo") && (map.get("mobileNo")) != null) {
            murabahApplication.setMobileNo(map.get("mobileNo").toString());
        }
        if (map.containsKey("teleNo") && (map.get("teleNo")) != null) {
            murabahApplication.setTeleNo(map.get("teleNo").toString());
        }
        if (map.containsKey("deviceType") && (map.get("deviceType")) != null) {
            murabahApplication.setDeviceType(map.get("deviceType").toString());
        }
        if (map.containsKey("ipAddress") && (map.get("ipAddress")) != null) {
            murabahApplication.setIpAddress(map.get("ipAddress").toString());
        }
        murabahApplication.setTenor(map.get("tenor").toString());
        murabahApplication.setTradingAcc(tradingAcc);
        murabahApplication.setTradingAccExchange(map.get("tradingAccExchange").toString());
        if (map.get("isOtherBrkAvailable").toString().equalsIgnoreCase("true")) {
            murabahApplication.setOtherBrkAvailable(true);
        } else {
            murabahApplication.setOtherBrkAvailable(false);
        }
        murabahApplication.setOtherBrkNames(map.get("otherBrkNames").toString());
        murabahApplication.setOtherBrkAvgPf(map.get("otherBrkAvgPf").toString());
        if (map.containsKey("initailRAVP")) {
            murabahApplication.setInitialRAPV(Double.parseDouble(map.get("initailRAVP").toString()));
        }
        if (map.containsKey("pflist")) {
            pflist = (ArrayList<Symbol>) map.get("pflist");
        }
        murabahApplication.setPflist(pflist);
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Status status = new Status();
        status.setLevelId(1);
        status.setStatusId(OverrallApprovalStatus.PENDING.statusCode());
        status.setStatusDescription(OverrallApprovalStatus.PENDING.statusDescription());
        status.setStatusChangedDate(dateFormat.format(date));
        murabahApplication.setDate(dateFormat.format(date));
        murabahApplication.setProposalDate(dateFormat.format(date));
        murabahApplication.addNewStatus(status);
        murabahApplication.setOverallStatus(Integer.toString(OverrallApprovalStatus.PENDING.statusCode()));
        murabahApplication.setCurrentLevel(1);
        murabahApplication.setAdminFeeCharged(0.0);
        if (map.containsKey("customerReferenceNumber") && (map.get("customerReferenceNumber")) != null) {
            murabahApplication.setCustomerReferenceNumber(map.get("customerReferenceNumber").toString());
        }
        if (map.containsKey("discountOnProfit")) {
            murabahApplication.setDiscountOnProfit(Integer.parseInt(map.get("discountOnProfit").toString()));
        }
        if (map.containsKey("productType") && (map.get("productType")) != null) {
            murabahApplication.setProductType(Integer.valueOf(map.get("productType").toString()));
        }

        CommissionStructure applyingCommissionStructure = lsfCoreService.getCommissionStructureBasedOnOrderValue(
                murabahApplication.getProposedLimit());
        if (applyingCommissionStructure != null) {
            murabahApplication.setProfitPercentage(applyingCommissionStructure.getPercentageAmount());
        }
        if (map.containsKey("financeMethod") && map.get("financeMethod") != null) {
            murabahApplication.setFinanceMethod(map.get("financeMethod").toString());
        }

        String id = lsfRepository.updateMurabahApplication(murabahApplication);
        logger.info("New application ID : " + id);
        String l32ID = lsfRepository.initialAgreementStatus(
                Integer.parseInt(id),
                (int) Double.parseDouble(murabahApplication.getFinanceMethod().toString()),
                murabahApplication.getProductType(),
                1);
        murabahApplication.setId(id);
        try {
            notificationManager.sendNotification(murabahApplication);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        logger.info("===========LSF : New Murabah Application Created , Application ID :"
                    + id
                    + " , User ID:"
                    + murabahApplication.getCustomerId()
                    + " ,l32ID : "
                    + l32ID);
        String RspMsg = id + "|" + 1 + "|" + OverrallApprovalStatus.PENDING.statusCode();

        if (pflist != null) {
            if (pflist.size() > 0) {
                for (int i = 0; i < pflist.size(); i++) {
                    Map<String, Object> params = (Map<String, Object>) pflist.get(i);
                    Symbol symbol = new Symbol();
                    symbol.setSymbolCode(params.get("symbolCode").toString());
                    symbol.setExchange(params.get("exchange").toString());
                    symbol.setPreviousClosed(Double.parseDouble(params.get("previousClosed").toString()));
                    symbol.setAvailableQty((int) Double.parseDouble(params.get("availableQty").toString()));
                    symbolList.add(symbol);
                }
                for (Symbol symbol : symbolList) {
                    lsfRepository.updateInitailAppPortfolio(symbol, id);
                }
            }
        }
        cmr.setResponseCode(200);
        cmr.setResponseMessage(RspMsg);
        logger.debug("===========LSF : LSF-SERVER RESPONSE  :" + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateApplicationParameters(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        List<MurabahApplication> applicationList = null;
        int count = 0;
        if (map.containsKey("id")) {
            try {

                String applicationId = map.get("id").toString();
                applicationList = lsfRepository.getMurabahAppicationApplicationID(applicationId);
                var PurchaseOrder = lsfRepository.getAllPurchaseOrder(applicationId);
                var po = PurchaseOrder.stream()
                        .findFirst().orElse(null);
                if (po != null) {
                    if (map.containsKey("isPhysicalDelivery")) {
                        int isPhysicalDelivery = Integer.parseInt(map.get("isPhysicalDelivery").toString());
                        po.setIsPhysicalDelivery(isPhysicalDelivery);
                        po.setAuthAbicToSell(isPhysicalDelivery == 1 ? "0" : "1");
                        count ++;
                    }
                    if(map.containsKey("sellButNotSettle")) {
                        po.setSellButNotSettle(Integer.parseInt(map.get("sellButNotSettle").toString()));
                        count ++;
                    }
                }
                if (applicationList != null) {
                    if (applicationList.size() > 0) {
                        MurabahApplication fromDB = applicationList.get(0);
                        if (fromDB != null) {
                            if (map.containsKey("marginabilityGroup")) {
                                fromDB.setMarginabilityGroup(map.get("marginabilityGroup").toString());
                                count++;
                            }
                            if (map.containsKey("stockConcentrationGroup")) {
                                fromDB.setStockConcentrationGroup(map.get("stockConcentrationGroup").toString());
                                count++;
                            }
                            if (map.containsKey("proposedLimit")) {
                                fromDB.setProposedLimit(Double.parseDouble(map.get("proposedLimit").toString()));
                                count++;
                            }
                            if (map.containsKey("reviewdate")) {
                                fromDB.setReviewDate(map.get("reviewdate").toString());
                                count++;
                            }
                            if (map.containsKey("maximumNumberOfSymbols")) {
                                fromDB.setMaximumNumberOfSymbols(Integer.parseInt(map.get("maximumNumberOfSymbols")
                                                                                     .toString()));
                            }
                            if (map.containsKey("rapv")) {
                                fromDB.setInitialRAPV(Double.parseDouble(map.get("rapv").toString()));
                            }
                            if (map.containsKey("discountOnProfit")) {
                                fromDB.setDiscountOnProfit(Integer.parseInt(map.get("discountOnProfit").toString()));
                            }
                            if (map.containsKey("profitPercentage")) {
                                fromDB.setProfitPercentage(Double.valueOf(map.get("profitPercentage").toString()));
                            }
                            if (map.containsKey("automaticSettlementAllow")) {
                                fromDB.setAutomaticSettlementAllow(Integer.valueOf(map.get("automaticSettlementAllow")
                                                                                      .toString()));
                            }
                            if (map.containsKey("tenor")) {
                                fromDB.setTenor(map.get("tenor").toString());
                            }
                            if (map.containsKey("productType") && (map.get("productType")) != null) {
                                fromDB.setProductType(Integer.valueOf(map.get("productType").toString()));

                                if (fromDB.getProductType() == 3) { // if condition Profit
                                    if (map.containsKey("discountOnProfit") && (map.get("discountOnProfit") != null)) {
                                        fromDB.setDiscountOnProfit(Integer.valueOf(map.get("discountOnProfit")
                                                                                      .toString()));
                                    }
                                }
                            }

                            if (count > 0) {
                                lsfRepository.updateMurabahApplication(fromDB);
                                if(po!= null) {
                                    lsfRepository.updatePurchaseOrderByAdmin(po);
                                }
                                cmr.setResponseCode(200);
                                cmr.setResponseMessage("Application Updated");
                            } else {
                                cmr.setResponseCode(500);
                                cmr.setErrorMessage("No Records to Update");
                                cmr.setErrorCode(LsfConstants.ERROR_NO_RECORDS_TO_UPDATE);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                cmr.setResponseCode(500);
                cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setErrorMessage("No, Application ID");
            cmr.setErrorCode(LsfConstants.ERROR_NO_APPLICATION_ID);
        }
        return gson.toJson(cmr);
    }

    private String validateTradingAcc(Map<String, Object> map) {
        CommonInqueryMessage trReq = new CommonInqueryMessage();
        trReq.setReqType(LsfConstants.VALIDATING_TRADING_ACCOUNT);
        trReq.setTradingAccountId(map.get("tradingAcc").toString());
        Object resMap = helper.sendMessageToOms(gson.toJson(trReq));

        TradingAccResponse tradingAccResponse = gson.fromJson(resMap.toString(), TradingAccResponse.class);
        String marginabilityGroupId = null;
        if (map.get("appId") != null) {
            String appId = map.get("appId").toString();
            MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(appId);
            marginabilityGroupId = murabahApplication != null ? murabahApplication.getMarginabilityGroup() : null;
        }

        processSymbols(tradingAccResponse.getResponseObject(), marginabilityGroupId);
        logger.debug("===========LSF : LSF-SERVER RESPONSE validateTradingAcc :" + gson.toJson(resMap));
        return gson.toJson(tradingAccResponse);
    }


    private void processSymbols(List<Symbol> tradingAccountSymbols, String marginabilityGroupId) {
        MarginabilityGroup marginabilityGroup = null;
        List<SymbolMarginabilityPercentage> symbolMarginabilityPercentages = null;
        if (marginabilityGroupId != null) {
            marginabilityGroup = helper.getMarginabilityGroup(marginabilityGroupId);
        } else {
            marginabilityGroup = lsfRepository.getDefaultMarginGroups().getFirst();
        }
        if(marginabilityGroup != null) {
            symbolMarginabilityPercentages = marginabilityGroup.getMarginableSymbols();
        }

        if (tradingAccountSymbols != null) {
            for (Symbol symbol : tradingAccountSymbols) {

                if (marginabilityGroup != null) {
                    symbol.setMarginabilityPercentage(marginabilityGroup.getGlobalMarginablePercentage());
                }

                if(symbolMarginabilityPercentages != null) {
                    for(SymbolMarginabilityPercentage smp :symbolMarginabilityPercentages) {
                        if(smp.getSymbolCode().equals(symbol.getSymbolCode()) && smp.getExchange().equals(symbol.getExchange())){
                            symbol.setMarginabilityPercentage(smp.getMarginabilityPercentage());
                        }
                    }
                }

                symbol.setMarketValue(symbol.getAvailableQty() * Math.max(symbol.getLastTradePrice(), symbol.getPreviousClosed()));
            }
        }
    }

    private String saveQuestionerForUser(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        String userName = null;
        String ip = "";
        if (map.containsKey("securityKey")) {
            String[] response = CustomEncryption.getDecrypted(map.get("securityKey").toString()).split("\\|");
            userName = response[0];
        }
        if (map.containsKey("ipAddress")) {
            ip = map.get("ipAddress").toString();
        }
        if (map.containsKey("Q1")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 1, map.get("Q1").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 1, null, ip);
        }
        if (map.containsKey("Q2")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 2, map.get("Q2").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 2, null, ip);
        }
        if (map.containsKey("Q3")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 3, map.get("Q3").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 3, null, ip);
        }
        if (map.containsKey("Q4")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 4, map.get("Q4").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 4, null, ip);
        }
        if (map.containsKey("Q5")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 5, map.get("Q5").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 5, null, ip);
        }
        if (map.containsKey("Q6")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 6, map.get("Q6").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 6, null, ip);
        }
        if (map.containsKey("Q7")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 7, map.get("Q7").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 7, null, ip);
        }
        if (map.containsKey("Q8")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 8, map.get("Q8").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 8, null, ip);
        }
        if (map.containsKey("Q9")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 9, map.get("Q9").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 9, null, ip);
        }
        if (map.containsKey("Q10")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 10, map.get("Q10").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 10, null, ip);
        }
        if (map.containsKey("Q11")) {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 11, map.get("Q11").toString(), ip);
        } else {
            lsfRepository.saveQuestionerAnswer(Integer.parseInt(userName), 11, null, ip);
        }

        logger.info("===========LSF : Saving Questioner for UserID :" + userName);

        cmr.setResponseCode(200);
        cmr.setResponseMessage("Successfully Saved.");
        return gson.toJson(cmr);
    }

    private String rateApplication(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            ApplicationRating applicationRating = new ApplicationRating();
            applicationRating.setClientId(Long.valueOf(String.valueOf(map.get("clientId"))));
            applicationRating.setAppId(Long.valueOf(String.valueOf(map.get("appId"))));
            applicationRating.setRating(Integer.valueOf(String.valueOf(map.get("rating"))));
            applicationRating.setUpdatedDate(new Date());
            applicationRating.setUpdatedBy(String.valueOf(map.get("updatedBy")));

            lsfRepository.rateApplication(applicationRating);

            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("Done");
            logger.info("Loan application rated: " + applicationRating.getAppId());
        } catch (NumberFormatException e) {
            logger.info("Cannot rate application. Invalid message format!!!");
            commonResponse.setResponseCode(400);
        }
        return gson.toJson(commonResponse);
    }

    private String getApplicationRating(Map<String, Object> map) {
        ApplicationRating applicationRating = new ApplicationRating();
        String clientId = String.valueOf(map.get("clientId"));
        String appId = String.valueOf(map.get("appId"));
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (appId == null || appId.equals("null")) {
                applicationRating.setAppId(0);
                applicationRating.setClientId(Long.valueOf(clientId));
            } else {
                applicationRating.setAppId(Long.valueOf(appId));
                applicationRating.setClientId(0);
            }
            List<ApplicationRating> applicationRatings = lsfRepository.getApplicationRating(applicationRating);

            commonResponse.setResponseCode(200);
            commonResponse.setResponseObject(applicationRatings);
            logger.info("Loan application rated: " + applicationRating.getAppId());
        } catch (NumberFormatException e) {
            logger.info("Cannot get application rating. Invalid message format!!!");
            commonResponse.setResponseCode(400);
        }

        return gson.toJson(commonResponse);
    }

    private String whiteListApplication(Map<String, Object> map) {
        String customerId = map.get("customerId").toString();
        String appId = map.get("id").toString();
        CommonResponse response = new CommonResponse();
        try {
            lsfRepository.whiteListApplication(appId, customerId);
            response.setResponseCode(200);
            logger.info("Application White listed LSF : " + appId);
            // send req to OMS
            CommonInqueryMessage blackListRequest = new CommonInqueryMessage();
            blackListRequest.setReqType(LsfConstants.BLACK_LIST_CUSTOMER);
            blackListRequest.setCustomerId(customerId);
            blackListRequest.setChangeParameter(1);
            blackListRequest.setValue("0");
            blackListRequest.setParams("Customer need to be revert back to white Listed");
            logger.info("===========LSF : Sending White List Request to OMS:" + gson.toJson(blackListRequest));
            String omsResponse = helper.omsCommonRequests(gson.toJson(blackListRequest));
            logger.info("===========LSF : OMS Response to  White List Request :" + omsResponse);
        } catch (Exception ex) {
            response.setResponseCode(400);
            ex.printStackTrace();
            logger.info("Cannot white list Customer");
        }
        return gson.toJson(response);
    }
}
