package com.coronation.collections.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coronation.collections.domain.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>,
        QuerydslPredicateExecutor<Product> {
	List<Product> findByMerchantId(Long id);
	Long countByMerchantId(Long id);
    @Query("select distinct p.product from Payment p where p.merchant.id = ?1 and p.distributor.id = ?2")
    List<Product> findDistributorProducts(Long merchantId, Long distributorId);
    Product findByCode(String code);
    Product findByName(String name);
}
