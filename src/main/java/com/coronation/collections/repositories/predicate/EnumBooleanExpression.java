package com.coronation.collections.repositories.predicate;

import com.coronation.collections.domain.enums.GenericStatus;
import com.coronation.collections.domain.enums.PaymentStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * Created by Toyin on 2/17/19.
 */
public class EnumBooleanExpression {
    public static  <T> BooleanExpression getExpression(String key, Object value, PathBuilder<T> entityPath) {
        switch (key) {
            case "status":
                return entityPath.getEnum(key, GenericStatus.class).eq((GenericStatus) value);
            case "paymentStatus":
                return entityPath.getEnum(key, PaymentStatus.class).eq((PaymentStatus) value);
        }
        return null;
    }
}
