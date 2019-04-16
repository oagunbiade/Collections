package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.PaymentStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.PageUtil;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private ProductService productService;
    private MerchantService merchantService;

    @Autowired
    public ProductController(ProductService productService, MerchantService merchantService) {
        this.productService = productService;
        this.merchantService = merchantService;
    }

    @PreAuthorize("hasRole('CREATE_PRODUCT')")
    @PostMapping("/merchants/{merchantId}/accounts/{merchantAccountId}")
    public ResponseEntity<Product> create(@PathVariable("merchantId") Long merchantId,
             @PathVariable("merchantAccountId") Long merchantAccountId, @RequestBody @Valid Product product,
          BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            Merchant merchant = merchantService.findById(merchantId);
            MerchantAccount account = merchantService.findByAccountId(merchantAccountId);
            if (merchant == null || account == null) {
                return ResponseEntity.notFound().build();
            } else if (account.getDeleted() || account.getAccount().getDeleted()) {
                return ResponseEntity.unprocessableEntity().build();
            } else {
                if (GenericUtil.isMerchantUser(user.getRole())) {
                    if (!isMerchantUser(merchant, user)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
                if (merchant.equals(account.getMerchant())) {
                    try {
                        return ResponseEntity.ok(productService.save(product, merchant, account));
                    } catch (DataIntegrityViolationException dve) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).build();
                    }
                } else {
                    return ResponseEntity.unprocessableEntity().build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_PRODUCT')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> edit(@PathVariable("id") Long id, @RequestBody @Valid Product product,
                    BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Product previous = productService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            } else {
                User user = profileDetails.toUser();
                if (GenericUtil.isMerchantUser(user.getRole())) {
                    if (!isMerchantUser(previous.getMerchant(), user)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
                try {
                    return ResponseEntity.ok(productService.update(previous, product));
                } catch (DataIntegrityViolationException dve) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('EDIT_PRODUCT_ACCOUNT')")
    @PutMapping("/{id}/accounts/{accountId}")
    public ResponseEntity<Product> updateProductAccount(@PathVariable("id") Long id,
        @PathVariable("accountId") Long accountId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Product product = productService.findById(id);
        MerchantAccount account = merchantService.findByAccountId(accountId);
        if (product == null || account == null) {
            return ResponseEntity.notFound().build();
        } else if (account.getDeleted() || account.getAccount().getDeleted()) {
            return ResponseEntity.unprocessableEntity().build();

        } else {
            if (GenericUtil.isMerchantUser(user.getRole())) {
                if (!isMerchantUser(product.getMerchant(), user)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            if (product.getMerchant().equals(account.getMerchant())) {
                return ResponseEntity.ok(productService.updateAccount(product, account));
            } else {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
    }

    @PreAuthorize("hasRole('APPROVE_PRODUCT')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Product> approve(@PathVariable("id") Long id,
           @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult,
                                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Product product = productService.findById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            } else {
                User user = profileDetails.toUser();
                if (GenericUtil.isMerchantUser(user.getRole())) {
                    if (!isMerchantUser(product.getMerchant(), user)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
                return ResponseEntity.ok(productService.approveProduct(product, approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('APPROVE_PRODUCT_ACCOUNT')")
    @PostMapping("/{id}/accounts/approve")
    public ResponseEntity<Product> approveAccount(@PathVariable("id") Long id,
          @AuthenticationPrincipal ProfileDetails profileDetails,
          @RequestBody @Valid ApprovalDto approvalDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Product product = productService.findById(id);
            if (product == null) {
                return ResponseEntity.notFound().build();
            } else {
                User user = profileDetails.toUser();
                if (GenericUtil.isMerchantUser(user.getRole())) {
                    if (!isMerchantUser(product.getMerchant(), user)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
                return ResponseEntity.ok(productService.approveAccount(product, approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('REVERT_PRODUCT')")
    @PostMapping("/{id}/revert")
    public ResponseEntity<Product> revert(@PathVariable("id") Long id,
                                          @AuthenticationPrincipal ProfileDetails profileDetails) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole())) {
                if (!isMerchantUser(product.getMerchant(), user)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return ResponseEntity.ok(productService.revert(product));
        }
    }

    @PreAuthorize("hasRole('DELETE_PRODUCT')")
    @PostMapping("/{id}/delete")
    public ResponseEntity<Product> delete(@PathVariable("id") Long id,
                      @AuthenticationPrincipal ProfileDetails profileDetails) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole())) {
                if (!isMerchantUser(product.getMerchant(), user)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return ResponseEntity.ok(productService.delete(product));
        }
    }

    @PreAuthorize("hasRole('ACTIVATE_PRODUCT')")
    @PostMapping("/{id}/status")
    public ResponseEntity<Product> activateOrDeactivate(@PathVariable("id") Long id,
                                @AuthenticationPrincipal ProfileDetails profileDetails) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        } else {
            User user = profileDetails.toUser();
            if (GenericUtil.isMerchantUser(user.getRole())) {
                if (!isMerchantUser(product.getMerchant(), user)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            return ResponseEntity.ok(productService.deactivateOrActivate(product));
        }
    }

    @PreAuthorize("hasRole('VIEW_PRODUCTS')")
    @GetMapping("/merchants/{id}")
    public ResponseEntity<List<Product>> getMerchantProducts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productService.findByMerchantId(id));
    }

    @PreAuthorize("hasRole('VIEW_PRODUCTS')")
    @GetMapping("/merchants/{merchantId}/distributors/{distributorId}")
    public ResponseEntity<List<Product>> getDistributorMerchantProducts(@PathVariable("merchantId") Long merchantId,
                    @PathVariable("distributorId") Long distributorId) {
        return ResponseEntity.ok(productService.merchantDistributorProducts(merchantId, distributorId));
    }

    @PreAuthorize("hasRole('VIEW_PRODUCTS')")
    @GetMapping
    public ResponseEntity<Page<Product>> listProducts(@RequestParam(value="page", required = false, defaultValue = "0") int page,
          @RequestParam(value="pageSize", defaultValue = "10") int pageSize, @RequestParam(value="name", required = false) String name,
          @RequestParam(value="code", required = false) String code, @RequestParam(value="amount", required = false) BigDecimal amount,
          @RequestParam(value="merchantName", required = false) String merchantName,
          @RequestParam(value="status", required = false) PaymentStatus status,
          @RequestParam(value="from", required=false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
          @RequestParam(value="to", required=false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime to) {

        BooleanExpression filter = new CustomPredicateBuilder<>("product", Product.class)
                .with("name", Operation.LIKE, name)
                .with("code", Operation.LIKE, code)
                .with("merchant.name", Operation.LIKE, merchantName)
                .with("status", Operation.ENUM, status)
                .with("deleted", Operation.BOOLEAN, false)
                .with("amount", Operation.EQUALS, amount)
                .with("createdAt", Operation.BETWEEN, GenericUtil.getDateRange(from, to)).build();
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.asc("name")));
        return ResponseEntity.ok(productService.listAll(filter, pageRequest));
    }

    private boolean isMerchantUser(Merchant merchant, User user) {
        MerchantUser merchantUser = merchantService.findByOrganizationUserId(user.getId());
        return merchantUser != null && merchantUser.getMerchant().equals(merchant);
    }
}
