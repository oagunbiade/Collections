package com.coronation.collections.services;

import com.coronation.collections.domain.Account;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.AccountDetailResponse;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.exception.ApiException;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AccountService {
    Account create(Account account);
    Account update(Account prevAccount, Account newAccount);
    Account approve(Account account, ApprovalDto approvalDto);
    Account findById(Long id);
    List<Account> findByBvn(String bvn);
    Account findByAccountNumber(String accountNumber);
    AccountDetailResponse fetchAccountDetails(String accountNumber) throws ApiException;
}
