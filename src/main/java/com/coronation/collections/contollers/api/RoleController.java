package com.coronation.collections.contollers.api;

import com.coronation.collections.domain.Role;
import com.coronation.collections.domain.Task;
import com.coronation.collections.services.RoleService;
import com.coronation.collections.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private RoleService roleService;
    private TaskService taskService;

    @Autowired
    public RoleController(RoleService roleService, TaskService taskService) {
        this.roleService = roleService;
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('CREATE_ROLE')")
    @PostMapping
    public ResponseEntity<Role> create(@RequestBody @Valid Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (roleService.findByName(role.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(roleService.save(role));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<Role> edit(@PathVariable("id") Long id,
                     @RequestBody @Valid Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Role previous = roleService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                    return ResponseEntity.ok(roleService.update(previous, role));
                } catch (DataIntegrityViolationException dve) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('ADD_ROLE_TASK')")
    @PostMapping ("/{id}/tasks/{taskId}")
    public ResponseEntity<Role> addTaskToRole(@PathVariable("id") Long id, @PathVariable("taskId") Long taskId) {
        Role role = roleService.findById(id);
        Task task = taskService.findById(taskId);
        if (role == null || task == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(roleService.addTaskToRole(role, task));
        }
    }

    @PreAuthorize("hasRole('DELETE_ROLE_TASK')")
    @DeleteMapping ("/{id}/tasks/{taskId}")
    public ResponseEntity<Role> removeTaskFromRole(@PathVariable("id") Long id, @PathVariable("taskId") Long taskId) {
        Role role = roleService.findById(id);
        Task task = taskService.findById(taskId);
        if (role == null || task == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(roleService.removeTaskFromRole(role, task));
        }
    }

    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }
}
