package com.alpha.omega.security.authentication;

import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.security.response.AOSecurityResponse;
import com.alpha.omega.security.response.AOSecurityResponseExceptionFactory;
import com.alpha.omega.security.response.DefaultAOSecurityResponseExceptionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class AOSecurityEntryPoint implements AuthenticationEntryPoint {

	private static Logger logger = LogManager.getLogger(AOSecurityEntryPoint.class);

	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.PROTOTYPE);
	private AOSecurityResponseExceptionFactory responseExceptionFactory = new DefaultAOSecurityResponseExceptionFactory();

	public AOSecurityEntryPoint(AOSecurityResponseExceptionFactory responseExceptionFactory) {
		this.responseExceptionFactory = responseExceptionFactory;
	}

	public void commence(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, AuthenticationException authException)
			throws IOException, jakarta.servlet.ServletException {
		logger.debug("commence with exception => {}",authException.getClass().getName());
		handleResponse(response, authException);
	}

	void handleResponse(jakarta.servlet.http.HttpServletResponse response, Exception exception) throws IOException {
		logger.debug("handleResponse with exception => {}",exception.getClass().getName());
		HttpHeaders httpHeaders = new HttpHeaders();
		response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		AOSecurityResponse securityResponse = responseExceptionFactory.createAOSecurityResponse(exception, httpHeaders);

		logger.trace("aoSecurityResponse => {}",securityResponse);
		response.setStatus(securityResponse.getStatus().value());
		response.getWriter().print(objectMapper.writeValueAsString(securityResponse));
		response.flushBuffer();
	}

	public void commenceException(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Exception exception)
			throws IOException, jakarta.servlet.ServletException  {

		logger.debug("commenceException with exception => {}",exception.getClass().getName());
		handleResponse(response, exception);
	}
}
