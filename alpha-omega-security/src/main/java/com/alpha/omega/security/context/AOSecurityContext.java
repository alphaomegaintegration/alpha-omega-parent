package com.alpha.omega.security.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AOSecurityContext {
	String name;
	Set<String> allPermissions = new HashSet<>();
	Map<String, ContextRole> roleMappings = new HashMap<>();

	public String getName() {
		return name;
	}

	public Set<String> getAllPermissions() {
		return allPermissions;
	}

	public Map<String, ContextRole> getRoleMappings() {
		return roleMappings;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PwcSecurityContext{");
		sb.append("name='").append(name).append('\'');
		sb.append(", allPermissions=").append(allPermissions);
		sb.append(", roleMappings=").append(roleMappings);
		sb.append('}');
		return sb.toString();
	}

	public static final class PwcSecurityContextBuilder {
		String name;
		Set<String> allPermissions = new HashSet<>();
		Map<String, ContextRole> roleMappings = new HashMap<>();

		private PwcSecurityContextBuilder() {
		}

		public static PwcSecurityContextBuilder aPwcSecurityContext() {
			return new PwcSecurityContextBuilder();
		}

		public PwcSecurityContextBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public PwcSecurityContextBuilder setAllPermissions(Set<String> allPermissions) {
			this.allPermissions = allPermissions;
			return this;
		}

		public PwcSecurityContextBuilder setRoleMappings(Map<String, ContextRole> roleMappings) {
			this.roleMappings = roleMappings;
			return this;
		}

		public AOSecurityContext build() {
			AOSecurityContext AOSecurityContext = new AOSecurityContext();
			AOSecurityContext.allPermissions = this.allPermissions;
			AOSecurityContext.name = this.name;
			AOSecurityContext.roleMappings = this.roleMappings;
			return AOSecurityContext;
		}
	}
}
