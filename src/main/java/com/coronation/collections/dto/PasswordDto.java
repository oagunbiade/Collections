package com.coronation.collections.dto;

import javax.validation.constraints.NotNull;

/**
 * Created by Toyin on 4/9/19.
 */
public class PasswordDto {
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
