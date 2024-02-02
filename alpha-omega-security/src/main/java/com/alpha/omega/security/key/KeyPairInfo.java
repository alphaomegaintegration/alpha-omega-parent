package com.globalpayments.security.key;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

public class KeyPairInfo {

    KeyPair keyPair;
    X509Certificate x509Certificate;
    String privateKeyId;
    String publicKeyId;

    public KeyPairInfo(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public KeyPairInfo() {
    }

    public String getPrivateKeyId() {
        return privateKeyId;
    }

    public void setPrivateKeyId(String privateKeyId) {
        this.privateKeyId = privateKeyId;
    }

    public String getPublicKeyId() {
        return publicKeyId;
    }

    public void setPublicKeyId(String publicKeyId) {
        this.publicKeyId = publicKeyId;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }

    public void setX509Certificate(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate;
    }
}
