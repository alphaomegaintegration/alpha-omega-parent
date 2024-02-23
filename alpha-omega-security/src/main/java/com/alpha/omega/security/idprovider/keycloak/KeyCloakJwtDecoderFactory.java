package com.alpha.omega.security.idprovider.keycloak;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class KeyCloakJwtDecoderFactory implements JwtDecoderFactory<ClientRegistration> {
    private static final Map<JwsAlgorithm, String> JCA_ALGORITHM_MAPPINGS;
    static {
        Map<JwsAlgorithm, String> mappings = new HashMap<>();
        mappings.put(MacAlgorithm.HS256, "HmacSHA256");
        mappings.put(MacAlgorithm.HS384, "HmacSHA384");
        mappings.put(MacAlgorithm.HS512, "HmacSHA512");
        JCA_ALGORITHM_MAPPINGS = Collections.unmodifiableMap(mappings);
    };

    private Function<ClientRegistration, JwsAlgorithm> jwsAlgorithmResolver = (
            clientRegistration) -> SignatureAlgorithm.RS256;
    private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();

    @Override
    public JwtDecoder createDecoder(ClientRegistration clientRegistration) {
        return this.jwtDecoders.computeIfAbsent(clientRegistration.getRegistrationId(), (key) -> {
            String jwkSetUri = clientRegistration.getProviderDetails().getJwkSetUri();
            String issuerUri = clientRegistration.getProviderDetails().getIssuerUri();
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
            nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
            return nimbusJwtDecoder;
        });
    }
}
