package com.alpha.omega.security.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisReactiveClientRegistrationRepository implements ReactiveClientRegistrationRepository, ClientRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(RedisReactiveClientRegistrationRepository.class);

    ClientRegistrationEntityRepository clientRegistrationEntityRepository;

    Function<Optional<ClientRegistrationEntity>, Optional<ClientRegistration>> convertClientRegistrationEntityToClientRegistration() {
        return entityOptional -> {
            Optional<ClientRegistration> clientRegistration = Optional.empty();
            if (entityOptional.isPresent()) {
                ClientRegistrationEntity entity = entityOptional.get();
                clientRegistration = Optional.of(ClientRegistration.withRegistrationId(entity.getRegistrationId())
                        .clientId(entity.getClientId())
                        .clientSecret(entity.getClientSecret())
                        .clientName(entity.getClientName())
                        .clientAuthenticationMethod(entity.getClientAuthenticationMethod())
                        .authorizationGrantType(entity.getAuthorizationGrantType())
                        .authorizationUri(entity.getAuthorizationUri())
                        .issuerUri(entity.getIssuerUri())
                        .jwkSetUri(entity.getJwkSetUri())
                        .redirectUri(entity.getRedirectUri())
                        .tokenUri(entity.getTokenUri())
                        .scope(entity.getScopes())
                        .userInfoAuthenticationMethod(entity.getUserInfoAuthenticationMethod())
                        .userInfoUri(entity.getUserInfoUri())
                        .providerConfigurationMetadata(entity.getConfigurationMetadata())
                        .userNameAttributeName(entity.getUserNameAttributeName())
                        .build());
            }
            return clientRegistration;
        };
    }

    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
        final String clientNotFoundMessage = new StringBuilder("Client with registration ").append(registrationId)
                .append(" not found.").toString();

        return Mono.just(registrationId)
                .map(regId -> clientRegistrationEntityRepository.findByRegistrationId(regId))
                .map(convertClientRegistrationEntityToClientRegistration())
                .map(clientRegistration -> clientRegistration.orElseThrow(() -> new ClientNotFoundException(clientNotFoundMessage)));
    }

    @Override
    public Mono<ClientRegistrationEntity> upsertClientRegistrationEntity(ClientRegistrationEntity clientRegistration) {
        return Mono.just(clientRegistration)
                .map(registration -> clientRegistrationEntityRepository.save(registration));
    }

    @Override
    public Mono<ClientRegistrationEntity> deleteClientRegistrationEntity(ClientRegistrationEntity clientRegistration) {
        return null;
    }
}
