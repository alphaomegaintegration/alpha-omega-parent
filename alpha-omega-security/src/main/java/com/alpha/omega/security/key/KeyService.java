package com.globalpayments.security.key;

import java.util.List;

public interface KeyService {
    KeyPairInfo createKeyPair(int keySize, String algorithm);
    KeyPairInfo saveKeyPair(KeyPairInfo keyPairInfo, String clientId);
    KeyPairInfo findKeyPair(String privateKeyId, String publicKeyId, String clientId);
    List<KeyPairInfo> findKeyPairs(String clientId);
    SecretKeyInfo createSecretKey(int keySize, String algorithm);
}
