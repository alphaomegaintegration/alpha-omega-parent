package com.alpha.omega.security.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasicServerWebExchangeConvertor implements Function<ServerWebExchange, Mono<Authentication>>, ServerAuthenticationConverter {
    public static final String BASIC = "Basic ";

    @Builder.Default
    private Charset credentialsCharset = StandardCharsets.UTF_8;


    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(authorization, "basic ")) {
            return Mono.empty();
        }
        String credentials = (authorization.length() <= BASIC.length()) ? "" : authorization.substring(BASIC.length());
        String decoded = new String(base64Decode(credentials), this.credentialsCharset);
        String[] parts = decoded.split(":", 2);
        if (parts.length != 2) {
            return Mono.empty();
        }
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(parts[0], parts[1]);
        token.setDetails(request.getHeaders().toSingleValueMap());
        return Mono.just(token);
    }

    private byte[] base64Decode(String value) {
        try {
            return Base64.getDecoder().decode(value);
        }
        catch (Exception ex) {
            return new byte[0];
        }
    }

    /**
     * Sets the {@link Charset} used to decode the Base64-encoded bytes of the basic
     * authentication credentials. The default is <code>UTF_8</code>.
     * @param credentialsCharset the {@link Charset} used to decode the Base64-encoded
     * bytes of the basic authentication credentials
     * @since 5.7
     */
    public final void setCredentialsCharset(Charset credentialsCharset) {
        Assert.notNull(credentialsCharset, "credentialsCharset cannot be null");
        this.credentialsCharset = credentialsCharset;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return this.apply(exchange);
    }
}
