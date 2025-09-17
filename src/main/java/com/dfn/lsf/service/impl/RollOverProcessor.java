package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.responseMsg.ProfitResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static com.dfn.lsf.util.LsfConstants.MESSAGE_TYPE_ML_ROLLOVER_PROCESS;

@Service
@MessageType(MESSAGE_TYPE_ML_ROLLOVER_PROCESS)
@RequiredArgsConstructor
@Slf4j
public class RollOverProcessor implements MessageProcessor {
    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;
    private final TransactionTemplate transactionTemplate;
    private final AuditLogProcessor auditLogProcessor;

    @Override
    public String process(final String request) {
        auditLogProcessor.process(request);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = gson.fromJson(request, resultMap.getClass());
        String subMessageType = resultMap.get("subMessageType").toString();
        return switch (subMessageType) {
            case "reqRollOver" -> processRollOver(resultMap);
            case "createRollOver" -> createRollOver(request);
            case "getRollOverAppDetails" -> getRollOverAppDetails(resultMap);
            default -> throw new IllegalArgumentException("Unknown subMessageType: " + subMessageType);
        };
    }

    private String getRollOverAppDetails(Map<String, Object> resultMap) {
        String appId = resultMap.get("appId").toString();
        MurabahApplication newApplication = lsfRepository.getMurabahApplication(appId);
        if (newApplication == null) {
            throw new IllegalArgumentException("Application not found for ID: " + appId);
        }
//        if (!newApplication.getFinanceMethod().equals("2")) {
//            throw new IllegalArgumentException("Application is not eligible for roll over: " + appId);
//        }
        RollOverSummeryResponse rollOverSummery = new RollOverSummeryResponse();
        var murabahProduct = lsfRepository.getMurabahaProduct(newApplication.getProductType());

        rollOverSummery.setAppId(newApplication.getId());
        rollOverSummery.setRollOverSeqNumber(newApplication.getRollOverSeqNumber());
        rollOverSummery.setOriginalAppId(newApplication.getRollOverAppId());

        rollOverSummery.setCustomerId(newApplication.getCustomerId());
        rollOverSummery.setCustomerName(newApplication.getFullName());

        rollOverSummery.setDate(newApplication.getDate());
        rollOverSummery.setProposalDate(newApplication.getProposalDate());
        rollOverSummery.setOverallStatus(Integer.parseInt(newApplication.getOverallStatus()));

        rollOverSummery.setCurrentLevel(newApplication.getCurrentLevel());

        double adminFee = GlobalParameters.getInstance().getComodityAdminFee();
        double vatAmount = lsfCore.calculateVatAmt(adminFee);

        rollOverSummery.setAdminFee(adminFee);
        rollOverSummery.setVatAmount(vatAmount);

        rollOverSummery.setTenor(newApplication.getTenor());

        rollOverSummery.setRequiredAmount(newApplication.getFinanceRequiredAmt());

        var collaterals = lsfRepository.getApplicationCompleteCollateralForRollOver(newApplication.getId());

        var tradingAccounts = helper.getLsfTypeTradingAccounts(newApplication.getCustomerId(), newApplication.getRollOverAppId(), newApplication.getMarginabilityGroup());
        if (tradingAccounts.isEmpty()) {
            throw new IllegalArgumentException("No trading accounts found for application ID: " + appId);
        }
        var totalPfValue = calculateTotalPfValue(tradingAccounts, collaterals.getTradingAccForColleterals());

        var lsfTypeCashAccounts = helper.getLsfTypeCashAccounts(newApplication.getCustomerId(), newApplication.getRollOverAppId());
        if (lsfTypeCashAccounts.isEmpty()) {
            throw new IllegalArgumentException("No cash accounts found for application ID: " + newApplication.getRollOverAppId());
        }
        var totalCashBalanceInLsfCash = lsfTypeCashAccounts.getFirst().getCashBalance();

        var cashAccounts = collaterals.getCashAccForColleterals().getFirst();
        var totalCashBalance = cashAccounts.getAmountAsColletarals();

        rollOverSummery.setLsfTypeCashAccounts(collaterals.getCashAccForColleterals());
        rollOverSummery.setLsfTypeTradingAccounts(collaterals.getTradingAccForColleterals());
        rollOverSummery.setTotalPfValue(totalPfValue);
        rollOverSummery.setTotalCashBalance(totalCashBalanceInLsfCash);
        rollOverSummery.setTotalCollateralValue(totalPfValue + totalCashBalance);

        rollOverSummery.setCashAccountId(newApplication.getCashAccount());
        rollOverSummery.setTradingAccountId(newApplication.getTradingAcc());

        rollOverSummery.setProductType(newApplication.getProductType());
        rollOverSummery.setFinanceMethod(newApplication.getFinanceMethod());
        rollOverSummery.setFacilityType(newApplication.getFacilityType());

        rollOverSummery.setProductName(murabahProduct.getProductName());
        rollOverSummery.setInitialRAPV(newApplication.getInitialRAPV());
        rollOverSummery.setApprovedLimit(collaterals.getApprovedLimitAmount());
        rollOverSummery.setEmail(newApplication.getEmail());
        rollOverSummery.setMobile(newApplication.getMobileNo());

        var profit = rollOverSummery.getRequiredAmount() * rollOverSummery.getProfitPercentage() * (Integer.parseInt(rollOverSummery.getTenor()) * 30) / 36000;

        rollOverSummery.setNewProfitAmount(profit);

        log.info("RollOver summery application processed with ID: {}", rollOverSummery.getAppId());
        return gson.toJson(rollOverSummery);
    }

