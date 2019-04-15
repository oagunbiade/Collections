package com.coronation.collections.services;

import com.coronation.collections.domain.Role;
import com.coronation.collections.domain.Task;

import java.util.List;

public interface RoleService {
	List<Role> findAll();
	Role save(Role role);
	Role update(Role prev, Role current);
	Role findByName(String name);
	Role findById(Long id);
	Role addTaskToRole(Role role, Task task);
	Role removeTaskFromRole(Role role, Task task);
}
