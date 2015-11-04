package com.vedri.mtp.consumption.transaction;


public class ValidationFailedException extends RuntimeException {

    public ValidationFailedException(String message) {
        super(message);
    }

    public ValidationFailedException(String message, Exception e) {
        super(message, e);
    }
}
