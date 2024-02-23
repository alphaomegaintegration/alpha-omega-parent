package com.alpha.omega.security.token;

import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenRequest {

	private String token;
	private String correlationId;
	private DecodedJWT decodedJWT;

	public TokenRequest(String token) {
		this.token = token;
	}

	public TokenRequest(String token, String correlationId) {
		this.token = token;
		this.correlationId = correlationId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public DecodedJWT getDecodedJWT() {
		return decodedJWT;
	}

	public void setDecodedJWT(DecodedJWT decodedJWT) {
		this.decodedJWT = decodedJWT;
	}
}
