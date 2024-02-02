package com.globalpayments.security.key;

import com.globalpayments.businessview.cache.dao.CacheDao;
import com.globalpayments.security.session.dao.impl.HttpAuthenticatedUserDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class HttpKeyService implements KeyService {

    private static Logger logger = LoggerFactory.getLogger(HttpAuthenticatedUserDao.class);

    String authenticationUrl;
    String applicationsUrl;
    CacheDao cacheDao;
    RestTemplate restTemplate;

    private HttpKeyService(String authenticationUrl, String applicationsUrl, CacheDao cacheDao, RestTemplate restTemplate) {
        this.authenticationUrl = authenticationUrl;
        this.applicationsUrl = applicationsUrl;
        this.cacheDao = cacheDao;
        this.restTemplate = restTemplate;

        Assert.notNull(cacheDao,"CacheDao cannot be null");

        if (StringUtils.isBlank(authenticationUrl)){
            throw new IllegalArgumentException("authenticationUrl cannot be null or empty");
        }

        if (StringUtils.isBlank(applicationsUrl)){
            throw new IllegalArgumentException("applicationsUrl cannot be null or empty");
        }

        if (null == restTemplate){
            logger.warn("Using default constructor for RestTemplate");
            restTemplate = new RestTemplate();
        }
    }

    public static Builder newBuilder(){
        return new Builder();
    }

    public static final class Builder{
        String authenticationUrl;
        String applicationsUrl;
        CacheDao cacheDao;
        RestTemplate restTemplate;

        public Builder setAuthenticationUrl(String authenticationUrl) {
            this.authenticationUrl = authenticationUrl;
            return this;
        }

        public Builder setApplicationsUrl(String applicationsUrl) {
            this.applicationsUrl = applicationsUrl;
            return this;
        }

        public Builder setCacheDao(CacheDao cacheDao) {
            this.cacheDao = cacheDao;
            return this;
        }

        public Builder setRestTemplate(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
            return this;
        }

        public HttpKeyService build(){
            return new HttpKeyService(authenticationUrl, applicationsUrl, cacheDao, restTemplate);
        }
    }

    @Override
    public KeyPairInfo createKeyPair(int keySize, String algorithm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyPairInfo saveKeyPair(KeyPairInfo keyPairInfo, String clientId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KeyPairInfo findKeyPair(String privateKeyId, String publicKeyId, String clientId) {
        return null;
    }

    @Override
    public List<KeyPairInfo> findKeyPairs(String clientId) {
        return null;
    }

    @Override
    public SecretKeyInfo createSecretKey(int keySize, String algorithm) {
        throw new UnsupportedOperationException();
    }
}
