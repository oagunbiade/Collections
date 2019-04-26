package com.coronation.collections.controllers;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.PaymentStatus;
import com.coronation.collections.dto.ApprovalDto;
import com.coronation.collections.dto.PaymentReport;
import com.coronation.collections.exception.InvalidDataException;
import com.coronation.collections.repositories.predicate.CustomPredicateBuilder;
import com.coronation.collections.repositories.predicate.Operation;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.*;
import com.coronation.collections.util.GenericUtil;
import com.coronation.collections.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private PaymentService paymentService;
    private ProductService productService;
    private DistributorService distributorService;
    private MerchantService merchantService;
    private OrganizationService organizationService;

    @Autowired
    public PaymentController(PaymentService paymentService, ProductService productService,
                 MerchantService merchantService, DistributorService distributorService,
                             OrganizationService organizationService) {
        this.paymentService = paymentService;
        this.productService = productService;
        this.distributorService = distributorService;
        this.merchantService = merchantService;
        this.organizationService = organizationService;
    }

    @PreAuthorize("hasRole('INITIATE_PAYMENT')")
    @PostMapping("/products/{productId}/distributors/{distributorId}")
    public ResponseEntity<Payment> create(@PathVariable("productId") Long productId,
                  @PathVariable("distributorId") Long distributorId,
                  @RequestBody @Valid Payment payment, BindingResult bindingResult,
                  @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Product product = productService.findById(productId);
            Distributor distributor = distributorService.findById(distributorId);
            if (product == null || distributor == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (GenericUtil.isDistributorUser(user.getRole())) {
                    if (!GenericUtil.isDistributorMerchant(product.getMerchant(), user, distributorService)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                        !GenericUtil.isMerchantUser(product.getMerchant(), user, merchantService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                if (GenericUtil.isProductValid(product) && GenericUtil.isMerchantValid(product.getMerchant())
                        && GenericUtil.isDistributorValid(distributor)) {
                    MerchantDistributor merchantDistributor = distributorService.
                            findByMerchantIdAndDistributorId(product.getMerchant().getId(), distributorId);
                    if (merchantDistributor == null || !GenericUtil.isMerchantDistributorValid(merchantDistributor)) {
                        return ResponseEntity.unprocessableEntity().build();
                    }
                    List<DistributorAccount> distributorAccounts = distributorService.distributorAccounts(distributorId);
                    if (distributorAccounts.isEmpty()) {
                        throw new ResponseStatusException(
                                HttpStatus.UNPROCESSABLE_ENTITY, "No distributor account found");
                    }
                    DistributorAccount distributorAccount =
                            distributorService.getDefaultAccount(distributorAccounts).
                                    orElse((DistributorAccount) distributorAccounts.toArray()[0]);
                    return ResponseEntity.ok(paymentService.save(payment, product.getMerchant(), product,
                            distributorAccount, distributor, user));
                } else {
                    return ResponseEntity.unprocessableEntity().build();
                }
            }
        }
    }

    @PreAuthorize("hasRole('APPROVE_PAYMENT')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<Payment> approvePayment(@PathVariable("id") Long id, @RequestBody @Valid ApprovalDto approvalDto,
            BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Payment payment = paymentService.findById(id);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(payment.getMerchant(), user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(paymentService.approve(payment, approvalDto));
        }
    }

    @PreAuthorize("hasRole('EDIT_PAYMENT')")
    @PutMapping("/{id}")
    public ResponseEntity<Payment> editPayment(@PathVariable("id") Long id, @RequestBody @Valid Payment payment,
              BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Payment previous = paymentService.findById(id);
            if (previous == null) {
                return ResponseEntity.notFound().build();
            }
            if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(previous.getMerchant(), user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            int diff = Math.abs(LocalDateTime.now().getHour() - previous.getDueDate().getHour());
            if (diff <= 1) {
                return ResponseEntity.unprocessableEntity().build();
            } else {
                return ResponseEntity.ok(paymentService.update(previous, payment));
            }
        }
    }

    @PreAuthorize("hasRole('CANCEL_PAYMENT')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Payment> revert(@PathVariable("id") Long id,
                                          @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Payment payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(payment.getMerchant(), user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            int diff = Math.abs(LocalDateTime.now().getHour() - payment.getDueDate().getHour());
            if (diff <= 1) {
                return ResponseEntity.unprocessableEntity().build();
            } else {
                return ResponseEntity.ok(paymentService.cancelPayment(payment));
            }
        }
    }

    @PreAuthorize("hasRole('REVERT_PAYMENT')")
    @PostMapping("/{id}/revert")
    public ResponseEntity<Payment> cancel(@PathVariable("id") Long id, @RequestBody @Valid ApprovalDto approvalDto,
                                          BindingResult bindingResult, @AuthenticationPrincipal ProfileDetails profileDetails) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            User user = profileDetails.toUser();
            Payment payment = paymentService.findById(id);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                    !GenericUtil.isMerchantUser(payment.getMerchant(), user, merchantService)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                return ResponseEntity.ok(paymentService.revert(payment,approvalDto));
            }
        }
    }

    @PreAuthorize("hasRole('CONFIRM_PAYMENT')")
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Payment> confirm(@PathVariable("id") Long id,
                                          @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Payment payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(payment.getMerchant(), user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.ok(paymentService.confirmPayment(payment));
        }
    }

    @PreAuthorize("hasRole('MAKE_PAYMENT')")
    @PostMapping("/{id}/pay")
    public ResponseEntity<Payment> pay(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Payment payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isDistributorMerchant(payment.getMerchant(), user, distributorService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            if (payment.getStatus().equals(PaymentStatus.APPROVED)) {
                return ResponseEntity.ok(paymentService.processPayment(payment));
            } else {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
    }

    @PreAuthorize("hasRole('MARK_PAYMENT_PAYABLE')")
    @PostMapping("/{id}/payable")
    public ResponseEntity<Payment> markPayable(@PathVariable("id") Long id) {
        Payment payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (payment.getStatus().equals(PaymentStatus.FAILED)) {
                return ResponseEntity.ok(paymentService.clearAsPayable(payment));
            } else {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_PAYMENTS')")
    @GetMapping("/code/{code}")
    public ResponseEntity<Payment> getByCode(@PathVariable("code") String code) {
        Payment payment = paymentService.getByReferenceCode(code);
        if (code == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(payment);
        }
    }

    @PreAuthorize("hasRole('DELETE_PAYMENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Payment> delete(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Payment payment = paymentService.findById(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        } else if (!GenericUtil.isMerchantUser(payment.getMerchant(), user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            int diff = Math.abs(LocalDateTime.now().getHour() - payment.getDueDate().getHour());
            if (diff <= 1) {
                return ResponseEntity.unprocessableEntity().build();
            } else {
                return ResponseEntity.ok(paymentService.delete(payment));
            }
        }
    }

    @PreAuthorize("hasRole('SET_DISTRIBUTOR_ACCOUNT')")
    @PostMapping("/{id}/distributors/accounts/{accountId}")
    public ResponseEntity<Payment> setDistributorAccount(@PathVariable("id") Long id,
         @PathVariable("accountId") Long accountId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Payment payment = paymentService.findById(id);
        DistributorAccount distributorAccount = distributorService.findByAccountId(accountId);
        if (payment == null || distributorAccount == null) {
            return ResponseEntity.notFound().build();
        } else {
            DistributorUser distributorUser = distributorService.findByUserId(user.getId());
            if (distributorUser == null || !payment.getDistributor().equals(distributorUser.getDistributor()) ||
                    !distributorAccount.getDistributor().equals(payment.getDistributor())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(paymentService.setDistributorAccount(payment, distributorAccount));
        }
    }

    @PreAuthorize("hasRole('VALIDATE_PAYMENT')")
    @PostMapping("/invalid/{id}")
    public ResponseEntity<Payment> validate(@PathVariable("id") Long id, InvalidPayment invalidPayment,
                                            @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        InvalidPayment previous = paymentService.findInvalidPaymentById(id);
        if (previous == null) {
            return ResponseEntity.notFound().build();
        } else if (previous.getValidated()) {
            return ResponseEntity.unprocessableEntity().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(previous.getMerchant(), user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            try {
                return ResponseEntity.ok(paymentService.validatePayment(invalidPayment, user));
            } catch (InvalidDataException e) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, e.getMessage(), e);
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_PAYMENTS')")
    @GetMapping("/merchants/{merchantId}/invalid")
    public ResponseEntity<Page<InvalidPayment>> invalidPayments(@PathVariable("merchantId") Long merchantId,
          @RequestParam(value="page", required = false, defaultValue = "0") int page, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            return ResponseEntity.ok(paymentService.merchantInvalidPayments(merchantId,
                    PageUtil.createPageRequest(page, Sort.by(Sort.Order.desc("dueDate")))));
        }
    }

    @PreAuthorize("hasRole('UPLOAD_PAYMENTS')")
    @PostMapping("/merchants/{merchantId}")
    public ResponseEntity<List<InvalidPayment>> uploadPayments(@PathVariable("merchantId") Long merchantId,
            @AuthenticationPrincipal ProfileDetails profileDetails, @RequestParam("file") MultipartFile file) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }  else {
            try {
                return ResponseEntity.ok(paymentService.uploadPayments(file.getInputStream(), merchant, user));
            } catch (IOException e) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing file", e);
            }
        }
    }

    @PreAuthorize("hasRole('VIEW_REPORTS')")
    @GetMapping("/reports")
    public ResponseEntity<PaymentReport> getAllReport(
                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (!GenericUtil.isStaffRole(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            PaymentReport paymentReport = new PaymentReport();
            paymentService.setAllAmountReport(paymentReport);
            return ResponseEntity.ok(paymentReport);
        }
    }

    @PreAuthorize("hasRole('VIEW_REPORTS')")
    @GetMapping("/organizations/{organizationId}/reports")
    public ResponseEntity<PaymentReport> getOrganizationReport(@PathVariable("organizationId") Long organizationId,
                       @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isOrganizationUser(organization, user, organizationService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            PaymentReport paymentReport = new PaymentReport();
            paymentService.setOrganizationAmountReport(organizationId, paymentReport);
            return ResponseEntity.ok(paymentReport);
        }
    }

    @PreAuthorize("hasRole('VIEW_REPORTS')")
    @GetMapping("/merchants/{merchantId}/reports")
    public ResponseEntity<PaymentReport> getMerchantReport(@PathVariable("merchantId") Long merchantId,
                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            PaymentReport paymentReport = new PaymentReport();
            paymentService.setMerchantAmountReport(merchantId, paymentReport);
            return ResponseEntity.ok(paymentReport);
        }
    }

    @PreAuthorize("hasRole('VIEW_REPORTS')")
    @GetMapping("/distributors/{distributorId}/reports")
    public ResponseEntity<PaymentReport> getDistributorReport(@PathVariable("distributorId") Long distributorId,
                                                           @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Distributor distributor = distributorService.findById(distributorId);
        if (distributor == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isDistributorUser(user.getRole()) &&
                !GenericUtil.isDistributorUser(distributor, user, distributorService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            PaymentReport paymentReport = new PaymentReport();
            paymentService.setDistributorAmountReport(distributorId, paymentReport);
            return ResponseEntity.ok(paymentReport);
        }
    }

    @PreAuthorize("hasRole('VIEW_REPORTS')")
    @GetMapping("/merchants/{merchantId}/distributors/{distributorId}/reports")
    public ResponseEntity<PaymentReport> getMerchantDistributorReport(@PathVariable("merchantId") Long merchantId,
            @PathVariable("distributorId") Long distributorId, @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        Distributor distributor = distributorService.findById(distributorId);
        if (merchant == null || distributor == null) {
            return ResponseEntity.notFound().build();
        } else if (GenericUtil.isMerchantUser(user.getRole()) &&
                !GenericUtil.isMerchantUser(merchant, user, merchantService)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else {
            List<Payment> payments = paymentService.findMerchantDistributorPayments(merchantId, distributorId);
            PaymentReport paymentReport = new PaymentReport();
            paymentService.sumPayments(payments, paymentReport);
            return ResponseEntity.ok(paymentReport);
        }
    }

    @PreAuthorize("hasRole('VIEW_PAYMENTS')")
    @GetMapping
    public ResponseEntity<Page<Payment>> listPayments(@AuthenticationPrincipal ProfileDetails profileDetails,
          @RequestParam(value="page", required = false, defaultValue = "0") int page,
          @RequestParam(value="pageSize", defaultValue = "10") int pageSize, @RequestParam(value="referenceCode", required = false) String referenceCode,
          @RequestParam(value="amount", required = false) BigDecimal amount, @RequestParam(value="status", required = false) PaymentStatus status,
          @RequestParam(value="dueDate", required = false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dueDate,
          @RequestParam(value="from", required=false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime from,
          @RequestParam(value="to", required=false) @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime to,
          @RequestParam(value="productName", required = false) String productName,
          @RequestParam(value="productCode", required = false) String productCode,
          @RequestParam(value="distributorName", required = false) String distributorName,
          @RequestParam(value="distributorBvn", required = false) String distributorBvn,
          @RequestParam(value="merchantName", required = false) String merchantName,
          @RequestParam(value="merchantCode", required = false) String merchantCode) {
        User user = profileDetails.toUser();
        CustomPredicateBuilder builder = new CustomPredicateBuilder<>("payment", Payment.class)
                .with("referenceCode", Operation.LIKE, referenceCode)
                .with("product.name", Operation.LIKE, productName)
                .with("product.code", Operation.LIKE, productCode)
                .with("amount", Operation.EQUALS, amount)
                .with("status", Operation.ENUM, status)
                .with("createdAt", Operation.BETWEEN, GenericUtil.getDateRange(to, from))
                .with("dueDate", Operation.BETWEEN, GenericUtil.getDateRange(dueDate, dueDate))
                .with("deleted", Operation.BOOLEAN, false);
        if (GenericUtil.isDistributorUser(user.getRole())) {
            DistributorUser distributorUser = distributorService.findByUserId(user.getId());
            if (distributorUser == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            builder.with("distributor.bvn", Operation.STRING_EQUALS,
                    distributorUser.getDistributor().getBvn())
                    .with("merchant.merchantName", Operation.LIKE, merchantName)
                    .with("merchant.merchantCode", Operation.LIKE, merchantCode);
        } else if (GenericUtil.isMerchantUser(user.getRole()) || GenericUtil.isRMUser(user.getRole())) {
            List<MerchantUser> merchantUsers = merchantService.findByOrganizationUserId(user.getId());
            if (merchantUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            builder.with("merchant.merchantCode", Operation.STRING_EQUALS,
                    merchantUsers.get(0).getMerchant().getMerchantCode())
                    .with("distributor.name", Operation.LIKE, distributorName)
                    .with("distributor.bvn", Operation.LIKE, distributorBvn);
        } else {
            builder.with("distributor.name", Operation.LIKE, distributorName)
                    .with("distributor.bvn", Operation.LIKE, distributorBvn)
                    .with("merchant.merchantName", Operation.LIKE, merchantName)
                    .with("merchant.merchantCode", Operation.LIKE, merchantCode);
        }
        Pageable pageRequest =
                PageUtil.createPageRequest(page, pageSize,
                        Sort.by(Sort.Order.desc("dueDate"), Sort.Order.asc("product.name")));
        return ResponseEntity.ok(paymentService.listAll(builder.build(), pageRequest));
    }
}
