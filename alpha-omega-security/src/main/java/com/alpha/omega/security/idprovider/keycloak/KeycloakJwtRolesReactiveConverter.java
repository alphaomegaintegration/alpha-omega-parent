package com.alpha.omega.security.idprovider.keycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;

public class KeycloakJwtRolesReactiveConverter implements Converter<Jwt, Mono<JwtAuthenticationToken>> {

    KeycloakJwtRolesConverter jwtRolesConverter = new KeycloakJwtRolesConverter();

    @Override
    public Mono<JwtAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = jwtRolesConverter.convert(jwt);
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);
        return Mono.just(token);
    }
    // JwtAuthenticationToken
}
