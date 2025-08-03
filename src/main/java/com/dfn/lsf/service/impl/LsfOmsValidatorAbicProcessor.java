package com.dfn.lsf.service.impl;

import java.util.List;
import java.util.Map;

import com.dfn.lsf.model.*;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.model.responseMsg.OMSQueueResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.Gson;


@Slf4j
@Service
@RequiredArgsConstructor
public class LsfOmsValidatorAbicProcessor {

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCoreService;

    public OMSQueueResponse process(OMSQueueRequest request) {
        
        OMSQueueResponse response = new OMSQueueResponse();
        log.info("Processing core operation request with subMessageType: {}", request.getMessageType());

        switch (request.getMessageType()) {
            case LsfConstants.APPROVE_ORDER_FOR_FTV: {
                log.debug("===========LSF :Validating Order");
                response.setReqType(LsfConstants.RESPONSE_APPROVE_ORDER_FOR_FTV);
                validateFtvForFirstMargineCall(request, response);
                break;
            }
            case LsfConstants.APPROVE_WITHDRAW_FOR_FTV: {
                log.debug("===========LSF :Validating Withdraw");
                response.setReqType(LsfConstants.RESPONSE_APPROVE_WITHDRAW_FOR_FTV);
                validateFTVForAgreedLimit(request, response);
                break;
            }
            case LsfConstants.RIA_LOGOUT_RESPONSE: {
                log.debug("===========LSF :Handling Ria Log Out :" + gson.toJson(request));
                handleRIALogOut(request);
                break;
            }
            case LsfConstants.TRADE_HOLDING_UPDATE_RESPONSE: {
                log.debug("===========LSF : Updating Holdings :" + gson.toJson(request));
                updateHoldings(request,response);
                break;
            }
        }
        return response;
    }

    private void validateFtvForFirstMargineCall(OMSQueueRequest request, OMSQueueResponse response) {
        //List<MurabahApplication> applicationList = lsfRepository.geMurabahAppicationUserID(request.getCustomerId());
        MurabahApplication application = lsfRepository.getMurabahApplication(request.getContractId());
        response.setParams(request.getPendingId());
        if (application == null) {
            response.setApproved(false);
            response.setRejectCode(1);
            log.debug("===========LSF : Application not found for contract id :" + request.getContractId());
            return;
        }
        MApplicationCollaterals collaterals = lsfCoreService.reValuationProcess(application,true);
        log.debug("===========LSF : Current FTV :" + collaterals.getFtv() + " , Current Outstanding Balance :" + collaterals.getOutstandingAmount() + " , Order Value :" + request.getAmount());
        log.debug("===========LSF :Order Details  Symbol :" + request.getSymbol() + " , Price :" + request.getPrice() + " , Quantity :" + request.getQuantity());

        //if ((request.getAmount() > collaterals.getTotalCashColleteral()) || (!validateWithSettlementDate(application.getId()))) { // if the order value is greater than available cash in LSF cash account
        if (!validateWithSettlementDate(application.getId())) { // if the order value is greater than available cash in LSF cash account
            response.setApproved(false);
        } else {
            String marginabilityGroupId = application.getMarginabilityGroup();
            List<LiquidityType> attachedLiqGoupList = null;
            MarginabilityGroup marginabilityGroup = null;
            List<SymbolMarginabilityPercentage> symbolMarginabilityPercentages = null;
            if (marginabilityGroupId != null) {
                marginabilityGroup = helper.getMarginabilityGroup(application.getMarginabilityGroup());
                if(marginabilityGroup != null) {
                    attachedLiqGoupList = marginabilityGroup.getMarginabilityList();
                    symbolMarginabilityPercentages = marginabilityGroup.getMarginableSymbols();
                }
            }
            double symbolMarginabilityPercentage = 0.0;
            if (marginabilityGroup != null) {
                symbolMarginabilityPercentage = marginabilityGroup.getGlobalMarginablePercentage();
            }

            if(symbolMarginabilityPercentages != null) {
                for(SymbolMarginabilityPercentage smp :symbolMarginabilityPercentages) {
                    if(smp.getSymbolCode().equals(request.getSymbol()) && smp.getExchange().equals(request.getExchange())){
                        symbolMarginabilityPercentage = smp.getMarginabilityPercentage();
                    }
                }
            }
            double weightedOrderValue = request.getAmount() * symbolMarginabilityPercentage / 100;

            // changed by Suranga on 2020/09/23 based on CR RD-ABIC-2020006-159- ML multiple order projected coverage-V1 1
            // 1. avoid adding total block amount to Net Total Colleteral at colleteral calculation logic
            // 2. projected block amount will be calculated based on the available block amount values for each symbol
            String openOrderValues = request.getOpenOrderValues();
            double weightedOpenOrderValue = 0.0;
            if(!openOrderValues.isEmpty() && openOrderValues.length() > 0 ) {
                log.debug("===========LSF : OpenOrder String :" + openOrderValues);
                String[] openOrders = openOrderValues.split("\\|");
                for (String s : openOrders) {
                    String[] dtl = s.split("=");
                    String smbl = dtl[0].toString();
                    double ordValue = Double.parseDouble(dtl[1].toString());
                    log.debug("===========LSF : OpenOrder Symbol :" + smbl + " Open Order Value " + ordValue);
                    LiquidityType smbLiqType = helper.existingSymbolLiqudityType(smbl, request.getExchange());
                    List<LiquidityType> liqGroupList = null;
                    LiquidityType assSmbLiquidityType = null;
                    if (marginabilityGroup != null) {
                        liqGroupList = marginabilityGroup.getMarginabilityList();
                        if (liqGroupList != null) {
                            for (LiquidityType liq : liqGroupList) {
                                if (liq.getLiquidId() == smbLiqType.getLiquidId()) {
                                    assSmbLiquidityType = liq;
                                }
                            }
                            if(assSmbLiquidityType != null){
                                weightedOpenOrderValue = ordValue * assSmbLiquidityType.getMarginabilityPercent() / 100;
                            }
                        }
                    }
                }
                log.debug("===========LSF : OpenOrder weightedOpenOrderValue :" + weightedOpenOrderValue);
            }

            //collaterals.setNetTotalColleteral(collaterals.getNetTotalColleteral() - Math.abs(request.getAmount()) + weightedOrderValue);
            collaterals.setNetTotalColleteral(collaterals.getNetTotalColleteral() - Math.abs(request.getAmount()) + weightedOrderValue + weightedOpenOrderValue);
            log.debug("===========LSF :Weighted Order Value:" + weightedOrderValue + " New Net Colletreal :" + collaterals.getNetTotalColleteral());
            lsfCoreService.calculateFTV(collaterals);  
            log.debug("===========LSF : Expected FTV :" + collaterals.getFtv() + " , First Margin Level :" + GlobalParameters.getInstance().getFirstMarginCall());
            if (lsfCoreService.violateFTVwithFirstMarginLimit(collaterals)) {
                response.setRejectCode(1);
                response.setApproved(false);
            } else {
                response.setRejectCode(1);
                response.setApproved(true);
                log.debug("===========LSF : Concentration Validation Disabled");

            }
        }
        log.debug("===========LSF :LSF Response to OMS Validation Message :" + gson.toJson(response));
        helper.OMSValidationResponseRelated(gson.toJson(response));
    }

