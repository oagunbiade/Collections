package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "merchant_accounts", uniqueConstraints=
        @UniqueConstraint(columnNames={"merchant_id", "account_id"}))
public class MerchantAccount {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    @NotNull
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
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

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

        MerchantAccount that = (MerchantAccount) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
