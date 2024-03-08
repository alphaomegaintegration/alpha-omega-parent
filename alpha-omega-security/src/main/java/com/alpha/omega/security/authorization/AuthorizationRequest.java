package com.alpha.omega.security.authorization;


import com.alpha.omega.cache.CacheControl;

public class AuthorizationRequest {

	private String correlationId;
	private String userName;
	private String contextId;
	private String authorization;
	private boolean useCache = Boolean.FALSE.booleanValue();
	private CacheControl cacheControl = CacheControl.NONE;


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


	public static Builder newBuilder() {
		return new Builder();
	}

	public boolean isUseCache() {
		return cacheControl.equals(CacheControl.USE) || cacheControl.equals(CacheControl.REFRESH);
	}

	public CacheControl getCacheControl() {
		return cacheControl;
	}



	public static final class Builder {
		private String correlationId;
		private String userName;
		private String contextId;
		private String authorization;
		private CacheControl cacheControl = CacheControl.NONE;

		private Builder() {
		}

		public static Builder anAuthorizationRequest() {
			return new Builder();
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

		public Builder setCacheControl(CacheControl cacheControl) {
			this.cacheControl = cacheControl;
			return this;
		}

		public AuthorizationRequest build() {
			AuthorizationRequest authorizationRequest = new AuthorizationRequest();
			authorizationRequest.correlationId = this.correlationId;
			authorizationRequest.userName = this.userName;
			authorizationRequest.authorization = this.authorization;
			authorizationRequest.contextId = this.contextId;
			authorizationRequest.cacheControl = this.cacheControl;
			return authorizationRequest;
		}
	}
}
