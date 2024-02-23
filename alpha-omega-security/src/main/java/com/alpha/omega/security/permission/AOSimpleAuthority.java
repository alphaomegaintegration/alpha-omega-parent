package com.alpha.omega.security.permission;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public final class AOSimpleAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 562L;
	private final String role;

	public AOSimpleAuthority(String role) {
		Assert.hasText(role, "A granted authority textual representation is required");
		this.role = role.toLowerCase();
	}

	public String getAuthority() {
		return this.role;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			return obj instanceof AOSimpleAuthority ? this.role.equals(((AOSimpleAuthority)obj).role) : false;
		}
	}

	public int hashCode() {
		return this.role.hashCode();
	}

	public String toString() {
		return this.role;
	}
}
