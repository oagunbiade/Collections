package com.coronation.collections.contollers.api;

import com.coronation.collections.domain.Account;
import com.coronation.collections.domain.Bank;
import com.coronation.collections.dto.AccountDetailResponse;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.exception.ApiException;
import com.coronation.collections.services.AccountService;
import com.coronation.collections.services.BankService;
import com.coronation.collections.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private AccountService accountService;
    private BankService bankService;

    @Autowired
    public AccountController(AccountService accountService, BankService bankService) {
        this.accountService = accountService;
        this.bankService = bankService;
    }

    @PreAuthorize("hasRole('CREATE_ACCOUNT')")
    @PostMapping("/number/{accountNumber}")
    public ResponseEntity<Account> create(@PathVariable("accountNumber") String accountNumber) {
        if (accountService.findByAccountNumber(accountNumber) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            try {
                AccountDetailResponse response = accountService.fetchAccountDetails(accountNumber);
                if (response.getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
                    if (Constants.ACCOUNT_ACTIVE_STATUS.equals(response.getStatus())) {
                        Account account = response.toAccount();
                        Bank bank = bankService.findByCode(Constants.BANK_CODE);
                        account.setBank(bank);
                        return ResponseEntity.ok(accountService.create(account));
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
                    }
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (ApiException ex) {
                return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).build();
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_ACCOUNT')")
    @PutMapping("/{id}")
    public ResponseEntity<Account> edit(@PathVariable("id") Long id, @RequestBody @Valid Account account,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Account previous = accountService.findById(id);
            if (!previous.getAccountNumber().equals(account.getAccountNumber())) {
                Account duplicate = accountService.findByAccountNumber(account.getAccountNumber());
                if (duplicate == null) {
                    try {
                        AccountDetailResponse response =
                                accountService.fetchAccountDetails(account.getAccountNumber());
                        if (response.getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
                            if (!Constants.ACCOUNT_ACTIVE_STATUS.equals(response.getStatus())) {
                                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
                            }
                        } else {
                            return ResponseEntity.notFound().build();
                        }
                    } catch (ApiException ex) {
                        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
            return ResponseEntity.ok(accountService.update(previous,account));
        }
    }

    @PreAuthorize("hasRole('APPROVE_ACCOUNT')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<Account> approve(@PathVariable("id") Long id,
                   @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Account account = accountService.findById(id);
            if (account == null) {
               return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(accountService.approve(account, approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('FETCH_ACCOUNT')")
    @GetMapping("/bvn/{bvn}")
    public ResponseEntity<List<Account>> fetchByBvn(@PathVariable("bvn") String bvn) {
        return ResponseEntity.ok(accountService.findByBvn(bvn));
    }

    @PreAuthorize("hasRole('FETCH_ACCOUNT')")
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> fetchByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
        Account account = accountService.findByAccountNumber(accountNumber);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("hasRole('FETCH_ACCOUNT')")
    @GetMapping("/number/{accountNumber}/api")
    public ResponseEntity<Account> fetchByAccountNumberApi(@PathVariable("accountNumber") String accountNumber) {
        try {
            AccountDetailResponse response = accountService.fetchAccountDetails(accountNumber);
            if (response.getResponseCode().equals(Constants.ACCOUNT_RESPONSE_CODE)) {
                return ResponseEntity.ok(response.toAccount());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ApiException ex) {
            return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode())).build();
        }
    }
}
