package com.alpha.omega.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface SecurityService {
    Mono<String> getAccessToken(Authentication authentication, ServerWebExchange exchange);
}
