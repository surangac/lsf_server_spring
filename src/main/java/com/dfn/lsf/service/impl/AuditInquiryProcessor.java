package com.dfn.lsf.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.ApplicationStatus;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.responseMsg.CustomerDetailedInfo;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("23")
public class AuditInquiryProcessor implements MessageProcessor {

    private final Gson gson;
    private final LSFRepository lsfRepository;

    @Override
    public  String process(String request) {
        log.info("Processing audit inquiry request: {}", request);
        Map<String, Object> requestMap = gson.fromJson(request, new TypeToken<Map<String, Object>>() {}.getType());
        String requestType = (String) requestMap.get("requestType");
        String response = null;

        switch (requestType) {
            case LsfConstants.GET_APPLICATION_STATUS_SUMMARY: {//application summary status
                return getApplicationStatusSummary();
            }
            case LsfConstants.GET_CUSTOMER_SUMMARY_INFO: /*-----Get Customer Details for Order Contract-----*/
                return getCustomerSummaryInfo(requestMap);
            default:
                log.error("Invalid request type: {}", requestType);
                return null;
        }
    }

    private String getApplicationStatusSummary() {
        log.debug("===========LSF : (getApplicationStatusSummary)-REQUEST ");
        List<ApplicationStatus> applicationStatusList = lsfRepository.applicationStatusSummary();
        for (ApplicationStatus applicationStatus : applicationStatusList) {
            if( applicationStatus.getOverallStatus() > 0){
                if (applicationStatus.getCurrentLevel() == 18) {
                    if (applicationStatus.getLiquidatedStatus() != null && applicationStatus.getLiquidatedStatus().equalsIgnoreCase("1")) {
                        applicationStatus.setSettlementDescription("Liquidated Due to PO Not Acceptance");
                    } else if (applicationStatus.getCustomerApproveStatus() != null && applicationStatus.getCustomerApproveStatus().equalsIgnoreCase("-1")) {
                        applicationStatus.setSettlementDescription("Order Agreement Rejected");
                    } else {
                        applicationStatus.setSettlementDescription("Settled");
                    }

                }
                if (applicationStatus.getCurrentLevel() == 14) {
                    applicationStatus.setSettlementDescription("Waiting for PO Fill");
                }
                if (applicationStatus.getCurrentLevel() == 15 && !applicationStatus.getOrderFilledStatus().equalsIgnoreCase("2")) {
                    applicationStatus.setSettlementDescription("Waiting for Order Agreement Approval");
                }

                if(applicationStatus.getCurrentLevel() == 16 && applicationStatus.getOrderFilledStatus().equalsIgnoreCase("2")){
                    switch (applicationStatus.getCustomerActivityID()){
                        case(LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_CASH_TRANSFER):{
                            applicationStatus.setSettlementDescription("Account deletion request failed in OMS due to cash transfer failure.");
                        }
                        case(LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_TO_SEND_OMS_DUE_TO_SHARE_TRANSFER):{
                            applicationStatus.setSettlementDescription("Account deletion request failed in OMS due to holding transfer failure.");
                        }
                        case(LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_SENT_TO_OMS):{
                            applicationStatus.setSettlementDescription("Account deletion request sent to OMS");
                        }
                        case(LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_DUE_TO_SHARE_TRANSFER_FAILURE_WITH_EXCHANGE):{
                            applicationStatus.setSettlementDescription("Account deletion request failed, due to holding transfer failure in exchange.");
                        }
                        case(LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_WITH_EXCHANGE):{
                            applicationStatus.setSettlementDescription("Account deletion request failed in exchange.");
                        }
                        default:{
                            applicationStatus.setSettlementDescription("Order Contract Signed & Active");
                        }
                    }
                }
            }else {
                applicationStatus.setSettlementDescription("Rejected ");
                if(applicationStatus.getOverallStatus() == -14){
                    applicationStatus.setStatusDescription("PO Cancelled & Closed.");
                }
            }


        }
        log.debug("===========LSF : (getApplicationStatusSummary)-RESPONSE :" + gson.toJson(applicationStatusList));
        return gson.toJson(applicationStatusList);
    }

