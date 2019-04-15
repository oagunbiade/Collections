package com.coronation.collections.services;

import com.coronation.collections.domain.AuthenticationDetail;
import com.coronation.collections.exception.DataEncryptionException;

import java.security.NoSuchAlgorithmException;

/**
 * Created by Toyin on 4/8/19.
 */
public interface AuthenticationDetailService {
    AuthenticationDetail create() throws NoSuchAlgorithmException, DataEncryptionException;
    AuthenticationDetail regenerateKey(AuthenticationDetail authenticationDetail) throws NoSuchAlgorithmException, DataEncryptionException;
    AuthenticationDetail decrypt(AuthenticationDetail authenticationDetail) throws DataEncryptionException;
    AuthenticationDetail edit(AuthenticationDetail prev, AuthenticationDetail current);
    AuthenticationDetail disable(AuthenticationDetail authenticationDetail);
}
