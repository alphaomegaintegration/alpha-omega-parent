package com.alpha.omega.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReactiveSecurityWebFilterFactory {


    AuthenticationWebFilter createFilter(ReactiveAuthenticationManager authenticationManager){
        final ServerWebExchangeMatcher xhrMatcher = (exchange) -> Mono.just(exchange.getRequest().getHeaders())
                .filter((h) -> h.getOrEmpty("X-Requested-With").contains("XMLHttpRequest"))
                .flatMap((h) -> ServerWebExchangeMatcher.MatchResult.match())
                .switchIfEmpty(ServerWebExchangeMatcher.MatchResult.notMatch());
        MediaTypeServerWebExchangeMatcher restMatcher = new MediaTypeServerWebExchangeMatcher(
                MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_XML, MediaType.MULTIPART_FORM_DATA,
                MediaType.TEXT_XML);
        restMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        ServerWebExchangeMatcher notHtmlMatcher = new NegatedServerWebExchangeMatcher(new MediaTypeServerWebExchangeMatcher(MediaType.TEXT_HTML));
        ServerWebExchangeMatcher restNotHtmlMatcher = new AndServerWebExchangeMatcher(Arrays.asList(notHtmlMatcher, restMatcher));
        ServerWebExchangeMatcher preferredMatcher = new OrServerWebExchangeMatcher(Arrays.asList(xhrMatcher, restNotHtmlMatcher));
        List<DelegatingServerAuthenticationEntryPoint.DelegateEntry> entryPoints = new ArrayList<>();
        entryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(xhrMatcher, new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
        DelegatingServerAuthenticationEntryPoint defaultEntryPoint = new DelegatingServerAuthenticationEntryPoint(entryPoints);
        defaultEntryPoint.setDefaultEntryPoint(new HttpBasicServerAuthenticationEntryPoint());
        //ServerHttpSecurity.this.defaultEntryPoints.add(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(preferredMatcher, defaultEntryPoint));
        AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationFilter.setRequiresAuthenticationMatcher(preferredMatcher);
        authenticationFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(defaultEntryPoint));
        authenticationFilter.setAuthenticationConverter(new ServerHttpBasicAuthenticationConverter());
        authenticationFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        return authenticationFilter;
        //http.addFilterAt(authenticationFilter, SecurityWebFiltersOrder.HTTP_BASIC);
    }

    public static class NoOpReactiveAuthenticationManager implements ReactiveAuthenticationManager{

        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
            String username = authentication.getName();
            return Mono.just(SecurityUtils.noOpAuthentication(username));
        }
    }
}
