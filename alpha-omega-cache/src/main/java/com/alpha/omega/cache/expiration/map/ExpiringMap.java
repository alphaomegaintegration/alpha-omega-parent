package com.alpha.omega.cache.expiration.map;



import java.util.Map;
import java.util.function.Function;

public interface ExpiringMap<K,V> extends Map<K,V> {

	public static enum Type{
		HashMap;
	}


	V put(K key, V value, long expiration);

	void putAll(Map<? extends K, ? extends V> m, long expiration);

	V putIfAbsent(K key, V value, long expiration);

	V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, long expiration);
}
