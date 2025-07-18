package com.dfn.lsf.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CashAcc;
import com.dfn.lsf.model.CashAccountResponse;
import com.dfn.lsf.model.CommissionStructure;
import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.ExternalCollaterals;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.LiquidityType;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MarginabilityGroup;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.OMSCommission;
import com.dfn.lsf.model.OrderProfit;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.Symbol;
import com.dfn.lsf.model.SymbolMarginabilityPercentage;
import com.dfn.lsf.model.Tenor;
import com.dfn.lsf.model.TradingAcc;
import com.dfn.lsf.model.requestMsg.CashTransferRequest;
import com.dfn.lsf.model.requestMsg.CloseLSFAccountRequest;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.model.requestMsg.ShareTransferRequest;
import com.dfn.lsf.model.responseMsg.AccountDeletionRequestState;
import com.dfn.lsf.model.responseMsg.ProfitResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service    
@Slf4j
@RequiredArgsConstructor
public class LsfCoreService {

    private final LSFRepository lsfRepository;
    private final Gson gson;
    private final Helper helper;
    private final NotificationManager notificationManager;
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Map<String, Map<String, Object>> orderContractSignedMap = new HashMap<>();

    public MApplicationCollaterals getApplicationCollateral(String applicationId) {
        try {
            return lsfRepository.getApplicationCompleteCollateral(applicationId);
        } catch (Exception ex) {
            log.error("Error getting application collateral: {}", ex.getMessage());
            return null;
        }
    }

    public CommonResponse transferCollaterals(String applicationId) {
        MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
        return transferCollaterals(collaterals);
    }

