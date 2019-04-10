package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coronation.collections.domain.Organization;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OrganizationRepository extends  JpaRepository<Organization, Long>,
        QuerydslPredicateExecutor<Organization> {
    Organization findByNameEquals(String name);
}