    private String processRollOver(Map<String, Object> resultMap) {
        String appId = resultMap.get("appId").toString();
        MurabahApplication oldApplication = lsfRepository.getMurabahApplication(appId);
        if (oldApplication == null) {
            throw new IllegalArgumentException("Application not found for ID: " + appId);
        }
//        if (!oldApplication.getFinanceMethod().equals("2")) {
//            throw new IllegalArgumentException("Application is not eligible for roll over: " + appId);
//        }
// enabling Roll over for all finance methods as per new requirement on 21-08-2025
        List<PurchaseOrder> purchaseOrders = oldApplication.getFinanceMethod().equals("2") ?
                lsfRepository.getAllPurchaseOrderforCommodity(appId) :
                lsfRepository.getAllPurchaseOrder(oldApplication.getId());
        var po = purchaseOrders.getFirst();
        double originalLoanAmount = 0.0;
        double adminFee = 0.0;
        if (oldApplication.getFinanceMethod().equals("2")) {
            originalLoanAmount = calculateOriginalLoanAmount(po.getCommodityList());
        } else {
            originalLoanAmount = po.getOrderCompletedValue();
        }
        adminFee = GlobalParameters.getInstance().getComodityAdminFee();
        var tradingAccounts = helper.getLsfTypeTradingAccounts(oldApplication.getCustomerId(), appId, oldApplication.getMarginabilityGroup());
        if (tradingAccounts.isEmpty()) {
            throw new IllegalArgumentException("No trading accounts found for application ID: " + appId);
        }
        var totalPfValue = calculateTotalPfValue(tradingAccounts);
        var cashAccounts = helper.getLsfTypeCashAccounts(oldApplication.getCustomerId(), appId);
        if (cashAccounts.isEmpty()) {
            throw new IllegalArgumentException("No cash accounts found for application ID: " + appId);
        }
        var totalCashBalance = cashAccounts.getFirst().getCashBalance() - cashAccounts.getFirst().getBlockedAmount();
        var murabahProduct = lsfRepository.getMurabahaProduct(oldApplication.getProductType());

        RollOverSummeryResponse rollOverSummeryResponse = new RollOverSummeryResponse();
        rollOverSummeryResponse.setOriginalAppId(appId);
        rollOverSummeryResponse.setAppId("-1");
        rollOverSummeryResponse.setCustomerId(oldApplication.getCustomerId());
        rollOverSummeryResponse.setCustomerName(oldApplication.getFullName());
        rollOverSummeryResponse.setCurrentLevel(1);
        rollOverSummeryResponse.setOverallStatus(OverrallApprovalStatus.PENDING.statusCode());
        rollOverSummeryResponse.setTradingAccounts(tradingAccounts);
        rollOverSummeryResponse.setCashAccounts(cashAccounts);
        rollOverSummeryResponse.setTotalPfValue(totalPfValue);
        rollOverSummeryResponse.setTotalCashBalance(totalCashBalance);
        rollOverSummeryResponse.setTenor(oldApplication.getTenor());

        rollOverSummeryResponse.setOriginalSettlementAmount(po.getOrderSettlementAmount());
        rollOverSummeryResponse.setOriginalLoanAmount(originalLoanAmount);
        rollOverSummeryResponse.setOriginalProfitAmount(po.getProfitAmount());
        rollOverSummeryResponse.setMarginabilityGroup(oldApplication.getMarginabilityGroup());
        rollOverSummeryResponse.setFinanceMethod("2");  // all roll overs are commodity finance
        rollOverSummeryResponse.setProductType(oldApplication.getProductType());
        rollOverSummeryResponse.setProductName(murabahProduct.getProductName());
        rollOverSummeryResponse.setFacilityType(oldApplication.getFacilityType());


        double vatAmount = lsfCore.calculateVatAmt(adminFee);

        rollOverSummeryResponse.setAdminFee(adminFee);
        rollOverSummeryResponse.setVatAmount(vatAmount);
        rollOverSummeryResponse.setProfitPercentage(oldApplication.getProfitPercentage());

        if (oldApplication.getProductType() != 3) {
            var orderProfit = lsfRepository.getSummationOfProfitUntilToday(appId, po.getId());
            if (orderProfit != null) {
                rollOverSummeryResponse.setRequiredAmount(po.getOrderCompletedValue() + orderProfit.getCumulativeProfitAmount());
            } else {
                rollOverSummeryResponse.setRequiredAmount(po.getOrderSettlementAmount());
            }
        } else {
            var collaterals = lsfRepository.getApplicationCompleteCollateral(appId);
            OrderProfit profit = lsfCore.calculateConditionalProfit(collaterals,
                                                                    po,
                                                                    oldApplication.getDiscountOnProfit());

            rollOverSummeryResponse.setRequiredAmount(po.getOrderCompletedValue() + profit.getCumulativeProfitAmount());
        }
        rollOverSummeryResponse.setRequiredAmount(Math.round((rollOverSummeryResponse.getRequiredAmount()) * 100.0) / 100.0);
        var profit = rollOverSummeryResponse.getRequiredAmount() * oldApplication.getProfitPercentage() * (Integer.parseInt(oldApplication.getTenor()) * 30) / 36000;

        rollOverSummeryResponse.setNewProfitAmount(profit);
        rollOverSummeryResponse.setTotalCollateralValue(rollOverSummeryResponse.getTotalPfValue() + rollOverSummeryResponse.getTotalCashBalance());

        rollOverSummeryResponse.setApprovedLimit(rollOverSummeryResponse.getRequiredAmount());

        rollOverSummeryResponse.setRollOverSeqNumber(oldApplication.getRollOverSeqNumber() + 1);
        rollOverSummeryResponse.setEmail(oldApplication.getEmail());
        rollOverSummeryResponse.setMobile(oldApplication.getMobileNo());
        log.info("Creating roll over summary response for application ID: {}", appId);
        return gson.toJson(rollOverSummeryResponse);
    }

