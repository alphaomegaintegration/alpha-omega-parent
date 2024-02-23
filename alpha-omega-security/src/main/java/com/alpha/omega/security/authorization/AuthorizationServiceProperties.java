package com.alpha.omega.security.authorization;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.alpha.omega.security.utils.AOSecurityConstants.FIVE_EXPIRATION_SECONDS;
import static com.alpha.omega.security.utils.AOSecurityConstants.HOUR_EXPIRATION_SECONDS;

@ConfigurationProperties(prefix = "ao.authorization-service")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class AuthorizationServiceProperties {

	private boolean useCache = Boolean.FALSE.booleanValue();
	private boolean addUserNameToAuthorizations = Boolean.FALSE.booleanValue();
	private boolean remoteExceptionsAsAOAuthorizationExceptions = Boolean.TRUE.booleanValue();
	private String contextAuthorizationsLocations;
	private Integer authorizationsCacheExpirationSeconds = HOUR_EXPIRATION_SECONDS;
	private Integer securityExpirationSeconds = FIVE_EXPIRATION_SECONDS;
	private Long authorizationRequestTimeout = FIVE_EXPIRATION_SECONDS.longValue();

}
