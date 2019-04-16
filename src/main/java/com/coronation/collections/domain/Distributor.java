package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "distributors")
public class Distributor {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "name", unique = true)
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenericStatus status = GenericStatus.INACTIVE;
    @NotNull
    @Column(unique = true)
    private String bvn;
    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    @Column(unique = true)
    private String phoneNumber;
    @Column(columnDefinition = "TEXT")
    private String comment;
    @NotNull
    @Column(columnDefinition = "TEXT")
    private String address;
    @NotNull
    @Column
    private String contact;
    @Column(nullable = false)
    private Boolean editMode = Boolean.FALSE;
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;
    @Column(columnDefinition = "TEXT")
    private String rejectReason;
    @Column(columnDefinition = "TEXT")
    private String updateData;
    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name="modified_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Transient
    private String rfpCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GenericStatus getStatus() {
        return status;
    }

    public void setStatus(GenericStatus status) {
        this.status = status;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUpdateData() {
        return updateData;
    }

    public void setUpdateData(String updateData) {
        this.updateData = updateData;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getRfpCode() {
        return rfpCode;
    }

    public void setRfpCode(String rfpCode) {
        this.rfpCode = rfpCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Distributor that = (Distributor) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
