package com.coronation.collections.repositories.predicate;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Toyin on 2/1/19.
 */
public class CustomPredicateBuilder<T> {
    private List<SearchCriteria> params;
    private String path;
    private Class<T> clazz;

    public CustomPredicateBuilder(String path, Class<T> clazz) {
        params = new ArrayList<>();
        this.path = path;
        this.clazz = clazz;
    }

    public CustomPredicateBuilder with(
            String key, Operation operation, Object value) {

        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }

        List<BooleanExpression> predicates = params.stream().map(param -> {
            CustomPredicate<T> predicate = new CustomPredicate(param, path, clazz);
            return predicate.getPredicate();
        }).filter(Objects::nonNull).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();
        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }
        return result;
    }
}
