package com.alpha.omega.security.filter;

import com.alpha.omega.core.exception.AOBaseException;
import com.alpha.omega.security.authentication.AOBaseAuthenticationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.log.LogMessage;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.alpha.omega.core.Constants.CORRELATION_ID;
import static com.alpha.omega.security.utils.AOSecurityConstants.SECURITY_AUTHENTICATION;

public class AOAuthenticationWebFilter implements WebFilter {

	/*
			See AuthenticationWebFilter
			org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration
			 */
	private static Logger logger = LogManager.getLogger(AOAuthenticationWebFilter.class);
	private AOServerAuthenticationEntryPoint entryPoint = new AOServerAuthenticationEntryPoint();
	private ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver;
	private ServerAuthenticationSuccessHandler authenticationSuccessHandler = new WebFilterChainServerAuthenticationSuccessHandler();
	private ServerAuthenticationConverter authenticationConverter = new ServerHttpBasicAuthenticationConverter();
	//private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint());
	private ServerAuthenticationFailureHandler authenticationFailureHandler = new ServerAuthenticationEntryPointFailureHandler(entryPoint);
	private ServerSecurityContextRepository securityContextRepository = NoOpServerSecurityContextRepository.getInstance();
	private ServerWebExchangeMatcher requiresAuthenticationMatcher = ServerWebExchangeMatchers.anyExchange();

	public AOAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		this.authenticationManagerResolver = (request) -> {
			return Mono.just(authenticationManager);
		};
		logger.info("---------- Created AOAuthenticationWebFilter with authenticationManagerResolver => {}",
				authenticationManagerResolver);
	}

	public AOAuthenticationWebFilter(ReactiveAuthenticationManagerResolver<ServerWebExchange> authenticationManagerResolver) {
		Assert.notNull(authenticationManagerResolver, "authenticationResolverManager cannot be null");
		this.authenticationManagerResolver = authenticationManagerResolver;
		logger.info("---------- Created AOAuthenticationWebFilter with authenticationManagerResolver => {}",
				this.authenticationManagerResolver);
	}

	@PostConstruct
	public void init() {
		logger.info("---------- Created AOAuthenticationWebFilter with authenticationManagerResolver => {}",
				authenticationManagerResolver);
	}

	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return this.requiresAuthenticationMatcher.matches(exchange)
				.doOnNext(matchResult -> logger.debug("AOAuthenticationWebFilter.filter matchResult.isMatch() => {} with variables => {}",
						matchResult.isMatch(), matchResult.getVariables()))
				.filter((matchResult) -> matchResult.isMatch())
				.map(extractURLVariables(exchange))
				.flatMap((matchResult) -> this.authenticationConverter.convert(exchange))
				//.doOnNext((authentication -> logger.debug("AOAuthenticationWebFilter.filter Got authentication => {}", authentication)))
				.switchIfEmpty(Mono.defer(() -> chain.filter(exchange).then(Mono.empty())))
				.flatMap((token) -> this.authenticate(exchange, chain, token))
				.onErrorResume(AuthenticationException.class, (ex) -> {  //AuthenticationException
					if (logger.isDebugEnabled()) {
						ex.printStackTrace();
					}
					return this.authenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), ex);
				})
				.onErrorResume(AOBaseException.class, (ex) -> {
					if (logger.isDebugEnabled()) {
						ex.printStackTrace();
					}
					return this.authenticationFailureHandler.onAuthenticationFailure(new WebFilterExchange(exchange, chain), new AOBaseAuthenticationException(ex.getMessage()));
				}).onErrorResume(Exception.class, (ex) -> {
					if (logger.isDebugEnabled()) {
						ex.printStackTrace();
					}
					return this.entryPoint.handleResponse(new WebFilterExchange(exchange, chain), ex);
				})
				.contextWrite(Context.of(CORRELATION_ID,
						exchange.getAttributeOrDefault(CORRELATION_ID, UUID.randomUUID().toString())));
		/*
		https://spring.io/blog/2023/03/28/context-propagation-with-project-reactor-1-the-basics
		 */
	}

	/*
	protected Mono<Void> sendRedirect(String targetUrl, ServerWebExchange exchange) {
		String transformedUrl = (isRemoteHost(targetUrl) ? targetUrl : exchange.transformUrl(targetUrl));
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setLocation(URI.create(transformedUrl));
		response.setStatusCode(getStatusCode());
		return Mono.empty();
	}

	 */

	Function<ServerWebExchangeMatcher.MatchResult, ServerWebExchangeMatcher.MatchResult> extractURLVariables(ServerWebExchange exchange) {
		return (matchResult) -> {
			Map<String, Object> variables = matchResult.getVariables();
			//String contextId = (String) matchResult.getVariables().get(BaseConstants.CONTEXT_ID);
			//logger.info("Found contextId {} in request ",contextId);
			//logger.info("matchResult.getVariables() {} in request ",matchResult.getVariables());
			exchange.getAttributes().putAll(variables);

			return matchResult;
		};
	}

	private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain, Authentication token) {
		return this.authenticationManagerResolver.resolve(exchange)
				.doOnNext((authenticationManager) -> logger.debug("Using authenticationManger => {}", authenticationManager))
				.flatMap((authenticationManager) -> authenticationManager.authenticate(token))
				//.doOnNext((authentication -> logger.debug("authenticate Got authentication => {}",authentication)))
				.switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalStateException("No provider found for " + token.getClass()))))
				.flatMap((authentication) -> this.onAuthenticationSuccess(authentication, new WebFilterExchange(exchange, chain)))
				.doOnError(AuthenticationException.class, (ex) -> logger.debug(LogMessage.format("Authentication failed: %s", ex.getMessage())));
	}

	protected Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
		ServerWebExchange exchange = webFilterExchange.getExchange();
		SecurityContextImpl securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(authentication);
		webFilterExchange.getExchange().getAttributes().put(SECURITY_AUTHENTICATION, authentication);
		return this.securityContextRepository.save(exchange, securityContext)
				.then(this.authenticationSuccessHandler.onAuthenticationSuccess(webFilterExchange, authentication))
				.contextWrite(ctx -> ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
	}

	public void setSecurityContextRepository(ServerSecurityContextRepository securityContextRepository) {
		Assert.notNull(securityContextRepository, "securityContextRepository cannot be null");
		this.securityContextRepository = securityContextRepository;
	}

	public void setAuthenticationSuccessHandler(ServerAuthenticationSuccessHandler authenticationSuccessHandler) {
		Assert.notNull(authenticationSuccessHandler, "authenticationSuccessHandler cannot be null");
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}

	public void setServerAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
		Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
		this.authenticationConverter = authenticationConverter;
	}

	public void setAuthenticationFailureHandler(ServerAuthenticationFailureHandler authenticationFailureHandler) {
		Assert.notNull(authenticationFailureHandler, "authenticationFailureHandler cannot be null");
		this.authenticationFailureHandler = authenticationFailureHandler;
	}

	public void setRequiresAuthenticationMatcher(ServerWebExchangeMatcher requiresAuthenticationMatcher) {
		Assert.notNull(requiresAuthenticationMatcher, "requiresAuthenticationMatcher cannot be null");
		this.requiresAuthenticationMatcher = requiresAuthenticationMatcher;
	}


}
