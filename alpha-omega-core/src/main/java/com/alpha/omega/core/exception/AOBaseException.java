package com.alpha.omega.core.exception;


import java.util.Objects;


public class AOBaseException extends RuntimeException{

    private static final long serialVersionUID = -4024139128663973568L;

    public AOBaseException() {
    }

    public AOBaseException(String message) {
        super(message);
    }

    public AOBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public AOBaseException(Throwable cause) {
        super(cause);
    }

    public AOBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
