package com.globalpayments.security.key;

import java.util.Map;

public interface PublicKeyResolverRequest {

    public static final String JWK_PUBLIC_KEY_URL = "jwk.public.key.url";

    public String getPublicKeyId();
    public String getClientId();
    public Map<String,Object> getExtendedAttributes();

}
