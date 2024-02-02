package com.globalpayments.security.key;


import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Map;

public class DefaultPublicKeyResolverRequest implements PublicKeyResolverRequest {

    private String publicKeyId;
    private String clientId;
    private Map<String, Object> extendedAttributes;
    private DecodedJWT decodedJWT;


    public static Builder newBuilder(){
        return new Builder();
    }

    private DefaultPublicKeyResolverRequest(String publicKeyId, String clientId, Map<String, Object> extendedAttributes,
                                            DecodedJWT decodedJWT) {
        this.publicKeyId = publicKeyId;
        this.clientId = clientId;
        this.extendedAttributes = extendedAttributes;
        this.decodedJWT = decodedJWT;
    }

    @Override
    public String getPublicKeyId() {
        return publicKeyId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Map<String, Object> getExtendedAttributes() {
        return extendedAttributes;
    }

    public DecodedJWT getDecodedJWT() {
        return decodedJWT;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultPublicKeyResolverRequest{");
        sb.append("publicKeyId='").append(publicKeyId).append('\'');
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", extendedAttributes=").append(extendedAttributes);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder{
       private String publicKeyId;
       private String clientId;
       private Map<String, Object> extendedAttributes;
       private DecodedJWT decodedJWT;

       public Builder setPublicKeyId(String publicKeyId) {
           this.publicKeyId = publicKeyId;
           return this;
       }

       public Builder setClientId(String clientId) {
           this.clientId = clientId;
           return this;
       }

       public Builder setExtendedAttributes(Map<String, Object> extendedAttributes) {
           this.extendedAttributes = extendedAttributes;
           return this;
       }

       public Builder setDecodedJWT(DecodedJWT decodedJWT) {
           this.decodedJWT = decodedJWT;
           return this;
       }

        public DefaultPublicKeyResolverRequest build(){
           return new DefaultPublicKeyResolverRequest(publicKeyId, clientId, extendedAttributes, decodedJWT);
       }
   }
}
