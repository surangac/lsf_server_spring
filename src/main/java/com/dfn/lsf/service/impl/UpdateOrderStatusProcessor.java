package com.dfn.lsf.service.impl;

import com.dfn.lsf.model.*;
import com.dfn.lsf.model.requestMsg.OMSQueueRequest;
import com.dfn.lsf.model.responseMsg.OrderStatusResponse;
import com.dfn.lsf.model.responseMsg.ProfitResponse;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.service.MessageProcessor;
import com.dfn.lsf.util.*;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.dfn.lsf.util.LsfConstants.UPDATE_ORDER_STATUS_PROCESS;

/**
 * Defined in OMSRequestHandlerCbr
 * route : OMS_ACTIVITY_ROUTE
 * Handling Message types :
 * - UPDATE_ORDER_STATUS_PROCESS = 120;
 */
@Service
@MessageType(UPDATE_ORDER_STATUS_PROCESS)
@RequiredArgsConstructor
public class UpdateOrderStatusProcessor {

    private static final Logger logger = LoggerFactory.getLogger(UpdateOrderStatusProcessor.class);

    private final Gson gson;
    private final LSFRepository lsfRepository;
    private final Helper helper;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;

    public String process(OMSQueueRequest omsRequest) {
        OrderStatusResponse statusResponse = new OrderStatusResponse();
        statusResponse.setOrderId(omsRequest.getCorrelationId());
        statusResponse.setOrderStatus(omsRequest.getStatus());
        statusResponse.setCompletedOrderValue(LSFUtils.ceilTwoDecimals(omsRequest.getFilledValue()));
        statusResponse.setVatAmount(LSFUtils.ceilTwoDecimals(omsRequest.getVat()));

        PurchaseOrder purchaseOrder =
                lsfRepository.getSinglePurchaseOrder(Integer.toString(statusResponse.getOrderId()));
        String response = "-1";

        String approvedBy = "";
        int approvedById = 0;

        if (statusResponse.getCompletedOrderValue() > 0) { /*---Update Order Completed Details---*/
            MurabahApplication application = lsfRepository.getMurabahApplication(purchaseOrder.getApplicationId());

            /*---Updating OutStanding Amount-----*/
            MApplicationCollaterals collaterals = lsfRepository.getApplicationCompleteCollateral(application.getId());
            collaterals.setOutstandingAmount(LSFUtils.ceilTwoDecimals(omsRequest.getFilledValue()));
            lsfRepository.addEditCollaterals(collaterals, approvedBy, approvedById);
            /*--------*/

            ProfitResponse profitResponse = lsfCore.calculateProfit(
                    Integer.parseInt(application.getTenor()),
                    statusResponse.getCompletedOrderValue(),
                    application.getProfitPercentage());
            logger.debug("===========LSF : (updateOrderStatus)-REQUEST , orderID :"
                         + statusResponse.getOrderId()
                         + " , order completed value:"
                         + statusResponse.getCompletedOrderValue()
                         + " , new Profit:"
                         + profitResponse.getProfitAmount());
            response = lsfRepository.upadateOrderStatus(
                    Integer.toString(statusResponse.getOrderId()),
                    statusResponse.getOrderStatus(),
                    statusResponse.getCompletedOrderValue(),
                    profitResponse.getTotalProfit(),
                    profitResponse.getProfitPercent(),
                    statusResponse.getVatAmount(), application.getIpAddress());
            if (response.equalsIgnoreCase("1")) {
                try {
                    if (/*statusResponse.getOrderStatus() == 2*/statusResponse.getCompletedOrderValue() > 0) {
                        lsfRepository.updateActivity(
                                application.getId(),
                                LsfConstants.STATUS_PO_FILLED_WAITING_FOR_ACCEPTANCE);
                        //todo remove this notification
                        //NotificationManager.sendNotification(application);
                        if (!(purchaseOrder.getNoOfCallingAttempts() > 1)) {
                            notificationManager.sendPOAcceptanceReminders(
                                    application,
                                    purchaseOrder,
                                    1,
                                    false);//send first order acceptance notification reminder counter 1 and customer
                            // approve state 2
                            lsfRepository.updatePurchaseOrderAcceptanceReminder(
                                    purchaseOrder.getId(),
                                    (purchaseOrder.getNoOfCallingAttempts() + 1),
                                    purchaseOrder.getCustomerApproveStatus(),
                                    LSFUtils.getCurrentMiliSecondAsString());
                        }
                    }
                    notificationManager.sendNotification(application);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return response;
    }
}
