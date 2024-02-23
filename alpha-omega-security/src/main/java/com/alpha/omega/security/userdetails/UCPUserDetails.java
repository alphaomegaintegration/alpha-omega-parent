package com.alpha.omega.security.userdetails;

import com.alpha.omega.security.context.UserContextPermissions;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UCPUserDetails extends UserContextPermissions implements UserDetails {

	Collection<? extends GrantedAuthority> authorities;
	String password;
	boolean accountNonExpired = Boolean.TRUE.booleanValue();
	boolean accountNonLocked = Boolean.TRUE.booleanValue();
	boolean credentialsNonExpired = Boolean.TRUE.booleanValue();
	boolean enabled = Boolean.TRUE.booleanValue();

	public static UCPUserDetails of(String username, String password, Set<String> permissions){
		return new UCPUserDetailsBuilder()
				.setUsername(username)
				.setPassword(password)
				.setPermissions(permissions)
				.build();
	}

	public static UCPUserDetails of(String username, String password, String contextId, Set<String> permissions){
		return new UCPUserDetailsBuilder()
				.setUsername(username)
				.setContextId(contextId)
				.setPassword(password)
				.setPermissions(permissions)
				.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public static UCPUserDetailsBuilder anUCPUserDetails() {
		return new UCPUserDetailsBuilder();
	}

	public static final class UCPUserDetailsBuilder {
		protected String username;
		protected String contextId;
		protected Set<String> permissions = new HashSet<>();
		Collection<? extends GrantedAuthority> authorities;
		String password;
		boolean accountNonExpired = Boolean.TRUE.booleanValue();
		boolean accountNonLocked = Boolean.TRUE.booleanValue();
		boolean credentialsNonExpired = Boolean.TRUE.booleanValue();
		boolean enabled = Boolean.TRUE.booleanValue();

		private UCPUserDetailsBuilder() {
		}

		public static UCPUserDetailsBuilder anUCPUserDetails() {
			return new UCPUserDetailsBuilder();
		}

		public UCPUserDetailsBuilder setUsername(String username) {
			this.username = username;
			return this;
		}

		public UCPUserDetailsBuilder setContextId(String contextId) {
			this.contextId = contextId;
			return this;
		}

		public UCPUserDetailsBuilder setPermissions(Set<String> permissions) {
			this.permissions = permissions;
			return this;
		}

		public UCPUserDetailsBuilder setAuthorities(Collection<? extends GrantedAuthority> authorities) {
			this.authorities = authorities;
			return this;
		}

		public UCPUserDetailsBuilder setPassword(String password) {
			this.password = password;
			return this;
		}

		public UCPUserDetailsBuilder setAccountNonExpired(boolean accountNonExpired) {
			this.accountNonExpired = accountNonExpired;
			return this;
		}

		public UCPUserDetailsBuilder setAccountNonLocked(boolean accountNonLocked) {
			this.accountNonLocked = accountNonLocked;
			return this;
		}

		public UCPUserDetailsBuilder setCredentialsNonExpired(boolean credentialsNonExpired) {
			this.credentialsNonExpired = credentialsNonExpired;
			return this;
		}

		public UCPUserDetailsBuilder setEnabled(boolean enabled) {
			this.enabled = enabled;
			return this;
		}

		public UCPUserDetails build() {
			Assert.notEmpty(permissions,"Permissions cannot be empty!");
			UCPUserDetails uCPUserDetails = new UCPUserDetails();
			uCPUserDetails.credentialsNonExpired = this.credentialsNonExpired;
			uCPUserDetails.authorities = this.authorities;
			uCPUserDetails.password = this.password;
			uCPUserDetails.accountNonLocked = this.accountNonLocked;
			uCPUserDetails.permissions = this.permissions;
			uCPUserDetails.accountNonExpired = this.accountNonExpired;
			uCPUserDetails.contextId = this.contextId;
			uCPUserDetails.username = this.username;
			uCPUserDetails.enabled = this.enabled;
			uCPUserDetails.authorities = permissions.stream().map(perm -> new SimpleGrantedAuthority(perm)).collect(Collectors.toSet());
			return uCPUserDetails;
		}
	}
}
