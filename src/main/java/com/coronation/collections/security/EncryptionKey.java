package com.coronation.collections.security;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EncryptionKey implements SecretKey {
    private static final long serialVersionUID = 1861556096647807538L;
    private  byte[] seed;

    public EncryptionKey(String seedString, boolean base64) throws UnsupportedEncodingException {
        if (base64) {
            seed = Base64.getDecoder().decode(seedString);
        } else {
            seed = seedString.getBytes("UTF-8");
        }
    }

    public EncryptionKey(byte[] seed) {
        this.seed = seed;
    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public String getFormat() {
        return "RAW";
    }

    @Override
    public byte[] getEncoded() {
        byte[] fin = new byte[seed.length];
        for (int r = 0; r < seed.length; r++) {
            fin[r] = seed[r];
        }
        return fin;
    }
}
