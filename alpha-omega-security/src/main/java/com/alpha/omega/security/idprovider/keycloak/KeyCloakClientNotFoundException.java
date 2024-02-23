package com.alpha.omega.security.idprovider.keycloak;

public class KeyCloakClientNotFoundException extends RuntimeException{
    public KeyCloakClientNotFoundException() {
    }

    public KeyCloakClientNotFoundException(String message) {
        super(message);
    }

    public KeyCloakClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyCloakClientNotFoundException(Throwable cause) {
        super(cause);
    }

    public KeyCloakClientNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
