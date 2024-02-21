package com.alpha.omega.security.authorization;

import reactor.core.publisher.Mono;

public interface AuthorizationServiceRx {

	Mono<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest);
}
