package com.alpha.omega.security.idprovider.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class KeyCloakInitializer implements InitializingBean {

    private final Keycloak keycloak;

    private final KeycloakInitializerConfigurationProperties keycloakInitializerConfigurationProperties;

    private final ObjectMapper mapper;

    private static String REALM_ID;

    private static final String INIT_KEYCLOAK_PATH = "initializer/init-keycloak.json";
    private static final String INIT_KEYCLOAK_USERS_PATH =
            "initializer/init-keycloak-users.json";

    //@Bean
    protected Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.PASSWORD)
                .realm(keycloakInitializerConfigurationProperties.masterRealm())
                .clientId(keycloakInitializerConfigurationProperties.clientId())
                .username(keycloakInitializerConfigurationProperties.username())
                .password(keycloakInitializerConfigurationProperties.password())
                .serverUrl(keycloakInitializerConfigurationProperties.url())
                .build();
    }

    @Builder
    public record KeycloakUser(String username, String password, String email, boolean isAdmin){}

    @Builder
    @ConfigurationProperties(prefix = "keycloak-initializer")
    public record KeycloakInitializerConfigurationProperties(boolean initializeOnStartup, String masterRealm, String applicationRealm,
                                                             String clientId, String username, String password, String url){}

    public KeyCloakInitializer(Keycloak keycloak,
                               KeycloakInitializerConfigurationProperties keycloakInitializerConfigurationProperties,
                               ObjectMapper mapper) {
        this.keycloak = keycloak;
        this.keycloakInitializerConfigurationProperties = keycloakInitializerConfigurationProperties;
        this.mapper = mapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        REALM_ID = keycloakInitializerConfigurationProperties.applicationRealm();

        if (keycloakInitializerConfigurationProperties.initializeOnStartup()) {
            init(false);
        }
    }

    public void init(boolean overwrite) {

        log.info("Initializer start");

        List<RealmRepresentation> realms = keycloak.realms().findAll();
        boolean isAlreadyInitialized =
                realms.stream().anyMatch(realm -> realm.getId().equals(REALM_ID));

        if (isAlreadyInitialized && overwrite) {
            reset();
        }

        if (!isAlreadyInitialized || overwrite) {

            initKeycloak();

            log.info("Keycloak initialized successfully");
        } else {
            log.warn("Keycloak initialization cancelled: realm already exist");
        }
    }

    private void initKeycloak() {

        initKeycloakRealm();
        initKeycloakUsers();
    }

    private void initKeycloakRealm() {
        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(REALM_ID);
        realmRepresentation.setId(REALM_ID);

        Resource resource = new ClassPathResource(INIT_KEYCLOAK_PATH);
        try {
            RealmRepresentation realmRepresentationToImport =
                    mapper.readValue(resource.getFile(), RealmRepresentation.class);
            keycloak.realms().create(realmRepresentationToImport);
        } catch (IOException e) {
            String errorMessage =
                    String.format("Failed to import keycloak realm representation : %s", e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    private void initKeycloakUsers() {

        List<KeycloakUser> users = null;
        try {
            Resource resource = new ClassPathResource(INIT_KEYCLOAK_USERS_PATH);
            users =
                    mapper.readValue(
                            resource.getFile(),
                            mapper.getTypeFactory().constructCollectionType(ArrayList.class, KeycloakUser.class));
        } catch (IOException e) {
            String errorMessage = String.format("Failed to read keycloak users : %s", e.getMessage());
            log.error(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }

        users.stream().forEach(u -> initKeycloakUser(u));
    }

    private void initKeycloakUser(KeycloakUser user) {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(user.email());
        userRepresentation.setUsername(user.username());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        CredentialRepresentation userCredentialRepresentation = new CredentialRepresentation();
        userCredentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        userCredentialRepresentation.setTemporary(false);
        userCredentialRepresentation.setValue(user.username());
        userRepresentation.setCredentials(Arrays.asList(userCredentialRepresentation));
        keycloak.realm(REALM_ID).users().create(userRepresentation);

        if (user.isAdmin()) {
            userRepresentation =
                    keycloak.realm(REALM_ID).users().search(user.username()).get(0);
            UserResource userResource =
                    keycloak.realm(REALM_ID).users().get(userRepresentation.getId());
            List<RoleRepresentation> rolesToAdd =
                    Arrays.asList(keycloak.realm(REALM_ID).roles().get("admin").toRepresentation());
            userResource.roles().realmLevel().add(rolesToAdd);
        }
    }

    public void reset() {
        try {
            keycloak.realm(REALM_ID).remove();
        } catch (Exception e) {
            log.error("Failed to reset Keycloak", e);
        }
    }
}