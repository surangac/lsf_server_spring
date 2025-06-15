package com.dfn.lsf.service.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.CommonResponse;
import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MApplicationCollaterals;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.service.LsfCoreService;
import com.dfn.lsf.util.LSFUtils;
import com.dfn.lsf.util.LsfConstants;
import com.dfn.lsf.util.NotificationManager;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Qualifier("11")
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderAcceptanceReminderProcessor {

    private final LSFRepository lsfRepository;
    private final LsfCoreService lsfCore;
    private final NotificationManager notificationManager;
    private final Gson gson;

    @Scheduled(fixedRateString = "${scheduler.order.acceptance.reminder.rate:900000}") // Default 15 minutes (in milliseconds)
    public void PurchaseOrderAcceptanceReminder() {
        log.info("==========Sending PO Reminders...........!!!!");
        List<PurchaseOrder> purchaseOrderList  = lsfRepository.getPOForReminding();
           if(purchaseOrderList != null && !purchaseOrderList.isEmpty()){
               for(PurchaseOrder purchaseOrder: purchaseOrderList){
                   List<MurabahApplication> murabahApplications = lsfRepository.getMurabahAppicationApplicationID(purchaseOrder.getApplicationId());
                   var application = murabahApplications != null && !murabahApplications.isEmpty() ? murabahApplications.getFirst() : null;
                   if (application != null && !application.isRollOverApp()) {
                       log.debug("===========LSF : Sending PO Acceptance Reminder , Application  :" + purchaseOrder.getId() + ", Attempt Count :" + purchaseOrder.getNoOfCallingAttempts());
                       poAcceptanceReminder(purchaseOrder, application);
                   } else {
                       // TODO : If the application is a roll over application, we can skip the PO acceptance reminder, need to implement a logic to handle this case
                   }
               }
           }
    }

    private void poAcceptanceReminder(PurchaseOrder purchaseOrder, MurabahApplication application) {
        if(!checkTimeGap(purchaseOrder)){
            return;
        }
        if(purchaseOrder.getNoOfCallingAttempts() < GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay()) { // if current notification count < maximum notification count , send next notification
            log.info("===========LSF : Sending PO Acceptance Reminder , AppId : {},Attempt Count : {}", purchaseOrder.getId(), purchaseOrder.getNoOfCallingAttempts() + 1);
            if(notificationManager.sendPOAcceptanceReminders(application, purchaseOrder,(purchaseOrder.getNoOfCallingAttempts() + 1), false)){
                lsfRepository.updatePurchaseOrderAcceptanceReminder(purchaseOrder.getId(), (purchaseOrder.getNoOfCallingAttempts() + 1), purchaseOrder.getCustomerApproveStatus(), LSFUtils.getCurrentMiliSecondAsString());
            }
        } else if (purchaseOrder.getNoOfCallingAttempts() == GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay() && application.getFinanceMethod().equals("1")) {
            //if maximum notification count exceeded liquidate & release block, liquidate only if share finance method
            CommonResponse liquidateResponse = (CommonResponse) lsfCore.liquidate(purchaseOrder.getId());
            log.debug("===========LSF : Sending Liquidation Due to PO not Acceptance , Application  :" + purchaseOrder.getApplicationId() + ", Liquidation Status :" + gson.toJson(liquidateResponse));

            if(liquidateResponse.getResponseCode() == 1){
                lsfRepository.updatePOLiquidateState(purchaseOrder.getId());
                MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateral(purchaseOrder.getApplicationId());
                CommonResponse blockReleaseResponse = (CommonResponse) lsfCore.releaseCollaterals(mApplicationCollaterals);
                log.debug("===========LSF : Sending Collaterals Release Due to PO not Acceptance , Application  :" + purchaseOrder.getApplicationId() + ", Collatral Release Status :" + blockReleaseResponse.getResponseCode());

                if(blockReleaseResponse.getResponseCode() == 200){
                    lsfRepository.updatePurchaseOrderAcceptanceReminder(purchaseOrder.getId(), GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay(), -2, LSFUtils.getCurrentMiliSecondAsString());
                    lsfCore.moveToCashTransferredClosedState(purchaseOrder.getApplicationId(), "Liquidated & Closed due to PO not acceptance", purchaseOrder.getId());
                    lsfRepository.moveToCloseDeuToPONotAcceptance(purchaseOrder.getApplicationId());
                    notificationManager.sendPOAcceptanceReminders(application, purchaseOrder, (purchaseOrder.getNoOfCallingAttempts() + 1), true);
                }
            }
        }
    }

    private boolean checkTimeGap(PurchaseOrder purchaseOrder){
        return (Long.valueOf(LSFUtils.getCurrentMiliSecondAsString()) - Long.valueOf(purchaseOrder.getLastCalledTime())) >= (LsfConstants.MILISECONDS_TO_HOUR * GlobalParameters.getInstance().getTimeGapBetweenCallingAttempts());
        //return true;

    }
}
