package com.coronation.collections.util;

import com.coronation.collections.domain.*;
import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.domain.enums.TaskType;
import com.coronation.collections.dto.AccessObject;
import com.coronation.collections.services.DistributorService;
import com.coronation.collections.services.MerchantService;
import com.coronation.collections.services.OrganizationService;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    public static boolean isStaffRole(Role role) {
        List<String> roles = Arrays.asList("RELATIONSHIP_MGR", "RELATIONSHIP_MGR_SUPERVISOR", "ADMIN");
        return roles.contains(role.getName());
    }

    public static boolean isRMUser(Role role) {
        return role.getName().toUpperCase().contains("RELATIONSHIP_MGR");
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

    public static boolean isDistributorUser(Distributor distributor, User user,
                                            DistributorService distributorService) {
        DistributorUser distributorUser = distributorService.findByUserId(user.getId());
        return distributorUser != null && distributorUser.getDistributor().equals(distributor);
    }

    public static boolean isProductValid(Product product) {
        return !product.getDeleted() && product.getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isMerchantValid(Merchant merchant) {
       return !merchant.getDeleted() && merchant.getStatus().equals(GenericStatus.ACTIVE) &&
               !merchant.getOrganization().getDeleted() &&
               merchant.getOrganization().getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isMerchantUser(Merchant merchant, User user, MerchantService merchantService) {
        List<MerchantUser> merchantUsers = merchantService.findByOrganizationUserId(user.getId());
        return !merchantUsers.isEmpty() && merchantUsers.get(0).getMerchant().equals(merchant);
    }

    public static boolean isOrganizationUser(Organization organization, User user,
                                             OrganizationService organizationService) {
        OrganizationUser organizationUser = organizationService.findByUserId(user.getId());
        return organizationUser != null && organizationUser.getOrganization().equals(organization);
    }

    public static boolean isMerchantDistributorValid(MerchantDistributor merchantDistributor) {
        return !merchantDistributor.getDeleted() && merchantDistributor.getStatus().equals(GenericStatus.ACTIVE);
    }

    public static boolean isDistributorValid(Distributor distributor) {
        return !distributor.getDeleted() && distributor.getStatus().equals(GenericStatus.ACTIVE);
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

    public static LocalDateTime dateTimeFromString(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.parse(dateStr, formatter);
    }

    public static List<AccessObject> getAccessors(List<MerchantUser> merchantUsers,
                          List<AccessControlEntry> aclEntries) {
        Map<String, List<String>> taskMap = new HashMap<>();
        Map<String, AccessObject> accessMap = new HashMap<>();
        for (MerchantUser merchantUser: merchantUsers) {
            User user = merchantUser.getOrganizationUser().getUser();
            List<String> tasks = new ArrayList<>();
            for (Task task: user.getRole().getTasks()) {
                tasks.add("ROLE_" + task.getName().name());
            }
            taskMap.put(user.getEmail(), tasks);
            accessMap.put(user.getEmail(), new AccessObject(user.getId(), user.getEmail(), user.getFirstName(),
                    user.getLastName()));
        }
        List<AccessControlEntry> principalAcls = new ArrayList<>();
        for (AccessControlEntry entry: aclEntries) {
            if (entry.getSid() instanceof GrantedAuthoritySid) {
                GrantedAuthoritySid authority = (GrantedAuthoritySid) entry.getSid();
                for (Map.Entry<String, List<String>> taskEntry: taskMap.entrySet()) {
                    if (taskEntry.getValue().contains(authority.getGrantedAuthority())) {
                        AccessObject accessObject = accessMap.get(authority.getGrantedAuthority());
                        if (accessObject != null) {
                            setAccess(accessObject, entry.getPermission(), entry.isGranting());
                        }
                    }
                }
            } else if (entry.getSid() instanceof PrincipalSid) {
                principalAcls.add(entry);

            }
        }
        principalAcls.forEach(acl -> {
            PrincipalSid principal = (PrincipalSid) acl.getSid();
            AccessObject accessObject = accessMap.get(principal.getPrincipal());
            if (accessObject != null) {
                setAccess(accessObject, acl.getPermission(), acl.isGranting());
            }
        });
        return new ArrayList<>(accessMap.values());
    }

    private static void setAccess(AccessObject access, Permission permission, boolean isGranting) {
        if (permission.getMask() == BasePermission.WRITE.getMask()) {
            access.setWrite(isGranting);
        } else if (permission.getMask() == BasePermission.ADMINISTRATION.getMask()) {
            access.setAdministration(isGranting);
        } else if (permission.getMask() == BasePermission.READ.getMask()) {
            access.setRead(isGranting);
        }
    }

    private static final String MERCHANT_ROLE_PREFIX = "MERCHANT";
    private static final String DISTRIBUTOR_ROLE_PREFIX = "DISTRIBUTOR";
}
