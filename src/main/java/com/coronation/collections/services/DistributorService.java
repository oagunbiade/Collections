package com.coronation.collections.services;

import com.coronation.collections.domain.*;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.dto.StringValue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 4/9/19.
 */
public interface DistributorService {
    Distributor create(Distributor distributor);
    Distributor approve(Distributor distributor, ApprovalDto approvalDto);
    MerchantDistributor addToMerchant(Distributor distributor, Merchant merchant, String rfpCode);
    MerchantDistributor deleteDistributor(MerchantDistributor merchantDistributor);
    MerchantDistributor approveMerchantDistributor(MerchantDistributor merchantDistributor, ApprovalDto approvalDto);
    MerchantDistributor editMerchantDistributor(MerchantDistributor prev, MerchantDistributor current);
    MerchantDistributor findByMerchantDistributorId(Long id);
    Distributor update(Distributor prev, Distributor current);
    Distributor findByBvn(String bvn);
    Distributor findByName(String name);
    Distributor findById(Long id);
    MerchantDistributor findByMerchantIdAndDistributorId(Long merchantId, Long distributorId);
    MerchantDistributor findByMerchantIdAndRfpCode(Long merchantId, String rfpCode);
    List<MerchantDistributor> findByMerchantId(Long id);

    DistributorAccount findByAccountId(Long accountId);
    DistributorAccount addAccount(Distributor distributor, Account account, Boolean defaultAccount);
    List<DistributorAccount> distributorAccounts(Long distributorId);
    Collection<DistributorAccount> setDefaultAccount(Collection<DistributorAccount> accounts,
                                                     DistributorAccount defaultAccount);
    Optional<DistributorAccount> getDefaultAccount(Collection<DistributorAccount> distributorAccounts);
    DistributorAccount findByAccountNumber(String accountNumber);

    DistributorUser findByUserId(Long userId);
    DistributorUser addUser(User user, Distributor distributor);
    List<DistributorUser> distributorUsers(Long id);

    Distributor deactivateOrActivate(Distributor distributor);
    Long countAll();
    Long countByOrganization(Long organizationId);
    Long countByMerchant(Long merchantId);
}
