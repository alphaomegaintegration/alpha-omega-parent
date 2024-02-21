package com.alpha.omega.security.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ao.security")
public class AOSecurityProperties {

	String excludeUris = AOSecurityConstants.EMPTY_STR;
	String identityUris = AOSecurityConstants.EMPTY_STR;;
	String engagementUris = AOSecurityConstants.EMPTY_STR;
	boolean maskSensitive = Boolean.TRUE.booleanValue();
	boolean useDefaultContextId = Boolean.TRUE.booleanValue();
	String defaultContextId = AOSecurityConstants.DEFAULT_CONTEXT_ID;
	boolean validateContextId = Boolean.FALSE.booleanValue();
	String validContextIds = AOSecurityConstants.EMPTY_STR;
	boolean useIdbrokerForAuthorizations = Boolean.FALSE.booleanValue();
	boolean allowLogging = Boolean.FALSE.booleanValue();
	String idbrokerServiceNameForAuthorization = AOSecurityConstants.EMPTY_STR;
	boolean includeServiceNameAsHeaderForAuthorizations = Boolean.FALSE.booleanValue();
	String commitId;

	public boolean isMaskSensitive() {
		return maskSensitive;
	}

	public void setMaskSensitive(boolean maskSensitive) {
		this.maskSensitive = maskSensitive;
	}

	public String getExcludeUris() {
		return excludeUris;
	}

	public void setExcludeUris(String excludeUris) {
		this.excludeUris = excludeUris;
	}

	public String getIdentityUris() {
		return identityUris;
	}

	public void setIdentityUris(String identityUris) {
		this.identityUris = identityUris;
	}

	public String getEngagementUris() {
		return engagementUris;
	}

	public void setEngagementUris(String engagementUris) {
		this.engagementUris = engagementUris;
	}

	public boolean isUseDefaultContextId() {
		return useDefaultContextId;
	}

	public void setUseDefaultContextId(boolean useDefaultContextId) {
		this.useDefaultContextId = useDefaultContextId;
	}

	public String getDefaultContextId() {
		return defaultContextId;
	}

	public void setDefaultContextId(String defaultContextId) {
		this.defaultContextId = defaultContextId;
	}

	public boolean isUseIdbrokerForAuthorizations() {
		return useIdbrokerForAuthorizations;
	}

	public void setUseIdbrokerForAuthorizations(boolean useIdbrokerForAuthorizations) {
		this.useIdbrokerForAuthorizations = useIdbrokerForAuthorizations;
	}

	public String getIdbrokerServiceNameForAuthorization() {
		return idbrokerServiceNameForAuthorization;
	}

	public void setIdbrokerServiceNameForAuthorization(String idbrokerServiceNameForAuthorization) {
		this.idbrokerServiceNameForAuthorization = idbrokerServiceNameForAuthorization;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public boolean isValidateContextId() {
		return validateContextId;
	}

	public void setValidateContextId(boolean validateContextId) {
		this.validateContextId = validateContextId;
	}

	public String getValidContextIds() {
		return validContextIds;
	}

	public void setValidContextIds(String validContextIds) {
		this.validContextIds = validContextIds;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PwcSecurityProperties{");
		sb.append("excludeUris='").append(excludeUris).append('\'');
		sb.append(", identityUris='").append(identityUris).append('\'');
		sb.append(", engagementUris='").append(engagementUris).append('\'');
		sb.append(", maskSensitive=").append(maskSensitive);
		sb.append(", useDefaultContextId=").append(useDefaultContextId);
		sb.append(", defaultContextId='").append(defaultContextId).append('\'');
		sb.append(", validateContextId=").append(validateContextId);
		sb.append(", validContextIds='").append(validContextIds).append('\'');
		sb.append(", useIdbrokerForAuthorizations=").append(useIdbrokerForAuthorizations);
		sb.append(", idbrokerServiceNameForAuthorization='").append(idbrokerServiceNameForAuthorization).append('\'');
		sb.append(", includeServiceNameAsHeaderForAuthorizations=").append(includeServiceNameAsHeaderForAuthorizations);
		sb.append(", commitId='").append(commitId).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public boolean isIncludeServiceNameAsHeaderForAuthorizations() {
		return includeServiceNameAsHeaderForAuthorizations;
	}

	public void setIncludeServiceNameAsHeaderForAuthorizations(boolean includeServiceNameAsHeaderForAuthorizations) {
		this.includeServiceNameAsHeaderForAuthorizations = includeServiceNameAsHeaderForAuthorizations;
	}

}
