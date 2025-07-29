package com.dfn.lsf.service.scheduler;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommodityAuthorisationService {

    private final LSFRepository lsfRepository;

    public void authoriseCommodity_sell(String commodityId) {
        log.info("===========LSF Commodity Authorisation Service - authoriseCommodity_sell===========");
        List<MurabahApplication> murabahApplicationList = lsfRepository.getOrderContractSingedApplications();
        int timeToAuthorize = GlobalParameters.getInstance().getGracePeriodforCommoditySell();
        var commodityApps = murabahApplicationList.stream()
                                                  .filter(application -> "2".equals(application.getFinanceMethod()))
                                                  .toList();
        if (!commodityApps.isEmpty()) {
            log.info("Commodity applications found for sell authorisation: {}", commodityApps.size());
            for (MurabahApplication application : commodityApps) {
                var purchaseOrders = lsfRepository.getAllPurchaseOrder(application.getId());
                if (!purchaseOrders.isEmpty()) {
                    var po = purchaseOrders.getFirst();
                    if (po.getAuthAbicToSell() == null || po.getAuthAbicToSell().isEmpty()) {
                        log.info("Authorising commodity sell for application ID: {}", application.getId());
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime acceptedDate = LocalDateTime.parse(po.getAcceptedDate());
                        long minutes = Duration.between(acceptedDate, now).toMinutes();
                        if (minutes >= timeToAuthorize) {
                            po.setAuthAbicToSell("1");
                            po.setIsPhysicalDelivery(0);
                            lsfRepository.updatePurchaseOrderByAdmin(po);
                            log.info("Commodity sell authorised for application ID: {}", application.getId());
                        } else {
                            log.info("Commodity sell not authorised yet for application ID: {}. Time remaining: {} minutes",
                                    application.getId(), timeToAuthorize - minutes);
                        }
                    } else {
                        log.info("Commodity sell already authorised for application ID: {}", application.getId());
                    }
                }

            }
        } else {
            log.info("No commodity applications found for sell authorisation.");
        }
    }
}
