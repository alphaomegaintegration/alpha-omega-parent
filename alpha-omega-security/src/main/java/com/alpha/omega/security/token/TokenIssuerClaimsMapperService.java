package com.enterprise.pwc.datalabs.security.token.issuer;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.enterprise.pwc.datalabs.security.token.PwcClaims.*;
import static com.enterprise.pwc.datalabs.security.token.PwcClaims.SUB;
import static com.pwc.base.utils.BaseConstants.NAME;

public interface TokenIssuerClaimsMapperService {
	static Logger logger = LogManager.getLogger(TokenIssuerClaimsMapperService.class);
	Map<String,String> PWC_IDENTITY_STANDARD_CLAIMS_MAP = new HashMap<>(){{
		put(GUID,SUB);
		put(EMAIL,EMAIL);
		put(FIRST_NAME,PWC_IDENTITY_FIRST_NAME);
		put(LAST_NAME,PWC_IDENTITY_LAST_NAME);
		put(COUNTRY_CODE,COUNTRY_CODE);
		put(LINE_OF_SERVICE,LOS);
	}};

	default Map<String, String> issuerClaimsMap(String issuer){
		return PWC_IDENTITY_STANDARD_CLAIMS_MAP;
	}

	static Supplier<String> pwcIdentityGuidSupplier(DecodedJWT decodedJWT){
		return () -> {
			String guidClaimStr = null;
			Claim guidClaim;

			if (decodedJWT != null){
				Map<String, Claim> claims = decodedJWT.getClaims();

				//subject is 'External'
				if(claims.containsKey(PWC_USER_TYPE) && EXTERNAL.equalsIgnoreCase(claims.get(PWC_USER_TYPE).asString())){
					guidClaim = claims.containsKey(EMAIL) ? claims.get(EMAIL) : claims.get(PREFERRED_MAIL);
					//return Optional.ofNullable(guidClaim.asString());
				} else {
					guidClaim = claims.get(SUB);
				}

				if (guidClaim == null){
					guidClaim = claims.get(NAME);
				}


				guidClaimStr = guidClaim.asString();
			}
			return guidClaimStr;
		};
	}

}
