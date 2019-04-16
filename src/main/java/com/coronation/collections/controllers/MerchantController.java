package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.exception.DataEncryptionException;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.*;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/merchants")
public class MerchantController {
    private MerchantService merchantService;
    private AccountService accountService;
    private OrganizationService organizationService;
    private AuthenticationDetailService authenticationDetailService;

    @Autowired
    public MerchantController(MerchantService merchantService, AccountService accountService,
              OrganizationService organizationService,
                          AuthenticationDetailService authenticationDetailService) {
        this.merchantService = merchantService;
        this.accountService = accountService;
        this.organizationService = organizationService;
        this.authenticationDetailService = authenticationDetailService;
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Merchant> delete(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.delete(merchant));
        }
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Merchant> activateOrDeactivate(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.deactivateOrActivate(merchant));
        }
    }

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

    @PreAuthorize("hasRole('ADD_ACCOUNT')")
    @PostMapping("/{id}/accounts")
    public ResponseEntity<MerchantAccount> createAccount(@PathVariable("id") Long id,
            @RequestBody @Valid Account account, BindingResult bindingResult,
            @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Merchant merchant = merchantService.findById(id);
            if (merchant == null) {
                return ResponseEntity.notFound().build();
            } else {
                User user = profileDetails.toUser();
                if (GenericUtil.isMerchantUser(user.getRole()) &&
                        !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                } else {
                    if (accountService.findByAccountNumber(account.getAccountNumber()) == null) {
                        account = accountService.create(account);
                        return ResponseEntity.ok(merchantService.addAccount(merchant, account));
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                }
            }
        }
    }

    @PostMapping("/accounts/{id}/approve")
    public ResponseEntity<MerchantAccount> approveAccount(@PathVariable Long id,
              @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult,
                                          @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            MerchantAccount merchantAccount = merchantService.findByAccountId(id);
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

    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<MerchantAccount> deleteAccount(@PathVariable Long id) {
        MerchantAccount merchantAccount = merchantService.findByAccountId(id);
        if (merchantAccount == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.deleteAccount(merchantAccount));
        }
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<MerchantAccount>> getAccounts(@PathVariable Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.merchantAccounts(id));
        }
    }

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
                    !GenericUtil.isMerchantUser(merchant, admin, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                MerchantUser merchantUser = merchantService.findByOrganizationUserId(user.getId());
                if (merchantUser == null) {
                    if (merchant.getOrganization().equals(user.getOrganization())) {
                        return ResponseEntity.ok(merchantService.addUser(user, merchant));
                    } else {
                        return ResponseEntity.unprocessableEntity().build();
                    }
                } else {
                    return  ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<MerchantUser>> getMerchantUsers(@PathVariable("id") Long id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(merchantService.findMerchantUsers(id));
        }
    }

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
