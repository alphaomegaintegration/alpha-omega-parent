package com.alpha.omega.security.authorization;

import com.enterprise.pwc.datalabs.security.PwcSecurityConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pwc.authorization-service")
public class AuthorizationServiceProperties {

	private String workspaceServiceUrl;
	private String workspaceServiceUser;
	private String workspaceServicePassword;
	private String autzServiceUrl;
	private String autzServiceUser;
	private String autzServicePassword;
	private String legacyAutzServiceUrl;
	private boolean autzAvailable;
	private boolean useIdbroker = Boolean.TRUE.booleanValue();
	private boolean useCache = Boolean.FALSE.booleanValue();
	private boolean addUserNameToAuthorizations = Boolean.FALSE.booleanValue();
	private boolean remoteExceptionsAsPwcAuthorizationExceptions = Boolean.TRUE.booleanValue();
	private String contextAuthorizationsLocations;
	private Integer authorizationsCacheExpirationSeconds = PwcSecurityConstants.HOUR_EXPIRATION_SECONDS;
	private Integer securityExpirationSeconds = PwcSecurityConstants.FIVE_EXPIRATION_SECONDS;
	private Long authorizationRequestTimeout = PwcSecurityConstants.FIVE_EXPIRATION_SECONDS.longValue();

	public String getWorkspaceServiceUrl() {
		return workspaceServiceUrl;
	}

	public String getAutzServiceUrl() {
		return autzServiceUrl;
	}

	public boolean isAutzAvailable() {
		return autzAvailable;
	}

	public String getLegacyAutzServiceUrl() {
		return legacyAutzServiceUrl;
	}

	public String getContextAuthorizationsLocations() {
		return contextAuthorizationsLocations;
	}

	public void setWorkspaceServiceUrl(String workspaceServiceUrl) {
		this.workspaceServiceUrl = workspaceServiceUrl;
	}

	public void setAutzServiceUrl(String autzServiceUrl) {
		this.autzServiceUrl = autzServiceUrl;
	}

	public void setLegacyAutzServiceUrl(String legacyAutzServiceUrl) {
		this.legacyAutzServiceUrl = legacyAutzServiceUrl;
	}

	public void setAutzAvailable(boolean autzAvailable) {
		this.autzAvailable = autzAvailable;
	}

	public void setContextAuthorizationsLocations(String contextAuthorizationsLocations) {
		this.contextAuthorizationsLocations = contextAuthorizationsLocations;
	}

	public String getAutzServiceUser() {
		return autzServiceUser;
	}

	public String getAutzServicePassword() {
		return autzServicePassword;
	}

	public void setAutzServiceUser(String autzServiceUser) {
		this.autzServiceUser = autzServiceUser;
	}

	public void setAutzServicePassword(String autzServicePassword) {
		this.autzServicePassword = autzServicePassword;
	}

	public boolean isUseIdbroker() {
		return useIdbroker;
	}

