package com.alpha.omega.security.context;

import java.util.HashSet;
import java.util.Set;

public class ContextRole {
	String roleName;
	Set<String> permissions = new HashSet<>();

	public String getRoleName() {
		return roleName;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public ContextRole(String roleName, Set<String> permissions) {
		this.roleName = roleName;
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ContextRole{");
		sb.append("roleName='").append(roleName).append('\'');
		sb.append(", permissions=").append(permissions);
		sb.append('}');
		return sb.toString();
	}
}
