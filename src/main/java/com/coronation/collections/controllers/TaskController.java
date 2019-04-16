package com.coronation.collections.controllers;

import com.coronation.collections.domain.Task;
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
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('CREATE_TASK')")
    @PostMapping
    public ResponseEntity<Task> create(@RequestBody @Valid Task task, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else if (taskService.findByName(task.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.ok(taskService.create(task));
        }
    }

    @PreAuthorize("hasRole('EDIT_TASK')")
    @PutMapping("/{id}")
    public ResponseEntity<Task> edit(@PathVariable("id") Long id, @RequestBody @Valid Task task,
                                     BindingResult bindingResult) {
        Task previous = taskService.findById(task.getId());
        if (previous == null) {
            return ResponseEntity.notFound().build();
        } else {
            try {
                return ResponseEntity.ok(taskService.edit(previous, task));
            } catch (DataIntegrityViolationException dve) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_TASKS')")
    public ResponseEntity<List<Task>> listTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }

}
