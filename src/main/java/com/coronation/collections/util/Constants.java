package com.coronation.collections.util;

public class Constants {

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
    public static final String SIGNING_KEY = "devtoyin123r";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";


    public static final String ACCOUNT_RESPONSE_CODE = "00";
    public static final String ACCOUNT_ACTIVE_STATUS = "A";
    public static final String TRANSFER_RESPONSE_CODE = "00";
    public static final String BANK_CODE = "559";

    public static final String STAFF_EMAIL_SUFFIX = "coronationmb.com";
    public static final String DEFAULT_BANK_NAME = "Corronation Merchant Bank";

    public static final String USER_PROFILE_CREATED_MAIL = "Hi {firstName}, <br/>Your profile has been created with <br/> Email address: {email}"
            + "<br/> Password: {password}<br/>";
    public static final String USER_PROFILE_EDITED_MAIL = "Hi {firstName}, <br/>Your profile has been edited";
    public static final String USER_PROFILE_CREATED_MAIL_STAFF = "Hi {firstName}, <br/>Your profile has been created with <br/> Email address: {email}";
    public static final String PASSWORD_RESET_MAIL = "Hi {firstName}, <br/>Your profile password is now reset.<br/> Your new password: {email}";
    public static final String PASSWORD_CHANGED_MAIL = "Hi {firstName}, <br/>Your profile password is now reset.<br/> Your new password: {email}";
    public static final String MERCHANT_CREATED_MAIL = "Hi {firstName}, <br/>Your Merchant profile has been created with <br/> Name: {email}";
    public static final String DISTRIBUTOR_CREATED_MAIL = "Hi {firstName}, <br/>Your Distributor profile has been created with <br/> Name: {email}";

    public static final String PAYMENT_INITIATED = "Hi {firstName}, <br/>A payment linked to your profile was initiated.<br/> " +
            "Product Name: {productName}<br/> Amount: {amount}<br/> Due Date: {dueDate}<br/> Reference Code: {code}";
    public static final String PAYMENT_APPROVED = "Hi {firstName}, <br/>A payment linked to your profile was approved.<br/> " +
            "Product Name: {productName}<br/> Amount: {amount}<br/> Due Date: {dueDate}<br/> Reference Code: {code}";
    public static final String PAYMENT_CANCELED = "Hi {firstName}, <br/>A payment linked to your profile was canceled.<br/> " +
            "Product Name: {productName}<br/> Amount: {amount}<br/> Due Date: {dueDate}<br/> Reference Code: {code}";
    public static final String PAYMENT_EDITED = "Hi {firstName}, <br/>A payment linked to your profile was edited.<br/> " +
            "Product Name: {productName}<br/> Amount: {amount}<br/> Due Date: {dueDate}<br/> Reference Code: {code}";

    public static final String PAYMENT_INITIATED_SUBJECT = "Corronation Merchant Bank Collections App :: Payment Initiated";
    public static final String PAYMENT_APPROVED_SUBJECT = "Corronation Merchant Bank Collections App :: Payment Approved";
    public static final String PAYMENT_CANCELED_SUBJECT = "Corronation Merchant Bank Collections App :: Payment Canceled";
    public static final String PAYMENT_EDITED_SUBJECT = "Corronation Merchant Bank Collections App :: Payment Edited";

    public static final String USER_PROFILE_CREATED_SUBJECT = "Corronation Merchant Bank Collections App :: User Profile created";
    public static final String USER_PROFILE_EDITED_SUBJECT = "Corronation Merchant Bank Collections App :: User Profile edited";
    public static final String USER_PASSWORD_RESET_SUBJECT = "Corronation Merchant Bank Collections App :: Password Reset";
    public static final String USER_PASSWORD_CHANGED_SUBJECT = "Corronation Merchant Bank Collections App :: Password Changed";
    public static final String MERCHANT_PROFILE_CREATED_SUBJECT = "Corronation Merchant Bank Collections App :: Merchant Profile created";
    public static final String DISTRIBUTOR_PROFILE_CREATED_SUBJECT = "Corronation Merchant Bank Collections App :: Distributor Profile created";
}
