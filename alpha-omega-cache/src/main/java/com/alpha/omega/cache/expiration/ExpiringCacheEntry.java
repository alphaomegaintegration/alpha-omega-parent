package com.alpha.omega.cache.expiration;

import java.util.concurrent.Future;

public interface ExpiringCacheEntry {
    Future<Boolean> expireCacheEntry(String namespace, String key, long expiresInMilliSeconds);
}
