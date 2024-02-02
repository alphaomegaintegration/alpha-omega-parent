package com.globalpayments.security.key;

public interface KeyStoreService {

    KeyStoreInfo createKeyStore(String clientId);
    KeyStoreInfo getKeyStore(String clientId);
    KeyStoreInfo saveOrUpdatePublicKey(String clientId, KeyPairInfo keyPairInfo);
    KeyStoreInfo saveKeyStoreToFile(String clientId, String filePath);
    String deleteKeyStore(String clientId);

    public enum KeyStoreType {
        JCEKS,JKS, PKCS12;
    }

    /*
    https://www.sslshopper.com/article-most-common-openssl-commands.html
     */
}
