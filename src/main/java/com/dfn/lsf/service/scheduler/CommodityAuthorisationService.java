package com.dfn.lsf.service.scheduler;

import com.dfn.lsf.model.GlobalParameters;
import com.dfn.lsf.model.MurabahApplication;
import com.dfn.lsf.repository.LSFRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommodityAuthorisationService {

    private final LSFRepository lsfRepository;

    public void authoriseCommodity_sell(String commodityId) {
        log.info("===========LSF Commodity Authorisation Service - authoriseCommodity_sell===========");
        List<MurabahApplication> murabahApplicationList = lsfRepository.getOrderContractSingedApplications();
        int timeToAuthorise = GlobalParameters.getInstance().getGracePeriodforCommoditySell();
        var commodityApps = murabahApplicationList.stream().filter(application -> application.getFinanceMethod().equals("2"));
        if (commodityApps.findAny().isPresent()) {
            log.info("Commodity applications found for sell authorisation: {}", commodityApps.count());
            for (MurabahApplication application : commodityApps.toList()) {
                var purchaseOrders = lsfRepository.getAllPurchaseOrder(application.getId());

            }
        } else {
            log.info("No commodity applications found for sell authorisation.");
        }
    }
}
