package com.coronation.collections.services;

import com.coronation.collections.domain.*;
import com.coronation.collections.dto.ApprovalDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MerchantService {
    Page<Merchant> listMerchants(BooleanExpression expression, Pageable pageable);
    List<Merchant> findByOrganizationId(Long id);
	Merchant findById(Long id);
    Merchant save(Merchant merchant, Organization organization);
    Merchant update(Merchant prevMerchant, Merchant newMerchant);
    Merchant delete(Merchant merchant);
    Merchant approve(Merchant merchant, ApprovalDto approvalDto);
    MerchantAccount findByAccountId(Long accountId);
    MerchantAccount addAccount(Merchant merchant, Account account);
    MerchantAccount deleteAccount(MerchantAccount merchantAccount);
    List<MerchantAccount> merchantAccounts(Long merchantId);
    MerchantUser findByOrganizationUserId(Long userId);
    MerchantUser addUser(OrganizationUser user, Merchant merchant);
    List<MerchantUser> findMerchantUsers(Long id);
    Merchant addAuthenticationDetail(Merchant merchant, AuthenticationDetail authenticationDetail);
    MerchantAccount approveAccount(MerchantAccount merchantAccount, ApprovalDto approvalDto);
    Merchant deactivateOrActivate(Merchant merchant);
}
