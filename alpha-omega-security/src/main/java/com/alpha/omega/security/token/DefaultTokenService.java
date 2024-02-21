package com.alpha.omega.security.token;

import com.alpha.omega.cache.CacheDao;
import com.alpha.omega.cache.DefaultObjectMapperFactory;
import com.alpha.omega.cache.ObjectMapperFactory;
import com.alpha.omega.core.alerts.AlertProducer;
import com.alpha.omega.security.exception.AOAuthenticationException;
import com.alpha.omega.security.key.PublicKeyResolver;
import com.alpha.omega.security.key.PublicKeyResolverRequest;
import com.alpha.omega.security.utils.AOSecurityUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.alpha.omega.core.Constants.BEARER;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	private List<PublicKeyResolver> publicKeyResolvers;
	private InvalidatedTokenService invalidatedTokenService;

	private ObjectMapper objectMapper = new DefaultObjectMapperFactory().createObjectMapper(ObjectMapperFactory.Scope.SINGLETON);

	private Optional<PublicKey> getRSAPublicKey(String kid) {

		return publicKeyResolvers.stream()
				.map(resolver -> (resolver.resolvePublicKey(PublicKeyResolverRequest.newBuilder().setPublicKeyId(kid).build())))
				.filter(pkey -> pkey!= null)
				.findAny();
	}

	@Override
	public Optional<TokenResponse>  validateToken(TokenRequest tokenRequest) {
		if (StringUtils.isBlank(tokenRequest.getToken())){
			throw new AOAuthenticationException(TOKEN_CAN_NOT_BE_NULL);
		}
		String token = findToken(tokenRequest);
		TokenResponse tokenResponse = null;
		DecodedJWT decodedJWT = tokenRequest.getDecodedJWT();

		if (decodedJWT == null){
			try{
				decodedJWT = JWT.decode(token);
			} catch (Exception e){
				throw new AOAuthenticationException(INVALID_TOKEN+ INVALID_FORMAT, e);
			}
		}

		Optional<String> kid = extractKid(decodedJWT);

		if (!kid.isPresent()) {
			logger.error("kid cannot be null in  {}", decodedJWT);
			throw new AOAuthenticationException(KID_CANNOT_BE_NULL);
		}

		final Optional<PublicKey> pubKey;
		try {
			pubKey = getRSAPublicKey(kid.get());
		} catch (Exception e) {
			String msg = String.format(KID_NOT_VALIDATED_AGAINST_PUBLIC_KEYS, kid.get());
			logger.error(msg, e);
			throw new AOAuthenticationException(msg, e);
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
				throw new AOTokenVerificationException(INVALID_TOKEN +exception.getMessage(),exception);
			}
		} else {
			throw new AOTokenVerificationException(INVALID_TOKEN+ NO_PUBLIC_KEY_ASSOCIATED_WITH_KID);
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

	private AOJwtHeader getJwtHeaderFromToken(String header) {
		AOJwtHeader jwtHeader = null;
		try {
			jwtHeader = (AOJwtHeader) AOSecurityUtils.convertToObj(header, AOJwtHeader.class);
			return jwtHeader;
		} catch (Exception e){
			throw new AOTokenVerificationException("Token cannot be parsed "+header,e);
		}
	}


}
