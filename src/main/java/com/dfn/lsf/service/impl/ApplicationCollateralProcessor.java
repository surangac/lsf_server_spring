package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_APPLICATION_COLLATERAL_PROCESS;

/**
 * Defined in InMessageHandlerAdminCbr,InMessageHandlerCbr
 * Handling Message types :
 * - MESSAGE_TYPE_APPLICATION_COLLATERAL_PROCESS = 19;
 */
@Service
@MessageType(MESSAGE_TYPE_APPLICATION_COLLATERAL_PROCESS)
@RequiredArgsConstructor
public class ApplicationCollateralProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationCollateralProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;

    @Override
    public String process(String request) {
        String rawMessage = request;
        logger.debug("====LSF : ApplicationCollateralProcessor , Received Message:" + rawMessage);
        Map<String, Object> map = new HashMap<String, Object>();
        CommonResponse cmr = null;
        map = gson.fromJson(rawMessage, map.getClass());
        try {
            if (map.containsKey("subMessageType")) {
                String subMessageType = map.get("subMessageType").toString();
                switch (subMessageType) {
                    case "reqApplicationCollaterals":
                        return getPortfolioForCollaterals(map);/*---Load PF details for Collateral window---*/
                    case "reqCollateralsForPO":
                        return getColleteralsForPO(map);/*---Load Collaterals for PO window---*/
                    case "updateCollaterals":
                        return updatePortfolioCollaterals(rawMessage);/*---Submit Collaterals---*/
                    case "changeStatusCollaterals":
                        return changeStatusCollaterals(rawMessage);/*------------Approve Collaterals*/
                    case "rejectCollateralWindow":/*----Reject Application in Collaterla window by customer----*/
                        return rejectApplicationInCollateralWindow(map);
                    case "rejectPOWindow":/*----Reject Application in PO window-----*/
                        return rejectApplicationPOWindow(map);
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

    private String getPortfolioForCollaterals(Map<String, Object> map) {
        String id = map.get("id").toString();
        MApplicationCollaterals collaterals = null;
        Map<String, Symbol> symbolMap = new HashMap<>();
        List<Symbol> allSymbols = lsfRepository.loadAllSymbols();

        if (allSymbols != null && allSymbols.size() > 0) {
            for (Symbol symbol : allSymbols) {
                symbolMap.put(symbol.getExchange() + "|" + symbol.getSymbolCode(), symbol);
            }
        }

        try {
            MurabahApplication fromDBApp = lsfRepository.getMurabahApplication(id);
            if (fromDBApp != null) {
                String customerId = fromDBApp.getCustomerId();
                if(fromDBApp.isRollOverApp()) {
                    collaterals = lsfRepository.getApplicationCollateral(fromDBApp.getId());
                    if (collaterals.getId() != null) { // already have collaterals for this Rollover
                        collaterals = lsfRepository.getCollateralForRollOverCollaterelWindow(fromDBApp.getId(), collaterals);
                    }
                } else {
                    collaterals = lsfRepository.getApplicationCompleteCollateral(id);
                }

                if (collaterals.getId() == null) {
                    collaterals.setApplicationId(id);
                    collaterals.setId("-1");
                }
                /*---set Approved limit---*/
                collaterals.setApprovedLimitAmount(fromDBApp.getProposedLimit());

//                /*---get attached Marginability Group---*/
//                MarginabilityGroup marginabilityGroup = helper.getMarginabilityGroup(fromDBApp.getMarginabilityGroup());
//                List<LiquidityType> attachedLiqGoupList = null;
//                if (marginabilityGroup != null) {
//                    attachedLiqGoupList = marginabilityGroup.getMarginabilityList();
//                }


                var pfForCollateral = fromDBApp.isRollOverApp()
                                      ? helper.getLsfTypeTradingAccounts(customerId, fromDBApp.getRollOverAppId(), fromDBApp.getMarginabilityGroup())
                                      : helper.getPFDetailsNonLSF(customerId, fromDBApp.getMarginabilityGroup());
                if (!pfForCollateral.isEmpty()) {
                    var tradingAccFromOms = pfForCollateral.stream().filter(tradingAccOmsResp -> tradingAccOmsResp.getAccountId().equals(fromDBApp.getTradingAcc()))
                            .findFirst().orElse(null);
                    TradingAcc tradingAcc =  collaterals.isTradingAccountExist(tradingAccFromOms.getAccountId());
                    tradingAcc.setAccountId(tradingAccFromOms.getAccountId());
                    tradingAcc.setExchange(tradingAccFromOms.getExchange());
                    tradingAcc.setLsfType(false);
                    tradingAcc.setApplicationId(id);
                    tradingAccFromOms.getSymbolList().forEach(symbol -> {
                        Symbol symbolExt = tradingAcc.isSymbolExist(symbol.getSymbolCode(), symbol.getExchange());
                        symbolExt.mapFromOms(symbol);
                        double marginabilityPerc = helper.getSymbolMarginabilityPerc(symbol.getSymbolCode(), symbol.getExchange(), id);
                        symbolExt.setMarginabilityPercentage(marginabilityPerc);
                        if (symbolMap.containsKey(symbol.getExchange() + "|" + symbol.getSymbolCode())) {
                            symbolExt.setAllowedForCollateral(symbolMap.get(symbol.getExchange()
                                                                         + "|"
                                                                         + symbol.getSymbolCode())
                                                                    .getAllowedForCollateral());
                        }
                    });
                }

//                CommonInqueryMessage req = new CommonInqueryMessage();
//                if (GlobalParameters.getInstance().getShariaSymbolsAsCollateral()) {
//                    req.setReqType(LsfConstants.GET_PF_SYMBOLS_FOR_COLLETRALS);
//                } else {
//                    req.setReqType(LsfConstants.GET_NON_SHARIA_PF_SYMBOLS_FOR_COLLETRALS);
//                }
//                req.setCustomerId(customerId);
//
//                /*---request customer  PF Symbols for Colleterals---*/
//                Object result = helper.sendMessageToOms(gson.toJson(req));
//                Map<String, Object> resultMap = new HashMap<>();
//                resultMap = gson.fromJson((String) result, resultMap.getClass());
//                ArrayList<Map<String, Object>> accList = (ArrayList<Map<String, Object>>) resultMap.get(
//                        "responseObject");
//                for (Map<String, Object> resMap : accList) {
//                    Map<String, Object> mpTRadingAcc = (Map<String, Object>) resMap.get("tradingAccount");
//                    String tradingAccId = mpTRadingAcc.get("accountId").toString();
//                    String exchange = mpTRadingAcc.get("exchange").toString();
//
//                    /*---Display only Application Selected Trading Account as Collateral---*/
//                    if (resMap.containsKey("shariaSymbols")
//                        && (tradingAccId.equalsIgnoreCase(fromDBApp.getTradingAcc()))) {
//                        TradingAcc tradingAcc = collaterals.isTradingAccountExist(tradingAccId);
//                        tradingAcc.setExchange(exchange);
//                        tradingAcc.setApplicationId(id);
//                        tradingAcc.setLsfType(false);
//                        ArrayList<Map<String, Object>> symbolsList = (ArrayList<Map<String, Object>>) resMap.get(
//                                "shariaSymbols");
//
//                        for (Map<String, Object> symbolObj : symbolsList) {
//                            String symbolCode = symbolObj.get("symbolCode").toString();
//                            String sExchange = symbolObj.get("exchange").toString();
//                            Symbol symbol = tradingAcc.isSymbolExist(symbolCode, sExchange);
//                            if (symbolObj.containsKey("shortDescription")) {
//                                symbol.setShortDescription(symbolObj.get("shortDescription").toString());
//                            }
//                            symbol.setPreviousClosed(Double.parseDouble(symbolObj.get("previousClosed").toString()));
//                            symbol.setLastTradePrice(Double.parseDouble(symbolObj.get("lastTradePrice").toString()));
//                            int pendingSettle = 0;
//                            if (symbolObj.containsKey("pendingSettle")) {
//                                pendingSettle = Math.round(Float.parseFloat(symbolObj.get("pendingSettle").toString()));
//                            }
//                            // remove pending settle from available qty, implemented to cater T+2
//                            symbol.setAvailableQty(Math.round(Float.parseFloat(symbolObj.get("availableQty")
//                                                                                        .toString())) - pendingSettle);
//                            symbol.setMarketValue(symbol.getAvailableQty() * (symbol.getLastTradePrice() > 0
//                                                                              ? symbol.getLastTradePrice()
//                                                                              : symbol.getPreviousClosed()));
//
//                            /*---setting weather symbol is allowed for collateral---*/
//                            if (symbolMap.containsKey(symbol.getExchange() + "|" + symbol.getSymbolCode())) {
//                                symbol.setAllowedForCollateral(symbolMap.get(symbol.getExchange()
//                                                                             + "|"
//                                                                             + symbol.getSymbolCode())
//                                                                        .getAllowedForCollateral());
//                            }
//                            LiquidityType attachedToSymbolLiq = helper.existingSymbolLiqudityType(symbolCode, exchange);
//
//                            /*---set Default liqudit Type---*/
//                            symbol.setLiquidityType(attachedToSymbolLiq);
//
//                            /*---override if Applicaiton level Marginability group is attached with relevent
//                            Liquidity Type---*/
//                            if (attachedLiqGoupList != null) {
//                                for (LiquidityType liq : attachedLiqGoupList) {
//                                    if (liq.getLiquidId() == attachedToSymbolLiq.getLiquidId()) {
//                                        symbol.setLiquidityType(liq);
//                                    }
//                                }
//                            }
//
//                            // getting assigned group's marginability type for symbol
//                            double marginabilityPerc = helper.getSymbolMarginabilityPerc(symbolCode, exchange, id);
//                            symbol.setMarginabilityPercentage(marginabilityPerc);
//                        }
//                    }
//                }

                var cashAccForCollateral = fromDBApp.isRollOverApp()
                                           ? helper.getLsfTypeCashAccounts(customerId, fromDBApp.getRollOverAppId())
                                           : helper.getNonLsfTypeCashAccounts(customerId);
                if (!cashAccForCollateral.isEmpty()) {
                    CashAcc accForCollateral = cashAccForCollateral.stream().filter(cashAcc -> cashAcc.getAccountId().equals(fromDBApp.getDibAcc()))
                            .findFirst().orElse(null);
                    CashAcc cashAcc = collaterals.isCashAccExist(accForCollateral);
                    cashAcc.setCashBalance(accForCollateral.getCashBalance() - accForCollateral.getNetReceivable());
                    if(accForCollateral.getInvestmentAccountNumber() == null) {
                        cashAcc.setInvestmentAccountNumber(accForCollateral.getAccountId());
                    } else {
                        cashAcc.setInvestmentAccountNumber(accForCollateral.getInvestmentAccountNumber());
                    }
                    cashAcc.setApplicationId(id);
                    cashAcc.setLsfType(false);
                }

                /*---requesting for Cash balance for Colleterals---*/
//                result = "";
//                req.setReqType(LsfConstants.GET_NON_LSF_CASH_ACCOUNT_DETAILS);
//                result = helper.sendMessageToOms(gson.toJson(req));
//                resultMap.clear();
//                resultMap = gson.fromJson((String) result, resultMap.getClass());
//                ArrayList<Map<String, Object>> cashAccList = (ArrayList<Map<String, Object>>) resultMap.get(
//                        "responseObject");
//                for (Map<String, Object> cashAcc : cashAccList) {
//                    String cashAccId = cashAcc.get("accountNo").toString();
//
//                    /*--display only the application related Cash Account for Collateral---*/
//                    if (cashAccId.equalsIgnoreCase(fromDBApp.getDibAcc())) {
//                        String investmentAccNo = cashAcc.get("investorAccountNo").toString();
//                        double pendingSettle = Double.parseDouble(cashAcc.get("pendingSettle").toString());
//                        CashAcc cashAcc1 = collaterals.isCashAccExist(cashAccId);
//                        // remove pending settle from the cash balance for collateral for T+2 implementation
//                        double netReceivable = 0;
//                        if (cashAcc.containsKey("netReceivable")) {
//                            netReceivable = Double.parseDouble(cashAcc.get("netReceivable").toString());
//                            if (netReceivable < 0) {
//                                netReceivable = 0;
//                            }
//                        }
//                        cashAcc1.setCashBalance(Double.parseDouble(cashAcc.get("balance").toString()) - netReceivable);
//                        cashAcc1.setInvestmentAccountNumber(investmentAccNo);
//                        cashAcc1.setApplicationId(id);
//                        cashAcc1.setLsfType(false);
//                    }
//                }

                collaterals.setReadyForColleteralTransfer(true);
                //List<PurchaseOrder> purchaseOrderList = lsfRepository.getPurchaseOrderForApplication(fromDBApp.getId());
                
                if (fromDBApp.getFinanceMethod().equals("1")) {
                    //double adminFee = GlobalParameters.getInstance().getShareAdminFee();
//                    if (purchaseOrderList != null && purchaseOrderList.size() > 0) {
//                        PurchaseOrder purchaseOrder = purchaseOrderList.get(0);
//                        if (purchaseOrder.getSimaCharges() > 0 || purchaseOrder.getTransferCharges() > 0) {
//                            adminFee = purchaseOrder.getSimaCharges() + purchaseOrder.getTransferCharges();
//                        }
//                    }
                    collaterals.setAdminFee(GlobalParameters.getInstance().getShareAdminFee());
                } else {
                    collaterals.setAdminFee(GlobalParameters.getInstance().getComodityAdminFee());
                    double vatAmout = LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(collaterals.getAdminFee()));
                    if (fromDBApp.isRollOverApp()) {
                        final MApplicationCollaterals finalCollaterals = collaterals;
                        collaterals.getCashAccForColleterals().forEach(cashAcc -> {
                            cashAcc.setAmountAsColletarals(cashAcc.getCashBalance() - finalCollaterals.getAdminFee() - vatAmout);
                        });
                    }
                }
                
                double vatAmount = LSFUtils.ceilTwoDecimals(lsfCore.calculateVatAmt(collaterals.getAdminFee()));
                collaterals.setVatAmount(vatAmount);
            }
        } catch (Exception e) {
            logger.error("Error in getPortfolioForCollaterals", e);
        }
        return gson.toJson(collaterals);
    }

    private String getColleteralsForPO(Map<String, Object> map) {

        String id = map.get("id").toString();
        logger.debug("===========LSF : (reqCollateralsForPO)-REQUEST RECEIVED , Application ID :" + id);
        MApplicationCollaterals fromDBApp = null;
        try {
            MurabahApplication application = lsfRepository.getMurabahApplication(id);
            if(application.isRollOverApp()) {
                fromDBApp = lsfRepository.getApplicationCompleteCollateralForRollOver(application.getRollOverAppId(), application.getId(), false, false);
            } else {
                fromDBApp = lsfRepository.getApplicationCompleteCollateral(id);
            }

            if (fromDBApp == null) {
                fromDBApp = new MApplicationCollaterals();
            }

            fromDBApp.setApprovedLimitAmount(application.getProposedLimit());
            fromDBApp.setTenorID(Integer.parseInt(application.getTenor()));
            fromDBApp.setMaximumNumberOfSymbols(GlobalParameters.getInstance().getMaximumNumberOfSymbols());
            fromDBApp.setAllowInstalmentSettlement(GlobalParameters.getInstance().getAllowInstalmentSettlement());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        logger.debug("===========LSF : (reqCollateralsForPO)-LSF-SERVER RESPONSE  : " + gson.toJson(fromDBApp));
        return gson.toJson(fromDBApp);
    }

    private String updatePortfolioCollaterals(String jsonString) {

        logger.debug("=========LSF: updatePortfolioCollaterals:" + jsonString);

        double accountBalance = 0.0;
        double cashCollateralAmount = 0.0;
        CommonResponse cmr = new CommonResponse();
        MApplicationCollaterals collaterals = gson.fromJson(jsonString, MApplicationCollaterals.class);
        MurabahApplication application = lsfRepository.getMurabahApplication(collaterals.getApplicationId());

        if (collaterals.getCashAccForColleterals() != null && !collaterals.getCashAccForColleterals().isEmpty()) {
            for (CashAcc cashAcc : collaterals.getCashAccForColleterals()) {
                accountBalance = cashAcc.getCashBalance();
                cashCollateralAmount = cashAcc.getAmountAsColletarals();
            }
        }

        double adminFee = application.getFinanceMethod().equals("1") ? GlobalParameters.getInstance().getShareAdminFee() : GlobalParameters.getInstance().getComodityAdminFee();
        double vatAmount = lsfCore.calculateVatAmt(adminFee);
        logger.debug("====LSF : adminFee :"
                     + adminFee
                     + " , Vat Amount :"
                     + vatAmount
                     + " , account Balance :"
                     + accountBalance);
        if (accountBalance >= (adminFee + vatAmount + cashCollateralAmount)) {
            logger.debug("====LSF: Is Market Open :" + LSFUtils.isMarketOpened());
            if (LSFUtils.isMarketOpened()) {
                if (application.getCurrentLevel() != GlobalParameters.getInstance()
                                                                        .getGetAppCloseLevel()
                    && Integer.parseInt(application.getOverallStatus()) > 0) {

                    if ((collaterals.getNetTotalColleteral() / collaterals.getApprovedLimitAmount()) * 100
                        >= GlobalParameters.getInstance().getColletralToMarginPercentage()) { /*---validating  the
                                           colletralToMargin percentage---*/
                        try {
                            java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date date = new Date();
                            collaterals.setUpdatedDate(dateFormat.format(date));

                            /*-------Updating LSF Cash Account & Trading Account----------*/

                            logger.debug("===========LSF : Updating Collaterals");
                            String appId = application.isRollOverApp() ? application.getRollOverAppId() : application.getId();
                            if (collaterals.getLsfTypeTradingAccounts() != null
                                && !collaterals.getLsfTypeCashAccounts().isEmpty()) {
                                logger.debug("===========LSF : Updating Cash Account :");
                                CashAcc lsfCashAccount = collaterals.getLsfTypeCashAccounts().getFirst();
                                CashAcc updatedLsfCashAccount = lsfCore.getLsfTypeCashAccountForUser(
                                        application.getCustomerId(),
                                        appId);
                                lsfCashAccount.setAccountId(updatedLsfCashAccount.getAccountId());
                                logger.debug("===========LSF : Updated Cash Account :"
                                             + updatedLsfCashAccount.getAccountId());
                            }
                            if (collaterals.getLsfTypeTradingAccounts() != null
                                && !collaterals.getLsfTypeTradingAccounts().isEmpty()) {
                                logger.debug("===========LSF : Updating Trading Account :");
                                TradingAcc lsfTradingAccount = collaterals.getLsfTypeTradingAccounts().getFirst();
                                TradingAcc updatedLsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(
                                        application.getCustomerId(),
                                        appId);
                                lsfTradingAccount.setAccountId(updatedLsfTradingAccount.getAccountId());
                                logger.debug("===========LSF : Updated Trading Account :"
                                             + updatedLsfTradingAccount.getAccountId());
                            }

                            /*-----------------*/

                            /*---Blocking Collaterals---*/
                            logger.debug("===LSF :About to block Collaterals");
                            CommonResponse blockResponse = (CommonResponse) lsfCore.blockCollaterals(
                                    collaterals,
                                    application);
                            if (blockResponse.getResponseCode() == 200) {
                                lsfRepository.addEditCompleteCollateral(collaterals);
                            } else {
                                cmr.setResponseCode(500);
                                cmr.setResponseMessage("Error While Blocking Collaterals");
                                cmr.setErrorMessage(blockResponse.getErrorMessage());
                                return gson.toJson(cmr);
                            }

                            if (collaterals.getExternalCollaterals() != null) {
                                if (collaterals.getExternalCollaterals().size() > 0) {
                                    lsfCore.addExternalCollaterals(
                                            collaterals.getId(),
                                            collaterals.getExternalCollaterals());
                                }
                            }

                            /*---- For ABIC approve the purchase Order Automatically. ---*/
                            CommonResponse approvalResponse = null;
                            // remove this condition as it is not required as we are not using ABIC
                            //if (LSFUtils.getConfiguration("client").equalsIgnoreCase("ABIC")) {
                                Map<String, Object> requestMap = new HashMap<>();
                                requestMap.put("status", 1);
                                requestMap.put("applicationId", collaterals.getApplicationId());
                                logger.debug("===========LSF : Auto Approving Collaterals colID : "
                                             + collaterals.getId()
                                             + ", ApplicationID:"
                                             + collaterals.getApplicationId());
                                approvalResponse = (CommonResponse) changeStatusCollateralsABIC(requestMap);
                                logger.debug("===========LSF : Auto Approving Collaterals , Status:"
                                             + approvalResponse.getResponseCode());
                                logger.debug("===========LSF : Auto Running Revaluation Process : , ApplicationID:"
                                             + collaterals.getApplicationId());
                                logger.debug("===========LSF : Auto Running Revaluation Process : , Status"
                                             + lsfCore.initialValuation(collaterals.getApplicationId()));
                           // }
                            /*----------------*/
                            application = lsfRepository.getMurabahApplication(collaterals.getApplicationId());
                            String respMessage = Integer.toString(application.getCurrentLevel())
                                                 + "|"
                                                 + application.getOverallStatus();
                            cmr.setResponseCode(approvalResponse.getResponseCode());
                            cmr.setResponseMessage(respMessage);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            cmr.setResponseCode(500);
                            cmr.setErrorMessage("error");
                        }
                    } else {
                        cmr.setResponseCode(500);
                        cmr.setResponseMessage("Net Collatral Value should be grater than to approvedLimit*"
                                               + GlobalParameters.getInstance().getColletralToMarginPercentage());
                        cmr.setErrorMessage("Net Collatral Value should be grater than Equal to (Approved Limit*"
                                            + GlobalParameters.getInstance().getColletralToMarginPercentage()
                                            + ")");
                        cmr.setErrorCode(LsfConstants.ERROR_NET_COLLATRAL_VALUE_SHOULD_BE_GREATER_THAN_CONFIGURED_PERCENTAGE);
                        List<String> parameters = new ArrayList<>();
                        parameters.add(String.valueOf(GlobalParameters.getInstance().getColletralToMarginPercentage()));
                        cmr.setParameterList(parameters);
                    }
                } else {
                    cmr.setResponseCode(500);
                    cmr.setErrorMessage("Invalid Details");
                    cmr.setErrorCode(LsfConstants.ERROR_INVALID_DETAILS);
                    cmr.setResponseMessage("Invalid Details");
                }
            } else {
                cmr.setResponseCode(500);
                cmr.setResponseMessage("Collateral cann't submit due to Market Close State");
                cmr.setErrorMessage("Collateral cann't submit due to Market Close State");
                cmr.setErrorCode(LsfConstants.ERROR_COLLATRAL_CANNOT_SUBMIT_DUE_TO_MARKET_CLOSED_STATE);
            }
        } else {
            cmr.setResponseCode(500);
            cmr.setResponseMessage("Cash Collateral Amount Should be less than or equal to :" + (accountBalance - (adminFee
                                                                                                                   + vatAmount)));
            cmr.setErrorMessage("Cash Collateral Amount Should be less than or equal to :" + (accountBalance - (adminFee
                                                                                                                + vatAmount)));
            cmr.setErrorCode(LsfConstants.ERROR_CASH_AMOUNT_VALIDATION_FAILED);
        }
        return gson.toJson(cmr);
    }

    private String changeStatusCollaterals(String jsonString) {

        CommonResponse cmr = new CommonResponse();
        try {
            MApplicationCollaterals collaterals = gson.fromJson(jsonString, MApplicationCollaterals.class);

            List<ExternalCollaterals> externalCollateralsList = collaterals.getExternalCollaterals();
            int appStatus = collaterals.getStatus();
            String statusChangedBy = collaterals.getStatusChangedBy();
            String ipAddress = collaterals.getIpAddress();
            String statusMessage = collaterals.getStatusMessage();
            collaterals = lsfRepository.getApplicationCompleteCollateral(collaterals.getApplicationId());
            collaterals.setStatus(appStatus);
            collaterals.setStatusChangedBy(statusChangedBy);
            collaterals.setIpAddress(ipAddress);
            collaterals.setStatusMessage(statusMessage);

            if (appStatus == -1) {
                cmr = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                if (cmr.getResponseCode() == 200) {
                    lsfRepository.changeStatusCollateral(collaterals);
                }
            } else {
                double externalCollateralsValue = 0.0;

                if (externalCollateralsList != null && externalCollateralsList.size() > 0) {
                    for (ExternalCollaterals externalCollaterals : externalCollateralsList) {
                        lsfRepository.updateExternalCollaterals(externalCollaterals);
                        externalCollateralsValue = externalCollateralsValue + externalCollaterals.getApplicableAmount();
                    }
                }

                lsfRepository.changeStatusCollateral(collaterals);
                collaterals.setTotalExternalColleteral(externalCollateralsValue);
                lsfRepository.addEditCollaterals(collaterals);
            }

            MurabahApplication application = lsfRepository.getMurabahApplication(collaterals.getApplicationId());
            notificationManager.sendNotification(application);
            cmr.setResponseCode(200);
            cmr.setResponseMessage("Collateral updated");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("error in Collateral updating");
            cmr.setErrorCode(LsfConstants.ERROR_ERROR_IN_COLLATERAL_UPDATING);
        }
        return gson.toJson(cmr);
    }

    private Object changeStatusCollateralsABIC(Map<String, Object> map) {
        CommonResponse cmr = new CommonResponse();
        MApplicationCollaterals collaterals = null;
        int appStatus = Integer.parseInt(map.get("status").toString());
        String statusChangedBy = "SYSTEM";
        String ipAddress = "127.0.0.1";
        String statusMessage = "AUTOMATIC APPROVED";
        String applicationId = map.get("applicationId").toString();
        try {
            collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
            collaterals.setStatus(appStatus);
            collaterals.setStatusChangedBy(statusChangedBy);
            collaterals.setIpAddress(ipAddress);
            collaterals.setStatusMessage(statusMessage);
            if (collaterals != null) {
                if (appStatus == -1) {
                    cmr = (CommonResponse) lsfCore.releaseCollaterals(collaterals);
                    if (cmr.getResponseCode() == 200) {
                        lsfRepository.changeStatusCollateral(collaterals);
                    }
                } else { /*---As this is an automatic process will go to only else part---*/
                    lsfRepository.changeStatusCollateral(collaterals);
                    lsfRepository.addEditCollaterals(collaterals);
                    lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_COLLATERLS_SUBMITTED);
                }
            }

            MurabahApplication application = lsfRepository.getMurabahApplication(collaterals.getApplicationId());
            notificationManager.sendNotification(application);/*---Sending Notification---*/

            cmr.setResponseCode(200);
            cmr.setResponseMessage("Collateral updated");
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("error in Collateral updating");
            cmr.setErrorCode(LsfConstants.ERROR_ERROR_IN_COLLATERAL_UPDATING);
            lsfRepository.updateActivity(applicationId, LsfConstants.STATUS_COLLATERLS_SUBMISSION_FAILED);
        }
        return cmr;
    }

    private String rejectApplicationInCollateralWindow(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        logger.debug("=========LSF: rejectCollateralWindow:" + map.get("applicationId").toString());
        lsfRepository.closeApplication(map.get("applicationId").toString());
        commonResponse.setResponseCode(200);
        commonResponse.setResponseMessage("Application Closed.");
        return gson.toJson(commonResponse);
    }

    private String rejectApplicationPOWindow(Map<String, Object> map) {
        CommonResponse commonResponse = new CommonResponse();
        String applicationId = map.get("applicationId").toString();
        logger.debug("=========LSF: rejectPOWindow:" + applicationId);
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
        MurabahApplication application = lsfRepository.getMurabahApplication(applicationId);
        collaterals.setStatus(-1);
        collaterals.setStatusChangedBy(application.getCustomerId());
        collaterals.setIpAddress(map.get("ipAddress").toString());
        collaterals.setStatusMessage("Customer Rejected.");
        lsfCore.releaseCollaterals(collaterals);
        lsfRepository.changeStatusCollateral(collaterals);
        commonResponse.setResponseCode(200);
        commonResponse.setResponseMessage("Application Rejected.");
        return gson.toJson(commonResponse);
    }
}
