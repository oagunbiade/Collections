package com.coronation.collections.services.impl;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.ApprovalDto;
import org.springframework.data.repository.query.Param;
import com.coronation.collections.repositories.DistributorAccountRepository;
import com.coronation.collections.repositories.DistributorRepository;
import com.coronation.collections.repositories.DistributorUserRepository;
import com.coronation.collections.repositories.MerchantDistributorRepository;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.util.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 4/9/19.
 */
@Service
public class DistributorServiceImpl implements DistributorService {
    private DistributorRepository distributorRepository;
    private MerchantDistributorRepository merchantDistributorRepository;
    private DistributorAccountRepository distributorAccountRepository;
    private DistributorUserRepository distributorUserRepository;

    @Autowired
    public DistributorServiceImpl(DistributorRepository distributorRepository,
                                  MerchantDistributorRepository merchantDistributorRepository,
                                  DistributorAccountRepository distributorAccountRepository,
                                  DistributorUserRepository distributorUserRepository) {
        this.distributorRepository = distributorRepository;
        this.merchantDistributorRepository = merchantDistributorRepository;
        this.distributorAccountRepository = distributorAccountRepository;
        this.distributorUserRepository = distributorUserRepository;
    }

    @Override
    public Distributor create(Distributor distributor) {
        return distributorRepository.saveAndFlush(distributor);
    }

    @Override
    public Distributor approve(Distributor distributor, ApprovalDto approvalDto) {
        if (approvalDto.getApprove()) {
            if (distributor.getEditMode() && distributor.getUpdateData() != null) {
                Distributor edit = JsonConverter.getElement(distributor.getUpdateData(), Distributor.class);
                distributor.setEditMode(Boolean.FALSE);
                distributor.setAddress(edit.getAddress());
                distributor.setBvn(edit.getBvn());
                distributor.setContact(edit.getContact());
                distributor.setName(edit.getName());
                distributor.setModifiedAt(edit.getCreatedAt());
                distributor.setUpdateData(null);
                distributor.setRejectReason(null);
            }
            distributor.setStatus(GenericStatus.ACTIVE);
        } else {
            distributor.setRejectReason(approvalDto.getReason());
            if (distributor.getEditMode()) {
                distributor.setEditMode(Boolean.FALSE);
            } else {
                distributor.setStatus(GenericStatus.REJECTED);
            }
        }
        return distributorRepository.saveAndFlush(distributor);
    }

    @Override
    public MerchantDistributor addToMerchant(Distributor distributor, Merchant merchant, String rfpCode) {
        MerchantDistributor merchantDistributor = new MerchantDistributor();
        merchantDistributor.setMerchant(merchant);
        merchantDistributor.setDistributor(distributor);
        if (rfpCode == null || rfpCode.isEmpty()) {
            merchantDistributor.setRfpCode(distributor.getId().toString());
        } else {
            merchantDistributor.setRfpCode(rfpCode);
        }
        return merchantDistributorRepository.saveAndFlush(merchantDistributor);
    }

    @Override
    @PreAuthorize("hasPermission(#merchantDistributor, 'WRITE')")
    public MerchantDistributor deleteDistributor(@Param("merchantDistributor")
                                 MerchantDistributor merchantDistributor) {
        merchantDistributor.setDeleted(Boolean.TRUE);
        merchantDistributor.setModifiedAt(LocalDateTime.now());
        return merchantDistributorRepository.saveAndFlush(merchantDistributor);
    }

    @Override
    @PreAuthorize("hasPermission(#merchantDistributor, 'WRITE')")
    public MerchantDistributor approveMerchantDistributor(@Param("merchantDistributor")
                  MerchantDistributor merchantDistributor, ApprovalDto approvalDto) {
        if (approvalDto.getApprove()) {
            if (merchantDistributor.getEditMode() && merchantDistributor.getUpdateData() != null) {
                MerchantDistributor edit = JsonConverter.getElement
                        (merchantDistributor.getUpdateData(), MerchantDistributor.class);
                merchantDistributor.setEditMode(Boolean.FALSE);
                merchantDistributor.setRfpCode(edit.getRfpCode());
                merchantDistributor.setModifiedAt(edit.getCreatedAt());
                merchantDistributor.setUpdateData(null);
                merchantDistributor.setRejectReason(null);
                merchantDistributor.getDistributor().setStatus(GenericStatus.ACTIVE);
            }
            merchantDistributor.setStatus(GenericStatus.ACTIVE);
        } else {
            merchantDistributor.setRejectReason(approvalDto.getReason());
            if (merchantDistributor.getEditMode()) {
                merchantDistributor.setEditMode(Boolean.TRUE);
            } else {
                merchantDistributor.setStatus(GenericStatus.REJECTED);
            }
        }
        return merchantDistributorRepository.saveAndFlush(merchantDistributor);
    }

