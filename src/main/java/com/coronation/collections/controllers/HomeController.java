package com.coronation.collections.controllers;

import com.coronation.collections.domain.Merchant;
import com.coronation.collections.domain.Organization;
import com.coronation.collections.domain.User;
import com.coronation.collections.dto.HomeData;
import com.coronation.collections.security.ProfileDetails;
import com.coronation.collections.services.*;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Toyin on 4/24/19.
 */
@RestController
@RequestMapping("/api/v1/home")
public class HomeController {
    private UserService userService;
    private OrganizationService organizationService;
    private MerchantService merchantService;
    private ProductService productService;
    private  DistributorService distributorService;

    @Autowired
    public HomeController(UserService userService, OrganizationService organizationService,
          MerchantService merchantService, ProductService productService,
                          DistributorService distributorService) {
        this.userService = userService;
        this.organizationService = organizationService;
        this.merchantService = merchantService;
        this.productService = productService;
        this.distributorService = distributorService;
    }

    @GetMapping
    public ResponseEntity<HomeData> getAll(@AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        if (GenericUtil.isStaffRole(user.getRole())) {
            HomeData homeData = new HomeData();
            homeData.setDistributor(distributorService.countAll());
            homeData.setMerchant(merchantService.countAll());
            homeData.setOrganization(organizationService.countAll());
            homeData.setProduct(productService.countAll());
            homeData.setUser(userService.countAll());
            return ResponseEntity.ok(homeData);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/merchants/{merchantId}")
    public ResponseEntity<HomeData> getForMerchant(@PathVariable("merchantId") Long merchantId,
                                   @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Merchant merchant = merchantService.findById(merchantId);
        if (merchant == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (user.getRole().getName().equalsIgnoreCase("Admin") ||
                    GenericUtil.isMerchantUser(merchant, user, merchantService)) {
                HomeData homeData = new HomeData();
                homeData.setDistributor(distributorService.countByMerchant(merchantId));
                homeData.setProduct(productService.countByMerchantId(merchantId));
                homeData.setUser(merchantService.countMerchantUsers(merchantId));
                return ResponseEntity.ok(homeData);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<HomeData> getForOrganization(@PathVariable("organizationId") Long organizationId,
            @AuthenticationPrincipal ProfileDetails profileDetails) {
        User user = profileDetails.toUser();
        Organization organization = organizationService.findById(organizationId);
        if (organization == null) {
            return  ResponseEntity.notFound().build();
        } else {

        }
        if (user.getRole().getName().equalsIgnoreCase("Admin") ||
                GenericUtil.isOrganizationUser(organization, user, organizationService)) {
            HomeData homeData = new HomeData();
            homeData.setDistributor(distributorService.countByOrganization(organizationId));
            homeData.setProduct(productService.countByOrganizationId(organizationId));
            homeData.setMerchant(merchantService.countByOrganization(organizationId));
            homeData.setUser(organizationService.countOrganizationUsers(organizationId));
            return ResponseEntity.ok(homeData);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
