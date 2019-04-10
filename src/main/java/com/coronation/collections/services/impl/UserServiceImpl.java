package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Role;
import com.coronation.collections.dto.PasswordDto;
import com.coronation.collections.services.UserService;
import com.coronation.collections.util.GenericUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.coronation.collections.domain.User;
import com.coronation.collections.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public void setUserRepository(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Page<User> listAll(BooleanExpression expression, Pageable pageable) {
		return userRepository.findAll(expression, pageable);
	}

	@Override
	public User save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.saveAndFlush(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public User update(User prev, User current) {
		prev.setAddress(current.getAddress());
		prev.setFirstName(current.getFirstName());
		prev.setLastName(current.getLastName());
		prev.setOtherNames(current.getOtherNames());
		prev.setPhone(current.getPhone());
		prev.setUserName(current.getUserName());
		prev.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(prev);
	}

	@Override
	public User delete(User user) {
		user.setDeleted(Boolean.TRUE);
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}

	@Override
	public User assignRole(User user, Role role) {
		user.setRole(role);
		return userRepository.saveAndFlush(user);
	}

	@Override
	public List<User> findByRole(Role role) {
		return userRepository.findByRoleId(role.getId());
	}

	@Override
	public User uploadImage(User user, byte[] image) {
		user.setProfileImage(image);
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}

	@Override
	public String resetPassword(User user) {
		String password = GenericUtil.generateRandomString(8);
		user.setPassword(passwordEncoder.encode(password));
		user.setModifiedAt(LocalDateTime.now());
		userRepository.saveAndFlush(user);
		return password;
	}

	@Override
	public User changePassword(User user, PasswordDto passwordDto) {
		user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
		user.setModifiedAt(LocalDateTime.now());
		return userRepository.saveAndFlush(user);
	}
}
