package com.alpha.omega.security.key;

import java.security.PublicKey;

/**
 * Public keys can be found in URLs, databases, keystores, etc
 */

@FunctionalInterface
public interface PublicKeyResolver {

	PublicKey resolvePublicKey(PublicKeyResolverRequest publicKeyRequest);
}
