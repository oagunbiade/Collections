package com.coronation.collections.services;

import com.coronation.collections.domain.Account;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.ApprovalDto;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AccountService {
    Account create(Account account);
    Account update(Account prevAccount, Account newAccount);
    Account approve(Account account, ApprovalDto approvalDto);
    Account findById(Long id);
    Account findByAccountName(String accountName);
    List<Account> findByBvn(String bvn);
    Account findByAccountNumber(String accountNumber);
}
