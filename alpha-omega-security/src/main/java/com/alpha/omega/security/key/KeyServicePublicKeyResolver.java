package com.globalpayments.security.key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;

public class KeyServicePublicKeyResolver extends JWKURLPublicKeyBase implements PublicKeyResolver {

    private static Logger logger = LoggerFactory.getLogger(KeyServicePublicKeyResolver.class);
    private KeyService keyService;

    public KeyServicePublicKeyResolver(KeyService keyService) {
        this.keyService = keyService;
        logger.info("Creating KeyServicePublicKeyResolver.....");
    }

    @Override
    public PublicKey resolvePublicKey(PublicKeyResolverRequest publicKeyRequest) {
        PublicKey publicKey = null;
        KeyPairInfo keyPairInfo = keyService.findKeyPair(null, publicKeyRequest.getPublicKeyId(), publicKeyRequest.getClientId());
        if (keyPairInfo != null && keyPairInfo.getKeyPair() != null) {
            publicKey = keyPairInfo.getKeyPair().getPublic();
        }
        if (null == publicKey){
            logger.info("KeyServicePublicKeyResolver could not find public key with request => {}",publicKeyRequest);
        } else {
            logger.info("KeyServicePublicKeyResolver found public key with request => {}",publicKeyRequest);
        }

        return publicKey;
    }


}
