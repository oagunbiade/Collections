package com.coronation.collections.contollers.api;

import com.coronation.collections.domain.Payment;
import com.coronation.collections.domain.Product;
import com.coronation.collections.domain.User;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.PaymentService;
import com.coronation.collections.services.ProductService;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by Toyin on 4/11/19.
 */
@RestController
@RequestMapping("/api/vi/payments")
public class PaymentController {
    private PaymentService paymentService;
    private ProductService productService;
    private DistributorService distributorService;
    private MerchantService merchantService;

    @Autowired
    public PaymentController(PaymentService paymentService, ProductService productService,
                 MerchantService merchantService, DistributorService distributorService) {
        this.paymentService = paymentService;
        this.productService = productService;
        this.distributorService = distributorService;
        this.merchantService = merchantService;
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<Payment> create(@PathVariable("productId") Long productId,
                  @RequestBody @Valid Payment payment, BindingResult bindingResult,
                  @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        } else {
            Product product = productService.findById(productId);
            if (product == null) {
                return ResponseEntity.notFound().build();
            } else {
                if (GenericUtil.isDistributorUser(user.getRole())) {
                    if (!GenericUtil.isDistributorMerchant(product.getMerchant(), user, distributorService)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                } else if (!GenericUtil.isMerchantProduct(product, user, merchantService)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
        }
    }
}
