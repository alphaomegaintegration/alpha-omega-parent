package com.alpha.omega.security.filter;

import jakarta.servlet.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultAOSecurityFilterChainFactory implements AOSecurityFilterChainFactory {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAOSecurityFilterChainFactory.class);

	/*
	Common CSRF resources
	https://docs.spring.io/spring-security/site/docs/5.5.x-SNAPSHOT/reference/html5/#csrf-when
	https://docs.spring.io/spring-security/reference/6.0.0-M3/features/exploits/csrf.html#csrf-when
	https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html
	https://www.baeldung.com/spring-security-csrf#example
	 */

	public SecurityFilterChain createSecurityFilterChain(AOSecurityFilterChainRequest filterChainRequest) {

		try {
			HttpSecurity http = filterChainRequest.getHttpSecurity();
			List<String> excludeUrls = filterChainRequest.getExcludeUrls();

			String[] unauthRoutesHolder = new String[excludeUrls.size()];
			final String[] unauthRoutes = excludeUrls.toArray(unauthRoutesHolder);

			List<AntPathRequestMatcher> requestPaths = filterChainRequest.getProtectedUrls().stream()
					.map(url -> new AntPathRequestMatcher(url))
					.collect(Collectors.toList());

			List<AntPathRequestMatcher> requestPathsMethod = filterChainRequest.getProtectedUrlsMethod().entrySet().stream()
					.map(entry -> AntPathRequestMatcher.antMatcher(entry.getValue(), entry.getKey()))
					.collect(Collectors.toList());

			requestPathsMethod.addAll(requestPaths);

			//AntPathRequestMatcher[] allRequestPaths = requestPathsMethod.stream().toArray(AntPathRequestMatcher[]::new);

			RequestMatcher orRequestMatcher = new OrRequestMatcher(requestPathsMethod.stream().map(rm -> (RequestMatcher)rm).collect(Collectors.toList()));
			List<Filter> filters = filterChainRequest.getFilters();

			http.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers(unauthRoutes).permitAll()
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
			);

			http.securityMatcher(orRequestMatcher)
					.authorizeHttpRequests((authz) -> authz.anyRequest().authenticated());

			/*
			http.anonymous();

			http.cors();

			 */

			filters.stream().forEach(filter -> http.securityMatcher(orRequestMatcher)
					.addFilterAfter(filter, SecurityContextHolderFilter.class));

			return http.build();
		} catch (Exception e) {
			throw new BeanCreationException("Could not create security filter chain", e);
		}
	}

	@Override
	public SecurityFilterChain createSecurityFilterChain(List<Filter> filters, Set<String> protectedUrls,
														 Map<String, HttpMethod> protectedUrlsMethod,
														 List<String> excludeUrls, HttpSecurity http,
														 boolean disableCSRF) {

		try {


			String[] unauthRoutesHolder = new String[excludeUrls.size()];
			final String[] unauthRoutes = excludeUrls.toArray(unauthRoutesHolder);

			logger.info("@@@@@@@@@@@@@@ unauthRoutes => {}",unauthRoutes);

			List<AntPathRequestMatcher> requestPaths = protectedUrls.stream()
					.map(url -> new AntPathRequestMatcher(url))
					.collect(Collectors.toList());

			List<AntPathRequestMatcher> requestPathsMethod = protectedUrlsMethod.entrySet().stream()
					.map(entry -> AntPathRequestMatcher.antMatcher(entry.getValue(), entry.getKey()))
					.collect(Collectors.toList());

			requestPathsMethod.addAll(requestPaths);

			RequestMatcher orRequestMatcher = new OrRequestMatcher(requestPathsMethod.stream().map(rm -> (RequestMatcher)rm).collect(Collectors.toList()));

			http.authorizeHttpRequests((authorize) -> authorize
					.requestMatchers(unauthRoutes).permitAll()
					.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
					.requestMatchers(orRequestMatcher).authenticated()
			);

			filters.stream().forEach(filter -> http.securityMatcher(orRequestMatcher)
					.addFilterAfter(filter, SecurityContextHolderFilter.class));

			if (disableCSRF){
				http.csrf().disable();
			}


			return http.build();
		} catch (Exception e) {
			throw new BeanCreationException("Could not create security filter chain", e);
		}


		/*
		return this.createSecurityFilterChain(aoSecurityFilterChainRequest.newBuilder()
				.setProtectedUrls(new HashSet<>(protectedUrls))
				.setHttpSecurity(http)
				.setFilters(new ArrayList<>(filters))
				.setExcludeUrls(new ArrayList<>(excludeUrls))
				.setDisableCSRF(disableCSRF)
				.setProtectedUrlsMethod(new LinkedHashMap<>(protectedUrlsMethod))
				.build());



		 */


	}
}
