package com.alpha.omega.security.key;

public class InvalidPublicKeyException extends RuntimeException {
	public InvalidPublicKeyException() {
	}

	public InvalidPublicKeyException(String message) {
		super(message);
	}

	public InvalidPublicKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPublicKeyException(Throwable cause) {
		super(cause);
	}

	public InvalidPublicKeyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
