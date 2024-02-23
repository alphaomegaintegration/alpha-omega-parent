package com.alpha.omega.security.authorization;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class NameableAuthorizationService implements AuthorizationService{

	AuthorizationService authorizationService;
	String name;

	public NameableAuthorizationService(AuthorizationService authorizationService, String name) {
		if (authorizationService == null){
			throw new IllegalArgumentException("AuthorizationService cannot be null.");
		}

		if (StringUtils.isBlank(name)){
			throw new IllegalArgumentException("AuthorizationService name cannot be null.");
		}
		this.authorizationService = authorizationService;
		this.name = name;
	}

	public AuthorizationService getAuthorizationService() {
		return authorizationService;
	}

	public String getName() {
		return name;
	}

	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		return authorizationService.getAuthorizations(authorizationRequest);
	}
}
