package com.alpha.omega.security.context;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserContextPermissions {
	protected String username;
	protected String contextId;
	protected Set<String> permissions = new HashSet<>();

	public String getUsername() {
		return username;
	}

	public String getContextId() {
		return contextId;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UserContextPermissions{");
		sb.append("userName='").append(username).append('\'');
		sb.append(", contextId='").append(contextId).append('\'');
		sb.append(", permissions=").append(permissions);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UserContextPermissions that = (UserContextPermissions) o;
		return username.equals(that.username) && contextId.equals(that.contextId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, contextId);
	}

	public static Builder newBuilder(){
		return new Builder();
	}


	public static final class Builder {
		String username;
		String contextId;
		Set<String> permissions = new HashSet<>();

		private Builder() {
		}

		public static Builder anUserContextPermissions() {
			return new Builder();
		}

		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder setContextId(String contextId) {
			this.contextId = contextId;
			return this;
		}

		public Builder setPermissions(Set<String> permissions) {
			this.permissions = permissions;
			return this;
		}

		public UserContextPermissions build() {
			UserContextPermissions userContextPermissions = new UserContextPermissions();
			userContextPermissions.contextId = this.contextId;
			userContextPermissions.username = this.username;
			userContextPermissions.permissions = this.permissions;
			return userContextPermissions;
		}
	}

}
