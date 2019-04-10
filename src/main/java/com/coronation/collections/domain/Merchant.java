package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "merchants")
public class Merchant {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "merchant_code", unique = true)
	private String merchantCode;

	@NotNull
	@Column(name = "merchant_name", unique = true)
	private String merchantName;

	@NotNull
	@Column(name = "email", unique = true)
	private String email;

	@OneToMany( fetch = FetchType.EAGER)
	@JoinColumn(name = "merchant_id")
	private Set<MerchantUser> merchantUsers;

	@NotNull
	@Pattern(regexp="(\\+)?[0-9]{11,20}$")
	@Column(name = "phone", unique = true)
	private String phone;

	@Column(name = "address")
	private String address;

	@Column(name = "city")
	private String city;

	@Column(name = "state")
	private String state;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GenericStatus status = GenericStatus.INACTIVE;

	@Column(nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@Column(name="created_at")
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name="modified_at")
	private LocalDateTime modifiedAt = LocalDateTime.now();

	@OneToMany( fetch = FetchType.EAGER)
	@JoinColumn(name = "merchant_id")
	private Set<Product> products;

	@OneToMany( fetch = FetchType.EAGER)
	@JoinColumn(name = "merchant_id")
	private Set<MerchantAccount> merchantAccounts;

	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@Column(nullable = false)
	private Boolean editMode = Boolean.FALSE;
	@Column(columnDefinition = "TEXT")
	private String rejectReason;
	@Column(columnDefinition = "TEXT")
	private String updateData;

	@OneToOne
	@JoinColumn(name = "authentication_detail_id")
	private AuthenticationDetail authenticationDetail;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public GenericStatus getStatus() {
		return status;
	}

	public void setStatus(GenericStatus status) {
		this.status = status;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Set<MerchantUser> getMerchantUsers() {
		return merchantUsers;
	}

	public void setMerchantUsers(Set<MerchantUser> merchantUsers) {
		this.merchantUsers = merchantUsers;
	}

	public AuthenticationDetail getAuthenticationDetail() {
		return authenticationDetail;
	}

	public void setAuthenticationDetail(AuthenticationDetail authenticationDetail) {
		this.authenticationDetail = authenticationDetail;
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Set<MerchantAccount> getMerchantAccounts() {
		return merchantAccounts;
	}

	public void setMerchantAccounts(Set<MerchantAccount> merchantAccounts) {
		this.merchantAccounts = merchantAccounts;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Merchant merchant = (Merchant) o;

		return id.equals(merchant.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
