package com.coronation.collections.services;

import com.coronation.collections.domain.Role;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.PasswordDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
	Page<User> listAll(BooleanExpression expression, Pageable pageable);
    User save(User user);
    User findByEmail(String email);
	User findById(Long userId);
    User update(User prev, User current);
    User delete(User user);
	User assignRole(User user, Role role);
	List<User> findByRole(Role role);
	User uploadImage(User user, byte[] image);
	String resetPassword(User user);
	User changePassword(User user, PasswordDto passwordDto);
}
