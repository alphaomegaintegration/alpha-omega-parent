package com.globalpayments.security.key;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.UrlJwkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

public class JwkProviderPublicKeyResolver extends JWKURLPublicKeyBase implements PublicKeyResolver {

    private static Logger logger = LoggerFactory.getLogger(JwkProviderPublicKeyResolver.class);
    private UrlJwkProviderFactory urlJwkProviderFactory = new JWKURLPublicKeyBase.DefaultUrlJwkProviderFactory();
    private String urlStr;
    private URL url;

    public JwkProviderPublicKeyResolver(UrlJwkProviderFactory urlJwkProviderFactory, String urlStr) throws MalformedURLException {
        this(new URL(urlStr));
        Assert.notNull(urlJwkProviderFactory, "UrlJwkProviderFactory cannot be null!");
        this.urlJwkProviderFactory = urlJwkProviderFactory;
        this.urlStr = urlStr;
        init();
    }

    public JwkProviderPublicKeyResolver(UrlJwkProviderFactory urlJwkProviderFactory, URL url) {
        Assert.notNull(urlJwkProviderFactory, "UrlJwkProviderFactory cannot be null!");
        this.urlJwkProviderFactory = urlJwkProviderFactory;
        this.url = url;
        init();
    }

    public JwkProviderPublicKeyResolver(String urlStr) throws MalformedURLException {
        this(new URL(urlStr));
        init();
    }

    public JwkProviderPublicKeyResolver(URL url) {
        this.url = url;
        init();
    }

    private void init() {
        try {

            logger.info("Creating JwkProviderPublicKeyResolver with URL => {}.....",url);
            url.openConnection();
            logger.info("Connection successful in JwkProviderPublicKeyResolver with URL => {}.....",url);
        } catch (Exception e) {
            logger.error("URL Not reachable exception " + e.toString());
        }
    }

    @Override
    public PublicKey resolvePublicKey(PublicKeyResolverRequest publicKeyResolverRequest) {
        PublicKey publicKey = null;
        //DefaultPublicKeyResolverRequest request = (DefaultPublicKeyResolverRequest) publicKeyConfig;
        try {
            logger.info("Using JwkProviderPublicKeyResolver with public key url => {} .",url);
            UrlJwkProvider urlJwkProvider = urlJwkProviderFactory.createUrlJwkProvider(url);
            Jwk jwk = urlJwkProvider.get(publicKeyResolverRequest.getPublicKeyId());
            publicKey = jwk.getPublicKey();
        } catch (JwkException e) {
            logger.warn("Could not find key with id " + publicKeyResolverRequest.getPublicKeyId() + " at "+url, e);
        } catch (Exception e) {
            logger.warn("Could not find key with id " + publicKeyResolverRequest.getPublicKeyId() + " at "+url, e);
        }

        if (null == publicKey){
            logger.info("JwkProviderPublicKeyResolver could not find public key with request => {}",publicKeyResolverRequest);
        } else {
            logger.info("JwkProviderPublicKeyResolver found public key with request => {}",publicKeyResolverRequest);
        }

        return publicKey;
    }


}
