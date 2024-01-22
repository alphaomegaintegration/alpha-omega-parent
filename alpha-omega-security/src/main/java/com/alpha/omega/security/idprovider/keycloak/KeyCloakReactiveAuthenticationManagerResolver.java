package com.alpha.omega.security.idprovider.keycloak;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class KeyCloakReactiveAuthenticationManagerResolver implements ReactiveAuthenticationManagerResolver<ServerWebExchange> {

    ReactiveAuthenticationManager keyCloakAuthenticationManager;

    public KeyCloakReactiveAuthenticationManagerResolver(ReactiveAuthenticationManager keyCloakAuthenticationManager) {
        this.keyCloakAuthenticationManager = keyCloakAuthenticationManager;
    }

    @Override
    public Mono<ReactiveAuthenticationManager> resolve(ServerWebExchange context) {
        return Mono.just(keyCloakAuthenticationManager);
    }
}
