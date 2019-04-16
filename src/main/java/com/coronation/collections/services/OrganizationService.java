package com.coronation.collections.services;

import com.coronation.collections.domain.Organization;
import com.coronation.collections.domain.OrganizationUser;
import com.coronation.collections.domain.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationService {
	Page<Organization> listAll(BooleanExpression expression, Pageable pageable);
	Organization findById(Long id);
	Organization save(Organization organization);
	Organization update(Organization prev, Organization current);
	Organization delete(Organization organization);
	Organization findByName(String name);
	Organization deactivateOrActivate(Organization organization);
	OrganizationUser addUser(Organization organization, User user);
	List<OrganizationUser> organizationUsers(Long id);
	OrganizationUser findByUserId(Long userId);
}