    private String getCustomerSummaryInfo(Map<String, Object> map) {
        String applicationID = map.get("applicationID").toString();
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationID);
        CustomerDetailedInfo customerDetailedInfo = new CustomerDetailedInfo();
        if(murabahApplication != null){
            customerDetailedInfo.setCustomerID(murabahApplication.getCustomerId());
            customerDetailedInfo.setApplicationID(murabahApplication.getId());
            customerDetailedInfo.setFullName(murabahApplication.getFullName());
            customerDetailedInfo.setMobileNumber(murabahApplication.getMobileNo());
            customerDetailedInfo.setEmail(murabahApplication.getEmail());
            customerDetailedInfo.setTradingAccount(murabahApplication.getTradingAcc());
            customerDetailedInfo.setCashAccount(murabahApplication.getCashAccount());
            customerDetailedInfo.setProposalDate(murabahApplication.getProposalDate());
            customerDetailedInfo.setInitialRAPV(murabahApplication.getInitialRAPV());
            customerDetailedInfo.setFinanceRequiredAmount(murabahApplication.getFinanceRequiredAmt());
            customerDetailedInfo.setProposedLimit(murabahApplication.getProposedLimit());
            MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateral(murabahApplication.getId());
            customerDetailedInfo.setLsfTradingAccount(mApplicationCollaterals.getLsfTypeTradingAccounts().size() > 0 ? mApplicationCollaterals.getLsfTypeTradingAccounts().get(0).getAccountId() : null);
            customerDetailedInfo.setLsfCashAccount(mApplicationCollaterals.getLsfTypeCashAccounts().size() > 0 ? mApplicationCollaterals.getLsfTypeCashAccounts().get(0).getAccountId() : null);
            customerDetailedInfo.setPfCollateralList(mApplicationCollaterals.getTradingAccForColleterals().size() > 0 ? mApplicationCollaterals.getTradingAccForColleterals().get(0).getSymbolsForColleteral() : null);
            customerDetailedInfo.setInitialPFCollaterals(mApplicationCollaterals.getInitialPFCollaterals());
            customerDetailedInfo.setCashCollateral(mApplicationCollaterals.getInitialCashCollaterals());
            customerDetailedInfo.setCashCollateralList(mApplicationCollaterals.getCashAccForColleterals());
            customerDetailedInfo.setTotalCollateralValue(mApplicationCollaterals.getInitialCashCollaterals() + mApplicationCollaterals.getInitialPFCollaterals());
            List<PurchaseOrder> orders = lsfRepository.getAllPurchaseOrder(murabahApplication.getId());
            for(PurchaseOrder purchaseOrder : orders){
                if(purchaseOrder.getApplicationId().equalsIgnoreCase(mApplicationCollaterals.getApplicationId()) ){
                    purchaseOrder.setTotalOutStandingBalance(mApplicationCollaterals.getOutstandingAmount());
                }
            }
            customerDetailedInfo.setPurchaseOrders(orders);
            if (customerDetailedInfo.getPurchaseOrders().size() > 0) {
                customerDetailedInfo.setSettled(customerDetailedInfo.getPurchaseOrders().get(0).getSettlementStatus() == 1 ? true : false);
                customerDetailedInfo.setSettlementDate(formatDate(customerDetailedInfo.getPurchaseOrders().get(0).getSettlementDate()));
                customerDetailedInfo.setSettledDate(customerDetailedInfo.getPurchaseOrders().get(0).getSettledDate());
            }
            customerDetailedInfo.setStatusList(lsfRepository.getApplicationStatus(murabahApplication.getId()));
            customerDetailedInfo.setDailyFtvList(lsfRepository.getFTVsummaryForDashBoard(murabahApplication.getId()));
        }
            return gson.toJson(customerDetailedInfo);
    }

    private String formatDate(String settlementDate) {
        String formattedDate = "";
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat sm = new SimpleDateFormat("MM/dd/yyyy");
        int difference = 0;
        try {
            Date settlement = df.parse(settlementDate);
            formattedDate = sm.format(settlement);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
