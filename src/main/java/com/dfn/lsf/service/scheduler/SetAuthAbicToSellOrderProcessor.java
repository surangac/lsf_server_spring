package com.dfn.lsf.service.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.model.PurchaseOrder;
import com.dfn.lsf.repository.LSFRepository;
import com.dfn.lsf.util.NotificationManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SetAuthAbicToSellOrderProcessor {

    private final LSFRepository lsfRepository;
    private final NotificationManager notificationManager;

    @Scheduled(fixedRateString = "${scheduler.set.auth.abic.to.sell.order.processor.rate:900000}") // Default 15 minutes (in milliseconds)
    public void setAuthAbicToSellOrderProcessor() {
        log.info("==========Authenticate ABIC to Sell...........!!!!");
        int gracePrd = GlobalParameters.getInstance().getGracePeriodforCommoditySell();
        List<PurchaseOrder> purchaseOrderList  = lsfRepository.getPOForSetAuthAbicToSell(gracePrd);
        if(purchaseOrderList != null && purchaseOrderList.size() > 0) {
            for(PurchaseOrder purchaseOrder: purchaseOrderList) {
                purchaseOrder.setAuthAbicToSell("2");
                purchaseOrder.setIsPhysicalDelivery(0);
                lsfRepository.addAuthAbicToSellStatus(purchaseOrder);
                MurabahApplication application = lsfRepository.getMurabahAppicationApplicationID(purchaseOrder.getApplicationId()).get(0);
                notificationManager.sendAuthAbicToSellNotification(application, true);/*---Send Notification---*/
                log.info("==========Authenticate ABIC to Sell...........!!!!");
            }
        }
    }
}
