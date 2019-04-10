package com.coronation.collections.repositories;

import com.coronation.collections.domain.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Created by Toyin on 4/8/19.
 */
public interface DistributorRepository extends JpaRepository<Distributor, Long>,
        QuerydslPredicateExecutor<Distributor> {
    Distributor findByBvn(String bvn);
    Distributor findByNameEquals(String name);
}
