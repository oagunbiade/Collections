package com.coronation.collections.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.coronation.collections.domain.Merchant;
import com.coronation.collections.domain.MerchantAccount;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.repositories.MerchantAccountRepository;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.util.JsonConverter;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.coronation.collections.domain.Product;
import com.coronation.collections.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	private ProductRepository productRepository;
	private MerchantAccountRepository accountRepository;

	@Autowired
	public ProductServiceImpl(ProductRepository productRepository, MerchantAccountRepository accountRepository) {
		this.productRepository = productRepository;
		this.accountRepository = accountRepository;
	}

	@Override
	public Page<Product> listAll(BooleanExpression expression, Pageable pageable) {
		return productRepository.findAll(expression, pageable);
	}

	@Override
	public Product findById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product save(Product product, Merchant merchant, MerchantAccount merchantAccount) {
		product.setMerchant(merchant);
		product.setAccount(merchantAccount);
		return productRepository.saveAndFlush(product);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product update(@Param("product")Product prev, Product current) {
		prev.setEditMode(Boolean.TRUE);
		prev.setUpdateData(JsonConverter.getJson(current));
		return productRepository.saveAndFlush(prev);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product updateAccount(@Param("product") Product product, MerchantAccount merchantAccount) {
		product.setAccountUpdateData(JsonConverter.getJson(merchantAccount));
		product.setEditMode(Boolean.TRUE);
		return productRepository.saveAndFlush(product);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product approveProduct(@Param("product") Product product, ApprovalDto approvalDto) {
		if (approvalDto.getApprove()) {
			if (product.getEditMode() && product.getUpdateData() != null) {
				Product edit = JsonConverter.getElement(product.getUpdateData(), Product.class);
				product.setComment(edit.getComment());
				product.setMinAmount(edit.getMinAmount());
				product.setCode(edit.getCode());
				product.setName(edit.getName());
				product.setModifiedAt(edit.getCreatedAt());
				product.setRejectReason(null);
				product.setUpdateData(null);
				if (product.getAccountUpdateData() == null) {
					product.setEditMode(Boolean.FALSE);
				}
			}
			product.setStatus(GenericStatus.ACTIVE);
		} else {
			product.setRejectReason(approvalDto.getReason());
			if (product.getEditMode()) {
				if (product.getAccountUpdateData() == null) {
					product.setEditMode(Boolean.FALSE);
				}
			} else {
				product.setStatus(GenericStatus.REJECTED);
			}
		}
		return productRepository.saveAndFlush(product);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product revert(@Param("product")Product product) {
		product.setEditMode(Boolean.FALSE);
		product.setUpdateData(null);
		product.setAccountUpdateData(null);
		product.setModifiedAt(LocalDateTime.now());
		return productRepository.saveAndFlush(product);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product approveAccount(@Param("product")Product product, ApprovalDto approvalDto) {
		if (approvalDto.getApprove()) {
			if (product.getAccountUpdateData() != null) {
				MerchantAccount account =
					JsonConverter.getElement(product.getAccountUpdateData(), MerchantAccount.class);
				account = accountRepository.findById(account.getId()).orElse(null);
				if (account != null) {
					product.setAccount(account);
				}
				product.setModifiedAt(LocalDateTime.now());
				product.setAccountUpdateData(null);
				product.setRejectReason(null);
			}
		} else {
			product.setRejectReason(approvalDto.getReason());
		}
		if (product.getUpdateData() == null) {
			product.setEditMode(Boolean.FALSE);
		}
		return productRepository.saveAndFlush(product);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product delete(@Param("product")Product product) {
		product.setDeleted(Boolean.TRUE);
		product.setModifiedAt(LocalDateTime.now());
		return productRepository.saveAndFlush(product);
	}

	@Override
	public List<Product> findByMerchantId(Long merchantId) {
		return productRepository.findByMerchantId(merchantId);
	}

	@Override
	public List<Product> merchantDistributorProducts(Long merchantId, Long distributorId) {
		return productRepository.findDistributorProducts(merchantId, distributorId);
	}

	@PostFilter("hasPermission(filterObject, 'READ')")
	@Override
	public Product findByName(String name) {
		return productRepository.findByName(name);
	}

	@PostFilter("hasPermission(filterObject, 'READ')")
	@Override
	public Product findByCode(String code) {
		return productRepository.findByCode(code);
	}

	@PreAuthorize("hasPermission(#product, 'WRITE')")
	@Override
	public Product deactivateOrActivate(@Param("product")Product product) {
		if (product.getStatus().equals(GenericStatus.ACTIVE)) {
			product.setStatus(GenericStatus.DEACTIVATED);
		} else {
			product.setStatus(GenericStatus.ACTIVE);
		}
		product.setModifiedAt(LocalDateTime.now());
		return productRepository.saveAndFlush(product);
	}

	@Override
	public Long countAll() {
		return productRepository.count();
	}

	@Override
	public Long countByMerchantId(Long merchantId) {
		return productRepository.countByMerchantId(merchantId);
	}

	@Override
	public Long countByOrganizationId(Long organizationId) {
		return productRepository.countByMerchantOrganizationId(organizationId);
	}
}
