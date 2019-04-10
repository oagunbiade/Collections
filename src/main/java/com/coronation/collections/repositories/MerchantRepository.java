package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coronation.collections.domain.Merchant;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long>,
        QuerydslPredicateExecutor<Merchant> {
    List<Merchant> findByOrganizationId(Long id);

}
