package com.coronation.collections.dto;

import java.math.BigDecimal;

/**
 * Created by Toyin on 4/11/19.
 */
public class TransferRequest {
    private String uniqueIdentifier;
    private String debitAccountNumber;
    private String creditAccountNumber;
    private BigDecimal tranAmount;
    private String naration;

    public TransferRequest() {

    }

    public TransferRequest(String uniqueIdentifier, String debitAccountNumber,
                           String creditAccountNumber, BigDecimal tranAmount, String naration) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.debitAccountNumber = debitAccountNumber;
        this.creditAccountNumber = creditAccountNumber;
        this.tranAmount = tranAmount;
        this.naration = naration;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getDebitAccountNumber() {
        return debitAccountNumber;
    }

    public void setDebitAccountNumber(String debitAccountNumber) {
        this.debitAccountNumber = debitAccountNumber;
    }

    public String getCreditAccountNumber() {
        return creditAccountNumber;
    }

    public void setCreditAccountNumber(String creditAccountNumber) {
        this.creditAccountNumber = creditAccountNumber;
    }

    public BigDecimal getTranAmount() {
        return tranAmount;
    }

    public void setTranAmount(BigDecimal tranAmount) {
        this.tranAmount = tranAmount;
    }

    public String getNaration() {
        return naration;
    }

    public void setNaration(String naration) {
        this.naration = naration;
    }
}
