package com.alpha.omega.security;

import reactor.core.publisher.Mono;

public interface ClientRegistrationService {
    Mono<ClientRegistrationEntity> upsertClientRegistrationEntity(ClientRegistrationEntity clientRegistration);
    Mono<ClientRegistrationEntity> deleteClientRegistrationEntity(ClientRegistrationEntity clientRegistration);
}
