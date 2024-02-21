package com.alpha.omega.security.filter;

import com.enterprise.pwc.datalabs.security.authentication.PwcSecurityEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import java.io.IOException;

public final class AOSecurityFilter extends OncePerRequestFilter {

	private static Logger logger = LogManager.getLogger(AOSecurityFilter.class);

	private RequestMatcher excludeRequestMatcher;

	private AuthenticationConverter authenticationConverter;

	private AuthenticationSuccessHandler successHandler;

	private PwcSecurityEntryPoint entryPoint;
	private AuthenticationFailureHandler failureHandler;

	private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

	public AOSecurityFilter(AuthenticationManager authenticationManager,
							AuthenticationConverter authenticationConverter) {
		this((AuthenticationManagerResolver<HttpServletRequest>) (r) -> authenticationManager, authenticationConverter);

		logger.info("=============== Creating PwcSecurityFilter ..........");
	}

	public AOSecurityFilter(AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
							AuthenticationConverter authenticationConverter) {
		Assert.notNull(authenticationManagerResolver, "authenticationManagerResolver cannot be null");
		Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
		this.authenticationManagerResolver = authenticationManagerResolver;
		this.authenticationConverter = authenticationConverter;
		logger.info("=============== Creating PwcSecurityFilter ..........");
	}

	public RequestMatcher getExcludeRequestMatcher() {
		return this.excludeRequestMatcher;
	}

	public void setExcludeRequestMatcher(RequestMatcher excludeRequestMatcher) {
		Assert.notNull(excludeRequestMatcher, "requestMatcher cannot be null");
		this.excludeRequestMatcher = excludeRequestMatcher;
	}

	public AuthenticationConverter getAuthenticationConverter() {
		return this.authenticationConverter;
	}

	public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
		Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
		this.authenticationConverter = authenticationConverter;
	}

	public AuthenticationSuccessHandler getSuccessHandler() {
		return this.successHandler;
	}

	public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
		Assert.notNull(successHandler, "successHandler cannot be null");
		this.successHandler = successHandler;
	}

	public AuthenticationFailureHandler getFailureHandler() {
		return this.failureHandler;
	}

	public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
		Assert.notNull(failureHandler, "failureHandler cannot be null");
		this.failureHandler = failureHandler;
	}

	public AuthenticationManagerResolver<HttpServletRequest> getAuthenticationManagerResolver() {
		return this.authenticationManagerResolver;
	}

	public void setAuthenticationManagerResolver(
			AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) {
		Assert.notNull(authenticationManagerResolver, "authenticationManagerResolver cannot be null");
		this.authenticationManagerResolver = authenticationManagerResolver;
	}

	public PwcSecurityEntryPoint getEntryPoint() {
		return entryPoint;
	}

	public void setEntryPoint(PwcSecurityEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
	}

	@PostConstruct
	public void init(){

		Assert.notNull(excludeRequestMatcher, "requestMatcher cannot be null");
		Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
		Assert.notNull(successHandler, "successHandler cannot be null");
		Assert.notNull(authenticationManagerResolver, "authenticationManagerResolver cannot be null");
		Assert.notNull(failureHandler, "failureHandler cannot be null");
		logger.info("=============== Configured PwcSecurityFilter ..........");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (this.excludeRequestMatcher.matches(request)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Did match request to " + this.excludeRequestMatcher);
			}
			filterChain.doFilter(request, response);
			return;
		}
		try {
			Authentication authenticationResult = attemptAuthentication(request, response);
			if (logger.isDebugEnabled()){
				logger.debug("authenticationResult => {}",authenticationResult);
			}
			if (authenticationResult == null) {
				filterChain.doFilter(request, response);
				return;
			}
			jakarta.servlet.http.HttpSession session = request.getSession(false);
			if (session != null) {
				request.changeSessionId();
			}
			successfulAuthentication(request, response, filterChain, authenticationResult);
		} catch (AuthenticationException ex) {
			unsuccessfulAuthentication(request, response, ex);
		} catch (Exception ex) {
			entryPoint.commenceException(request, response, ex);
		}
	}

	private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
											AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		this.failureHandler.onAuthenticationFailure(request, response, failed);
	}

	private void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
										  Authentication authentication) throws IOException, ServletException {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		//logger.info("REMOVE THIS LOG Setting SecurityContext with authentication => {}",authentication);
		this.successHandler.onAuthenticationSuccess(request, response, chain, authentication);
	}

	private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, ServletException {
		Authentication authentication = this.authenticationConverter.convert(request);
		if (authentication == null) {
			return null;
		}
		AuthenticationManager authenticationManager = this.authenticationManagerResolver.resolve(request);
		if (logger.isDebugEnabled()){
			logger.debug("Using authenticationManager => {} ",authenticationManager);
		}
		Authentication authenticationResult = authenticationManager.authenticate(authentication);
		if (authenticationResult == null) {
			throw new ServletException("AuthenticationManager should not return null Authentication object.");
		}
		return authenticationResult;
	}

	public static Builder newBuilder() {
		return new Builder();
	}




	public static final class Builder {
		private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;
		private AuthenticationConverter authenticationConverter;
		private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		private AuthenticationFailureHandler failureHandler = new AuthenticationEntryPointFailureHandler(
				new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
		private PwcSecurityEntryPoint entryPoint;

		private Builder() {
		}

		public static Builder aPwcSecurityFilter() {
			return new Builder();
		}

		public Builder setRequestMatcher(RequestMatcher requestMatcher) {
			this.requestMatcher = requestMatcher;
			return this;
		}

		public Builder setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
			this.authenticationConverter = authenticationConverter;
			return this;
		}

		public Builder setSuccessHandler(AuthenticationSuccessHandler successHandler) {
			this.successHandler = successHandler;
			return this;
		}

		public Builder setFailureHandler(AuthenticationFailureHandler failureHandler) {
			this.failureHandler = failureHandler;
			return this;
		}

		public Builder setAuthenticationManagerResolver(AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver) {
			this.authenticationManagerResolver = authenticationManagerResolver;
			return this;
		}

		public Builder setEntryPoint(PwcSecurityEntryPoint entryPoint) {
			this.entryPoint = entryPoint;
			return this;
		}

		public AOSecurityFilter build() {
			AOSecurityFilter AOSecurityFilter = new AOSecurityFilter(authenticationManagerResolver, authenticationConverter);
			AOSecurityFilter.setExcludeRequestMatcher(requestMatcher);
			AOSecurityFilter.setSuccessHandler(successHandler);
			AOSecurityFilter.setFailureHandler(failureHandler);
			AOSecurityFilter.setEntryPoint(entryPoint);
			return AOSecurityFilter;
		}
	}
}


