package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "accounts")
public class Account implements IEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String accountNumber;
    @Column
    private String accountName;
    @NotNull
    @Column
    private String bvn;
    @NotNull
    @Column
    private String currency;
    @NotNull
    @Column
    private String schemeCode;
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;
    @Column(nullable = false)
    private Boolean editMode = Boolean.FALSE;
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenericStatus status = GenericStatus.ACTIVE;
    @Column(columnDefinition = "TEXT")
    private String updateData;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="modified_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSchemeCode() {
        return schemeCode;
    }

    public void setSchemeCode(String schemeCode) {
        this.schemeCode = schemeCode;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public String getUpdateData() {
        return updateData;
    }

    public void setUpdateData(String updateData) {
        this.updateData = updateData;
    }

    public Boolean getEditMode() {
        return editMode;
    }

    public void setEditMode(Boolean editMode) {
        this.editMode = editMode;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public GenericStatus getStatus() {
        return status;
    }

    public void setStatus(GenericStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
