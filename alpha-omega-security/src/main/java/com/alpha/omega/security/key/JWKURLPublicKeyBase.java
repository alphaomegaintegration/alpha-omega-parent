package com.globalpayments.security.key;

import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalpayments.security.token.AuthConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JWKURLPublicKeyBase {

    private static Logger logger = LoggerFactory.getLogger(JWKURLPublicKeyBase.class);

    protected ObjectMapper mapper = new ObjectMapper();

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    String extractIss(DecodedJWT decodedJWT) {
        try {
            String payLoadJson = new String(Base64Utils.decodeFromUrlSafeString(decodedJWT.getPayload()));
            HashMap payLoadJsonMap;
            payLoadJsonMap = mapper.readValue(payLoadJson, HashMap.class);
            return payLoadJsonMap.get(AuthConstants.ISSUER).toString();
        } catch (Exception e) {
            logger.error("Exception parsing token payload: " + e.toString());
            //throw new InvalidTokenException("exception parsing token header");
            return null;
        }
    }

    public interface UrlJwkProviderFactory{
        public UrlJwkProvider createUrlJwkProvider(URL url);
    }

    public static class DefaultUrlJwkProviderFactory implements UrlJwkProviderFactory{

        @Override
        public UrlJwkProvider createUrlJwkProvider(URL url) {
            return new UrlJwkProvider(url);
        }
    }

    public static class MappedJwkProviderFactory implements UrlJwkProviderFactory{

        Map<URL,UrlJwkProvider> urlJwkProviderMap = new HashMap<>();

        @Override
        public UrlJwkProvider createUrlJwkProvider(URL url) {
            UrlJwkProvider urlJwkProvider = urlJwkProviderMap.get(url);
            if (urlJwkProvider == null){
                urlJwkProvider = new UrlJwkProvider(url);
                urlJwkProviderMap.put(url,urlJwkProvider);
            }
            return urlJwkProvider;
        }

    }
}
