package com.alpha.omega.security.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ao.security")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@SuperBuilder
public class AOSecurityProperties {

	@Builder.Default
	String excludeUris = AOSecurityConstants.EMPTY_STR;
	@Builder.Default
	String identityUris = AOSecurityConstants.EMPTY_STR;
	@Builder.Default
	String engagementUris = AOSecurityConstants.EMPTY_STR;
	@Builder.Default
	boolean maskSensitive = Boolean.TRUE.booleanValue();
	@Builder.Default
	boolean useDefaultContextId = Boolean.TRUE.booleanValue();
	@Builder.Default
	String defaultContextId = AOSecurityConstants.DEFAULT_CONTEXT_ID;
	@Builder.Default
	boolean validateContextId = Boolean.FALSE.booleanValue();
	@Builder.Default
	String validContextIds = AOSecurityConstants.EMPTY_STR;
	@Builder.Default
	boolean useIdbrokerForAuthorizations = Boolean.FALSE.booleanValue();
	@Builder.Default
	boolean allowLogging = Boolean.FALSE.booleanValue();
	@Builder.Default
	String idbrokerServiceNameForAuthorization = AOSecurityConstants.EMPTY_STR;
	@Builder.Default
	boolean includeServiceNameAsHeaderForAuthorizations = Boolean.FALSE.booleanValue();
	String commitId;

}
