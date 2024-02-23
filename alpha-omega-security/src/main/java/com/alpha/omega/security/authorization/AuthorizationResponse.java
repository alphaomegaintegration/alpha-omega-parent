package com.alpha.omega.security.authorization;

import com.alpha.omega.security.permission.AOSimpleAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public class AuthorizationResponse {
	private String contextId;
	private String correlationId;
	private Collection<String> errorMessages = Collections.EMPTY_SET;
	private Collection<AOSimpleAuthority> authorities = Collections.EMPTY_SET;
	private Long elapsedProcessing;
	private String elapsedProcessingStr;

	public String getContextId() {
		return contextId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public Collection<AOSimpleAuthority> getAuthorities() {
		return authorities;
	}

	public Collection<String> getErrorMessages() {
		return errorMessages;
	}

	public Long getElapsedProcessing() {
		return elapsedProcessing;
	}

	public String getElapsedProcessingStr() {
		return elapsedProcessingStr;
	}

	public static final AuthorizationResponse fromElapsed(AuthorizationResponse authorizationResponse, Long elapsed, String elapsedStr){
		return new Builder()
				.setErrorMessages(authorizationResponse.getErrorMessages())
				.setCorrelationId(authorizationResponse.getCorrelationId())
				.setAuthorities(authorizationResponse.getAuthorities())
				.setContextId(authorizationResponse.getContextId())
				.setElapsedProcessing(elapsed)
				.setElapsedProcessingStr(elapsedStr)
				.build();
	}

	public static final AuthorizationResponse fromElapsed(AuthorizationResponse authorizationResponse, Supplier<Long> elapsed, Supplier<String> elapsedStr){
		return new Builder()
				.setErrorMessages(authorizationResponse.getErrorMessages())
				.setCorrelationId(authorizationResponse.getCorrelationId())
				.setAuthorities(authorizationResponse.getAuthorities())
				.setContextId(authorizationResponse.getContextId())
				.setElapsedProcessing(elapsed.get())
				.setElapsedProcessingStr(elapsedStr.get())
				.build();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AuthorizationResponse{");
		sb.append("contextId='").append(contextId).append('\'');
		sb.append(", correlationId='").append(correlationId).append('\'');
		sb.append(", errorMessages=").append(errorMessages);
		sb.append(", authorities=").append(authorities);
		sb.append(", elapsedProcessing=").append(elapsedProcessing);
		sb.append(", elapsedProcessingStr='").append(elapsedProcessingStr).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public static final class Builder {
		private String contextId;
		private String correlationId;
		private Collection<String> errorMessages = Collections.EMPTY_SET;
		private Collection<AOSimpleAuthority> authorities = Collections.EMPTY_SET;
		private Long elapsedProcessing;
		private String elapsedProcessingStr;

		private Builder() {
		}

		public static Builder anAuthorizationResponse() {
			return new Builder();
		}

		public Builder setContextId(String contextId) {
			this.contextId = contextId;
			return this;
		}

		public Builder setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
			return this;
		}

		public Builder setErrorMessages(Collection<String> errorMessages) {
			this.errorMessages = errorMessages;
			return this;
		}

		public Builder setAuthorities(Collection<AOSimpleAuthority> authorities) {
			this.authorities = authorities;
			return this;
		}

		public Builder setElapsedProcessing(Long elapsedProcessing) {
			this.elapsedProcessing = elapsedProcessing;
			return this;
		}

		public Builder setElapsedProcessingStr(String elapsedProcessingStr) {
			this.elapsedProcessingStr = elapsedProcessingStr;
			return this;
		}

		public AuthorizationResponse build() {
			AuthorizationResponse authorizationResponse = new AuthorizationResponse();
			authorizationResponse.contextId = this.contextId;
			authorizationResponse.authorities = this.authorities;
			authorizationResponse.correlationId = this.correlationId;
			authorizationResponse.errorMessages = this.errorMessages;
			authorizationResponse.elapsedProcessingStr = this.elapsedProcessingStr;
			authorizationResponse.elapsedProcessing = this.elapsedProcessing;
			return authorizationResponse;
		}
	}
}
