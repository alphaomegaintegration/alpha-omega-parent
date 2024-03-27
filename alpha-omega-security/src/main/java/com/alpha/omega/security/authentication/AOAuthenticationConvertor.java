package com.alpha.omega.security.authentication;

import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.security.exception.AOAuthenticationException;
import com.alpha.omega.security.model.UserProfile;
import com.alpha.omega.security.token.AOClaims;
import com.alpha.omega.security.token.TokenIssuerClaimsMapperService;
import com.alpha.omega.security.utils.AOSecurityProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alpha.omega.core.Constants.*;
import static com.alpha.omega.security.SecurityConstants.SERVICE_NAME;
import static com.alpha.omega.security.token.AOClaims.*;
import static com.alpha.omega.security.utils.AOSecurityUtils.parseBasicAuthString;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AOAuthenticationConvertor implements AuthenticationConverter {

	public static final String NO_ISS_CLAIM = "No iss claim!";
	private static Logger logger = LogManager.getLogger(AOAuthenticationConvertor.class);
	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

	private Charset credentialsCharset = StandardCharsets.UTF_8;
	private AOSecurityProperties aoSecurityProperties;

	private HttpServletRequestToRequestMap httpServletRequestToRequestMap = new HttpServletRequestToRequestMap();
	private TokenIssuerClaimsMapperService claimsMapperService;


	protected static final List<String> CONVERSION_HEADERS = Arrays.asList(PRINCIPAL, IDENTITY_PROVIDER, IDENTITY_PROVIDER, CORRELATION_ID, CONTEXT_ID, HttpHeaders.AUTHORIZATION);

	@Override
	public Authentication convert(HttpServletRequest request) {

		Map<String, Object> requestMap = httpServletRequestToRequestMap.apply(request);
		PreAuthenticationPrincipal principalAuth = requestMapToPreAuthenticationPrincipal(aoSecurityProperties, objectMapper, claimsMapperService).apply(requestMap);
		((UserProfile) principalAuth.getPrincipal()).setAdditionalMetaData(convertRequestHeadersToMap(request));
		return principalAuth;
	}

	private static String extractFromBasicAuthHeader(String authorization) {
		try {
			return parseBasicAuthString(authorization).getT1();
		} catch (Exception e) {
			logger.warn("Could not extract guid ", e);
			return null;
		}
	}

	protected static Map<String, Object> convertRequestHeadersToMap(final HttpServletRequest request) {
		Map<String, Object> headers = Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(name -> name, name -> request.getHeader(name)));
		// TODO Do we need all these headers or not
		headers.remove("authorization");
		headers.remove("Authorization");
		headers.remove("host");
		return headers;
	}

	static Optional<JsonNode> getSubject(String subjectHeader, ObjectMapper objectMapper) {
		Optional<JsonNode> jsonNodeOptional = Optional.empty();
		if (!StringUtils.isBlank(subjectHeader)) {
			JsonNode jsonNode;
			try {
				byte[] subjectObj = Base64.getDecoder().decode(subjectHeader);
				jsonNode = objectMapper.readValue(subjectObj, JsonNode.class);
				return Optional.ofNullable(jsonNode);
			} catch (Exception e) {
				logger.error("getSubject Exception while parsing", e);
			}
		}
		return jsonNodeOptional;

	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		if (objectMapper != null) {
			this.objectMapper = objectMapper;
		} else {
			logger.debug("Null object mapper passed into setter, using default object mapper");
		}
	}

	public static Function<Map<String, Object>, PreAuthenticationPrincipal> requestMapToPreAuthenticationPrincipal(final AOSecurityProperties aoSecurityProperties,
																												   ObjectMapper objectMapper,
																												   TokenIssuerClaimsMapperService claimsMapperService) {

		return (Map<String, Object> requestMap) -> {

			final String principal = (String) requestMap.get(PRINCIPAL);
			final String authorization = (String) requestMap.get(HttpHeaders.AUTHORIZATION);
			final String serviceName = (String) requestMap.get(SERVICE_NAME);
			final String identityProvider = (String) requestMap.get(IDENTITY_PROVIDER);
			final String refreshToken = (String) requestMap.get(REFRESH_TOKEN_HEADER);
			final String correlationId = (String) requestMap.get(CORRELATION_ID);
			String contextId = (String) requestMap.get(CONTEXT_ID);

			if (StringUtils.isBlank(contextId) && aoSecurityProperties.isUseDefaultContextId()) {
				contextId = aoSecurityProperties.getDefaultContextId();
			}

			UserProfile userProfile = UserProfile.builder()
					.serviceName(serviceName)
					.refreshToken(refreshToken)
					.accessToken(authorization)
					.identityProvider(identityProvider)
					//.additionalMetaData(convertRequestHeadersToMap(request))
					.contextId(contextId)
					.correlationId(correlationId)
					.build();

			String completeAuthHeader = null;
			if (StringUtils.isBlank(authorization)) {
				throw new AOAuthenticationException("No authorization header!");
			}

			DecodedJWT decodedJWT = null;

			if (authorization.startsWith(BEARER)) {
				String[] tokenSplit = authorization.split(BEARER);
				completeAuthHeader = tokenSplit[tokenSplit.length - 1];
				decodedJWT = JWT.decode(completeAuthHeader);
				completeAuthHeader = authorization;
				userProfile = fromDecodedJwt(decodedJWT, claimsMapperService, userProfile);
			} else {
				completeAuthHeader = authorization;
				userProfile = fromBasicAuthHeader(authorization, userProfile);
			}

			userProfile = updateUserProfile(principal, userProfile, objectMapper);

			logger.trace("userProfile [{}] in aoAuthenticationConvertor", userProfile);

			PreAuthenticationPrincipal authPrincipal = decodedJWT != null ? new PreAuthenticationPrincipal(userProfile, completeAuthHeader, decodedJWT, serviceName)
					: new PreAuthenticationPrincipal(userProfile, completeAuthHeader, serviceName);

			return authPrincipal;
		};
	}

	private static UserProfile fromBasicAuthHeader(String authorization, UserProfile userProfileP) {
		try {

			String guid = parseBasicAuthString(authorization).getT1();
			return userProfileP.toBuilder().name(guid).build();
		} catch (Exception e) {
			logger.warn("Could not extract guid ", e);
			return userProfileP;
		}
	}


	static Optional<String> extractUserId(String completeAuthHeader) {

		String guidClaimStr = null;
		DecodedJWT decodedJWT = null;
		Claim guidClaim;

		try {
			decodedJWT = JWT.decode(completeAuthHeader);
		} catch (Exception ex) {
			logger.warn("Could not extract userId from token");
		}

		if (decodedJWT != null) {
			Map<String, Claim> claims = decodedJWT.getClaims();

			//subject is 'External'
			if (claims.containsKey(AO_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(AO_USER_TYPE).asString())) {
				guidClaim = claims.containsKey(EMAIL) ? claims.get(EMAIL) : claims.get(PREFERRED_MAIL);
				//return Optional.ofNullable(guidClaim.asString());
			} else {
				guidClaim = claims.get(SUB);
			}

			if (guidClaim == null) {
				guidClaim = claims.get(NAME);
			}


			guidClaimStr = guidClaim.asString();
		}


		return Optional.ofNullable(guidClaimStr);
	}

	static UserProfile updateUserProfile(String principal, UserProfile userProfileP, ObjectMapper objectMapper) {
		Optional<JsonNode> jsonNode = AOAuthenticationConvertor.getSubject(principal, objectMapper);
		UserProfile userProfile = userProfileP.toBuilder().build();
		if (jsonNode.isPresent()) {
			JsonNode jn = jsonNode.get();
			JsonNode userGuid = jn.findValue("guid");
			JsonNode firstNameNd = jn.findValue("firstName");
			JsonNode lastNameNd = jn.findValue("lastName");
			JsonNode countryCodeNd = jn.findValue("country");
			JsonNode emailNd = jn.findValue("email");
			if (countryCodeNd != null) {
				userProfile.setCountryCode(!countryCodeNd.isNull() ? countryCodeNd.asText(EMPTY_STR) : EMPTY_STR);
			}

			if (firstNameNd != null) {
				userProfile.setFirstName(!firstNameNd.isNull() ? firstNameNd.asText(EMPTY_STR) : EMPTY_STR);
			}

			if (lastNameNd != null) {
				userProfile.setLastName(!lastNameNd.isNull() ? lastNameNd.asText(EMPTY_STR) : EMPTY_STR);
			}

			if (emailNd != null) {
				userProfile.setEmail(!emailNd.isNull() ? emailNd.asText(EMPTY_STR) : EMPTY_STR);
			}

			if (!userGuid.isNull() && StringUtils.isBlank(userProfile.getName())) {
				userProfile.setName(userGuid.asText());
			}

		}
		return userProfile;
	}

	static UserProfile fromDecodedJwt(DecodedJWT decodedJWT, TokenIssuerClaimsMapperService claimsService, UserProfile userProfile) {
		String issuer = decodedJWT.getClaim(ISS).asString();
		if (StringUtils.isBlank(issuer)) {
			throw new AOAuthenticationException(NO_ISS_CLAIM);
		}

		Map<String, String> issuerClaimMap = claimsService.issuerClaimsMap(issuer);
		if (issuerClaimMap == null || issuerClaimMap.isEmpty()) {
			issuerClaimMap = TokenIssuerClaimsMapperService.AO_IDENTITY_STANDARD_CLAIMS_MAP;
		}

		Map<String, Claim> claims = decodedJWT.getClaims();
		logger.debug("UserProfile fromDecodedJwt Using issuerClaimMap => {}", issuerClaimMap);

		Claim guid = null;
		if (claims.containsKey(AO_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(AO_USER_TYPE).asString())) {
			guid = claims.containsKey(EMAIL) ? claims.get(EMAIL) : claims.get(PREFERRED_MAIL);

		} else {
			guid = decodedJWT.getClaim(issuerClaimMap.get(AOClaims.GUID));
		}

		//Claim guid = decodedJWT.getClaim(issuerClaimMap.get(aoClaims.GUID));
		Claim email = decodedJWT.getClaim(issuerClaimMap.get(EMAIL));
		Claim firstName = decodedJWT.getClaim(issuerClaimMap.get(AOClaims.FIRST_NAME));
		Claim lastName = decodedJWT.getClaim(issuerClaimMap.get(AOClaims.LAST_NAME));
		Claim country = decodedJWT.getClaim(issuerClaimMap.get(COUNTRY_CODE));

		return userProfile.toBuilder()
				.name(guid.asString())
				.email(email.asString())
				.lastName(lastName.asString())
				.firstName(firstName.asString())
				.countryCode(country.asString())
				.build();
	}
}
