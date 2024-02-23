package com.alpha.omega.security.token;

public interface InvalidatedTokenService {
	InvalidatedTokenResponse hasTokenBeenInvalidated(InvalidatedTokenRequest invalidatedTokenRequest);
}
