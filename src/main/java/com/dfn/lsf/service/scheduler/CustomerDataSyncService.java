package com.dfn.lsf.service.scheduler;

import com.dfn.lsf.model.CustomerInfo;
import com.dfn.lsf.repository.CustomerInfoRepository;
import com.dfn.lsf.service.impl.CustomerInquiryProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDataSyncService {

    private final CustomerInfoRepository customerInfoRepository;
    private final CustomerInquiryProcessor customerInquiryProcessor;

    @Scheduled(cron = "${scheduler.customer.sync.cron:0 0 9 * * ?}")
    @Transactional
    public void syncCustomerData() {
        log.info("Starting daily customer data sync...");

        try {
            List<String> customerIds = customerInfoRepository.findDistinctCustomerIdsForSync();
            log.info("Found {} customers to sync", customerIds.size());

            int successCount = 0;
            int errorCount = 0;

            for (String customerId : customerIds) {
                try {
                    CustomerInfo customerInfo = customerInquiryProcessor.getCustomerDetails(customerId);
                    if (customerInfo != null) {
                        customerInfoRepository.save(customerInfo);
                        successCount++;
                        log.debug("Successfully synced customer: {}", customerId);
                    } else {
                        log.warn("No data received for customer: {}", customerId);
                        errorCount++;
                    }
                } catch (Exception e) {
                    log.error("Error syncing customer {}: {}", customerId, e.getMessage(), e);
                    errorCount++;
                }
            }

            log.info("Customer data sync completed. Success: {}, Errors: {}", successCount, errorCount);
        } catch (Exception e) {
            log.error("Failed to complete customer data sync", e);
        }
    }
}