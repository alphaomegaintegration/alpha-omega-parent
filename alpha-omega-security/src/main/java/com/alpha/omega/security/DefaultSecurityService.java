package com.alpha.omega.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class DefaultSecurityService implements SecurityService, ReactiveUserDetailsService {

    ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    @Override
    public Mono<String> getAccessToken(Authentication authentication, ServerWebExchange exchange) {
        return login(authentication, exchange, "okta");
    }

    public Mono<String> login(final Authentication authentication, ServerWebExchange exchange, String registrationId) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("okta")
                .principal(authentication)
                .attribute(ServerWebExchange.class.getName(), exchange)
                .build();

        return this.authorizedClientManager.authorize(authorizeRequest)
                .map(oAuth2AuthorizedClient -> oAuth2AuthorizedClient.getAccessToken())
				.thenReturn("index");
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return null;
    }
}