    @Override
    @PreAuthorize("hasPermission(#merchantDistributor, 'WRITE')")
    public MerchantDistributor editMerchantDistributor(@Param("merchantDistributor")
                                   MerchantDistributor prev, MerchantDistributor current) {
        prev.setEditMode(Boolean.TRUE);
        prev.setUpdateData(JsonConverter.getJson(current));
        return merchantDistributorRepository.saveAndFlush(prev);
    }

    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public MerchantDistributor findByMerchantDistributorId(Long id) {
        return merchantDistributorRepository.findById(id).orElse(null);
    }

    @Override
    public Distributor update(Distributor prev, Distributor current) {
        prev.setEditMode(Boolean.TRUE);
        prev.setUpdateData(JsonConverter.getJson(current));
        return distributorRepository.saveAndFlush(prev);
    }

    @Override
    public Distributor findByBvn(String bvn) {
        return distributorRepository.findByBvn(bvn);
    }

    @Override
    public Distributor findByName(String name) {
        return distributorRepository.findByNameEquals(name);
    }

    @Override
    public Distributor findById(Long id) {
        return distributorRepository.findById(id).orElse(null);
    }

    @Override
    public DistributorAccount findByAccountId(Long accountId) {
        return distributorAccountRepository.findByAccountId(accountId);
    }

    @Override
    public DistributorAccount addAccount(Distributor distributor, Account account, Boolean defaultAccount) {
        DistributorAccount distributorAccount = new DistributorAccount();
        distributorAccount.setAccount(account);
        distributorAccount.setDistributor(distributor);
        distributorAccount.setDefaultAccount(defaultAccount);
        return distributorAccountRepository.saveAndFlush(distributorAccount);
    }

    @Override
    public List<DistributorAccount> distributorAccounts(Long distributorId) {
        return distributorAccountRepository.findByDistributorId(distributorId);
    }

    @Override
    public DistributorUser findByUserId(Long userId) {
        return distributorUserRepository.findByUserId(userId);
    }

    @Override
    public DistributorUser addUser(User user, Distributor distributor) {
        DistributorUser distributorUser = new DistributorUser();
        distributorUser.setDistributor(distributor);
        distributorUser.setUser(user);
        return distributorUserRepository.saveAndFlush(distributorUser);
    }

    @Override
    public List<DistributorUser> distributorUsers(Long id) {
        return distributorUserRepository.findByDistributorId(id);
    }

    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public MerchantDistributor findByMerchantIdAndDistributorId(Long merchantId, Long distributorId) {
        return merchantDistributorRepository.findByMerchantIdAndDistributorId(merchantId, distributorId);
    }

    @Override
    public Collection<DistributorAccount> setDefaultAccount(Collection<DistributorAccount> accounts,
                                                            DistributorAccount defaultAccount) {
        accounts.forEach(a -> {
            if (a.equals(defaultAccount)) {
                a.setDefaultAccount(Boolean.TRUE);
                a.setModifiedAt(LocalDateTime.now());
            } else {
                a.setDefaultAccount(Boolean.FALSE);
            }
        });
        distributorAccountRepository.saveAll(accounts);
        distributorAccountRepository.flush();
        return accounts;
    }

    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public MerchantDistributor findByMerchantIdAndRfpCode(Long merchantId, String rfpCode) {
        return merchantDistributorRepository.findByMerchantIdAndRfpCode(merchantId, rfpCode);
    }

    @Override
    @PostFilter("hasPermission(filterObject, 'READ')")
    public List<MerchantDistributor> findByMerchantId(Long id) {
        return merchantDistributorRepository.findByMerchantId(id);
    }

    @Override
    public Optional<DistributorAccount> getDefaultAccount(Collection<DistributorAccount> distributorAccounts) {
        return distributorAccounts.stream().filter(d -> d.getDefaultAccount()).findFirst();
    }

    @Override
    public DistributorAccount findByAccountNumber(String accountNumber) {
        return distributorAccountRepository.findByAccount_AccountNumber(accountNumber);
    }

    @Override
    public Distributor deactivateOrActivate(Distributor distributor) {
        if (distributor.getStatus().equals(GenericStatus.ACTIVE)) {
            distributor.setStatus(GenericStatus.DEACTIVATED);
        } else {
            distributor.setStatus(GenericStatus.ACTIVE);
        }
        distributor.setModifiedAt(LocalDateTime.now());
        return distributorRepository.saveAndFlush(distributor);
    }

    @Override
    public Long countAll() {
        return distributorRepository.count();
    }

    @Override
    public Long countByOrganization(Long organizationId) {
        return merchantDistributorRepository.countByMerchantOrganizationId(organizationId);
    }

    @Override
    public Long countByMerchant(Long merchantId) {
        return merchantDistributorRepository.countByMerchantId(merchantId);
    }
}