    private String createRollOver(String reqQuest) {
        CommonResponse cmnResponse = new CommonResponse();
        return transactionTemplate.execute(status -> {
            try {

                RollOverSummeryResponse newRollOverApp = gson.fromJson(reqQuest, RollOverSummeryResponse.class);
                var newApplication = processNewApplication(newRollOverApp);

                lsfRepository.initialAgreementStatus(Integer.parseInt(newApplication.getId()),
                                                     2,
                                                     newRollOverApp.getProductType(),
                                                     2);

                createCollaterals(newApplication, newRollOverApp);
                newApplication.setCurrentLevel(1);
                lsfRepository.updateMurabahApplication(newApplication);

                Map<String, Object> reqMap = gson.fromJson(reqQuest, Map.class);
                String approvedbyId = reqMap.get("customerId").toString();
                String approvedbyName = reqMap.get("customerName").toString();
                String statusChangedIP = reqMap.get("ipAddress").toString();
                String statusMessage = "Application has been rollovered";
                lsfRepository.commodityAppStatus(newRollOverApp.getOriginalAppId(), newRollOverApp.getCurrentLevel(), statusMessage, approvedbyId, approvedbyName, statusChangedIP);

                cmnResponse.setResponseCode(200);
                cmnResponse.setResponseMessage("Roll Over application created successfully.");
                cmnResponse.setResponseObject(newApplication);
                return gson.toJson(cmnResponse);
            } catch (Exception e) {
                status.setRollbackOnly();
                cmnResponse.setResponseCode(500);
                cmnResponse.setErrorMessage("Error creating roll over application: " + e.getMessage());
                return gson.toJson(cmnResponse);
            }
        });
    }

