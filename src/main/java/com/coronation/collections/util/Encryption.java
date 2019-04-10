package com.coronation.collections.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Encryption {
	
	Cipher ecipher;
	Cipher dcipher;
	
	@Value("${app.key}")
	private String k;

	
	public Encryption() throws Exception {
		String k = "Bar12345Bar12345";
		SecretKey key = new SecretKeySpec(k.getBytes(), "AES");
		ecipher = Cipher.getInstance("AES");
		dcipher = Cipher.getInstance("AES");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
	}

	public String encrypt(String str) throws Exception {
		// Encode the string into bytes using utf-8
		byte[] utf8 = str.getBytes("UTF8");

		// Encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// Encode bytes to base64 to get a string
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	public String decrypt(String str) throws Exception {
		// Decode base64 to get bytes
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		byte[] utf8 = dcipher.doFinal(dec);

		// Decode using utf-8
		return new String(utf8, "UTF8");
	}

	/*
	 * public static void main(String args[]) throws Exception {
	 * 
	 * String data = "Don't tell anybody!"; String k = "Bar12345Bar12345";
	 * 
	 * // SecretKey key = KeyGenerator.getInstance("AES").generateKey(); SecretKey
	 * key = new SecretKeySpec(k.getBytes(), "AES"); Encryption encrypter = new
	 * Encryption();
	 * 
	 * System.out.println("Original String: " + data);
	 * 
	 * String encrypted = encrypter.encrypt(data);
	 * 
	 * System.out.println("Encrypted String: " + encrypted);
	 * 
	 * String decrypted = encrypter.decrypt(encrypted);
	 * 
	 * System.out.println("Decrypted String: " + decrypted); }
	 */
}
