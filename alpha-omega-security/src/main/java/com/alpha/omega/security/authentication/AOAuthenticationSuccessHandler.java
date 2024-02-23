package com.alpha.omega.security.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class AOAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

	private static Logger logger = LogManager.getLogger(AOAuthenticationSuccessHandler.class);
	protected ApplicationEventPublisher eventPublisher;

	public AOAuthenticationSuccessHandler(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
		if (this.logger.isTraceEnabled()) {
			this.logger.trace(LogMessage.format("Set SecurityContextHolder for user %s with authorities %s",authResult.getDetails(), authResult.getAuthorities()));
		}
		//response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		/*
		if (this.eventPublisher != null) {
			this.eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}

		 */
	}
}
