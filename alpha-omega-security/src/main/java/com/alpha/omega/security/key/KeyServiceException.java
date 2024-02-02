package com.globalpayments.security.key;

public class KeyServiceException extends RuntimeException {

    public KeyServiceException() {
    }

    public KeyServiceException(String message) {
        super(message);
    }

    public KeyServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyServiceException(Throwable cause) {
        super(cause);
    }

    public KeyServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
