package com.alpha.omega.security.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

import java.util.Map;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultUserContextPermissionsService implements UserContextPermissionsService{
    private static Logger logger = LogManager.getLogger(AOServerSecurityContextService.class);

    public static final String USER_CONTEXT_SERVICE_URI = "/usercontexts/user/{user}/context/{context}";

    WebClient webClient;
    String baseUrl;
    @Builder.Default
    String serviceAccount = "sa.reader@ucs.com";
    String serviceAccountCredentials;

    @PostConstruct
    public void init() {

        logger.info("##################### DefaultUserContextPermissionsService init ###################");
        webClient = WebClient.builder().baseUrl(baseUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                //.clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }

    @Override
    public Mono<UserContextPermissions> getUserContextByUserIdAndContextId(UserContextRequest userContextRequest) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(USER_CONTEXT_SERVICE_URI)
                        .build(Map.of("user",userContextRequest.getUserId(),"context", userContextRequest.getContextId())))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeToMono(response -> {
                    logger.debug("getUserContextByUserIdAndContextId response.statusCode() => {}", response.statusCode());
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(UserContextPermissions.class);
                    } else {
                        // Turn to error
                        return response.createError();
                    }
                });
    }
}
