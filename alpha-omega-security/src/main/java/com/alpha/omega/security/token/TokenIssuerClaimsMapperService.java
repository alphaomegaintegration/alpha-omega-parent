package com.alpha.omega.security.token;

import com.alpha.omega.core.Constants;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.alpha.omega.security.token.AOClaims.*;


public interface TokenIssuerClaimsMapperService {
	static Logger logger = LogManager.getLogger(TokenIssuerClaimsMapperService.class);
	Map<String,String> AO_IDENTITY_STANDARD_CLAIMS_MAP = new HashMap<>(){{
		put(GUID,SUB);
		put(EMAIL,EMAIL);
		put(FIRST_NAME,AO_IDENTITY_FIRST_NAME);
		put(LAST_NAME,AO_IDENTITY_LAST_NAME);
		put(COUNTRY_CODE,COUNTRY_CODE);
		put(LINE_OF_SERVICE,LOS);
	}};

	default Map<String, String> issuerClaimsMap(String issuer){
		return AO_IDENTITY_STANDARD_CLAIMS_MAP;
	}

	static Supplier<String> AOIdentityGuidSupplier(DecodedJWT decodedJWT){
		return () -> {
			String guidClaimStr = null;
			Claim guidClaim;

			if (decodedJWT != null){
				Map<String, Claim> claims = decodedJWT.getClaims();

				//subject is 'External'
				if(claims.containsKey(AO_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(AO_USER_TYPE).asString())){
					guidClaim = claims.containsKey(EMAIL) ? claims.get(EMAIL) : claims.get(PREFERRED_MAIL);
					//return Optional.ofNullable(guidClaim.asString());
				} else {
					guidClaim = claims.get(SUB);
				}

				if (guidClaim == null){
					guidClaim = claims.get(Constants.NAME);
				}


				guidClaimStr = guidClaim.asString();
			}
			return guidClaimStr;
		};
	}

}
