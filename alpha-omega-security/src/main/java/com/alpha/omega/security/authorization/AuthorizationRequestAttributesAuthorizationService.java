package com.alpha.omega.security.authorization;

import com.alpha.omega.security.permission.AOSimpleAuthority;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AuthorizationRequestAttributesAuthorizationService implements AuthorizationService{
	@Override
	public Optional<AuthorizationResponse> getAuthorizations(AuthorizationRequest authorizationRequest) {
		Objects.requireNonNull(authorizationRequest.getUserName(), "Username not set. Maybe basic not valid");
		List<AOSimpleAuthority> authorities = new ArrayList<>();
		if (StringUtils.isNotBlank(authorizationRequest.getUserName())){
			authorities.add(new AOSimpleAuthority(authorizationRequest.getUserName()));
		}
		return Optional.of(AuthorizationResponse
				.newBuilder()
				.setAuthorities(authorities)
				.setCorrelationId(authorizationRequest.getCorrelationId())
				.build());
	}
}
