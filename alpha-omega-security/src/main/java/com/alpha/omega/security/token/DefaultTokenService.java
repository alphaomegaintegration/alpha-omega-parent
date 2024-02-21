package com.alpha.omega.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.enterprise.pwc.datalabs.caching.CacheDao;
import com.enterprise.pwc.datalabs.caching.DefaultObjectMapperFactory;
import com.enterprise.pwc.datalabs.caching.ObjectMapperFactory;
import com.enterprise.pwc.datalabs.security.key.PublicKeyResolver;
import com.enterprise.pwc.datalabs.security.key.PublicKeyResolverRequest;
import com.enterprise.pwc.datalabs.security.utils.PwcSecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pwc.base.alerts.AlertProducer;
import com.pwc.base.exceptions.auth.PwcAuthenticationException;
import com.pwc.base.response.ResponseValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.pwc.base.utils.BaseConstants.BEARER;

public class DefaultTokenService implements TokenService{

	public static final String TOKEN_CAN_NOT_BE_NULL = "Token can not be null";
	public static final String KID_CANNOT_BE_NULL = "kid cannot be null";
	public static final String INVALID_TOKEN = "Invalid token. ";
	public static final String NO_PUBLIC_KEY_ASSOCIATED_WITH_KID = "No public key associated with kid";
	public static final String INVALID_FORMAT = "Invalid format. ";
	public static final String KID_NOT_VALIDATED_AGAINST_PUBLIC_KEYS = "Could not validate kid %s against public keys";
	private static Logger logger = LogManager.getLogger(DefaultTokenService.class);
	private TokenServiceProperties tokenServiceProperties;
	private CacheDao cacheDao;
	private AlertProducer alertProducer;
	private ResponseValidator responseValidator;
	private List<PublicKeyResolver> publicKeyResolvers;
	private InvalidatedTokenService invalidatedTokenService;

	public TokenServiceProperties getTokenServiceProperties() {
		return tokenServiceProperties;
	}

	public CacheDao getCacheDao() {
		return cacheDao;
	}

	public AlertProducer getAlertProducer() {
		return alertProducer;
	}

	public ResponseValidator getResponseValidator() {
		return responseValidator;
	}

