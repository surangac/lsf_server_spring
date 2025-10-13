package com.dfn.lsf.repository;

import com.dfn.lsf.model.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, String> {

    @Query(value = "SELECT DISTINCT L01_CUSTOMER_ID FROM L01_APPLICATION WHERE L01_CURRENT_LEVEL != 18", nativeQuery = true)
    List<String> findDistinctCustomerIdsForSync();
}