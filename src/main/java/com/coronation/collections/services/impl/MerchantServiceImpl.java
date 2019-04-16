package com.coronation.collections.services.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.repositories.MerchantAccountRepository;
import com.coronation.collections.repositories.MerchantUserRepository;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.util.JsonConverter;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coronation.collections.repositories.MerchantRepository;

@Service
public class MerchantServiceImpl implements MerchantService {
	
	private MerchantRepository merchantRepository;
	private MerchantAccountRepository merchantAccountRepository;
	private MerchantUserRepository merchantUserRepository;

	@Autowired
	public MerchantServiceImpl(MerchantRepository merchantRepository,
	   	MerchantAccountRepository merchantAccountRepository, MerchantUserRepository
					   merchantUserRepository) {
		this.merchantRepository = merchantRepository;
		this.merchantAccountRepository = merchantAccountRepository;
		this.merchantUserRepository = merchantUserRepository;
	}

	@Override
	public Page<Merchant> listMerchants(BooleanExpression expression, Pageable pageable) {
		return merchantRepository.findAll(expression, pageable);
	}

	@Override
	public List<Merchant> findByOrganizationId(Long id) {
		return merchantRepository.findByOrganizationId(id);
	}

	@Override
	public Merchant findById(Long id) {
		return merchantRepository.findById(id).orElse(null);
	}

	@Override
	public Merchant save(Merchant merchant, Organization organization) {
		merchant.setOrganization(organization);
		return merchantRepository.saveAndFlush(merchant);
	}

	@Override
	public Merchant update(Merchant prevMerchant, Merchant newMerchant) {
		prevMerchant.setEditMode(Boolean.TRUE);
		prevMerchant.setUpdateData(JsonConverter.getJson(newMerchant));
		return merchantRepository.saveAndFlush(prevMerchant);
	}

	@Override
	public Merchant delete(Merchant merchant) {
		merchant.setDeleted(Boolean.TRUE);
		merchant.setModifiedAt(LocalDateTime.now());
		return merchantRepository.saveAndFlush(merchant);
	}

	@Override
	public Merchant approve(Merchant merchant, ApprovalDto approvalDto) {
		if (approvalDto.getApprove()) {
			if (merchant.getEditMode() && merchant.getUpdateData() != null) {
				Merchant edit = JsonConverter.getElement(merchant.getUpdateData(), Merchant.class);
				merchant.setEditMode(Boolean.FALSE);
				merchant.setAddress(edit.getAddress());
				merchant.setCity(edit.getCity());
				merchant.setEmail(edit.getEmail());
				merchant.setMerchantCode(edit.getMerchantCode());
				merchant.setMerchantName(edit.getMerchantName());
				merchant.setPhone(edit.getPhone());
				merchant.setModifiedAt(edit.getCreatedAt());
				merchant.setUpdateData(null);
				merchant.setRejectReason(null);
			}
			merchant.setStatus(GenericStatus.ACTIVE);
		} else {
			merchant.setRejectReason(approvalDto.getReason());
			if (merchant.getEditMode()) {
				merchant.setEditMode(Boolean.FALSE);
			} else {
				merchant.setStatus(GenericStatus.REJECTED);
			}
		}
		return merchantRepository.saveAndFlush(merchant);
	}

	@Override
	public MerchantAccount findByAccountId(Long accountId) {
		return merchantAccountRepository.findByAccountId(accountId);
	}

	@Override
	public MerchantAccount addAccount(Merchant merchant, Account account) {
		MerchantAccount merchantAccount = new MerchantAccount();
		merchantAccount.setAccount(account);
		merchantAccount.setMerchant(merchant);
		return merchantAccountRepository.saveAndFlush(merchantAccount);
	}

	@Override
	public MerchantAccount deleteAccount(MerchantAccount merchantAccount) {
		merchantAccount.setDeleted(Boolean.TRUE);
		merchantAccount.setModifiedAt(LocalDateTime.now());
		return merchantAccountRepository.saveAndFlush(merchantAccount);
	}

	@Override
	public List<MerchantAccount> merchantAccounts(Long merchantId) {
		return merchantAccountRepository.findByMerchantId(merchantId);
	}

	@Override
	public MerchantUser findByOrganizationUserId(Long userId) {
		return merchantUserRepository.findByOrganizationUserId(userId);
	}

	@Override
	public MerchantUser addUser(OrganizationUser user, Merchant merchant) {
		MerchantUser merchantUser = new MerchantUser();
		merchantUser.setOrganizationUser(user);
		merchantUser.setMerchant(merchant);
		return merchantUserRepository.saveAndFlush(merchantUser);
	}

	@Override
	public List<MerchantUser> findMerchantUsers(Long id) {
		return merchantUserRepository.findByMerchantId(id);
	}

	@Override
	public Merchant addAuthenticationDetail(Merchant merchant, AuthenticationDetail authenticationDetail) {
		merchant.setAuthenticationDetail(authenticationDetail);
		merchant.setModifiedAt(LocalDateTime.now());
		return merchantRepository.saveAndFlush(merchant);
	}

	@Override
	public MerchantAccount approveAccount(MerchantAccount merchantAccount, ApprovalDto approvalDto) {
		if (approvalDto.getApprove()) {
			merchantAccount.getAccount().setStatus(GenericStatus.ACTIVE);
		} else {
			merchantAccount.getAccount().setStatus(GenericStatus.REJECTED);
			merchantAccount.setRejectReason(approvalDto.getReason());
		}
		return merchantAccountRepository.saveAndFlush(merchantAccount);
	}

	@Override
	public Merchant deactivateOrActivate(Merchant merchant) {
		if (merchant.getStatus().equals(GenericStatus.ACTIVE)) {
			merchant.setStatus(GenericStatus.DEACTIVATED);
		} else {
			merchant.setStatus(GenericStatus.ACTIVE);
		}
		merchant.setModifiedAt(LocalDateTime.now());
		return merchantRepository.saveAndFlush(merchant);
	}
}
