package com.alpha.omega.security.idprovider.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "idp.provider.keycloak")
public record KeyCloakIdpProperties(String clientId, String clientSecret, String baseUrl, String tokenUri,
                                    String userUri, String realm, String adminTokenUri, String adminUsername,
                                    String adminPassword, String adminClientId, String adminClientSecret,
                                    String jwksetUri, String issuerUrl, String adminRealmUri) {
}
