package com.alpha.omega.security.authorization;

import java.util.Optional;

public class NOOPAuthorizationService implements AuthorizationService {
	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		return Optional.empty();
	}
}
