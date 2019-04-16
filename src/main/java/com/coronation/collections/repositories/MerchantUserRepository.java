package com.coronation.collections.repositories;

import com.coronation.collections.domain.MerchantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface MerchantUserRepository extends JpaRepository<MerchantUser, Long>,
        QuerydslPredicateExecutor<MerchantUser> {
    List<MerchantUser> findByMerchantId(Long id);
    MerchantUser findByOrganizationUserId(Long userId);
}
