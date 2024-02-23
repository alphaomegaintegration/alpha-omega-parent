package com.alpha.omega.security.filter;

import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;

public final class AOAuthenticationWebFilterBuilder {
	private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();
	private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint());
	private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository.getInstance();
	private ServerWebExchangeMatcher requiresAuthenticationMatcher = ServerWebExchangeMatchers.anyExchange();
	private ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;
	private ServerAuthenticationConverter authenticationConverter = new ServerHttpBasicAuthenticationConverter();

	private AOAuthenticationWebFilterBuilder() {
	}

	public static AOAuthenticationWebFilterBuilder newBuilder() {
		return new AOAuthenticationWebFilterBuilder();
	}

	public AOAuthenticationWebFilterBuilder setAuthenticationSuccessHandler(ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
		this.authenticationSuccessHandler = authenticationSuccessHandler;
		return this;
	}

	public AOAuthenticationWebFilterBuilder setAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
		this.authenticationConverter = authenticationConverter;
		return this;
	}

	public AOAuthenticationWebFilterBuilder setAuthenticationFailureHandler(ServerAuthenticationFailureHandler authenticationFailureHandler) {
		this.authenticationFailureHandler = authenticationFailureHandler;
		return this;
	}

	public AOAuthenticationWebFilterBuilder setSecurityContextRepository(ServerSecurityContextRepository securityContextRepository) {
		this.securityContextRepository = securityContextRepository;
		return this;
	}

	public AOAuthenticationWebFilterBuilder setRequiresAuthenticationMatcher(ServerWebExchangeMatcher requiresAuthenticationMatcher) {
		this.requiresAuthenticationMatcher = requiresAuthenticationMatcher;
		return this;
	}

	public AOAuthenticationWebFilterBuilder setAuthenticationManagerResolver(ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {
		this.authenticationManagerResolver = authenticationManagerResolver;
		return this;
	}

	public AOAuthenticationWebFilter build() {
		AOAuthenticationWebFilter AOAuthenticationWebFilter = new AOAuthenticationWebFilter(authenticationManagerResolver);
		AOAuthenticationWebFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
		AOAuthenticationWebFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
		AOAuthenticationWebFilter.setSecurityContextRepository(securityContextRepository);
		AOAuthenticationWebFilter.setRequiresAuthenticationMatcher(requiresAuthenticationMatcher);
		return AOAuthenticationWebFilter;
	}
}
