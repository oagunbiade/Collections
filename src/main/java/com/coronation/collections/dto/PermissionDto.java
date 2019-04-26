package com.coronation.collections.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 4/23/19.
 */
public class PermissionDto {
    @NotNull
    private Boolean read;
    @NotNull
    private Boolean write;

    private Boolean administration;

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getWrite() {
        return write;
    }

    public void setWrite(Boolean write) {
        this.write = write;
    }

    public Boolean getAdministration() {
        return administration;
    }

    public void setAdministration(Boolean administration) {
        this.administration = administration;
    }
}
