package com.coronation.collections.util;

import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Created by Toyin on 2/3/19.
 */
public class GenericUtil {
    public static LocalDateTime truncateTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime ceilTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.DAYS).plusHours(24).minusNanos(1);
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64Utils.encodeToString(bytes);
    }

    public static LocalDateTime getFirstDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getFirstDayOfTheMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime getFirstDayOfTheYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);
    }

    public static String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    public static String generateKey(int keyLen) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keyLen);
        SecretKey secretKey = keyGen.generateKey();
        byte[] encoded = secretKey.getEncoded();
        return DatatypeConverter.printHexBinary(encoded).toLowerCase();
    }

    public static String generateRandomString(int length) {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] randomChars = new char[length];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            randomChars[i] = chars[random.nextInt(chars.length)];
        }

        return new String(randomChars);
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}
