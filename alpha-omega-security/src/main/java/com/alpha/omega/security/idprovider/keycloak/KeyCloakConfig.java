package com.alpha.omega.security.idprovider.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@EnableConfigurationProperties(value = {KeyCloakIdpProperties.class})
public class KeyCloakConfig {


}