	public List<PublicKeyResolver> getPublicKeyResolvers() {
		return publicKeyResolvers;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

	public static Builder newBuilder() {
		return new Builder();
	}

	private Optional<PublicKey> getRSAPublicKey(String kid) {

		return publicKeyResolvers.stream()
				.map(resolver -> (resolver.resolvePublicKey(PublicKeyResolverRequest.newBuilder().setPublicKeyId(kid).build())))
				.filter(pkey -> pkey!= null)
				.findAny();
	}

	@Override
	public Optional<TokenResponse>  validateToken(TokenRequest tokenRequest) {
		if (StringUtils.isBlank(tokenRequest.getToken())){
			throw new PwcAuthenticationException(TOKEN_CAN_NOT_BE_NULL);
		}
		String token = findToken(tokenRequest);
		TokenResponse tokenResponse = null;
		DecodedJWT decodedJWT = tokenRequest.getDecodedJWT();

		if (decodedJWT == null){
			try{
				decodedJWT = JWT.decode(token);
			} catch (Exception e){
				throw new PwcAuthenticationException(INVALID_TOKEN+ INVALID_FORMAT);
			}
		}

		Optional<String> kid = extractKid(decodedJWT);

		if (!kid.isPresent()) {
			logger.error("kid cannot be null in  {}", decodedJWT);
			throw new PwcAuthenticationException(KID_CANNOT_BE_NULL);
		}

		final Optional<PublicKey> pubKey;
		try {
			pubKey = getRSAPublicKey(kid.get());
		} catch (Exception e) {
			String msg = String.format(KID_NOT_VALIDATED_AGAINST_PUBLIC_KEYS, kid.get());
			logger.error(msg, e);
			throw new PwcAuthenticationException(msg, e);
		}

		if (pubKey.isPresent()){
			RSAPublicKey publicKey = (RSAPublicKey)pubKey.get();
			tokenResponse = new TokenResponse();
			try {
				Algorithm algorithm = Algorithm.RSA256(publicKey,null);
				JWTVerifier verifier = JWT.require(algorithm).acceptLeeway(tokenServiceProperties.getTimeSkewSeconds()).build();
				DecodedJWT jwt = verifier.verify(decodedJWT);
				tokenResponse.setDecodedJWT(jwt);
			} catch (JWTVerificationException exception){
				exception.printStackTrace();
				//Invalid signature/claims
				throw new PwcTokenVerificationException(INVALID_TOKEN +exception.getMessage(),exception);
			}
		} else {
			throw new PwcTokenVerificationException(INVALID_TOKEN+ NO_PUBLIC_KEY_ASSOCIATED_WITH_KID);
		}
		return Optional.ofNullable(tokenResponse);
	}

	Optional<String> extractKid(DecodedJWT decodedJWT) {
		String kid = decodedJWT.getKeyId();
		if (Objects.isNull(kid)){
			kid = decodedJWT.getHeaderClaim("x5t").asString();
		}
		return Optional.ofNullable(kid);
	}

	String findToken(TokenRequest tokenRequest) {
		return tokenRequest.getToken().startsWith(BEARER) ? tokenRequest.getToken().split(BEARER)[1] : tokenRequest.getToken();
	}

	private PwcJwtHeader getJwtHeaderFromToken(String header) {
		PwcJwtHeader jwtHeader = null;
		try {
			jwtHeader = (PwcJwtHeader) PwcSecurityUtils.convertToObj(header, PwcJwtHeader.class);
			return jwtHeader;
		} catch (Exception e){
			throw new PwcTokenVerificationException("Token cannot be parsed "+header,e);
		}
	}


	public static final class Builder {
		private TokenServiceProperties tokenServiceProperties;
		private CacheDao cacheDao;
		private AlertProducer alertProducer;
		private ResponseValidator responseValidator;
		private List<PublicKeyResolver> publicKeyResolvers;
		private InvalidatedTokenService invalidatedTokenService;
		private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

		private Builder() {
		}

		public static Builder aDefaultTokenService() {
			return new Builder();
		}

		public Builder setTokenServiceProperties(TokenServiceProperties tokenServiceProperties) {
			this.tokenServiceProperties = tokenServiceProperties;
			return this;
		}

		public Builder setCacheDao(CacheDao cacheDao) {
			this.cacheDao = cacheDao;
			return this;
		}

		public Builder setAlertProducer(AlertProducer alertProducer) {
			this.alertProducer = alertProducer;
			return this;
		}

		public Builder setResponseValidator(ResponseValidator responseValidator) {
			this.responseValidator = responseValidator;
			return this;
		}

		public Builder setPublicKeyResolvers(List<PublicKeyResolver> publicKeyResolvers) {
			this.publicKeyResolvers = publicKeyResolvers;
			return this;
		}

		public Builder setObjectMapper(ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
			return this;
		}

		public Builder setInvalidatedTokenService(InvalidatedTokenService invalidatedTokenService) {
			this.invalidatedTokenService = invalidatedTokenService;
			return this;
		}

		public DefaultTokenService build() {
			DefaultTokenService defaultTokenService = new DefaultTokenService();
			defaultTokenService.alertProducer = this.alertProducer;
			defaultTokenService.objectMapper = this.objectMapper;
			defaultTokenService.responseValidator = this.responseValidator;
			defaultTokenService.tokenServiceProperties = this.tokenServiceProperties;
			defaultTokenService.publicKeyResolvers = this.publicKeyResolvers;
			defaultTokenService.cacheDao = this.cacheDao;
			return defaultTokenService;
		}
	}
}
