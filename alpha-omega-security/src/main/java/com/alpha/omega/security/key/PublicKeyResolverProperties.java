package com.alpha.omega.security.key;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ao.public-key-resolver")
public class PublicKeyResolverProperties {

	private String idpMappingPath;
	private Boolean internal = Boolean.TRUE;
	private String publicKeyUrl;

	public String getIdpMappingPath() {
		return idpMappingPath;
	}

	public void setIdpMappingPath(String idpMappingPath) {
		this.idpMappingPath = idpMappingPath;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	public String getPublicKeyUrl() {
		return publicKeyUrl;
	}

	public void setPublicKeyUrl(String publicKeyUrl) {
		this.publicKeyUrl = publicKeyUrl;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PublicKeyResolverProperties{");
		sb.append("idpMappingPath='").append(idpMappingPath).append('\'');
		sb.append(", internal=").append(internal);
		sb.append(", publicKeyUrl='").append(publicKeyUrl).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
