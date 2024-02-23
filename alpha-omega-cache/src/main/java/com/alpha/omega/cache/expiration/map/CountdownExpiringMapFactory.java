package com.alpha.omega.cache.expiration.map;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class CountdownExpiringMapFactory<K,V> implements ExpiringMapFactory<K,V>{

	CountDownLatch countDownLatch;

	public CountdownExpiringMapFactory(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	@Override
	public ExpiringMap<K, V> createExpiringMap(Map<K, V> backedMap, Long expiration, ExpiringMap.Type mapType) {
		ExpiringMapEntry<K> expiringMapEntry = new CountdownExpiringMapEntry<K,V>(backedMap,countDownLatch);
		ExpiringMap<K,V> expiringMap = new DefaultExpiringHashMap<K,V>(backedMap,expiringMapEntry, expiration);
		return expiringMap;
	}
}
