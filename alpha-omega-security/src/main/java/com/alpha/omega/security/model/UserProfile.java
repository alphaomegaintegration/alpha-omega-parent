package com.pwc.base.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.Map;

@ToString
@Data
@Builder(toBuilder = true)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    private String name;
    private String firstName;
    private String lastName;
    private String accessToken;
	private String refreshToken;
    private String countryCode;
	private AccountType accountType;
	private String serviceName;
	private String identityProvider;
	private String contextId;
	private String engagementId;
	private String workspaceId;
	private String correlationId;
	private String email;
	@Builder.Default
	private Map<String, Object> additionalMetaData = new HashMap<>();
}
