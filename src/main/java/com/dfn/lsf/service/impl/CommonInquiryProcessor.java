package com.dfn.lsf.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dfn.lsf.util.*;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.Agreement;
import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.MurabahApplicationListResponse;
import com.dfn.lsf.model.MurabahaProduct;
import com.dfn.lsf.model.PhysicalDeliverOrder;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.Status;
import com.dfn.lsf.model.StockConcentrationRptData;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.responseMsg.CommissionDetail;
import com.dfn.lsf.model.responseMsg.FTVInfo;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_COMMON_INQUIRY_PROCESS;

@Service
@MessageType(MESSAGE_TYPE_COMMON_INQUIRY_PROCESS)
@Slf4j
@RequiredArgsConstructor
public class CommonInquiryProcessor implements MessageProcessor {
    
    private final LSFRepository lsfRepository;
    private final Gson gson;
    private final LsfCoreService lsfCore;
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(String request) {
        try {
            auditLogProcessor.process(request);
            Map<String, Object> requestMap = gson.fromJson(request, Map.class);
            String subMessageType = (String) requestMap.get("subMessageType");
            
            log.info("Processing common inquiry request with subMessageType: {}", subMessageType);
            // Handle different sub-message types
            switch (subMessageType) {
                case LsfConstants.REQ_DETAILED_FTV_LIST:/*-----------Get All Detailed FTV List - Admin-----------*/
                    return getDetailedFTVList(requestMap);
                case LsfConstants.GET_APPLICATIONS_FTV:
                    return getFTVList();
                case LsfConstants.REQ_APPROVED_PURCHASE_ORDERS:
                    return getApprovedPurchaseOrders(requestMap);
                case LsfConstants.REQ_GET_BLACKLISTED_APPLICATION:
                    return getBlackListedApplications();
                case LsfConstants.REQ_CONVERT_NUMBER_TO_STRING:
                    return convertNumberToArabic(requestMap);
                case LsfConstants.REQ_CONCENTRATION_RPT_DATA:
                    return getStockConcentrationRPT(requestMap);
                case LsfConstants.REQ_MURABAHA_PRODUCTS:
                    return getMurabahaProductsList();
                case LsfConstants.UPDATE_MURABAHA_PRODUCTS: 
                    return updateMurabahaProduct(requestMap);
                case  LsfConstants.CHANGE_MURABAHA_PRODUCTS_STATUS:
                    return changeMurabahaProductStatus(requestMap);
                case  LsfConstants.REQ_GET_MURABAHA_PRODUCT:
                    return getMurabahaProduct(requestMap);
                case LsfConstants.GET_PHYSICAL_DELIVER_LIST:
                    return getPhysicalDelivery();
                case LsfConstants.CHANGE_PO_STATUS:
                    return changePOStatus(requestMap);
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error processing common inquiry request", e);
            CommonResponse cmr = new CommonResponse();
            cmr.setResponseCode(500);
            cmr.setErrorMessage(ErrorCodes.ERROR_EXCEPTION.errorDescription());
            return gson.toJson(cmr);
        }
    }

    public String getDetailedFTVList(Map<String, Object> paraMap) {
        String fromDate = "01012017";
        String toDate = "31122050";
        int settleStatus = 1;   //-1-all,1-unsettled,0-settled

        if (paraMap.containsKey("fromDate"))
            fromDate = paraMap.get("fromDate").toString();
        if (paraMap.containsKey("toDate"))
            toDate = paraMap.get("toDate").toString();
        if (paraMap.containsKey("settlementStatus"))
            settleStatus = Integer.parseInt(paraMap.get("settlementStatus").toString());
        List<CommissionDetail> commissionDetailList = lsfRepository.getCommissionDetails(toDate);
        log.info("===========LSF : (getDetailedFTVList)-REQUEST Search params fromdate:" + fromDate + ", toDate:" + toDate + ", settlementStatus:" + settleStatus);
        List<FTVInfo> ftvInfoList = lsfRepository.getDetailedFTVList(fromDate, toDate, settleStatus);
        if (ftvInfoList != null) {

            for (final FTVInfo ftvInfo : ftvInfoList) {
                calculateWithdrawAmount(ftvInfo);
            }

            for (final CommissionDetail commissionDetail : commissionDetailList) {
                for (final FTVInfo ftvInfo : ftvInfoList) {
                    if (commissionDetail.getTradingAccId().equals(ftvInfo.getTradingAcc())) {
                        ftvInfo.setCommission(Double.parseDouble(commissionDetail.getCommission()));
                        ftvInfo.setCommissionPreviousDay(Double.parseDouble(commissionDetail.getPreviousDayCommission()));
                    }
                }
            }

        }
        return gson.toJson(ftvInfoList);
    }

    private String getFTVList() {
        log.debug("===========LSF : (applicationsFTV)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        try {
            String ftvList = "";
            List<MApplicationCollaterals> list = lsfRepository.getApplicationCollateralFtvList();
            DecimalFormat formater = new DecimalFormat("#0.00");
            if (list != null) {
                for (MApplicationCollaterals col : list) {
                    if (!ftvList.equalsIgnoreCase("")) {
                        ftvList = ftvList + ",";
                    }
                    ftvList = ftvList + formater.format(col.getFtv());
                }
            }
            cmr.setResponseCode(200);
            cmr.setResponseObject(ftvList);
        } catch (Exception e) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(e.getMessage());
        }
        log.debug("===========LSF : (applicationsFTV)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    public String getApprovedPurchaseOrders(Map<String, Object> returnMap) {
        MurabahApplicationListResponse response = new MurabahApplicationListResponse();
        log.debug("===========LSF : (reqApprovedPurchaseOrders)-REQUEST , Request Params:" + gson.toJson(returnMap));
        String fromDate = "";
        String toDate = "";
        String corellationID = "";
        if (returnMap.containsKey("corellationID")) {
            corellationID = returnMap.get("corellationID").toString();
        }
        if (returnMap.containsKey("fromDate")) {
            fromDate = returnMap.get("fromDate").toString();
        }
        if (returnMap.containsKey("toDate")) {
            toDate = returnMap.get("toDate").toString();
        }
        List<MurabahApplication> murabahApplications = lsfRepository.getApprovedPurchaseOrderApplicationList(convertToSQLDate(fromDate), convertToSQLDate(toDate));
        for (MurabahApplication murabahApplication : murabahApplications) {
            List<Status> appStatusList = lsfRepository.getApplicationStatus(murabahApplication.getId());
            murabahApplication.setAppStatus(appStatusList);
        }
        log.info("===========LSF : (reqApprovedPurchaseOrders)LSF-SERVER RESPONSE  :" + gson.toJson(murabahApplications) + " , CorrelationID:" + corellationID);
        response.setApplicationList(murabahApplications);
        return gson.toJson(response);
    }

    public String getBlackListedApplications() {
        log.debug("===========LSF : (reqGetBlackListedApplication)-REQUEST ");
        List<MurabahApplication> murabahApplications = lsfRepository.getBlackListedApplications();
        if (murabahApplications != null) {
            for (MurabahApplication murabahApplication : murabahApplications) {
                List<Status> appStatusList = lsfRepository.getApplicationStatus(murabahApplication.getId());
                murabahApplication.setAppStatus(appStatusList);
                murabahApplication.setDisplayApplicationId(murabahApplication.getDisplayApplicationId());
            }
        }

        log.info("===========LSF : (reqGetBlackListedApplication)LSF-SERVER RESPONSE  :" + gson.toJson(murabahApplications));
        return gson.toJson(murabahApplications);
    }

    private Date convertToSQLDate(String date) {
        java.util.Date d = null;
        java.sql.Date sqlDate = null;
        try {
            d = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(date);
            sqlDate = new java.sql.Date(d.getTime());
        } catch (ParseException e) {
            log.error("Error converting date to SQL date: {}", e.getMessage());
        }
        return sqlDate;


    }

    private String convertNumberToArabic(Map<String, Object> returnMap) {
        if (returnMap.containsKey("number")) {
            String convertedString = "";
            BigDecimal decimalValue = new BigDecimal(returnMap.get("number").toString());
            if (returnMap.containsKey("lan")) {
                String language = returnMap.get("lan").toString();
                if (language.equalsIgnoreCase("AR")) {
                    convertedString = NumberToArabic.convertToArabic(decimalValue, "SAR");
                } else if (language.equalsIgnoreCase("EN")) {
                    convertedString = NumberToArabic.convertToEnglish(decimalValue, "SAR");
                    convertedString = UnicodeUtils.getUnicodeString(convertedString);
                }
            }

            return convertedString;
        } else {
            return null;
        }
    }

    private void calculateWithdrawAmount(FTVInfo ftvInfo) {
        MApplicationCollaterals applicationCollaterals = lsfRepository.getApplicationCollateral(ftvInfo.getApplicationID());
        double unsettledAmount = applicationCollaterals.getOutstandingAmount();
        double netTotalCollateral = applicationCollaterals.getNetTotalColleteral();
        double firstMarginLevel = GlobalParameters.getInstance().getFirstMarginCall();
        double acceptableMaximumCollateralValue = unsettledAmount / firstMarginLevel;
        double maximumWithdrawAmount = netTotalCollateral - acceptableMaximumCollateralValue;
        ftvInfo.setMaximumWithdrawAmount(maximumWithdrawAmount);
        CashAcc lsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(ftvInfo.getCustomerID(), ftvInfo.getApplicationID());
        if (lsfCashAccount != null) {
            ftvInfo.setAvailableCashBalance(lsfCashAccount.getCashBalance());
        }
    }

    public String getStockConcentrationRPT(Map<String, Object> paraMap) {

        StockConcentrationRptData stockConcentrationRptData = new StockConcentrationRptData();
        List<Map<String, Object>> cashDtlList;
        if (paraMap.get("isSnapshot").equals("1")) {
            cashDtlList = lsfRepository.getCashAccDataForConcentrationToday();
        } else {
            cashDtlList = lsfRepository.getCashAccDataForConcentration((String) paraMap.get("todate"));
        }
        if (cashDtlList != null && !cashDtlList.isEmpty()) {
            log.debug("getStockConcentrationRPT" + cashDtlList.toString());
            Map<String, Object> valueMap = cashDtlList.get(0);
            stockConcentrationRptData.setTotalBuyingPower(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_BUYING_POWER").toString())));
            stockConcentrationRptData.setTotalReceibableCash(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_NET_RECEIVABLE").toString())));
            stockConcentrationRptData.setTotalPayableCash(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_PENDING_SETTLE").toString())));
            double cashBalance = stockConcentrationRptData.getTotalBuyingPower() - stockConcentrationRptData.getTotalReceibableCash() + stockConcentrationRptData.getTotalPayableCash();
            stockConcentrationRptData.setCashBalance(LSFUtils.ceilTwoDecimals(cashBalance));
        }
        List<Map<String, Object>> objSymbolList;
        if (paraMap.get("isSnapshot").equals("1")) {
            objSymbolList = lsfRepository.getStockDataForConcentrationToday();
        } else {
            objSymbolList = lsfRepository.getStockDataForConcentration((String) paraMap.get("todate"));
        }
        double totalPfValue = 0;
        List<Symbol> symbolList = new ArrayList<>();
        if (objSymbolList != null && !objSymbolList.isEmpty()) {
            for (Map<String, Object> symbol : objSymbolList) {
                Symbol objSymbol = new Symbol();
                objSymbol.setSymbolCode(symbol.get("S01_SYMBOL").toString());
                objSymbol.setShortDescription(symbol.containsKey("SHORT_DESCRIPTION") ? symbol.get("SHORT_DESCRIPTION").toString(): "");
                objSymbol.setAvailableQty(Integer.parseInt(symbol.getOrDefault("NET_HOLDINGS", 0).toString()));
                objSymbol.setPreviousClosed(LSFUtils.ceilTwoDecimals(Double.parseDouble(symbol.getOrDefault("PREVIOUSE_CLOSED", 0).toString())));
                objSymbol.setMarketValue(LSFUtils.ceilTwoDecimals(Double.parseDouble(symbol.getOrDefault("L08_MARKET_VALUE", 0).toString())));
                totalPfValue = totalPfValue + objSymbol.getMarketValue();
                symbolList.add(objSymbol);
            }
        }
        stockConcentrationRptData.setTotalAsset(stockConcentrationRptData.getTotalBuyingPower() + totalPfValue);
        stockConcentrationRptData.setTotalAssetPercentage(100);
        stockConcentrationRptData.setBuyingPwerPercentage(LSFUtils.ceilTwoDecimals((stockConcentrationRptData.getTotalBuyingPower() / stockConcentrationRptData.getTotalAsset()) * 100));
        for (Symbol smb : symbolList) {
            smb.setPercentage(LSFUtils.ceilTwoDecimals((smb.getMarketValue() / stockConcentrationRptData.getTotalAsset()) * 100));
        }
        stockConcentrationRptData.setConcentrationSymbolList(symbolList);
        return gson.toJson(stockConcentrationRptData);
    }

    public String getStockConcentrationRPTForDate(Map<String, Object> paramMap) {
        StockConcentrationRptData stockConcentrationRptData = new StockConcentrationRptData();
        List<Map<String, Object>> cashDtlList = lsfRepository.getCashAccDataForConcentrationToday();
        if (cashDtlList != null && !cashDtlList.isEmpty()) {
            Map<String, Object> valueMap = cashDtlList.get(0);
            stockConcentrationRptData.setTotalBuyingPower(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_BUYING_POWER").toString())));
            stockConcentrationRptData.setTotalReceibableCash(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_NET_RECEIVABLE").toString())));
            stockConcentrationRptData.setTotalPayableCash(LSFUtils.ceilTwoDecimals(Double.parseDouble(valueMap.get("TOTAL_PENDING_SETTLE").toString())));
        }
        return gson.toJson(stockConcentrationRptData);
    }

    private String getMurabahaProductsList() {
        log.debug("===========LSF : (getMurabahaProductsList)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        try {
            List<MurabahaProduct> productsList = lsfRepository.getMurabahaProducts();
            List<Agreement> agreementsList = lsfRepository.getAgreements();

            for (MurabahaProduct product : productsList) {
                product.setStatus(product.getStatus());
                List<Agreement> agreements = agreementsList.stream().filter(agreement -> agreement.getProductType() == product.getProductType()).collect(Collectors.toList());
                product.setAgreement(agreements);
            }

            cmr.setResponseCode(200);
            cmr.setResponseObject(productsList);
        } catch (Exception e) {
            cmr.setResponseCode(500);
            cmr.setErrorMessage(e.getMessage());
        }
        log.debug("===========LSF : (getMurabahaProductsList)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String updateMurabahaProduct(Map<String, Object> paraMap) {
        log.debug("===========LSF : (updateMurabahaProduct)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        ArrayList<Agreement> agreementArrayList = (ArrayList<Agreement>) paraMap.get("agreement");
        String productString =  paraMap.get("product").toString();
        List<Agreement> agreementList = new ArrayList<>();
        try {
            MurabahaProduct murabahaProduct = gson.fromJson(productString, MurabahaProduct.class);
            String key = lsfRepository.updateMurabahaProduct(murabahaProduct);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
            log.info("AgreementList length : "+ agreementArrayList.size());
            Agreement agreement = null;
            if (agreementArrayList != null && !agreementArrayList.isEmpty()) {
                String ip = paraMap.get("ipAddress").toString();
                    if (agreementArrayList.size() > 0) {
                        for (int i = 0; i < agreementArrayList.size(); i++) {
                            Object item = agreementArrayList.get(i);
                            Map<String, Object> params = (Map<String, Object>) item;
                            agreement = new Agreement();
                            agreement.setAgreementType((int) Double.parseDouble(params.get("agreementType").toString()));
                            agreement.setProductType((int) Double.parseDouble(params.get("productType").toString()));
                            agreement.setFilePath(params.get("filePath").toString());
                            agreement.setVersion((int) Double.parseDouble(params.get("version").toString()));
                            agreement.setFileExtension(params.get("fileExtension").toString());
                            agreement.setFinanceMethod((int) Double.parseDouble(params.get("financeMethod").toString()));
                            agreement.setFileName(params.get("fileName").toString());
                            agreementList.add(agreement);
                        }
                        for (Agreement agreementObj : agreementList) {
                            String m11Key = lsfRepository.updateAgreementByAdmin(agreementObj, ip, "", "");
                            log.info("updateAgreementByAdmin ID : " + m11Key);
                            if (m11Key.equalsIgnoreCase("-1")) {
                                cmr.setResponseCode(500);
                                cmr.setErrorMessage("Error While Saving Data for Agreement uploading");
                                log.error("Error While Saving Data for Agreement uploading");
                            }
                        }
                }

            }
        } catch (Exception ex) {
            log.error("Error While Saving Data updateMurabahaProduct :"+ex.getMessage());
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error While Saving Data");
        }
        log.debug("===========LSF : (updateMurabahaProduct)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String getMurabahaProduct(Map<String, Object> paraMap){
        log.debug("===========LSF : (getMurabahaProduct)-REQUEST ");
        int productType = Integer.parseInt((String) paraMap.get("productType"));
        CommonResponse cmr = new CommonResponse();
        cmr.setResponseCode(200);
        cmr.setResponseObject(lsfRepository.getMurabahaProduct(productType));
        log.debug("===========LSF : (getMurabahaProduct)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }

    private String changeMurabahaProductStatus(Map<String, Object> paraMap){
        log.debug("===========LSF : (changeProductStatus)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        String productString = (String) paraMap.get("product");
        try {
            MurabahaProduct murabahaProduct = gson.fromJson(productString, MurabahaProduct.class);
            log.debug("===========LSF : (changeProductStatus)-REQUEST product Type " + murabahaProduct.getProductType() + " Status " + murabahaProduct.getStatus() );
            String key = lsfRepository.changeMurabahaProductStatus(murabahaProduct);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
           log.error("Error While Updating the Murabaha Product Status " + ex.getMessage());
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error While Updating the Murabaha Product Status");
        }
        log.debug("===========LSF : (changeProductStatus)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
    private String getPhysicalDelivery() {
        log.debug("===========LSF : (getPhysicalDelivery)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        try {
            List<PhysicalDeliverOrder> applicationList = lsfRepository.getPhysicalDeliveryFromDB();

            cmr.setResponseCode(200);
            cmr.setResponseObject(applicationList);
        } catch (Exception e) { 
            cmr.setResponseCode(500);
            cmr.setErrorMessage(e.getMessage());
        }
        log.debug("===========LSF : (getPhysicalDelivery)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
    private String changePOStatus(Map<String, Object> paraMap){
        log.debug("===========LSF : (changePOStatus)-REQUEST ");
        CommonResponse cmr = new CommonResponse();
        PurchaseOrder po = lsfRepository.getSinglePurchaseOrder(paraMap.get("poId").toString());
        int apprvlStatus = (int) Double.parseDouble(paraMap.get("status").toString());
        po.setApprovalStatus(apprvlStatus);
        try {
            log.debug("===========LSF : (changePOStatus)-REQUEST PO ID : " + po.getId() + " Status " + po.getApprovalStatus() );
            String key = lsfRepository.approveRejectPOCommodity(po);
            cmr.setResponseCode(200);
            cmr.setResponseMessage(key);
        } catch (Exception ex) {
            log.error("Error While Updating the Purchase Order Status " + ex.getMessage());
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error While Updating the Purchase Order Status");
        }
        log.debug("===========LSF : (changePOStatus)-LSF-SERVER RESPONSE  : " + gson.toJson(cmr));
        return gson.toJson(cmr);
    }
}