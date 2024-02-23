package com.alpha.omega.security.authentication;

import com.alpha.omega.security.model.UserProfile;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PreAuthenticationPrincipal extends UsernamePasswordAuthenticationToken {

	private String serviceName;
	private DecodedJWT decodedJWT;

	public PreAuthenticationPrincipal(Object principal, Object credentials, String serviceName) {
		super(principal, credentials);
		this.serviceName = serviceName;
	}

	public PreAuthenticationPrincipal(Object principal, Object credentials, DecodedJWT decodedJWT, String serviceName) {
		super(principal, credentials);
		this.decodedJWT = decodedJWT;
		this.serviceName = serviceName;
	}

	public PreAuthenticationPrincipal(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String serviceName) {
		super(principal, credentials, authorities);
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public DecodedJWT getDecodedJWT() {
		return decodedJWT;
	}

	public void setDecodedJWT(DecodedJWT decodedJWT) {
		this.decodedJWT = decodedJWT;
	}

	public String getName() {
		String name = null;
		if (this.getPrincipal() instanceof UserProfile) {
			name = ((UserProfile)this.getPrincipal()).getName();
		} else {
			name = super.getName();
		}
		return name;
	}

}
