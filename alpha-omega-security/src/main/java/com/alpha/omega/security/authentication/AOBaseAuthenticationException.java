package com.alpha.omega.security.authentication;

import com.pwc.base.exceptions.PwcBaseException;
import org.springframework.security.core.AuthenticationException;

public class AOBaseAuthenticationException extends AuthenticationException {

	private PwcBaseException pwcBaseException;

//	public PwcBaseAuthenticationException(String msg, Throwable cause, PwcBaseException pwcBaseException) {
//		super(msg, pwcBaseException);
//		this.pwcBaseException = pwcBaseException;
//	}

	public AOBaseAuthenticationException(String msg, PwcBaseException pwcBaseException) {
		super(msg,pwcBaseException);
		this.pwcBaseException = pwcBaseException;
	}
}
