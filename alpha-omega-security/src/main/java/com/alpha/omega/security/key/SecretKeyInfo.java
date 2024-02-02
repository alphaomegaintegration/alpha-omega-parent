package com.globalpayments.security.key;

import javax.crypto.SecretKey;

public class SecretKeyInfo {

    SecretKey secretKey;

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
