package com.alpha.omega.security.idprovider.keycloak;

import com.alpha.omega.security.ClientRegistrationEntityRepository;
import com.alpha.omega.security.SecurityUtils;
import com.alpha.omega.security.UserContextPermissionsService;
import com.alpha.omega.security.UserContextRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.alpha.omega.security.idprovider.keycloak.KeyCloakUtils.MAP_OBJECT;
import static com.alpha.omega.security.idprovider.keycloak.KeyCloakUtils.convertResultMapToJwt;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCloakAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    /*
    AbstractUserDetailsReactiveAuthenticationManager
     */
    private static final Logger logger = LoggerFactory.getLogger(KeyCloakAuthenticationManager.class);

    private String defaultContext;
    private UserContextPermissionsService userContextService;
    @Builder.Default
    private Scheduler scheduler = Schedulers.boundedElastic();
    String realmBaseUrl;
    String realmTokenUri;
    String realmJwkSetUri;
    String issuerURL;
    String realmClientId;
    String realmClientSecret;
    ClientRegistrationEntityRepository clientRegistrationEntityRepository;

    WebClient webClient;
    @Builder.Default
    ObjectMapper objectMapper = new ObjectMapper();
    JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {

        /*
        HttpClient httpClient = HttpClient
                .create()
                .baseUrl(realmBaseUrl)
                .doOnError((req, ex) -> {
                            logger.info("req.fullPath() => {}", req.fullPath());
                        },
                        (resp, ex) -> {
                            logger.info("resp.status() => {}", resp.status().toString());
                            throw (RuntimeException)ex;
                        })
                .wiretap(true);

         */

        webClient = WebClient.builder().baseUrl(realmBaseUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                //.clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(realmJwkSetUri).build();
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerURL));
        jwtDecoder = nimbusJwtDecoder;
    }

    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        final UserContextRequest userContextRequest = UserContextRequest.builder().contextId(defaultContext).userId(username).build();

        return Mono.just(userContextRequest)
                .publishOn(this.scheduler)
                .flatMap(request -> userContextService.getUserContextByUserIdAndContextId(request))
                .map(SecurityUtils.convertUserContextPermissionsToUserDetails());
    }


    protected Mono<UserDetails> retrieveUser(Authentication authentication) {
        if (authentication instanceof BearerTokenAuthenticationToken) {
            BearerTokenAuthenticationToken bearerToken = (BearerTokenAuthenticationToken) authentication;
            Optional<Jwt> jwt = Optional.of(jwtDecoder.decode(bearerToken.getToken()));
            String username = jwt.get().getClaimAsString("email");
            UserContextRequest userContextRequest = UserContextRequest.builder()
                    .contextId(defaultContext)
                    .userId(username)
                    .build();
            return Mono.just(userContextRequest)
                    .publishOn(this.scheduler)
                    .flatMap(request -> userContextService.getUserContextByUserIdAndContextId(request))
                    .map(SecurityUtils.convertUserContextPermissionsToUserDetails(jwt));

        } else {
            return this.retrieveUser(authentication.getName());
        }

    }

    public Mono<Optional<Jwt>> passwordGrantLoginJwt(String username, String password) {

        return passwordGrantLoginMap(username, password).map(convertResultMapToJwt(jwtDecoder));
    }


    public Mono<Map<String, Object>> passwordGrantLoginMap(String username, String password) {

        logger.debug("using realmTokenUri => {}, realmClientId => {}", realmTokenUri, realmClientId);
        //logger.info("using realmClientSecret => {}, password => {}", realmClientSecret, password);
        return webClient.post().uri(realmTokenUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("client_id", realmClientId)
                        .with("client_secret", realmClientSecret)
                        .with("scope", "openid"))
                .exchangeToMono(response -> {
            logger.debug("response.statusCode() => {}", response.statusCode());
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(MAP_OBJECT);
            } else {
                // Turn to error
                return response.createError();
            }
        });
    }


    public Mono<Map<String, Object>> passwordGrantLoginMap(String username, String password, String contextId) {

        logger.debug("using realmTokenUri => {}, realmClientId => {}", realmTokenUri, realmClientId);
        //logger.info("using realmClientSecret => {}, password => {}", realmClientSecret, password);
        return webClient.post().uri(realmTokenUri)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("username", username)
                        .with("password", password)
                        .with("client_id", realmClientId)
                        .with("client_secret", realmClientSecret)
                        .with("scope", "openid"))
                .exchangeToMono(response -> {
                    logger.debug("response.statusCode() => {}", response.statusCode());
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(MAP_OBJECT);
                    } else {
                        // Turn to error
                        return response.createError();
                    }
                });
    }


    public Mono<Optional<Jwt>> validLoginJwt(String token) {

        return Mono.just(token).map(tk -> jwtDecoder.decode(tk)).map(jwt -> Optional.of(jwt));

    }

    Function<Tuple2<Authentication, UserDetails>, Mono<Tuple2<UserDetails, Optional<Jwt>>>> basicAuthOrJwtAccess() {
        return tuple -> {
            Authentication authentication = tuple.getT1();
            UserDetails userDetails = tuple.getT2();
            final String username = authentication.getName();
            logger.debug("Got username => {}", username);
            if (authentication instanceof BearerTokenAuthenticationToken) {
                return validLoginJwt(username).map(jwt -> Tuples.of(tuple.getT2(), jwt));
            } else {
                final String presentedPassword = (String) authentication.getCredentials();
                return passwordGrantLoginJwt(username, presentedPassword).map(jwt -> Tuples.of(tuple.getT2(), jwt));
            }
        };
    }

    // JwtAuthenticationToken
    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        /*
        username might be a token or user in basic auth
        BearerTokenAuthenticationToken or UsernamePasswordAuthenticationToken
         */
        //String username = authentication.getName();
        //logger.info("authentication name => {} TOKEN ??????? => {}", authentication.getClass().getName(), username);

        return   Mono.just(authentication)
                .flatMap(authen -> retrieveUser(authen))
                .publishOn(this.scheduler)
                .doOnNext(userDetails -> defaultPreAuthenticationChecks(userDetails))
                .map(userDetails -> Tuples.of(authentication, userDetails))
                .flatMap(basicAuthOrJwtAccess())
                .filter((tuple) -> tuple.getT2().isPresent())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid Credentials"))))
                .doOnNext(tuple -> defaultPostAuthenticationChecks(tuple.getT1()))
                .map(tuple -> this.createJwtAuthenticationToken(tuple));
        }

    private JwtAuthenticationToken createJwtAuthenticationToken(Tuple2<UserDetails, Optional<Jwt>> tuple) {
         /*
            public User(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities)
			User newUser = new User(user.getUsername(), jwt.getTokenValue(), user.isEnabled(), user.isAccountNonExpired(),
                jwt.getExpiresAt().isBefore(Instant.now()),user.isAccountNonLocked(), user.getAuthorities());
             */
        UserDetails user = tuple.getT1();
        Jwt jwt = tuple.getT2().get();
        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, user.getAuthorities());
        return token;
    }

    private void defaultPreAuthenticationChecks(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            this.logger.debug("User account is locked");
            throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }
        if (!user.isEnabled()) {
            this.logger.debug("User account is disabled");
            throw new DisabledException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            this.logger.debug("User account is expired");
            throw new AccountExpiredException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    private void defaultPostAuthenticationChecks(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            this.logger.debug("User account credentials have expired");
            throw new CredentialsExpiredException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
        }
    }
}
