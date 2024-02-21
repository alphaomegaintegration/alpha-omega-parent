package com.alpha.omega.cache.expiration.map;

import java.util.Map;

public interface ExpiringMapFactory<K,V> {
	//ExpiringMap<K,V> createExpiringMap(Map<K,V> backedMap, Long expiration, ExpiringMap.Type mapType);

	default ExpiringMap<K, V> createExpiringMap(Map<K, V> backedMap, Long expiration, ExpiringMap.Type mapType) {
		ExpiringMapEntry<K> expiringMapEntry = new DefaultExpiringMapEntry<K,V>(backedMap);
		ExpiringMap<K,V> expiringMap = new DefaultExpiringHashMap<K,V>(backedMap,expiringMapEntry, expiration);
		return expiringMap;
	}
}
