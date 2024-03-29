package com.alpha.omega.security.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultAOSecurityWebFilterChainFactory implements AOSecurityWebFilterChainFactory {

	private static Logger logger = LogManager.getLogger(DefaultAOSecurityWebFilterChainFactory.class);

	/*
		https://medium.com/@mgray_94552/reactive-authorization-in-spring-security-943e6534aaeb
		 */

	ServerWebExchangeMatcher urlNotFoundServerWebExchangeMatcher;

	public DefaultAOSecurityWebFilterChainFactory(ServerWebExchangeMatcher urlNotFoundServerWebExchangeMatcher) {
		this.urlNotFoundServerWebExchangeMatcher = urlNotFoundServerWebExchangeMatcher;
	}

	@Override
	public SecurityWebFilterChain createSecurityWebFilterChain(AORxSecurityWebFilterChainRequest filterChainRequest) {

		logger.info("Creating SecurityWebFilterChain for request filterChainRequest => {}", filterChainRequest);
		validate(filterChainRequest);
		ServerHttpSecurity http = filterChainRequest.getHttpSecurity();

		ServerWebExchangeMatcher aoServerWebExchangeMatcher = aoServerWebExchangeMatcher(filterChainRequest.getProtectedUrls(), filterChainRequest.getProtectedUrlsMethod());
		WebFilter webFilter = aoAuthenticationWebFilter(filterChainRequest.getResolver(), filterChainRequest.getConverter(),
				aoServerWebExchangeMatcher);
		WebFilter urlNotFoundFilter = new AOHttpNotFoundWebFilter(urlNotFoundServerWebExchangeMatcher);


		filterChainRequest.getWebFilters().add(WebFilterPosition.newBuilder()
				.setWebFilter(urlNotFoundFilter)
				.setOrder(SecurityWebFiltersOrder.AUTHENTICATION)
				.setPosition(WebFilterPosition.OtherWebFilter.BEFORE)
				.build());

		filterChainRequest.getWebFilters().add(WebFilterPosition.newBuilder()
				.setWebFilter(webFilter)
				.setOrder(SecurityWebFiltersOrder.AUTHENTICATION)
				.setPosition(WebFilterPosition.OtherWebFilter.AT)
				.build());

		/*http
				.exceptionHandling()
				.and()
				//.addFilterBefore(requestLoggingWebFilter, SecurityWebFiltersOrder.HTTP_HEADERS_WRITER)
				//.addFilterBefore(urlNotFoundFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				//.addFilterAt(webFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.authorizeExchange()
				.pathMatchers(convertCollectionToStringArray(filterChainRequest.getExcludeUrls())).permitAll()
				.anyExchange().authenticated()
				.and()
				.httpBasic().disable()
				.formLogin().disable()
				//.csrf().disable()
				.logout().disable();

		if (filterChainRequest.isDisableCSRF()) {
			http.csrf().disable();
		}*/

		filterChainRequest.getWebFilters().forEach(wf -> {
			if (wf.getPosition() == WebFilterPosition.OtherWebFilter.BEFORE) {
				http.addFilterBefore(wf.getWebFilter(), wf.getOrder());
			} else if (wf.getPosition() == WebFilterPosition.OtherWebFilter.AFTER) {
				http.addFilterAfter(wf.getWebFilter(), wf.getOrder());
			} else {
				http.addFilterAt(wf.getWebFilter(), wf.getOrder());
			}
		});


		return http.build();

	}

	WebFilter aoAuthenticationWebFilter(ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver,
										 ServerAuthenticationConverter converter,
										 ServerWebExchangeMatcher aoServerWebExchangeMatcher) {
		AOAuthenticationWebFilter webFilter = new AOAuthenticationWebFilter(resolver);
		webFilter.setRequiresAuthenticationMatcher(aoServerWebExchangeMatcher);
		webFilter.setServerAuthenticationConverter(converter);
		return webFilter;
	}

	/*
	@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	http
		.x509(withDefaults())
		.authorizeExchange(exchanges -> exchanges
		    .anyExchange().permitAll()
		);
	return http.build();
}

public SecurityFilterChain createSecurityFilterChain(aoSecurityFilterChainRequest filterChainRequest);
	 */

	static String[] convertCollectionToStringArray(Collection<String> collection) {
		ArrayList<String> strings = new ArrayList<>(collection);
		return strings.toArray(new String[strings.size()]);
	}

}
