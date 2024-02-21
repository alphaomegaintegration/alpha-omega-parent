package com.alpha.omega.security.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pwc.token-service")
public class TokenServiceProperties {

	private int maxRetries;
	private long timeSkewSeconds;
	private TokenValidator tokenValidator = TokenValidator.token_service;
	private TokenServiceInvalidation tokenServiceInvalidation = TokenServiceInvalidation.noop_false;
	private TokenClaimsIssuerMapping tokenClaimsIssuerMapping = TokenClaimsIssuerMapping.pwc_identity;

	public int getMaxRetries() {
		return maxRetries;
	}

	public long getTimeSkewSeconds() {
		return timeSkewSeconds;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void setTimeSkewSeconds(long timeSkewSeconds) {
		this.timeSkewSeconds = timeSkewSeconds;
	}

	public TokenValidator getTokenValidator() {
		return tokenValidator;
	}

	public void setTokenValidator(TokenValidator tokenValidator) {
		this.tokenValidator = tokenValidator;
	}

	public TokenServiceInvalidation getTokenServiceInvalidation() {
		return tokenServiceInvalidation;
	}

	public void setTokenServiceInvalidation(TokenServiceInvalidation tokenServiceInvalidation) {
		this.tokenServiceInvalidation = tokenServiceInvalidation;
	}

	public TokenClaimsIssuerMapping getTokenClaimsIssuerMapping() {
		return tokenClaimsIssuerMapping;
	}

	public void setTokenClaimsIssuerMapping(TokenClaimsIssuerMapping tokenClaimsIssuerMapping) {
		this.tokenClaimsIssuerMapping = tokenClaimsIssuerMapping;
	}

	public enum TokenValidator{
		idbroker, token_service;
	}

	public enum TokenServiceInvalidation{
		local_cache, service_cache, noop_false;
	}

	public enum TokenClaimsIssuerMapping{
		pwc_identity, idbroker;
	}
}
