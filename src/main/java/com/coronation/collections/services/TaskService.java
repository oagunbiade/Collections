package com.coronation.collections.services;

import com.coronation.collections.domain.Task;
import com.coronation.collections.domain.User;

import java.util.List;

/**
 * Created by Toyin on 4/4/19.
 */
public interface TaskService {
    Task create(Task task);
    Task update(Task prev, Task current);
    List<Task> findAll();
    Task findById(Long id);
    Task findByName(String name);
}
