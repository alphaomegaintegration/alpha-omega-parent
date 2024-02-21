package com.alpha.omega.security.authentication;

import com.enterprise.pwc.datalabs.security.PwcSecurityConstants;
import com.enterprise.pwc.datalabs.security.permission.PwcSimpleAuthority;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pwc.base.model.UserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import javax.security.auth.Subject;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static com.enterprise.pwc.datalabs.security.PwcSecurityConstants.AUTHORITIES_CANNOT_BE_NULL_OR_EMPTY;
import static com.enterprise.pwc.datalabs.security.PwcSecurityConstants.USER_PROFILE_CANNOT_BE_NULL;
//@JsonDeserialize(using = UserProfileAuthentication.UserProfileAuthenticationDeserializer.class)
public class UserProfileAuthentication implements Authentication {

	private UserProfile userProfile;
	private Collection<? extends GrantedAuthority> authorities;


	@JsonCreator
	public UserProfileAuthentication(@JsonProperty("userProfile") UserProfile userProfile,@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(userProfile, USER_PROFILE_CANNOT_BE_NULL);
		Assert.notNull(authorities, AUTHORITIES_CANNOT_BE_NULL_OR_EMPTY);
		Assert.notEmpty(authorities, AUTHORITIES_CANNOT_BE_NULL_OR_EMPTY);
		this.userProfile = userProfile;
		this.authorities = authorities;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("UserProfileAuthentication{");
		sb.append("userProfile=").append(userProfile);
		sb.append(", authorities=").append(authorities);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return userProfile.getAccessToken();
	}

	@Override
	public Object getDetails() {
		return userProfile;
	}

	@Override
	public Object getPrincipal() {

		return userProfile.getName();
	}

	@Override
	public boolean isAuthenticated() {
		return Boolean.TRUE;
	}

	@Override
	public void setAuthenticated(boolean b) throws IllegalArgumentException {
		throw new IllegalArgumentException(PwcSecurityConstants.CANNOT_SET_AUTHENTICATION);
	}

	@Override
	public String getName() {
		return userProfile.getName();
	}

	@Override
	public boolean implies(Subject subject) {
		return Authentication.super.implies(subject);
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public boolean containsAuthority(String permission){
		return getAuthorities().contains(new PwcSimpleAuthority(permission));
	}

	public boolean containsAuthorities(List<String> permissions){
		Set<String> perms = permissions.stream().map(str -> str.toLowerCase(Locale.ROOT)).collect(Collectors.toSet());
		return getAuthorities().stream().map(role -> role.getAuthority().toLowerCase(Locale.ROOT)).anyMatch(perms::contains);
	}

	public static class UserProfileAuthenticationDeserializer extends StdDeserializer<UserProfileAuthentication> {

		protected UserProfileAuthenticationDeserializer(Class<?> vc) {
			super(vc);
		}

		protected UserProfileAuthenticationDeserializer(JavaType valueType) {
			super(valueType);
		}

		protected UserProfileAuthenticationDeserializer(StdDeserializer<?> src) {
			super(src);
		}

		/*
		{"userProfile":{"name":"test-user","firstName":null,"lastName":null,"accessToken":null,"refreshToken":null,"countryCode":null,"accountType":null,"serviceName":"autz-service","identityProvider":null,"contextId":null,"engagementId":null,"workspaceId":null,"correlationId":null,"email":null,"additionalMetaData":{}},"authorities":[{"authority":"dp-idbroker"},{"authority":"autz-service"},{"authority":"do_something"}],"name":"test-user","credentials":null,"details":{"name":"test-user","firstName":null,"lastName":null,"accessToken":null,"refreshToken":null,"countryCode":null,"accountType":null,"serviceName":"autz-service","identityProvider":null,"contextId":null,"engagementId":null,"workspaceId":null,"correlationId":null,"email":null,"additionalMetaData":{}},"principal":"test-user","authenticated":true}
		 */

		@Override
		public UserProfileAuthentication deserialize(JsonParser jsonParser, DeserializationContext dCtx) throws IOException, JacksonException {

			/*
			JsonNode node = jsonParser.getCodec().readTree(jsonParser);
			UserProfile userProfile = dCtx.readValue(node.get("userProfile").traverse(), UserProfile.class);
			node.get("authorities").withArray()
			int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

			return new Item(id, itemName, new User(userId, null));

			 */
			return null;
		}
	}
}