	public void setUseIdbroker(boolean useIdbroker) {
		this.useIdbroker = useIdbroker;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public boolean isAddUserNameToAuthorizations() {
		return addUserNameToAuthorizations;
	}

	public void setAddUserNameToAuthorizations(boolean addUserNameToAuthorizations) {
		this.addUserNameToAuthorizations = addUserNameToAuthorizations;
	}

	public void setSecurityExpirationSeconds(Integer securityExpirationSeconds) {
		this.securityExpirationSeconds = securityExpirationSeconds;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public Integer getAuthorizationsCacheExpirationSeconds() {
		return authorizationsCacheExpirationSeconds;
	}

	public void setAuthorizationsCacheExpirationSeconds(Integer authorizationsCacheExpirationSeconds) {
		this.authorizationsCacheExpirationSeconds = authorizationsCacheExpirationSeconds;
	}

	public Integer getSecurityExpirationSeconds() {
		return securityExpirationSeconds;
	}

	public boolean isRemoteExceptionsAsPwcAuthorizationExceptions() {
		return remoteExceptionsAsPwcAuthorizationExceptions;
	}

	public void setRemoteExceptionsAsPwcAuthorizationExceptions(boolean remoteExceptionsAsPwcAuthorizationExceptions) {
		this.remoteExceptionsAsPwcAuthorizationExceptions = remoteExceptionsAsPwcAuthorizationExceptions;
	}

	public String getWorkspaceServiceUser() {
		return workspaceServiceUser;
	}

	public void setWorkspaceServiceUser(String workspaceServiceUser) {
		this.workspaceServiceUser = workspaceServiceUser;
	}

	public String getWorkspaceServicePassword() {
		return workspaceServicePassword;
	}

	public void setWorkspaceServicePassword(String workspaceServicePassword) {
		this.workspaceServicePassword = workspaceServicePassword;
	}

	public Long getAuthorizationRequestTimeout() {
		return authorizationRequestTimeout;
	}

	public void setAuthorizationRequestTimeout(Long authorizationRequestTimeout) {
		this.authorizationRequestTimeout = authorizationRequestTimeout;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AuthorizationServiceProperties{");
		sb.append("workspaceServiceUrl='").append(workspaceServiceUrl).append('\'');
		sb.append(", workspaceServiceUser='").append(workspaceServiceUser).append('\'');
		sb.append(", autzServiceUrl='").append(autzServiceUrl).append('\'');
		sb.append(", autzServiceUser='").append(autzServiceUser).append('\'');
		sb.append(", legacyAutzServiceUrl='").append(legacyAutzServiceUrl).append('\'');
		sb.append(", autzAvailable=").append(autzAvailable);
		sb.append(", useIdbroker=").append(useIdbroker);
		sb.append(", useCache=").append(useCache);
		sb.append(", addUserNameToAuthorizations=").append(addUserNameToAuthorizations);
		sb.append(", remoteExceptionsAsPwcAuthorizationExceptions=").append(remoteExceptionsAsPwcAuthorizationExceptions);
		sb.append(", contextAuthorizationsLocations='").append(contextAuthorizationsLocations).append('\'');
		sb.append(", authorizationsCacheExpirationSeconds=").append(authorizationsCacheExpirationSeconds);
		sb.append(", securityExpirationSeconds=").append(securityExpirationSeconds);
		sb.append(", authorizationRequestTimeout=").append(authorizationRequestTimeout);
		sb.append('}');
		return sb.toString();
	}

	public static final class Builder {
		private String workspaceServiceUrl;
		private String workspaceServiceUser;
		private String workspaceServicePassword;
		private String autzServiceUrl;
		private String autzServiceUser;
		private String autzServicePassword;
		private String legacyAutzServiceUrl;
		private boolean autzAvailable;
		private boolean useIdbroker = Boolean.TRUE.booleanValue();
		private boolean useCache = Boolean.FALSE.booleanValue();
		private boolean addUserNameToAuthorizations = Boolean.FALSE.booleanValue();
		private boolean remoteExceptionsAsPwcAuthorizationExceptions = Boolean.TRUE.booleanValue();
		private String contextAuthorizationsLocations;
		private Integer authorizationsCacheExpirationSeconds = PwcSecurityConstants.HOUR_EXPIRATION_SECONDS;
		private Integer securityExpirationSeconds = PwcSecurityConstants.FIVE_EXPIRATION_SECONDS;
		private Long authorizationRequestTimeout = PwcSecurityConstants.FIVE_EXPIRATION_SECONDS.longValue();

		private Builder() {
		}

		public static Builder anAuthorizationServiceProperties() {
			return new Builder();
		}

		public Builder setWorkspaceServiceUrl(String workspaceServiceUrl) {
			this.workspaceServiceUrl = workspaceServiceUrl;
			return this;
		}

		public Builder setWorkspaceServiceUser(String workspaceServiceUser) {
			this.workspaceServiceUser = workspaceServiceUser;
			return this;
		}

		public Builder setWorkspaceServicePassword(String workspaceServicePassword) {
			this.workspaceServicePassword = workspaceServicePassword;
			return this;
		}


		public Builder setAutzServiceUrl(String autzServiceUrl) {
			this.autzServiceUrl = autzServiceUrl;
			return this;
		}

		public Builder setAutzServiceUser(String autzServiceUser) {
			this.autzServiceUser = autzServiceUser;
			return this;
		}

		public Builder setAutzServicePassword(String autzServicePassword) {
			this.autzServicePassword = autzServicePassword;
			return this;
		}

		public Builder setLegacyAutzServiceUrl(String legacyAutzServiceUrl) {
			this.legacyAutzServiceUrl = legacyAutzServiceUrl;
			return this;
		}

		public Builder setAutzAvailable(boolean autzAvailable) {
			this.autzAvailable = autzAvailable;
			return this;
		}

		public Builder setUseIdbroker(boolean useIdbroker) {
			this.useIdbroker = useIdbroker;
			return this;
		}

		public Builder setUseCache(boolean useCache) {
			this.useCache = useCache;
			return this;
		}

		public Builder setContextAuthorizationsLocations(String contextAuthorizationsLocations) {
			this.contextAuthorizationsLocations = contextAuthorizationsLocations;
			return this;
		}

		public Builder setAuthorizationsCacheExpirationSeconds(Integer authorizationsCacheExpirationSeconds) {
			this.authorizationsCacheExpirationSeconds = authorizationsCacheExpirationSeconds;
			return this;
		}

		public Builder setSecurityExpirationSeconds(Integer securityExpirationSeconds) {
			this.securityExpirationSeconds = securityExpirationSeconds;
			return this;
		}

		public Builder setAddUserNameToAuthorizations(boolean addUserNameToAuthorizations) {
			this.addUserNameToAuthorizations = addUserNameToAuthorizations;
			return this;
		}

		public Builder setRemoteExceptionsAsPwcAuthorizationExceptions(boolean remoteExceptionsAsPwcAuthorizationExceptions) {
			this.remoteExceptionsAsPwcAuthorizationExceptions = remoteExceptionsAsPwcAuthorizationExceptions;
			return this;
		}

		public Builder setAuthorizationRequestTimeout(Long authorizationRequestTimeout) {
			this.authorizationRequestTimeout = authorizationRequestTimeout;
			return this;
		}

		public AuthorizationServiceProperties build() {
			AuthorizationServiceProperties authorizationServiceProperties = new AuthorizationServiceProperties();
			authorizationServiceProperties.setWorkspaceServiceUrl(workspaceServiceUrl);
			authorizationServiceProperties.setAutzServiceUrl(autzServiceUrl);
			authorizationServiceProperties.setAutzServiceUser(autzServiceUser);
			authorizationServiceProperties.setAutzServicePassword(autzServicePassword);
			authorizationServiceProperties.setLegacyAutzServiceUrl(legacyAutzServiceUrl);
			authorizationServiceProperties.setAutzAvailable(autzAvailable);
			authorizationServiceProperties.setUseIdbroker(useIdbroker);
			authorizationServiceProperties.setUseCache(useCache);
			authorizationServiceProperties.setAddUserNameToAuthorizations(addUserNameToAuthorizations);
			authorizationServiceProperties.setContextAuthorizationsLocations(contextAuthorizationsLocations);
			authorizationServiceProperties.setAuthorizationsCacheExpirationSeconds(authorizationsCacheExpirationSeconds);
			authorizationServiceProperties.securityExpirationSeconds = this.securityExpirationSeconds;
			authorizationServiceProperties.setRemoteExceptionsAsPwcAuthorizationExceptions(remoteExceptionsAsPwcAuthorizationExceptions);
			authorizationServiceProperties.setWorkspaceServiceUser(workspaceServiceUser);
			authorizationServiceProperties.setWorkspaceServicePassword(workspaceServicePassword);
			authorizationServiceProperties.setAuthorizationRequestTimeout(authorizationRequestTimeout);
			return authorizationServiceProperties;
		}
	}
}
