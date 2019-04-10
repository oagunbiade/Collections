package com.coronation.collections.exception;

/**
 * Created by Toyin on 4/10/19.
 */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }
}
