package com.coronation.collections.services;

import com.coronation.collections.domain.Organization;
import com.coronation.collections.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface OrganizationService {
	Page<Organization> listAll(BooleanExpression expression, Pageable pageable);
	Organization findById(Long id);
	Organization save(Organization organization);
	Organization update(Organization prev, Organization current);
	Organization delete(Organization organization);
	Organization findByName(String name);
}
