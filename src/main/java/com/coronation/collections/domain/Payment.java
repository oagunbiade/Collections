package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.PaymentStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "payment_details")
public class Payment implements Serializable{
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "reference_code")
	private String referenceCode;

	@NotNull
	@Column
	private Integer numberOfUnits;

	@NotNull
	@Column(name = "amount")
	private BigDecimal amount;

	@JoinColumn(name = "distributor_id", nullable = false)
	private Distributor distributor;

	@JoinColumn(name = "distributor_account_id")
	private DistributorAccount distributorAccount;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PaymentStatus status = PaymentStatus.INITIATED;

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@Column(name = "try_count")
	private int tryCount;

	@Column(name = "notification_status", nullable = false)
	private Boolean notificationStatus = Boolean.FALSE;

	@Column(nullable = false)
	private Boolean confirmed = Boolean.FALSE;

	@Column(nullable = false)
	private Boolean editMode = Boolean.FALSE;
	@Column(columnDefinition = "TEXT")
	private String rejectReason;
	@Column(columnDefinition = "TEXT")
	private String updateData;

	@ManyToOne
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Column(columnDefinition = "TEXT")
	private String comment;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@FutureOrPresent
	@Column(name="created_at")
	private LocalDate dueDate;

	@Column(name="created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name="modified_at")
	private LocalDateTime modifiedAt = LocalDateTime.now();

	@ManyToOne
	@JoinColumn(name = "merchant_id", nullable = false)
	private Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Distributor getDistributor() {
		return distributor;
	}

	public void setDistributor(Distributor distributor) {
		this.distributor = distributor;
	}

	public DistributorAccount getDistributorAccount() {
		return distributorAccount;
	}

	public void setDistributorAccount(DistributorAccount distributorAccount) {
		this.distributorAccount = distributorAccount;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(Integer numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(Boolean notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public int getTryCount() {
		return tryCount;
	}

	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	public Boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
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

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getNarration() {
		return String.format("Collections for Product: %s, by: %s, to: %s",
				product.getName(), distributor.getName(), merchant.getMerchantName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Payment payment = (Payment) o;

		return id != null ? id.equals(payment.id) : payment.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
