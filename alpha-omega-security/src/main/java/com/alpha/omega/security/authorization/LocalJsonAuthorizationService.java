package com.alpha.omega.security.authorization;

import com.enterprise.pwc.datalabs.caching.DefaultObjectMapperFactory;
import com.enterprise.pwc.datalabs.caching.ObjectMapperFactory;
import com.enterprise.pwc.datalabs.security.context.UserContextPermissions;
import com.enterprise.pwc.datalabs.security.permission.PwcSimpleAuthority;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LocalJsonAuthorizationService implements AuthorizationService {

	private static final Logger logger = LoggerFactory.getLogger(LegacyEngagementAuthorizationService.class);

	private static final String NO_LOCATIONS_MESSAGE =
			"No locations configured for property pwc.authorization-service.context-authorizations-locations. " +
					"Please provide a comma separated list of locations EG classpath:auth.json";

	AuthorizationServiceProperties properties;
	Map<Pair<String, String>, UserContextPermissions> contexts = new TreeMap<>();

	public Map<Pair<String, String>, UserContextPermissions> getContexts() {
		return Collections.unmodifiableMap(contexts);
	}

	public LocalJsonAuthorizationService(AuthorizationServiceProperties properties) {
		this.properties = properties;
		logger.info("LocalJsonAuthorizationService AuthorizationServiceProperties {}",properties);
	}

	@PostConstruct
	public void init() {
		List<String> locations = Arrays.asList(properties.getContextAuthorizationsLocations().split(","));
		if (locations.isEmpty()) {
			throw new BeanCreationException(NO_LOCATIONS_MESSAGE);
		}

		contexts = locations.stream()
				.map(new ConvertToUserContextPermissionsMap())
				.flatMap(userMap -> userMap.values().stream())
				.collect(Collectors.toMap(user -> Pair.of(user.getUsername(), user.getContextId()), (user) -> user));
		logger.info("######### USING LocalJsonAuthorizationService FOR AUTHORIZATIONS ##########");

	}

	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		AuthorizationResponse authorizationResponse = null;

		Set<PwcSimpleAuthority> perms = new HashSet<>();
		if (properties.isAddUserNameToAuthorizations()){
			perms.add(new PwcSimpleAuthority(authorizationRequest.getUserName()));
		}
		Pair<String, String> key = Pair.of(authorizationRequest.getUserName(), authorizationRequest.getContextId());
		UserContextPermissions userContextPermissions = contexts.get(key);
		if (userContextPermissions != null){
			Set<PwcSimpleAuthority> foundPerms = userContextPermissions.getPermissions().stream()
					.map(perm -> new PwcSimpleAuthority(perm))
					.collect(Collectors.toSet());
			perms.addAll(foundPerms);
		}

		if (!perms.isEmpty()){
			authorizationResponse = AuthorizationResponse.newBuilder()
					.setAuthorities(perms)
					.setContextId(authorizationRequest.getContextId())
					.build();
		}

		return  Optional.ofNullable(authorizationResponse);
	}

	static class ConvertToUserContextPermissionsMap implements Function<String, Map<Pair<String, String>, UserContextPermissions>> {

		ResourceLoader resourceLoader = new DefaultResourceLoader();
		ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

		TypeReference userMapRef = new TypeReference<Map<String,List<UserContextPermissions>>>() { };

		@Override
		public Map<Pair<String, String>, UserContextPermissions> apply(String location) {
			Resource resource = resourceLoader.getResource(location);
			try {
				Map<String, List<UserContextPermissions>> allUsersMap = (Map<String, List<UserContextPermissions>>)objectMapper.readValue(resource.getInputStream(),userMapRef);

				Map<Pair<String, String>, UserContextPermissions> userContextPermissionsMap =allUsersMap.get("users").stream()
						//.map(obj -> extractUser(obj))
						.collect(Collectors.toMap(user -> Pair.of(user.getUsername(), user.getContextId()), user -> user));
				return userContextPermissionsMap;

			} catch (IOException e) {
				e.printStackTrace();
				return Collections.emptyMap();
			}
		}

		private UserContextPermissions extractUser(JsonNode jsonNode) {
			return UserContextPermissions.newBuilder()
					.setPermissions(jsonNode.isArray() ?
							ImmutableList.copyOf(jsonNode).stream().map(jsonNode1 -> jsonNode1.asText()).collect(Collectors.toSet())
							: Collections.EMPTY_SET)
					.setContextId(jsonNode.get("contextId").asText())
					.setUsername(jsonNode.get("username").asText())
					.build();
		}

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("LocalJsonAuthorizationService{");
		sb.append("properties=").append(properties);
		sb.append(", contexts=").append(contexts);
		sb.append('}');
		return sb.toString();
	}
}
