package com.coronation.collections.repositories;

import com.coronation.collections.domain.DistributorUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface DistributorUserRepository extends JpaRepository<DistributorUser, Long>,
        QuerydslPredicateExecutor<DistributorUser> {
    List<DistributorUser> findByDistributorId(Long id);
    DistributorUser findByUserId(Long userId);
}
