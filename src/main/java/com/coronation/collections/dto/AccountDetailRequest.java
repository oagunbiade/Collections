package com.coronation.collections.dto;

/**
 * Created by Toyin on 4/11/19.
 */
public class AccountDetailRequest {
    private String  accountNumber;

    public AccountDetailRequest(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
