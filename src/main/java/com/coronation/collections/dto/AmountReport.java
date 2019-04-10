package com.coronation.collections.dto;

import java.math.BigDecimal;

/**
 * Created by Toyin on 4/8/19.
 */
public class AmountReport {
    private BigDecimal all = new BigDecimal("0.00");
    private BigDecimal processed = new BigDecimal("0.00");
    private BigDecimal failed = new BigDecimal("0.00");
    private BigDecimal approved = new BigDecimal("0.00");
    private BigDecimal canceled = new BigDecimal("0.00");
    private BigDecimal initiated = new BigDecimal("0.00");
    private BigDecimal invalid = new BigDecimal("0.00");
    private BigDecimal rejected = new BigDecimal("0.00");

    public BigDecimal getAll() {
        return all;
    }

    public void setAll(BigDecimal all) {
        this.all = all;
    }

    public BigDecimal getProcessed() {
        return processed;
    }

    public void setProcessed(BigDecimal processed) {
        this.processed = processed;
    }

    public BigDecimal getFailed() {
        return failed;
    }

    public void setFailed(BigDecimal failed) {
        this.failed = failed;
    }

    public BigDecimal getApproved() {
        return approved;
    }

    public void setApproved(BigDecimal approved) {
        this.approved = approved;
    }

    public BigDecimal getCanceled() {
        return canceled;
    }

    public void setCanceled(BigDecimal canceled) {
        this.canceled = canceled;
    }

    public BigDecimal getInitiated() {
        return initiated;
    }

    public void setInitiated(BigDecimal initiated) {
        this.initiated = initiated;
    }

    public BigDecimal getInvalid() {
        return invalid;
    }

    public void setInvalid(BigDecimal invalid) {
        this.invalid = invalid;
    }

    public BigDecimal getRejected() {
        return rejected;
    }

    public void setRejected(BigDecimal rejected) {
        this.rejected = rejected;
    }
}
