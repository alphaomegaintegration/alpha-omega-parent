package com.alpha.omega.security.exception;

public class AOAuthenticationException extends RuntimeException {
    public AOAuthenticationException() {
    }

    public AOAuthenticationException(String message) {
        super(message);
    }

    public AOAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AOAuthenticationException(Throwable cause) {
        super(cause);
    }

    public AOAuthenticationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
