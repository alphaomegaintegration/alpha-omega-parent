package com.alpha.omega.security.idprovider.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import reactor.core.publisher.Mono;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;


public class KeyCloakUtils {

    static final Logger logger = LoggerFactory.getLogger(KeyCloakUtils.class);

    public static final String KEY_CLOAK_DEFAULT_CONTEXT = "user-context-service";
    final static Mono<Map<String, Object>> EMPTY_ACCESS_CREDS = Mono.empty();
    final static ParameterizedTypeReference<Map<String, Object>> MAP_OBJECT = new ParameterizedTypeReference<Map<String, Object>>() {
    };
    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    final static ResourceLoader resourceLoader = new DefaultResourceLoader();
    public static final String DEFAULT_CLIENT_TEMPLATE = "classpath:client/keycloak-client-template2.json";

    public final static Function<Map<String, Object>, Optional<Jwt>> convertResultMapToJwt(JwtDecoder jwtDecoder) {
        return map -> {
            String accessToken = (String)map.get("access_token");
            Jwt jwt = null;
            try{
                jwt = jwtDecoder.decode(accessToken);
            } catch (Exception e){
                logger.warn("Could not decode jwt!",e);
            }
            return Optional.ofNullable(jwt);
        };
    }

    public static Optional<ClientRepresentation> loadClientRepresentationTemplate(String defaultClientPath) {
        Optional<ClientRepresentation> clientRepresentation = Optional.empty();
        try{
            Resource resource = resourceLoader.getResource(DEFAULT_CLIENT_TEMPLATE);
            clientRepresentation = Optional.ofNullable(OBJECT_MAPPER.readValue(resource.getInputStream(), ClientRepresentation.class));
        } catch(Exception e ){
            logger.error("Could not load {} for client template",defaultClientPath,e);
        }
        return clientRepresentation;
    }

    public static final Integer DEFAULT_KEY_SIZE = 256;
    public static String createAKey(){
        String key = null;
        try{
            SecretKey secretKey = generateKey(DEFAULT_KEY_SIZE);
            key = convertSecretKeyToString(secretKey);
        } catch (Exception e){
            logger.error("Could not create key ",e);
        }
         return key;
    }

    /* Generating Secret key */

    // Generating Secret Key using KeyGenerator class with 256
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey originalKey = keyGenerator.generateKey();
        return originalKey;
    }

    // Generating Secret Key using password and salt
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return originalKey;
    }

    /* Converting Secret key into String */
    public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
        // Converting the Secret Key into byte array
        byte[] rawData = secretKey.getEncoded();
        // Getting String - Base64 encoded version of the Secret Key
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    /* Converting String into Secret key into */
    public static SecretKey convertStringToSecretKeyto(String encodedKey) {
        // Decoding the Base64 encoded string into byte array
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // Rebuilding the Secret Key using SecretKeySpec Class
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        return originalKey;
    }

}
