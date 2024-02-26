package com.alpha.omega.security.idprovider.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;


//@Configuration
@EnableConfigurationProperties(value = {KeyCloakIdpProperties.class})
public class KeyCloakConfig {


    public static final String MASTER = "master";
    public static final String ADMIN_CLI = "admin-cli";

    @Bean
    Keycloak keycloak(KeyCloakIdpProperties keyCloakIdpProperties){

        /*

    public static Keycloak getInstance(String serverUrl, String realm, String username, String password, String clientId, String clientSecret) {
        return getInstance(serverUrl, realm, username, password, clientId, clientSecret, (SSLContext)null, (Object)null, false, (String)null);
    }
         */

        return  Keycloak.getInstance(
                keyCloakIdpProperties.baseUrl(),
                MASTER,
                keyCloakIdpProperties.adminUsername(),
                keyCloakIdpProperties.adminPassword(),
                ADMIN_CLI,
                keyCloakIdpProperties.adminClientSecret());
    }

}
