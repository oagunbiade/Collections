package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Task;
import com.coronation.collections.domain.User;
import com.coronation.collections.repositories.TaskRepository;
import com.coronation.collections.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
@Service
public class TaskServiceImpl implements TaskService {
    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task create(Task task) {
        return taskRepository.saveAndFlush(task);
    }

    @Override
    public Task update(Task prev, Task current) {
        prev.setName(current.getName());
        prev.setModifiedAt(LocalDateTime.now());
        return taskRepository.saveAndFlush(prev);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task findByName(String name) {
        return taskRepository.findByNameEquals(name);
    }
}
