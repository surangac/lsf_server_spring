package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.TradingAcc;
import com.dfn.lsf.model.requestMsg.AccountCreationRequest;
import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.MessageType;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.dfn.lsf.util.LsfConstants.INVESTOR_ACCOUNT_CREATION_RESPONSE;

@Service
@MessageType(INVESTOR_ACCOUNT_CREATION_RESPONSE)
@RequiredArgsConstructor
@Slf4j
public class InvestorAccountCreationProcessor implements MessageProcessor {

    private final Gson gson;

    private final LSFRepository lsfRepository;

    private final Helper helper;

    private final LsfCoreService lsfCore;

    @Override
    public String process(final String request) {
        OMSQueueRequest omsRequest = gson.fromJson(request, OMSQueueRequest.class);
        return processInvestorAccountResponse(omsRequest);
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

    public String manualCreationExchangeAccount(String applicationId, String accontId) {
        log.info("Manual creation of exchange account for application ID: {}", applicationId);
        MurabahApplication murabahApplication = lsfRepository.getMurabahApplication(applicationId);
        TradingAcc lsfTradingAccount = lsfCore.getLsfTypeTradinAccountForUser(
                murabahApplication.getCustomerId(),
                murabahApplication.getId());
        String tradingAccountId = accontId;
        if (lsfTradingAccount != null) {
            tradingAccountId = lsfTradingAccount.getAccountId() != null ? lsfTradingAccount.getAccountId() : accontId;
        }

        AccountCreationRequest createExchangeAccount = new AccountCreationRequest();
        createExchangeAccount.setReqType(LsfConstants.CREATE_EXCHANGE_ACCOUNT);
        createExchangeAccount.setTradingAccountId(tradingAccountId);
        createExchangeAccount.setExchange("TDWL");
        String omsResponseForExchangeAccountCreation = helper.cashAccountRelatedOMS(gson.toJson(
                createExchangeAccount));
        CommonResponse exchangeAccountResponse = helper.processOMSCommonResponseAccountCreation(
                omsResponseForExchangeAccountCreation);
        log.info("===========LSF : Creating Exchange Account for Trading Account Response: {}", exchangeAccountResponse);
        if (exchangeAccountResponse.getResponseCode() == 1) {
            lsfRepository.updateActivity(
                    murabahApplication.getId(),
                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_AND_SENT_EXCHANGE_ACCOUNT_CREATION);
        } else {
            lsfRepository.updateActivity(
                    murabahApplication.getId(),
                    LsfConstants.STATUS_INVESTOR_ACCOUNT_CREATED_FAILED_TO_SUBMIT_EXCHANGE_ACCOUNT_CREATION);
        }
        return gson.toJson(exchangeAccountResponse);
    }
}
