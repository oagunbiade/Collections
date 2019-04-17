package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.dto.StringValue;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.AccountService;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.UserService;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/vi/distributors")
public class DistributorController {
    private DistributorService distributorService;
    private MerchantService merchantService;
    private AccountService accountService;
    private UserService userService;

    @Autowired
    public DistributorController(DistributorService distributorService, MerchantService merchantService,
                                 AccountService accountService, UserService userService) {
        this.distributorService = distributorService;
        this.merchantService = merchantService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Transactional
    @PreAuthorize("hasRole('CREATE_DISTRIBUTOR')")
    @PostMapping("/merchants/{merchantId}")
    public ResponseEntity<MerchantDistributor> create(@PathVariable("merchantId") Long merchantId,
                      @RequestBody @Valid Distributor distributor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            if (distributorService.findByBvn(distributor.getBvn()) == null &&
                    distributorService.findByName(distributor.getName()) == null) {
                Merchant merchant = merchantService.findById(merchantId);
                if (merchant == null) {
                    return ResponseEntity.notFound().build();
                } else {
                    String rfpCode = distributor.getRfpCode();
                    distributor = distributorService.create(distributor);
                    return ResponseEntity.ok(distributorService.addToMerchant(distributor, merchant, rfpCode));
                }
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }
    }

    @Transactional
    @PreAuthorize("hasRole('APPROVE_DISTRIBUTOR')")
    @PostMapping("/merchants/{id}/approve")
    public ResponseEntity<MerchantDistributor> approveMerchantDistributor(@PathVariable("id") Long id,
                  @RequestBody @Valid ApprovalDto approvalDto,
              BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            MerchantDistributor merchantDistributor = distributorService.findByMerchantDistributorId(id);
            if (merchantDistributor == null) {
                return ResponseEntity.notFound().build();
            }
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchantDistributor.getMerchant(), user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                if (!merchantDistributor.getDistributor().getStatus().equals(GenericStatus.ACTIVE)) {
                    try {
                        distributorService.approve(merchantDistributor.getDistributor(), approvalDto);
                    } catch (DataIntegrityViolationException dve) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                }
                return ResponseEntity.ok(distributorService.
                        approveMerchantDistributor(merchantDistributor, approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('ADD_MERCHANT_DISTRIBUTOR')")
    @PostMapping("/{id}/merchants/{merchantId}")
    public ResponseEntity<MerchantDistributor> addDistributorToMerchant(@PathVariable("id") Long id,
            @PathVariable("merchantId") Long merchantId, @RequestBody StringValue stringValue,
                                @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        Distributor distributor = distributorService.findById(id);
        if (merchant == null || distributor == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.ok(distributorService.
                        addToMerchant(distributor, merchant, stringValue.getValue()));
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_DISTRIBUTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Distributor> edit(@PathVariable("id") Long id,
                @RequestBody @Valid Distributor distributor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Distributor previous = distributorService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(distributorService.update(previous, distributor));
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_DISTRIBUTOR')")
    @PutMapping("/merchants/{id}")
    public ResponseEntity<MerchantDistributor> edit(@PathVariable("id") Long id,
            @RequestBody @Valid MerchantDistributor merchantDistributor, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            MerchantDistributor previous = distributorService.findByMerchantDistributorId(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(distributorService.editMerchantDistributor(previous, merchantDistributor));
            }
        }
    }

    @PreAuthorize("hasRole('DELETE_DISTRIBUTOR')")
    @DeleteMapping("{id}/merchants/{merchantId}")
    public ResponseEntity<MerchantDistributor> delete(@PathVariable("id") Long id,
              @PathVariable("merchantId") Long merchantId,
              @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        MerchantDistributor merchantDistributor =
                distributorService.findByMerchantIdAndDistributorId(merchantId, id);
        if (merchantDistributor == null || merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.ok(distributorService.deleteDistributor(merchantDistributor));
            }
        }
    }

    @PreAuthorize("hasRole('APPROVE_DISTRIBUTOR')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Distributor> approveDistributor(@PathVariable("id") Long id,
          @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Distributor distributor = distributorService.findById(id);
            if (distributor == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(distributorService.approve(distributor, approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_DISTRIBUTORS')")
    @GetMapping("/bvn/{bvn}")
    public ResponseEntity<Distributor> findByBvn(@PathVariable("bvn") String bvn) {
        Distributor distributor = distributorService.findByBvn(bvn);
        if (bvn == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(distributor);
        }
    }

    @PreAuthorize("hasRole('VIEW_DISTRIBUTORS')")
    @GetMapping("/merchants/{merchantId}/rfp/{rfpCode}")
    public ResponseEntity<MerchantDistributor> findByBvn(@PathVariable("merchantId") Long merchantId,
                             @PathVariable("rfpCode") String rfpCode) {
        MerchantDistributor distributor = distributorService.findByMerchantIdAndRfpCode(merchantId, rfpCode);
        if (distributor == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(distributor);
        }
    }

    @PreAuthorize("hasRole('CREATE_ACCOUNT')")
    @PostMapping("/{id}/accounts")
    public ResponseEntity<DistributorAccount> createAccount(@PathVariable("id") Long id,
            @RequestBody @Valid Account account, BindingResult bindingResult,
                        @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Distributor distributor = distributorService.findById(id);
            if (distributor == null) {
                return ResponseEntity.notFound().build();
            } else {
                User user = profileDetails.toUser();
                if (GenericUtil.isDistributorUser(user.getRole()) &&
                        !GenericUtil.isDistributorUser(distributor, user, distributorService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    if (accountService.findByAccountNumber(account.getAccountNumber()) == null) {
                        account = accountService.create(account);
                        List<DistributorAccount> distributorAccounts = distributorService.distributorAccounts(id);
                        return ResponseEntity.ok(distributorService.addAccount(distributor, account,
                                distributorAccounts.isEmpty()));
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                }
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_ACCOUNTS')")
    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<DistributorAccount>> getAccounts(@PathVariable("id") Long id) {
        Distributor distributor = distributorService.findById(id);
        if (distributor == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(distributorService.distributorAccounts(id));
        }
    }

    @PreAuthorize("hasRole('CREATE_ACCOUNT')")
    @PostMapping("/{id}/accounts/{accountId}/default")
    public ResponseEntity<Collection<DistributorAccount>> setDefaultAccount(@PathVariable("id") Long id,
            @PathVariable("accountId") Long accountId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Distributor distributor = distributorService.findById(id);
        DistributorAccount distributorAccount = distributorService.findByAccountId(accountId);
        if (distributor == null || distributorAccount == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (distributor.equals(distributorAccount.getDistributor())) {
                if (GenericUtil.isDistributorUser(user.getRole()) &&
                        !GenericUtil.isDistributorUser(distributor, user, distributorService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    List<DistributorAccount> distributorAccounts = distributorService.distributorAccounts(id);
                    return ResponseEntity.ok(distributorService.
                            setDefaultAccount(distributorAccounts, distributorAccount));
                }
            } else {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
    }

    @PreAuthorize("hasRole('ADD_DISTRIBUTOR_USER')")
    @PostMapping("/{id}/users/{userId}")
    public ResponseEntity<DistributorUser> addUser(@PathVariable("id") Long id,
                               @PathVariable("userId") Long userId){
        Distributor distributor = distributorService.findById(id);
        User user = userService.findById(userId);
        if (distributor == null || user == null) {
            return ResponseEntity.notFound().build();
        } else {
            DistributorUser distributorUser = distributorService.findByUserId(userId);
            if (distributorUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.ok(distributorService.addUser(user, distributor));
        }
    }

    @PreAuthorize("hasRole('VIEW_USERS')")
    @GetMapping("/{id}/users")
    public ResponseEntity<List<DistributorUser>> viewUsers(@PathVariable("id") Long id){
        Distributor distributor = distributorService.findById(id);

        if (distributor == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(distributorService.distributorUsers(id));
        }
    }
 }
