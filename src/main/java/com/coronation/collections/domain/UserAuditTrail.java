package com.coronation.collections.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_audit_trails")
public class UserAuditTrail {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@OneToOne
	@JoinColumn(name = "user_task_id")	
	private Task userTask;
	@ManyToOne
	@JoinColumn(name = "user_id")	
	private User user;	
	@Column(name = "audit_value_before", columnDefinition="TEXT COMMENT 'Value before edit'")
	private String auditValueBefore;
	@Column(name = "audit_value_after", columnDefinition="TEXT COMMENT 'Value after edit'")
	private String auditValueAfter;
	@Column(name = "audit_action")
	private String auditAction;
	@Column(name = "created_at")
	private LocalDateTime createdAt = LocalDateTime.now();
	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	public UserAuditTrail() {}

	public UserAuditTrail(Task userTask, User user, String auditValueBefore, String auditValueAfter,
                          String auditAction) {
		this.userTask = userTask;
		this.user = user;
		this.auditValueBefore = auditValueBefore;
		this.auditValueAfter = auditValueAfter;
		this.auditAction = auditAction;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Task getUserTask() {
		return userTask;
	}
	public void setUserTask(Task userTask) {
		this.userTask = userTask;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getAuditValueBefore() {
		return auditValueBefore;
	}
	public void setAuditValueBefore(String auditValueBefore) {
		this.auditValueBefore = auditValueBefore;
	}
	public String getAuditValueAfter() {
		return auditValueAfter;
	}
	public void setAuditValueAfter(String auditValueAfter) {
		this.auditValueAfter = auditValueAfter;
	}
	public String getAuditAction() {
		return auditAction;
	}
	public void setAuditAction(String auditAction) {
		this.auditAction = auditAction;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
