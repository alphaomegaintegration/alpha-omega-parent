package com.alpha.omega.security.token;

import org.springframework.security.core.AuthenticationException;

public class AOTokenVerificationException extends AuthenticationException {
	public AOTokenVerificationException(String message, Exception exception) {
		super(message, exception);
	}

	public AOTokenVerificationException(String msg) {
		super(msg);
	}
}
