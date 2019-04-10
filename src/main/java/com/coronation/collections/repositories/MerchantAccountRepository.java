package com.coronation.collections.repositories;

import com.coronation.collections.domain.MerchantAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface MerchantAccountRepository extends JpaRepository<MerchantAccount, Long> {
    List<MerchantAccount> findByMerchantId(Long id);
    MerchantAccount findByAccountId(Long accountId);
}
