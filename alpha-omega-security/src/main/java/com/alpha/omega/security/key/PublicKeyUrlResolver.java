package com.globalpayments.security.key;
@FunctionalInterface
public interface PublicKeyUrlResolver {
    public String resolveUrl(PublicKeyResolverRequest publicKeyConfig);
}
