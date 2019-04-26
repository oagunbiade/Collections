package com.coronation.collections.services.impl;

import com.coronation.collections.domain.IEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by Toyin on 4/23/19.
 */
@Service
@Transactional
public class LocalPermissionService {

    @Autowired
    private MutableAclService aclService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void addPermissionForUser(IEntity targetObj, Permission permission, String username, boolean grant) {
        final Sid sid = new PrincipalSid(username);
        addPermissionForSid(targetObj, permission, sid, grant);
    }

    public void addPermissionForAuthority(IEntity targetObj, Permission permission, String authority, boolean grant) {
        if (!authority.startsWith("ROLE_")) {
            authority = "ROLE_" + authority;
        }
        final Sid sid = new GrantedAuthoritySid(authority);
        addPermissionForSid(targetObj, permission, sid, grant);
    }

    private void addPermissionForSid(IEntity targetObj, Permission permission, Sid sid, boolean grant) {
        final TransactionTemplate tt = new TransactionTemplate(transactionManager);

        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());

                MutableAcl acl = null;
                try {
                    acl = (MutableAcl) aclService.readAclById(oi);
                } catch (final NotFoundException nfe) {
                    acl = aclService.createAcl(oi);
                }

                acl.insertAce(acl.getEntries().size(), permission, sid, grant);
                aclService.updateAcl(acl);
            }
        });
    }

    public MutableAcl getObjectAcl(IEntity targetObj) {
        final ObjectIdentity oi = new ObjectIdentityImpl(targetObj.getClass(), targetObj.getId());
        return (MutableAcl) aclService.readAclById(oi);
    }
}
