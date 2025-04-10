package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.model.requestMsg.CommonInqueryMessage;
import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.model.requestMsg.OrderBasket;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.Helper;
import com.dfn.lsf.util.LsfConstants;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Defined in OMSRequestHandlerCbr
 * route : DEPOSIT_WITHDRAW_RESPONSE_ROUTE
 * Handling Message types :
 * - DEPOSIT_SUCCESS_RESPONSE = 132;
 * - WITHDRAW_SUCCESS_RESPONSE = 133
 */
@Service
@RequiredArgsConstructor
@Qualifier("") //  Both 132, 133 come to this processor
public class DepositWithdrawResponseHandlingProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepositWithdrawResponseHandlingProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;

    @Override
    public String process(String request) {
        Map<String, Object> map = new HashMap<String, Object>();
        OMSQueueRequest omsRequest = gson.fromJson((String) request, OMSQueueRequest.class);
        map = gson.fromJson(request, map.getClass());
        int messageType = omsRequest.getMessageType();
        switch (messageType) {
            case LsfConstants.DEPOSIT_SUCCESS_RESPONSE:
                return processB2BDepositResponse(map);/*----------Deposit Success Response-----------*/
            case LsfConstants.WITHDRAW_SUCCESS_RESPONSE:
                return processB2BWithdrawResponse(map);/*----------Withdraw Success Response-------*/
            default:
                return null;
        }
    }

    public String processB2BDepositResponse(Map<String, Object> map) {
        String referenceNo = map.get("referenceNo").toString();
        //  int status = Integer.parseInt(map.get("status").g(0)/*.replace(".0","")*/);
        //String narration = map.get("narration").toString();
        double status = Double.parseDouble(map.get("status").toString());
        logger.info("===========LSF :Deposit Response Receive from , PoID: "
                    + referenceNo
                    + " , status :"
                    + status /*+ " , narration:" + narration */);

        PurchaseOrder po = lsfRepository.getPurchaseOrderByReference(referenceNo);
        if (status > 0) { // if deposit is success in both bank side and oms side
            OrderBasket orderBasket = createPOInstruction(po);
            if (setPOInstructionToOMS(orderBasket, po.getApplicationId())) {
                logger.info("===========LSF :Purchase Order Sent to OMS, PoID: " + po.getId());
                lsfRepository.updateDepositStatus(
                        referenceNo,
                        LsfConstants.RESPONSE_RECEIVED_B2B_SUCCESS,
                        LsfConstants.DEPOSIT);
                logger.info("Deposit Success Response ===" + lsfRepository.approveApplication(
                        1,
                        po.getApplicationId(),
                        "Deposit Success",
                        "SYSTEM",
                        "SYSTEM",
                        "127.0.0.1"));
            } else {
                logger.info("===========LSF :Failed to send Purchase Order to OMS, PoID: " + referenceNo);
            }
        } else { // if the deposit is failed in bank side
            lsfRepository.updateDepositStatus(
                    referenceNo,
                    LsfConstants.RESPONSE_RECEIVED_B2B_FAILED,
                    LsfConstants.DEPOSIT);
        }

       /* po.setApprovalStatus(approvalStatus);
        po.setApprovedById(approvedbyId);
        po.setApprovedByName(approvedbyName);*/
        return null;
    }

    private OrderBasket createPOInstruction(PurchaseOrder purchaseOrder) {
        OrderBasket orderBasket = new OrderBasket();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, 1);
        java.text.DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        orderBasket.setBasketReference(purchaseOrder.getId());
        orderBasket.setTradingAccountId(purchaseOrder.getTradingAccount());
        orderBasket.setCustomerId(purchaseOrder.getCustomerId());
        orderBasket.setExpiryDate(dateFormat.format(date.getTime()));
        orderBasket.setLoanAmount(purchaseOrder.getOrderValue());
        orderBasket.setSymbolList(purchaseOrder.getSymbolList());
        return orderBasket;
    }

    private boolean setPOInstructionToOMS(OrderBasket basket, String applicationId) {
        CommonInqueryMessage inqueryMessage = new CommonInqueryMessage();
        inqueryMessage.setReqType(LsfConstants.SEND_PO_INSTRUCTIONS);
        inqueryMessage.setLsfBasket(basket);
        inqueryMessage.setContractId(applicationId);
        Object response = helper.sendMessageToOms(gson.toJson(inqueryMessage));

        Map<String, Object> resMap = new HashMap<>();
        resMap = gson.fromJson(response.toString(), resMap.getClass());
        String s = resMap.get("responseObject").toString();
        String delimitter = "\\|\\|";
        String[] resultMap = s.split(delimitter);
        return resultMap[0].equals("1");
    }

    private String processB2BWithdrawResponse(Map<String, Object> map) {
        String referenceNo = map.get("referenceNo").toString();
        //if(referenceNo.contains("|")){
        double status = Double.parseDouble(map.get("status").toString()/*.replace(".0","")*/);
        //String narration = map.get("narration").toString();
        logger.info("===========LSF :Withdraw Response Receive from , referenceNo: "
                    + referenceNo
                    + " , status :"
                    + status /*+ " , narration:" + narration */);
        if (status > 0) {
            lsfRepository.updateDepositStatus(
                    referenceNo,
                    LsfConstants.RESPONSE_RECEIVED_B2B_SUCCESS,
                    LsfConstants.WITHDRAW);
        } else {
            lsfRepository.updateDepositStatus(
                    referenceNo,
                    LsfConstants.RESPONSE_RECEIVED_B2B_FAILED,
                    LsfConstants.WITHDRAW);
        }
        //}

        return null;
    }
}
