package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.coronation.collections.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>,
		QuerydslPredicateExecutor<User> {
	User findByEmail(String email);
	User findByPhone(String phone);
	List<User> findByRoleId(Long roleId);
}
