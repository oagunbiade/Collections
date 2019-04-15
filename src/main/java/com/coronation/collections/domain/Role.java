package com.coronation.collections.domain;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

  	@NotNull
  	@Column(name = "role_name", unique = true)
	private String name;
		
	@Column(name = "role_description")
	private String roleDescription;

	@ManyToMany( fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id")
	private Set<Task> tasks;
		
	@Column(name = "is_admin")
	private Boolean isAdmin;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	
	@Column(name = "is_deletable")
	private Boolean isDeletable;
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

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public Boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Boolean isDeletable() {
		return isDeletable;
	}

	public void setDeletable(Boolean isDeletable) {
		this.isDeletable = isDeletable;
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

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Role role = (Role) o;

		return id != null ? id.equals(role.id) : role.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
