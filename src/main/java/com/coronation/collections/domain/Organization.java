package com.coronation.collections.domain;

import com.coronation.collections.domain.enums.GenericStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "Organizations")
public class Organization implements IEntity {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "name", unique = true)
	private String name;

	@NotNull
	@Column(unique = true)
	private String email;

	@Column
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenericStatus status = GenericStatus.INACTIVE;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

		Organization that = (Organization) o;

		return id != null ? id.equals(that.id) : that.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
