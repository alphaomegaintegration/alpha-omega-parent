package com.alpha.omega.security.utils;

public class AOSecurityConstants {

	public static final String USER_PROFILE_CANNOT_BE_NULL = "UserProfile cannot be null ";
	public static final String AUTHORITIES_CANNOT_BE_NULL_OR_EMPTY = "Authorities cannot be null or empty ";
	public static final String CANNOT_SET_AUTHENTICATION = "Cannot set authentication!";

	public static final String ID_BROKER_LOGIN = "/public/login";
	public static final String EMPTY_STR = "";
	public static final String ALGORITHM = "alg";
	public static final String CONTENT_TYPE = "cty";
	public static final String TYPE = "typ";
	public static final String KEY_ID = "kid";
	public static final String AMR = "amr";
	public static final String ACR = "acr";

	public static final String EXPONENT = "e";
	public static final String MODULUS = "n";
	public static final String USE_SIG = "sig";
	public static final String USE = "use";
	public static final String KTY = "kty";

	// https://www.iana.org/assignments/jwt/jwt.xhtml
	//Payload
	public static final String ISSUER = "iss";
	public static final String SUBJECT = "sub";
	public static final String EXPIRES_AT = "exp";
	public static final String NOT_BEFORE = "nbf";
	public static final String ISSUED_AT = "iat";
	public static final String JWT_ID = "jti";
	public static final String AUDIENCE = "aud";
	public static final String CLIENT_ID = "client_id";
	public static final String IDP = "idp";
	public static final String ALG = "alg";
	public static final String RS256 = "RS256";

	public static final Integer DAY_EXPIRATION_SECONDS = 24 * 60 * 60 * 1000;
	public static final Integer HOUR_EXPIRATION_SECONDS =  60 * 60 * 1000;
	public static final Integer FIVE_EXPIRATION_SECONDS =  5 * 1000;
	public static final String JWKS_ENDPOINT_KEY = "jwksEndpoint";
	public static final String DEFAULT_CONTEXT_ID = "default";
	public static final String SECURITY_AUTHENTICATION = "security.authentication";

	public static final String AO_SECURITY = "ao.security";

}
