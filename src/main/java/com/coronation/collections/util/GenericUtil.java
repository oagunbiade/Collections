package com.coronation.collections.util;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.MerchantService;
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

    public static boolean isStaffEmail(String email) {
        return email.toLowerCase().endsWith(Constants.STAFF_EMAIL_SUFFIX);
    }

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isMerchantUser(Role role) {
        return role.getName().toUpperCase().startsWith(MERCHANT_ROLE_PREFIX);
    }

    public static boolean isDistributorUser(Role role) {
        return role.getName().toUpperCase().startsWith(DISTRIBUTOR_ROLE_PREFIX);
    }

    public static boolean isDistributorMerchant(Merchant merchant, User user, DistributorService distributorService) {
        DistributorUser distributorUser = distributorService.findByUserId(user.getId());
        if (distributorUser == null) {
            return false;
        } else {
            MerchantDistributor merchantDistributor = distributorService.
                    findByMerchantIdAndDistributorId(merchant.getId(), distributorUser.getDistributor().getId());
            return merchantDistributor != null;
        }
    }

    public static boolean isProductValid(Product product) {
        return !product.getDeleted() && product.getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isMerchantAccountValid(MerchantAccount account) {
        return !account.getDeleted() && account.getStatus().equals(GenericStatus.ACTIVE) &&
                isAccountValid(account.getAccount());
    }

    public static boolean isMerchantValid(Merchant merchant) {
       return !merchant.getDeleted() && merchant.getStatus().equals(GenericStatus.ACTIVE) &&
               !merchant.getOrganization().getDeleted() &&
               merchant.getOrganization().getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isAccountValid(Account account) {
        return !account.getDeleted() &&
            account.getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isMerchantProduct(Product product, User user, MerchantService merchantService) {
        MerchantUser merchantUser = merchantService.findByMerchantUserId(user.getId());
        return merchantUser != null && merchantUser.getMerchant().equals(product.getMerchant());
    }

    public static LocalDateTime[] getDateRange(LocalDateTime from, LocalDateTime to) {
        if (from == null) {
            from = LocalDateTime.now();
        }
        if (to == null || to.isBefore(from)) {
            to = from;
        }
        from = GenericUtil.truncateTime(from);
        to = GenericUtil.ceilTime(to);
        return new LocalDateTime[] {from, to};
    }

    private static final String MERCHANT_ROLE_PREFIX = "MERCHANT";
    private static final String DISTRIBUTOR_ROLE_PREFIX = "DISTRIBUTOR";
}
