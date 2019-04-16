package com.coronation.collections.repositories;

import com.coronation.collections.domain.OrganizationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

/**
 * Created by Toyin on 4/8/19.
 */
public interface OrganizationUserRepository extends JpaRepository<OrganizationUser, Long>,
        QuerydslPredicateExecutor<OrganizationUser> {
    List<OrganizationUser> findByOrganizationId(Long id);
    OrganizationUser findByUserId(Long id);
}
