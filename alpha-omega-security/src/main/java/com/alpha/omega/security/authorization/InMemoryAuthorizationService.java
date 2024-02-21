package com.alpha.omega.security.authorization;

import com.alpha.omega.security.permission.AOSimpleAuthority;
import com.alpha.omega.security.userdetails.UCPUserDetails;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryAuthorizationService implements AuthorizationService {

	private static final Logger logger = LoggerFactory.getLogger(InMemoryAuthorizationService.class);

	private static final String NO_LOCATIONS_MESSAGE =
			"No locations configured for property ao.authorization-service.context-authorizations-locations. " +
					"Please provide a comma separated list of locations EG classpath:auth.json";

	AuthorizationServiceProperties properties;
	Map<Pair<String, String>, UCPUserDetails> contexts = new TreeMap<>();
	Collection<UCPUserDetails> userDetails;

	public Map<Pair<String, String>, UCPUserDetails> getContexts() {
		return Collections.unmodifiableMap(contexts);
	}

	public InMemoryAuthorizationService(AuthorizationServiceProperties properties, Collection<UCPUserDetails> userDetails) {
		this.properties = properties;
		this.userDetails = userDetails;
	}

	public InMemoryAuthorizationService(Collection<UCPUserDetails> userDetails) {
		this.userDetails = userDetails;
	}

	@PostConstruct
	public void init() {


		contexts = userDetails.stream()
			//	.peek(user -> logger.info("user -> {}",user))
				.collect(Collectors.toMap(user -> Pair.of(user.getUsername(), user.getContextId()), (user) -> user));

	}

	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		AuthorizationResponse authorizationResponse = null;
		Pair<String, String> key = Pair.of(authorizationRequest.getUserName(), authorizationRequest.getContextId());
		UCPUserDetails userDetails = contexts.get(key);
		if (userDetails != null){
			Set<AOSimpleAuthority> perms = userDetails.getPermissions().stream()
					.map(perm -> new AOSimpleAuthority(perm))
					.collect(Collectors.toSet());

			authorizationResponse = AuthorizationResponse.newBuilder()
					.setAuthorities(perms)
					.setContextId(authorizationRequest.getContextId())
					.build();
		}

		return  Optional.ofNullable(authorizationResponse);
	}

}
