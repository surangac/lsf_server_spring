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
           if(purchaseOrderList != null && purchaseOrderList.size() > 0){
               for(PurchaseOrder purchaseOrder: purchaseOrderList){
                   if(checkTimeGap(purchaseOrder)){
                       if(purchaseOrder.getNoOfCallingAttempts() < GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay()){ // if current notification count < maximum notification count , send next notification
                           List<MurabahApplication> murabahApplications = lsfRepository.getMurabahAppicationApplicationID(purchaseOrder.getApplicationId());
                           if(murabahApplications != null && murabahApplications.size() > 0){
                               log.debug("===========LSF : Sending PO Acceptance Reminder , Application  :" + purchaseOrder.getId() + ", Attempt Count :" + purchaseOrder.getNoOfCallingAttempts() + 1);
                             //   NotificationManager.sendPOAcceptanceReminders(murabahApplications.get(0), purchaseOrder,(purchaseOrder.getNoOfCallingAttempts() + 1), false);
                              if(notificationManager.sendPOAcceptanceReminders(murabahApplications.get(0), purchaseOrder,(purchaseOrder.getNoOfCallingAttempts() + 1), false)){
                                   lsfRepository.updatePurchaseOrderAcceptanceReminder(purchaseOrder.getId(), (purchaseOrder.getNoOfCallingAttempts() + 1), purchaseOrder.getCustomerApproveStatus(), LSFUtils.getCurrentMiliSecondAsString());
                               }
   
                           }
                       }else if (purchaseOrder.getNoOfCallingAttempts() == GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay()){//if maximum notification count exceeded liquidate & release block
                           CommonResponse liquidateResponse = (CommonResponse) lsfCore.liquidate(purchaseOrder.getId());
                           log.debug("===========LSF : Sending Liquidation Due to PO not Acceptance , Application  :" + purchaseOrder.getApplicationId() + ", Liquidation Status :" + gson.toJson(liquidateResponse));
   
                           if(liquidateResponse.getResponseCode() == 1){
                               lsfRepository.updatePOLiquidateState(purchaseOrder.getId());
                               MApplicationCollaterals mApplicationCollaterals = lsfRepository.getApplicationCompleteCollateral(purchaseOrder.getApplicationId());
                               CommonResponse blockReleaseResponse = (CommonResponse) lsfCore.releaseCollaterals(mApplicationCollaterals);
                               log.debug("===========LSF : Sending Collaterals Release Due to PO not Acceptance , Application  :" + purchaseOrder.getApplicationId() + ", Collatral Release Status :" + blockReleaseResponse.getResponseCode());
   
                               if(blockReleaseResponse.getResponseCode() == 200){
                                   lsfRepository.updatePurchaseOrderAcceptanceReminder(purchaseOrder.getId(), GlobalParameters.getInstance().getNoOfCallingAttemptsPerDay(), -2, LSFUtils.getCurrentMiliSecondAsString());
                                   //lsfRepository.moveToCloseDeuToPONotAcceptance(purchaseOrder.getApplicationId());
                                   lsfCore.moveToCashTransferredClosedState(purchaseOrder.getApplicationId(), "Liquidated & Closed due to PO not acceptance", purchaseOrder.getId());
                                   lsfRepository.moveToCloseDeuToPONotAcceptance(purchaseOrder.getApplicationId());
                                   MurabahApplication application = lsfRepository.getMurabahApplication(mApplicationCollaterals.getApplicationId());
                                   notificationManager.sendPOAcceptanceReminders(application, purchaseOrder, (purchaseOrder.getNoOfCallingAttempts() + 1), true);
   
                               }
                           }
                       }
                   }
               }
           }
    }

    private boolean checkTimeGap(PurchaseOrder purchaseOrder){
        return (Long.valueOf(LSFUtils.getCurrentMiliSecondAsString()) - Long.valueOf(purchaseOrder.getLastCalledTime())) >= (LsfConstants.MILISECONDS_TO_HOUR * GlobalParameters.getInstance().getTimeGapBetweenCallingAttempts());
        //return true;

    }

    public static void main(String[] args) {
        System.out.println((Long.valueOf(LSFUtils.getCurrentMiliSecondAsString()) - Long.valueOf("1478167602482")) >= (LsfConstants.MILISECONDS_TO_HOUR * 0.1));
    }

}
