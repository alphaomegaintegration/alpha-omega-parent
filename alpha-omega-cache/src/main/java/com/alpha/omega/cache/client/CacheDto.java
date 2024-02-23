package com.alpha.omega.cache.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = CacheDto.Builder.class)
public class CacheDto<T> {
    String cacheName;
    String cacheKey;
    T cacheValue;
    CacheAction cacheAction;
    Long cacheExpiry;

    private CacheDto(String cacheName, String cacheKey, T cacheValue, CacheAction cacheAction, Long cacheExpiry) {
        this.cacheName = cacheName;
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
        this.cacheAction = cacheAction;
        this.cacheExpiry = cacheExpiry;
    }


    public static Builder newBuilder(){
        return new Builder<>();
    }
    public static Builder newBuilder(Object cacheValue){
        return new Builder(cacheValue);
    }

    public String getCacheName() {
        return cacheName;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public T getCacheValue() {
        return cacheValue;
    }

    public CacheAction getCacheAction() {
        return cacheAction;
    }

    public Long getCacheExpiry() {
        return cacheExpiry;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MPCacheDto{");
        sb.append("cacheName='").append(cacheName).append('\'');
        sb.append(", cacheKey='").append(cacheKey).append('\'');
        sb.append(", cacheValue=").append(cacheValue);
        sb.append(", cacheAction=").append(cacheAction);
        sb.append(", cacheExpiry=").append(cacheExpiry);
        sb.append('}');
        return sb.toString();
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "set")
    public static class Builder<T>{
        String cacheName;
        String cacheKey;
        T cacheValue;
        CacheAction cacheAction;
        Long cacheExpiry;

        public Builder() {
        }

        public Builder(T cacheValue) {
            this.cacheValue = cacheValue;
        }

        public Builder setCacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public Builder setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
            return this;
        }

        public Builder setCacheValue(T cacheValue) {
            this.cacheValue = cacheValue;
            return this;
        }

        public Builder setCacheAction(CacheAction cacheAction) {
            this.cacheAction = cacheAction;
            return this;
        }

        public Builder setCacheExpiry(Long cacheExpiry) {
            this.cacheExpiry = cacheExpiry;
            return this;
        }

        public CacheDto build(){
            return new CacheDto(cacheName, cacheKey, cacheValue, cacheAction, cacheExpiry);
        }
    }

}
