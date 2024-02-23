package com.alpha.omega.security.token;

import com.auth0.jwt.interfaces.DecodedJWT;

/*
https://github.com/auth0/java-jwt
 */
public class TokenResponse {
	private DecodedJWT decodedJWT;


	public DecodedJWT getDecodedJWT() {
		return decodedJWT;
	}

	public void setDecodedJWT(DecodedJWT decodedJWT) {
		this.decodedJWT = decodedJWT;
	}
}
