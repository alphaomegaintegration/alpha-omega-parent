package com.alpha.omega.security.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class AOJwtHeader {
	private String typ;
	private String alg;
	private String kid;
	private String x5t;

	public AOJwtHeader() {
	}

	public String getTyp() {
		return typ;
	}

	public String getAlg() {
		return alg;
	}

	public String getKid() {
		return kid;
	}

	public String getX5t() {
		return x5t;
	}
}
