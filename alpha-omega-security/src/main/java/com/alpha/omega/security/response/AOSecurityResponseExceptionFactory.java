package com.alpha.omega.security.response;

import org.springframework.http.HttpHeaders;

public interface AOSecurityResponseExceptionFactory {

	AOSecurityResponse createAOSecurityResponse(Exception exception, HttpHeaders httpHeaders);
}
