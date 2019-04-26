package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/5/19.
 */
@Entity
@Table(name = "banks")
public class Bank implements IEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String name;
    @NotNull
    @Column(unique = true)
    private String bankCode;
    @Column(name="sort_code", columnDefinition="VARCHAR(15) COMMENT 'Bank sortcode'")
    private String sortCode;
    @Column(nullable = false)
    private GenericStatus status = GenericStatus.ACTIVE;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
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

        Bank bank = (Bank) o;

        return id.equals(bank.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
