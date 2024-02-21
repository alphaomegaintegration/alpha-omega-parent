package com.alpha.omega.security.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface AOSecurityWebFilterChainFactory {

	static Logger logger = LogManager.getLogger(AOSecurityWebFilterChainFactory.class);

	public SecurityWebFilterChain createSecurityWebFilterChain(AORxSecurityWebFilterChainRequest filterChainRequest);

	default ServerWebExchangeMatcher pwcServerWebExchangeMatcher(Collection<String> includeUrls, Map<String, HttpMethod> protectedUrlsMethod){
		logger.debug("pwcServerWebExchangeMatcher includeUrls => {}",includeUrls);
		List<ServerWebExchangeMatcher> matchersList =  includeUrls.stream()
				.map(url -> new PathPatternParserServerWebExchangeMatcher(url))
				.collect(Collectors.toList());

		List<ServerWebExchangeMatcher> matchersListMethods =  protectedUrlsMethod.entrySet().stream()
				.map(entry -> new PathPatternParserServerWebExchangeMatcher(entry.getKey(),entry.getValue()))
				.collect(Collectors.toList());

		matchersListMethods.addAll(matchersList);

		logger.debug("pwcServerWebExchangeMatcher matchersListMethods => {}",matchersListMethods);
		OrServerWebExchangeMatcher serverWebExchangeMatcher = new OrServerWebExchangeMatcher(matchersListMethods);
		return serverWebExchangeMatcher;
	}

	default void validate(AORxSecurityWebFilterChainRequest filterChainRequest){
		Objects.requireNonNull(filterChainRequest,"PwcRxSecurityWebFilterChainRequest cannot be null");
		Objects.requireNonNull(filterChainRequest.getResolver(),"resolver cannot be null");
		Objects.requireNonNull(filterChainRequest.getConverter(),"converter cannot be null");
	}
}
