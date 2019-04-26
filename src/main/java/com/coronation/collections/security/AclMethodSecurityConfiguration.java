package com.coronation.collections.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Created by Toyin on 4/23/19.
 */
@Configuration
public class AclMethodSecurityConfiguration {
//        extends GlobalMethodSecurityConfiguration {
//    @Autowired
//    MethodSecurityExpressionHandler
//            methodSecurityExpressionHandler;
//
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        return methodSecurityExpressionHandler;
//    }
}

