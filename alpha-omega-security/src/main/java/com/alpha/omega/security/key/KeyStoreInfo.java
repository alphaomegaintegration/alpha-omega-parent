package com.globalpayments.security.key;

import java.security.KeyStore;

public class KeyStoreInfo {

    KeyStore keyStore;
    String keyStoreId;

    public String getKeyStoreId() {
        return keyStoreId;
    }

    public void setKeyStoreId(String keyStoreId) {
        this.keyStoreId = keyStoreId;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyStoreInfo{");
        sb.append("keyStore=").append(keyStore);
        sb.append(", keyStoreId='").append(keyStoreId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
