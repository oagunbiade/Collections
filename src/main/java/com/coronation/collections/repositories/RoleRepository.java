package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coronation.collections.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String roleName);
}
