package com.alpha.omega.cache.expiration.map;

import java.util.concurrent.Future;

public interface ExpiringMapEntry<K> {
    Future<Boolean> expireMapEntry(K key, long expiresInMilliSeconds);
}
