package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Account;
import com.coronation.collections.domain.User;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.repositories.AccountRepository;
import com.coronation.collections.services.AccountService;
import com.coronation.collections.util.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 4/9/19.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account create(Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public Account update(Account prevAccount, Account newAccount) {
        prevAccount.setEditMode(Boolean.TRUE);
        prevAccount.setUpdateData(JsonConverter.getJson(newAccount));
        return accountRepository.saveAndFlush(prevAccount);
    }

    @Override
    public Account approve(Account account, ApprovalDto approvalDto) {
        if (approvalDto.getApprove()) {
            if (account.getEditMode() && account.getUpdateData() != null) {
                Account edit = JsonConverter.getElement(account.getUpdateData(), Account.class);
                account.setEditMode(Boolean.FALSE);
                account.setAccountName(edit.getAccountName());
                account.setAccountNumber(edit.getAccountNumber());
                account.setBvn(edit.getBvn());
                account.setModifiedAt(edit.getCreatedAt());
                account.setUpdateData(null);
                account.setRejectReason(null);
            }
            account.setStatus(GenericStatus.ACTIVE);
        } else {
            account.setRejectReason(approvalDto.getReason());
            if (account.getEditMode()) {
                account.setEditMode(Boolean.FALSE);
            } else {
                account.setStatus(GenericStatus.REJECTED);
            }
        }
        return accountRepository.saveAndFlush(account);
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public Account findByAccountName(String accountName) {
        return accountRepository.findByAccountName(accountName);
    }

    @Override
    public List<Account> findByBvn(String bvn) {
        return accountRepository.findByBvn(bvn);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return findByAccountNumber(accountNumber);
    }
}
