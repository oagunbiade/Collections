package com.coronation.collections.services.impl;

import com.coronation.collections.domain.Organization;
import com.coronation.collections.domain.User;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.repositories.OrganizationRepository;
import com.coronation.collections.services.OrganizationService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	private OrganizationRepository organizationRepository;

	@Autowired
	public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	@Override
	public Page<Organization> listAll(BooleanExpression expression, Pageable pageable) {
		return organizationRepository.findAll(expression, pageable);
	}

	@Override
	public Organization findById(Long id) {
		return organizationRepository.findById(id).orElse(null);
	}

	@Override
	public Organization save(Organization organization) {
		return organizationRepository.saveAndFlush(organization);
	}

	@Override
	public Organization update(Organization prev, Organization current) {
		prev.setName(current.getName());
		prev.setModifiedAt(LocalDateTime.now());
		return organizationRepository.saveAndFlush(prev);
	}

	@Override
	public Organization delete(Organization organization) {
		organization.setDeleted(Boolean.TRUE);
		return organizationRepository.saveAndFlush(organization);
	}

	@Override
	public Organization findByName(String name) {
		return organizationRepository.findByNameEquals(name);
	}

}
