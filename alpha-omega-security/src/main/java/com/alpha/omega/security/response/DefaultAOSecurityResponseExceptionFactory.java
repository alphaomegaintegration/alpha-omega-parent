package com.alpha.omega.security.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.alpha.omega.core.Constants.CORRELATION_ID;
import static com.alpha.omega.security.utils.AOSecurityUtils.getStackTraceAsList;

public class DefaultAOSecurityResponseExceptionFactory implements AOSecurityResponseExceptionFactory {
	private static Logger logger = LogManager.getLogger(DefaultAOSecurityResponseExceptionFactory.class);

	@Override
	public AOSecurityResponse createAOSecurityResponse(Exception exception, HttpHeaders httpHeaders) {

		logger.info("createAOSecurityResponse Handling exception => {}", exception.getClass().getName(), exception);
		final List<String> content = logger.isDebugEnabled() ? getStackTraceAsList(exception) :
				Collections.singletonList(exception.getMessage());
		final String correlationId = httpHeaders.getFirst(CORRELATION_ID);
		final HttpStatus httpStatus = mappedFromException(exception);
		LocalDateTime now = LocalDateTime.now();
		AOSecurityResponse aoSecurityResponse = AOSecurityResponse.builder()
				.message(content)
				.correlationId(correlationId)
				.status(httpStatus)
				.timestamp(now)
				.build();

		return aoSecurityResponse;
	}

	HttpStatus mappedFromException(Exception exception) {
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
		if (exception instanceof IllegalArgumentException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else if (exception instanceof NullPointerException) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		} /*else if (exception instanceof AONotFoundException) {
			httpStatus = HttpStatus.NOT_FOUND;
		} else if (exception instanceof AOBadRequestException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		}else if (exception instanceof TypeMismatchException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else if (exception instanceof AOConflictException) {
			httpStatus = HttpStatus.CONFLICT;
		} else if (exception instanceof AONoAuthorizationsException) {
			httpStatus = HttpStatus.FORBIDDEN;
		} else if (exception instanceof AOBaseAuthenticationException) {
			httpStatus = mappedFromException((Exception) exception.getCause());
		} else if (exception instanceof AOAuthenticationException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof BadCredentialsException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} else if (exception instanceof AOTokenVerificationException) {
			httpStatus = HttpStatus.UNAUTHORIZED;
		} */ else {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		logger.debug("translating exception {} to HttpStatus => {}", exception, httpStatus);
		return httpStatus;
	}

}
