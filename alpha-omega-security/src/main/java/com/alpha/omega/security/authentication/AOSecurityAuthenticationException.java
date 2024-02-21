package com.alpha.omega.security.authentication;

import com.pwc.base.exceptions.PwcBaseException;
import com.pwc.base.exceptions.auth.PwcAuthenticationException;
import org.springframework.http.HttpStatus;

public class AOSecurityAuthenticationException extends PwcBaseException {

	private PwcAuthenticationException pwcAuthenticationException;
	private String correlationId;


	public AOSecurityAuthenticationException(String message, Throwable t) {
		super(HttpStatus.UNAUTHORIZED,message,t);
	}

	public AOSecurityAuthenticationException(String msg) {
		super(msg);
	}



	public static final class Builder {
		private PwcAuthenticationException pwcAuthenticationException;
		private String correlationId;
		private String message;
		private Throwable cause;

		private Builder() {
		}

		public static Builder aPwcSecurityAuthenticationException() {
			return new Builder();
		}

		public Builder setPwcAuthenticationException(PwcAuthenticationException pwcAuthenticationException) {
			this.pwcAuthenticationException = pwcAuthenticationException;
			return this;
		}

		public Builder setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setCause(Throwable cause) {
			this.cause = cause;
			return this;
		}

		public AOSecurityAuthenticationException build() {
			AOSecurityAuthenticationException AOSecurityAuthenticationException = new AOSecurityAuthenticationException(message, cause);
			AOSecurityAuthenticationException.correlationId = this.correlationId;
			AOSecurityAuthenticationException.pwcAuthenticationException = this.pwcAuthenticationException;
			return AOSecurityAuthenticationException;
		}
	}
}
