package com.coronation.collections.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/10/19.
 */
@Entity
@Table(name = "invalid_payments")
public class InvalidPayment implements IEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column
    private String productCode;
    @Column
    private String rfpCode;
    @Column
    private String amount;
    @Column
    private String numberOfUnits;
    @Column
    private String dueDate;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    @Column
    private Boolean validated = Boolean.FALSE;
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getRfpCode() {
        return rfpCode;
    }

    public void setRfpCode(String rfpCode) {
        this.rfpCode = rfpCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(String numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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
}
