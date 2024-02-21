package com.alpha.omega.security.authentication;

import com.alpha.omega.core.exception.AOBaseException;
import org.springframework.security.core.AuthenticationException;

public class AOBaseAuthenticationException extends AuthenticationException {

	public AOBaseAuthenticationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public AOBaseAuthenticationException(String msg) {
		super(msg);
	}
}