    private MurabahApplication processNewApplication(RollOverSummeryResponse rollOverSummeryResponse) throws Exception {
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        MurabahApplication newApplication = new MurabahApplication();
        MurabahApplication oldApplication = lsfRepository.getMurabahApplication(rollOverSummeryResponse.getOriginalAppId());

        Status status = new Status();
        status.setLevelId(1);
        status.setStatusId(OverrallApprovalStatus.PENDING.statusCode());
        status.setStatusDescription(OverrallApprovalStatus.PENDING.statusDescription());
        status.setStatusChangedDate(dateFormat.format(java.sql.Date.valueOf(LocalDate.now())));

        newApplication.setId("-1");
        newApplication.setCustomerId(rollOverSummeryResponse.getCustomerId());
        newApplication.setFullName(rollOverSummeryResponse.getCustomerName());
        newApplication.setDate(dateFormat.format(java.sql.Date.valueOf(LocalDate.now())));
        newApplication.setProposalDate(dateFormat.format(java.sql.Date.valueOf(LocalDate.now())));
        newApplication.addNewStatus(status);
        newApplication.setOverallStatus(Integer.toString(OverrallApprovalStatus.PENDING.statusCode()));

        newApplication.setCurrentLevel(1);
        newApplication.setAdminFeeCharged(0.0);
        newApplication.setTenor(rollOverSummeryResponse.getTenor());

        log.info("Processing new roll over application for App ID: {}, Finance Required Amt: {}", rollOverSummeryResponse.getOriginalAppId(), rollOverSummeryResponse.getRequiredAmount());
        newApplication.setFinanceRequiredAmt(rollOverSummeryResponse.getRequiredAmount());

        newApplication.setRollOverAppId(rollOverSummeryResponse.getOriginalAppId());
        newApplication.setRollOverSeqNumber(rollOverSummeryResponse.getRollOverSeqNumber());

        var cashAccount = rollOverSummeryResponse.getCashAccounts().getFirst();
        var lsfTradingAccount = rollOverSummeryResponse.getTradingAccounts().getFirst();
        newApplication.setCashAccount(cashAccount.getAccountId());

        newApplication.setTradingAcc(lsfTradingAccount.getAccountId());
        newApplication.setDibAcc(cashAccount.getAccountId());
        newApplication.setAvailableCashBalance(lsfTradingAccount.getAvailableCash());
        newApplication.setProductType(rollOverSummeryResponse.getProductType());
        newApplication.setFinanceMethod(rollOverSummeryResponse.getFinanceMethod());
        newApplication.setFacilityType(rollOverSummeryResponse.getFacilityType());
        newApplication.setProposedLimit(rollOverSummeryResponse.getApprovedLimit());
        newApplication.setProfitPercentage(rollOverSummeryResponse.getProfitPercentage());
        newApplication.setEmail(rollOverSummeryResponse.getEmail());
        newApplication.setMobileNo(rollOverSummeryResponse.getMobile());

        ProfitResponse profitResponse = lsfCore.calculateProfit(
                Integer.parseInt(rollOverSummeryResponse.getTenor()),
                newApplication.getFinanceRequiredAmt(),
                rollOverSummeryResponse.getProfitPercentage());

        rollOverSummeryResponse.setNewProfitAmount(profitResponse.getProfitAmount());
        //double initialRapv = rollOverSummeryResponse.getRequiredAmount() - rollOverSummeryResponse.getAdminFee() - rollOverSummeryResponse.getVatAmount();

        newApplication.setInitialRAPV(rollOverSummeryResponse.getTotalPfValue());
        newApplication.setMarginabilityGroup(oldApplication.getMarginabilityGroup());
        newApplication.setStockConcentrationGroup(oldApplication.getStockConcentrationGroup());
        newApplication.setAddress(oldApplication.getAddress());
        newApplication.setTradingAccExchange(lsfTradingAccount.getExchange());
        newApplication.setCashAccount(cashAccount.getAccountId());
        newApplication.setCustomerReferenceNumber(oldApplication.getCustomerReferenceNumber());
        newApplication.setZipCode(oldApplication.getZipCode());
        newApplication.setCity(oldApplication.getCity());
        newApplication.setPoBox(oldApplication.getPoBox());
        newApplication.setPreferedLanguage(oldApplication.getPreferedLanguage());
        newApplication.setDeviceType(rollOverSummeryResponse.getDeviceType());
        newApplication.setIpAddress(rollOverSummeryResponse.getIpAddress());


        String id = lsfRepository.updateMurabahApplication(newApplication);
        newApplication.setId(id);
        log.info("New RollOver application created with ID: {}", id);
        return newApplication;
    }

