package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.dto.AccountDetailResponse;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.exception.ApiException;
import com.coronation.collections.exception.DataEncryptionException;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.*;
import com.coronation.collections.services.impl.DomainSecurityService;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/merchants")
public class MerchantController {
    private MerchantService merchantService;
    private DistributorService distributorService;
    private AccountService accountService;
    private OrganizationService organizationService;
    private AuthenticationDetailService authenticationDetailService;
    private DomainSecurityService domainSecurityService;

    @Autowired
    public MerchantController(MerchantService merchantService, AccountService accountService,
              OrganizationService organizationService, DistributorService distributorService,
                          AuthenticationDetailService authenticationDetailService,
                              DomainSecurityService domainSecurityService) {
        this.merchantService = merchantService;
        this.distributorService = distributorService;
        this.accountService = accountService;
        this.organizationService = organizationService;
        this.authenticationDetailService = authenticationDetailService;
        this.domainSecurityService = domainSecurityService;
    }

    @PreAuthorize("hasRole('ADD_MERCHANT')")
    @PostMapping("/organizations/{organizationId}")
    public ResponseEntity<Merchant> create(@PathVariable("organizationId") Long organizationId,
                                           @RequestBody @Valid Merchant merchant, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Organization organization = organizationService.findById(organizationId);
            if (organization == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(merchantService.save(merchant, organization));
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_MERCHANT')")
    @PutMapping("/{id}")
    public ResponseEntity<Merchant> edit(@PathVariable("id") Long id, @RequestBody @Valid Merchant merchant,
             BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            Merchant previous = merchantService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (GenericUtil.isMerchantUser(user.getRole()) &&
                        !GenericUtil.isMerchantUser(previous, user, merchantService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    return ResponseEntity.ok(merchantService.update(previous, merchant));
                }
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_MERCHANTS')")
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> view(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchant);
        }
    }

    @PreAuthorize("hasRole('DELETE_MERCHANT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Merchant> delete(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.delete(merchant));
        }
    }

    @PreAuthorize("hasRole('ACTIVATE_MERCHANT')")
    @PostMapping("/{id}/status")
    public ResponseEntity<Merchant> activateOrDeactivate(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.deactivateOrActivate(merchant));
        }
    }

    @PreAuthorize("hasRole('APPROVE_MERCHANT')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Merchant> approve(@PathVariable("id") Long id,
        @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult,
                                @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            Merchant merchant = merchantService.findById(id);
            if (merchant == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (GenericUtil.isMerchantUser(user.getRole()) &&
                        !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                    return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    return ResponseEntity.ok(merchantService.approve(merchant, approvalDto));
                }
            }
        }
    }

    @PreAuthorize("hasRole('CREATE_ACCOUNT')")
    @PostMapping("/{id}/accounts/{accountNumber}")
    public ResponseEntity<MerchantAccount> createAccount(@PathVariable("id") Long id,
            @PathVariable("accountNumber") String accountNumber,
            @AuthenticationPrincipal ProfileDetails profileDetails) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else if (accountService.findByAccountNumber(accountNumber) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                try {
                    AccountDetailResponse detailResponse = accountService.fetchAccountDetails(accountNumber);
                    Account account = accountService.create(detailResponse.toAccount());
                    MerchantAccount merchantAccount = merchantService.addAccount(merchant, account);
                    domainSecurityService.addMerchantAccountPermissions(merchantAccount);
                    return ResponseEntity.ok(merchantAccount);
                } catch (ApiException e) {
                    throw new ResponseStatusException(
                            HttpStatus.valueOf(e.getStatusCode()), e.getMessage(), e);
                }
            }
        }
    }

    @PreAuthorize("hasRole('APPROVE_ACCOUNT')")
    @PostMapping("/accounts/{id}/approve")
    public ResponseEntity<MerchantAccount> approveAccount(@PathVariable Long id,
              @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult,
                                          @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            MerchantAccount merchantAccount = merchantService.findByMerchantAccountId(id);
            if (merchantAccount == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchantAccount.getMerchant(), user, merchantService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    return ResponseEntity.ok(merchantService.approveAccount(merchantAccount, approvalDto));
                }
            }
        }
    }

    @PreAuthorize("hasRole('DELETE_ACCOUNT')")
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<MerchantAccount> deleteAccount(@PathVariable Long id) {
        MerchantAccount merchantAccount = merchantService.findByMerchantAccountId(id);
        if (merchantAccount == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.deleteAccount(merchantAccount));
        }
    }

    @PostFilter("hasPermission(filterObject, 'READ')")
    @PreAuthorize("hasRole('VIEW_ACCOUNTS')")
    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<MerchantAccount>> getAccounts(@PathVariable Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.merchantAccounts(id));
        }
    }

    @PreAuthorize("hasRole('ADD_MERCHANT_USER')")
    @PostMapping("/{id}/users/{userId}")
    public ResponseEntity<MerchantUser> addUser(@PathVariable("id") Long id,
                                                @PathVariable("userId") Long userId,
                            @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        Merchant merchant = merchantService.findById(id);
        OrganizationUser user = organizationService.findByUserId(userId);
        if (merchant == null || user == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (GenericUtil.isMerchantUser(admin.getRole()) &&
                    (!GenericUtil.isMerchantUser(merchant, admin, merchantService))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                if (merchant.getOrganization().equals(user.getOrganization()) ||
                        GenericUtil.isRMUser(user.getUser().getRole())) {
                    try {
                        return ResponseEntity.ok(merchantService.addUser(user, merchant));
                    } catch (DataIntegrityViolationException dte) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                } else {
                    return ResponseEntity.unprocessableEntity().build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_MERCHANT_USERS')")
    @GetMapping("/{id}/users")
    public ResponseEntity<List<User>> getMerchantUsers(@PathVariable("id") Long id,
           @AuthenticationPrincipal ProfileDetails profileDetails) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            List<MerchantUser> merchantUsers = merchantService.findMerchantUsers(id);
            return ResponseEntity.ok(merchantUsers.stream().
                map(merchantUser -> merchantUser.getOrganizationUser().getUser()).collect(Collectors.toList()));
        }
    }

    @PostFilter("hasPermission(filterObject, 'READ')")
    @PreAuthorize("hasRole('VIEW_DISTRIBUTORS')")
    @GetMapping("/{id}/distributors")
    public ResponseEntity<List<MerchantDistributor>> getMerchantDistributors(@PathVariable("id") Long id,
                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(distributorService.findByMerchantId(id));
        }
    }

    @PreAuthorize("hasRole('VIEW_MERCHANTS')")
    @GetMapping
    public ResponseEntity<Page<Merchant>> listMerchants(@RequestParam(value="page",
            required = false, defaultValue = "0") int page,
            @RequestParam(value="pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value="name", required = false) String name,
            @RequestParam(value="phone", required = false) String phone,
            @RequestParam(value="email", required = false) String email,
            @RequestParam(value="code", required = false) String code,
            @RequestParam(value="status", required = false) GenericStatus status) {
        BooleanExpression filter = new CustomPredicateBuilder<>("merchant", Merchant.class)
                .with("merchantName", Operation.LIKE, name)
                .with("merchantCode", Operation.LIKE, code)
                .with("phone", Operation.LIKE, phone)
                .with("email", Operation.LIKE, email)
                .with("status", Operation.ENUM, status).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("merchantName")));
        return ResponseEntity.ok(merchantService.listMerchants(filter, pageRequest));
    }

    @PreAuthorize("hasRole('MANAGE_API_KEYS')")
    @PostMapping("/{id}/keys")
    public ResponseEntity<AuthenticationDetail> createAuthKeys(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal ProfileDetails profileDetails) {
        Merchant merchant = merchantService.findById(id);
        User user = profileDetails.toUser();
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                if (merchant.getAuthenticationDetail() == null) {
                    try {
                        AuthenticationDetail authenticationDetail = authenticationDetailService.create();
                        merchantService.addAuthenticationDetail(merchant, authenticationDetail);
                        authenticationDetailService.decrypt(authenticationDetail);
                        return ResponseEntity.ok(authenticationDetail);
                    } catch (NoSuchAlgorithmException | DataEncryptionException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_MERCHANT_USERS')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Merchant>> getUserMerchants(@PathVariable("userId") Long id) {
        List<MerchantUser> merchantUsers = merchantService.findUserMerchants(id);
        List<Merchant> merchants = merchantUsers.stream().map(merchantUser -> merchantUser.getMerchant()).
                collect(Collectors.toList());
        return ResponseEntity.ok(merchants);
    }

    @PreAuthorize("hasRole('MANAGE_API_KEYS')")
    @PostMapping("/{id}/keys/generate")
    public ResponseEntity<AuthenticationDetail> regenerateKey(@PathVariable("id") Long id,
                  @AuthenticationPrincipal ProfileDetails profileDetails) {
        Merchant merchant = merchantService.findById(id);
        User user = profileDetails.toUser();
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                if (merchant.getAuthenticationDetail() != null) {
                    try {
                        AuthenticationDetail authenticationDetail =
                                authenticationDetailService.regenerateKey(merchant.getAuthenticationDetail());
                        return ResponseEntity.ok(authenticationDetailService.decrypt(authenticationDetail));
                    } catch (NoSuchAlgorithmException | DataEncryptionException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                } else {
                    return ResponseEntity.unprocessableEntity().build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }
}
