package com.globalpayments.security.key;

import com.auth0.jwt.interfaces.RSAKeyProvider;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class KeyPairRsaKeyProvider implements RSAKeyProvider {

   KeyPairInfo keyPairInfo;

    public KeyPairRsaKeyProvider(KeyPairInfo keyPairInfo) {
        this.keyPairInfo = keyPairInfo;
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return (RSAPublicKey)keyPairInfo.getKeyPair().getPublic();
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey)keyPairInfo.getKeyPair().getPrivate();
    }

    @Override
    public String getPrivateKeyId() {
        return keyPairInfo.getPrivateKeyId();
    }
}
