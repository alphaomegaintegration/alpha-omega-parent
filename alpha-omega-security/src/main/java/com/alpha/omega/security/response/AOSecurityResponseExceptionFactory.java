package com.alpha.omega.security.response;

import org.springframework.http.HttpHeaders;

public interface AOSecurityResponseExceptionFactory {

	AOSecurityResponse createPwcSecurityResponse(Exception exception, HttpHeaders httpHeaders);
}
