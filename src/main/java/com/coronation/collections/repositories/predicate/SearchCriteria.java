package com.coronation.collections.repositories.predicate;

/**
 * Created by Toyin on 2/1/19.
 */
public class SearchCriteria {
    private String key;
    private Operation operation;
    private Object value;

    public SearchCriteria(String key, Operation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
