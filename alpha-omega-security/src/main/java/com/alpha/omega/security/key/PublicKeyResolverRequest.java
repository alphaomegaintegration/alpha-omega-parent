package com.alpha.omega.security.key;

public class PublicKeyResolverRequest {

	private String publicKeyId;
	private String clientId;

	public String getPublicKeyId() {
		return publicKeyId;
	}

	public void setPublicKeyId(String publicKeyId) {
		this.publicKeyId = publicKeyId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PublicKeyResolverRequest{");
		sb.append("publicKeyId='").append(publicKeyId).append('\'');
		sb.append(", clientId='").append(clientId).append('\'');
		sb.append('}');
		return sb.toString();
	}

	public static final class Builder {
		private String publicKeyId;
		private String clientId;

		private Builder() {
		}

		public static Builder aPublicKeyResolverRequest() {
			return new Builder();
		}

		public Builder setPublicKeyId(String publicKeyId) {
			this.publicKeyId = publicKeyId;
			return this;
		}

		public Builder setClientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public PublicKeyResolverRequest build() {
			PublicKeyResolverRequest publicKeyResolverRequest = new PublicKeyResolverRequest();
			publicKeyResolverRequest.setPublicKeyId(publicKeyId);
			publicKeyResolverRequest.setClientId(clientId);
			return publicKeyResolverRequest;
		}
	}
}
