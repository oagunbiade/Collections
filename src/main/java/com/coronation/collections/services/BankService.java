package com.coronation.collections.services;

import com.coronation.collections.domain.Bank;
import com.coronation.collections.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Created by Toyin on 4/8/19.
 */
@Service
public interface BankService {
    Bank create(Bank bank);
    Bank edit(Bank prev, Bank current);
    Bank findById(Long id);
    Page<Bank> findAll(BooleanExpression expression, Pageable pageable);
    Bank findByName(String name);
    Bank findByCode(String code);
}
