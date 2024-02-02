package com.globalpayments.security.key;

import com.auth0.jwt.interfaces.RSAKeyProvider;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class SeparateKeyRsaKeyProvider implements RSAKeyProvider {

    PublicKey publicKey;
    PrivateKey privateKey;
    String privateKeyId;

    public SeparateKeyRsaKeyProvider(PublicKey publicKey, PrivateKey privateKey, String privateKeyId) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.privateKeyId = privateKeyId;
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return (RSAPublicKey)publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey)privateKey;
    }

    @Override
    public String getPrivateKeyId() {
        return privateKeyId;
    }
}
