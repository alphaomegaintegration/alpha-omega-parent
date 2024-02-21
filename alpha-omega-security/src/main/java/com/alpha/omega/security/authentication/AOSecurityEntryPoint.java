package com.alpha.omega.security.authentication;

import com.enterprise.pwc.datalabs.caching.DefaultObjectMapperFactory;
import com.enterprise.pwc.datalabs.caching.ObjectMapperFactory;
import com.enterprise.pwc.datalabs.security.response.DefaultPwcSecurityResponseExceptionFactory;
import com.enterprise.pwc.datalabs.security.response.PwcSecurityResponse;
import com.enterprise.pwc.datalabs.security.response.PwcSecurityResponseExceptionFactory;
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
	private PwcSecurityResponseExceptionFactory responseExceptionFactory = new DefaultPwcSecurityResponseExceptionFactory();

	public AOSecurityEntryPoint(PwcSecurityResponseExceptionFactory responseExceptionFactory) {
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
		PwcSecurityResponse securityResponse = responseExceptionFactory.createPwcSecurityResponse(exception, httpHeaders);

		logger.trace("PwcSecurityResponse => {}",securityResponse);
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
