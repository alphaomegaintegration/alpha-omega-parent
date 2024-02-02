package com.globalpayments.security.key;

import java.security.PublicKey;

@FunctionalInterface
public interface PublicKeyResolver {
    public PublicKey resolvePublicKey(PublicKeyResolverRequest publicKeyConfig);
}
