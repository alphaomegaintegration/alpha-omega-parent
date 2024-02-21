package com.alpha.omega.cache.expiration.map;


import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DefaultExpiringHashMap<K,V> extends ConcurrentHashMap<K,V> implements ExpiringMap<K,V>{

	public final static String NON_NULL_EXPIRATION = "Default Expiration cannot be null!";
	public final static String NON_NULL_EXPIRING_MAP_ENTRY = "ExpiringMapEntry cannot be null!";

	ExpiringMapEntry<K> expiringMapEntry;
	Long defaultExpiration;

	public DefaultExpiringHashMap(Map<? extends K, ? extends V> m, ExpiringMapEntry<K> expiringMapEntry, Long defaultExpiration) {
		super(m);
		Objects.requireNonNull(defaultExpiration, NON_NULL_EXPIRATION);
		Objects.requireNonNull(expiringMapEntry, NON_NULL_EXPIRING_MAP_ENTRY);
		this.expiringMapEntry = expiringMapEntry;
		this.defaultExpiration = defaultExpiration;
		m.entrySet().stream().forEach(entry -> expiringMapEntry.expireMapEntry(entry.getKey(), defaultExpiration.longValue()));
	}

	@Override
	public V put(K key, V value) {
		V v = super.put(key, value);
		expiringMapEntry.expireMapEntry(key, defaultExpiration.longValue());
		return v;
	}

	public V put(K key, V value, long pExpiration) {
		long expiration = checkExpiration(pExpiration);
		V v = super.put(key, value);
		expiringMapEntry.expireMapEntry(key, expiration);
		return v;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		super.putAll(m);
		m.entrySet().forEach(entry -> expiringMapEntry.expireMapEntry(entry.getKey(), defaultExpiration.longValue()));
	}

	public void putAll(Map<? extends K, ? extends V> m, long pExpiration) {
		long expiration = checkExpiration(pExpiration);
		super.putAll(m);
		m.entrySet().forEach(entry -> expiringMapEntry.expireMapEntry(entry.getKey(), expiration));
	}

	@Override
	public V putIfAbsent(K key, V value) {
		V v = super.putIfAbsent(key, value);
		expiringMapEntry.expireMapEntry(key, defaultExpiration.longValue());
		return v;
	}

	public V putIfAbsent(K key, V value, long pExpiration) {
		long expiration = checkExpiration(pExpiration);
		V v = super.putIfAbsent(key, value);
		expiringMapEntry.expireMapEntry(key, expiration);
		return v;
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, long pExpiration) {
		long expiration = checkExpiration(pExpiration);
		V v = super.computeIfAbsent(key, mappingFunction);
		expiringMapEntry.expireMapEntry(key, expiration);
		return v;
	}

	long checkExpiration(long expiration) {
		return (expiration < 0)?defaultExpiration.longValue():expiration;
	}
}
