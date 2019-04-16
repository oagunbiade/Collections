package com.coronation.collections.services.impl;

import com.coronation.collections.domain.AuthenticationDetail;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.exception.DataEncryptionException;
import com.coronation.collections.repositories.AuthenticationDetailRepository;
import com.coronation.collections.security.AESEncryptionUtil;
import com.coronation.collections.services.AuthenticationDetailService;
import com.coronation.collections.util.GenericUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 4/9/19.
 */
@Service
public class AuthenticationDetailServiceImpl implements AuthenticationDetailService {
    private AuthenticationDetailRepository detailRepository;
    private AESEncryptionUtil aesEncryptionUtil;

    @Autowired
    public AuthenticationDetailServiceImpl(AuthenticationDetailRepository detailRepository,
                                           AESEncryptionUtil aesEncryptionUtil) {
        this.detailRepository = detailRepository;
        this.aesEncryptionUtil = aesEncryptionUtil;
    }

    @Override
    public AuthenticationDetail create() throws NoSuchAlgorithmException, DataEncryptionException {
        AuthenticationDetail authenticationDetail = new AuthenticationDetail();
        authenticationDetail.setStatus(GenericStatus.ACTIVE);
        authenticationDetail.setApiKey(aesEncryptionUtil.encryptData(GenericUtil.generateKey(256),
                aesEncryptionUtil.getEncryptionKey()));
        authenticationDetail.setAppId(GenericUtil.generateKey(128));
        return detailRepository.saveAndFlush(authenticationDetail);
    }

    @Override
    public AuthenticationDetail regenerateKey(AuthenticationDetail authenticationDetail)
            throws NoSuchAlgorithmException, DataEncryptionException {
        authenticationDetail.setApiKey(aesEncryptionUtil.encryptData(GenericUtil.generateKey(256),
                aesEncryptionUtil.getEncryptionKey()));
        return detailRepository.saveAndFlush(authenticationDetail);
    }

    @Override
    public AuthenticationDetail decrypt(AuthenticationDetail authenticationDetail) throws DataEncryptionException {
        authenticationDetail.setApiKey(aesEncryptionUtil.decryptData(authenticationDetail.getApiKey(),
                aesEncryptionUtil.getEncryptionKey()));
        return authenticationDetail;
    }

    @Override
    public AuthenticationDetail edit(AuthenticationDetail prev, AuthenticationDetail current) {
        prev.setApiKey(current.getApiKey());
        prev.setAppId(current.getAppId());
        prev.setModifiedAt(LocalDateTime.now());
        return detailRepository.saveAndFlush(prev);
    }

    @Override
    public AuthenticationDetail disable(AuthenticationDetail authenticationDetail) {
        authenticationDetail.setStatus(GenericStatus.DEACTIVATED);
        authenticationDetail.setModifiedAt(LocalDateTime.now());
        return detailRepository.saveAndFlush(authenticationDetail);
    }
}
