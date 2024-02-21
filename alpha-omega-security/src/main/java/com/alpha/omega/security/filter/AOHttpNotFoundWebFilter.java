package com.alpha.omega.security.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class AOHttpNotFoundWebFilter implements WebFilter {

	private static Logger logger = LogManager.getLogger(AOHttpNotFoundWebFilter.class);
	private ServerWebExchangeMatcher exchangeMatcher;
	private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

	public AOHttpNotFoundWebFilter(ServerWebExchangeMatcher exchangeMatcher) {
		this.exchangeMatcher = exchangeMatcher;
	}

	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return this.exchangeMatcher.matches(exchange)
				.filter(ServerWebExchangeMatcher.MatchResult::isMatch)
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				.flatMap((result) -> {
			return Mono.fromRunnable(() -> {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(this.httpStatus);;
				//logger.debug(LogMessage.format("Redirecting to '%s'", newLocation));
			});
		});
	}

}
