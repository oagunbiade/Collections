package com.coronation.collections.repositories;

import com.coronation.collections.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AccountRepository extends JpaRepository<Account, Long>,
        QuerydslPredicateExecutor<Account> {
    Account findByAccountName(String accountName);
    List<Account> findByBvn(String bvn);
    Account findByAccountNumber(String accountNumber);
}