    private double calculateOriginalLoanAmount(List<Commodity> commodities) {
        return commodities.stream()
                .mapToDouble(Commodity::getBoughtAmnt)
                .sum();
    }

    private double calculateTotalPfValue(List<TradingAccOmsResp> lsfTradingAccList, List<TradingAcc> tradingAccList) {
        var tradingAccFromOms = lsfTradingAccList.getFirst();
        var totalPFValue = 0.0;
        var tradingAccFromDb = tradingAccList.getFirst();
        for (Symbol smb: tradingAccFromOms.getSymbolList()) {
            var smbFromDb = tradingAccFromDb.getSymbolsForColleteral().stream().filter(symbol -> symbol.getExchange().equals(smb.getExchange()) && symbol.getSymbolCode().equals(smb.getSymbolCode())).findFirst().orElse(null);
            if (smbFromDb != null) {
                double price = smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed();
                double contribToColletaral = ((smbFromDb.getColleteralQty() * price) / 100.0) * smb.getMarginabilityPercentage();
                smbFromDb.setMarginabilityPercentage(smb.getMarginabilityPercentage());
                smbFromDb.setContibutionTocollateral(contribToColletaral);
                smbFromDb.setLastTradePrice(smb.getLastTradePrice());
                smbFromDb.setPreviousClosed(smb.getPreviousClosed());

                totalPFValue += contribToColletaral;
            }
        }
        return totalPFValue;
    }

    private double calculateTotalPfValue(List<TradingAccOmsResp> lsfTradingAccList) {
        var tradingAccFromOms = lsfTradingAccList.getFirst();
        var totalPFValue = 0.0;
        for (Symbol smb: tradingAccFromOms.getSymbolList()) {
            double contribToColletaral = ((smb.getAvailableQty() * (smb.getLastTradePrice() > 0 ? smb.getLastTradePrice() : smb.getPreviousClosed())) / 100) * smb.getMarginabilityPercentage();
            totalPFValue += contribToColletaral;
        }
        return totalPFValue;
    }

