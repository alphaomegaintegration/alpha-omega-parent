package com.alpha.omega.security;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.Map;
import java.util.Set;

import static com.alpha.omega.security.SecurityConstants.CLIENT_REGISTRATION_KEY_PREFIX;

@RedisHash(CLIENT_REGISTRATION_KEY_PREFIX)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ClientRegistrationEntity {

    @Id
    private String id;
    @Indexed
    private String registrationId;
    private String clientId;
    private String clientSecret;
    private ClientAuthenticationMethod clientAuthenticationMethod;
    private AuthorizationGrantType authorizationGrantType;
    private String redirectUri;
    private Set<String> scopes;
    private String authorizationUri;
    private String tokenUri;
    private String userInfoUri;
    private AuthenticationMethod userInfoAuthenticationMethod;
    private String userNameAttributeName;
    private String jwkSetUri;
    private String issuerUri;
    private Map<String, Object> configurationMetadata;
    private String clientName;
    @Builder.Default
    private Boolean enabled = Boolean.TRUE;
}
