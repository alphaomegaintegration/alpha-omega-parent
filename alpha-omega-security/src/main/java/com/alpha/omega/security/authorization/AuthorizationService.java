package com.alpha.omega.security.authorization;

import java.util.Optional;

public interface AuthorizationService {

	Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest);
}
