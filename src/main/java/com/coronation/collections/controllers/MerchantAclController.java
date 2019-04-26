package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.dto.AccessObject;
import com.coronation.collections.dto.PermissionDto;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.services.UserService;
import com.coronation.collections.services.impl.LocalPermissionService;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Toyin on 4/23/19.
 */
@RestController
@RequestMapping("/api/v1/acl/merchants/{id}")
public class MerchantAclController {
    private MerchantService merchantService;
    private UserService userService;
    private DistributorService distributorService;
    private ProductService productService;
    private LocalPermissionService localPermissionService;

    @Autowired
    public MerchantAclController(MerchantService merchantService, DistributorService distributorService,
                                 ProductService productService,
                                 UserService userService,
                                 LocalPermissionService localPermissionService) {
        this.merchantService = merchantService;
        this.distributorService = distributorService;
        this.productService = productService;
        this.localPermissionService = localPermissionService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @GetMapping("/products/{productId}")
    public ResponseEntity<List<AccessObject>> getProductAccessList(@PathVariable("id") Long id,
        @PathVariable("productId") Long productId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(id);
        Product product = productService.findById(productId);
        if (merchant == null || product == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, user, merchantService) ||
                !merchant.equals(product.getMerchant())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            List<MerchantUser> merchantUsers = merchantService.findMerchantUsers(id);
            MutableAcl acl = localPermissionService.getObjectAcl(product);
            List<AccessObject> accessObjects = GenericUtil.getAccessors(merchantUsers, acl.getEntries());
            return ResponseEntity.ok(accessObjects);
        }
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @PostMapping("/products/{productId}/users/{userId}")
    public ResponseEntity<?> updateProductAccessList(
            @RequestBody @Valid PermissionDto permissionDto,
            @PathVariable("id") Long id,
          @PathVariable("productId") Long productId, @PathVariable("userId") Long userId,
                              @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(userId);
        Merchant merchant = merchantService.findById(id);
        Product product = productService.findById(productId);
        if (merchant == null || product == null || user == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, admin, merchantService) ||
                !GenericUtil.isMerchantUser(merchant, user, merchantService) ||
                !merchant.equals(product.getMerchant())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return updateAcl(product, permissionDto, user);
        }
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<AccessObject>> getAccountAccessList(@PathVariable("id") Long id,
                               @PathVariable("accountId") Long accountId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(id);
        MerchantAccount account = merchantService.findByMerchantAccountId(accountId);
        if (merchant == null || account == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, user, merchantService) ||
                !merchant.equals(account.getMerchant())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            List<MerchantUser> merchantUsers = merchantService.findMerchantUsers(id);
            MutableAcl acl = localPermissionService.getObjectAcl(account);
            List<AccessObject> accessObjects = GenericUtil.getAccessors(merchantUsers, acl.getEntries());
            return ResponseEntity.ok(accessObjects);
        }
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @PostMapping("/accounts/{accountId}/users/{userId}")
    public ResponseEntity<?> updateAccountAccessList(
            @RequestBody @Valid PermissionDto permissionDto,
            @PathVariable("id") Long id,
            @PathVariable("accountId") Long accountId, @PathVariable("userId") Long userId,
            @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(userId);
        Merchant merchant = merchantService.findById(id);
        MerchantAccount account = merchantService.findByMerchantAccountId(accountId);
        if (merchant == null || account == null || user == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, admin, merchantService) ||
                !GenericUtil.isMerchantUser(merchant, user, merchantService) ||
                !merchant.equals(account.getMerchant())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return updateAcl(account, permissionDto, user);
        }
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @GetMapping("/distributors/{distributorId}")
    public ResponseEntity<List<AccessObject>> getDistributorAccessList(@PathVariable("id") Long id,
               @PathVariable("distributorId") Long distributorId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(id);
        MerchantDistributor merchantDistributor = distributorService.
                findByMerchantIdAndDistributorId(id, distributorId);
        if (merchant == null || merchantDistributor == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            List<MerchantUser> merchantUsers = merchantService.findMerchantUsers(id);
            MutableAcl acl = localPermissionService.getObjectAcl(merchantDistributor);
            List<AccessObject> accessObjects = GenericUtil.getAccessors(merchantUsers, acl.getEntries());
            return ResponseEntity.ok(accessObjects);
        }
    }

    @PreAuthorize("hasRole('MANAGE_ACCESS')")
    @PostMapping("/distributors/{distributorId}/users/{userId}")
    public ResponseEntity<?> updateDistributorAccessList(
            @RequestBody @Valid PermissionDto permissionDto,
            @PathVariable("id") Long id,
            @PathVariable("distributorId") Long distributorId, @PathVariable("userId") Long userId,
            @AuthenticationPrincipal ProfileDetails profileDetails) {
        User admin = profileDetails.toUser();
        User user = userService.findById(userId);
        Merchant merchant = merchantService.findById(id);
        MerchantDistributor merchantDistributor = distributorService.
                findByMerchantIdAndDistributorId(id, distributorId);
        if (merchant == null || merchantDistributor == null || user == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(merchant, admin, merchantService) ||
                !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return updateAcl(merchantDistributor, permissionDto, user);
        }
    }

    private ResponseEntity<?> updateAcl(IEntity entity, PermissionDto permissionDto, User user) {
        localPermissionService.addPermissionForUser(entity, BasePermission.WRITE,
                user.getEmail(), permissionDto.getWrite());
        localPermissionService.addPermissionForUser(entity, BasePermission.READ,
                user.getEmail(), permissionDto.getRead());
        return ResponseEntity.ok().build();
    }
}
