package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Bank;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.repositories.BankRepository;
import com.coronation.collections.services.BankService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/8/19.
 */
@Service
public class BankServiceImpl implements BankService {
    private BankRepository bankRepository;

    @Autowired
    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public Bank create(Bank bank) {
        bank.setStatus(GenericStatus.ACTIVE);
        return null;
    }

    @Override
    public Bank edit(Bank prev, Bank current) {
        prev.setBankCode(current.getBankCode());
        prev.setBankName(current.getBankName());
        prev.setSortCode(current.getSortCode());
        prev.setModifiedAt(LocalDateTime.now());
        return bankRepository.saveAndFlush(prev);
    }

    @Override
    public Bank findById(Long id) {
        return bankRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Bank> findAll(BooleanExpression expression, Pageable pageable) {
        return bankRepository.findAll(expression, pageable);
    }

    @Override
    public Bank findByName(String name) {
        return bankRepository.findByBankName(name);
    }

    @Override
    public Bank findByCode(String code) {
        return bankRepository.findByBankCode(code);
    }
}
