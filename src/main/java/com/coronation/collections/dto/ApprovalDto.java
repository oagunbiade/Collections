package com.coronation.collections.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 4/9/19.
 */
public class ApprovalDto {
    @NotNull
    private Boolean approve;
    private String reason;

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
