package com.alpha.omega.security.key;

import java.security.PublicKey;

public class PublicKeyPublicKeyResolver implements PublicKeyResolver{

	PublicKey publicKey;

	public PublicKeyPublicKeyResolver(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public PublicKey resolvePublicKey(PublicKeyResolverRequest publicKeyRequest) {
		return publicKey;
	}
}
