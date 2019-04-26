package com.coronation.collections.repositories;

import com.coronation.collections.domain.InvalidPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Toyin on 4/10/19.
 */
public interface InvalidPaymentRepository extends JpaRepository<InvalidPayment, Long> {
    Page<InvalidPayment> findByMerchantIdAndValidatedFalse(Long merchantId, Pageable pageable);
}
