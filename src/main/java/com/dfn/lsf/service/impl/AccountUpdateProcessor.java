package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.AccountCreationRequest;
import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Defined in OMSRequestHandlerCbr
 * Handling Message types :
 * - EXCHANGE_ACCOUNT_DELETION_RESPONSE = 137;
 * - INVESTOR_ACCOUNT_CREATION_RESPONSE = 140
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountUpdateProcessor implements MessageProcessor {

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;

    @Override
    public String process(String request) {
        OMSQueueRequest omsRequest = gson.fromJson((String) request, OMSQueueRequest.class);
        switch (omsRequest.getMessageType()) {
            case LsfConstants.INVESTOR_ACCOUNT_CREATION_RESPONSE: {
                return processInvestorAccountResponse(omsRequest);
            }
            case LsfConstants.EXCHANGE_ACCOUNT_CREATION_RESPONSE: {
                return processExchangeAccountResponse(omsRequest);
            }
            case LsfConstants.EXCHANGE_ACCOUNT_DELETION_RESPONSE: {
                return processExchangeAccountDeletionResponse(omsRequest);
            }
        }
        return null;
    }

    private String processInvestorAccountResponse(OMSQueueRequest omsQueueRequest) {
        log.debug("===========LSF : Updating Investor Account Response, Cash Account ID :"
                     + omsQueueRequest.getCashAccNo()
                     + " , Status:"
                     + omsQueueRequest.getStatus()
                     + " Investor account:"
                     + omsQueueRequest.getInvestorAccount());
        //List<MurabahApplication> murabahApplications = lsfDaoI.geMurabahAppicationUserID(omsQueueRequest
        // .getMubasherNo());
        MurabahApplication murabahApplication = lsfRepository.getApplicationByCashAccount(
                omsQueueRequest.getCashAccNo(),
                1);

//        MurabahApplication murabahApplication= lsfDaoI.getApplicationByCashAccount(omsQueueRequest.getCashAccNo(),1);
        if (murabahApplication != null) {
            if (omsQueueRequest.getStatus() == 1) {
                // updating investor account of the
                lsfRepository.updateInvestorAcc(
                        omsQueueRequest.getCashAccNo(),
                        omsQueueRequest.getInvestorAccount(),
                        murabahApplication.getId());
                TradingAcc lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(
                        omsQueueRequest.getMubasherNo(),
                        murabahApplication.getId());

                if (lsfTradingAccount != null) {
                    if (murabahApplication != null) {
                        AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
                        createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
                        createExchangeAccount.setTradingAccountId(lsfTradingAccount.getAccountId());
                        createExchangeAccount.setExchange(lsfTradingAccount.getExchange());
                        String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(
                                createExchangeAccount));
                        log.debug("===========LSF : Creating Exchange Account for Trading Account :"
                                     + createExchangeAccount.getTradingAccountId()
                                     + " OMS Response  :"
                                     + omsResponseForExchangeAccountCreation);
                        CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(
                                omsResponseForExchangeAccountCreation);
                        if (exchangeAccountResponse.getResponseCode() == 1) {
                            lsfRepository.updateActivity(
                                    murabahApplication.getId(),
                                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
                        } else {
                            lsfRepository.updateActivity(
                                    murabahApplication.getId(),
                                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
                        }
                    }
                } else {
                    log.debug("===========LSF : LSF Type Trading Account not found for Cash Account :"
                                 + omsQueueRequest.getCashAccNo());
                }
            } else {
                lsfRepository.updateActivity(
                        murabahApplication.getId(),
                        LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATION_FAILED);
                log.debug("===========LSF : Updating Investor Account Failed, Cash Account ID :"
                             + omsQueueRequest.getCashAccNo()
                             + " , Status:"
                             + omsQueueRequest.getStatus());
            }
        }

        return null;
    }

    private String processExchangeAccountResponse(OMSQueueRequest omsQueueRequest) {
        log.debug("===========LSF : Updating Exchange Account Response, Trading Account ID :"
                     + omsQueueRequest.getTradingAccount()
                     + "Cash Account Number "
                     + omsQueueRequest.getCashAccNo()
                     + " ,Exchange Account"
                     + omsQueueRequest.getExchangeAccount()
                     + " , Status:"
                     + omsQueueRequest.getStatus());
        MurabahApplication application = lsfRepository.getApplicationByLSFTradingAccount(
                omsQueueRequest.getTradingAccount(),
                1);
        if (application == null) {
            application = lsfRepository.getApplicationByCashAccount(
                    omsQueueRequest.getCashAccNo(),
                    omsQueueRequest.getIsLsf());
        }
        if (omsQueueRequest.getStatus() == 1) {
            if (application != null && application.getCurrentLevel() != 18) {
                lsfRepository.updateExchangeAccount(
                        omsQueueRequest.getTradingAccount(),
                        omsQueueRequest.getExchangeAccount(),
                        application.getId(),
                        omsQueueRequest.getIsLsf());/*---Updating Exchange Account----*/
                List<PurchaseOrder> purchaseOrders = lsfRepository.getPurchaseOrderForApplication(application.getId());
                if (purchaseOrders != null && purchaseOrders.size() > 0) {
                    if (purchaseOrders.get(0).getOrderStatus()
                        == 2) { // order is filled and exchenge account creation comes after that
                        lsfRepository.updateActivity(
                                application.getId(),
                                LsfConstants.STATUS_PO_FILLED_WAITING_FOR_ACCEPTANCE);
                    }
                }
            } else {
                log.debug("===========LSF: Application Closed.");
            }
        } else {
            lsfRepository.updateActivity(application.getId(), LsfConstants.STATUS_EXCHANGE_ACCOUNT_CREATION_FAILED);
            log.debug("===========LSF : Creating Exchange Account Failed, Trading Account ID :"
                         + omsQueueRequest.getTradingAccount()
                         + " , Status:"
                         + omsQueueRequest.getStatus());
        }
        return null;
    }

//    public TradingAcc getLsfTypeTradingAccountForUser(String customerID) {
//        TradingAcc tradingAcc = new TradingAcc();
//        CommonInqueryMessage commonInqueryMessage = new CommonInqueryMessage();
//        commonInqueryMessage.setCustomerId(customerID);
//        commonInqueryMessage.setReqType(LsfConstants.GET_LSF_TYPE_TRADING_ACCOUNTS);
//        String result = (String) helper.sendSettlementRelatedOMSRequest(gson.toJson(commonInqueryMessage),
//        LsfConstants.HTTP_PRODUCER_OMS_REQ_GET_LSF_TYPE_TRADING_ACCOUNT);
//        Map<String, Object> resMap = new HashMap<>();
//        resMap = gson.fromJson(result, resMap.getClass());
//        ArrayList<Map<String, Object>> lsfTrd = (ArrayList<Map<String, Object>>) resMap.get("responseObject");
//        Map<String, Object> lsfTrdAccnt = (Map<String, Object>) lsfTrd.get(0).get("tradingAccount");
//        tradingAcc.setExchange(lsfTrdAccnt.get("exchange").toString());
//        tradingAcc.setAccountId(lsfTrdAccnt.get("accountId").toString());
//        return tradingAcc;
//    }

    private String processExchangeAccountDeletionResponse(OMSQueueRequest accountDeletionResponse) {
        if (accountDeletionResponse.getIsLsf()
            == 0) { // from account of the share transfer is a LSF type account during the collateral transfer
            log.debug("===========LSF : Share Transfer Failure during Collateral Transfer, Response Received :"
                         + gson.toJson(accountDeletionResponse));
            MurabahApplication murabahApplication =
                    lsfRepository.getApplicationByLSFTradingAccount(accountDeletionResponse.getExchangeAccount(),
                                                                                                    0); //getting non
            // lsf type collateral trading account
            if (murabahApplication != null) {
                lsfRepository.updateActivity(
                        murabahApplication.getId(),
                        LsfConstants.STATUS_COLLATERAL_SHARE_TRANSFER_FAILED_FROM_EXCHANGE);
                lsfRepository.updateSymbolTransferState(
                        accountDeletionResponse.getExchangeAccount(),
                        murabahApplication.getId(),
                        accountDeletionResponse.getSymbol(),
                        LsfConstants.SHARE_TRANSFER_FAILED_FROM_EXCHANGE);
            } else {
                if (accountDeletionResponse.getExchangeAccount()
                                           .equalsIgnoreCase(GlobalParameters.getInstance()
                                                                             .getInstitutionTradingAcc())) {//
                    // Failure of basket share transfer
                    PurchaseOrder purchaseOrder =
                            lsfRepository.getSinglePurchaseOrder(accountDeletionResponse.getBasketReference());
                    if (purchaseOrder != null) {
                        MurabahApplication tempMurabahApplication =
                                lsfRepository.getMurabahApplication(purchaseOrder.getApplicationId());
                        if (tempMurabahApplication != null) {
                            lsfRepository.updateBasketTransferState(
                                    Integer.parseInt(accountDeletionResponse.getBasketReference()),
                                    LsfConstants.BASKET_TRANSFER_FAILED);
                            lsfRepository.updateActivity(
                                    tempMurabahApplication.getId(),
                                    LsfConstants.STATUS_COLLATERAL_SHARE_TRANSFER_FAILED_FROM_EXCHANGE);
                        }
                    }
                }
            }
        } else if (accountDeletionResponse.getIsLsf()
                   == 1) {// from account of the share transfer is a non lsf type account during the account closer
            log.debug("===========LSF : Account Deletion Response Received :"
                         + gson.toJson(accountDeletionResponse));
            MurabahApplication murabahApplication =
                    lsfRepository.getApplicationByLSFTradingAccount(accountDeletionResponse.getExchangeAccount(),
                                                                                                    1);
            int activityID = 0;
            if (murabahApplication != null) {
                if (accountDeletionResponse.getStatus()
                    == LsfConstants.ACCOUNT_DELETION_SUCCESS) { // if account deletion is success from exchange
                    activityID = LsfConstants.STATUS_ACCOUNT_DELETION_SUCCEED_FROM_OMS;
                    if (murabahApplication != null) {
                        List<PurchaseOrder> purchaseOrders = lsfRepository.getPurchaseOrderForApplication(
                                murabahApplication.getId());
                        if (purchaseOrders.size() > 0) {
                            PurchaseOrder purchaseOrder = purchaseOrders.get(0);
                            log.info("===========LSF : Moving Application to close state, Application ID :"
                                        + murabahApplication.getId());
                            lsfCore.moveToCashTransferredClosedState(
                                    murabahApplication.getId(),
                                    "Settled",
                                    purchaseOrder.getId());//updating application*/
                        }
                    }
                } else if (accountDeletionResponse.getStatus()
                           == LsfConstants.EXCHANGE_ACCOUNT_DELETION_FAILED_FROM_EXCHANGE) {
                    activityID = LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_WITH_EXCHANGE;
                } else if (accountDeletionResponse.getStatus() == LsfConstants.SHARE_TRANSFER_FAILED_WITH_EXCHANGE) {
                    activityID =
                            LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_DUE_TO_SHARE_TRANSFER_FAILURE_WITH_EXCHANGE;
                } else if (accountDeletionResponse.getStatus()
                           == LsfConstants.REQUEST_DID_NOT_ACCEPTED_SELL_PENDING_AVAILABLE) {
                    activityID =
                            LsfConstants.STATUS_ACCOUNT_DELETION_REQUEST_FAILED_DUE_TO_SHARE_TRANSFER_FAILURE_WITH_EXCHANGE;
                }
                lsfRepository.updateActivity(murabahApplication.getId(), activityID);
            }
        }

        return null;
    }
}
