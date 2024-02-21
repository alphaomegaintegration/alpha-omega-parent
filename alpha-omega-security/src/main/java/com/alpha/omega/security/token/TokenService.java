package com.alpha.omega.security.token;

import java.util.Optional;

public interface TokenService {

	Optional<TokenResponse> validateToken(TokenRequest tokenRequest);
}
