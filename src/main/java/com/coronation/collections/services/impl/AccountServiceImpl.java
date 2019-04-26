package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Account;
import com.coronation.collections.domain.User;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.AccountDetailRequest;
import com.coronation.collections.dto.AccountDetailResponse;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.exception.ApiException;
import com.coronation.collections.repositories.AccountRepository;
import com.coronation.collections.services.AccountService;
import com.coronation.collections.util.Constants;
import com.coronation.collections.util.JsonConverter;
import com.coronation.collections.util.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 4/9/19.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;
    private Utilities utilities;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, Utilities utilities) {
        this.accountRepository = accountRepository;
        this.utilities = utilities;
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
    public List<Account> findByBvn(String bvn) {
        return accountRepository.findByBvn(bvn);
    }

    @Override
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public AccountDetailResponse fetchAccountDetails(String accountNumber) throws ApiException {
        AccountDetailRequest accountDetailRequest = new AccountDetailRequest(accountNumber);
        ResponseEntity<AccountDetailResponse> response = utilities.getAccountDetails(accountDetailRequest);
        if (response.getStatusCode() != HttpStatus.OK) {
            ApiException exception = new ApiException("An error occurred while fetching account");
            exception.setStatusCode(response.getStatusCode().value());
            throw exception;
        } else if (response.getBody().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
            return response.getBody();
        } else {
            ApiException exception = new ApiException("Account was not found");
            exception.setStatusCode(HttpStatus.NOT_FOUND.value());
            throw exception;
        }
    }
}
