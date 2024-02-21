package com.alpha.omega.security.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.enterprise.pwc.datalabs.caching.DefaultObjectMapperFactory;
import com.enterprise.pwc.datalabs.caching.ObjectMapperFactory;
import com.enterprise.pwc.datalabs.security.PwcSecurityProperties;
import com.enterprise.pwc.datalabs.security.token.PwcClaims;
import com.enterprise.pwc.datalabs.security.token.issuer.TokenIssuerClaimsMapperService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwc.base.exceptions.auth.PwcAuthenticationException;
import com.pwc.base.model.UserProfile;
import com.pwc.base.utils.BaseUtil;
import jakarta.servlet.http.HttpServletRequest;
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

import static com.enterprise.pwc.datalabs.security.token.PwcClaims.*;
import static com.pwc.base.utils.BaseConstants.*;

public class AOAuthenticationConvertor implements AuthenticationConverter {

	public static final String NO_ISS_CLAIM = "No iss claim!";
	private static Logger logger = LogManager.getLogger(AOAuthenticationConvertor.class);
	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

	private Charset credentialsCharset = StandardCharsets.UTF_8;
	private PwcSecurityProperties pwcSecurityProperties;

	private HttpServletRequestToRequestMap httpServletRequestToRequestMap = new HttpServletRequestToRequestMap();

	private TokenIssuerClaimsMapperService claimsMapperService;

	public AOAuthenticationConvertor(PwcSecurityProperties pwcSecurityProperties, TokenIssuerClaimsMapperService claimsMapperService) {
		this.pwcSecurityProperties = pwcSecurityProperties;
		this.claimsMapperService = claimsMapperService;
	}

	protected static final List<String> CONVERSION_HEADERS = Arrays.asList(PRINCIPAL, SERVICE_NAME, IDENTITY_PROVIDER, IDENTITY_PROVIDER,
			ENGAGEMENT_ID, CORRELATION_ID, CONTEXT_ID, HttpHeaders.AUTHORIZATION, WORKSPACE_ID);

	@Override
	public Authentication convert(HttpServletRequest request) {

		Map<String, Object> requestMap = httpServletRequestToRequestMap.apply(request);
		PreAuthenticationPrincipal principalAuth = requestMapToPreAuthenticationPrincipal(pwcSecurityProperties, objectMapper, claimsMapperService).apply(requestMap);
		((UserProfile) principalAuth.getPrincipal()).setAdditionalMetaData(convertRequestHeadersToMap(request));
		return principalAuth;
	}

	private static String extractFromBasicAuthHeader(String authorization) {
		try {
			return BaseUtil.parseBasicAuthString(authorization)._1();
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

	public static Function<Map<String, Object>, PreAuthenticationPrincipal> requestMapToPreAuthenticationPrincipal(final PwcSecurityProperties pwcSecurityProperties,
																												   ObjectMapper objectMapper,
																												   TokenIssuerClaimsMapperService claimsMapperService) {

		return (Map<String, Object> requestMap) -> {

			final String principal = (String) requestMap.get(PRINCIPAL);
			final String authorization = (String) requestMap.get(HttpHeaders.AUTHORIZATION);
			final String serviceName = (String) requestMap.get(SERVICE_NAME);
			final String identityProvider = (String) requestMap.get(IDENTITY_PROVIDER);
			final String refreshToken = (String) requestMap.get(REFRESH_TOKEN_HEADER);
			final String engagementId = (String) requestMap.get(ENGAGEMENT_ID);
			final String correlationId = (String) requestMap.get(CORRELATION_ID);
			String contextId = (String) requestMap.get(CONTEXT_ID);

			if (StringUtils.isBlank(contextId) && pwcSecurityProperties.isUseDefaultContextId()) {
				contextId = pwcSecurityProperties.getDefaultContextId();
			}

			UserProfile userProfile = UserProfile.builder()
					.serviceName(serviceName)
					.refreshToken(refreshToken)
					.accessToken(authorization)
					.identityProvider(identityProvider)
					//.additionalMetaData(convertRequestHeadersToMap(request))
					.contextId(contextId)
					.engagementId(engagementId)
					.correlationId(correlationId)
					.build();

			String completeAuthHeader = null;
			if (StringUtils.isBlank(authorization)) {
				throw new PwcAuthenticationException("No authorization header!");
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

			logger.trace("userProfile [{}] in PwcAuthenticationConvertor", userProfile);

			PreAuthenticationPrincipal authPrincipal = decodedJWT != null ? new PreAuthenticationPrincipal(userProfile, completeAuthHeader, decodedJWT, serviceName)
					: new PreAuthenticationPrincipal(userProfile, completeAuthHeader, serviceName);

			return authPrincipal;
		};
	}

	private static UserProfile fromBasicAuthHeader(String authorization, UserProfile userProfileP) {
		try {

			String guid = BaseUtil.parseBasicAuthString(authorization)._1();
			return userProfileP.toBuilder().name(guid).build();
		} catch (Exception e) {
			logger.warn("Could not extract guid ", e);
			return userProfileP;
		}
	}


	static Optional<String> extractPwcguid(String completeAuthHeader) {

		String guidClaimStr = null;
		DecodedJWT decodedJWT = null;
		Claim guidClaim;

		try {
			decodedJWT = JWT.decode(completeAuthHeader);
		} catch (Exception ex) {
			logger.warn("Could not extract pwcguid from token");
		}

		if (decodedJWT != null) {
			Map<String, Claim> claims = decodedJWT.getClaims();

			//subject is 'External'
			if (claims.containsKey(PWC_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(PWC_USER_TYPE).asString())) {
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
			JsonNode pwcguidNd = jn.findValue("guid");
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

			if (!pwcguidNd.isNull() && StringUtils.isBlank(userProfile.getName())) {
				userProfile.setName(pwcguidNd.asText());
			}

		}
		return userProfile;
	}

	static UserProfile fromDecodedJwt(DecodedJWT decodedJWT, TokenIssuerClaimsMapperService claimsService, UserProfile userProfile) {
		String issuer = decodedJWT.getClaim(ISS).asString();
		if (StringUtils.isBlank(issuer)) {
			throw new PwcAuthenticationException(NO_ISS_CLAIM);
		}
		Map<String, String> issuerClaimMap = claimsService.issuerClaimsMap(issuer);
		if (issuerClaimMap == null || issuerClaimMap.isEmpty()) {
			issuerClaimMap = TokenIssuerClaimsMapperService.PWC_IDENTITY_STANDARD_CLAIMS_MAP;
		}
		Map<String, Claim> claims = decodedJWT.getClaims();
		logger.debug("UserProfile fromDecodedJwt Using issuerClaimMap => {}", issuerClaimMap);

		Claim guid = null;
		if (claims.containsKey(PWC_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(PWC_USER_TYPE).asString())) {
			guid = claims.containsKey(EMAIL) ? claims.get(EMAIL) : claims.get(PREFERRED_MAIL);

		} else {
			guid = decodedJWT.getClaim(issuerClaimMap.get(PwcClaims.GUID));
		}

		//Claim guid = decodedJWT.getClaim(issuerClaimMap.get(PwcClaims.GUID));
		Claim email = decodedJWT.getClaim(issuerClaimMap.get(EMAIL));
		Claim firstName = decodedJWT.getClaim(issuerClaimMap.get(PwcClaims.FIRST_NAME));
		Claim lastName = decodedJWT.getClaim(issuerClaimMap.get(PwcClaims.LAST_NAME));
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
