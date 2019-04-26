package com.coronation.collections.services.impl;

import com.coronation.collections.domain.MerchantAccount;
import com.coronation.collections.domain.MerchantDistributor;
import com.coronation.collections.domain.Product;
import com.coronation.collections.domain.enums.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Toyin on 4/23/19.
 */
@Service
@Transactional
public class DomainSecurityService {
    @Autowired
    private LocalPermissionService localPermissionService;

    public void addMerchantAccountPermissions(MerchantAccount merchantAccount) {
        localPermissionService.addPermissionForAuthority(merchantAccount, BasePermission.READ,
                TaskType.VIEW_ACCOUNTS.name(), true);
        localPermissionService.addPermissionForAuthority(merchantAccount, BasePermission.WRITE,
                TaskType.DELETE_ACCOUNT.name(), true);
        localPermissionService.addPermissionForAuthority(merchantAccount, BasePermission.WRITE,
                TaskType.EDIT_ACCOUNT.name(), true);
        localPermissionService.addPermissionForAuthority(merchantAccount, BasePermission.WRITE,
                TaskType.APPROVE_ACCOUNT.name(), true);
    }

    public void addDistributorPermissions(MerchantDistributor distributor) {
        localPermissionService.addPermissionForAuthority(distributor, BasePermission.READ,
                TaskType.VIEW_DISTRIBUTORS.name(), true);
        localPermissionService.addPermissionForAuthority(distributor, BasePermission.WRITE,
                TaskType.DELETE_DISTRIBUTOR.name(), true);
        localPermissionService.addPermissionForAuthority(distributor, BasePermission.WRITE,
                TaskType.EDIT_DISTRIBUTOR.name(), true);
        localPermissionService.addPermissionForAuthority(distributor, BasePermission.WRITE,
                TaskType.APPROVE_ACCOUNT.name(), true);
    }

    public void addProductPermissions(Product product) {
        localPermissionService.addPermissionForAuthority(product, BasePermission.READ,
                TaskType.VIEW_PRODUCTS.name(), true);
        localPermissionService.addPermissionForAuthority(product, BasePermission.WRITE,
                TaskType.DELETE_PRODUCT.name(), true);
        localPermissionService.addPermissionForAuthority(product, BasePermission.WRITE,
                TaskType.EDIT_PRODUCT.name(), true);
        localPermissionService.addPermissionForAuthority(product, BasePermission.WRITE,
                TaskType.APPROVE_PRODUCT.name(), true);
    }
}
