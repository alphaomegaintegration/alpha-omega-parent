package com.alpha.omega.cache.redis;

import com.alpha.omega.cache.expiration.ExpiringCacheEntry;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RedisTemplateExpiringCacheEntry implements ExpiringCacheEntry {

	RedisTemplate redisTemplate;

	public RedisTemplateExpiringCacheEntry(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Future<Boolean> expireCacheEntry(String namespace, String key, long expiresInMilliSeconds) {
		return CompletableFuture.completedFuture(redisTemplate.expire(key,expiresInMilliSeconds, TimeUnit.MILLISECONDS));
	}
}
