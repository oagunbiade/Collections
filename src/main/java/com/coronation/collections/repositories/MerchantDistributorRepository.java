package com.coronation.collections.repositories;

import com.coronation.collections.domain.MerchantDistributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface MerchantDistributorRepository extends JpaRepository<MerchantDistributor, Long> {
    List<MerchantDistributor> findByMerchantId(Long id);
    MerchantDistributor findByMerchantIdAndDistributorId(Long merchantId, Long distributorId);
    MerchantDistributor findByMerchantIdAndRfpCode(Long merchantId, String rfpCode);
}
