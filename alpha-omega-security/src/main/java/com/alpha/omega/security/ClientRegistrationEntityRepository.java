package com.alpha.omega.security;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClientRegistrationEntityRepository extends CrudRepository<ClientRegistrationEntity, String> {
    Optional<ClientRegistrationEntity> findByRegistrationId(String registrationId);
}
