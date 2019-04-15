package com.coronation.collections.dto;

import com.coronation.collections.domain.Account;

import java.math.BigDecimal;

/**
 * Created by Toyin on 4/11/19.
 */
public class AccountDetailResponse {
    private String status;
    private BigDecimal balance;
    private String restriction;
    private String cifId;
    private String accountSchmCode;
    private String misCode;
    private BigDecimal effectiveBalance;
    private String phoneNumber;
    private String email;
    private String bvn;
    private String responseCode;
    private String accountNumber;
    private String accountName;
    private String responseText;
    private String accountCurrency;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public String getCifId() {
        return cifId;
    }

    public void setCifId(String cifId) {
        this.cifId = cifId;
    }

    public String getAccountSchmCode() {
        return accountSchmCode;
    }

    public void setAccountSchmCode(String accountSchmCode) {
        this.accountSchmCode = accountSchmCode;
    }

    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
    }

    public BigDecimal getEffectiveBalance() {
        return effectiveBalance;
    }

    public void setEffectiveBalance(BigDecimal effectiveBalance) {
        this.effectiveBalance = effectiveBalance;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public Account toAccount() {
        Account account = new Account();
        account.setBvn(bvn);
        account.setAccountNumber(accountNumber);
        account.setAccountName(accountName);
        account.setCurrency(accountCurrency);
        account.setSchemeCode(accountSchmCode);
        return account;
    }
}
