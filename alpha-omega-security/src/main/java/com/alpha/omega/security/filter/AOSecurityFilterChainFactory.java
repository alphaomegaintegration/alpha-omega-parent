package com.alpha.omega.security.filter;

import jakarta.servlet.Filter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AOSecurityFilterChainFactory {

	public SecurityFilterChain createSecurityFilterChain(AOSecurityFilterChainRequest filterChainRequest);
	public SecurityFilterChain createSecurityFilterChain(List<Filter> filters, Set<String> protectedUrls,
														 Map<String, HttpMethod> protectedUrlsMethod,
														 List<String> excludeUrls, HttpSecurity httpSecurity,
														 boolean disableCSRF);
}
