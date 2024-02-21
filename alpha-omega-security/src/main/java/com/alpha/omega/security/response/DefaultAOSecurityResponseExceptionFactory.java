package com.alpha.omega.security.response;

import com.enterprise.pwc.datalabs.security.authentication.PwcBaseAuthenticationException;
import com.enterprise.pwc.datalabs.security.authorization.PwcNoAuthorizationsException;
import com.enterprise.pwc.datalabs.security.token.PwcTokenVerificationException;
import com.pwc.base.exceptions.PwCBadRequestException;
import com.pwc.base.exceptions.PwcConflictException;
import com.pwc.base.exceptions.PwcNotFoundException;
import com.pwc.base.exceptions.auth.PwcAuthenticationException;
import com.pwc.base.log.PWCLogUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.pwc.base.utils.BaseConstants.CORRELATION_ID;

public class DefaultAOSecurityResponseExceptionFactory implements PwcSecurityResponseExceptionFactory {
	private static Logger logger = LogManager.getLogger(DefaultAOSecurityResponseExceptionFactory.class);

	@Override
	public PwcSecurityResponse createPwcSecurityResponse(Exception exception, HttpHeaders httpHeaders) {

		logger.info("createPwcSecurityResponse Handling exception => {}", exception.getClass().getName(), exception);
		final List<String> content = logger.isDebugEnabled() ? PWCLogUtils.getStackTraceAsList(exception) :
				Collections.singletonList(exception.getMessage());
		final String correlationId = httpHeaders.getFirst(CORRELATION_ID);
		final HttpStatus httpStatus = mappedFromException(exception);
		LocalDateTime now = LocalDateTime.now();
		PwcSecurityResponse pwcSecurityResponse = PwcSecurityResponse.newBuilder()
				.setMessage(content)
				.setCorrelationId(correlationId)
				.setStatus(httpStatus)
				.setTimestamp(now)
				.build();

		return pwcSecurityResponse;
	}

	HttpStatus mappedFromException(Exception exception) {
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
		if (exception instanceof IllegalArgumentException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else if (exception instanceof NullPointerException) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		} else if (exception instanceof PwcNotFoundException) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (exception instanceof PwCBadRequestException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}else if (exception instanceof TypeMismatchException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else if (exception instanceof PwcConflictException) {
			httpStatus = HttpStatus.CONFLICT;
		} else if (exception instanceof PwcNoAuthorizationsException) {
			httpStatus = HttpStatus.FORBIDDEN;
		} else if (exception instanceof PwcBaseAuthenticationException) {
			httpStatus = mappedFromException((Exception) exception.getCause());
		} else if (exception instanceof PwcAuthenticationException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof BadCredentialsException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof PwcTokenVerificationException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		logger.debug("translating exception {} to HttpStatus => {}", exception, httpStatus);
		return httpStatus;
	}

}
