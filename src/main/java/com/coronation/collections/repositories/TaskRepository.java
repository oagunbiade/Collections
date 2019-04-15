package com.coronation.collections.repositories;

import com.coronation.collections.domain.Task;
import com.coronation.collections.domain.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Toyin on 4/4/19.
 */
public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findByName(TaskType name);
}
