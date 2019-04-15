package com.coronation.collections.repositories;

import com.coronation.collections.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by Toyin on 4/8/19.
 */
public interface BankRepository extends JpaRepository<Bank, Long>,
        QuerydslPredicateExecutor<Bank> {
    Bank findByBankCode(String code);
    Bank findByName(String name);
}
