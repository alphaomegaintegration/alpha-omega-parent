package com.alpha.omega.security.authorization;

import com.enterprise.pwc.datalabs.caching.CacheControl;

public class AuthorizationRequest {

	private String serviceName;
	private String correlationId;
	private String userName;
	private String contextId;
	private String authorization;
	private String engagementId;
	private String workspaceId;
	private boolean useCache = Boolean.FALSE.booleanValue();
	private CacheControl cacheControl = CacheControl.NONE;

	public String getServiceName() {
		return serviceName;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public String getUserName() {
		return userName;
	}

	public String getContextId() {
		return contextId;
	}

	public String getAuthorization() {
		return authorization;
	}

	public String getEngagementId() {
		return engagementId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public boolean isUseCache() {
		return cacheControl.equals(CacheControl.USE) || cacheControl.equals(CacheControl.REFRESH);
	}

	public CacheControl getCacheControl() {
		return cacheControl;
	}

	public String getWorkspaceId() {
		return workspaceId;
	}


	public static final class Builder {
		private String serviceName;
		private String correlationId;
		private String userName;
		private String contextId;
		private String authorization;
		private String engagementId;
		private String workspaceId;
		private CacheControl cacheControl = CacheControl.NONE;

		private Builder() {
		}

		public static Builder anAuthorizationRequest() {
			return new Builder();
		}

		public Builder setServiceName(String serviceName) {
			this.serviceName = serviceName;
			return this;
		}

		public Builder setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
			return this;
		}

		public Builder setUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder setContextId(String contextId) {
			this.contextId = contextId;
			return this;
		}

		public Builder setAuthorization(String authorization) {
			this.authorization = authorization;
			return this;
		}

		public Builder setEngagementId(String engagementId) {
			this.engagementId = engagementId;
			return this;
		}

		public Builder setWorkspaceId(String workspaceId) {
			this.workspaceId = workspaceId;
			return this;
		}

		public Builder setCacheControl(CacheControl cacheControl) {
			this.cacheControl = cacheControl;
			return this;
		}

		public AuthorizationRequest build() {
			AuthorizationRequest authorizationRequest = new AuthorizationRequest();
			authorizationRequest.correlationId = this.correlationId;
			authorizationRequest.userName = this.userName;
			authorizationRequest.engagementId = this.engagementId;
			authorizationRequest.authorization = this.authorization;
			authorizationRequest.contextId = this.contextId;
			authorizationRequest.serviceName = this.serviceName;
			authorizationRequest.workspaceId = this.workspaceId;
			authorizationRequest.cacheControl = this.cacheControl;
			return authorizationRequest;
		}
	}
}
