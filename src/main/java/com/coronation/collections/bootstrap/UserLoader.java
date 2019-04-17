package com.coronation.collections.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.TaskType;
import com.coronation.collections.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
	private BCryptPasswordEncoder encoder;

	
	private Logger log = LogManager.getLogger(UserLoader.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			User user = new User();
			user.setFirstName("Bosun");
			user.setLastName("Admin");
			user.setPhone("07011223344");
			user.setEmail("olatunbossun@gmail.com");
			user.setPassword(encoder.encode("password"));
			Role role = getOrCreateRole();
			user.setRole(role);
			userRepository.saveAndFlush(user);
			log.info("Saved user - id:" + user.getId());
		}
	}



	private Role getOrCreateRole() {
		seedTasks();
		Role role = roleRepository.findByName("Admin");
		if (role == null) {
			role = new Role();
			role.setName("Admin");
			role = roleRepository.saveAndFlush(role);
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_USER));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_ROLE));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_TASK));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_ROLE_TASK));
			role.getTasks().add(taskRepository.findByName(TaskType.ASSIGN_ROLE));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_USERS));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_TASKS));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_ROLES));
			role = roleRepository.saveAndFlush(role);
		}
		return role;
	}

	private void seedTasks() {
		List<TaskType> taskTypes = Arrays.asList(TaskType.values());
		List<Task> tasks = taskRepository.findAll();
		List<TaskType> addedTaskTypes =
				tasks.stream().map(task -> task.getName()).collect(Collectors.toList());
		taskTypes.forEach(taskType -> {
			if (!addedTaskTypes.contains(taskType)) {
				Task task = new Task();
				task.setName(taskType);
				taskRepository.saveAndFlush(task);
			}
		});
	}
}
