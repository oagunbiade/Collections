package com.coronation.collections.services;


import com.coronation.collections.domain.Merchant;
import com.coronation.collections.domain.MerchantAccount;
import com.coronation.collections.domain.Product;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.ApprovalDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
	Page<Product> listAll(BooleanExpression expression, Pageable pageable);
	Product findById(Long id);
	Product save(Product product, Merchant merchant, MerchantAccount merchantAccount);
    Product update(Product prev, Product current);
    Product updateAccount(Product product, MerchantAccount merchantAccount);
    Product approveProduct(Product product, ApprovalDto approvalDto);
    Product revert(Product product);
    Product approveAccount(Product product, ApprovalDto approvalDto);
    Product delete(Product product);
    List<Product> findByMerchantId(Long merchantId);
    List<Product> merchantDistributorProducts(Long merchantId, Long distributorId);
    Product findByName(String name);
    Product findByCode(String code);
}
