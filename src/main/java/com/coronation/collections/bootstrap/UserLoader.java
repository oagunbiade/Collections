package com.coronation.collections.bootstrap;

import java.util.List;

import com.coronation.collections.domain.*;
import com.coronation.collections.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class UserLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private OrganizationRepository organizationRepository;
	
	private Logger log = LogManager.getLogger(UserLoader.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			Organization org = new Organization();
			org.setName("Sahara Energy");
			User user = new User();
			user.setEmail("olatunbossun@gmail.com");
			user.setPassword("password");
			user = userRepository.save(user);
			Role role = getOrCreateRole();
			role = addTaskToRole(role);

			user.setRole(role);
			userRepository.saveAndFlush(user);
			log.info("Saved user - id:" + user.getId());
		}
	}

	private Role getOrCreateRole() {
		Role role = roleRepository.findByRoleName("Admin");
		if (role == null) {
			role = new Role();
			role.setName("Admin");
			role = roleRepository.saveAndFlush(role);
		}
		return role;
	}

	private Role addTaskToRole(Role role) {
		if (role.getTasks().isEmpty()) {
			Task task = new Task();
			task.setName("CREATE_USER");
			task = taskRepository.saveAndFlush(task);
			role.getTasks().add(task);
			role = roleRepository.saveAndFlush(role);
		}
		return role;
	}
}
