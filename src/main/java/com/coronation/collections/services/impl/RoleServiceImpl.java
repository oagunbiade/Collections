package com.coronation.collections.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.coronation.collections.domain.Task;
import com.coronation.collections.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coronation.collections.domain.Role;
import com.coronation.collections.repositories.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {
	
	private RoleRepository roleRepository;
	
	@Autowired
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}


	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public Role save(Role role) {
		return roleRepository.saveAndFlush(role);
	}

	@Override
	public Role update(Role prev, Role current) {
		prev.setName(current.getName());
		prev.setModifiedAt(LocalDateTime.now());
		return roleRepository.saveAndFlush(prev);
	}

	@Override
	public Role findByName(String name) {
		return roleRepository.findByName(name);
	}

	@Override
	public Role findById(Long id) {
		return roleRepository.findById(id).orElse(null);
	}

	@Override
	public Role addTaskToRole(Role role, Task task) {
		role.getTasks().add(task);
		role.setModifiedAt(LocalDateTime.now());
		return roleRepository.saveAndFlush(role);
	}

	@Override
	public Role removeTaskFromRole(Role role, Task task) {
		role.getTasks().remove(task);
		role.setModifiedAt(LocalDateTime.now());
		return roleRepository.saveAndFlush(role);
	}
}
