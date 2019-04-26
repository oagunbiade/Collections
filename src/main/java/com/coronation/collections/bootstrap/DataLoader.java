package com.coronation.collections.bootstrap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.domain.enums.TaskType;
import com.coronation.collections.repositories.*;
import com.coronation.collections.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrganizationUserRepository organizationUserRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;

	
	private Logger log = LogManager.getLogger(DataLoader.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		seedOrganization();
		seedUsers();
		seedBanks();
		seedRoles();
	}

	private void seedOrganization() {
		Organization organization = organizationRepository.findByNameEquals(Constants.DEFAULT_BANK_NAME);
		if (organization == null) {
			organization = new Organization();
			organization.setEmail("admin@coronationmb.com");
			organization.setName(Constants.DEFAULT_BANK_NAME);
			organization.setStatus(GenericStatus.ACTIVE);
			organizationRepository.saveAndFlush(organization);
		}
	}

	private void seedUsers() {
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			Organization organization = organizationRepository.findByNameEquals(Constants.DEFAULT_BANK_NAME);
			User user = new User();
			user.setFirstName("Bosun");
			user.setLastName("Admin");
			user.setPhone("07011223344");
			user.setEmail("olatunbossun@gmail.com");
			user.setPassword(encoder.encode("password"));
			Role role = getOrCreateRole();
			user.setRole(role);
			user = userRepository.saveAndFlush(user);
			log.info("Saved user - id:" + user.getId());
			OrganizationUser organizationUser = new OrganizationUser();
			organizationUser.setUser(user);
			organizationUser.setOrganization(organization);
			organizationUserRepository.saveAndFlush(organizationUser);
		}
	}



	private Role getOrCreateRole() {
		seedTasks();
		Role role = roleRepository.findByName("ADMIN");
		if (role == null) {
			role = new Role();
			role.setName("ADMIN");
			role = roleRepository.saveAndFlush(role);
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_ORGANIZATIONS));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_ORGANIZATION));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_REPORTS));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_DISTRIBUTOR_USER));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_ORGANIZATION_USER));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT_USER));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_PRODUCT));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_ACCOUNT));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT_DISTRIBUTOR));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_DISTRIBUTOR));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_MERCHANTS));

			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_USER));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_ROLE));
			role.getTasks().add(taskRepository.findByName(TaskType.CREATE_TASK));
			role.getTasks().add(taskRepository.findByName(TaskType.ADD_ROLE_TASK));
			role.getTasks().add(taskRepository.findByName(TaskType.ASSIGN_ROLE));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_USERS));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_TASKS));
			role.getTasks().add(taskRepository.findByName(TaskType.VIEW_ROLES));
			role.getTasks().add(taskRepository.findByName(TaskType.MANAGE_ACCESS));
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

	private void seedBanks() {
		Bank bank = bankRepository.findByBankCode(Constants.BANK_CODE);
		if (bank == null) {
			bank = new Bank();
			bank.setName("Coronation Merchant Bank");
			bank.setBankCode(Constants.BANK_CODE);
			bankRepository.saveAndFlush(bank);
		}
	}

	private void seedRoles() {
		List<String> defaultRoles = Arrays.asList("MERCHANT_INITIATOR", "MERCHANT_SUPERVISOR", "MERCHANT_ADMIN"
			, "MERCHANT_ORGANIZATION_ADMIN", "DISTRIBUTOR", "RELATIONSHIP_MGR", "RELATIONSHIP_MGR_SUPERVISOR");
		List<Role> roles = roleRepository.findAll();
		List<String> addedRoles = roles.stream().map(role -> role.getName()).collect(Collectors.toList());
		defaultRoles.forEach(roleName -> {
			if (!addedRoles.contains(roleName)) {
				Role role = new Role();
				role.setName(roleName);
				roleRepository.saveAndFlush(role);
			}
		});
	}

	private void seedMerchantTasks() {
	    Role role = roleRepository.findByName("MERCHANT_INITIATOR");
		role.getTasks().add(taskRepository.findByName(TaskType.VIEW_ORGANIZATIONS));
		role.getTasks().add(taskRepository.findByName(TaskType.ADD_ORGANIZATION_USER));
		role.getTasks().add(taskRepository.findByName(TaskType.VIEW_ACCOUNTS));
		role.getTasks().add(taskRepository.findByName(TaskType.CREATE_PRODUCT));
		role.getTasks().add(taskRepository.findByName(TaskType.CREATE_ACCOUNT));
		role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT_DISTRIBUTOR));
		role.getTasks().add(taskRepository.findByName(TaskType.CREATE_DISTRIBUTOR));
		role.getTasks().add(taskRepository.findByName(TaskType.VIEW_MERCHANTS));

		role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT));
		role.getTasks().add(taskRepository.findByName(TaskType.ADD_MERCHANT_USER));
		roleRepository.saveAndFlush(role);
    }
}
