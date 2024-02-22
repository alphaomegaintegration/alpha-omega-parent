package com.alpha.omega.security.client;

import com.alpha.omega.security.context.UserContextPermissions;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.core.Constants.COLON;


public class SecurityUtils {


    public final static String BASIC_PREFIX = "Basic ";
    public final static String BEARER_PREFIX = "Bearer ";
    public final static String EMPTY_STRING = "";

    public static String basicAuthCredsFrom(String username, String password){
        return Base64.getEncoder().encodeToString(new StringBuilder(username)
                .append(COLON)
                .append(password)
                .toString().getBytes(Charset.defaultCharset()));
    }

    public static Tuple2<String,String> fromBasicAuthToTuple(String basicAuth){
        String token = basicAuth.replace(BASIC_PREFIX,EMPTY_STRING);
        String decoded = new String(Base64.getDecoder().decode(token.getBytes(Charset.defaultCharset())));
        String[] array = decoded.split(COLON);
        return Tuples.of(array[0],array[1]);
    }

    public static String fromBearerHeaderToToken(String authorization) {
        String token = authorization.replace(BEARER_PREFIX, EMPTY_STRING);
        return token;
    }

    public static Function<UserContextPermissions, UserDetails> convertUserContextPermissionsToUserDetails() {
        return userContextPermissions -> {
            List<GrantedAuthority> grantedAuthorities = userContextPermissions.getPermissions()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails user = SecurityUser.builder()
                    .authorities(grantedAuthorities)
                    .username(userContextPermissions.getUserId())
                    .build();
            return user;
        };
    }


    public static Function<UserContextPermissions, UserDetails> convertUserContextPermissionsToUserDetails(Optional<Jwt> jwt) {
        return userContextPermissions -> {
            List<GrantedAuthority> grantedAuthorities = userContextPermissions.getPermissions()
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails user = SecurityUser.builder()
                    .authorities(grantedAuthorities)
                    .jwt(jwt)
                    .username(userContextPermissions.getUserId())
                    .build();
            return user;
        };
    }

    public static final UserDetails noOpUserDetails(String username){
        return SecurityUser.builder()
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(username.toLowerCase())))
                .username(username)
                .build();
    }

    public static Authentication noOpAuthentication(String username){
        return new AnonymousAuthenticationToken(username, username, Collections.singletonList(new SimpleGrantedAuthority(username.toLowerCase())));
    }


}
