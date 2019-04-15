package com.coronation.collections.repositories;

import com.coronation.collections.domain.DistributorAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface DistributorAccountRepository extends JpaRepository<DistributorAccount, Long>,
        QuerydslPredicateExecutor<DistributorAccount> {
    List<DistributorAccount> findByDistributorId(Long id);
    DistributorAccount findByAccountId(Long accountId);
    DistributorAccount findByAccount_AccountNumber(String accountNumber);
}
