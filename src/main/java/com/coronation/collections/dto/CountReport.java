package com.coronation.collections.dto;

/**
 * Created by Toyin on 4/8/19.
 */
public class CountReport {
    private int all;
    private int processed;
    private int failed;
    private int approved;
    private int canceled;
    private int initiated;
    private int invalid;
    private int rejected;
    private int dueToday;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int getCanceled() {
        return canceled;
    }

    public void setCanceled(int canceled) {
        this.canceled = canceled;
    }

    public int getInitiated() {
        return initiated;
    }

    public void setInitiated(int initiated) {
        this.initiated = initiated;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected(int rejected) {
        this.rejected = rejected;
    }

    public int getDueToday() {
        return dueToday;
    }

    public void setDueToday(int dueToday) {
        this.dueToday = dueToday;
    }
}
