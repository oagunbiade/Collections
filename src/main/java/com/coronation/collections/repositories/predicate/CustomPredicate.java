package com.coronation.collections.repositories.predicate;

import com.querydsl.core.types.dsl.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Toyin on 2/1/19.
 */
public class CustomPredicate<T> {
    private SearchCriteria criteria;
    private Class<T> clazz;
    private String path;

    public CustomPredicate(SearchCriteria criteria, String path, Class<T> clazz) {
        this.criteria = criteria;
        this.clazz = clazz;
        this.path = path;
    }

    public BooleanExpression getPredicate() {
        if (criteria.getValue() == null) return null;

        PathBuilder<T> entityPath = new PathBuilder<>(clazz, path);

        if (criteria.getOperation().getType().equalsIgnoreCase("number")) {
            NumberPath<BigDecimal> path = entityPath.getNumber(criteria.getKey(), BigDecimal.class);
            BigDecimal value = new BigDecimal(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case EQUALS:
                    return path.eq(value);
                case GREATER:
                    return path.goe(value);
                case LESS:
                    return path.loe(value);
            }
        }
        else if (criteria.getOperation().getType().equalsIgnoreCase("string")){
            StringPath path = entityPath.getString(criteria.getKey());
            switch (criteria.getOperation()) {
                case STRING_EQUALS:
                    return path.equalsIgnoreCase(criteria.getValue().toString());
                case LIKE:
                    return path.containsIgnoreCase(criteria.getValue().toString());
            }
        } else if (criteria.getOperation().getType().equalsIgnoreCase("date")) {
            if (criteria.getOperation().equals(Operation.BETWEEN)) {
                DateTimePath<LocalDateTime> path = entityPath.getDateTime(criteria.getKey(), LocalDateTime.class);
                LocalDateTime[] times = (LocalDateTime[]) criteria.getValue();
                return path.between(times[0], times[1]);
            } else if (criteria.getOperation().equals(Operation.DATE_EQUALS)) {
                DateTimePath<LocalDate> path = entityPath.getDateTime(criteria.getKey(), LocalDate.class);
                return path.eq((LocalDate) criteria.getValue());
            }
        } else if (criteria.getOperation().getType().equalsIgnoreCase("enum")) {
            return EnumBooleanExpression.getExpression(criteria.getKey(), criteria.getValue(), entityPath);
        } else if (criteria.getOperation().getType().equalsIgnoreCase("boolean")) {
            BooleanPath booleanPath = entityPath.getBoolean(criteria.getKey());
            return booleanPath.eq(Boolean.valueOf(String.valueOf(criteria.getValue())));
        }
        return null;
    }
}
