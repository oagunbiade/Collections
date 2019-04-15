package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "product_name", unique = true)
	private String name;

	@NotNull
	@Column(name = "product_code", unique = true)
	private String code;
	@Column(name="created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name="modified_at")
	private LocalDateTime modifiedAt = LocalDateTime.now();
	@ManyToOne
	@JoinColumn(name = "merchant_id")
	private Merchant merchant;
	@ManyToOne
	@JoinColumn
	private MerchantAccount account;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenericStatus status = GenericStatus.INACTIVE;

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@Column
	private BigDecimal minAmount;

	@Column(columnDefinition = "TEXT")
	private String comment;
	@Column(columnDefinition = "TEXT")
	private String rejectReason;
	@Column(columnDefinition = "TEXT")
	private String updateData;
	@Column(columnDefinition = "TEXT")
	private String accountUpdateData;
	@NotNull
	@Column
	private Boolean editMode = Boolean.FALSE;
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

	public MerchantAccount getAccount() {
		return account;
	}

	public void setAccount(MerchantAccount account) {
		this.account = account;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public GenericStatus getStatus() {
		return status;
	}

	public void setStatus(GenericStatus status) {
		this.status = status;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public String getAccountUpdateData() {
		return accountUpdateData;
	}

	public void setAccountUpdateData(String accountUpdateData) {
		this.accountUpdateData = accountUpdateData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUpdateData() {
		return updateData;
	}

	public void setUpdateData(String updateData) {
		this.updateData = updateData;
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

		Product product = (Product) o;

		return id != null ? id.equals(product.id) : product.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
