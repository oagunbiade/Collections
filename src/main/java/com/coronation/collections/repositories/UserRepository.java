package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import com.coronation.collections.domain.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>,
		QuerydslPredicateExecutor<User> {
	User findByEmail(String email);
	User findByPhone(String phone);
	@Query("select u from User u where u.userName = :userName and u.userType.userTypeId = :userTypeId")
	User findByUserByUserType(@Param("userName") String userName,
	                                 @Param("userTypeId") Long userTypeId);
	List<User> findByRoleId(Long roleId);
}
