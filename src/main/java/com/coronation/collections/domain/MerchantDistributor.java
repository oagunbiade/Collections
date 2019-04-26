package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "merchant_distributors", uniqueConstraints={
                    @UniqueConstraint(columnNames={"merchant_id", "distributor_id"}),
        @UniqueConstraint(columnNames={"merchant_id", "rfp_code"})})
public class MerchantDistributor implements IEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    @NotNull
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "distributor_id")
    private Distributor distributor;
    @Column(name = "rfp_code")
    private String rfpCode;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private GenericStatus status = GenericStatus.INACTIVE;
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
    @Column(nullable = false)
    private Boolean editMode = Boolean.FALSE;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    @Column(columnDefinition = "TEXT")
    private String updateData;
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

    public GenericStatus getStatus() {
        return status;
    }

    public void setStatus(GenericStatus status) {
        this.status = status;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public String getRfpCode() {
        return rfpCode;
    }

    public void setRfpCode(String rfpCode) {
        this.rfpCode = rfpCode;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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

    public String getUpdateData() {
        return updateData;
    }

    public void setUpdateData(String updateData) {
        this.updateData = updateData;
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

        MerchantDistributor that = (MerchantDistributor) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