    private void createCollaterals(final MurabahApplication application,final RollOverSummeryResponse rollOverSummeryResponse) throws Exception {

        String approvedBy = "";
        int approvedById = 0;

        log.info("=========LSF: creating Collaterals for OriginalAppId: {}, Roll Over : {}",application.getRollOverAppId(), application.getId());
        java.text.DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        MApplicationCollaterals collaterals = new MApplicationCollaterals();
        collaterals.setApplicationId(application.getId());
        collaterals.setAdminFee(rollOverSummeryResponse.getAdminFee());
        collaterals.setVatAmount(rollOverSummeryResponse.getVatAmount());
        collaterals.setApprovedLimitAmount(rollOverSummeryResponse.getRequiredAmount());

        double cashCollateral = application.getFinanceRequiredAmt() - rollOverSummeryResponse.getTotalPfValue() <= 0 ? 0 : application.getFinanceRequiredAmt() - rollOverSummeryResponse.getTotalPfValue();
        // add a complete log with financeRequiredAmt, totalPfValue, cashCollateral
        log.info("For Rollover of OriginalApplication: {}, RollOverAppId: {}, Finance Required Amount: {}, Total PF Value: {}, Cash Collateral: {}",
                 application.getRollOverAppId(),
                 application.getId(),
                 application.getFinanceRequiredAmt(),
                 rollOverSummeryResponse.getTotalPfValue(),
                 cashCollateral);

        collaterals.setNetTotalColleteral(cashCollateral + rollOverSummeryResponse.getTotalPfValue());
        if (rollOverSummeryResponse.getTotalCashBalance() > cashCollateral + rollOverSummeryResponse.getVatAmount() + rollOverSummeryResponse.getAdminFee()) {
            collaterals.setInitialCashCollaterals(cashCollateral + rollOverSummeryResponse.getVatAmount() + rollOverSummeryResponse.getAdminFee());
        } else {
            collaterals.setInitialCashCollaterals(rollOverSummeryResponse.getTotalCashBalance());
        }
        collaterals.setInitialPFCollaterals(rollOverSummeryResponse.getTotalPfValue());
        collaterals.setIsExchangeAccountCreated(true);

        rollOverSummeryResponse.getCashAccounts().forEach(cashAcc -> {
            cashAcc.setAmountAsColletarals(collaterals.getInitialCashCollaterals());
            cashAcc.setLsfType(true);
            cashAcc.setApplicationId(application.getId());
        });
        collaterals.setLsfTypeCashAccounts(rollOverSummeryResponse.getCashAccounts());

        List<TradingAcc> tradingAccList = Optional.ofNullable(rollOverSummeryResponse.getTradingAccounts())
                                                  .map(accounts -> accounts.stream()
                                                                           .map(tradingAccOmsResp ->
                                                                                {
                                                                                    TradingAcc tradingAcc = new TradingAcc();
                                                                                    tradingAcc.setExchange(tradingAccOmsResp.getExchange());
                                                                                    tradingAcc.setLsfType(true);
                                                                                    tradingAcc.setAccountId(tradingAccOmsResp.getAccountId());
                                                                                    tradingAcc.setSymbolsForColleteral(new ArrayList<>());
                                                                                    Optional.ofNullable(tradingAccOmsResp.getSymbolList())
                                                                                            .ifPresent(
                                                                                                    symbols -> symbols.forEach(Symbol -> {
                                                                                                        Symbol symbol = new Symbol();
                                                                                                        symbol.setSymbolCode(Symbol.getSymbolCode());
                                                                                                        symbol.setExchange(Symbol.getExchange());
                                                                                                        symbol.setAvailableQty(Symbol.getAvailableQty());
                                                                                                        symbol.setColleteralQty(symbol.getAvailableQty());
                                                                                                        symbol.setLastTradePrice(Symbol.getLastTradePrice());
                                                                                                        symbol.setPreviousClosed(Symbol.getPreviousClosed());
                                                                                                        double contribToColletaral = ((symbol.getAvailableQty() * (symbol.getLastTradePrice() > 0 ? symbol.getLastTradePrice() : symbol.getPreviousClosed())) / 100) * symbol.getMarginabilityPercentage();
                                                                                                        symbol.setContibutionTocollateral(contribToColletaral);
                                                                                                        symbol.setMarginabilityPercentage(Symbol.getMarginabilityPercentage());
                                                                                                        tradingAcc.getSymbolsForColleteral().add(symbol);
                                                                                                    }));
                                                                                    tradingAcc.setApplicationId(application.getId());
                                                                                    return tradingAcc; }).toList()).orElse(List.of());

        collaterals.setLsfTypeTradingAccounts(tradingAccList);
        collaterals.setUpdatedDate(dateFormat.format(java.sql.Date.valueOf(LocalDate.now())));
        collaterals.setRemainingOperativeLimitAmount(collaterals.getNetTotalColleteral());
        collaterals.setOpperativeLimitAmount(collaterals.getNetTotalColleteral());

        var blockResponse = lsfCore.blockCollaterals_RollOverAcc(collaterals, application);
        if (blockResponse) {
            collaterals.setStatus(1);
            collaterals.setStatusChangedBy("SYSTEM");
            collaterals.setIpAddress("127.0.0.1");
            collaterals.setStatusMessage("AUTOMATIC APPROVED");
            String collateralId = lsfRepository.addEditCompleteCollateral(collaterals, approvedBy, approvedById);
            collaterals.setId(collateralId);
            lsfRepository.changeStatusCollateral(collaterals);
            lsfRepository.updateActivity(application.getId(), LsfConstants.STATUS_COLLATERLS_SUBMITTED);
        } else {
            log.error("Failed to block collaterals for application ID: {}", application.getId());
            throw new Exception("Failed to block collaterals for application ID: " + application.getId());
        }
        notificationManager.sendNotification(application);
        lsfCore.initialValuation(collaterals.getApplicationId());
    }
}
