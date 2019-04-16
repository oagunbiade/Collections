package com.coronation.collections.repositories;

import com.coronation.collections.domain.UserAuditTrail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.sql.Date;

/**
 * Created by Toyin on 4/8/19.
 */
public interface UserAuditTrailRepository extends JpaRepository<UserAuditTrail, Long>,
        QuerydslPredicateExecutor<UserAuditTrail> {
    @Query(value="Select a from UserAuditTrail a where "
            + "a.createdAt between ?1 and ?2")
    Page<UserAuditTrail> findTrailsByDate(Date startDate, Date endDate, Pageable pageRequest);
}