    public CommonResponse transferCollaterals(MApplicationCollaterals collaterals) {
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (collaterals != null) {
                // initiating Share Transfer process
                for (TradingAcc fromAcc : collaterals.getTradingAccForColleterals()) {
                    TradingAcc toAccount = findTradingAccForTransfer(fromAcc, collaterals.getLsfTypeTradingAccounts());
                    if (toAccount != null) {
                        for (Symbol symbol : fromAcc.getSymbolsForColleteral()) {
                            if (symbol.getTransStatus() != LsfConstants.SHARE_TRANSFERED_STATUS) {
                                ShareTransferRequest tranferObject = new ShareTransferRequest();
                                tranferObject.setReqType(LsfConstants.SHARE_TRANSFER);
                                tranferObject.setApplicationid(collaterals.getApplicationId());
                                tranferObject.setId(Long.toString(System.currentTimeMillis()));
                                tranferObject.setFromTradingAccountId(fromAcc.getAccountId());
                                tranferObject.setFromExchange(fromAcc.getExchange());
                                tranferObject.setToTradingAccountId(toAccount.getAccountId());
                                tranferObject.setToExchange(toAccount.getExchange());

                                tranferObject.setSymbol(symbol.getSymbolCode());
                                tranferObject.setExchange(symbol.getExchange());
                                int transferedQty = symbol.getColleteralQty();
                                tranferObject.setQuantity(transferedQty);
                                CommonResponse cmr = performAction(tranferObject);
                                if (cmr.getResponseCode() == 1) {
                                    lsfRepository.updateSymbolTransferState(fromAcc.getAccountId(), collaterals.getApplicationId(), symbol.getSymbolCode(), LsfConstants.SHARE_TRANSFERED_STATUS);
                                    symbol.setTransferedQty(symbol.getTransferedQty() + transferedQty);
                                    symbol.setColleteralQty(0);
                                    symbol.setTransStatus(LsfConstants.SHARE_TRANSFERED_STATUS);
                                } else {
                                    commonResponse.setResponseCode(500);
                                    return commonResponse;
                                }
                            }

                        }
                    }
                }
                // initiating cash Transfer process
                for (CashAcc cashAcc : collaterals.getCashAccForColleterals()) {
                    log.debug("===========LSF : Cash Collateral Transferring , amount:" + cashAcc.getAmountAsColletarals());
                    if (cashAcc.getTransStatus() != LsfConstants.SHARE_TRANSFERED_STATUS) {
                        if (cashAcc.getAmountAsColletarals() > 0) {
                            BigDecimal bigDecimal = BigDecimal.valueOf(cashAcc.getAmountAsColletarals());
                            bigDecimal.setScale(2);

                            CashTransferRequest cashTransferRequest = new CashTransferRequest();
                            cashTransferRequest.setReqType(LsfConstants.CASH_TRANSFER);
                            cashTransferRequest.setId(Long.toString(System.currentTimeMillis()));
                            cashTransferRequest.setApplicationid(collaterals.getApplicationId());
                            cashTransferRequest.setFromCashAccountId(cashAcc.getAccountId());
                            cashTransferRequest.setAmount(bigDecimal.doubleValue());
                            //   cashAcc.setAmountTransfered(cashAcc.getAmountTransfered() + cashAcc.getAmountAsColletarals());
                            cashAcc.setAmountTransfered(/*cashAcc.getAmountTransfered() +*/ cashAcc.getAmountAsColletarals());
                            cashTransferRequest.setToCashAccountId(collaterals.getLsfTypeCashAccounts().get(0).getAccountId());
                            CommonResponse cmr = performAction(cashTransferRequest);
                            log.debug("===========LSF : Performed Cash Transfer , OMS Response:" + gson.toJson(cmr).toString());
                            if (cmr.getResponseCode() == 1) {
                                cashAcc.setAmountAsColletarals(0);
                                cashAcc.setTransStatus(LsfConstants.SHARE_TRANSFERED_STATUS);
                            } else {
                                commonResponse.setResponseCode(500);
                                return commonResponse;
                            }
                        }
                    }
                }
                // save whole collateral collection
                lsfRepository.addEditCompleteCollateral(collaterals);
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage("Collateral Transfered");
            }
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("No Records Found");

        } catch (Exception ex) {
            log.error("Error transferring collaterals: {}", ex.getMessage());
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage(ex.getMessage());
        }
        return commonResponse;
    }

    public Object blockCollaterals(MApplicationCollaterals collaterals, MurabahApplication application) {
        log.debug("===LSF : Blocking Collaterals : Collaterls :" + gson.toJson(collaterals));
        log.debug("===LSF : Blocking Collaterals : Application :" + gson.toJson(application));
        CommonResponse commonResponse = new CommonResponse();
        try {
            if (collaterals != null) {
                log.debug("===LSF : Collaterals is not null");
                List<Symbol> blockedSymbols = new ArrayList<>();
                if (collaterals.getTradingAccForColleterals() != null) {
                    for (TradingAcc fromAcc : collaterals.getTradingAccForColleterals()) {
                        log.debug("===LSF : Iterating Trading Account");
                        if (fromAcc.getSymbolsForColleteral() != null) {
                            for (Symbol symbol : fromAcc.getSymbolsForColleteral()) {
                                ShareTransferRequest shareBlockRequest = new ShareTransferRequest();
                                shareBlockRequest.setReqType(LsfConstants.SHARE_BLOCK_REQUEST);
                                shareBlockRequest.setFromTradingAccountId(fromAcc.getAccountId());
                                shareBlockRequest.setQuantity(symbol.getColleteralQty());
                                shareBlockRequest.setExchange(symbol.getExchange());
                                shareBlockRequest.setSymbol(symbol.getSymbolCode());

                                if (symbol.getColleteralQty() > 0) {
                                    log.debug("===Blocking Symbol:" + gson.toJson(shareBlockRequest));
                                    CommonResponse cmr = performAction(shareBlockRequest);

                                    if (cmr.getResponseCode() == 1) {
                                        symbol.setBlockedReference(cmr.getResponseMessage());
                                        symbol.setTransStatus(LsfConstants.SHARE_BLOCK_STATUS);
                                        log.debug("===========LSF : Share Block Succeeded , Block Reference:" + symbol.getBlockedReference());
                                        blockedSymbols.add(symbol);
                                    } else {
                                        commonResponse.setResponseCode(500);
                                        commonResponse.setErrorMessage(cmr.getResponseMessage() + " , Symbol :" + shareBlockRequest.getSymbol());
                                        log.debug("===========LSF : Share Block Failed , Symbol:" + symbol.getSymbolCode() + " , Reason :" + cmr.getErrorMessage());
                                        if (blockedSymbols.size() > 0) {
                                            log.debug("===========LSF : Releasing Blocked Symbols");
                                            for (Symbol releaseBlockSymbol : blockedSymbols) {
                                                ShareTransferRequest shareReleaseRequest = new ShareTransferRequest();
                                                shareReleaseRequest.setReqType(LsfConstants.SHARE_RELEASE_REQUEST);
                                                shareReleaseRequest.setParams(releaseBlockSymbol.getBlockedReference());
                                                CommonResponse shareReleaseResponse = performAction(shareReleaseRequest);
                                                if (shareReleaseResponse.getResponseCode() == 1) {
                                                    log.debug("===========LSF : Release Block Success , Symbol :" + releaseBlockSymbol.getSymbolCode());
                                                } else {
                                                    log.debug("===========LSF : Release Block Failed , Symbol :" + releaseBlockSymbol.getSymbolCode() + " , Failure Reason:" + shareReleaseResponse.getResponseMessage());
                                                }
                                            }
                                        }
                                        return commonResponse;
                                    }
                                }
                            }
                        }
                    }
                }
                /*---initiating cash block process---*/
                if (collaterals.getCashAccForColleterals() != null) {
                    for (CashAcc cashAcc : collaterals.getCashAccForColleterals()) {
                        log.debug("Iterating Cash Accounts : " + gson.toJson(cashAcc));
                        CashTransferRequest cashBlockRequest = new CashTransferRequest();
                        cashBlockRequest.setReqType(LsfConstants.CASH_BLOCK_REQUEST);
                        cashBlockRequest.setFromCashAccountId(cashAcc.getAccountId());

                        if (cashAcc.getAccountId().equalsIgnoreCase(application.getDibAcc())) { // block colletral amount + adminFee from cashAccount
                            log.debug("Checking Cash Account :" + cashAcc.getAccountId() + " |" + application.getCashAccount());
                            double adminFee = application.getFinanceMethod().equals("1") ? GlobalParameters.getInstance().getShareAdminFee() : GlobalParameters.getInstance().getComodityAdminFee();
                            double vat = calculateVatAmt(adminFee);
                            double totalCharge = LSFUtils.ceilTwoDecimals(adminFee + vat);
                            cashBlockRequest.setAmount(cashAcc.getAmountAsColletarals() + totalCharge);
                        }
                        log.debug("===LSF : Blocking Cash :" + gson.toJson(cashBlockRequest));

                        if (cashBlockRequest.getAmount() > 0) {
                            CommonResponse cmr = performAction(cashBlockRequest);
                            if (cmr.getResponseCode() == 1) {
                                cashAcc.setBlockedReference(cmr.getResponseMessage());
                                cashAcc.setTransStatus(LsfConstants.SHARE_BLOCK_STATUS);
                                log.debug("===========LSF : Cash Block Succeeded , Block Reference:" + cashAcc.getBlockedReference());
                            } else {
                                if (blockedSymbols.size() > 0) {
                                    log.debug("===========LSF : Releasing Blocked Symbols");
                                    for (Symbol releaseBlockSymbol : blockedSymbols) {
                                        ShareTransferRequest shareReleaseRequest = new ShareTransferRequest();
                                        shareReleaseRequest.setReqType(LsfConstants.SHARE_RELEASE_REQUEST);
                                        shareReleaseRequest.setParams(releaseBlockSymbol.getBlockedReference());
                                        CommonResponse shareReleaseResponse = performAction(shareReleaseRequest);
                                        if (shareReleaseResponse.getResponseCode() == 1) {
                                            log.debug("===========LSF : Release Block Success , Symbol :" + releaseBlockSymbol.getSymbolCode());
                                        } else {
                                            log.debug("===========LSF : Release Block Failed , Symbol :" + releaseBlockSymbol.getSymbolCode() + " , Failure Reason:" + shareReleaseResponse.getResponseMessage());
                                        }
                                    }
                                }
                                commonResponse.setResponseCode(500);
                                commonResponse.setErrorMessage(cmr.getResponseMessage());
                                return commonResponse;
                            }
                        }
                    }
                }

                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage("Collateral Transfered");
            }
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("No Records Found");

        } catch (Exception ex) {
            log.error("Error blocking collaterals: {}", ex.getMessage());
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage(ex.getMessage());
        }
        return commonResponse;
    }

    public boolean blockCollaterals_RollOverAcc(MApplicationCollaterals collaterals, MurabahApplication application) {
        log.info("===LSF : Blocking Collaterals RollOver AccI : {}", application.getId());
        try {
            if (collaterals.getLsfTypeTradingAccounts() != null) {
                collaterals.getLsfTypeTradingAccounts().forEach(fromAcc -> {
                    if (fromAcc != null && fromAcc.getSymbolsForColleteral() != null) {
                        fromAcc.getSymbolsForColleteral().forEach(symbol -> {
                            if (symbol != null) {
                                log.debug("===LSF : Blocking Symbol for RollOver Acc: {}", symbol.getSymbolCode());
                                ShareTransferRequest shareBlockRequest = new ShareTransferRequest();
                                shareBlockRequest.setReqType(LsfConstants.SHARE_BLOCK_REQUEST);
                                shareBlockRequest.setFromTradingAccountId(fromAcc.getAccountId());
                                shareBlockRequest.setQuantity(symbol.getColleteralQty());
                                shareBlockRequest.setExchange(symbol.getExchange());
                                shareBlockRequest.setSymbol(symbol.getSymbolCode());
                                CommonResponse cmr = performAction(shareBlockRequest);
                                if (cmr.getResponseCode() == 1) {
                                    symbol.setBlockedReference(cmr.getResponseMessage());
                                    symbol.setTransStatus(LsfConstants.SHARE_BLOCK_STATUS);
                                }
                            }
                        });
                    }
                });
            }

            if (collaterals.getLsfTypeCashAccounts() != null) {
                collaterals.getLsfTypeCashAccounts().forEach(cashAcc -> {
                    log.debug("===LSF : Blocking Cash for RollOver Acc: {}", cashAcc.getAccountId());
                    CashTransferRequest cashBlockRequest = new CashTransferRequest();
                    cashBlockRequest.setReqType(LsfConstants.CASH_BLOCK_REQUEST);
                    cashBlockRequest.setFromCashAccountId(cashAcc.getAccountId());
                    cashBlockRequest.setAmount(collaterals.getInitialCashCollaterals());

                    if (cashBlockRequest.getAmount() > 0) {
                        CommonResponse cmr = performAction(cashBlockRequest);
                        if (cmr.getResponseCode() == 1) {
                            cashAcc.setBlockedReference(cmr.getResponseMessage());
                            cashAcc.setTransStatus(LsfConstants.SHARE_BLOCK_STATUS);
                        }
                    }
                });
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object releaseCollaterals(MApplicationCollaterals collaterals) {

        MurabahApplication application = lsfRepository.getMurabahApplication(collaterals.getApplicationId());

        CommonResponse commonResponse = new CommonResponse();
        try {
            //MApplicationCollaterals collaterals=getApplicationCollateral(applicationId);
            if (collaterals != null) {
                // initiating Share Transfer process
                var tradingAccountList = application.isRollOverApp() ? collaterals.getLsfTypeTradingAccounts() : collaterals.getTradingAccForColleterals();
                if (tradingAccountList != null) {
                    boolean isModified = false;
                    for (TradingAcc fromAcc : tradingAccountList) {
                        for (Symbol symbol : fromAcc.getSymbolsForColleteral()) {
                            ShareTransferRequest shareReleaseRequest = new ShareTransferRequest();
                            shareReleaseRequest.setReqType(LsfConstants.SHARE_RELEASE_REQUEST);
                            shareReleaseRequest.setParams(symbol.getBlockedReference());
                            if (symbol.getBlockedReference() != null) {
                                CommonResponse cmr = performAction(shareReleaseRequest);
                                if (cmr.getResponseCode() == 1) {
                                    symbol.setTransStatus(LsfConstants.SHARE_RELEASE_STATUS);
                                    isModified = true;
                                } else {
                                    commonResponse.setResponseCode(500);
                                    return commonResponse;
                                }
                            }
                        }
                    }
                    if (isModified) {
                        lsfRepository.addEditCompleteCollateral(collaterals);
                    }

                }

                // initiating cash Transfer process
                var cashAccountList = application.isRollOverApp() ? collaterals.getLsfTypeCashAccounts() : collaterals.getCashAccForColleterals();
                if (cashAccountList != null) {
                    boolean isModified = false;
                    for (CashAcc cashAcc : cashAccountList) {
                        CashTransferRequest cashReleaseRequest = new CashTransferRequest();
                        cashReleaseRequest.setReqType(LsfConstants.CASH_RELEASE_REQUEST);
                        cashReleaseRequest.setParams(cashAcc.getBlockedReference());
                        if (cashAcc.getBlockedReference() != null) {
                            CommonResponse cmr = performAction(cashReleaseRequest);
                            if (cmr.getResponseCode() == 1) {
                                cashAcc.setTransStatus(LsfConstants.SHARE_RELEASE_STATUS);
                                isModified = true;
                            } else {
                                commonResponse.setResponseCode(500);
                                return commonResponse;
                            }
                        }
                    }
                    if (isModified) {
                        lsfRepository.addEditCompleteCollateral(collaterals);
                    }
                }

                // save whole collateral collection
                //  lsfRepository.addEditCompleteCollateral(collaterals);
                commonResponse.setResponseCode(200);
                commonResponse.setResponseMessage("Collateral Transfered");
            }
            commonResponse.setResponseCode(200);
            commonResponse.setResponseMessage("No Records Found");

        } catch (Exception ex) {
            log.error("Error transferring collaterals: {}", ex.getMessage());
            commonResponse.setResponseCode(500);
            commonResponse.setErrorMessage(ex.getMessage());
        }
        return commonResponse;
    }

    private TradingAcc findTradingAccForTransfer(TradingAcc fromAcc, List<TradingAcc> toAccList) {
        for (TradingAcc acc : toAccList) {
            if (acc.getExchange().equals(fromAcc.getExchange())) {
                return acc;
            }
        }
        return null;
    }

    protected CommonResponse performAction(Object request) {
        log.debug("===LSF : Performing Action:" + gson.toJson(request));
        CommonResponse cmr = new CommonResponse();
        try {
            String result = (String) helper.sendMessageToOms(gson.toJson(request));
            if (result != null && !result.equalsIgnoreCase("")) {
                Map<String, Object> resMap = new HashMap<>();
                resMap = gson.fromJson(result, resMap.getClass());
                String s = resMap.get("responseObject").toString();
                String delimitter = "\\|\\|";
                String[] resultArray = s.split(delimitter);
                if (resultArray.length > 0) {
                    if (resultArray[0].equals("1")) {
                        cmr.setResponseCode(1);
                        if (resultArray.length > 1) {
                            cmr.setResponseMessage(resultArray[1]);
                        }

                    } else {
                        cmr.setResponseCode(-1);
                        if (resultArray.length > 1) {
                            cmr.setResponseMessage(resultArray[1]);
                        }
                    }
                }

                return cmr;
            } else {
                cmr.setResponseCode(-1);
                return cmr;
            }
        } catch (Exception e) {
            log.error("Error performing action: {}", e.getMessage());   
            cmr.setResponseCode(-1);
            return cmr;
        }


    }

    private boolean doTransfer(Object request) {
        String result = (String) helper.sendMessageToOms(gson.toJson(request));
        Map<String, Object> resMap = new HashMap<>();
        resMap = gson.fromJson(result, resMap.getClass());
        String s = resMap.get("responseObject").toString();
        String delimitter = "\\|\\|";
        String[] resultArray = s.split(delimitter);
        return resultArray[0].equals("1");
    }

    public String reValuationProcess() {
        CommonResponse response = new CommonResponse();
        try {
            Date date = new Date();   // given date
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            if ((10 <= currentHour) && (currentHour <= 15)) {
                List<MurabahApplication> appList = lsfRepository.getOrderContractSingedApplications();
                var mainApplications = appList.stream().filter(application -> !application.isRollOverApp()).toList();
                var rollOverApplications = appList.stream().filter(MurabahApplication::isRollOverApp).toList();
                log.debug("===========LSF : (reValuationProcess) - mainApplications size: {}, rollOverApplications size: {}", mainApplications.size(), rollOverApplications.size());
                mainApplications.forEach(application -> reValuationProcess(application, false));
                rollOverApplications.forEach(application -> reValuationProcess_RollOverApp(application, true));
            }
            response.setResponseCode(200);
            response.setResponseMessage("recalculation process completed");

        } catch (Exception ex) {
            log.error("Error revaluating application: {}", ex.getMessage());
            response.setResponseCode(500);
            response.setErrorMessage(ex.getMessage());
        }
        return gson.toJson(response);
    }

    public String reValuationProcess(String applicationId) {
        log.debug("===========LSF : (runRevalueProcess)-REQUEST , applicationID :" + applicationId);
        CommonResponse response = new CommonResponse();
        try {
            MurabahApplication application = lsfRepository.getMurabahApplication(applicationId);
            reValuationProcess(application,true);

            response.setResponseCode(200);
            response.setResponseMessage("recalculation process completed");
        } catch (Exception ex) {
            log.error("Error revaluating application: {}", ex.getMessage());
            response.setResponseCode(500);
            response.setErrorMessage(ex.getMessage());
        }
        log.debug("===========LSF : (runRevalueProcess)-LSF-SERVER RESPONSE  : " + gson.toJson(response));
        return gson.toJson(response);
    }

    public MApplicationCollaterals reValuationProcess_RollOverApp(MurabahApplication application,boolean considerBlockAmount) {
        log.info("===========LSF : (reValuationProcess_RollOverApp) appId : {}", application.getId());
        if(!application.isRollOverApp() && !application.getFinanceMethod().equals("2")) {
            log.info("===========LSF : (reValuationProcess_RollOverApp) RollOver AppId : {} , not allowed this method" , application.getRollOverAppId());
            return null;
        }
        double totalPFCollateral = 0.0;
        double totalCollateral = 0.0;
        double totalPFMarketValue = 0.0;
        double totalWeightedPFMarketValue = 0.0;
        double totalCashCollateral = 0.0;
        MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateralForRollOver(application.getId());
        if( mApplicationCollaterals == null) {
            log.info("===========LSF : (reValuationProcess_RollOverApp) No Collaterals Found for AppId : {}", application.getId());
            return null;
        }
        var lsfTradingAccList = helper.getLsfTypeTradingAccounts(application.getCustomerId(),
                                                                 application.getRollOverAppId(), application.getMarginabilityGroup());
        if (!lsfTradingAccList.isEmpty()) {
            var tradingAccFromAoms = lsfTradingAccList.getFirst();
            TradingAcc tradingAcc =  mApplicationCollaterals.isTradingAccountLSFTypeExist(tradingAccFromAoms.getAccountId());
            tradingAcc.mapFromOmsResponse(tradingAccFromAoms);
            for (Symbol omsSymbol: tradingAccFromAoms.getSymbolList()) {
                Symbol smb = tradingAcc.isSymbolExist(omsSymbol.getSymbolCode(), omsSymbol.getExchange());
                smb.mapFromOms(omsSymbol);
                double contribToColletaral = ((smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed())) / 100) * smb.getMarginabilityPercentage();
                smb.setContibutionTocollateral(contribToColletaral);
                totalPFCollateral += contribToColletaral;
                totalCollateral += contribToColletaral;

                totalPFMarketValue = totalPFMarketValue + smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed());
                totalWeightedPFMarketValue = totalWeightedPFMarketValue + contribToColletaral;
                if (mApplicationCollaterals.getSecurityList() == null) {
                    mApplicationCollaterals.setSecurityList(new ArrayList<>());
                }
                mApplicationCollaterals.getSecurityList().add(smb);
            }
            lsfRepository.updateRevaluationInfo(tradingAcc.getAccountId(), totalPFMarketValue, totalWeightedPFMarketValue);
        }
        mApplicationCollaterals.setTotalPFMarketValue(totalPFMarketValue);
        mApplicationCollaterals.setTotalWeightedPFValue(totalPFCollateral);
        mApplicationCollaterals.setTotalPFColleteral(totalPFCollateral);

        var lsfTypeCashAccounts = helper.getLsfTypeCashAccounts(application.getCustomerId(), application.getRollOverAppId());
        if (!lsfTypeCashAccounts.isEmpty()) {
            CashAcc lsfTypeCashAcc = lsfTypeCashAccounts.getFirst();
            CashAcc cashAcc1 = mApplicationCollaterals.isCashAccLSFTypeExist(lsfTypeCashAcc.getAccountId());
            cashAcc1.setCashBalance(lsfTypeCashAcc.getCashBalance());
            totalCashCollateral += lsfTypeCashAcc.getCashBalance();
            totalCollateral += lsfTypeCashAcc.getCashBalance();
            cashAcc1.setAmountTransfered(lsfTypeCashAcc.getCashBalance());

            if (lsfTypeCashAcc.getBlockedAmount() > 0 && considerBlockAmount) {
                totalCollateral += lsfTypeCashAcc.getBlockedAmount();
            }
            if (lsfTypeCashAcc.getPendingSettle() > 0) {
                cashAcc1.setPendingSettle(lsfTypeCashAcc.getPendingSettle());
            }
            if (lsfTypeCashAcc.getNetReceivable() > 0) {
                cashAcc1.setNetReceivable(lsfTypeCashAcc.getNetReceivable());
            }
            lsfRepository.updateRevaluationCashAccountRelatedInfo(cashAcc1.getAccountId(), cashAcc1);
        }

        mApplicationCollaterals.setTotalCashColleteral(totalCashCollateral);
        mApplicationCollaterals.setNetTotalColleteral(totalCollateral);
        calculateOperativeLimit(mApplicationCollaterals);
        calculateRemainingOperativeLimit(mApplicationCollaterals);
        calculateFTV(mApplicationCollaterals);
        log.debug("=========== reValuationProcess_RollOverApp, AppId {} : FTV Value : {}", application.getId(), mApplicationCollaterals.getFtv());

        if (violateFTVwithThirdMarginLimit(mApplicationCollaterals)) {
            log.debug("=========== reValuationProcess_RollOverApp : Reached to Third  Margin Level, AppId {}, Last Margin Call Date : {}, Last Margine Call Date {}", application.getId(), mApplicationCollaterals.getMargineCallDate(), mApplicationCollaterals.getLiquidateCallDate());
            if (mApplicationCollaterals.getLiquidateCallDate() == null || mApplicationCollaterals.getLiquidateCallDate().isEmpty()) {
                if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                    mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                    sendMargineNotification(1, mApplicationCollaterals, application);
                    lsfRepository.addMarginCallLog(mApplicationCollaterals, 1);
                }

                mApplicationCollaterals.setLiquidateCallDate(dateFormat.format(new Date()));
                sendMargineNotification(3, mApplicationCollaterals, application);

            } else {
                if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                    sendMargineNotification(1, mApplicationCollaterals, application);
                    mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                }
                if (isEligibleForMarginNotification(mApplicationCollaterals.getLiquidateCallDate())) {
                    sendMargineNotification(3, mApplicationCollaterals, application);
                    mApplicationCollaterals.setLiquidateCallDate(dateFormat.format(new Date()));
                }

            }
            mApplicationCollaterals.setLiqudationCall(true);
            lsfRepository.addMarginCallLog(mApplicationCollaterals, 3);
        } else if (violateFTVwithFirstMarginLimit(mApplicationCollaterals)) {
            log.debug("=========== reValuationProcess_RollOverApp : Reached to First  Margin Level. AppId {}, Last Margin Call Date : {} ", application.getId(), mApplicationCollaterals.getMargineCallDate());
            mApplicationCollaterals.setLiquidateCallDate("");
            if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                sendMargineNotification(1, mApplicationCollaterals, application);
                lsfRepository.addMarginCallLog(mApplicationCollaterals, 1);
            }

        } else {
            mApplicationCollaterals.setMargineCallDate("");
            mApplicationCollaterals.setLiquidateCallDate("");
        }
        lsfRepository.updateCollateralWithCompleteTradingAcc(mApplicationCollaterals);
        lsfRepository.addFTVLog(mApplicationCollaterals);
        return mApplicationCollaterals;
    }

    public MApplicationCollaterals reValuationProcess(MurabahApplication application,boolean considerBlockAmount) {
        log.info("===========LSF : (reValuationProcess) appId : {}", application.getId());
        if(application.isRollOverApp()) {
            log.info("===========LSF : (reValuationProcess) RollOver AppId : {} , not allowed this method" , application.getRollOverAppId());
            return null;
        }
        double totalPFCollateral = 0.0;
        double totalCollateral = 0.0;
        double totalPFMarketValue = 0.0;
        double totalWeightedPFMarketValue = 0.0;
        double totalCashCollateral = 0.0;
        MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateral(application.getId());
        if( mApplicationCollaterals == null) {
            log.info("===========LSF : (reValuationProcess) No Collaterals Found for AppId : {}", application.getId());
            return null;
        }
        var lsfTradingAccList = helper.getLsfTypeTradingAccounts(application.getCustomerId(),
                                                                 application.getId(), application.getMarginabilityGroup());
        if (!lsfTradingAccList.isEmpty()) {
            var tradingAccFromAoms = lsfTradingAccList.getFirst();
            TradingAcc tradingAcc =  mApplicationCollaterals.isTradingAccountLSFTypeExist(tradingAccFromAoms.getAccountId());
            tradingAcc.mapFromOmsResponse(tradingAccFromAoms);
            for (Symbol omsSymbol: tradingAccFromAoms.getSymbolList()) {
                Symbol smb = tradingAcc.isSymbolExist(omsSymbol.getSymbolCode(), omsSymbol.getExchange());
                smb.mapFromOms(omsSymbol);
                double contribToColletaral = ((smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed())) / 100) * smb.getMarginabilityPercentage();
                smb.setContibutionTocollateral(contribToColletaral);
                totalPFCollateral += contribToColletaral;
                totalCollateral += contribToColletaral;

                totalPFMarketValue = totalPFMarketValue + smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed());
                totalWeightedPFMarketValue = totalWeightedPFMarketValue + contribToColletaral;
                if (mApplicationCollaterals.getSecurityList() == null) {
                    mApplicationCollaterals.setSecurityList(new ArrayList<>());
                }
                mApplicationCollaterals.getSecurityList().add(smb);
            }
            lsfRepository.updateRevaluationInfo(tradingAcc.getAccountId(), totalPFMarketValue, totalWeightedPFMarketValue);
        }
        mApplicationCollaterals.setTotalPFMarketValue(totalPFMarketValue);
        mApplicationCollaterals.setTotalWeightedPFValue(totalPFCollateral);
        mApplicationCollaterals.setTotalPFColleteral(totalPFCollateral);

        var lsfTypeCashAccounts = helper.getLsfTypeCashAccounts(application.getCustomerId(), application.getId());
        if (!lsfTypeCashAccounts.isEmpty()) {
            CashAcc lsfTypeCashAcc = lsfTypeCashAccounts.getFirst();
            CashAcc cashAcc1 = mApplicationCollaterals.isCashAccLSFTypeExist(lsfTypeCashAcc.getAccountId());
            cashAcc1.setCashBalance(lsfTypeCashAcc.getCashBalance());
            totalCashCollateral += lsfTypeCashAcc.getCashBalance();
            totalCollateral += lsfTypeCashAcc.getCashBalance();
            cashAcc1.setAmountTransfered(lsfTypeCashAcc.getCashBalance());

            if (lsfTypeCashAcc.getBlockedAmount() > 0 && considerBlockAmount) {
                totalCollateral += lsfTypeCashAcc.getBlockedAmount();
            }
            if (lsfTypeCashAcc.getPendingSettle() > 0) {
                cashAcc1.setPendingSettle(lsfTypeCashAcc.getPendingSettle());
            }
            if (lsfTypeCashAcc.getNetReceivable() > 0) {
                cashAcc1.setNetReceivable(lsfTypeCashAcc.getNetReceivable());
            }
            lsfRepository.updateRevaluationCashAccountRelatedInfo(cashAcc1.getAccountId(), cashAcc1);
        }

        mApplicationCollaterals.setTotalCashColleteral(totalCashCollateral);
        mApplicationCollaterals.setNetTotalColleteral(totalCollateral);
        calculateOperativeLimit(mApplicationCollaterals);
        calculateRemainingOperativeLimit(mApplicationCollaterals);
        calculateFTV(mApplicationCollaterals);
        log.debug("=========== reValuationProcess, AppId {} : FTV Value : {}", application.getId(), mApplicationCollaterals.getFtv());

        if (violateFTVwithThirdMarginLimit(mApplicationCollaterals)) {
            log.debug("=========== reValuationProcess : Reached to Third  Margin Level, AppId {}, Last Margin Call Date : {}, Last Margine Call Date {}", application.getId(), mApplicationCollaterals.getMargineCallDate(), mApplicationCollaterals.getLiquidateCallDate());
            if (mApplicationCollaterals.getLiquidateCallDate() == null || mApplicationCollaterals.getLiquidateCallDate().isEmpty()) {
                if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                    mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                    sendMargineNotification(1, mApplicationCollaterals, application);
                    lsfRepository.addMarginCallLog(mApplicationCollaterals, 1);
                }

                mApplicationCollaterals.setLiquidateCallDate(dateFormat.format(new Date()));
                sendMargineNotification(3, mApplicationCollaterals, application);

            } else {
                if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                    sendMargineNotification(1, mApplicationCollaterals, application);
                    mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                }
                if (isEligibleForMarginNotification(mApplicationCollaterals.getLiquidateCallDate())) {
                    sendMargineNotification(3, mApplicationCollaterals, application);
                    mApplicationCollaterals.setLiquidateCallDate(dateFormat.format(new Date()));
                }

            }
            mApplicationCollaterals.setLiqudationCall(true);
            lsfRepository.addMarginCallLog(mApplicationCollaterals, 3);
        } else if (violateFTVwithFirstMarginLimit(mApplicationCollaterals)) {
            log.debug("=========== reValuationProcess : Reached to First  Margin Level. AppId {}, Last Margin Call Date : {} ", application.getId(), mApplicationCollaterals.getMargineCallDate());
            mApplicationCollaterals.setLiquidateCallDate("");
            if (isEligibleForMarginNotification(mApplicationCollaterals.getMargineCallDate())) {
                mApplicationCollaterals.setMargineCallDate(dateFormat.format(new Date()));
                sendMargineNotification(1, mApplicationCollaterals, application);
                lsfRepository.addMarginCallLog(mApplicationCollaterals, 1);
            }

        } else {
            mApplicationCollaterals.setMargineCallDate("");
            mApplicationCollaterals.setLiquidateCallDate("");
        }
        lsfRepository.updateCollateralWithCompleteTradingAcc(mApplicationCollaterals);
        lsfRepository.addFTVLog(mApplicationCollaterals);
        return mApplicationCollaterals;
    }

    protected void calculateOperativeLimit(MApplicationCollaterals applicationCollateral) { // TODO need to change
        double ftvForOperativeLimit = ((GlobalParameters.getInstance().getFtvForOperativeLimit()) / 100);
        double operativeLimit = (ftvForOperativeLimit * (applicationCollateral.getNetTotalColleteral() - applicationCollateral.getUtilizedLimitAmount())) / (1.0 - ftvForOperativeLimit);
        if (operativeLimit >= applicationCollateral.getApprovedLimitAmount()) {
            applicationCollateral.setOpperativeLimitAmount(applicationCollateral.getApprovedLimitAmount());
        } else {
            applicationCollateral.setOpperativeLimitAmount(operativeLimit);
        }
    }

    //below should be called from settlement function.
    public void calculateRemainingOperativeLimit(MApplicationCollaterals applicationCollateral) {// TODO need to change
        //"revolving"
        applicationCollateral.setRemainingOperativeLimitAmount(0.0);
        if (GlobalParameters.getInstance().getOperatingLimitType()) {
            if (applicationCollateral.getOutstandingAmount() < applicationCollateral.getOpperativeLimitAmount()) {
                applicationCollateral.setRemainingOperativeLimitAmount(applicationCollateral.getOpperativeLimitAmount() - applicationCollateral.getOutstandingAmount());
            }
        } else {// non-revolving
            if (applicationCollateral.getUtilizedLimitAmount() < applicationCollateral.getOpperativeLimitAmount()) {
                applicationCollateral.setRemainingOperativeLimitAmount(applicationCollateral.getOpperativeLimitAmount() - applicationCollateral.getUtilizedLimitAmount());
            }
        }
    }

    public void calculateFTV(MApplicationCollaterals collaterals) { /*-----Calculate FTV for ABIC-----*/
        if (collaterals.getOutstandingAmount() > 0) {
            double ftv = (collaterals.getNetTotalColleteral()/collaterals.getOutstandingAmount()) * 100.0;
            collaterals.setFtv(LSFUtils.ceilTwoDecimals(ftv));
        }else{
            collaterals.setFtv(0.0);
        }
    }

    public boolean violateFTVwithFirstMarginLimit(MApplicationCollaterals collaterals) {
        double firstMargineCallLimit = GlobalParameters.getInstance().getFirstMarginCall();
        if (collaterals.getFtv() <= firstMargineCallLimit) {
            collaterals.setFirstMargineCall(true);
            collaterals.setSecondMargineCall(false);
            collaterals.setLiqudationCall(false);
            return true;
        } else {
            collaterals.setFirstMargineCall(false);
            collaterals.setSecondMargineCall(false);
            collaterals.setLiqudationCall(false);
            return false;
        }
    }

    public boolean violateFTVwithSecondMarginLimit(MApplicationCollaterals collaterals) {
        double secondMarginCallLimit = GlobalParameters.getInstance().getSecondMarginCall();
        if (collaterals.getFtv() <= secondMarginCallLimit) {
            collaterals.setSecondMargineCall(true);
            collaterals.setFirstMargineCall(false);
            collaterals.setLiqudationCall(false);
            return true;
        } else {
            collaterals.setFirstMargineCall(false);
            collaterals.setSecondMargineCall(false);
            collaterals.setLiqudationCall(false);
            return false;
        }
    }

    public boolean violateFTVwithThirdMarginLimit(MApplicationCollaterals collaterals) {
        double liquidationCallLimit = GlobalParameters.getInstance().getLiquidationCall();
        if (collaterals.getFtv() <= liquidationCallLimit) {
            collaterals.setLiqudationCall(true);
            collaterals.setFirstMargineCall(true);
            collaterals.setSecondMargineCall(false);
            return true;
        } else {
            collaterals.setFirstMargineCall(false);
            collaterals.setSecondMargineCall(false);
            collaterals.setLiqudationCall(false);
            return false;
        }
    }

    private void sendMargineNotification(int margineType, MApplicationCollaterals collaterals, MurabahApplication application) {
        collaterals.setMargineCallAtempts(collaterals.getMargineCallAtempts() + 1);
        notificationManager.sendMarginNotification(margineType, collaterals, application);
    }

    public ProfitResponse calculateProfitOnTenor(int tenorId, double amount, double profitPercentage) {
        ProfitResponse profitResponse = new ProfitResponse();
        Tenor tenor = lsfRepository.getTenor(tenorId);
        double profit = amount * (tenor.getProfitPercentage() / 100);
        profitResponse.setProfitPercent(tenor.getProfitPercentage());
        if (profitPercentage > 0) {
            profit = amount * (profitPercentage / 100);
            profitResponse.setProfitPercent(profitPercentage);
        }
        profitResponse.setLibourAmount(0);
        profitResponse.setProfitAmount(profit);
        profitResponse.setSibourAmount(0);
        profitResponse.setTotalProfit(amount + profit);
        return profitResponse;
    }

    public ProfitResponse calculateProfitOnStructure(double amount, int loanPeriodInDays, double profitPercentage, boolean isRollover) {
        List<CommissionStructure> commissionStructures = lsfRepository.getCommissionStructure();
        CommissionStructure applyingStructuer = null;
        ProfitResponse profitResponse = new ProfitResponse();
        double orderValue = 0;
        double profit = 0;
        double sibour = 0;
        double libour = 0;
        double tototal = 0;

        if (profitPercentage > 0) {
            orderValue = amount / (1 + (profitPercentage * loanPeriodInDays / 36000));
            profit = amount - orderValue;
            if (isRollover) {
                profit = amount * applyingStructuer.getPercentageAmount()* loanPeriodInDays / 36000;
            }
            sibour = amount * (0 / 100);
            libour = amount * (0 / 100);
            tototal = amount + profit + sibour + libour;
            profitResponse.setLibourAmount(libour);
            profitResponse.setProfitAmount(profit);
            profitResponse.setProfitPercent(profitPercentage);
            profitResponse.setSibourAmount(sibour);
            profitResponse.setTotalProfit(tototal);

        } else {
            for (CommissionStructure commissionStructure : commissionStructures) {
                if ((commissionStructure.getFromValue() <= amount) && (amount <= commissionStructure.getToValue())) {
                    applyingStructuer = commissionStructure;
                    break;
                }
            }
            // start calculation
            if (applyingStructuer != null) {
                orderValue = amount / (1 + (applyingStructuer.getPercentageAmount() * loanPeriodInDays / 36000));
                  profit = amount - orderValue;
                if (isRollover) {
                    profit = amount * applyingStructuer.getPercentageAmount()* loanPeriodInDays / 36000;
                }
                sibour = amount * (applyingStructuer.getSibourRate() / 100);
                libour = amount * (applyingStructuer.getLibourRate() / 100);
                tototal = amount + profit + sibour + libour;
                profitResponse.setLibourAmount(libour);
                profitResponse.setProfitAmount(profit);
                profitResponse.setProfitPercent(applyingStructuer.getPercentageAmount());
                profitResponse.setSibourAmount(sibour);
                profitResponse.setTotalProfit(tototal);
            }
        }
        return profitResponse;
    }

    public ProfitResponse calculateProfitOnStructureSimple(double amount, int loanPeriodInDays, double profitPercentage) {
        List<CommissionStructure> commissionStructures = lsfRepository.getCommissionStructure();
        CommissionStructure applyingStructuer = null;
        double profit = 0;
        double sibour = 0;
        double libour = 0;
        double tototal = 0;
        ProfitResponse profitResponse = new ProfitResponse();
        if (profitPercentage > 0) // use applied profit % for existing loans
        {
            profit = (amount * (profitPercentage / 100) * loanPeriodInDays) / 360;
            sibour = 0;
            libour = 0;
            tototal = profit + sibour + libour;
            profitResponse.setLibourAmount(libour);
            profitResponse.setProfitAmount(profit);
            profitResponse.setProfitPercent(profitPercentage);
            profitResponse.setSibourAmount(sibour);
            profitResponse.setTotalProfit(tototal);

        } else {
            for (CommissionStructure commissionStructure : commissionStructures) {
                if ((commissionStructure.getFromValue() <= amount) && (amount <= commissionStructure.getToValue())) {
                    applyingStructuer = commissionStructure;
                    break;
                }
            }
            // start calculation
            if (applyingStructuer != null) {
                profit = (amount * (applyingStructuer.getPercentageAmount() / 100) * loanPeriodInDays) / 360;
                sibour = amount * (applyingStructuer.getSibourRate() / 100);
                libour = amount * (applyingStructuer.getLibourRate() / 100);
                tototal = profit + sibour + libour;
                profitResponse.setLibourAmount(libour);
                profitResponse.setProfitAmount(profit);
                profitResponse.setProfitPercent(applyingStructuer.getPercentageAmount());
                profitResponse.setSibourAmount(sibour);
                profitResponse.setTotalProfit(tototal);

            }
        }
        return profitResponse;
    }

    public String initialValuation(String applicationId) {
        log.debug("===========LSF : (runInitialValuation)-REQUEST , applicationID:" + applicationId);

        CommonResponse response = new CommonResponse();
        MApplicationCollaterals mApplicationCollaterals = null;
        MurabahApplication application = lsfRepository.getMurabahApplication(applicationId);
        try {
            if(application.isRollOverApp()) {
                mApplicationCollaterals = lsfRepository.getApplicationCollateral(application.getId());
                if (mApplicationCollaterals.getId() != null) { // already have collaterals for this Rollover
                    mApplicationCollaterals = lsfRepository.getCollateralForRollOverCollaterelWindow(application.getId(), mApplicationCollaterals);
                }
            } else {
                mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateral(application.getId());
            }



        } catch (Exception ex) {
            throw new RuntimeException("Error getting application collateral", ex);
        }
        // get attached Marginability Group
        MarginabilityGroup marginabilityGroup = helper.getMarginabilityGroup(application.getMarginabilityGroup());
        List<LiquidityType> attachedLiqGoupList = null;
        List<SymbolMarginabilityPercentage> marginabilityPercentages = null;
        if (marginabilityGroup != null)
            marginabilityPercentages = marginabilityGroup.getMarginableSymbols();


        if (mApplicationCollaterals != null) {
            double totalCollateral = 0.0;
            double totalCashCollateral = 0.0;
            double totalPFCollateral = 0.0;
            // PF colletarals
            if (mApplicationCollaterals.getTradingAccForColleterals() != null) {
                for (TradingAcc tradingAcc : mApplicationCollaterals.getTradingAccForColleterals()) {
                    for (Symbol symbol : tradingAcc.getSymbolsForColleteral()) {
                        LiquidityType attachedToSymbolLiq = symbol.getLiquidityType();
                        if (attachedLiqGoupList != null) {
                            for (LiquidityType liq : attachedLiqGoupList) {
                                if (liq.getLiquidId() == attachedToSymbolLiq.getLiquidId()) {
                                    symbol.setLiquidityType(liq);
                                }
                            }
                        }

                        if (marginabilityGroup != null)
                            symbol.setMarginabilityPercentage(marginabilityGroup.getGlobalMarginablePercentage());

                        if(marginabilityPercentages != null) {
                            for(SymbolMarginabilityPercentage smp: marginabilityPercentages) {
                                if(smp.getSymbolCode().equals(symbol.getSymbolCode()) && smp.getExchange().equals(symbol.getExchange())) {
                                    symbol.setMarginabilityPercentage(smp.getMarginabilityPercentage());
                                }
                            }
                        }

                        Double contribToColletaral = 0.0;

                        if (symbol.getLastTradePrice() > 0) {
                            contribToColletaral = ((symbol.getColleteralQty() * symbol.getLastTradePrice()) / 100) * symbol.getMarginabilityPercentage();
                        } else {
                            contribToColletaral = ((symbol.getColleteralQty() * symbol.getPreviousClosed()) / 100) * symbol.getMarginabilityPercentage();
                        }
                        symbol.setContibutionTocollateral(contribToColletaral);
                        totalPFCollateral += contribToColletaral;
                        totalCollateral += contribToColletaral;
                    }
                }
            }

            // cash Colletarals
            if (mApplicationCollaterals.getCashAccForColleterals() != null) {
                for (CashAcc cashAcc : mApplicationCollaterals.getCashAccForColleterals()) {
                    totalCashCollateral += cashAcc.getAmountAsColletarals();
                    totalCollateral += cashAcc.getAmountAsColletarals();
                }
            }

            mApplicationCollaterals.setTotalCashColleteral(totalCashCollateral);
            mApplicationCollaterals.setTotalPFColleteral(totalPFCollateral);

            /*--Valuation of External Colleterals-*/
            double totalExternalCollatralsValue = 0.0;
            List<ExternalCollaterals> applicationExternalColletrals = lsfRepository.getExternalCollateralsForApplication(Integer.parseInt(applicationId));
            if (applicationExternalColletrals != null && applicationExternalColletrals.size() > 0) {
                for (ExternalCollaterals externalCollaterals : applicationExternalColletrals) {
                    if (externalCollaterals.isAddToCollateral()) {
                        if (isExternalColletralsExpired(externalCollaterals)) {
                            //  totalCollateral = totalCollateral + externalCollaterals.getApplicableAmount();
                            totalExternalCollatralsValue = totalExternalCollatralsValue + externalCollaterals.getApplicableAmount();
                        }
                    }
                }
            }
            totalCollateral = totalCollateral + totalExternalCollatralsValue;
            mApplicationCollaterals.setNetTotalColleteral(totalCollateral);
            calculateOperativeLimit(mApplicationCollaterals);
            calculateRemainingOperativeLimit(mApplicationCollaterals);
            calculateFTV(mApplicationCollaterals);
            lsfRepository.addEditCollaterals(mApplicationCollaterals);
            mApplicationCollaterals.setInitialCashCollaterals(mApplicationCollaterals.getTotalCashColleteral());
            mApplicationCollaterals.setInitialPFCollaterals(mApplicationCollaterals.getTotalPFColleteral());
            lsfRepository.addInitialCollaterals(mApplicationCollaterals);

            response.setResponseCode(200);
            response.setResponseMessage("recalculation process completed");
        } else {
            response.setResponseCode(200);
            response.setResponseMessage("No Records Found");
        }
        log.debug("===========LSF : (runInitialValuation)-LSF-SERVER RESPONSE  : " + gson.toJson(response));

        return gson.toJson(response);
    }

    public String moveToCashTransferredClosedState(String applicationID, String message, String orderID) {
        return lsfRepository.moveToClosedState(applicationID, message, orderID);
    }

    public Object liquidate(String orderId) {
        log.info("===================LSF: Sending Liquidation Call :" + orderId);
        CommonResponse cmr = new CommonResponse();
        try {
            ShareTransferRequest liquidateReq = new ShareTransferRequest();
            liquidateReq.setReqType(LsfConstants.LIQUIDATE_SYMBOLS);
            liquidateReq.setBasketReference(orderId);
            cmr = performAction(liquidateReq);

        } catch (Exception ex) {
            cmr.setResponseCode(-1);
        }
        return cmr;
    }

    public void addExternalCollaterals(String colId, List<ExternalCollaterals> externalCollaterals) {
        for (ExternalCollaterals collaterals : externalCollaterals) {
            collaterals.setCollateralId(Integer.parseInt(colId));
            collaterals.setId(Integer.parseInt(lsfRepository.addExternalCollaterals(collaterals)));
        }

    }

    public String moveToLiquidatedState(String applicationID, String message) {
        String response = lsfRepository.moveToLiquidateState(applicationID, message);
        return response;
    }
    /*----------------Settlement Related Common Functions--------------*/

    public CashAcc getLsfTypeCashAccountForUser(String userID, String applicationId) {
        CashAcc cashAcc = CashAcc.builder().build();
        Map<String, Object> resultMap = new HashMap<>();
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        inqueryMessage.setCustomerId(userID);
        inqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
        inqueryMessage.setContractId(applicationId);
        Object result = helper.sendSettlementRelatedOMSRequest(gson.toJson(inqueryMessage), LsfConstants.HTTP_PRODUCER_OMS_GET_LSF_CASH_ACCOUNT_USERID);
        if (result == null) {
            return null;
        } else {
            resultMap = gson.fromJson((String) result, resultMap.getClass());
            ArrayList<Map<String, Object>> cashAccList = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
            if (cashAccList.size() > 0) {
                cashAcc.setCashBalance(Double.parseDouble(cashAccList.get(0).get("balance").toString()));
                cashAcc.setAccountId(cashAccList.get(0).get("accountNo").toString());
                if (cashAccList.get(0).containsKey("investorAccountNo")) {
                    cashAcc.setInvestmentAccountNumber(String.valueOf(cashAccList.get(0).get("investorAccountNo").toString()));
                }
                if (cashAccList.get(0).containsKey("pendingSettle")) {
                    cashAcc.setPendingSettle(Double.parseDouble(cashAccList.get(0).get("pendingSettle").toString()));
                }
                if (cashAccList.get(0).containsKey("netReceivable")) {
                    cashAcc.setNetReceivable(Double.parseDouble(cashAccList.get(0).get("netReceivable").toString()));
                }
            }
            return cashAcc;
        }
    }

    public TradingAcc getLsfTypeTradinAccountForUser(String customerID, String applicationId) {
        TradingAcc tradingAcc = new TradingAcc();
        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
        commonInqueryMessage.setCustomerId(customerID);
        commonInqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
        commonInqueryMessage.setContractId(applicationId);
        String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage), LsfConstants.HTTP_PRODUCER_OMS_REQ_GET_LSF_TYPE_TRADING_ACCOUNT);
        Map<String, Object> resMap = new HashMap<>();
        resMap = gson.fromJson(result, resMap.getClass());
        ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resMap.get("responseObject");
        if (lsfTrd.size() > 0) {
            Map<String, Object> lsfTrdAccnt = (Map<String, Object>) lsfTrd.get(0).get("tradingAccount");
            tradingAcc.setExchange(lsfTrdAccnt.get("exchange").toString());
            tradingAcc.setAccountId(lsfTrdAccnt.get("accountId").toString());
        }
        return tradingAcc;
    }

    public String getMasterCashAccount() {
        String cashAccount = null;
        String institutionTradingAccount = null;
        String exchange = null;
        institutionTradingAccount = GlobalParameters.getInstance().getInstitutionTradingAcc();
        exchange = GlobalParameters.getInstance().getDefaultExchange();
        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
        if (exchange != null && institutionTradingAccount != null) {
            commonInqueryMessage.setReqType(LsfConstants.GET_ACCOUNT_INFO_BY_TRADING_ACCOUNT);
            commonInqueryMessage.setTradingAccountId(institutionTradingAccount);
            commonInqueryMessage.setExchange(exchange);
            String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage), LsfConstants.HTTP_PRODUCER_OMS_GET_MASTER_CASH_ACCOUNT);
            if (result != null) {
                Map<String, Object> resMap = new HashMap<>();
                resMap = gson.fromJson(result, resMap.getClass());
                String responseString = resMap.get("responseObject").toString();
                Map<String, Object> finalMap = gson.fromJson(responseString, resMap.getClass());
                cashAccount = finalMap.get("relCashAccNo").toString();
            }
        }
        return cashAccount;
    }

    public boolean cashTransferToMasterAccount(String fromAccount, String toAccount, double transferAmount, String applicationID) {
        boolean isTransferred = false;
        BigDecimal bigDecimal = BigDecimal.valueOf(transferAmount);
        // Fix: Set scale with proper rounding mode and store in a variable
        BigDecimal roundedValue = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        
        CashTransferRequest cashTransferRequest = new CashTransferRequest();
        cashTransferRequest.setReqType(LsfConstants.CASH_TRANSFER);
        cashTransferRequest.setId(Long.toString(System.currentTimeMillis()));
        cashTransferRequest.setApplicationid(applicationID);
        cashTransferRequest.setFromCashAccountId(fromAccount);
        cashTransferRequest.setAmount(roundedValue.doubleValue()); // Use the rounded value
        cashTransferRequest.setToCashAccountId(toAccount);
        cashTransferRequest.setParams("1"); // identify the cash transfer to Master Account
        String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(cashTransferRequest), LsfConstants.HTTP_PRODUCER_OMS_CASH_TRANSFER_MASTER_CASH_ACCOUNT);
        if (result != null && !result.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(result, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray[0].equals("1")) {
                isTransferred = true;
            } else {
                isTransferred = false;
                log.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:" + fromAccount + " ,To Account :" + toAccount + " , Failure Reason :" + resultArray[1]);
            }
        } else {
            isTransferred = false;
        }
        return isTransferred;
    }

    public boolean cashTransfer(String fromAccount, String toAccount, double transferAmount, String applicationID) {
        boolean isTransferred = false;
        BigDecimal bigDecimal = BigDecimal.valueOf(transferAmount);
        // Fix: Set scale with proper rounding mode and store in a variable
        BigDecimal roundedValue = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        CashTransferRequest cashTransferRequest = new CashTransferRequest();
        cashTransferRequest.setReqType(LsfConstants.CASH_TRANSFER);
        cashTransferRequest.setId(Long.toString(System.currentTimeMillis()));
        cashTransferRequest.setApplicationid(applicationID);
        cashTransferRequest.setFromCashAccountId(fromAccount);
        cashTransferRequest.setAmount(roundedValue.doubleValue()); // Use the rounded value
        cashTransferRequest.setToCashAccountId(toAccount);
        String result = (String) helper.sendMessageToOms(gson.toJson(cashTransferRequest));
        if (result != null && !result.equalsIgnoreCase("")) {
            Map<String, Object> resMap = new HashMap<>();
            resMap = gson.fromJson(result, resMap.getClass());
            String s = resMap.get("responseObject").toString();
            String delimitter = "\\|\\|";
            String[] resultArray = s.split(delimitter);
            if (resultArray[0].equals("1")) {
                isTransferred = true;
            } else {
                isTransferred = false;
                String reason = resultArray.length > 1 ? resultArray[1] : "Unknown reason";
                log.error("===========LSF : Cash Transfer Failure(Transfer Failed) , From Account:" + fromAccount + " ,To Account :" + toAccount + " , Failure Reason :" + reason);
            }
        } else {
            isTransferred = false;
        }
        return isTransferred;
    }

    private boolean isExternalColletralsExpired(ExternalCollaterals externalCollaterals) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return dateFormat.parse(externalCollaterals.getExpireDate()).after(date);
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean validateSymbolAmountAgainstConcentration(String concetrationGroup, List<Symbol> purchaseSymbolList) {
        if (concetrationGroup.equals("")) {
            // default validate for 100%
            return true;
        }
        // initialize List of LiqTypes to store LiqType Concentration in purchase order symbols
        List<LiquidityType> liquidityTypesInPurchaseOrder = new ArrayList<>();
        // get the liquidityType of the attached Concentration Group
        List<LiquidityType> liquidityTypes = lsfRepository.getStockConcentrationGroupLiquidTypes(concetrationGroup);
        for (Symbol symbol : purchaseSymbolList) {
            LiquidityType liqType = checkForExistanceLiqType(liquidityTypesInPurchaseOrder, lsfRepository.getSymbolLiquidityType(symbol), true);
            liqType.setStockConcentrationPercent(liqType.getStockConcentrationPercent() + symbol.getPercentage());
        }
        // match each Symbol wise liqtype percentrage with Group LiqType
        for (LiquidityType poLiqType : liquidityTypesInPurchaseOrder) {
            LiquidityType liqConcentrationGrp = checkForExistanceLiqType(liquidityTypes, poLiqType, false);
            if (liqConcentrationGrp != null) {
                if (poLiqType.getStockConcentrationPercent() > liqConcentrationGrp.getStockConcentrationPercent()) {
                    return false;
                }
            }
        }
        return true;
    }

    public Map<String,Object> validateforStockConcentration(MurabahApplication application, MApplicationCollaterals collaterals, OMSQueueRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            List<LiquidityType> liquidityTypes = lsfRepository.getStockConcentrationGroupLiquidTypes(application.getStockConcentrationGroup());
            List<Symbol> symbols = lsfRepository.loadSymbols(request.getExchange(), request.getSymbol());
            LiquidityType symbolLiqType = checkForExistanceLiqType(liquidityTypes, symbols.get(0).getConcentrationType(), false);
            log.info("symbolLiqType " + symbolLiqType);
            Double openSellOrderValue = 0.0;
            double availableConcentraion = 0;
            if (collaterals.getLsfTypeTradingAccounts() != null && collaterals.getLsfTypeTradingAccounts().size() > 0) {
                if (collaterals.getLsfTypeTradingAccounts().get(0).getSymbolsForColleteral() != null) {
                    for (Symbol symbol : collaterals.getLsfTypeTradingAccounts().get(0).getSymbolsForColleteral()) {
                        openSellOrderValue = symbol.getOpenSellQty() * (symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed());
                        if (symbol.getSymbolCode().equals(request.getSymbol())) {
                            Double openBuyOrderValue = symbol.getOpenBuyQty() * (symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed());
                            availableConcentraion = symbol.getAvailableQty() * (symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed());

                            availableConcentraion = availableConcentraion - openBuyOrderValue;
                        }
                    }
                }
            }
            double totalAssets = (collaterals.getTotalCashColleteral() - openSellOrderValue) + collaterals.getTotalPFMarketValue();
            log.debug("Open Sell Order Value : " + openSellOrderValue);
            log.debug("Total Assets : " + totalAssets);
//          log.info("stockConcentrationPercent"+symbolLiqType.getStockConcentrationPercent());

            double allowdConcentration = totalAssets * (symbolLiqType != null ? symbolLiqType.getStockConcentrationPercent() : 0) / 100;

            log.debug("===========LSF : Total Assets  :" + totalAssets + " , Allowed concentration :" +
                    allowdConcentration + " , Available concentration :" + availableConcentraion +
                    " , Order Amount :" + request.getAmount());

            responseMap.put("allowedConcentration", LSFUtils.ceilTwoDecimals((allowdConcentration - availableConcentraion)));
            if ((allowdConcentration - availableConcentraion) < (request.getAmount())) {
                // return false;
                responseMap.put("approveStatus", false);
            } else {
                // return true;
                responseMap.put("approveStatus", true);
            }

            return responseMap;
        }catch (Exception ex){
            log.debug("===========LSF Validate for StockConcentration Exception");
            responseMap.put("allowedConcentration",0.0);
            responseMap.put("approveStatus", false);
            return responseMap;
        }
    }


    private LiquidityType checkForExistanceLiqType(List<LiquidityType> listOfLiq, LiquidityType typeToValidate, boolean addtoList) {
        for (LiquidityType lTye : listOfLiq) {
            if (lTye.getLiquidId() == typeToValidate.getLiquidId()) {
                return lTye;
            }
        }
        if (addtoList) {
            typeToValidate.setStockConcentrationPercent(0.0);
            listOfLiq.add(0, typeToValidate);
            return typeToValidate;
        } else {
            return null;
        }
    }

    public boolean disableTradingLSFTypeTradingAccount(String applicationID, String tradingAccountID, String exchange) {
        boolean result = true;
        CommonInqueryMessage message = new CommonInqueryMessage();
        message.setReqType(LsfConstants.TRADING_ENABLE_LSF_TRADING_ACOOUNT);
        message.setTradingAccountId(tradingAccountID);
        message.setExchange(exchange);
        message.setValue("0");
        CommonResponse enableTradingResponse = helper.processOMSCommonResponse(helper.portfolioRelatedOMS(gson.toJson(message)).toString());
        log.debug("===========LSF : Enable Trading , Trading Account ID:" + message.getTradingAccountId() + " , Application ID :" + applicationID + "OMS, Status :" + enableTradingResponse.getResponseCode());
        if (enableTradingResponse.getResponseCode() != 200) {
            enableTradingResponse.setErrorMessage("Trading Enable Failed");
            result = false;
        }
        return result;

    }


    public AccountDeletionRequestState closeLSFAccount(String applicationID, String fromTradingAccount, String toTradingAccount, String fromCashAccount, String toCashAccount) {
        CloseLSFAccountRequest closeLSFAccountRequest = new CloseLSFAccountRequest();
        closeLSFAccountRequest.setReqType(LsfConstants.LSF_TYPE_ACCOUNT_CLOSER);
        closeLSFAccountRequest.setFromTradingAccountId(fromTradingAccount);
        closeLSFAccountRequest.setToTradingAccountId(toTradingAccount);
        closeLSFAccountRequest.setFromCashAccountId(fromCashAccount);
        closeLSFAccountRequest.setToCashAccountId(toCashAccount);
        log.debug("===========LSF : Closing LSF Accounts :" + gson.toJson(closeLSFAccountRequest));
        String omsResponse = helper.omsCommonRequests(gson.toJson(closeLSFAccountRequest));
        CommonResponse commonResponse = helper.processOMSCommonResponseAccountDeletionRequest(omsResponse);
        AccountDeletionRequestState accountDeletionRequestState = new AccountDeletionRequestState();
        if (commonResponse.getResponseCode() == LsfConstants.REQUEST_SENT_TO_OMS) {
            accountDeletionRequestState.setIsSent(true);
            lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_SENT_TO_OMS);
        } else if (commonResponse.getResponseCode() == LsfConstants.REQUEST_DID_NOT_ACCEPTED_CASH_TRANSFER_FAILED) {
            accountDeletionRequestState.setIsSent(false);
            accountDeletionRequestState.setFailureReason("Cash Transfer Failed from OMS.Contact ABIC for more information");
            lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_CASH_TRANSFER);
        } else if (commonResponse.getResponseCode() == LsfConstants.REQUEST_DID_NOT_ACCEPTED_SHARE_TRANSFER_FAILED) {
            accountDeletionRequestState.setIsSent(false);
            accountDeletionRequestState.setFailureReason("Cash Transferred & Failed to Initialize Share Transfer in OMS.Contact ABIC for more information");
            lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_SHARE_TRANSFER);

        } else if (commonResponse.getResponseCode() == LsfConstants.REQUEST_DID_NOT_ACCEPTED_SELL_PENDING_AVAILABLE) {
            accountDeletionRequestState.setIsSent(false);
            accountDeletionRequestState.setFailureReason("Sell Pending Available,Failed to close accounts.Contact ABIC for more information");
            lsfRepository.updateActivity(applicationID, LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_SHARE_TRANSFER);
        }
        lsfRepository.updateAccountDeletionState(applicationID, commonResponse.getResponseCode());
        return accountDeletionRequestState;
    }

    private static boolean isEligibleForMarginNotification(String lastMarginNotificationDate) {
        if (lastMarginNotificationDate == null || lastMarginNotificationDate.equalsIgnoreCase("")) {
            return true;
        } else {
            //   Date today = new Date();
            try {
                Date lastDate = dateFormat.parse(lastMarginNotificationDate);
                String todayDate = dateFormat.format(new Date());
                if (dateFormat.parse(todayDate).compareTo(lastDate) > 0) {
                    return true;
                } else {
                    return false;
                }
            } catch (ParseException e) {

            }
            return true;
        }

    }

    public Object transferToLsfAccount(String applicationId, String orderId) {
        CommonResponse cmr = new CashAccountResponse();
        try {
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(applicationId);
            String lsfTypeAccount = collaterals.getLsfTypeTradingAccounts().get(0).getAccountId();
            String toExchange = collaterals.getLsfTypeTradingAccounts().get(0).getExchange();
            ShareTransferRequest request = new ShareTransferRequest();
            request.setReqType(LsfConstants.SHARE_TRANSFER_FINAL_ORDER);
            request.setBasketReference(orderId);
            request.setToTradingAccountId(lsfTypeAccount);
            request.setExchange(toExchange);
            cmr = performAction(request);
            if (cmr.getResponseCode() == 1) {
                lsfRepository.updateBasketTransferState(Integer.parseInt(orderId), LsfConstants.BASKET_TRANSFER_SENT);
            }
        } catch (Exception ex) {
            cmr.setResponseCode(-1);
        }
        return cmr;
    }

    public CommissionStructure getCommissionStructureBasedOnOrderValue(double amount) {
        List<CommissionStructure> commissionStructures = lsfRepository.getCommissionStructure();
        CommissionStructure applyingStructure = null;
        ProfitResponse profitResponse = new ProfitResponse();
        for (CommissionStructure commissionStructure : commissionStructures) {
            if ((commissionStructure.getFromValue() <= amount) && (amount <= commissionStructure.getToValue())) {
                applyingStructure = commissionStructure;
                break;
            }
        }
        return applyingStructure;
    }

    public boolean createAccounts(CommonInqueryMessage accountCreationRequest) {
        try {
            helper.sendMessageToOms(gson.toJson(accountCreationRequest));
            log.debug("===========LSF : Account Creation Request Successfully Sent to OMS :" + gson.toJson(accountCreationRequest));
            return true;
        } catch (Exception exception) {
            log.error("===========LSF : Account Creation Request Failed :" + gson.toJson(accountCreationRequest));
            return false;
        }
    }

    public void addContractDetailsToMap(String applicationID, Map<String, Object> requestMap) {
        orderContractSignedMap.put(applicationID, requestMap);
    }

    public void removeContractDetailsFromMap(String applicationID) {
        orderContractSignedMap.remove(applicationID);
    }

    public Map<String, Object> getOrderContractDetailMap(String applicationID) {
        return orderContractSignedMap.get(applicationID);
    }

    public int checkPendingOrdersForLSFTradingAccount(String tradingAccount, String cashAccount) {
        return 0;
    }

    public boolean updateActivity(String applicationId, int activityId) {
        if (lsfRepository.updateActivity(applicationId, activityId).equals("1")) {
            return true;
        }
        return false;
    }

    public double calculateVatAmt(double amountForVat) {
        double vatPercent = GlobalParameters.getInstance().getVatPercentage();
        return amountForVat * vatPercent / 100;
    }

    public int getNoOfOpenMurabahContracts(String customerId) {
        List<MurabahApplication> openAppList = lsfRepository.geMurabahAppicationUserIDFilteredByClosedApplication(customerId);
        return openAppList != null ? openAppList.size() : 0;
    }

    public int getnoOfPendingMurabahContrats(String customerId) {
        List<MurabahApplication> pendingList = lsfRepository.getNotGrantedApplication(customerId);
        return pendingList != null ? pendingList.size() : 0;
    }

    public OrderProfit calculateConditionalProfit(MApplicationCollaterals collaterals, PurchaseOrder purchaseOrder,int isFullProfit){
        // this total commisson should be taken from OMS pkg
        log.info("calculateConditionalProfit : is called isFullProfit  :" + isFullProfit + " Order Target Comm : " + purchaseOrder.getProfitAmount());
        OrderProfit profit = new OrderProfit();
        profit.setApplicationID(purchaseOrder.getApplicationId());

        double totalCommissionOMS = 0;
        if(collaterals.getLsfTypeTradingAccounts().size() > 0) {
            totalCommissionOMS =  getTotalCommissionFromOMS(collaterals.getLsfTypeTradingAccounts().get(0).getAccountId());

        }

        int dayCount = 0;
        if(isFullProfit==1){
            profit.setTargetCommission(purchaseOrder.getProfitAmount());
            profit.setTradedCommission(totalCommissionOMS);
            if( (totalCommissionOMS - purchaseOrder.getProfitAmount())>0 ){
                profit.setChargeCommission(false);
                profit.setChargeCommissionAmt(0);
                profit.setCumulativeProfitAmount(0);
            }else {
                profit.setChargeCommission(true);
                profit.setChargeCommissionAmt(purchaseOrder.getProfitAmount());
                profit.setCumulativeProfitAmount(purchaseOrder.getProfitAmount());
            }
        }else {

           dayCount =  LSFUtils.getDaysTillNowAfterSigned(purchaseOrder.getCustomerApprovedDate());
            double targetComm = (purchaseOrder.getProfitAmount()/(Integer.valueOf(purchaseOrder.getTenorId()) * 30)) * dayCount;
            profit.setTargetCommission(targetComm);
            profit.setTradedCommission(totalCommissionOMS);
            if(totalCommissionOMS > targetComm){
                profit.setChargeCommission(false);
                profit.setChargeCommissionAmt(0);
                profit.setCumulativeProfitAmount(0);
            }else {
                profit.setChargeCommission(true);
                profit.setChargeCommissionAmt(targetComm);
                profit.setCumulativeProfitAmount(targetComm);
            }
        }
        log.info("calculateConditionalProfit: Target Commi : " + profit.getTargetCommission() + ", TradedComm: " + profit.getTradedCommission() + " isComm Charge: "+profit.isChargeCommission() + " ChargeAmt: " + profit.getChargeCommissionAmt());
        return profit;
    }
    public double getTotalCommissionFromOMS(String exchangeAcc) {
        log.info("getTotalCommissionFromOMS : getting Total commission from OMS PROC, Exchange Acc: " + exchangeAcc);
        OMSCommission omsCommission = lsfRepository.getExchangeAccCommission(exchangeAcc);

        if (omsCommission != null) {
           return omsCommission.getBrokerCommission();
        } else {
            return 0;
        }


    }
    public Object updateHoldingsProcessor(MurabahApplication application) {
        log.debug("===========LSF : (updateHoldingsProcessor) applicationID :" + application.getId());
        CommonResponse response = new CommonResponse();
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        Map<String, Object> resultMap = new HashMap<>();
        MApplicationCollaterals mApplicationCollaterals = null;
        try {
            mApplicationCollaterals = lsfRepository.getApplicationCollateral(application.getId());
        } catch (Exception ex) {
            return null;
        }
        // get attached Marginability Group
        MarginabilityGroup marginabilityGroup = helper.getMarginabilityGroup(application.getMarginabilityGroup());
        List<LiquidityType> attachedLiqGoupList = null;
        List<SymbolMarginabilityPercentage> marginabilityPercentages = null;
        if (marginabilityGroup != null)
            marginabilityPercentages = marginabilityGroup.getMarginableSymbols();

        if (mApplicationCollaterals != null) {
            double totalCollateral = 0.0;
            double totalCashCollateral = 0.0;
            double totalPFCollateral = 0.0;
            /*-----Report-----*/
            double totalPFMarketValue = 0.0;
            double totalWeightedPFMarketValue = 0.0;
            String tradingAccId = null;
            /*-----Report-----*/
            // PF colletarals
            inqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
            inqueryMessage.setCustomerId(application.getCustomerId());
            inqueryMessage.setContractId(application.getId());
            Object result = helper.sendMessageToOms(gson.toJson(inqueryMessage));
            resultMap = gson.fromJson((String) result, resultMap.getClass());
            ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
            List<Symbol> symbols = new ArrayList<>();
            for (Map<String, Object> trd : lsfTrd) {
                if (trd.containsKey("shariaSymbols")) {
                    Map<String, Object> mpTRadingAcc = (Map<String, Object>) trd.get("tradingAccount");
                    tradingAccId = mpTRadingAcc.get("accountId").toString();
                    TradingAcc t = mApplicationCollaterals.isTradingAccountLSFTypeExist(tradingAccId);
                    t.setExchange(mpTRadingAcc.get("exchange").toString());
                    ArrayList<Map<String, Object>> symbolList = (ArrayList<Map<String, Object>>) trd.get("shariaSymbols");
                    for (Map<String, Object> symbol : symbolList) {
                        try {
                            Symbol smb = t.isSymbolExist(symbol.get("symbolCode").toString(), symbol.get("exchange").toString());
                            if (symbol.containsKey("shortDescription") && symbol.get("shortDescription") != null) {
                                smb.setShortDescription(symbol.get("shortDescription").toString());
                            }
                            if(symbol.containsKey("openBuyQty")){
                                smb.setOpenBuyQty(Integer.parseInt(symbol.get("openBuyQty").toString().split("\\.")[0]));
                            }
                            if(symbol.containsKey("openSellQty")){
                                smb.setOpenBuyQty(Integer.parseInt(symbol.get("openSellQty").toString().split("\\.")[0]));
                            }
                            smb.setAvailableQty(Math.round(Float.parseFloat(symbol.get("availableQty").toString())));
                            /*--Change for T+2 sell path--*/
                            smb.setAvailableQty(smb.getAvailableQty() + Math.round(Float.parseFloat(symbol.get("sellPending").toString())));
                            /*--*/
                            smb.setPreviousClosed(Double.parseDouble(symbol.get("previousClosed").toString()));
                            smb.setLastTradePrice(Double.parseDouble(symbol.get("lastTradePrice").toString()));
                            smb.setTransferedQty(smb.getAvailableQty());
                            LiquidityType attachedToSymbolLiq = helper.existingSymbolLiqudityType(smb.getSymbolCode(), smb.getExchange());
                            // set Default liqudity Type
                            smb.setLiquidityType(attachedToSymbolLiq);
                            // override if Applicaiton level Marginability group is attached with relevent Liquidity Type
                            if (attachedLiqGoupList != null) {
                                for (LiquidityType liq : attachedLiqGoupList) {
                                    if (liq.getLiquidId() == attachedToSymbolLiq.getLiquidId()) {
                                        smb.setLiquidityType(liq);
                                    }
                                }
                            }

                            if (marginabilityGroup != null)
                                smb.setMarginabilityPercentage(marginabilityGroup.getGlobalMarginablePercentage());

                            if(marginabilityPercentages != null) {
                                for(SymbolMarginabilityPercentage smp: marginabilityPercentages){
                                    if(smp.getSymbolCode().equals(smb.getSymbolCode()) && smp.getExchange().equals(smb.getExchange())){
                                        smb.setMarginabilityPercentage(smp.getMarginabilityPercentage());
                                    }
                                }
                            }

                            Double contribToColletaral = ((smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed())) / 100) * smb.getMarginabilityPercentage();
                            smb.setContibutionTocollateral(contribToColletaral);
                            totalPFCollateral += contribToColletaral;
                            totalCollateral += contribToColletaral;
                            symbols.add(smb);
                            /*-----Report-----*/
                            totalPFMarketValue = totalPFMarketValue + smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed());
                            totalWeightedPFMarketValue = totalWeightedPFMarketValue + contribToColletaral;
                            /*-----Report-----*/
                        } catch (Exception ex) {
                            log.info("===========LSF : (updateHoldingsProcessor) error Applicaiton id " + application.getId() + " in update holding processor " + ex.getMessage() + ", symbol:" + symbol.get("symbolCode").toString(),ex);
                        }
                    }
                } else {
                    log.info("===========LSF : (updateHoldingsProcessor) - invalid response from OMS shariaSymbols not found");
                }
            }
            /*---Setting totalPFMarketValue & security list for client Dash board summary----- */
            mApplicationCollaterals.setTotalPFMarketValue(totalPFMarketValue);
            mApplicationCollaterals.setSecurityList(symbols);
            if (totalPFMarketValue == 0) {
                mApplicationCollaterals.setTotalWeightedPFValue(0.0);
            } else {
                mApplicationCollaterals.setTotalWeightedPFValue(totalPFCollateral);
            }
            /*------*/
            lsfRepository.updateRevaluationInfo(tradingAccId, totalPFMarketValue, totalWeightedPFMarketValue);
            // cash Colletarals
            inqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_CASH_ACCOUNTS);
            /*-----Report-----*/
            double cashAccountBalance = 0.0;
            String cashAccountId = "";
            /*-----Report-----*/
            inqueryMessage.setCustomerId(application.getCustomerId());
            inqueryMessage.setContractId(application.getId());
            result = helper.sendMessageToOms(gson.toJson(inqueryMessage));
            resultMap = gson.fromJson((String) result, resultMap.getClass());
            ArrayList<Map<String, Object>> lsfCashAcc = (ArrayList<Map<String, Object>>) resultMap.get("responseObject");
            for (Map<String, Object> cashAcc : lsfCashAcc) {
                String cashAccId = cashAcc.get("accountNo").toString();
                CashAcc cashAcc1 = mApplicationCollaterals.isCashAccLSFTypeExist(cashAccId);
                cashAcc1.setCashBalance(Double.parseDouble(cashAcc.get("balance").toString()));
                totalCashCollateral += Double.parseDouble(cashAcc.get("balance").toString());
                totalCollateral += Double.parseDouble(cashAcc.get("balance").toString());
                cashAcc1.setAmountTransfered(cashAcc1.getCashBalance());
                cashAccountBalance = Double.parseDouble(cashAcc.get("balance").toString());
                cashAccountId = cashAccId;

                if (cashAcc.containsKey("blockedAmount")) {
                    totalCollateral += Double.parseDouble(cashAcc.get("blockedAmount").toString());
                }
                if (cashAcc.containsKey("pendingSettle")) {
                    cashAcc1.setPendingSettle(Double.parseDouble(cashAcc.get("pendingSettle").toString()));
                }
                if (cashAcc.containsKey("netReceivable")) {
                    cashAcc1.setNetReceivable(Double.parseDouble(cashAcc.get("netReceivable").toString()));
                }
                /*-----Report-----*/
                lsfRepository.updateRevaluationCashAccountRelatedInfo(cashAccountId, cashAcc1);
                /*-----Report-----*/
            }
            mApplicationCollaterals.setTotalCashColleteral(totalCashCollateral);
            mApplicationCollaterals.setTotalPFColleteral(totalPFCollateral);
            totalCollateral = totalCollateral + mApplicationCollaterals.getTotalExternalColleteral();
            mApplicationCollaterals.setNetTotalColleteral(totalCollateral);
            calculateOperativeLimit(mApplicationCollaterals);
            calculateRemainingOperativeLimit(mApplicationCollaterals);

            lsfRepository.updateCollateralWithCompleteTradingAcc(mApplicationCollaterals);

            response.setResponseCode(200);
            response.setResponseMessage("Holding update process completed");
            return mApplicationCollaterals;
        } else {
            response.setResponseCode(200);
            response.setResponseMessage("No Records Found");
            return null;

        }
    }
    public int getIsInRollOverPeriod(String settlementDate,int minRollOvrPrd,int maxRollOvrPrd){
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        Date currentDate = new Date();
        Date settlement ;
        int result = -1;
        try {
            settlement = df.parse(settlementDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int duration = (int) TimeUnit.MILLISECONDS.toMinutes((settlement.getTime() - currentDate.getTime()));
        if (duration <= maxRollOvrPrd && duration >= minRollOvrPrd){
            result = 1;
        }else {
            result = 0;
        }

        return result;
    }

    public ProfitResponse calculateProfit(int tenorId, double orderCompletedValue, double profitPercent) {
        CommonResponse cmr = new CommonResponse();
        ProfitResponse profitResponse = new ProfitResponse();
        try {

            if (GlobalParameters.getInstance().getProfitCalculateMethode() == LsfConstants.PROFIT_CALC_TENOR_BASED) {
                if (tenorId != -1) {
                    profitResponse = calculateProfitOnTenor(tenorId, orderCompletedValue, profitPercent);
                }
            } else {
                int loanPeriodInDays = 30 * tenorId;// days per month is taken as 30
                profitResponse = calculateProfitOnStructureSimple(
                        orderCompletedValue,
                        loanPeriodInDays,
                        profitPercent);
            }
            cmr.setResponseCode(200);
            cmr.setResponseObject(profitResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
            cmr.setResponseCode(500);
            cmr.setErrorMessage("Error on calculating Profit");
            cmr.setErrorCode(LsfConstants.ERROR_ERROR_ON_CALCULATING_PROFIT);
        }
        log.info("===========LSF : (calculateProfit)-LSF-SERVER RESPONSE  : {}", gson.toJson(cmr));
        return profitResponse;
    }
}