    private void validateFTVForAgreedLimit(OMSQueueRequest request, OMSQueueResponse response) {
        MurabahApplication application = null;
        try {
            application = lsfRepository.getMurabahApplication(request.getContractId());
        }catch (Exception e){
            log.error("Error occurs in getMurabahApplication for application id : " + request.getContractId());
        }
        response.setParams(request.getPendingId());
        if (application == null) {
            response.setApproved(false);
        }
        MApplicationCollaterals collaterals = lsfCoreService.reValuationProcess(application,true);
        log.debug("===========LSF : Current FTV :" + collaterals.getFtv() + " , Current Outstanding Balance :" + collaterals.getOutstandingAmount());
        collaterals.setNetTotalColleteral(collaterals.getNetTotalColleteral() - Math.abs(request.getAmount()));
        lsfCoreService.calculateFTV(collaterals);
        log.debug("===========LSF : Expected FTV :" + collaterals.getFtv() + " , Agreed Limit :" + GlobalParameters.getInstance().getAgreedLimit());
        if (collaterals.getFtv() <= GlobalParameters.getInstance().getAgreedLimit()) {
            response.setApproved(false);
        } else {
            response.setApproved(true);
        }
        log.debug("===========LSF :LSF Response to OMS Validation Message :" + gson.toJson(response));
        helper.OMSValidationResponseRelated(gson.toJson(response));
    }

    private Object handleRIALogOut(OMSQueueRequest request) {
        log.debug("===========LSF :Handling Ria Log Out, SessionID :" + gson.toJson(request.getSessionID()));
        return "Done";
    }

   private boolean validateWithSettlementDate(String applicationID){
       List<PurchaseOrder> orders= lsfRepository.getAllPurchaseOrder(applicationID);
        int dateDiff=LSFUtils.getDaysToSettlement(orders.getFirst().getSettlementDate());
        if (dateDiff==0||dateDiff>0) {
            return true;
        }else {
            return false;
        }
    }

    private void updateHoldings(OMSQueueRequest request, OMSQueueResponse response){
        MurabahApplication application = lsfRepository.getMurabahApplication(request.getContractId());
        response.setParams(request.getPendingId());
        if (application == null) {
            response.setApproved(false);
        }

        MApplicationCollaterals collaterals=(MApplicationCollaterals)lsfCoreService.updateHoldingsProcessor(application);
        if (collaterals == null) {
            response.setApproved(false);
        } else {
            response.setApproved(true);
        }
        log.debug("===========LSF :LSF Response to OMS Validation Message :" + gson.toJson(response));
        helper.OMSValidationResponseRelated(gson.toJson(response));
    }

}
