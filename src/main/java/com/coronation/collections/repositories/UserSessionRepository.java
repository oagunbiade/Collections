package com.coronation.collections.repositories;

import com.coronation.collections.domain.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by Toyin on 4/8/19.
 */
public interface UserSessionRepository extends JpaRepository<UserSession, Long>,
        QuerydslPredicateExecutor<UserSession> {
}
