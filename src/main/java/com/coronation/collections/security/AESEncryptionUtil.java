/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coronation.collections.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.coronation.collections.exception.DataEncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class AESEncryptionUtil {
    @Value("${app.key}")
    private String appKey;

    private static final String cipherTransformation = "AES/ECB/PKCS5Padding";
    private static final String aesEncryptionAlgorithm = "AES";
    private static Encoder encoder = Base64.getEncoder();
    private static Decoder decoder = Base64.getDecoder();

    public synchronized String encryptData(String plainText, SecretKey secretKey) throws DataEncryptionException {
        try {
            if(plainText == null){
                return null;
            }
            return encrypt(plainText, secretKey.getEncoded());
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException |
                    IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException ex) {
            throw new DataEncryptionException(ex);
        }
    }

    public synchronized String decryptData(String cipherText, SecretKey secretKey)
            throws DataEncryptionException {
        try {
            if (cipherText == null) {
                return null;
            }
            return decrypt(cipherText, secretKey.getEncoded());
        } catch (GeneralSecurityException | IOException ex) {
            throw new DataEncryptionException(ex);
        }
    }

    /// <summary>
    /// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
    /// </summary>
    /// <param name="plainText">Plain text to encrypt</param>
    /// <param name="key">Secret key</param>
    /// <returns>Base64 encoded string</returns>
    private String encrypt(String plainText, byte[] key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] plainTextbytes = plainText.getBytes("UTF-8");
        byte[] cipherText = encrypt(plainTextbytes, key);
        return encoder.encodeToString(cipherText);
    }

    /// <summary>
    /// Decrypts a base64 encoded string using the given key (AES 128bit key and a Chain Block Cipher)
    /// </summary>
    /// <param name="encryptedText">Base64 Encoded String</param>
    /// <param name="key">Secret Key</param>
    /// <returns>Decrypted String</returns>
    private String decrypt(String encryptedText, byte[] key) throws GeneralSecurityException, IOException {
        byte[] cipheredBytes = decoder.decode(encryptedText);
        byte[] decryptedText = decrypt(cipheredBytes, key);
        return new String(decryptedText, "UTF-8");
    }

    private byte[] encrypt(byte[] plainText, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        plainText = cipher.doFinal(plainText);
        return plainText;
    }

    private byte[] decrypt(byte[] cipherText, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, aesEncryptionAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        cipherText = cipher.doFinal(cipherText);
        return cipherText;
    }

    public EncryptionKey getEncryptionKey()  {
        byte[] encryptStringByte = appKey.getBytes();
        byte[] fin = new byte[16];
        for (int r = 0; r < encryptStringByte.length && r < 16; r++) {
            fin[r] = encryptStringByte[r];
        }
        return new EncryptionKey(fin);
    }
}
